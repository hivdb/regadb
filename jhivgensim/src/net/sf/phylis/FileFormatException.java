package net.sf.phylis;
/*
 * Created on Apr 7, 2003
 */

/**
 * A file format exception was encountered during reading a file
 */
public class FileFormatException extends Exception {
    private final String errorMessage;
    private final int lineNumber;

    public FileFormatException(String errorMessage, int lineNumber) {
        super("at line " + lineNumber + ": " + errorMessage);
        
        this.errorMessage = errorMessage;
        this.lineNumber = lineNumber;
    }
}
