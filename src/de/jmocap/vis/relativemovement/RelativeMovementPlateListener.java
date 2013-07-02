
package de.jmocap.vis.relativemovement;

import de.jmocap.anim.FrameChangeListener;

public class RelativeMovementPlateListener implements FrameChangeListener{

	private RelativeMovementPlate rMP;
	
	public RelativeMovementPlateListener(RelativeMovementPlate rMP){
		this.rMP = rMP;
	}

	public void frameUpdate(int frameNumber) {
		rMP.update();
		
	}



}
