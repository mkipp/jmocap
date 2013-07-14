package de.jmocap.vis.disk;

import javax.media.j3d.BranchGroup;

/**
 * @author Michael Christopher Hrstka
 * @version 06.2013
 */

public interface DiskPrimitiveInterface {

    // create a normale grey disk
    BranchGroup createDisk(float transparency, float radius);

    // create a disk the a direction arrow
    BranchGroup createDiskWithDirectionArrow(float transparency, float radius);

    // create 2d-text
    BranchGroup getText2D(String message);

    // alter the 2d-text
    void setText2D(String message);

    // set the visibility of the direction arrow
    void setArrowVisibility(boolean visible);
}
