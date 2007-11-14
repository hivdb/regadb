/*
 * BusyTableModel.java
 *
 * Created on September 8, 2003, 11:16 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.gui;

import javax.swing.table.*;

/**
 *
 * @author  kdg
 */
public class BusyTableModel extends AbstractTableModel {
    
    String title;
    String message;
    
    /** Creates a new instance of BusyTableModel */
    public BusyTableModel(String title, String message) {
        this.title = title;
        this.message = message;
    }
    
    public int getColumnCount() {
        return 1;
    }
    
    public int getRowCount() {
        return 1;
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        return message;
    }
    
    public String getColumnName(int columnIndex) {
        return title;
    }
    
}
