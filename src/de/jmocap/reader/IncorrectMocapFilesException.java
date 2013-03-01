package de.jmocap.reader;

import java.io.IOException;

/**
 * @author Michael Kipp
 */
public class IncorrectMocapFilesException extends IOException {

    public IncorrectMocapFilesException() {
    }

    public IncorrectMocapFilesException(String string) {
        super(string);
    }
}
