package de.jmocap.vis.mcneillgrid;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

public interface McNeillGridInterface {

	public BranchGroup getGrid();
	
	public void createGrid(float shoulderWidth);
	public Vector3f getPosition();
	public Vector3f getVectorFromRightToLeftofTheGrid();
	
	public void activateSegment(Segment segment, Color3f color);
	public void deactivateSegment(Segment segment);
	
	public void activateAllSegments();
	public void deactivateAllSegments();
	
	public void increaseXScale();
	public void decreaseXScale();
	
	public void increaseYScale();
	public void decreaseYScale();
}
