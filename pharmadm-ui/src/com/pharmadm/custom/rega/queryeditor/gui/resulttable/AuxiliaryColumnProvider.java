/*
 * AuxiliaryColumnProvider.java
 *
 * Created on July 12, 2004, 2:22 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.gui.resulttable;


import javax.swing.table.TableModel;

/**
 * A provider of auxiliary columns, to be used by an AuxiliaryColumnTableModel.
 * Auxiliary columns are columns that provide more information about an existing column
 * of a base table model.
 * The columns of the base model that have an auxiliary column are called 
 * augmented columns.
 *
 * @author  kdg
 */
public interface AuxiliaryColumnProvider {
    
    /**
     * Whether this AuxiliaryColumnProvider has an auxiliary column for 
     * the given base column.
     */
    public boolean isAugmentedColumn(TableModel baseModel, int col);
    
    /**
     * Gets the suffix for constructing a name for the auxliary columns out of 
     * the name of the base column. The suffix should be disciminatory for each provider type.
     */
    public String getNameSuffix();
    
    /**
     * Gets the value of the cell of the auxiliary column that corresponds to 
     * the given augmented column.
     */
    public Object getAuxiliaryValue(TableModel baseModel, int row, int augmentedColumn);

    /**
     * Whether the value of the cell of the auxiliary column that corresponds to 
     * the given augmented column can be changed using setAuxiliaryValue.
     */
    public boolean isAuxiliaryValueEditable(TableModel baseModel, int augmentedColumn);

    /**
     * Gets the value of the cell of the auxiliary column that corresponds to 
     * the given augmented column.
     *
     * @pre isAuxiliaryValueEditable(baseModel, augmentedColumn)
     */
    public void setAuxiliaryValue(Object value, TableModel baseModel, int row, int augmentedColumn);
}
