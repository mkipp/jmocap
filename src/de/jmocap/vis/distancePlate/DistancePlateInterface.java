package de.jmocap.vis.distancePlate;

/**
 * @author Michael Christopher Hrstka
 * @version 06.2013
 */
public interface DistancePlateInterface {

    // create distance plate
    void setDistancePlate();

    //set relative distance
    // e.g. the length between the waist and a knee of a normal person is 0.5m
    // therefore the first sector to be red is 0-1m and in our program 0- 2*relativeDistanceFactor
    void setRelativeDistance(String pointOne, String pointTwo);

    //alter the plate
    void updateDistancePlate();

    //set size of distance plate
    void setDistancePlateSize(float size);

    //set the number how many frames should be taken to calculate with	
    void setFramesToCalculateAverageWith(int framesToCalculateWith);
}
