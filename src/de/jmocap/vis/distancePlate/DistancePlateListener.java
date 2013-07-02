/**
 * @author Hrstka Michael Christopher
 * @version 06.2013
 */
package de.jmocap.vis.distancePlatexxx;

import de.jmocap.anim.FrameChangeListener;

public class DistancePlateListener implements FrameChangeListener{
    private DistancePlateJMocap d;

    public DistancePlateListener(DistancePlateJMocap d){
        this.d = d;
    }
    
    @Override
    public void frameUpdate(int frameNumber) {
       d.updateDistancePlate();
    }
    
}
