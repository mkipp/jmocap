package de.jmocap.vis.gesturespace;

import de.jmocap.anim.FrameChangeListener;

/**
 * @author Levin Freiherr von Hollen
 * @version 14-07-2013
 */
public class McNeillListener implements FrameChangeListener {

    McNeillGridLogic mng;

    public McNeillListener(McNeillGridLogic mng) {
        this.mng = mng;
    }

    @Override
    public void frameUpdate(int frameNumber) {
        mng.updateMcNeillGridPosition();
    }
}
