package de.dfki.embots.mocap.reader;

import java.io.File;
import java.io.IOException;

import de.dfki.embots.mocap.figure.Bone;

/**
 *
 * @author Michael Kipp
 */
public interface SkeletonReader {
    public Bone getSkeleton(File file) throws IOException;
}
