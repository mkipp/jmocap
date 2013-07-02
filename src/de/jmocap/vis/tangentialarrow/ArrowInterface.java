
package de.jmocap.vis.tangentialarrow;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.TransformGroup;

/**
 *
 * @author Franziska
 * @date 29.06.13
 */
public interface ArrowInterface {
    
    public BranchGroup getRoot();
    
    public TransformGroup getTransformGroupAngle();
    
    public TransformGroup getTransformGroupPosition();
    
    public Appearance getAppearance();
    
    public void setColoringAttributes(ColoringAttributes colAttr);
    
    public void setScale(double scale);
}
