/*
 * ASFReader.java
 * Created on 31. August 2006, 16:48
 */
package de.jmocap.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.vecmath.Vector3d;

import de.jmocap.figure.Bone;

/**
 * Reads ASF format (Acclaim Skeleton Format). Ignores the joints' ID.
 * 
 * ASF stores skeleton data in a hierarchical fashion.
 * The actual motion data is stored in a separate file (.amc file).
 * Also see:
 * 
 * http://www.cs.wisc.edu/graphics/Courses/cs-838-1999/Jeff/ASF-AMC.html
 *
 * The file contains information about the various limbs in the
 * section "bonedata" and then information about the hierarchy in another
 * section called "hierarchy".
 *
 * Snippet of first section:
 *
 * :bonedata
 * begin
 *  id 8
 *  name leftcollar
 *  direction 1.000000 0.000000 0.000000
 *  length 4.830000
 *  axis 0.00000 0.00000 0.00000 XYZ
 *  dof rx ry rz
 *  limits (-180.0 180.0)
 *         (-180.0 180.0)
 *         (-180.0 180.0)
 * end
 *
 * Snippet of hierarchy section:
 *
 * :hierarchy
 * begin
 *    root hips
 *    hips hips1 hips2 hips3
 *    hips1 chest
 *    chest chest1 chest2 chest3
 *    chest1 neck
 *    neck head
 *    chest2 leftcollar
 *    leftcollar leftuparm
 *    leftuparm leftlowarm
 *    leftlowarm lefthand
 *    chest3 rightcollar
 *    rightcollar rightuparm
 *    rightuparm rightlowarm
 *    rightlowarm righthand
 *    hips2 leftupleg
 *    leftupleg leftlowleg
 *    leftlowleg leftfoot
 *    hips3 rightupleg
 *    rightupleg rightlowleg
 *    rightlowleg rightfoot
 * end
 *
 *
 * @author Michael Kipp
 */
public class ASFReader {

    private static final boolean DEBUG = false;
    private static final float LENGTH_SCALE = 1f;
    private int indexCounter;

    /**
     * Intermediate structure to store ASF data in the 1st pass.
     */
    abstract class ASFNode {

        List<ASFNode> children = new ArrayList<ASFNode>();
        int[] dof = new int[0]; // number and order of motion channels
        int index;

        double findMax(double max) {
            double l = max;
            for (ASFNode b: children)
                l = b instanceof ASFBone ? ((ASFBone)b).findMax(l) : l;
            return l > max ? l : max;
        }
    }

    class ASFRoot extends ASFNode {

        Vector3d position; // starting position
        Vector3d orientation; // starting orientation
    }

    class ASFBone extends ASFNode {

        String name;
        Vector3d offset; // offset to the child
        Vector3d axis;

        double findMax(double max) {
            double l = offset.length();
            for (ASFNode b: children)
                l = b instanceof ASFBone ? ((ASFBone)b).findMax(l) : l;
            return l > max ? l : max;
        }
    }

    public ASFReader() {
        indexCounter = 0;
    }

    /**
     * 2nd pass constructs Bone structure. This method creates the root node.
     */
    private Bone makeSkeleton(ASFRoot r) {
        Bone root = new Bone("root", indexCounter++, r.dof);
        root.setBaseTranslation(r.position);
        root.setBaseRotDeg(r.orientation);

        // create children
        Bone[] ch = new Bone[r.children.size()];
        int i = 0;
        for (ASFNode n : r.children) {
            ch[i++] = processBone(root, r, (ASFBone) n, r.findMax(0)/15);
        }
        root.setChildren(ch);
        return root;
    }

    /**
     * Constructs (non-root) bone in 2nd pass.
     */
    private Bone processBone(Bone parent, ASFNode asfParent, ASFBone b, double maxRadius) {

        Bone bone = new Bone(b.name, indexCounter++, b.dof);
        bone.setParent(parent);
        if (DEBUG) {
            System.out.println("> processBone " + b.name + " offset " + b.offset);
        }
        if (!(asfParent instanceof ASFRoot)) {
            // take parent's offset as base translation
            bone.setBaseTranslation(((ASFBone) asfParent).offset);
        }
        bone.setBaseRotDeg(b.axis);

        // take own offset as tip for bone geometry
        bone.attachGeom(b.offset, maxRadius);

        // create children
        Bone[] ch = new Bone[b.children.size()];
        int i = 0;
        for (ASFNode n : b.children) {
            ch[i++] = processBone(bone, b, (ASFBone) n, maxRadius);
        }
        bone.setChildren(ch);
        return bone;
    }

