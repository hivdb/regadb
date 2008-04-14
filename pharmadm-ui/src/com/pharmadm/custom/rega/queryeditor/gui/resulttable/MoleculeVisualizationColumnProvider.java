/*
 * MoleculeVisualizationModel.java
 *
 * Created on July 9, 2004, 6:57 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.gui.resulttable;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.*;

/**
 * A column providerthat provides a column with a molecule visualization for each moleculeid column in the base model.
 *
 * @author  kdg
 */
public class MoleculeVisualizationColumnProvider implements AuxiliaryColumnProvider {
    
    private final static String COL_NAME_SUFFIX_VISUALIZATION = " visualization";
    private final static String COL_NAME_SUFFIX_MOLECULEID = ".MOLECULEID";
    
    public String getNameSuffix() {
        return COL_NAME_SUFFIX_VISUALIZATION;
    }
    
    public String getMoleculeIDSuffix() {
        return COL_NAME_SUFFIX_MOLECULEID;
    }
    
    public Object getAuxiliaryValue(TableModel baseModel, int row, int augmentedColumn) {
        return baseModel.getValueAt(row, augmentedColumn);
    }
    
    public boolean isAugmentedColumn(TableModel baseModel, int col) {
        return isMoleculeIDColumn(baseModel, col);
    }
    
    public boolean isMoleculeIDColumn(TableModel model, int col) {
        return model.getColumnName(col).endsWith(getMoleculeIDSuffix());
    }
    
    public void setAuxiliaryValue(Object value, TableModel baseModel, int row, int augmentedColumn) {
        // do nothing
    }
    
    public boolean isAuxiliaryValueEditable(TableModel baseModel, int augmentedColumn) {
        return false;
    }
    
}