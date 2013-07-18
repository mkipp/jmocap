package de.jmocap.anim;

/**
 * To be notified when skeleton comes to a new frame number.
 * 
 * @author Michael Kipp
 * @version 14-05-2013
 */

public interface FrameChangeListener {
    
    /**
     * Frame has changed to given frame number.
     * 
     * @param frameNumber current frame
     */
    void frameUpdate(int frameNumber);
    
}