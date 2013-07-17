package de.jmocap.vis.distancePlate;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import com.sun.j3d.utils.geometry.Primitive;
import de.jmocap.JMocap;
import de.jmocap.anim.FrameChangeListener;
import de.jmocap.vis.disk.DiskPrimitive;
import de.jmocap.vis.disk.DiskPrimitiveInterface;
import de.jmocap.figure.Bone;
import de.jmocap.figure.Figure;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.media.j3d.ColoringAttributes;
import javax.vecmath.Point3d;

/**
 * @author Michael Christopher Hrstka
 * @version 06.2013
 */
public class DistancePlateJMocap implements DistancePlateInterface {

    private DiskPrimitiveInterface disk;
    private BranchGroup distanceObject;
    private BranchGroup distancePlateRoot;
    private TransformGroup tGroupPlate;
    private TransformGroup tGroupText;
    private Transform3D t3DPlate;
    private Transform3D t3DText;

    /*
     red:    0-1m
     orange: 1-2m
     yellow: 2-5m
     green:  5+m
     */
    private double redArea;
    private double orangeArea;
    private double yellowArea;
    private double greenArea;
    private JMocap jmocap;
    private Figure figureOne;
    private Figure figureTwo;
    // the bone to be worked with
    private String boneName = "Root";
    private Bone boneOfFigureOne;
    private Bone boneOfFigureTwo;
    private ArrayList<PositionsWithAngles> positionList;
    // set the number of how many frames should be taken to calculate the average length and angles with	 
    private int framesToCalculateWith = 50;
    private double relativeDistanceFactor;
    private float plateSize = 0.5f;

    /**
     * get jmocap get both figures
     *
     * @param jmocap
     */
    public DistancePlateJMocap(JMocap jmocap) {
        this.jmocap = jmocap;

        // get first and second figure
        figureOne = jmocap.getFigureManager().getFigures().get(0);
        figureTwo = jmocap.getFigureManager().getFigures().get(1);
    }

    /**
     * set distancePlate add distancePlate, text to JMocap's root add a listener
     */
    public void setDistancePlate() {

        // create disk == distancePlate
        disk = new DiskPrimitive();
        distanceObject = disk.createDisk(0.5f, plateSize);

        tGroupPlate = new TransformGroup();
        tGroupText = new TransformGroup();

        Vector3d position = new Vector3d(getCurrentPosition());
        t3DText = new Transform3D();
        t3DText.setScale(new Vector3d(1.0, 0.0, 1.0));
        t3DText.setTranslation(new Vector3d(position.x, position.y, position.z + 0.6));
        t3DPlate = new Transform3D();
        t3DPlate.setScale(new Vector3d(2.0, 0.0, 2.0));
        t3DPlate.setTranslation(position);

        tGroupPlate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tGroupPlate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tGroupText.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tGroupText.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        tGroupText.addChild(disk.getText2D("Distance plate"));
        tGroupPlate.addChild(distanceObject);

        tGroupText.setTransform(t3DText);
        tGroupPlate.setTransform(t3DPlate);

        distancePlateRoot = new BranchGroup();
        distancePlateRoot.addChild(tGroupPlate);
        distancePlateRoot.addChild(tGroupText);

        // add our branchGroup to JMocap's root
        jmocap.getRootBG().addChild(distancePlateRoot);

        // set positions and angles for the distance object
        setPositions();
        setAnglesAndLengths();

        System.out.println("Distance plate was set.");

        // add a listeners
        FrameChangeListener li = new DistancePlateListener(this);
        figureOne.addFrameChangeListener(li);
    }

    /**
     * set relative distance e.g. the length between the waist and a knee of a
     * normal person is 0.5m therefore the first sector to be red is 0-1m and in
     * our program 0- 2*relativeDistanceFactor
     */
    public void setRelativeDistance(String pointOne, String pointTwo) {

        Point3d positionOne = new Point3d();
        Point3d positionTwo = new Point3d();

        // using bones of the figures instead of making new ones - will be overwrited later anyway
        boneOfFigureOne = figureOne.getSkeleton().findBone(pointOne);
        boneOfFigureTwo = figureOne.getSkeleton().findBone(pointTwo);

        // get position of first bone (e.g. root)
        boneOfFigureOne.getWorldPosition(positionOne);
        //get position of second bone (e.g. right knee)
        boneOfFigureTwo.getWorldPosition(positionTwo);

        relativeDistanceFactor = getLength(positionOne, positionTwo);
        redArea = 2.0 * relativeDistanceFactor;
        orangeArea = 4.0 * relativeDistanceFactor;
        yellowArea = 10.0 * relativeDistanceFactor;
        greenArea = 10.0 * relativeDistanceFactor;
    }

