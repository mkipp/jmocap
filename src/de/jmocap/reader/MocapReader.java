package de.jmocap.reader;

import de.jmocap.anim.MotionData;
import de.jmocap.figure.Bone;
import java.io.File;
import java.io.IOException;

/**
 * Generalized interface for motion capture data readers.
 * You first read in the files, then access the skeleton
 * and motion data via accessor methods.
 * 
 * @author Michael Kipp
 */
public interface MocapReader {
    
    /**
     * Reads in motion capture files. For BVH, just
     * specify a single file. For ASF/AMC, specify
     * the two files.
     * 
     * @param file one file (BVH) or two files (ASF+AMC)
     */
    void readFiles(File... files) throws IOException;
    
    Bone getSkeleton();
    
    MotionData getMotion();
}
