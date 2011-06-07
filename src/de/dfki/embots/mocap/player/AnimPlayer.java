package de.dfki.embots.mocap.player;

import java.util.ArrayList;
import java.util.List;

import de.dfki.embots.mocap.figure.Bone;

/**
 * Abstract player of figure animations.
 * 
 * @author Michael Kipp
 */
public abstract class AnimPlayer {
    protected List<PlayerFrameListener> _listeners = new ArrayList<PlayerFrameListener>();
    
    public void addListener(PlayerFrameListener li) {
        _listeners.add(li);
    }
    
    public void removeListener(PlayerFrameListener li) {
        _listeners.remove(li);
    }
    
    public abstract Bone[] getBones();
    
    public abstract void gotoTime(double sec);
    
    public abstract void setIsPlaying(boolean val);
    
    public abstract boolean isPlaying();
    
    public abstract void reset();
    
    public abstract void update(float fps);

}
