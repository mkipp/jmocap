package de.jmocap.reader;

import de.jmocap.anim.MotionData;
import de.jmocap.figure.Bone;
import java.io.File;
import java.io.IOException;

/**
 * @author Michael Kipp
 */
public class AcclaimReader implements MocapReader {
    
    private Bone skeleton;
    private MotionData motionData;

    @Override
    public void readFiles(File... files) throws IOException {
        if (files.length < 2) 
            throw new IncorrectMocapFilesException
                    ("You have to specify two files: ASF and AMC.");
        File asfFile = files[0];
        File amcFile = files[1];
        ASFReader asfReader = new ASFReader();
        skeleton = asfReader.readSkeleton(asfFile);
        AMCReader amcReader = new AMCReader();
        motionData = amcReader.readMotion(amcFile, skeleton);
    }

    @Override
    public Bone getSkeleton() {
        return skeleton;
    }

    @Override
    public MotionData getMotion() {
        return motionData;
    }

}