    /**
     * returns the possibly altered distanceObject therefore it checks the
     * color, the size and angle
     */
    public void updateDistancePlate() {

        // current frame
        int index = figureOne.getPlayer().getCurrentFrame();

        ColoringAttributes color = getColor(positionList.get(index).getLength());
        ((Primitive) distanceObject.getChild(0)).getAppearance().setColoringAttributes(color);

        // set the plate text
        double length = positionList.get(index).getLength();
        setPlateText(length);

        // set the length and with of the distance object
        length = positionList.get(index).getLength();
        double width = 10 / length;
        if (width > length) {
            width = length;
        }

        Transform3D scaleTrans3D = new Transform3D();
        scaleTrans3D.setScale(new Vector3d(width, 0.5, length));

        // get the angle between x-axis and the two person and rotate it
        double angle = positionList.get(index).getAngle();
        t3DPlate.rotY(angle);

        t3DPlate.mul(scaleTrans3D);
        // set the position of plate
        t3DPlate.setTranslation(getCurrentPosition());
        // set the position of text
        t3DText.rotY(angle + Math.PI);
        Vector3d v = getCurrentPosition();
        t3DText.setTranslation(new Vector3d(v.x, v.y, v.z + 0.5)); //align ~center

        tGroupPlate.setTransform(t3DPlate);
        tGroupText.setTransform(t3DText);
    }

    /**
     * set the text on the plate
     *
     * @param length
     */
    private void setPlateText(double length) {
        double lengthInMeter = length * 0.5 / relativeDistanceFactor;
        DecimalFormat df = new DecimalFormat("0.00");
        String meters = df.format(lengthInMeter);
        //disk.setText2D("Distance plate: " + meters + " m");
    }

    /**
     * get all positions of the figures bone and write it into a list
     */
    private void setPositions() {

        positionList = new ArrayList<PositionsWithAngles>();

        int maxFrame = figureOne.getPlayer().getNumFrames();
        float fps = figureOne.getPlayer().getPlaybackFps();

        // set the position of every frame
        for (int frameCounter = 0; frameCounter <= maxFrame; frameCounter++) {

            Point3d positionPersonOne = new Point3d();
            Point3d positionPersonTwo = new Point3d();

            // move figure one to the position of a frame
            figureOne.getPlayer().gotoTime(frameCounter / fps);
            // move figure two to the position of a frame
            figureTwo.getPlayer().gotoTime(frameCounter / fps);

            // get the current position of bones from both figures
            boneOfFigureOne.getWorldPosition(positionPersonOne);
            boneOfFigureTwo.getWorldPosition(positionPersonTwo);

            // add the position
            positionList.add(new PositionsWithAngles(positionPersonOne, positionPersonTwo));
        }

        // reset the figures
        figureOne.getPlayer().gotoTime(0);
        figureTwo.getPlayer().gotoTime(0);

    }

    private Vector3d getCurrentPosition() {

        Point3d positionOne = new Point3d();
        Point3d positionTwo = new Point3d();

        // get bones of both figures (root-bone by default)
        boneOfFigureOne = figureOne.getSkeleton().findBone(boneName);
        boneOfFigureTwo = figureTwo.getSkeleton().findBone(boneName);
        // get positions
        boneOfFigureOne.getWorldPosition(positionOne);
        boneOfFigureTwo.getWorldPosition(positionTwo);

        // get position of object
        return getAverageVector(positionOne, positionTwo);
    }

