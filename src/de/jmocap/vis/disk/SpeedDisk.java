package de.jmocap.vis.disk;

import java.awt.Color;
import java.util.ArrayList;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import com.sun.j3d.utils.geometry.Primitive;
import de.jmocap.JMocap;
import de.jmocap.anim.FrameChangeListener;
import de.jmocap.figure.Bone;
import de.jmocap.figure.Figure;
import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;

/**
 * @author Michael Christopher Hrstka
 * @version 06.2013
 */

public class SpeedDisk implements DiskInterface {

    private float transparency = 0.5f;                      //	general transparency of the disk
    private float diskRadius = 0.8f;                        //	general size of the disk
    private float diskSensibility = 0.05f;                  //	e.g. if you want the disk to be triggered only when big moves happen set the sensibility <1.0
    //	  	 if the disk should trigger at small moves set the sensibility >1.0
    private double scaleFactor = 0.5;                       //  scale the disk radius of the triggered disk
    private int framesToCalculateAverageSpeedwith = 50;     //	set the number of how many frames should be taken to calculate the average speed with	
    private String boneName = "Root";                       //	name of the bone to set the disk on
    private ArrayList<PositionWithSpeed> positionList;
    private DiskPrimitiveInterface primitiveDisk;
    private BranchGroup disk;
    private BranchGroup diskRoot;
    private TransformGroup tGroupDisk = new TransformGroup();
    private TransformGroup tGroupText = new TransformGroup();
    private Transform3D t3DDisk;
    private Transform3D t3DText;
    private JMocap jmocap;
    private Figure figure;
    private Bone bone;

    /**
     * get the figure get the position of the bone to set the default disk on
     *
     * @param jmocap
     */
    public SpeedDisk(JMocap jmocap) {

        this.jmocap = jmocap;

        diskRoot = new BranchGroup();
        diskRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        diskRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        diskRoot.setCapability(BranchGroup.ALLOW_DETACH);

        figure = jmocap.getFigure();

        // get specific bone of the skeleton
        bone = figure.getSkeleton().findBone(boneName);

        // set all positions for the disk for every frame
        setPositionsOfDisk();
    }

    /**
     * get all positions of the skeleton's bone and write it into a list later
     * calculate the speed of every frame for the disk's radius
     */
    private void setPositionsOfDisk() {

        positionList = new ArrayList<PositionWithSpeed>();

        int maxFrame = figure.getPlayer().getNumFrames();
        float fps = figure.getPlayer().getPlaybackFps();

        // set the position of every frame
        for (int frameCounter = 0; frameCounter <= maxFrame; frameCounter++) {

            Point3d position = new Point3d();

            // move the figure to the position of a frame
            figure.getPlayer().gotoTime(frameCounter / fps);

            // get the current position
            bone.getWorldPosition(position);

            // add the position
            positionList.add(new PositionWithSpeed(position));
        }

        // reset the figure
        figure.getPlayer().gotoTime(0);

        // get the speed for every frame
        setSpeedAndAngle();
    }

    /**
     * creates a primitive disk add disk to JMocap's root add the listener
     */
    public void setDisk() {

        // get the current position of the specific bone to set the disk later on
        Point3d p = new Point3d();
        bone.getWorldPosition(p);

        tGroupDisk.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tGroupDisk.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tGroupText.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tGroupText.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        // primitiveDisk object
        primitiveDisk = new DiskPrimitive();

        // get default disk branchGroup
        disk = primitiveDisk.createDiskWithDirectionArrow(transparency, diskRadius);
        disk.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        
        

        tGroupDisk.addChild(disk);
        tGroupText.addChild(primitiveDisk.getText2D("Directional speed disk"));

        // scale it to the default size and move it to the bone's position
        t3DDisk = new Transform3D();
        t3DDisk.setScale(new Vector3d(1.0, 1.0, 1.0));
        t3DDisk.setTranslation(new Vector3d(p.x, p.y, p.z));

        tGroupDisk.setTransform(t3DDisk);

        // set the position of text
        t3DText = new Transform3D();
        t3DText.setScale(new Vector3d(0.5, 0.5, 0.5));
        t3DText.setTranslation(new Vector3d(p.x - 0.5, p.y, p.z + 0.3));
        Transform3D tRot = new Transform3D();
        tRot.rotZ(Math.toRadians(-90));
        t3DText.mul(tRot);
        tRot = new Transform3D();
        tRot.rotX(Math.toRadians(90));
        t3DText.mul(tRot);

        tGroupText.setTransform(t3DText);

        diskRoot.addChild(tGroupDisk);
        diskRoot.addChild(tGroupText);

        // add our branchGroup to JMocap's root
        jmocap.getRootBG().addChild(diskRoot);

        System.out.println("Disk was set.");

        // add a listener and give it figure to get information if something happened
        FrameChangeListener li = new DiskListener(this);
        figure.addFrameChangeListener(li);
    }

