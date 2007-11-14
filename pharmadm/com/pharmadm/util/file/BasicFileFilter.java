/*
 * BasicFileFilter.java
 *
 * Created on December 18, 2001, 1:11 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.file;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * A file filter that only uses the extension of a filename.
 *
 * @author  henkv, kdg
 * @version 2.0
 */
public class BasicFileFilter extends FileFilter {
    
    private final String extension;
    private final String dottedExtension;
    private final String description;
    
    /** Creates new BasicFileFilter with a default description.
     *
     * @pre extension != null
     *
     * @param extension the filename extension to use as criterium (may be with or without dot prefix)
     */
    public BasicFileFilter(String extension) {
        this(extension, extension);
    }
    
    /** Creates new BasicFileFilter
     *
     * @pre extension != null
     * @param extension the filename extension to use as criterium (may be with or without dot prefix)
     * @param description a description of the file type that this filter accepts
     */
    public BasicFileFilter(String extension, String description) {
        while ((extension.length() >= 1) && (extension.charAt(0) == '.')) {
            extension = extension.substring(1);
        }
        this.extension = extension;
        this.dottedExtension = '.' + extension;
        this.description = description;
    }
    
    /** Whether the given file is accepted by this filter. A file is accepted if
     * it is a directory or if the extension of the filename matches
     * (case insensitive) the extension of this filter.
     *
     * Note that not accepting directories might prevent filesystem navigation in common file dialogs in some
     * versions of the JRE.  When all commonly used versions of the JRE do not longer have this
     * restriction, this method might be changed to no longer accept directories.
     *
     * @param file the file that is to accept or not
     * @return whether this filter accepts the given file
     */
    public boolean accept(File file) {
        if (file!= null) {
            if (file.isDirectory()) {
                return true;
            } else {
                final String fileName = file.getName();
                return dottedExtension.regionMatches(true, 0, fileName, fileName.length()-dottedExtension.length(), dottedExtension.length());
            }
        }
        return false;
    }
    
    /**
     * Gets the extension that this filefilter uses as it's criterium for accepting files.
     * 
     * @return the filename extension without a dot prefix.
     */
    public String getExtension() {
        return extension;
    }

    public String getDescription() {
        return description;
    }
}
