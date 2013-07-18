package de.jmocap.vis.handdirection;

import de.jmocap.anim.FrameChangeListener;

/**
 * @author Franziska Zamponi
 * @date 29.06.13
 */
public class HandDirectionFrameListener implements FrameChangeListener{
    
    private HandDirectionController.ArrowTrail trail;
    
    public HandDirectionFrameListener(HandDirectionController.ArrowTrail tac){
        this.trail = tac;
    }
    
    @Override
    public void frameUpdate(int frame){
        trail.updateSwitch(frame);
    }
}
