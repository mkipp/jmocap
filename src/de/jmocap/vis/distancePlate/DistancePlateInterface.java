/**
 * @author Hrstka Michael Christopher
 * @version 06.2013
 */
package de.jmocap.vis.distancePlatexxx;

public interface DistancePlateInterface {

    // create distance plate
    public void setDistancePlate();

    //set relative distance
    // e.g. the length between the waist and a knee of a normal person is 0.5m
    // therefore the first sector to be red is 0-1m and in our program 0- 2*relativeDistanceFactor
    public void setRelativeDistance(String pointOne, String pointTwo);

    //alter the plate
    public void updateDistancePlate();

    //set size of distance plate
    public void setDistancePlateSize(float size);
    
    //set the number how many frames should be taken to calculate with	
    public void setFramesToCalculateAverageWith(int framesToCalculateWith);

}
