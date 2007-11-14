/*
 * MergedTableModel.java
 *
 * Created on July 9, 2004, 4:11 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.gui.resulttable;

import java.util.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * A TableModel whose values and properties are based on (the columns of) two underlying TableModels.
 * The columns of the two underlying models may be interleaved in the merged model.
 *
 * @author  kdg
 */
public class MergedTableModel implements TableModel {
    
    private final TableModel firstModel;
    private final TableModel secondModel;
    private final boolean[] useColumnOfFirstModel;
    private final int[] colIndexToBackendIndex;
    private final List listeners = new ArrayList();
    
    /**
     * Creates a new MergedTableModel, using the column distribution as specified by useColumnOfFirstModel.
     * No changes are allowed to the underlying models after construction.
     *
     * @param firstModel the first tablemodel
     * @param secondModel the second tablemodel
     * @param useColumnOfFirstModel on each index where the araay is true, uses a column of the first model.
     *                              on all other locations, used a column of the second model.
     *
     * @pre firstModel != null
     * @pre secondModel != null
     * @pre useColumnOfFirstModel != null
     * @pre firstModel.getRowCount() == secondModel.getRowCount()
     * @pre firstModel.getColumnCount() + secondModel.getColumnCount() == useColumnOfFirstModel.length
     * @pre firstModel.getColumnCount() == the number of values equal to true in useColumnOfFirstModel
     */
    public MergedTableModel(TableModel firstModel, TableModel secondModel, boolean[] useColumnOfFirstModel) {
        this.firstModel = firstModel;
        this.secondModel = secondModel;
        this.useColumnOfFirstModel = useColumnOfFirstModel;
        this.colIndexToBackendIndex = new int[useColumnOfFirstModel.length];
        int firstIndex = 0;
        int secondIndex = 0;
        for (int i = 0; i < useColumnOfFirstModel.length; i++) {
            if (useColumnOfFirstModel[i]) {
                colIndexToBackendIndex[i] = firstIndex++;
            } else {
                colIndexToBackendIndex[i] = secondIndex++;
            }
        }
    }
    
    /**
     * Creates a new MergedTableModel, using the auxiliaryModel's base model as the first model,
     * the auxiliaryModel itself as the second model,
     * and the column distribution as specified by the auxiliaryModel.
     *
     * No changes are allowed to the underlying models after construction.
     *
     * @pre auxiliaryModel != null
     */
    public MergedTableModel(AuxiliaryTableModel auxiliaryModel) {
        this(auxiliaryModel.getBaseModel(), auxiliaryModel, auxiliaryModel.getBaseModelColumnLocations());
    }
    
    /**
     * Convenience constructor that uses an AuxiliaryColumnProvider to add columns to a base model.
     */
    public MergedTableModel(TableModel baseModel, AuxiliaryColumnProvider auxColProvider) {
        this(new AuxiliaryColumnsTableModel(baseModel, auxColProvider));
    }
    
    /**
     * Gets the first or second model, depending on the parameter.
     */
    private TableModel getModel(boolean useFirstModel) {
        if (useFirstModel) {
            return this.firstModel;
        } else {
            return this.secondModel;
        }
    }
    
    protected boolean isColumnOfFirstModel(int col) {
        return useColumnOfFirstModel[col];
    }
    
    /**
     * Gets the model that is used for the given column.
     */
    private TableModel getBackendModelForColumn(int col) {
        return getModel(isColumnOfFirstModel(col));
    }
    
    private int projectColIndexToBackendIndex(int col) {
        return colIndexToBackendIndex[col];
    }
    
    /**
     * No changes allowed, so this method does nothing.
     */
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }
    
    public Class getColumnClass(int columnIndex) {
        int backendIndex = projectColIndexToBackendIndex(columnIndex);
        TableModel backend = getBackendModelForColumn(columnIndex);
        return backend.getColumnClass(backendIndex);
    }
    
    public int getColumnCount() {
        return colIndexToBackendIndex.length;
    }
    
    public String getColumnName(int columnIndex) {
        int backendIndex = projectColIndexToBackendIndex(columnIndex);
        TableModel backend = getBackendModelForColumn(columnIndex);
        return backend.getColumnName(backendIndex);
    }
    
    public int getRowCount() {
        return firstModel.getRowCount();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        int backendIndex = projectColIndexToBackendIndex(columnIndex);
        TableModel backend = getBackendModelForColumn(columnIndex);
        return backend.getValueAt(rowIndex, backendIndex);
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        int backendIndex = projectColIndexToBackendIndex(columnIndex);
        TableModel backend = getBackendModelForColumn(columnIndex);
        return backend.isCellEditable(rowIndex, backendIndex);
    }
    
    /**
     * No changes allowed, so this method does nothing.
     */
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        int backendIndex = projectColIndexToBackendIndex(columnIndex);
        TableModel backend = getBackendModelForColumn(columnIndex);
        backend.setValueAt(aValue, rowIndex, backendIndex);
        fireCellValueChanged(rowIndex, columnIndex);
    }
    
    private void fireCellValueChanged(int row, int column) {
        
        // FIXME
        
        // TODO
        
        // TableSorter currently can't cope with the ChangedEvent...
        
        /*
        TableModelEvent tme = new TableModelEvent(this, row, row, column, TableModelEvent.UPDATE);
        Iterator iterListeners = listeners.iterator();
        while (iterListeners.hasNext()) {
            TableModelListener l = (TableModelListener)iterListeners.next();
            l.tableChanged(tme);
        }
         */
    }
}
