/**
 * @author Michael Christopher Hrstka
 * @version 06.2013
 */
package de.jmocap.vis.distance;

import de.jmocap.anim.FrameChangeListener;

public class DistancePlateListener implements FrameChangeListener {

    private DistanceController d;

    public DistancePlateListener(DistanceController d) {
        this.d = d;
    }

    @Override
    public void frameUpdate(int frameNumber) {
        d.updateDistancePlate();
    }
}
