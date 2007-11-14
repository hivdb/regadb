/*
 * ClobModelDecorator.java
 *
 * Created on July 9, 2004, 4:14 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.gui.resulttable;

import java.sql.Clob;
import java.sql.SQLException;
import javax.swing.table.TableModel;

import com.pharmadm.custom.rega.queryeditor.QueryEditorApp;

/**
 * A TableModel decorator that replaces Clobs by Strings, which can
 * be displayed by regular cell renderers.
 * It is preferable over having a renderer do the same conversion,
 * because saving the table to a CSV files does not go through the renderers.
 *
 * @author  kdg
 */
public class ClobModelDecorator implements TableModel {
    
    private final TableModel backend;
    private final boolean[] convertColumn;
    
    ClobModelDecorator(TableModel backendModel) {
        this.backend = backendModel;
        convertColumn = new boolean[backendModel.getColumnCount()];
        for (int colIndex = 0; colIndex < backend.getColumnCount(); colIndex++) {
            convertColumn[colIndex] = isClobColumn(backend, colIndex);
        }
    }
    
    public static boolean hasClobColumns(TableModel model) {
        for (int colIndex = 0; colIndex < model.getColumnCount(); colIndex++) {
            if (isClobColumn(model, colIndex)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isClobColumn(TableModel model, int col) {
        Class colClass = model.getColumnClass(col);
        if (colClass != null) {
            Class[] interfaces = colClass.getInterfaces();
            for (int iface= 0; iface < interfaces.length; iface++) {
                if (interfaces[iface].equals(java.sql.Clob.class)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void addTableModelListener(javax.swing.event.TableModelListener l) {
        backend.addTableModelListener(l);
    }
    
    public Class getColumnClass(int columnIndex) {
        if (convertColumn[columnIndex]) {
            return String.class;
        } else {
            return backend.getColumnClass(columnIndex);
        }
    }
    
    public int getColumnCount() {
        return backend.getColumnCount();
    }
    
    public String getColumnName(int columnIndex) {
        return backend.getColumnName(columnIndex);
    }
    
    public int getRowCount() {
        return backend.getRowCount();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (convertColumn[columnIndex]) {
            Object value = backend.getValueAt(rowIndex, columnIndex);
            Clob clob = (Clob)value;
            try {
                long clobLength = clob.length();
                if (clobLength > Integer.MAX_VALUE) {
                    Exception e = new Exception("Clob too large!\nThe sequence contains more than " + Integer.MAX_VALUE + " characters.");
                    QueryEditorApp.getInstance().showException(e, "Clob too large");
                }
                return clob.getSubString(1, (int)clobLength);
            } catch (SQLException sqle) {
                QueryEditorApp.getInstance().showException(sqle, "Database error while fetching sequence CLOB.");
            }
        }
        return backend.getValueAt(rowIndex, columnIndex);
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return backend.isCellEditable(rowIndex, columnIndex);
    }
    
    public void removeTableModelListener(javax.swing.event.TableModelListener l) {
        backend.removeTableModelListener(l);
    }
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        backend.setValueAt(aValue, rowIndex, columnIndex);
    }
}
