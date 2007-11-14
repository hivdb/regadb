/*
 * SpreadsheetTools.java
 *
 * Created on April 6, 2004, 2:38 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.file.table;

/**
 * Static tools for generating spreadsheet formulas.
 *
 * @author  kdg
 */
public class SpreadsheetTools {
    
    /** Creates a new instance of SpreadsheetTools */
    private SpreadsheetTools() {
    }
    
    /**
     * Translates the column index into a spreadsheet-style column name (A, B, C, ...).
     *
     * @param colIndex the index of the column (first column is 0)
     */
    public static String getColumnName(int colIndex) {
        String name = "";
        for (; colIndex >= 0; colIndex = (colIndex/26) -1) {
            name =(char)((char)(colIndex%26) + 'A') + name;
        }
        return name;
    }
    
    /**
     * Generates a spreadsheet-style list or range of equidistant cells.
     * The cells must be either on a single row or in a single column.
     *
     * @param horizontal true if applied to cells on a single row, false if the cells are in a single column.
     * @param rowOfFirstCell the row of the first relevant cell (uppermost row is 0)
     * @param colOfFirstCell the column of the first relevant cell (leftmost column is 0)
     * @param period the number of irrelevant cells between each relevant cell, plus one.
     *                  A period of one indicates contiguous cells.
     * @param nbCells the number of relevant cells in the range
     *
     * @pre rowOfFirstCell >= 0
     * @pre colOfFirstCell >= 0
     * @pre period >= 1
     *
     * @return a String with a spreadsheet-style formatted list of cells (or a range if the period is 1)
     */
    public static String generateInterleavedCellList(boolean horizontal, int rowOfFirstCell, int colOfFirstCell, int period, int nbCells) {
        StringBuffer expression = new StringBuffer();
        if (nbCells == 1) {
            expression.append(SpreadsheetTools.getColumnName(colOfFirstCell));
            expression.append(rowOfFirstCell+1);
        } else if (nbCells > 1) {
            if (horizontal) {
                if (period == 1) {
                    expression.append(SpreadsheetTools.getColumnName(colOfFirstCell));
                    expression.append(rowOfFirstCell+1);  // formula's must take account of spreadsheet programs counting rows from 1, not 0.
                    expression.append(':');
                    expression.append(SpreadsheetTools.getColumnName(colOfFirstCell + (period * (nbCells-1))));
                    expression.append(rowOfFirstCell+1);
                } else {
                    for (int i = 0; i < nbCells; i++) {
                        expression.append(SpreadsheetTools.getColumnName(colOfFirstCell + (period * i)));
                        expression.append(rowOfFirstCell+1);  // formula's must take account of spreadsheet programs counting rows from 1, not 0.
                        if ((i+1) < nbCells) {
                            expression.append(',');
                        }
                    }
                }
            } else {
                String colName = SpreadsheetTools.getColumnName(colOfFirstCell);
                if (period == 1) {
                    expression.append(colName);
                    expression.append(rowOfFirstCell + 1);  // formula's must take account of spreadsheet programs counting rows from 1, not 0.
                    expression.append(':');
                    expression.append(colName);
                    expression.append(rowOfFirstCell + 1 + (period * (nbCells - 1)));  // formula's must take account of spreadsheet programs counting rows from 1, not 0.
                } else {
                    for (int i = 0; i < nbCells; i++) {
                        expression.append(colName);
                        expression.append(rowOfFirstCell + 1 + (period * i));  // formula's must take account of spreadsheet programs counting rows from 1, not 0.
                        if ((i+1) < nbCells) {
                            expression.append(',');
                        }
                    }
                }
            }
        }
        return expression.toString();
    }
    
    public static String getRelativeAddress(int relRow, int relCol) {
        return "R["+relRow+"]C["+relCol+"]";
    }
}
