package de.jmocap.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import de.jmocap.anim.MotionData;
import de.jmocap.figure.Bone;

/**
 * Reads AMC motion files.
 * 
 * @author Michael Kipp
 */
public class AMCReader {

    public AMCReader() {
    }

    public MotionData readMotion(File file, Bone skeletonRoot) {
        System.out.println("Read AMC: " + file);
        Pattern num = Pattern.compile("[0-9]+");
        BufferedReader in = null;
        int keyframe = -1;
        try {
            HashMap<String, Bone> name2bone = new HashMap<String, Bone>();
            HashMap<String, List<Float>> name2data = new HashMap<String, List<Float>>();
            skeletonRoot.collectBones(name2bone);
            MotionData dat = new MotionData(name2bone.size());
            in = new BufferedReader(new FileReader(file));
            String line;

            // read data
            while ((line = in.readLine()) != null) {

                // ignore comments + acclaim info
                if (!(line.startsWith("#") || line.startsWith(":"))) {
                    if (num.matcher(line).matches()) {
                        keyframe++;
                    } else {
                        parseBoneKeyframe(line, name2data, name2bone);
                    }
                }
            }

            // put data in skeleton
            for (String key : name2bone.keySet()) {
                List<Float> ls = name2data.get(key);
                if (ls != null) {
//                    name2bone.get(key).loadAnimData(layer, ls);
                    dat.putBoneData(name2bone.get(key).getIndex(), ls);
                }
            }
            dat.setNumFrames(keyframe + 1);
            return dat;
        } catch (IOException ex) {
            Logger.getLogger(ASFReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(ASFReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        return keyframe + 1;
        return null;
    }

    private void parseBoneKeyframe(String line, HashMap<String, List<Float>> data,
            HashMap<String, Bone> name2bone) {
        String[] tok = line.split(" ");
        Bone b = name2bone.get(tok[0]);
        if (b == null) {
            System.out.println("WARNING: Bone " + tok[0] + " does not exist.");
            return;
        }
        List<Float> list = data.get(tok[0]);
        if (list == null) {
            list = new ArrayList<Float>();
            data.put(tok[0], list);
        }
        for (int i = 1; i < tok.length; i++) {
            try {
                Float fl = Float.parseFloat(tok[i]);
                if (b.getDOF()[i - 1] == Bone.TX || b.getDOF()[i - 1] == Bone.TY || b.getDOF()[i - 1] == Bone.TZ) {
                    list.add(fl);
                } else {
                    list.add((float) Math.toRadians(fl));
                }
            } catch (NumberFormatException e) {
                System.out.println("WARNING: Couldn't parse: " + tok[i]);
            }
        }

    }
}
