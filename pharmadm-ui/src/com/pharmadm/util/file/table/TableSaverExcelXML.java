/*
 * TableSaverExcelXML.java
 *
 * Created on November 3, 2004
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.file.table;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.table.*;

import com.pharmadm.util.file.FileUtil;

/**
 * Saves a Jtable's TableModel to a Microsoft Excel 2002 XML file on disk.
 *
 * WARNING: These XML files cannot be opened in Excel 2000 or earlier.
 * They can, generally, be opened in Excel 2002 (Office XP) and saved to
 * standard Excel Workbook format, which can be opened in earlier Excel versions.
 *
 * WARNING: Character escaping only covers markup characters.
 * I18N, math, Greek, and ISO 8859-1 (Latin-1) characters are not escaped.
 *
 * @author  kdg
 */
public class TableSaverExcelXML extends CharacterAbstractTableSaver {
    
    private String title;
    private boolean escapeColumnNames = true;
    
    // Example date: 2004-11-03T15:34:01Z
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    public TableSaverExcelXML() {
    }
    
    protected void writeHeader(TableModel model, BufferedWriter writer, String encoding) throws IOException {
        // Not all of the tags are really required:
        // Excel 2002 will still be able to read the file if they are not present.
        // However, they are still present here to match an Excel file as closely as possible.
        
        String userName = System.getProperty("user.name");
        
        writer.write("<?xml version=\"1.0\"?>");
        writer.newLine();
        writer.write("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"");
        writer.newLine();
        writer.write(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"");
        writer.newLine();
        writer.write(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"");
        writer.newLine();
        writer.write(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"");
        writer.newLine();
        writer.write(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">");
        writer.newLine();
        writer.write(" <DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\">");
        writer.newLine();
        if (title != null) {
            writer.write("  <Title>" + escape(title) + "</Title>");
            writer.newLine();
        }
        writer.write("  <Author>" + escape(userName) + " (using PharmaDM table exporter)</Author>");
        writer.newLine();
        writer.write("  <LastAuthor>" + escape(userName) + "</LastAuthor>");
        writer.newLine();
        writer.write("  <Created>" + dateFormat.format(new java.util.Date()) + "</Created>"); //Example: 2004-11-03T15:34:01Z
        writer.newLine();
        writer.write("  <Version>10.6714</Version>");
        writer.newLine();
        writer.write(" </DocumentProperties>");
        writer.newLine();
        writer.write(" <OfficeDocumentSettings xmlns=\"urn:schemas-microsoft-com:office:office\">");
        writer.newLine();
        writer.write("  <DownloadComponents/>");
        writer.newLine();
        writer.write("  <LocationOfComponents HRef=\"/\"/>");
        writer.newLine();
        writer.write(" </OfficeDocumentSettings>");
        writer.newLine();
        writer.write(" <ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">");
        writer.newLine();
        writer.write("  <WindowHeight>8700</WindowHeight>");
        writer.newLine();
        writer.write("  <WindowWidth>11355</WindowWidth>");
        writer.newLine();
        writer.write("  <WindowTopX>240</WindowTopX>");
        writer.newLine();
        writer.write("  <WindowTopY>105</WindowTopY>");
        writer.newLine();
        writer.write("  <ProtectStructure>False</ProtectStructure>");
        writer.newLine();
        writer.write("  <ProtectWindows>False</ProtectWindows>");
        writer.newLine();
        writer.write(" </ExcelWorkbook>");
        writer.newLine();
        writer.write(" <Styles>");
        writer.newLine();
        writer.write("  <Style ss:ID=\"Default\" ss:Name=\"Normal\">");
        writer.newLine();
        writer.write("   <Alignment ss:Vertical=\"Bottom\"/>");
        writer.newLine();
        writer.write("   <Borders/>");
        writer.newLine();
        writer.write("   <Font/>");
        writer.newLine();
        writer.write("   <Interior/>");
        writer.newLine();
        writer.write("   <NumberFormat/>");
        writer.newLine();
        writer.write("   <Protection/>");
        writer.newLine();
        writer.write("  </Style>");
        writer.newLine();
        writer.write("  <Style ss:ID=\"s21\">");
        writer.newLine();
        writer.write("   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/>");
        writer.newLine();
        writer.write("   <Borders/>");
        writer.newLine();
        writer.write("   <Font ss:Color=\"#FFFFFF\" ss:Bold=\"1\"/>");
        writer.newLine();
        writer.write("   <Interior ss:Color=\"#008000\" ss:Pattern=\"Gray75\" ss:PatternColor=\"#008080\"/>");
        writer.newLine();
        writer.write("  </Style>");
        writer.newLine();
        writer.write("  <Style ss:ID=\"s22\">");
        writer.newLine();
        writer.write("   <Alignment ss:Vertical=\"Bottom\"/>");
        writer.newLine();
        writer.write("   <Borders/>");
        writer.newLine();
        writer.write("   <Font ss:Color=\"#000000\"/>");
        writer.newLine();
        writer.write("   <Interior ss:Color=\"#CCFFCC\" ss:Pattern=\"Solid\"/>");
        writer.newLine();
        writer.write("  </Style>");
        writer.newLine();
        writer.write(" </Styles>");
        writer.newLine();
        writer.write(" <Worksheet ss:Name=\"Sheet1\">");
        writer.newLine();
        writer.write("  <Table ss:ExpandedColumnCount=\"" + model.getColumnCount() + "\" ss:ExpandedRowCount=\"" + (model.getRowCount()+1) + "\" x:FullColumns=\"1\"");  // Include header row
        writer.newLine();
        writer.write("   x:FullRows=\"1\">");
        writer.newLine();
        for (int col = 0; col < model.getColumnCount(); col++) {
            writer.write("   <Column ss:AutoFitWidth=\"1\"/>");
            writer.newLine();
        }
        writer.write("   <Row>");
        writer.newLine();
        for (int col = 0; col < model.getColumnCount(); col++) {
            final String colName = model.getColumnName(col);
            writer.write("    <Cell ss:StyleID=\"s21\"><Data ss:Type=\"String\">");
            if (isEscapeColumnNames()) {
                writer.write(escape(colName));
            } else {
                writer.write(colName);
            }
            writer.write("</Data></Cell>");
            writer.newLine();
        }
        writer.write("   </Row>");
        writer.newLine();
    }
    
    protected void writeRow(TableModel model, int row, BufferedWriter writer) throws IOException {
        boolean skipped = false;
        writer.write("   <Row>");
        writer.newLine();
        for (int col = 0; col < model.getColumnCount(); col++) {
            final Object value = model.getValueAt(row, col);
            if (value == null) {
                skipped = true;
            } else {
                writer.write("    <Cell");
                if (skipped) {
                    writer.write(" ss:Index=\"");
                    writer.write(""+(col+1));
                    writer.write("\"");
                    skipped = false;
                }
                writer.write(" ss:StyleID=\"s22\"");
                if (value instanceof SpreadsheetFormula) {
                    SpreadsheetFormula formula = (SpreadsheetFormula)value;
                    if (formula.isArrayFormula()) {
                        writer.write(" ss:ArrayRange=\"RC\"");
                    }
                    writer.write(" ss:Formula=\"");
                    writer.write(escape(formula.toString()));
                    writer.write("\"/>");
                } else {
                    String type = ((value instanceof Number)? "Number" : "String");
                    String valueString = "";
                    TableSaverCellRenderer renderer = getCellRenderer(col);
                    if (renderer != null) {
                        valueString = renderer.render(value, row, col);
                    } else if (value != null) {
                        valueString = value.toString();
                    }
                    writer.write("><Data ss:Type=\"");
                    writer.write(type);
                    writer.write("\">");
                    writer.write(escape(valueString));
                    writer.write("</Data></Cell>");
                }
                writer.newLine();
            }
        }
        writer.write("   </Row>");
        writer.newLine();
    }
    
    protected void writeFooter(TableModel model, BufferedWriter writer) throws IOException {
        writer.write("  </Table>");
        writer.newLine();
        writer.write("  <WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\">");
        writer.newLine();
        writer.write("   <Selected/>");
        writer.newLine();
        writer.write("   <Panes>");
        writer.newLine();
        writer.write("    <Pane>");
        writer.newLine();
        writer.write("     <Number>3</Number>");
        writer.newLine();
        writer.write("     <ActiveCol>1</ActiveCol>");
        writer.newLine();
        writer.write("    </Pane>");
        writer.newLine();
        writer.write("   </Panes>");
        writer.newLine();
        writer.write("   <ProtectObjects>False</ProtectObjects>");
        writer.newLine();
        writer.write("   <ProtectScenarios>False</ProtectScenarios>");
        writer.newLine();
        writer.write("  </WorksheetOptions>");
        writer.newLine();
        writer.write(" </Worksheet>");
        writer.newLine();
        writer.write("</Workbook>");
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
        return "Excel Spreadsheet XML (XML)";
    }
    
    public String getDefaultExtension() {
        return "xml";
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
