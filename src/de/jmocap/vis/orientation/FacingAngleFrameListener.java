package de.jmocap.vis.orientation;

import de.jmocap.anim.FrameChangeListener;

/**
 * @author Franziska Zamponi
 * @date 29.06.13
 */
public class FacingAngleFrameListener implements FrameChangeListener {

    private FacingAngleController _fac;

    public FacingAngleFrameListener(FacingAngleController facingAngleController) {
        _fac = facingAngleController;
    }

    public void frameUpdate(int frame) {
        _fac.updateFacingAngle();
    }
}
