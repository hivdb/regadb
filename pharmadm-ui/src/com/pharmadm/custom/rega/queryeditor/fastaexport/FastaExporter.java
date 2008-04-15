/*
 * FastaExporter.java
 *
 * Created on November 17, 2003, 4:50 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.fastaexport;

import java.io.*;
import java.util.Collection;
import java.util.regex.*;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ProgressMonitor;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;

import com.pharmadm.util.gui.wizard.*;

/**
 * A class to export tablemodels containing at least one sequence column
 * to fasta-formatted files.
 *
 * @author  kdg
 */
public class FastaExporter {
    
    private SelectSequenceStep seqStep;
    private SelectDescriptionComponentsStep descCompsStep;
    private ConfigureFormatStep configFormatStep;
    
    private JTable table;
    private File file;
    private int sequenceColumn = -1;
    private int[] descriptionColumnIds = new int[0];
    private boolean limitDescriptionLength = true;
    private int descriptionLength = 80;
    private Character separatorChar = new Character(' ');
    private boolean lineWrap = true;
    private boolean stripAlignments = true;
    private final Pattern alignmentStripper = Pattern.compile("[\\.\\-\\s]");
    private final Pattern whitespaceStripper = Pattern.compile("[\\s]");
    
    /** Creates a new instance of FastaExporter */
    public FastaExporter() {
    }
    
    public javax.swing.filechooser.FileFilter createFilefilter() {
        return new com.pharmadm.util.file.BasicFileFilter("fasta", "Fasta (saves sequence only)");
    }
    
    public boolean hasSequenceColumns(TableModel model) {
        for (int colIndex = 0; colIndex < model.getColumnCount(); colIndex++) {
            if (isSequenceColumn(model, colIndex)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isSequenceColumn(TableModel model, int column) {
        return model.getColumnName(column).endsWith("_SEQUENCE");
    }
    
    /**
     * Invokes the Fasta export wizard.
     */
    public void startWizard(JTable table, File file) {
        this.table = table;
        this.file = file;
        Wizard wizard = new Wizard(null, true);
        wizard.setTitle("Fasta export wizard");
        seqStep = new SelectSequenceStep(this, table.getModel());
        descCompsStep = new SelectDescriptionComponentsStep(this, table.getModel());
        configFormatStep = new ConfigureFormatStep(this);
        wizard.addStep(seqStep);
        wizard.addStep(descCompsStep);
        wizard.addStep(configFormatStep);
        wizard.setSize(534 + 200, 507);  // 200 px wider than normal
        wizard.show();
    }
    
    protected void writeFastaToFile() {
        final ProgressMonitor progressMonitor = new ProgressMonitor(null, "Saving the sequences to file", null, 0, table.getRowCount()-1);
        new Thread() {
            public void run() {
                try {
                    Writer writer = new BufferedWriter(new FileWriter(file));
                    writeFasta(writer, Long.MAX_VALUE, progressMonitor);
                } catch (IOException ioe) {
                    progressMonitor.close();
                    JOptionPane.showMessageDialog(null, "Error while exporting fasta to file:\n"+ioe.getMessage(), "I/O error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.start();
    }
    
    protected void setSequenceColumn(int col) {
        this.sequenceColumn = col;
    }
    
    protected void setDescriptionColumnIds(int[] columnIds) {
        this.descriptionColumnIds = columnIds;
    }
    
    protected void setLimitDescriptionLength(boolean limit) {
        this.limitDescriptionLength = limit;
    }
    
    protected void setDescriptionLength(int length) {
        this.descriptionLength = length;
    }
    
    protected void setSeparatorChar(Character c) {
        this.separatorChar = c;
    }
    
    protected void setLineWrapEnabled(boolean enable) {
        this.lineWrap = enable;
    }
    
    protected void setStripAlignmentEnabled(boolean enable) {
        stripAlignments = enable;
    }
    
    /**
     * Writes the fasta information.
     * At most linesLimit lines are written.
     * The writer gets closed at the end.
     */
    protected void writeFasta(Writer writer, long linesLimit, ProgressMonitor progressMonitor) {
        final PrintWriter printer = new PrintWriter(writer);
        if (sequenceColumn != -1) {
            TableModel model = table.getModel();
            long linesWritten = 0;
            final char[] cBuff = new char[80];
            for (int row = 0; row < model.getRowCount() && (linesWritten < linesLimit); row++) {
                StringBuffer descriptionLine = new StringBuffer();
                descriptionLine.append('>');
                int[] dCols = descriptionColumnIds;
                for (int i = 0; ((i < dCols.length) && (!limitDescriptionLength || (descriptionLine.length() < descriptionLength))); i++) {
                    Object value = model.getValueAt(row, dCols[i]);
                    descriptionLine.append(value);
                    if ((separatorChar != null) && ((i+1) < dCols.length)) {
                        descriptionLine.append(separatorChar);
                    }
                }
                if (limitDescriptionLength && (descriptionLine.length() > descriptionLength)) {
                    descriptionLine.setLength(descriptionLength);
                }
                printer.println(descriptionLine.toString());
                linesWritten++;
                if (linesWritten < linesLimit) {
                    Object seqValue = model.getValueAt(row, sequenceColumn);
                    String seqString = seqValue.toString();
                    if (stripAlignments) {
                        Matcher matcher = alignmentStripper.matcher(seqString);
                        seqString = matcher.replaceAll("");
                    } else {
                        Matcher matcher = whitespaceStripper.matcher(seqString);
                        seqString = matcher.replaceAll("");
                    }
                    for (int c = 0; (c < seqString.length()) && (linesWritten < linesLimit); c += 80) {
                        int charsToWrite = Math.min(80, (seqString.length() - c));
                        seqString.getChars(c, c + charsToWrite, cBuff, 0);
                        printer.write(cBuff, 0, charsToWrite);
                        if (lineWrap) {
                            printer.println();
                            linesWritten++;
                        }
                    }
                    if (! lineWrap) {
                        printer.println();
                        linesWritten++;
                    }
                }
                if (progressMonitor != null) {
                    progressMonitor.setProgress(row);
                    if (progressMonitor.isCanceled()) {
                        printer.flush();
                        printer.close();
                        JOptionPane.showMessageDialog(null, "Not all sequences were saved. The fasta file is incomplete.", "File save did not complete", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
        }
        printer.flush();
        printer.close();
    }
}
