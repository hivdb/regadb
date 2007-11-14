/*
 * TextFileFormatException.java
 *
 * Created on February 2, 2001, 11:15 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.file;

/**
 * An Exception class indicating that a file is not correctly formatted.
 *
 * @author  kdg
 * @version 1.0
 */
public class FileFormatException extends java.lang.Exception {

    // The line nmber is not exactly known
    private static final int LINENUMBER_UNKNOWN = -1;
    // The file is not being read as a text file
    private static final int LINENUMBER_NA = -2;
    
    private int line;
    private String description;
    private String fileName;
    
    /**
     * Constructs a new FileFormatException object
     *
     * @param	description	the description of the format error
     * @param	fileName	the file in which the format error is found
     * @param	lineNumber	the line on which the format error is found
     *
     * @pre     description != null
     *		fileName != null
     *		lineNumber > 0 or lineNumber == LINENUMBER_UNKNOWN or lineNumber == LINENUMBER_NA
     */
    public FileFormatException(String description,String fileName,int lineNumber) {
        this.line = lineNumber;
        this.description = description;
    }
    
    /**
     * Returns the description of the format error
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Returns the line number on which the format error is found, or
     * LINENUMBER_UNKNOWN if the line number is not exactly known or
     * LINENUMBER_NA if the file is not being read as a text file.
     */
    public int getLineNumber() {
        return line;
    }
    
    /**
     * Returns the name of the file in which the format error is found
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Returns a string representation of this Exception
     */
    public String toString() {
        return "Invalid file format: " + description;
    }
}


