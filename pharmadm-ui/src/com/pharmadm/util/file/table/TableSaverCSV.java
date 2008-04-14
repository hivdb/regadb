/*
 * TableSaver.java
 *
 * Created on October 1, 2003, 2:09 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.file.table;

import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.table.*;
import com.pharmadm.util.file.FileUtil;

/**
 * Saves a Jtable's TableModel to a comma separated file (CSV) on disk.
 * WARNING: This class does not produce a generally-accepted-standards-compliant CSV file.
 *
 * @author  kdg
 */
public class TableSaverCSV extends CharacterAbstractTableSaver {
    
    public TableSaverCSV() {
    }
    
    protected void writeHeader(TableModel model, BufferedWriter writer, String encoding) throws IOException {
        boolean firstColumn = true;
        for (int col = 0; col < model.getColumnCount(); col++) {
            if (!firstColumn) {
                writer.write(',');
            } else {
                firstColumn = false;
            }
            final String colName = model.getColumnName(col);
            writer.write(escape(colName, shouldQuote(colName, -1, col)));
        }
        writer.newLine();
    }
    
    protected void writeRow(TableModel model, int row, BufferedWriter writer) throws IOException {
        boolean firstValue = true;
        for (int col = 0; col < model.getColumnCount(); col++) {
            if (!firstValue) {
                writer.write(',');
            } else {
                firstValue = false;
            }
            final Object value = model.getValueAt(row, col);
            String valueString = "";
            TableSaverCellRenderer renderer = getCellRenderer(col);
            if (renderer != null) {
                valueString = renderer.render(value, row, col);
            } else if (value != null) {
                valueString = value.toString();
            }
            writer.write(escape(valueString, shouldQuote(valueString, row, col)));
        }
        writer.newLine();
    }
    
    protected void writeFooter(TableModel model, BufferedWriter writer) throws IOException {
    }
    
    /** This does not behave as generally accepted common practice, although it will
     * suffice for most cases.
     *
     * This default behaviour only uses the value, but subclasses may override this to use
     * the coordinates of the cell too.
     *
     * @param value The string value of the cell
     * @param row The row of the cell, or -1 for the header cell
     * @param col The column of the cell
     * @return If the value should be quoted or not.
     */
    protected boolean shouldQuote(String value, int row, int col) {
        final int valueLength = value.length();
        for (int i = 0; i < valueLength; i++) {
            final char c = value.charAt(i);
            switch (c) {
                case '"' :
                case ' ' :
                case ',' :
                case '\r' :
                case '\n' :
                case '\f' :
                case '\t' : return true;
                default : break;
            }
        }
        return false;
    }
    
    private String escape(final String value, final boolean quote) {
        final StringBuffer sb = new StringBuffer();
        if (quote) {
            sb.append('"');
        }
        final int valueLength = value.length();
        for (int i = 0; i < valueLength; i++) {
            final char c = value.charAt(i);
            switch (c) {
                case '\\' : sb.append("\\\\"); break;
                case '\r' : sb.append("\\r"); break;
                case '\n' : sb.append("\\n"); break;
                case '"'  : sb.append("\"\""); break;
                default   : sb.append(c);
            }
        }
        if (quote) {
            sb.append('"');
        }
        return sb.toString();
    }
    
    protected java.util.Collection getAlternativeDescriptions() {
        return null;
    }
    
    public java.util.Collection getAlternativeExtensions() {
        return null;
    }
    
    protected String getDefaultDescription() {
        return "Spreadsheet (comma separated values)";
    }
    
    public String getDefaultExtension() {
        return "csv";
    }
    
}
