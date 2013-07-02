
package de.jmocap.vis.facingangle;

import de.jmocap.anim.FrameChangeListener;

/**
 *
 * @author Franziska
 * @date 29.06.13
 */
public class FacingAngleFrameListener implements FrameChangeListener{
    private FacingAngleController _fac;
    
    public FacingAngleFrameListener(FacingAngleController facingAngleController){
        _fac = facingAngleController;
    }
    
    public void frameUpdate(int frame){
        _fac.updateFacingAngle();
    }
}
