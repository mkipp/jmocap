
package de.jmocap.vis.facingangle;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import de.jmocap.vis.tangentialarrow.ArrowInterface;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

/**
 *
 * @author Franziska
 * @date 29.06.13
 * 
 * this arrow has a smaler cone and shorter cylinder than the arrow in package "tangentialarrow"
 */
public class ShortArrow implements ArrowInterface{
    private BranchGroup root;
    private TransformGroup tgScale; // transform group for size
    private TransformGroup tgAngle;
    private TransformGroup tgPosition;
    private Appearance appearance; //not needed?
    private Cone cone;
    private Cylinder cylinder;
    
    public ShortArrow(){
        root = new BranchGroup();
        root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        
        appearance = new Appearance();
        Color3f purple = new Color3f(0.8f, 0.2f, 1.0f);
        ColoringAttributes colAtr = new ColoringAttributes(purple, ColoringAttributes.FASTEST);
        appearance.setColoringAttributes(colAtr);
        appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
        appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        
        tgScale = new TransformGroup();
        tgScale.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tgAngle = new TransformGroup();
        tgPosition = new TransformGroup();
        
        root.addChild( tgPosition );
        tgPosition.addChild(tgAngle);
        tgAngle.addChild(tgScale);
        cylinder = new Cylinder( 0.05f, 2.0f, appearance );
        cylinder.setCapability(Cylinder.ENABLE_APPEARANCE_MODIFY);
        tgScale.addChild( cylinder );
        
        //Pfeilspitze anfuegen:
        cone = new Cone(0.2f, Primitive.GENERATE_NORMALS, appearance);
        cone.setCapability(Cone.ENABLE_APPEARANCE_MODIFY);
        TransformGroup tgCone = new TransformGroup();
        Transform3D t3dCone = new Transform3D();
        t3dCone.setTranslation(new Vector3d(0.0d, 1.0d, 0.0d)); //moves cone to the top
        t3dCone.setScale(0.5); 
        tgCone.setTransform(t3dCone);
        tgCone.addChild(cone);
        cylinder.addChild(tgCone);
    }
    
    @Override
    public BranchGroup getRoot(){
        return root;
    }
    
    @Override
    public TransformGroup getTransformGroupAngle(){
        return tgAngle;
    }
    
    @Override
    public TransformGroup getTransformGroupPosition(){
        return tgPosition;
    }
    
    @Override
    public Appearance getAppearance(){
        return appearance;
    }
    
    public void setAppearance(Appearance newAppear){
        cylinder.setAppearance(newAppear);
        cone.setAppearance(newAppear);
        appearance = newAppear;
    }
    
    @Override
    public void setColoringAttributes(ColoringAttributes colAttr){
        appearance.setColoringAttributes(colAttr);
    }
    
    @Override
    public void setScale(double scale){
        Transform3D t3d = new Transform3D();
        t3d.setScale(scale);
        tgScale.setTransform(t3d);
    }
    
}

