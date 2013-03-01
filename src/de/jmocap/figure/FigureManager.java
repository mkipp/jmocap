package de.jmocap.figure;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

/**
 * Manages a number of figures and allows to control playback for 
 * all figures.
 * 
 * @author Michael Kipp
 */
public class FigureManager {

    private List<Figure> _figures = new ArrayList<Figure>();

    public FigureManager() {
    }

    /**
     * Creates a new figure object and add it to the pool.
    
     * @return Created figure object.
     */
    public Figure addFigure(String name, Bone skeleton, Point3d offset) {
        Figure f = new Figure(name, skeleton);
        f.setOffset(offset);
        _figures.add(f);
        return f;
    }

    public List<Figure> getFigures() {
        return _figures;
    }

    public void update(float fps) {
        for (Figure f : _figures) {
            if (f.hasAnimation()) {
                f.getPlayer().update(fps);
            }
        }
    }

    public boolean playAll() {
        if (_figures.size() > 0) {
            for (Figure f : _figures) {
                f.getPlayer().setIsPlaying(true);
            }
            return true;
        } else {
            return false;
        }
    }

    public void pauseAll() {
        for (Figure f : _figures) {
            f.getPlayer().setIsPlaying(false);
        }
    }

    public void stopAll() {
        for (Figure f : _figures) {
            f.getPlayer().reset();
        }
    }

    public void frameForwardAll() {
        for (Figure f : _figures) {
            f.getPlayer().frameForward();
        }
    }

    public void frameBackwardAll() {
        for (Figure f : _figures) {
            f.getPlayer().frameBackward();
        }
    }

    public void setFpsAll(float fps) {
        for (Figure f : _figures) {
            f.getPlayer().setPlaybackFps(fps);
        }
    }
}
