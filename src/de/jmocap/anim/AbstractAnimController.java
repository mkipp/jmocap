package de.jmocap.anim;

import java.util.ArrayList;
import java.util.List;

import de.jmocap.figure.Bone;

/**
 * Abstract player for animation. It is a passive player which means
 * that the update method must be called continually to make the 
 * animation run.
 * 
 * @author Michael Kipp
 */
public abstract class AbstractAnimController {
    
    protected List<FrameChangeListener> _listeners = new ArrayList<FrameChangeListener>();
    
    public void addListener(FrameChangeListener li) {
        _listeners.add(li);
    }
    
    public void removeListener(FrameChangeListener li) {
        _listeners.remove(li);
    }
    
    public abstract Bone[] getBones();
    
    public abstract void gotoTime(double sec);
    
    public abstract void setIsPlaying(boolean val);
    
    public abstract boolean isPlaying();
    
    public abstract void reset();
    
    public abstract void update(double fps);

}
