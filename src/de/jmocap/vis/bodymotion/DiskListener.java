package de.jmocap.vis.bodymotion;

import de.jmocap.anim.FrameChangeListener;

/**
 * @author Michael Christopher Hrstka
 * @version 06.2013
 */

public class DiskListener implements FrameChangeListener{

    private DiskInterface di;

    public DiskListener(Disk di){
        this.di = di;
    }

    @Override
    public void frameUpdate(int frameNumber) {
        di.getTransform();

    }
}
