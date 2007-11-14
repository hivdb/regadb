/*
 * ColumnInfo.java
 *
 * Created on November 20, 2003, 11:16 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.fastaexport;

/**
 * A class for representing columns in a JList.
 *
 * @author  kdg
 */
public class ColumnInfo {
    
    private int columnId;
    private String columnName;
    
    public ColumnInfo(int id, String name) {
        this.columnId = id;
        this.columnName = name;
    }
    
    public int getColumnId() {
        return columnId;
    }
    
    public String toString() {
        return columnName;
    }
}
