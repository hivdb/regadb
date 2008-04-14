/*
 * JTableExporter.java
 *
 * Created on November 17, 2003, 4:36 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.gui;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;

import com.pharmadm.util.file.FileUtil;
import com.pharmadm.util.file.table.TableSaverCSV;
import com.pharmadm.util.file.table.TableSaverExcelXML;
import com.pharmadm.custom.rega.queryeditor.fastaexport.FastaExporter;

/**
 * A utility class to export a TableModel to a file, according to the user's specification.
 *
 * @author  kdg
 */
public class JTableExporter {
    
    /** Creates a new instance of JTableExporter */
    public JTableExporter() {
    }
    
    public void export(JTable table) {
        
        TableSaverExcelXML excelXMLSaver = new TableSaverExcelXML();
        FileFilter excelXMLFilter = excelXMLSaver.getDefaultFileFilter();

        TableSaverCSV cSVSaver = new TableSaverCSV();
        FileFilter cSVFilter = cSVSaver.getDefaultFileFilter();
        
        FastaExporter fastaExporter = new FastaExporter();
        FileFilter fastaFilter = fastaExporter.createFilefilter();
        
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(cSVFilter);
        fc.addChoosableFileFilter(excelXMLFilter);
        if (fastaExporter.hasSequenceColumns(table.getModel())) {
            fc.addChoosableFileFilter(fastaFilter);
        }
        
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            final FileFilter fileFilterUsed = fc.getFileFilter();
            if (fileFilterUsed == excelXMLFilter) {
                file = FileUtil.forceExtension(file, ".xml");
                if (!askOverWritePermission(file)) {
                    return;
                }
                excelXMLSaver.saveUnconditional(table.getModel(), file);
            } else  if (fileFilterUsed == cSVFilter) {
                file = FileUtil.forceExtension(file, ".csv");
                if (!askOverWritePermission(file)) {
                    return;
                }
                cSVSaver.saveUnconditional(table.getModel(), file);
            } else if (fileFilterUsed == fastaFilter) {
                file = FileUtil.forceExtension(file, ".fasta");
                if (!askOverWritePermission(file)) {
                    return;
                }
                fastaExporter.startWizard(table, file);
            }
        }
    }
    
    private boolean askOverWritePermission(File file) {
        if (file.exists()) {
            int option = JOptionPane.showConfirmDialog(null, "This file already exists. Do you want to overwrite the file?\nAny current contents of the file will be lost.", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.YES_OPTION) {
                return false;
            }
        }
        return true;
    }
    
}
