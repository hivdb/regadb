/*
 * MolFileFilter.java
 *
 * Created on March 3, 2003, 5:29 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.file;

import java.io.*;

/**
 *
 * @author  henkv
 */
public class MolFileFilter extends javax.swing.filechooser.FileFilter {
    
    public MolFileFilter() {
        super();
    }
    
    public boolean accept(File f) {
        String fs = f.getName();
        if (fs.endsWith(".mol") || fs.endsWith(".MOL") || f.isDirectory()) {
            return true;
        }
        return false;
    }
    
    public String getDescription() {
        return "MOL files (.mol)";
    }
}
