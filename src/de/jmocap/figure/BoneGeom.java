package de.jmocap.figure;

import javax.media.j3d.Appearance;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;

/**
 * Actual geometry of the bone (various options).
 * 
 * @author Michael Kipp
 */
public class BoneGeom {
    public static final int NONE = -1, LINE = 0, CYLINDER = 1, QUAD = 2;
    private static final double CYLINDER_RADIUS = .005;
    private Switch _switch;
    private Box _quad;
    private double _length;
    
    public BoneGeom(TransformGroup parent, Vector3d direction) {
        _length = direction.length();
        TransformGroup tgRot = new TransformGroup();
        tgRot.setTransform(computeRotation(direction));
        _switch = new Switch();
        _switch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        _switch.addChild(createLineLimb(_length));
        _switch.addChild(createCylinder(CYLINDER_RADIUS, _length));
        _switch.addChild(createQuad(_length));
        _switch.setWhichChild(LINE);
        parent.addChild(tgRot);
        tgRot.addChild(_switch);
    }

//    public void detach() {
//        ((TransformGroup)_switch.getParent().getParent()).removeChild(_switch.getParent());
//    }

    public double getLength() {
        return _length;
    }
    
    public void select(int style) {
        _switch.setWhichChild(style == NONE ? Switch.CHILD_NONE : style);
    }
    
    private TransformGroup createCylinder(double radius, double length) {
        Cylinder c = new Cylinder((float)radius, (float)length);
        TransformGroup tg = new TransformGroup();
        Transform3D tf = new Transform3D();
        tf.setTranslation(new Vector3d(0, length/2, 0));
        tg.setTransform(tf);
        tg.addChild(c);
        return tg;
    }
    
    private TransformGroup createQuad(double length) {
        Box c = new Box(.2f, (float)length/2, .2f, new Appearance());
        TransformGroup tg = new TransformGroup();
        Transform3D tf = new Transform3D();
        tf.setTranslation(new Vector3d(0, length/2, 0));
        tg.setTransform(tf);
        tg.addChild(c);
        _quad = c;
        _quad.setCapability(Box.ENABLE_APPEARANCE_MODIFY);
        for (int i=0; i<6; i++)
            _quad.getShape(i).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        return tg;
    }
    
    private Shape3D createLineLimb(double length) {
        LineArray line = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
        line.setCoordinate(0, new Point3d(0, 0, 0));
        line.setCoordinate(1, new Point3d(0, length, 0));
        line.setColor(0, new Color3f(0, 0, 1));
        line.setColor(1, new Color3f(0, 0, 1));
        return new Shape3D(line);
    }
    
    protected void setSelected(boolean val) {
        if (val) 
            _quad.setAppearance(JointAppearance.getSelectedInstance());
        else 
            _quad.setAppearance(JointAppearance.getInstance());
    }
    /**
     * Given a direction, computes the necessary rotation to let the y-axis
     * point in that direction.
     */
    private Transform3D computeRotation(Vector3d direction) {
        Transform3D tf = new Transform3D();
        double x2 = direction.x * direction.x;
        double z2 = direction.z * direction.z;
        if (x2 < .00001 && z2 < .00001) {
            if (direction.y < 0)
                tf.rotX(Math.PI);
        } else {
            Vector3d vec = new Vector3d();
            vec.cross(new Vector3d(0d,1d,0d), new Vector3d(direction));
            double angle = Math.asin(Math.sqrt(x2 + z2) / direction.length());
            if (direction.y < 0)
                angle = Math.PI-angle;
            tf.setRotation(new AxisAngle4d(vec, angle));
        }
        return tf;
    }
}
