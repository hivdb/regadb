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
 * Saves a Jtable's TableModel to a HTML file on disk.
 *
 * WARNING: Character escaping only covers markup characters.
 * I18N, math, Greek, and ISO 8859-1 (Latin-1) characters are not escaped.
 * (Might not be a problem on most browsers in most cases.)
 *
 * @author  kdg
 */
public class TableSaverHTML extends CharacterAbstractTableSaver {
    
    private String title;
    private boolean escapeColumnNames = true;
    
    public TableSaverHTML() {
    }
    
    protected void writeHeader(TableModel model, BufferedWriter writer, String encoding) throws IOException {
        writer.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        writer.newLine();
        writer.write("<html>");
        writer.newLine();
        writer.write("<head>");
        writer.newLine();
        writer.write("   <meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + encoding + "\">");
        writer.newLine();
        writer.write("   <title>");
        if (title != null) {
            writer.write(escape(title));
        }
        writer.write("</title>");
        writer.newLine();
        writer.write("   <meta name=\"GENERATOR\" content=\"PharmaDM table exporter 1.0\">");
        writer.newLine();
        writer.write("</head>");
        writer.newLine();
        writer.write("<body>");
        writer.write("<table>");
        writer.newLine();
        writer.write("<tr>");
        for (int col = 0; col < model.getColumnCount(); col++) {
            final String colName = model.getColumnName(col);
            writer.write("<th>");
            if (isEscapeColumnNames()) {
                writer.write(escape(colName));
            } else {
                writer.write(colName);
            }
            writer.write("</th>");
        }
        writer.write("</tr>");
        writer.newLine();
    }
    
    protected void writeRow(TableModel model, int row, BufferedWriter writer) throws IOException {
        writer.write("<tr>");
        for (int col = 0; col < model.getColumnCount(); col++) {
            final Object value = model.getValueAt(row, col);
            String valueString = "";
            TableSaverCellRenderer renderer = getCellRenderer(col);
            if (renderer != null) {
                valueString = renderer.render(value, row, col);
            } else if (value != null) {
                valueString = value.toString();
            }
            writer.write("<td>");
            writer.write(escape(valueString));
            writer.write("</td>");
        }
        writer.write("</tr>");
        writer.newLine();
    }
    
    protected void writeFooter(TableModel model, BufferedWriter writer) throws IOException {
        writer.write("</table></body></html>");
        writer.newLine();
    }
    
    /**
     * Escapes special characters in a String.
     * This implementation ONLY escapes markup characters.
     * I18N, math, Greek, and ISO 8859-1 (Latin-1) characters are not escaped.
     */
    protected String escape(final String value) {
        final StringBuffer sb = new StringBuffer((int)(value.length() * 1.1));
        final int valueLength = value.length();
        for (int i = 0; i < valueLength; i++) {
            final char c = value.charAt(i);
            switch (c) {
                case '<' : sb.append("&lt;"); break;
                case '>' : sb.append("&gt;"); break;
                case '&' : sb.append("&amp;"); break;
                case '"' : sb.append("&quot;"); break;
                default   : sb.append(c);
            }
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
        return "Hypertext markup language (HTML)";
    }
    
    public String getDefaultExtension() {
        return "html";
    }
    
    /** Getter for property title.
     * @return Value of property title.
     *
     */
    public java.lang.String getTitle() {
        return title;
    }
    
    /** Setter for property title.
     * @param title New value of property title.
     *
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }
    
    /** Getter for property escapeColumnNames.
     * @return Value of property escapeColumnNames.
     *
     */
    public boolean isEscapeColumnNames() {
        return escapeColumnNames;
    }
    
    /** Setter for property escapeColumnNames.
     * @param escapeColumnNames New value of property escapeColumnNames.
     *
     */
    public void setEscapeColumnNames(boolean escapeColumnNames) {
        this.escapeColumnNames = escapeColumnNames;
    }
}
