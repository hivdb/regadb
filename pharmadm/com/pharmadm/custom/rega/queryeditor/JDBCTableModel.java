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
package com.pharmadm.custom.rega.queryeditor;

import java.util.*;
import java.sql.*;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author  kdg
 */
public class JDBCTableModel extends AbstractTableModel {
    
    private ResultSet resultSet;
    private int rowCount;
    private int colCount;
    private String[] colNames;
    private Class[] colClasses;
    
    /**
     * Creates a new instance of JDBCTableModel
     *
     * @param resultSet a _scrollable_ ResultSet aquired from JDBCManager
     */
    public JDBCTableModel(ResultSet resultSet, List columnSelections) throws SQLException {
        this.resultSet = resultSet;
        ResultSetMetaData rsmd = resultSet.getMetaData();
        this.colCount = rsmd.getColumnCount();
        colNames = new String[colCount];
        colClasses = new Class[colCount];
        for (int i = 0; i < colCount; i++) {
            if (columnSelections != null) {
                colNames[i] = (String)columnSelections.get(i); //rsmd.getColumnName(i + 1);
            } else {
                colNames[i] = rsmd.getColumnName(i + 1);
            }
            String className = rsmd.getColumnClassName(i + 1);
            try {
                colClasses[i] = Class.forName(className);
            } catch (ClassNotFoundException cnfe) {
                System.err.println("Class not found: " + className);
                colClasses[i] = Object.class;
            }
        }
        
        resultSet.last();
        this.rowCount = (resultSet.last() ? resultSet.getRow() : 0);
    }
    
    public Object getValueAt(int row, int column) {
        Object value = null;
        try {
            resultSet.absolute(row + 1);
            value = resultSet.getObject(column + 1);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
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
        JDBCManager.getInstance().closeStatement(resultSet);
    }
    
}
