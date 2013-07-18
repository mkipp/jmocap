package de.jmocap.vis.handdirection;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

/**
 * @author Franziska Zamponi
 * @date 29.06.13
 */
public class Arrow implements ArrowInterface{
    private BranchGroup root;
    private TransformGroup tgScale; // transform group for size
    private TransformGroup tgAngle;
    private TransformGroup tgPosition;
    private Appearance appearance; 
    private Cone cone;
    private Cylinder cylinder;
    
    public Arrow(){
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
        cylinder = new Cylinder( 0.05f, 4.0f, appearance );
        cylinder.setCapability(Cylinder.ENABLE_APPEARANCE_MODIFY);
        tgScale.addChild( cylinder );
        
        //Pfeilspitze anfügen:
        cone = new Cone(0.2f, Primitive.GENERATE_NORMALS, appearance);
        cone.setCapability(Cone.ENABLE_APPEARANCE_MODIFY);
        TransformGroup tgCone = new TransformGroup();
        Transform3D t3dCone = new Transform3D();
        t3dCone.setTranslation(new Vector3d(0.0d, 2.0d, 0.0d)); //Translation zum verschieben an die spitze
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
    /**
     * 
     * chould scale both the size of the cylinder and cone by one factor
     */
    @Override
    public void setScale(double scale){
        Transform3D t3d = new Transform3D();
        t3d.setScale(scale);
        tgScale.setTransform(t3d);
    }
    
}
