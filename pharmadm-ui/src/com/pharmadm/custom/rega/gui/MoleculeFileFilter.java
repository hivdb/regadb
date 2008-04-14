/*
 * MoleculeFileFilter.java
 *
 * Created on September 13, 2003, 10:55 PM
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
 * @author  kristof
 */
public class MoleculeFileFilter extends FileExtensionFilter {

    public MoleculeFileFilter() {
        super(new String[]{"mol","mol2","sd"}, "Mol Files");
    }

}
