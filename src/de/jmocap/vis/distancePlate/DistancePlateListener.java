/**
 * @author Michael Christopher Hrstka
 * @version 06.2013
 */
package de.jmocap.vis.distancePlate;

import de.jmocap.anim.FrameChangeListener;

public class DistancePlateListener implements FrameChangeListener {

    private DistancePlateJMocap d;

    public DistancePlateListener(DistancePlateJMocap d) {
        this.d = d;
    }

    @Override
    public void frameUpdate(int frameNumber) {
        d.updateDistancePlate();
    }
}
