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
package com.pharmadm.custom.rega.reporteditor;

import java.util.*;
import java.sql.*;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author  kdg
 */ 
public class ReportTableModel extends AbstractTableModel {
    
    private Report report;
    private int rowCount;
    private int colCount;
    private String[] colNames;
    private List columnData;
    
    /**
     * Creates a new instance of ReportTableModel
     *
     */
    public ReportTableModel(Report report, List columnData, List columnNames) {
        this.report = report;
        this.columnData = columnData;
        this.colCount = columnData.size();
        this.rowCount = report.getDataSize();
        colNames = new String[colCount];
        for (int i = 0; i < colCount; i++) {
            colNames[i] = (String)columnNames.get(i); 
        }
    }
    
    public Object getValueAt(int row, int column) {
        Object value = null;
        try {
            DataRow dataRow = (DataRow)report.getDataRows().get(row);
            Valuable dataColumn = (Valuable)columnData.get(column);
            return dataColumn.getValue(dataRow);
        } catch (Exception e) {
            e.printStackTrace();
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
    
    public void close() {
        // close all resultsets
    }
    
}
