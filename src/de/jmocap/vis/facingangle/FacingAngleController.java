package de.jmocap.vis.facingangle;

import de.jmocap.JMocap;
import de.jmocap.figure.Bone;
import de.jmocap.figure.Figure;
import java.util.List;
import javax.media.j3d.ColoringAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * @author Franziska Zamponi
 * @date 29.06.13
 *
 * controlls three FacingAngle objects according to two Figures. once they are
 * set the two Figures can not be changed again, but their shoulder bones can!
 */
public class FacingAngleController {

    private JMocap _jMocap;
    private String[] _figureNames;
    private Bone[] _shoulderBones; // figure 1 to figure 2, left to right
    private Point3d[] _shoulderPoints;
    private FacingAngleFrameListener _frameListener; // only one listener is needed, 
    // because all figures usualy update at the same time
    private FacingAngle _facingAngleMiddle; // passive arrow points to first figure, active arrow points to second figure
    private FacingAngle _facingAngleA;
    private FacingAngle _facingAngleB;
    private double _angleMiddle; //to show in GUI
    private double _angleA;
    private double _angleB;
    private double _heightA;
    private double _heightB;
    private double _heightMiddle;
    private ColoringAttributes _colAttrRed; // 0°-15°
    private ColoringAttributes _colAttrGreen; // 15°- 90°
    private ColoringAttributes _colAttrBlue; // 90°+
    private static final double RADIANT90 = Math.toRadians(90); //90° as radiants
    private static final double RADIANT15 = Math.toRadians(15);
    private static final double RADIANT0 = Math.toRadians(0);

    public FacingAngleController(JMocap jMocap) {
        _jMocap = jMocap;
        _figureNames = new String[2];
        _shoulderBones = new Bone[4];
        _shoulderPoints = new Point3d[4];
        _facingAngleMiddle = new FacingAngle();
        _facingAngleA = new FacingAngle();
        _facingAngleB = new FacingAngle();
        _frameListener = new FacingAngleFrameListener(this);
        _jMocap.getRootBG().addChild(_facingAngleMiddle.getBranchGroupRoot());
        _jMocap.getRootBG().addChild(_facingAngleA.getBranchGroupRoot());
        _jMocap.getRootBG().addChild(_facingAngleB.getBranchGroupRoot());
        _facingAngleMiddle.setInvisible();
        _facingAngleA.setInvisible();
        _facingAngleB.setInvisible();

        _angleMiddle = 0;
        _angleA = 0;
        _angleB = 0;

        _heightA = 0;
        _heightB = 0;
        _heightMiddle = 0;

        Color3f blue = new Color3f(.2f, 0.2f, 0.6f);
        Color3f green = new Color3f(0f, .7f, .15f);
        Color3f red = new Color3f(0.7f, .0f, .15f);
        _colAttrBlue = new ColoringAttributes(blue, ColoringAttributes.FASTEST);
        _colAttrGreen = new ColoringAttributes(green, ColoringAttributes.FASTEST);
        _colAttrRed = new ColoringAttributes(red, ColoringAttributes.FASTEST);
    }

    public void createFacingAngle(String figure1, String figure2, String figure1left,
            String figure1right, String figure2left, String figure2right) {
        List<Figure> figures = _jMocap.getFigureManager().getFigures();
        for (Figure figure : figures) {
            if (_figureNames[0] != null) {
                if (figure.getName().equals(_figureNames[0]) == true) {
                    // if there was a FacingAngle created before, we have to remove the old Listener
                    figure.getPlayer().removeListener(_frameListener);
                }
            }
            if (figure.getName().equals(figure1) == true) {
                figure.getPlayer().addListener(_frameListener); //only figure1 gets a listener!
                _shoulderBones[0] = figure.getSkeleton().findBone(figure1left);
                System.out.println(_shoulderBones[0]);
                _shoulderBones[1] = figure.getSkeleton().findBone(figure1right);
                System.out.println(_shoulderBones[1]);
            }
            if (figure.getName().equals(figure2) == true) {
                _shoulderBones[2] = figure.getSkeleton().findBone(figure2left);
                System.out.println(_shoulderBones[2]);
                _shoulderBones[3] = figure.getSkeleton().findBone(figure2right);
                System.out.println(_shoulderBones[3]);
            }
        }
        _figureNames[0] = figure1;
        _figureNames[1] = figure2;
        setVisible();
        updateFacingAngle();
    }

    public void setShoulderBones(String figure1left, String figure1right, String figure2left, String figure2right) {
        List<Figure> figures = _jMocap.getFigureManager().getFigures();
        for (Figure figure : figures) {
            if (figure.getName().equals(_figureNames[0]) == true) {
                _shoulderBones[0] = figure.getSkeleton().findBone(figure1left);
                _shoulderBones[1] = figure.getSkeleton().findBone(figure1right);
            }
            if (figure.getName().equals(_figureNames[1]) == true) {
                _shoulderBones[2] = figure.getSkeleton().findBone(figure2left);
                _shoulderBones[3] = figure.getSkeleton().findBone(figure2right);
            }
        }
    }

