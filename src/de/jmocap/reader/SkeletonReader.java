package de.jmocap.reader;

import java.io.File;
import java.io.IOException;

import de.jmocap.figure.Bone;

/**
 *
 * @author Michael Kipp
 */
public interface SkeletonReader {
    public Bone getSkeleton(File file) throws IOException;
}
