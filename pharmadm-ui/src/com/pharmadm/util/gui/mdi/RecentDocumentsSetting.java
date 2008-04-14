/*
 * RecentDocumentsSetting.java
 *
 * Created on May 13, 2004, 1:45 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.gui.mdi;

import java.io.File;

import com.pharmadm.util.settings.FileSetting;
import com.pharmadm.util.settings.FixedComposedSetting;

/**
 * A setting to that maintains an ordered list of FileSettings.
 *
 * @author  kdg
 */
public class RecentDocumentsSetting extends FixedComposedSetting {
    
    private int cardinality;
    
    /**
     * Creates a new instance of RecentDocumentsSetting.
     *
     * @param name a valid setting name, e.g. RecentCubes
     * @param cardinality the number of FileSettings to maintain.
     */
    public RecentDocumentsSetting(String name, int cardinality) {
        setName(name);
        this.cardinality = cardinality;
        // the names are user-visible, so they count from 1, not zero.
        for (int i = 1; i<= cardinality; i++) {
            add(new FileSetting(null, "Document" + i));
        };
    }
    
    /**
     * Gets the filesetting with the given index.
     *
     * @pre 0 <= index < cardinality
     */
    public FileSetting getRecentFileSetting(int index) {
        return (FileSetting)getChild("Document"+ (index + 1));
    }
    
    /**
     * Gets the file from the filesetting with the given index.
     *
     * @pre 0 <= index < cardinality
     */
    public File getRecentFile(int index) {
        return getRecentFileSetting(index).fileValue();
    }
    
    /**
     * Returns the number of recent document filesettings maintained.
     */
    public int getCardinality() {
        return cardinality;
    }
}