    public void updateFacingAngle() {
        for (int i = 0; i < 4; i++) {
            _shoulderPoints[i] = new Point3d();
            _shoulderBones[i].getWorldPosition(_shoulderPoints[i]);
        }
        // update angle in GUI:
        Vector3d vA = getShoulderVector(_shoulderPoints[0], _shoulderPoints[1]);
        Vector3d vB = getShoulderVector(_shoulderPoints[2], _shoulderPoints[3]);
        _angleMiddle = vA.angle(vB);

        // update positions:
        // logical positions:
        Point3d positionA = new Point3d(getMiddle(_shoulderPoints[0], _shoulderPoints[1]));
        Point3d positionB = new Point3d(getMiddle(_shoulderPoints[2], _shoulderPoints[3]));
        Vector3d vPositionMiddle = getMiddle(positionA, positionB);
        // actual positions of FacingAngle objects:
        _facingAngleMiddle.setPosition(new Vector3d(vPositionMiddle.x, _heightMiddle, vPositionMiddle.z));
        _facingAngleA.setPosition(new Vector3d(positionA.x, _heightA, positionA.z));
        _facingAngleB.setPosition(new Vector3d(positionB.x, _heightB, positionB.z));

        //update angles:
//        _facingAngleMiddle.setAnglePassive(new Vector3d(positionA.x-vPositionMiddle.x, 0,positionA.z-vPositionMiddle.z));
        _facingAngleMiddle.setAnglePassive(vB);
//        _facingAngleMiddle.setAngleActive(new Vector3d(positionB.x-vPositionMiddle.x, 0,positionB.z-vPositionMiddle.z));
        _facingAngleMiddle.setAngleActive(vA);
        _facingAngleMiddle.setText(Double.toString(Math.toDegrees(_angleMiddle)));
        Vector3d vAActive = new Vector3d(positionB.x - positionA.x, 0, positionB.z - positionA.z);
        _facingAngleA.setAngleActive(vAActive);
        _facingAngleA.setAnglePassive(vA);
        _angleA = vA.angle(vAActive);
        _facingAngleA.setText(Double.toString(Math.toDegrees(_angleA)));
        Vector3d vBActive = new Vector3d(positionA.x - positionB.x, 0, positionA.z - positionB.z);
        _facingAngleB.setAngleActive(vBActive);
        _facingAngleB.setAnglePassive(vB);
        _angleB = vB.angle(vBActive);
        _facingAngleB.setText(Double.toString(Math.toDegrees(_angleB)));

        updateColor(_angleA, _facingAngleA);
        updateColor(_angleB, _facingAngleB);
        updateColor(_angleMiddle, _facingAngleMiddle);
        if (_angleA > RADIANT90 | _angleB > RADIANT90) {
            _facingAngleMiddle.setInvisible();
        } else {
            _facingAngleMiddle.setVisible();
        }
    }

    public void setInvisible() {
        _facingAngleMiddle.setInvisible();
        _facingAngleA.setInvisible();
        _facingAngleB.setInvisible();
    }

    public void setVisible() {
        _facingAngleMiddle.setVisible();
        _facingAngleA.setVisible();
        _facingAngleB.setVisible();
    }

    public void setHeightA(double h) {
        if (Double.isInfinite(h) == false & Double.isNaN(h) == false) {
            _heightA = h;
        }
    }

    public void setHeightB(double h) {
        if (Double.isInfinite(h) == false & Double.isNaN(h) == false) {
            _heightB = h;
        }
    }

    public void setHeightMiddle(double h) {
        if (Double.isInfinite(h) == false & Double.isNaN(h) == false) {
            _heightMiddle = h;
        }
    }

    /*
     * returns the vector wich represents the viewing direction 
     * calculated from 2 shoulder points
     */
    private Vector3d getShoulderVector(Point3d leftShoulder, Point3d rightShoulder) {
        Vector3d vector = new Vector3d(
                rightShoulder.x - leftShoulder.x,
                0, // y-axis is unnecessary!
                rightShoulder.z - leftShoulder.z);
        // rotation matrix for a y-axis rotation:
        Matrix3d rotationMatrixY = new Matrix3d(Math.cos(RADIANT90), 0, Math.sin(RADIANT90), 0, 1, 0, -Math.sin(RADIANT90), 0, Math.cos(RADIANT90));
        rotationMatrixY.transform(vector); // vector is now rotated by 90°
        return vector;
    }

    private Vector3d getMiddle(Point3d a, Point3d b) {
        Vector3d middlePosition = new Vector3d(
                (a.x + b.x) / 2,
                0,
                (a.z + b.z) / 2);
        return middlePosition;
    }

    /*
     * updates color of a FacingAngle according to the size of the angle
     */
    private void updateColor(double angle, FacingAngle facingAngle) {
        if (angle > RADIANT0 & angle < RADIANT15) {
            facingAngle.setColoringAttributes(_colAttrRed);
        } else if (angle > RADIANT15 & angle < RADIANT90) {
            facingAngle.setColoringAttributes(_colAttrGreen);
        } else if (angle > RADIANT90) {
            facingAngle.setColoringAttributes(_colAttrBlue);
        }
    }
}