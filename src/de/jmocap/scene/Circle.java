package de.jmocap.scene;

import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import com.sun.j3d.utils.geometry.Primitive;

/**
 * Create a Circle for the 3d scene
 *
 * @author Quan
 *
 */
public class Circle extends Primitive {

    private Shape3D _shape3DForm;
    private Appearance _appearance;

    public Circle(float radius, int divisions, Color color) {
        super();
        _appearance = initAppearance(color);

        _shape3DForm = new Shape3D(createCircleGeometry(radius, divisions),
                _appearance);
        this.addChild(_shape3DForm);
    }

    private Appearance initAppearance(Color color) {
        Material materialStandard = new Material(new Color3f(color),
                new Color3f(color), new Color3f(color), new Color3f(0.7f, 0.7f,
                0.7f), 10.0f);
        Appearance appearance = new Appearance();
        TransparencyAttributes attrTransparency = new TransparencyAttributes(
                TransparencyAttributes.NICEST, 0.2f);
        appearance.setTransparencyAttributes(attrTransparency);
        appearance.setMaterial(materialStandard);
        ColoringAttributes ca = new ColoringAttributes(new Color3f(color),
                ColoringAttributes.FASTEST);
        appearance.setColoringAttributes(ca);
        return appearance;
    }

    private Point3f[] createCircleCoords(float radius, int divisions) {
        Point3f[] coords = new Point3f[divisions];
        double dAngle = 0;
        float x, z;

        for (int i = 0; i < divisions; dAngle = 2.0 * Math.PI / (divisions - 1)
                        * ++i) {
            x = (float) (radius * Math.sin(dAngle));
            z = (float) (radius * Math.cos(dAngle));
            coords[i] = new Point3f(x, 0f, z);
        }
        return coords;
    }

    /**
     * Generates the geometry of a circle
     *
     * @param radius
     * @param divisions
     * @return
     */
    private Geometry createCircleGeometry(float radius, int divisions) {
        int[] stripCounts = {divisions};
        LineStripArray lsArray = new LineStripArray(divisions,
                LineStripArray.COORDINATES, stripCounts);
        lsArray.setCoordinates(0, createCircleCoords(radius, divisions));
        return lsArray;
    } //

    @Override
    public Appearance getAppearance(int arg0) {
        return _appearance;
        //		return null;

    }

    @Override
    public Shape3D getShape(int arg0) {
        return _shape3DForm;
    }

    @Override
    public void setAppearance(Appearance appearance) {
        _appearance = appearance;
        _shape3DForm.setAppearance(_appearance);

    }
}
