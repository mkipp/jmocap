package de.jmocap.figure;

import de.jmocap.anim.MotionData;
import java.util.ArrayList;
import java.util.List;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import de.jmocap.anim.AnimController;
import de.jmocap.anim.FrameChangeListener;

/**
 * Represents an autonomous graphical figure. Contains the skeleton and its own
 * player.
 *
 * @author Michael Kipp
 * @version 14-05-2013
 */
public class Figure {

    private String _name;
    private Bone _skeleton; // root bone of skeleton
    private AnimController _player; // drives animation
    private Point3d _offset;
    private BranchGroup _branchGroup; // scenegraph root
    private TransformGroup _tg; // can be used to give a base orientation

    /**
     * Creates a new figure and initializes the skeleton.
     *
     * @param name ID of the figure
     * @param skeleton Root bone of the skeleton
     */
    public Figure(String name, Bone skeleton) {
        _name = name;
        _skeleton = skeleton;
        _branchGroup = new BranchGroup();
        _branchGroup.setCapability(BranchGroup.ALLOW_DETACH);
        _tg = new TransformGroup();
        _tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        _tg.addChild(_skeleton.getBaseTG());
        _branchGroup.addChild(_tg);
        _branchGroup.compile();
    }

    public void addFrameChangeListener(FrameChangeListener li) {
        _player.addListener(li);
    }

    public void removeFrameChangeListener(FrameChangeListener li) {
        _player.removeListener(li);
    }

    public double getScale() {
        return _skeleton.getScale();
    }

    /**
     * Measures the length of the skeleton by computing the max. distance
     * between any two leaves in the skeleton tree. Figure must be LIVE to allow
     * this operation.
     */
    public double measureLength() {
        List<Bone> leaves = new ArrayList<Bone>();
        List<Bone> all = new ArrayList<Bone>();
        _skeleton.collectBones(all);
        for (Bone b : all) {
            if (b.getChildren().length == 0) {
                leaves.add(b);
            }
        }
        double dist = 0;
        Point3d p1 = new Point3d();
        Point3d p2 = new Point3d();
        for (Bone b1 : leaves) {
            for (Bone b2 : leaves) {
                if (!b1.equals(b2)) {
                    b1.getWorldPosition(p1);
                    b2.getWorldPosition(p2);
                    dist = Math.max(p1.distance(p2), dist);
                }
            }
        }
        System.out.println("### LENGTH IS " + dist);
        return dist;
    }

    /**
     * For motion data where the skeleton head point up the z-axis, use this
     * transformation (reset with resetRotation).
     */
    public void setZUpRotation() {
        Transform3D t2 = new Transform3D();
        t2.rotX(-Math.PI / 2d);
        _tg.setTransform(t2);
    }

    /**
     * For motion data where the skeleton head point up the x-axis, use this
     * transformation (reset with resetRotation).
     */
    public void setXUpRotation() {
        Transform3D t2 = new Transform3D();
        t2.rotZ(Math.PI / 2d);
        _tg.setTransform(t2);
    }

    public void resetRotation() {
        _tg.setTransform(new Transform3D());
    }

    public boolean hasAnimation() {
        return _player != null;
    }

    public String getName() {
        return _name;
    }

    public AnimController getPlayer() {
        return _player;
    }

    public Bone getSkeleton() {
        return _skeleton;
    }

    public void setBG(BranchGroup bg) {
        _branchGroup = bg;
    }

    /**
     * Point in the scene graph where this skeleton is attached.
     */
    public BranchGroup getBG() {

        return _branchGroup;
    }

    public void setAnimation(MotionData data) {
        _player = new AnimController(_skeleton, data, _offset);
        _player.setPlaybackFps(data.getFps());
    }

    public void setOffset(Point3d p) {
        _offset = p;
    }
}
