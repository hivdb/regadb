/*
 * TableSaverCellRenderer.java
 *
 * Created on March 24, 2004, 4:36 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.file.table;

/**
 * An interface for objects that can render cell values to a string.
 *
 * @author  kdg
 */
public interface TableSaverCellRenderer {
    
    /** Converts the value of the table cell to a String.
     *
     * @param value the value of the table cell (may be null)
     * @param row the row coordinate of the table cell
     * @param col the column coordinate of the table cell
     * @return a String representing the value
     */
    public String render(Object value, int row, int col);
    
}
