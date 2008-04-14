/*
 * FileExtensionFilter.java
 *
 * Created on October 20, 2003, 10:55 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.gui;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;


/**
 *
 * @author  kristof, kdg
 */
public class FileExtensionFilter extends FileFilter {
    
    private String description;
    private String[] extensions;
    private String[] extensionsLCWithPoint; // normalized
    
    /**
     * @pre extensions does not contain null
     */
    public FileExtensionFilter(String[] extensions, String description) {
        this.extensions = extensions;
        this.description = description;
        this.extensionsLCWithPoint = new String[extensions.length];
        for (int i=0; i < extensions.length; i++) {
            String extension = extensions[i].toLowerCase();
            if (extension.startsWith(".")) {
                extensionsLCWithPoint[i] = extension;
            } else {
                extensionsLCWithPoint[i] = "." + extension;
            }
        }
    }
    
    /**
     * Accepts all directories and the files with one if the given extensions.
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String fileName = f.getName();
        if (fileName != null) {
            String fileNameLC = fileName.toLowerCase();
            for (String extension: extensionsLCWithPoint) {
                if (fileNameLC.endsWith(extension)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Gets the last extension of the file name in lower case.
     * Use repeatedly to scan composed extensions.
     */     
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    public String[] getExtensions() {
        return extensions;
    }
    
    /**
     * The description of this filter.
     */
    public String getDescription() {
        return description;
    }
}
