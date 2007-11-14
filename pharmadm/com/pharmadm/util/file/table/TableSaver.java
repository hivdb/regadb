/*
 * TableSaver.java
 *
 * Created on October 1, 2003, 2:09 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.file.table;

import java.io.File;
import java.util.Collection;
import javax.swing.table.TableModel;
import javax.swing.filechooser.FileFilter;

/**
 * An interface for classes capable of saving a TableModel to a file.
 * Implementing classes may support a specific file format.
 *
 * @invar getAlternativeExtensions().size() == getAlternativeFileFilters().size()
 *
 * @author  kdg
 */
public interface TableSaver {

    /**
     * The default filename extension for new files.
     * Implementing classes must ensure that the extension does not include a dot.
     * Must not return null.
     */
    public String getDefaultExtension();
    
    /**
     * Gets the alternative filename extensions for new files.
     * Implementing classes must ensure that the extensions do not include a dot.
     * The Collection will not contain the default extension.
     * If is not permitted for clients to change the collection.
     * The size and iteration order of the collection must correspond to getAlternativeFileFilters.
     * May return null.
     */
    public Collection getAlternativeExtensions();
    
    /**
     * Gets a filefilter associated with the default extension.
     * Must not return null.
     */
    public FileFilter getDefaultFileFilter();

    /**
     * Gets filefilters for all other file formats than the default that this TableSaver can handle.
     * The Collection will not contain the default FileFilter.
     * If is not permitted for clients to change the collection.
     * The size and iteration order of the collection must correspond to getAlternativeExtensions.
     * Must not return null.
     */
    public Collection getAlternativeFileFilters();

    /**
     * Opens a save dialog, alowing the user to select a file. The table will then be saved to the file.
     * The filename extension will be forced to be getDefaultFileFilter or to be in the range getAlternativeExtensions().
     *
     * @returns true iff the file was successfully saved
     */
    public boolean save(TableModel table);

    /**
     * Saves the TableModel to the given file, without user interaction, except when 
     * the file already exists (for confirmation) or when an IOException occurs.
     * The filename extension will not be manipulated.
     *
     * @pre the filename extension must be one of getExtensions() or the default extension.
     * @pre file must not be a directory
     * @returns true iff the file was successfully saved
     */
    public boolean save(TableModel table, File file);

    /**
     * Saves the TableModel to the given file, without any user interaction, except when an IOException occurs.
     * If the file already exists, it will be overwritten.
     * The filename extension will not be manipulated.
     *
     * @pre the filename extension must be one of getExtensions() or the default extension.
     * @pre file must not be a directory
     * @returns true iff the file was successfully saved
     */
    public boolean saveUnconditional(TableModel table, File file);
}
