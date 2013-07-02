/**
 * @author Hrstka Michael Christopher
 * @version 06.2013
 */
package de.jmocap.vis.disk;

import de.jmocap.anim.FrameChangeListener;

public class DiskListener implements FrameChangeListener{

    private DiskInterface di;

    public DiskListener(DiskJmocap di){
        this.di = di;
    }

    public void frameUpdate(int frameNumber) {
        di.getTransform();

    }



}
