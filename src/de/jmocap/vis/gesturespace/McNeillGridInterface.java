package de.jmocap.vis.gesturespace;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

/**
 * @author Levin Freiherr von Hollen
 * @version 14-07-2013
 */
public interface McNeillGridInterface {

    BranchGroup getGrid();

    void createGrid(float shoulderWidth);

    Vector3f getPosition();

    Vector3f getVectorFromRightToLeftofTheGrid();

    void activateSegment(GestureSpaceSector segment, Color3f color);

    void deactivateSegment(GestureSpaceSector segment);

    void activateAllSegments();

    void deactivateAllSegments();

    void increaseXScale();

    void decreaseXScale();

    void increaseYScale();

    void decreaseYScale();
}
