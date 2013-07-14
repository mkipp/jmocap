package de.jmocap.vis.disk;

import java.awt.Color;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Text2D;
import de.jmocap.vis.tangentialarrow.Arrow;
import de.jmocap.vis.tangentialarrow.ArrowInterface;
import java.awt.Font;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;

/**
 * @author Michael Christopher Hrstka
 * @version 06.2013
 */

public class DiskPrimitive implements DiskPrimitiveInterface{
    Text2D text;
    RenderingAttributes rendAttr;

    /**
    * creates a disk
    * and returns the BranchGroup
    */
    public BranchGroup createDisk(float transparency, float radius) {
        return createCylidner(transparency, radius);    
    }

    /**
    * creates a disk with a direction arrow
    * and returns the BranchGroup
    */
    public BranchGroup createDiskWithDirectionArrow(float transparency, float radius){
        // disc
        BranchGroup bg = createCylidner(transparency, radius);

        // directionarrow
        Transform3D t3D =  new Transform3D();
        TransformGroup tGroup = new TransformGroup();

        ArrowInterface arrow = new Arrow();

        Appearance arrowAppearance = arrow.getAppearance();
        rendAttr = new RenderingAttributes();
        arrowAppearance.setRenderingAttributes(rendAttr);
        arrowAppearance.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
        //app.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
        rendAttr.setVisible(true);

        rendAttr.setCapability(RenderingAttributes.ALLOW_VISIBLE_WRITE);

        arrow.setScale(radius / 3);

        t3D.rotX(Math.toRadians(90));

        tGroup.setTransform(t3D);

        tGroup.addChild(arrow.getRoot());

        bg.addChild(tGroup);

        return bg;
    }

    private BranchGroup createCylidner(float transparency, float radius){

        Appearance app = new Appearance();
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        app.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
        //app.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);

        BranchGroup bg = new BranchGroup();

        // set color
        Color3f objColor = new Color3f(Color.gray);
        ColoringAttributes colAttr = new ColoringAttributes(objColor, ColoringAttributes.FASTEST);
        app.setColoringAttributes(colAttr);

        // set transparency
        TransparencyAttributes ta = new TransparencyAttributes();
        ta.setTransparencyMode(TransparencyAttributes.FASTEST);
        ta.setTransparency(transparency);
        app.setTransparencyAttributes(ta);

        Cylinder cl = new Cylinder(radius, 0.01f, app);
        cl.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        cl.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
        cl.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

        bg.addChild(cl);
        return bg;
    }

    public void setArrowVisibility(boolean visible){
        rendAttr.setVisible(visible);
    }

    public BranchGroup getText2D(String message){

        Transform3D rotY =  new Transform3D();
        Transform3D rotX =  new Transform3D();
        TransformGroup tGroup = new TransformGroup();
        BranchGroup bg = new BranchGroup();

        text = new Text2D(message, new Color3f(Color.BLUE), "Helvetica", 50, Font.BOLD);
        text.getAppearance().setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);
        text.getAppearance().setCapability(Appearance.ALLOW_TEXTURE_WRITE);

        // make text 2-sided
        Appearance app = text.getAppearance();
        PolygonAttributes polyAttrib = new PolygonAttributes();
        polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
        polyAttrib.setBackFaceNormalFlip(true);
        app.setPolygonAttributes(polyAttrib);

        tGroup.addChild(text);

        rotY.rotY(Math.toRadians(90));
        rotX.rotX(Math.toRadians(-90));
        rotY.mul(rotX);
        tGroup.setTransform(rotY);
        bg.addChild(tGroup);
        return bg;
    }

    public void setText2D(String message){
        text.setString(message);
    }
}
