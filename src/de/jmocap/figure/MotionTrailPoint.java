package de.jmocap.figure;

import java.awt.Color;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import com.sun.j3d.utils.geometry.Sphere;

/**
 * Represents a trail point of a given joint.
 *
 * @author Quan Nguyen
 */
public class MotionTrailPoint
{

    private double _dTime;
    private Point3d _position;
    private double _dVelocity = 0.0;
    private Color _color;
    private float _fRadius = 0.01f;
//    private float _fVelocityHeight = 0.001f;
    //	private BranchGroup _bgPoint;
    private BranchGroup _bgMotionTrailPoint;
    private double _dScale;
    private double _velocityScale = 1d;
    private String _sBoneName;
    private TransformGroup _tgSphere;
    private Sphere _sphere;
    //	private BranchGroup _bgVelocity;
//	private Cylinder _cylinderVelocity;
    private Circle _circleVelocity;
    private TransformGroup _tgVelocity;
    private TransformGroup _tgMotionTrailPoint;
    private Switch _velocitySwitch;

    /**
     * Represents the position of the given bone at a specific time.
     *
     * @param bone
     * @param time
     * @param color
     * @param scale
     */
    private MotionTrailPoint(String bone, double time, 
            double velocity, Color color, double scale)
    {
        _sBoneName = bone;
        _dVelocity = velocity;
        _dTime = time;
        _position = new Point3d();
        _color = color;
        _dScale = scale;
        _dVelocity = _fRadius + (velocity * 0.05);

        initSceneObjects();
        createMotionTrailSphere();
        createVelocityVisualisation();
        _velocitySwitch.setWhichChild(Switch.CHILD_ALL);
    }

    
    public MotionTrailPoint(String bone, double time, Color color)
    {

        this(bone, time, 0, color, 1);
    }

    public MotionTrailPoint(String bone, double time, double velocity,
            Color color)
    {

        this(bone, time, velocity, color, 1);
    }

    public MotionTrailPoint(String bone, double time, Color color, double scale)
    {

        this(bone, time, 0, color, scale);
    }

    private void initSceneObjects()
    {
        _bgMotionTrailPoint = new BranchGroup();
        _bgMotionTrailPoint.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        _bgMotionTrailPoint.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        _bgMotionTrailPoint.setCapability(BranchGroup.ALLOW_DETACH);

        _tgMotionTrailPoint = new TransformGroup();
        _tgMotionTrailPoint.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        _tgMotionTrailPoint.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        _tgMotionTrailPoint.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        Transform3D t3dMotionTrailPointTranslation = new Transform3D();
        t3dMotionTrailPointTranslation.setTranslation(new Vector3d(_position));
        _tgMotionTrailPoint.setTransform(t3dMotionTrailPointTranslation);
        Transform3D t3dMotionTrailPointScale = new Transform3D();
        t3dMotionTrailPointScale.setScale(_dScale);

        _bgMotionTrailPoint.addChild(_tgMotionTrailPoint);

        _velocitySwitch = new Switch(Switch.CHILD_MASK);
        _velocitySwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        _tgMotionTrailPoint.addChild(_velocitySwitch);
    }

    /**
     * Attaches sphere representation (geometry) to the point in space.
     */
    private void createMotionTrailSphere()
    {
        Material materialStandard = new Material(new Color3f(_color),
                new Color3f(_color), new Color3f(_color), new Color3f(0.7f, 0.7f,
                0.7f), 10.0f);
        Appearance appearance = new Appearance();
        appearance.setMaterial(materialStandard);

        _sphere = new Sphere(_fRadius);
        _sphere.setAppearance(appearance);
        _tgSphere = new TransformGroup();
        _tgSphere.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        _tgSphere.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        _tgSphere.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
        Transform3D t3dSphereTranslation = new Transform3D();
        t3dSphereTranslation.setTranslation(new Vector3d(_position));
        _tgSphere.setTransform(t3dSphereTranslation);
        Transform3D t3dSphereScale = new Transform3D();
        t3dSphereScale.setScale(_dScale);
        _tgSphere.addChild(_sphere);
        // set to null for garbage collector
        _velocitySwitch.addChild(_tgSphere); // add the sphere

        materialStandard = null;
        appearance = null;
        t3dSphereTranslation = null;
        t3dSphereScale = null;

    }

