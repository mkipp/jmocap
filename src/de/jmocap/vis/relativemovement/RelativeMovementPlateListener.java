package de.jmocap.vis.relativemovement;

import de.jmocap.anim.FrameChangeListener;

/**
 * @author Levin Freiherr von Hollen
 * @version 14-07-2013
 */
public class RelativeMovementPlateListener implements FrameChangeListener {

    private RelativeMovementPlate rMP;

    public RelativeMovementPlateListener(RelativeMovementPlate rMP) {
        this.rMP = rMP;
    }

    @Override
    public void frameUpdate(int frameNumber) {
        rMP.update();

    }
}
