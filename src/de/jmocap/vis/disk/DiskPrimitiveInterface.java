/**
 * @author Hrstka Michael Christopher
 * @version 06.2013
 */
package de.jmocap.vis.disk;

import javax.media.j3d.BranchGroup;

public interface DiskPrimitiveInterface {
    
    // create a normale grey disk
    public BranchGroup createDisk(float transparency, float radius);
    
    // create a disk the a direction arrow
    public BranchGroup createDiskWithDirectionarrow(float transparency, float radius);
    
    // create 2d-text
    public BranchGroup getText2D(String message);
    
    // alter the 2d-text
    public void setText2D(String message);
    
    // set the visibility of the direction arrow
    public void setArrowVisibility(boolean visible);
    
}