    private ASFRoot readRoot(BufferedReader in) throws IOException {
        ASFRoot root = new ASFRoot();
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("order")) {
                root.dof = readDOF(line);
            }
            if (line.startsWith("position")) {
                root.position = getVector(line);
            }
            if (line.startsWith("orientation")) {
                root.orientation = getVector(line);
            }
            if (line.startsWith(":bonedata")) {
                break;
            }
        }
        return root;
    }

    /**
     * Note that this is used for "order" in the root node as well as
     * for "dof" for regular nodes.
     */
    private int[] readDOF(String line) {
        String[] tok = line.split(" ");
        if (tok.length > 1) {
            int[] dof = new int[tok.length - 1];
            for (int i = 1; i < tok.length; i++) {
                String t = tok[i].toLowerCase();
                if (t.startsWith("r")) {
                    dof[i - 1] = t.equals("rx") ? Bone.RX : (t.equals("ry") ? Bone.RY : Bone.RZ);
                } else {
                    dof[i - 1] = t.equals("tx") ? Bone.TX : (t.equals("ty") ? Bone.TY : Bone.TZ);
                }
            }
            return dof;
        }
        return new int[0];
    }

    private ASFBone readBone(BufferedReader in) throws IOException {

        ASFBone bone = new ASFBone();
        Vector3d direction = null;
        float length = 0;
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("id")) {
                // The ID is not really needed anywhere -> ignore
            } else if (line.startsWith("name")) {
                bone.name = trimKeyword(line);
            } else if (line.startsWith("direction")) {
                direction = getVector(line);
            } else if (line.startsWith("length")) {
                length = LENGTH_SCALE * getFloat(line);
            } else if (line.startsWith("axis")) {
                // sets base rotation (TODO: getSkeleton axis order "XYZ" etc.)
                bone.axis = getVector(line);
            } else if (line.startsWith("dof")) {
                bone.dof = readDOF(line);
            } else if (line.startsWith("end")) // end of bonedata block
            {
                break;
            }
        }


        // direction value is transformed into a single "offset" vector
        direction.normalize();
        direction.scale(length);
        if (direction.length() < .00000001) {
            throw new IOException("Bone " + bone.name + " has (0,0,0) direction!");
        }

        bone.offset = direction;
        if (DEBUG) {
            System.out.println("> bone " + bone.name + " dir=" + bone.offset);
        }
        return bone;
    }


    /**
     * 1st pass
     *
     * @throws java.io.IOException
     */
    private void readHierarchy(BufferedReader in, HashMap<String, ASFNode> name2bone) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.equals("end")) {
                break;
            }
            int pos = line.indexOf(' ');
            if ((line.length() > 0) && (pos > -1)) {
                String name = line.substring(0, pos);

                // get parent
                ASFNode parent = name2bone.get(name);
                if (parent == null) {
                    System.out.println("WARNING! Parent \"" + name + "\" not found (:hierarchy)");
                } else {

                    // parse children
                    StringTokenizer tok = new StringTokenizer(trimKeyword(line));
                    while (tok.hasMoreElements()) {
                        String n = tok.nextToken();
                        ASFNode el = name2bone.get(n);
                        if (el == null) {
                            System.out.println("WARNING! Bone \"" + n + "\" not found (:hierarchy).");
                        } else {
                            parent.children.add(el);
                        }
                    }
                }
            }
        }
    }

    private String trimKeyword(String line) {
        return line.substring(line.indexOf(' ') + 1);
    }

    private Vector3d getVector(String st) {
        String[] tok = st.split(" ");
        double x = Double.parseDouble(tok[1]);
        double y = Double.parseDouble(tok[2]);
        double z = Double.parseDouble(tok[3]);
        return new Vector3d(x, y, z);
    }

    private float getFloat(String str) {
        str = trimKeyword(str);
        return Float.parseFloat(str);
    }

    /**
     * Reads skeleton data from ASF file.
     * @param file ASF file
     * @return Root bone of the skeleton
     * @throws java.io.IOException
     */
    public Bone readSkeleton(File file) throws IOException {
        System.out.println("Read ASF: " + file);
        ASFRoot root = new ASFRoot();
        HashMap<String, ASFNode> name2bone = new HashMap<String, ASFNode>();
        BufferedReader in = new BufferedReader(new FileReader(file));
        boolean bonedata = false;
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.startsWith((":root"))) {
                root = readRoot(in);
                name2bone.put("root", root);
                bonedata = true;
            }
            if (bonedata && (line.startsWith("begin"))) {
                ASFBone bone = readBone(in);
                name2bone.put(bone.name, bone);
            }
            if (line.startsWith(":hierarchy")) {
                bonedata = false;
                readHierarchy(in, name2bone);
            }
        }
        return makeSkeleton(root);
    }
}
