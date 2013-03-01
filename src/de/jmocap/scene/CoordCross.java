package de.jmocap.scene;

import java.awt.Font;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Text2D;

/**
 * Coordinate cross to represent a joint.
 *
 * x = red
 * y = green
 * z = blue
 *
 * @author Michael Kipp
 */
public class CoordCross {

    private final float RADIUS = 0.02f;
    private TransformGroup _tg = new TransformGroup();

    public CoordCross(float length) {
        Appearance green = new Appearance();
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(0f, 1f, 0f);
        ca.setShadeModel(ColoringAttributes.NICEST);
        green.setColoringAttributes(ca);
        Appearance blue = new Appearance();
        ca = new ColoringAttributes();
        ca.setColor(0f, 0f, 1f);
        blue.setColoringAttributes(ca);
        Appearance red = new Appearance();
        ca = new ColoringAttributes();
        ca.setColor(1f, 0f, 0f);
        red.setColoringAttributes(ca);
        Appearance grey = new Appearance();
        ca = new ColoringAttributes();
        ca.setColor(.8f, .8f, 0f);
        grey.setColoringAttributes(ca);

        Transform3D tf = new Transform3D();
        Vector3f vec = new Vector3f(0, length / 2, 0);

        Sphere center = new Sphere(RADIUS * 4);
        center.setAppearance(grey);
        _tg.addChild(center);

        // Y-AXIS
        tf = new Transform3D();
        tf.setTranslation(vec);
        TransformGroup ty = getTranslation(length);
        Cylinder cy = new Cylinder(RADIUS, length);
        cy.setAppearance(green);
        _tg.addChild(ty);
        ty.addChild(cy);
        ty.addChild(makeHead("y", green, length));

        // X-AXIS
        tf = new Transform3D();
        tf.setTranslation(vec);
        tf.rotZ(3 * Math.PI / 2);
        TransformGroup tx = new TransformGroup(tf);
        TransformGroup tx2 = getTranslation(length);
        _tg.addChild(tx);
        tx.addChild(tx2);
        cy = new Cylinder(RADIUS, length);
        cy.setAppearance(red);
        tx2.addChild(cy);
        tx2.addChild(makeHead("x", red, length));

        // Z-AXIS
        tf = new Transform3D();
        tf.setTranslation(vec);
        tf.rotX(Math.PI / 2);
        TransformGroup tz = new TransformGroup(tf);
        TransformGroup tz2 = getTranslation(length);
        _tg.addChild(tz);
        tz.addChild(tz2);
        cy = new Cylinder(RADIUS, length);
        cy.setAppearance(blue);
        tz2.addChild(cy);
        tz2.addChild(makeHead("z", blue, length));

        addLabels(length);
    }

    private void addLabels(float length) {
        float LABEL_OFFSET = length / 9;
        Vector3f v = new Vector3f(length + LABEL_OFFSET, 0, 0);
        Transform3D tf = new Transform3D();
        tf.setTranslation(v);
        TransformGroup tx = new TransformGroup(tf);

        v.set(0, length + LABEL_OFFSET, 0);
        tf.setTranslation(v);
        TransformGroup ty = new TransformGroup(tf);

        v.set(0, 0, length + LABEL_OFFSET);
        tf.setTranslation(v);
        TransformGroup tz = new TransformGroup(tf);

        _tg.addChild(tx);
        _tg.addChild(ty);
        _tg.addChild(tz);

        tx.addChild(makeLabel("x"));
        ty.addChild(makeLabel("y"));
        tz.addChild(makeLabel("z"));
    }

    private Text2D makeLabel(String label) {
        Text2D text = new Text2D(label, new Color3f(1, 1, 0), "Helvetica", 20, Font.BOLD);
        // make text 2-sided
        Appearance app = text.getAppearance();
        PolygonAttributes polyAttrib = new PolygonAttributes();
        polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
        polyAttrib.setBackFaceNormalFlip(true);
        app.setPolygonAttributes(polyAttrib);
        return text;
    }

    private TransformGroup getTranslation(float length) {
        Transform3D t = new Transform3D();
        t.setTranslation(new Vector3d(0, length / 2, 0));
        TransformGroup tf = new TransformGroup(t);
        return tf;
    }

    public TransformGroup getRoot() {
        return _tg;
    }

    private TransformGroup makeHead(String label, Appearance app, float length) {
        float headlength = length / 9;
        float headradius = RADIUS * 3.5f;
        Vector3f vec = new Vector3f(0, length / 2 + headlength / 2, 0);
        Transform3D tf = new Transform3D();
        tf.set(vec);
        TransformGroup tgH = new TransformGroup(tf);
        Cone head = new Cone(headradius, headlength);
        head.setAppearance(app);
        tgH.addChild(head);
        return tgH;
    }
}
