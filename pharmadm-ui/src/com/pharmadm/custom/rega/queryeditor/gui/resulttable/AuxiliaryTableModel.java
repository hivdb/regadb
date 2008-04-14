/*
 * AuxiliaryTableModel.java
 *
 * Created on July 9, 2004, 5:02 PM
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
 * A TableModel with columns that contain additional information, related to
 * the columns of a base table model.  It also determines in what order
 * the auxiliary columns should be merged with the base columns.
 *
 * @see MergedTableModel
 *
 * The implementing class must ensure that:
 *
 * @invar getBaseModel() != null.
 * @invar getBaseModelColumnLocations() != null
 * @invar getRowCount() == getBaseModel().getRowCount()
 * @invar getColumnCount() + getBaseModel().getColumnCount() == getBaseModelColumnLocations().length
 * @invar getBaseModel().getColumnCount() == the number of values equal to true in getBaseModelColumnLocations()
 *
 * @author  kdg
 */
public interface AuxiliaryTableModel extends TableModel {
    
    /**
     * Returns the base table model.
     */
    public TableModel getBaseModel();
    
    /**
     * Returns an array where true on a position indicates that that merged table
     * should contain a column of the base model on that position.
     * False indicates that an auxiliary column should be used.
     */
    public boolean[] getBaseModelColumnLocations();
}