    /**
     * set all angles and lengths for the distance object
     */
    private void setAnglesAndLengths() {

        int currentFrame,
                MaximumFrames;

        for (int frameCounter = 0; frameCounter < positionList.size(); frameCounter++) {

            double length = 0,
                    averageXPersonOne = 0,
                    averageZPersonOne = 0,
                    averageXPersonTwo = 0,
                    averageZPersonTwo = 0,
                    angle;

            currentFrame = frameCounter;
            MaximumFrames = 0;

            // first entry of the list
            if (frameCounter == 0) {
                Point3d point1 = positionList.get(0).getPositionPersonOne();
                Point3d point2 = positionList.get(0).getPositionPersonTwo();

                averageXPersonOne += point1.x;
                averageZPersonOne += point1.z;
                averageXPersonTwo += point2.x;
                averageZPersonTwo += point2.z;

                MaximumFrames++;
            }

            while (currentFrame > 0 && MaximumFrames < framesToCalculateWith) {
                // get positions of frames e.g. 49,48 ... 0
                Point3d point1 = positionList.get(currentFrame).getPositionPersonOne();
                Point3d point2 = positionList.get(currentFrame).getPositionPersonTwo();

                // sum all x and z parameters
                averageXPersonOne += point1.x;
                averageZPersonOne += point1.z;
                averageXPersonTwo += point2.x;
                averageZPersonTwo += point2.z;

                MaximumFrames++;
                currentFrame--;
            }
            // get average
            averageXPersonOne /= MaximumFrames;
            averageZPersonOne /= MaximumFrames;
            averageXPersonTwo /= MaximumFrames;
            averageZPersonTwo /= MaximumFrames;
            // create a vector between person one and two
            Vector3d vecBetweenPersons = new Vector3d(averageXPersonOne - averageXPersonTwo, 0.0, averageZPersonOne - averageZPersonTwo);

            // set the length
            length = vecBetweenPersons.length();
            positionList.get(frameCounter).setLength(length);
            // set the angle between x-axis and the two person
            angle = Math.atan2(vecBetweenPersons.x, vecBetweenPersons.z);
            positionList.get(frameCounter).setAngle(angle);
        }
    }

    /**
     * returns the length between two points
     *
     * @param pointOne
     * @param pointTwo
     * @return length
     */
    private double getLength(Point3d pointOne, Point3d pointTwo) {

        double x = pointOne.x - pointTwo.x;
        double y = pointOne.y - pointTwo.y;
        double z = pointOne.z - pointTwo.z;

        Vector3d v = new Vector3d(x, y, z);

        return v.length();
    }

    /**
     * returns a vector with the midpoint between to points on the floor
     *
     * @param pointOne
     * @param pointTwo
     * @return
     */
    private Vector3d getAverageVector(Point3d pointOne, Point3d pointTwo) {

        double xMidPoint = 0.5 * (pointOne.x + pointTwo.x);
        double yMidPoint = 0.0;
        double zMidPoint = 0.5 * (pointOne.z + pointTwo.z);

        return new Vector3d(xMidPoint, yMidPoint, zMidPoint);
    }

    /**
     * get the suitable color
     *
     * @param length
     * @return ColoringAttribute
     */
    private ColoringAttributes getColor(double length) {

        Color3f objColor = null;

        if (length < redArea) {
            objColor = new Color3f(Color.red);
            return new ColoringAttributes(objColor, ColoringAttributes.FASTEST);
        }
        if (length < orangeArea) {
            objColor = new Color3f(Color.orange);
            return new ColoringAttributes(objColor, ColoringAttributes.FASTEST);
        }
        if (length < yellowArea) {
            objColor = new Color3f(Color.yellow);
            return new ColoringAttributes(objColor, ColoringAttributes.FASTEST);
        }
        if (length >= greenArea) {
            objColor = new Color3f(Color.green);
        }
        return new ColoringAttributes(objColor, ColoringAttributes.FASTEST);
    }

    public void setBoneName(String name) {
        this.boneName = name;
        System.out.println("Distance plate: Bone name was set to " + name);
    }

    public void setDistancePlateSize(float size) {
        this.plateSize = size;
        System.out.println("Diatnce plate: Size was set to " + size);
    }

    public void setFramesToCalculateAverageWith(int framesToCalculateWith) {
        this.framesToCalculateWith = framesToCalculateWith;
        System.out.println("Distance plate: framesToCalculateWith was set to " + framesToCalculateWith);
    }

    /**
     * class for positionList
     */
    class PositionsWithAngles {

        private Point3d positionPersonOne;
        private Point3d positionPersonTwo;
        private double angle = 0.0;
        private double length = 0.0;

        public PositionsWithAngles(Point3d positionPersonOne, Point3d positionPersonTwo) {
            this.positionPersonOne = positionPersonOne;
            this.positionPersonTwo = positionPersonTwo;
        }

        private Point3d getPositionPersonOne() {
            return positionPersonOne;
        }

        private Point3d getPositionPersonTwo() {
            return positionPersonTwo;
        }

        private void setAngle(double angle) {
            this.angle = angle;
        }

        private void setLength(double length) {
            this.length = length;
        }

        private double getAngle() {
            return this.angle;
        }

        private double getLength() {
            return this.length;
        }
    }
}
