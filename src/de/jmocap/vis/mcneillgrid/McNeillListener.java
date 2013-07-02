package de.jmocap.vis.mcneillgrid;

import de.jmocap.anim.FrameChangeListener;

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
