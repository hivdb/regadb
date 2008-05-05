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
 * A TableModel that has an auxiliary column for some of the columns in the base model.
 * It hints the merged model to use each auxiliary column just after its corresponding base column.
 *
 * @author  kdg
 */
public class AuxiliaryColumnsTableModel extends AbstractTableModel implements AuxiliaryTableModel {
    
    private final TableModel baseModel;
    private final AuxiliaryColumnProvider auxiliaryColumnProvider;
    
    // column indexes into basemodel that indicate columns for which this model has an auxiliary column
    // the indexes are stored in ascending order
    private final int[] augmentedBaseColumns;
    
    // each position indicates if the merged model has a base column on that position
    // (otherwise it's an auxiliary column)
    private final boolean[] baseModelColumnLocations;
    
    AuxiliaryColumnsTableModel(TableModel baseModel, AuxiliaryColumnProvider auxiliaryColumnProvider) {
        this.baseModel = baseModel;
        this.auxiliaryColumnProvider = auxiliaryColumnProvider;
        List<Integer> baseCols = new ArrayList<Integer>();
        for (int i = 0; i < baseModel.getColumnCount(); i++) {
            if (auxiliaryColumnProvider.isAugmentedColumn(baseModel, i)) {
                baseCols.add(new Integer(i));
            }
        }
        augmentedBaseColumns = new int[baseCols.size()];
        for (int i = 0; i < baseCols.size(); i++) {
            augmentedBaseColumns[i] = ((Integer)baseCols.get(i)).intValue();
        }
        baseModelColumnLocations = new boolean[baseModel.getColumnCount() + augmentedBaseColumns.length];
        initializeBaseModelColumnLocations();
    }
    
    // public abstract boolean providesAuxiliaryColumnFor(TableModel baseModel, int col);
    
    // public abstract String getNameSuffix();
    
    private void initializeBaseModelColumnLocations() {
        int baseColsDone = 0;
        int currentBaseCol = -2;
        if (augmentedBaseColumns.length > 0) {
            currentBaseCol = augmentedBaseColumns[baseColsDone];
        }
        for (int i = 0; i < baseModelColumnLocations.length; i++) {
            if (i != (currentBaseCol + baseColsDone + 1)) {  // indexes shift by CurrentMol because of the inserted columns
                // use base model column
                baseModelColumnLocations[i] = true;
            } else {
                // use auxiliary column and move to next molecule
                baseColsDone++;
                if (baseColsDone < augmentedBaseColumns.length) {
                    currentBaseCol = augmentedBaseColumns[baseColsDone];
                } else {
                    currentBaseCol = baseModelColumnLocations.length;
                }
            }
        }
    }
    
    public int getColumnCount() {
        return augmentedBaseColumns.length;
    }
    
    public String getColumnName(int columnIndex) {
        int baseColIndex = augmentedBaseColumns[columnIndex];
        return baseModel.getColumnName(baseColIndex) + auxiliaryColumnProvider.getNameSuffix();
    }
    
    public int getRowCount() {
        return baseModel.getRowCount();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        int baseColIndex = augmentedBaseColumns[columnIndex];
        return auxiliaryColumnProvider.getAuxiliaryValue(baseModel, rowIndex, baseColIndex);
    }
    
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        int baseColIndex = augmentedBaseColumns[columnIndex];
        auxiliaryColumnProvider.setAuxiliaryValue(value, baseModel, rowIndex, baseColIndex);
    }

    public TableModel getBaseModel() {
        return baseModel;
    }
    
    public boolean[] getBaseModelColumnLocations() {
        return baseModelColumnLocations;
    }
}