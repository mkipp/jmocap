
package de.jmocap.vis.tangentialarrow;

import javax.media.j3d.*;
import javax.vecmath.*; 

/**
 *
 * @author Franziska
 * @date 29.06.13
 * 
 * contains one actual visible arrow object,
 * stores some information needed by TangentialArrowController
 */
public class TangentialArrow {
 
    private ArrowInterface _arrow; 
    private Point3d _position; 
    private String _boneName;
    private double _time;
    private int _frame; //the arrow is supposed to be visual at this frame
    private TransformGroup _tg; //to put in Switch
    private int _switchIndex; //position in its Switch
    
    public TangentialArrow(int frame, double time, String boneName, double scale){
        _arrow = new Arrow();
        _arrow.setScale(scale);
        this._time = time;
        this._frame = frame;
        this._boneName = boneName;
        _tg = new TransformGroup();
        _tg.addChild(_arrow.getRoot());
    }
    
    public TangentialArrow(int frame, double time, String boneName, double scale, Point3d position){
        this(frame, time, boneName, scale);
        setPosition(position);
    }
    
    public void setArrow(Arrow arrow){
        this._arrow = arrow;
        _tg = new TransformGroup();
        _tg.addChild(_arrow.getRoot());
    }
    
    public void setRotation(Vector3d vAimPos){
        Vector3d vOriginPos = new Vector3d(0, 1, 0); 
        Vector3d vCross = new Vector3d();
        vCross.cross(vOriginPos, vAimPos);
        double angle = vOriginPos.angle(vAimPos);
        
        Transform3D t3dVector = new Transform3D();
        t3dVector.setRotation(new AxisAngle4d(vCross, angle));
        _arrow.getTransformGroupAngle().setTransform(t3dVector);
    }
    
    public void setPosition(Vector3d vAimPos){
        // arrange arrow:
        Transform3D t3dVector = new Transform3D();
        t3dVector.setTranslation(vAimPos);
        _arrow.getTransformGroupPosition().setTransform(t3dVector);
        
        // save position:
        _position.x = vAimPos.x;
        _position.y = vAimPos.y;
        _position.z = vAimPos.z;
    }
    
    public void setPosition(Point3d pAimPos){
        // arrange arrow:
        Vector3d vector = new Vector3d(pAimPos);
        Transform3D t3dVector = new Transform3D();
        t3dVector.setTranslation(vector);
        _arrow.getTransformGroupPosition().setTransform(t3dVector);
        
        // save position:
        _position = pAimPos;
    }
    
    public Point3d getPositionPoint(){
        return _position;
    }
    
    public void setSwitchIndex(int index){
        this._switchIndex = index;
    }
    
    public int getSwitchIndex(){
        return _switchIndex;
    }
    
    public TransformGroup getTgForSwitch(){
        return _tg;
    }
    
    public int getFrame(){
        return _frame;
    }
}
