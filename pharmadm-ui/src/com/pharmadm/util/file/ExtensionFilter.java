/*
 * ExtensionFilter.java
 *
 * Created on February 13, 2001, 5:41 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A Filter Class to filter Files by Extension.
 * @author  kdg
 * @version 2.0
 */
public class ExtensionFilter implements FilenameFilter {
    
     /* The extension*/
    private String extension;
    private boolean partialMatchAllowed;
    
     /**
      * Construct a new Extension Filter.
      *
      * @param	extension	the (exact) extension of the filename.
      */
    public ExtensionFilter(String extension) {
        this.extension = extension;
        this.partialMatchAllowed = false;
    }
    
    public ExtensionFilter(String extension, boolean partialMatchAllowed) {
        this.extension = extension;
        this.partialMatchAllowed = partialMatchAllowed;
    }
    
     /**
      * Test if a given File is accepted by this Filter.
      *
      * @param	dir	the Directory in which the File is located.
      * @param	dir	the Name of the File.
      *
      * @returns true if the given File is accepted.
      */
    public boolean accept(File dir, String name) {
        String fileext = FileUtil.getExtension(name);
        if (fileext == null) return false;
        if (partialMatchAllowed) {
            return (fileext.indexOf(extension) != -1);
        } else {
            return fileext.equals(extension);
        }
    }
    
}