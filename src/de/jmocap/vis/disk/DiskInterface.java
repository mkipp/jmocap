/**
 * @author Hrstka Michael Christopher
 * @version 06.2013
 */
package de.jmocap.vis.disk;

public interface DiskInterface {

    public void setDisk();

    //get transformed disk
    public void getTransform();

    //general transparency of the disk
    public void setTransparency(float transparency);

    //general size of the disk
    public void setDiskRadius(float radius);

    //e.g. if you want the disk to be triggered only when big moves happen set the sensibility <1.0
    //if the disk should trigger at small moves set the sensibility >1.0
    public void setDiskSensibility(float sensibility);

    //set the number how many frames should be taken to calculate the average speed with	
    public void setFramesToCalculateAverageSpeedwith(int framesToCalculateAverageSpeedwith);

    //name of the bone to set the disk on
    public void setBoneName(String boneName);	
    
    //scale the size of the triggerd disk
    public void setTriggeredDiskScaleFactor(double scale);
}
