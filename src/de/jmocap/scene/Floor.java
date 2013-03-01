package de.jmocap.scene;

import java.awt.Font;
import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Text2D;

/**
 * The floor is a green grid, with a small red square
 * at the (0,0) position on the (X,Z) plane, and with numbers along
 * the X- and Z- axes.
 */
public class Floor
{

    private final static int FLOOR_LEN = 30;  // should be even
    private final static Color3f CENTER_MARK_RED = new Color3f(0.7f, 0f, 0f);
    private final static Color3f NUMBERS_COLOR = new Color3f(.3f, .3f, .3f);
    private final static Color3f GRID_COLOR = new Color3f(.2f, .2f, .2f);
    private BranchGroup _floorBG;

    /**
     * create tiles, add origin marker, then the axes labels
     */
    public Floor()
    {
        ArrayList blueCoords = new ArrayList();
        _floorBG = new BranchGroup();
        for (int z = -FLOOR_LEN / 2; z <= (FLOOR_LEN / 2) - 1; z++) {
            for (int x = -FLOOR_LEN / 2; x <= (FLOOR_LEN / 2) - 1; x++) {
                createCoords(x, z, blueCoords);
            }
        }
        _floorBG.addChild(new Tiles(blueCoords, GRID_COLOR));
        addOriginMarker();
        labelAxes();
    }

    /**
     * Coors for a single square.
     * Its left hand corner at (x,0,z)
     */
    private void createCoords(int x, int z, ArrayList coords)
    {
        // points created in counter-clockwise order
        Point3f p1 = new Point3f(x, 0.0f, z + 1.0f);
        Point3f p2 = new Point3f(x + 1.0f, 0.0f, z + 1.0f);
        Point3f p3 = new Point3f(x + 1.0f, 0.0f, z);
        Point3f p4 = new Point3f(x, 0.0f, z);
        coords.add(p1);
        coords.add(p2);
        coords.add(p3);
        coords.add(p4);
    }

    private void addOriginMarker() // A red square centered at (0,0,0), of length 1
    {  // points created counter-clockwise, a bit above the floor
        Point3f p1 = new Point3f(-0.5f, 0.01f, 0.5f);
        Point3f p2 = new Point3f(0.5f, 0.01f, 0.5f);
        Point3f p3 = new Point3f(0.5f, 0.01f, -0.5f);
        Point3f p4 = new Point3f(-0.5f, 0.01f, -0.5f);

        ArrayList oCoords = new ArrayList();
        oCoords.add(p1);
        oCoords.add(p2);
        oCoords.add(p3);
        oCoords.add(p4);

        _floorBG.addChild(new Tiles(oCoords, CENTER_MARK_RED));
    }

    private void labelAxes() // Place numbers along the X- and Z-axes at the integer positions
    {
        Vector3d pt = new Vector3d();
        for (int i = -FLOOR_LEN / 2; i <= FLOOR_LEN / 2; i++) {
            pt.x = i;
            _floorBG.addChild(makeText(pt, "" + i));   // along x-axis
        }

        pt.x = 0;
        for (int i = -FLOOR_LEN / 2; i <= FLOOR_LEN / 2; i++) {
            pt.z = i;
            _floorBG.addChild(makeText(pt, "" + i));   // along z-axis
        }
    }

    private TransformGroup makeText(Vector3d vertex, String text) // Create a Text2D object at the specified vertex
    {
        Text2D message = new Text2D(text, NUMBERS_COLOR, "SansSerif", 24, Font.BOLD);
        // 36 point bold Sans Serif

        Appearance app = message.getAppearance();
        PolygonAttributes polyAttrib = new PolygonAttributes();
        polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
        polyAttrib.setBackFaceNormalFlip(true);
        app.setPolygonAttributes(polyAttrib);

        TransformGroup tg = new TransformGroup();
        Transform3D t3d = new Transform3D();
        t3d.setTranslation(vertex);
        tg.setTransform(t3d);
        tg.addChild(message);
        return tg;
    }

    public BranchGroup getBG()
    {
        return _floorBG;
    }
}

