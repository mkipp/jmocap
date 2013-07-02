
package de.jmocap.vis.facingangle;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Text2D;
import de.jmocap.vis.tangentialarrow.Arrow;
import de.jmocap.vis.tangentialarrow.ArrowInterface;
import java.awt.Font;
import javax.media.j3d.Appearance;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import java.lang.Math;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Switch;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Tuple3d;

/**
 *
 * @author Franziska
 * @date 29.06.13
 */
public class FacingAngle {
    
    private ArrowInterface _arrowPassive;
    private ArrowInterface _arrowActive;
    private Sphere _joint;
    private Text2D _text;
    
    private TransformGroup _tgAnglePassive;
    private TransformGroup _tgAngleActive;
    private TransformGroup _tgPosition; 
    
    private Switch _switch;
    
    private BranchGroup _root;
    
    public FacingAngle(){
        _arrowPassive = new ShortArrow();
        _arrowPassive.setScale(0.5); 
        _arrowActive = new ShortArrow();
        _arrowActive.setScale(0.5); 
        _joint = new Sphere(0.1f, _arrowPassive.getAppearance());
        
        _text = new Text2D("00000°", new Color3f(1, 1, 1), "Helvetica", 40, Font.BOLD);
        _text.getAppearance().setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);
        _text.getAppearance().setCapability(Appearance.ALLOW_TEXTURE_WRITE);
        PolygonAttributes polyAttrib = new PolygonAttributes();
        polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
        polyAttrib.setBackFaceNormalFlip(true);
        _text.getAppearance().setPolygonAttributes(polyAttrib); // make text 2-sided
        
        _tgPosition = new TransformGroup();
        _tgPosition.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);  
        _tgPosition.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE); //*******
        _tgAnglePassive = new TransformGroup();
        _tgAnglePassive.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        _tgAngleActive = new TransformGroup();
        _tgAngleActive.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        _switch = new Switch();
        _switch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        _root = new BranchGroup();
        _root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        
        // the passive, not moving arrow points to the top
        // but we want it to lay on the ground:
        // position passive arrow so that its end is at the sphere (middle):
        Transform3D t3d1 = new Transform3D();
        t3d1.setTranslation(new Vector3d(0.0, 0.5, 0.0));
        _arrowPassive.getTransformGroupPosition().setTransform(t3d1);
        
        // do the same with active arrow:
        _arrowActive.getTransformGroupPosition().setTransform(t3d1);
        
        // move text a bit upwards:
        TransformGroup tgText = new TransformGroup();
        Transform3D t3dText = new Transform3D();
        t3dText.setTranslation( new Vector3d(0, 0.1, 0));
        tgText.setTransform(t3dText);
        
        // complete node tree:        
        _tgAnglePassive.addChild(_arrowPassive.getRoot());
        _tgPosition.addChild(_tgAnglePassive);
        
        _tgAngleActive.addChild(_arrowActive.getRoot());
        _tgPosition.addChild(_tgAngleActive);
        
        tgText.addChild(_text);
        _tgPosition.addChild(tgText); 
        
        _tgPosition.addChild(_joint);
        _switch.addChild(_tgPosition);
        _root.addChild(_switch);
    }
    
    public void setText(String s){
        s = s.substring(0, 5); //we only want a number of 4 digits 
        s = s + "°";
        _text.setString(s);
    }
    
    public void setAngleActive(Vector3d vAimPos){
        _tgAngleActive.setTransform( getAngleTransform3D(vAimPos) );
    }
    
    public void setAnglePassive(Vector3d vAimPos){
        _tgAnglePassive.setTransform( getAngleTransform3D(vAimPos) );
    }
    
    private Transform3D getAngleTransform3D(Vector3d vAimPos){
        Vector3d vOriginPos = new Vector3d(0, 1, 0); // default setting
        Vector3d vCross = new Vector3d();
        vCross.cross(vOriginPos, vAimPos);
        double angle = vOriginPos.angle(vAimPos);
        Transform3D t3d = new Transform3D();
        t3d.setRotation(new AxisAngle4d(vCross, angle));
        return t3d;
    }
    
    public void setPosition(Vector3d v){
        Transform3D t3d = new Transform3D();
        t3d.setTranslation(v);
        _tgPosition.setTransform(t3d);
    }
    
    public void setColoringAttributes(ColoringAttributes newColAttr){
        _arrowActive.setColoringAttributes(newColAttr);
        _arrowPassive.setColoringAttributes(newColAttr);
    }
    
    public void setVisible(){
        _switch.setWhichChild(0);
    }
    
    public void setInvisible(){
        _switch.setWhichChild(Switch.CHILD_NONE);
    }
    
    public BranchGroup getBranchGroupRoot(){
        return _root;
    }
    
}