    /**
     * returns the appropriate transformation of the disk this includes the size
     * and the position as well as the color
     */
    public void getTransform() {

        int index = figure.getPlayer().getCurrentFrame();
        // get the current position of the specific bone to set the disk later on
        Point3d position = new Point3d();
        bone.getWorldPosition(position);

        // get the radius for the disk at the current frame
        double radius = positionList.get(index).getSpeed();

        // sensibility of the trigger
        radius *= diskSensibility;

        // get the color for the disk
        // if nothing happened the color will be set to gray else to cyan
        // additionally the directionarrow will be set to invisible if there 
        // was not a movement
        ColoringAttributes color = null;
        double angle = 0;

        if (radius <= 1.0) {
            color = getColor(true);
            radius = 1.0f;
            primitiveDisk.setArrowVisibility(false);
        } else {
            color = getColor(false);
            // get the angle between x-axis and the person
            angle = positionList.get(index).getAngle();
            primitiveDisk.setArrowVisibility(true);
            //scale triggered disk
            radius *= scaleFactor;
            if (radius < 1.0) {
                radius = 1.0;
            }
        }
        // set the color
        Primitive p = (Primitive) disk.getChild(0);
        Appearance a = p.getAppearance();
        ((Primitive) disk.getChild(0)).getAppearance().setColoringAttributes(color);

        // scale the disk
        Transform3D scale = new Transform3D();
        scale.setScale(new Vector3d(radius, 1.0, radius));

        t3DDisk.rotY(angle);
        t3DDisk.mul(scale);
        t3DDisk.setTranslation(new Vector3d(position.x, position.y, position.z));

        tGroupDisk.setTransform(t3DDisk);

        t3DText.setTranslation(new Vector3d(position.x - 0.5, position.y, position.z + 0.3));
        tGroupText.setTransform(t3DText);
    }

    /**
     * if the skeleton did not move the disk color will be set to grey else it
     * is set to cyan
     *
     * @param relaxing
     * @return ColoringAttribute
     */
    private ColoringAttributes getColor(boolean relaxing) {

        Color3f objColor = null;

        if (relaxing) {
            objColor = new Color3f(Color.gray);
        } else {
            objColor = new Color3f(Color.cyan);
        }
        return new ColoringAttributes(objColor, ColoringAttributes.FASTEST);
    }

    /**
     * set the speed of every frame that represents the radius of the disk and
     * get the average angle
     */
    private void setSpeedAndAngle() {

        double length = 0,
                speed,
                angle;
        Vector3d vector;

        float fps = figure.getPlayer().getPlaybackFps();
        float time = 1 / fps; // elapsed time

        for (int frameCounter = 0; frameCounter < positionList.size(); frameCounter++) {

            Point3d pastPosition = getPastAveragePosition(frameCounter);
            Point3d futurePosition = getFutureAveragePosition(frameCounter);

            // vector between the future and the past position
            vector = new Vector3d(futurePosition.x - pastPosition.x, 0.0, futurePosition.z - pastPosition.z);

            length = vector.length();
            speed = length / time;

            angle = Math.atan2(vector.x, vector.z);

            positionList.get(frameCounter).setSpeed(speed);
            positionList.get(frameCounter).setAngle(angle);
        }
    }

    /**
     * get the average X- and Z-Coordinate of the past 50 frames
     *
     * @param currentFrame
     * @return Point3d
     */
    private Point3d getPastAveragePosition(int currentFrame) {

        double averageX = 0.0, averageZ = 0.0;
        int counterForMaximumFrames = 0;

        // first entry
        if (currentFrame == 0) {
            Point3d position = positionList.get(currentFrame).getPoint();
            averageX = position.x;
            averageZ = position.z;
            counterForMaximumFrames++;
        }

        // get the previous positions and sum their coordinates
        while (currentFrame > 0 && counterForMaximumFrames < framesToCalculateAverageSpeedwith) {

            counterForMaximumFrames++;
            currentFrame--;

            Point3d pastPosition = positionList.get(currentFrame).getPoint();

            averageX = averageX + pastPosition.x;
            averageZ = averageZ + pastPosition.z;

        }
        // get the average
        averageX /= counterForMaximumFrames;
        averageZ /= counterForMaximumFrames;

        return new Point3d(averageX, 0.0, averageZ);
    }

    /**
     * get the average X- and Z-Coordinate of the future 50 frames
     *
     * @param currentFrame
     * @return Point3d
     */
    private Point3d getFutureAveragePosition(int currentFrame) {

        double averageX = 0.0, averageZ = 0.0;
        int counterForMaximumFrames = 0;

        while (counterForMaximumFrames < framesToCalculateAverageSpeedwith && currentFrame < positionList.size()) {

            Point3d position = positionList.get(currentFrame).getPoint();
            averageX = averageX + position.x;
            averageZ = averageZ + position.z;
            counterForMaximumFrames++;
            currentFrame++;
        }
        // get the average
        averageX /= counterForMaximumFrames;
        averageZ /= counterForMaximumFrames;

        return new Point3d(averageX, 0.0, averageZ);
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
        System.out.println("Disk: Transparency was set to: " + transparency);
    }

    public void setDiskRadius(float radius) {
        this.diskRadius = radius;
        System.out.println("Disk: Radius was set to: " + radius);
    }

    public void setDiskSensibility(float sensibility) {
        this.diskSensibility = sensibility;
        System.out.println("Disk: Sensibility was set to: " + sensibility);
    }

    public void setFramesToCalculateAverageSpeedwith(int framesToCalculateAverageSpeedwith) {
        this.framesToCalculateAverageSpeedwith = framesToCalculateAverageSpeedwith;
        System.out.println("Disk: Frames to calculate speed with was set to: " + framesToCalculateAverageSpeedwith);
    }

    public void setBoneName(String boneName) {
        this.boneName = boneName;
        System.out.println("Disk: Name of bone was set to: " + boneName);
    }

    public void setTriggeredDiskScaleFactor(double scale) {
        this.scaleFactor = scale;
        System.out.println("Disk: Scale factor was set to " + scale);
    }

    /**
     * class for positonList
     */
    class PositionWithSpeed {

        private Point3d point;
        private double speed;
        private double angle;

        public PositionWithSpeed(Point3d point) {
            this.point = point;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        public void setAngle(double angle) {
            this.angle = angle;
        }

        public double getAngle() {
            return angle;
        }

        public Point3d getPoint() {
            return point;
        }

        public double getSpeed() {
            return speed;
        }
    }
}
