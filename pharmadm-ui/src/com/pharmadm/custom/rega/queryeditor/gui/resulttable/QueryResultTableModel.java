/*
 * JDBCTableModel.java
 *
 * Created on September 8, 2003, 9:41 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.gui.resulttable;

import java.util.*;
import java.sql.*;
import javax.swing.table.AbstractTableModel;

import com.pharmadm.custom.rega.queryeditor.port.QueryResult;

/**
 *
 * @author  kdg
 */
public class QueryResultTableModel extends AbstractTableModel {
    
    private QueryResult resultSet;
    private int rowCount;
    private int colCount;
    private String[] colNames;
    private Class[] colClasses;
    
    /**
     * Creates a new instance of JDBCTableModel
     *
     * @param resultSet a _scrollable_ ResultSet aquired from DatabaseManager
     */
    public QueryResultTableModel(QueryResult resultSet, List<String> columnSelections) throws SQLException {
        this.resultSet = resultSet;
        this.colCount = resultSet.getColumnCount();
        colNames = new String[colCount];
        colClasses = new Class[colCount];
        for (int i = 0; i < colCount; i++) {
            if (columnSelections != null && columnSelections.size() >= colCount) {
                colNames[i] = (String)columnSelections.get(i); //rsmd.getColumnName(i + 1);
            } else {
                colNames[i] = resultSet.getColumnName(i);
            }
            String className = resultSet.getColumnClassName(i);
            try {
                colClasses[i] = Class.forName(className);
            } catch (ClassNotFoundException cnfe) {
                System.err.println("Class not found: " + className);
                colClasses[i] = Object.class;
            }
        }
        
        this.rowCount = resultSet.size();
    }
    
    public Object getValueAt(int row, int column) {
        Object value = null;
    	value = resultSet.get(row, column);
        return value;
    }
    
    public int getColumnCount() {
        return  colCount;
    }
    
    public int getRowCount() {
        return rowCount;
    }
    
    public String getColumnName(int column) {
        return colNames[column];
    }
    
    public Class getColumnClass(int column) {
        return colClasses[column];
    }
    
    public Class getColumnClass(String name) {
        for (int col = 0; col < colClasses.length; col++) {
            if (colNames[col].equals(name)) {
                return getColumnClass(col);
            }
        }
        return null;
    }
    
    public void close() {
    	resultSet.close();
    }
    
}
