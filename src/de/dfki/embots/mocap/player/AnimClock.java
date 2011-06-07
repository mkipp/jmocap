package de.dfki.embots.mocap.player;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.dfki.embots.mocap.JMocap;
import de.dfki.embots.mocap.figure.FigureManager;

/**
 * Drives the figures managed by the FigureManager by calling the update
 * method.
 * 
 * @author Michael Kipp
 */
public class AnimClock extends Thread {

    private FigureManager _figureManager;
    private int _frames = 0;
    private float _fps = 100; // used for fps computation
    private long _t0 = System.currentTimeMillis();

    public AnimClock(FigureManager fm) {
        _figureManager = fm;
    }

    @Override
    public void run() {
        while (true) {
            _frames++;

            // TODO: make this dependent on time *****
            if (_frames >= 200) {
                long t = System.currentTimeMillis();
                _fps = _frames / ((t - _t0) / 1000f);
                _frames = 0;
                _t0 = t;
            }
            _figureManager.update(_fps);
            try {
                sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(JMocap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