    /**
     * Creates a 2D circle to represent velocity.
     *
     * @param pos
     * @param color
     * @param velocity
     * @param scale
     */
    private void createVelocityVisualisation()
    {
        _circleVelocity = new Circle((float) (_velocityScale * _dVelocity), 100, _color);
        _tgVelocity = new TransformGroup();
        _tgVelocity.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        _tgVelocity.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        _tgVelocity.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        _tgVelocity.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);
        Transform3D t3dVelocityTranslation = new Transform3D();
        t3dVelocityTranslation.setTranslation(new Vector3d(_position));
        _tgVelocity.setTransform(t3dVelocityTranslation);
        Transform3D t3dVelocityScale = new Transform3D();
        t3dVelocityScale.setScale(_dScale);
        _tgVelocity.addChild(_circleVelocity);
        _velocitySwitch.addChild(_tgVelocity);
        t3dVelocityTranslation = null;
        t3dVelocityScale = null;

    }

    public BranchGroup getObject()
    {
        return _bgMotionTrailPoint;
    }

    public String getBone()
    {
        return _sBoneName;
    }

    public double getTimePointInSeconds()
    {
        return _dTime;
    }

    public Color getColor()
    {
        return _color;
    }

    public void setScale(double scale)
    {
        _dScale = scale;
        Transform3D t3dSphere = new Transform3D();
        _tgMotionTrailPoint.getTransform(t3dSphere);
        t3dSphere.setScale(_dScale);
        _tgMotionTrailPoint.setTransform(t3dSphere);
        t3dSphere = null;
    }

    public double getScale()
    {
        return _dScale;
    }

    public void setVelocityScale(double s) {
        _velocityScale = s;
    }

    public void setPosition(Point3d positionInWorld)
    {
        _position = positionInWorld;
        Transform3D t3dPosition = new Transform3D();
        _tgMotionTrailPoint.getTransform(t3dPosition);
        t3dPosition.setTranslation(new Vector3d(_position));
        _tgMotionTrailPoint.setTransform(t3dPosition);
        //		drawLine(_tgMotionTrailPoint, new Vector3d(0,0,0),
        //				new Vector3d(_p3dPosition), new Color3f(Color.MAGENTA), 0.1f);
        t3dPosition = null;
    }

    public void getPosition(Vector3d positionInWorld)
    {
        Transform3D t3dSphere = new Transform3D();
        _tgMotionTrailPoint.getTransform(t3dSphere);
        t3dSphere.get(positionInWorld);
    }

    public void angleVelocityVisualisation(Point3d positionNextPointInWorld)
    {
        //		Transform3D t3dPosition = new Transform3D();
        //		_tgMotionTrailPoint.setTransform(t3dPosition);
        positionNextPointInWorld.sub(_position);
        Point3d p3dOrigin = new Point3d(0, 0, 0);
        Vector3d v3dNewYAxis = new Vector3d();
        Vector3d v3dOldYAxis = new Vector3d();
        Vector3d v3dCrossProduct_RotationAxis = new Vector3d();

        v3dOldYAxis.add(p3dOrigin, new Vector3d(0, 1, 0));
        v3dOldYAxis.sub(v3dOldYAxis, p3dOrigin);

        // Create the Vector to the current effector pos
        v3dNewYAxis.sub(positionNextPointInWorld, p3dOrigin);

        // normalize the vectors
        v3dNewYAxis.normalize();
        v3dNewYAxis.normalize();

        // the dot product gives me the cosine of the desired angle
        double dCosinus = v3dOldYAxis.angle(v3dNewYAxis);
        //		drawLine(_tgVelocity, new Vector3d(p3dOrigin),
        //				v3dNewYAxis, new Color3f(Color.RED), 0.1f);
        //		drawLine(_tgVelocity, new Vector3d(p3dOrigin),
        //				v3dOldYAxis, new Color3f(Color.BLUE), 0.1f);
        //		drawLine(_tgVelocity, new Vector3d(0,0,0),
        //				new Vector3d(p3dOrigin), new Color3f(Color.GREEN), 0.1f);
        //
        //		drawLine(_tgVelocity, new Vector3d(positionNextPointInWorld),
        //				new Vector3d(p3dOrigin), new Color3f(Color.CYAN), 0.1f);

        // use the cross product to check which way to rotate
        v3dCrossProduct_RotationAxis.cross(v3dOldYAxis, v3dNewYAxis);
        //		drawLine(_tgVelocity, new Vector3d(p3dOrigin),
        //				v3dCrossProduct_RotationAxis, new Color3f(Color.WHITE), 0.1f);
        rotate(v3dCrossProduct_RotationAxis, dCosinus);

        v3dNewYAxis = null;
        v3dOldYAxis = null;
        v3dCrossProduct_RotationAxis = null;

    }

    protected double rotate(Vector3d rotationAxis, double angle)
    {
        // float fAngle = (float) Math.toRadians(dRotationX);

        //
        // System.out.println(">>>> Rotate Axis (world):  " + rotationAxis
        // + " <<<<<< ");

        Transform3D t3dCurrentPositionInWorld = new Transform3D();
        _tgVelocity.getLocalToVworld(t3dCurrentPositionInWorld);

        t3dCurrentPositionInWorld.invert();
        t3dCurrentPositionInWorld.transform(rotationAxis);
        // System.out.println(">>>> Rotate Axis (local):  " + rotationAxis
        // + " <<<<<< ");

        // //System.out.println("Rotate double:" + this + " : " + dRotationX +
        // "(Rad: " + fAngle + ")");
        // axisAngleX.angle = fAngle;

        // if absoute then reset the joint
        //		if (isAbsolute) {
        //			reset();
        //
        //		}
        AxisAngle4d axisAngle = new AxisAngle4d(rotationAxis, angle);

        Transform3D t3dNewRotation = new Transform3D();
        t3dNewRotation.setRotation(axisAngle);

        Transform3D t3dCurrentMainTransform = new Transform3D();
        _tgVelocity.getTransform(t3dCurrentMainTransform);

        t3dCurrentMainTransform.mul(t3dNewRotation);

        _tgVelocity.setTransform(t3dCurrentMainTransform);
        // LineDrawer.drawLine(tgMain, rotationAxis, Color.YELLOW);
        //		t3dCurrentMainTransform.
        // update all Listeners

        t3dCurrentPositionInWorld = null;
        axisAngle = null;
        return angle;
    }

    public void showMotionTrailVelocity(boolean showMotionTrailVelocity)
    {
        if (showMotionTrailVelocity) {
            _velocitySwitch.setWhichChild(Switch.CHILD_ALL);
        } else {
            _velocitySwitch.setWhichChild(0);
        }
    }
}
