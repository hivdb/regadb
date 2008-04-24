/*
 * QueryEditorFrame.java
 *
 * Created on August 27, 2003, 7:31 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor.gui;

import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.event.*;

import com.pharmadm.custom.rega.reporteditor.*;
import com.pharmadm.custom.rega.gui.BusyTableModel;
import com.pharmadm.custom.rega.gui.FileExtensionFilter;
import com.pharmadm.custom.rega.gui.JTableExporter;
import com.pharmadm.custom.rega.queryeditor.gui.*;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.VisualizationComponentFactory;
import com.pharmadm.custom.rega.queryeditor.FrontEndManager;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.savable.*;
import com.pharmadm.util.settings.RegaSettings;
import com.pharmadm.util.work.Work;
import com.pharmadm.util.work.WorkManager;

/**
 *
 * @author  kristof
 */
public class ReportBuilderFrame extends javax.swing.JFrame {
    
    private QueryContext master; // link to the query context used for seeding object list variables
    private QueryOutputReportSeeder seedController;
    private String busyString;
    private boolean running = false;
    private ReportBuilderRunnable runningExecution;
    
    private ReportBuilder reportBuilder;
    private ReportFormatEditor reportFormatModel;
    private DataGroup cursorGroup = null;
    private File currentReportFile = null;
    private JProgressBar progressBar;
    private JFileChooser fc1 = new javax.swing.JFileChooser();
    private JTable resultTable;
    
    private WorkManager workManager;
    
    /** Creates new form ReportBuilderFrame */
    public ReportBuilderFrame(ReportBuilder reportBuilder, QueryContext master) {
        this.master = master;
        this.reportBuilder = reportBuilder;
        initComponents();
        initWorkManagerAndPanel();
        initResultTable();
        initSeeder();
        initReportFormatList();
        fc1.removeChoosableFileFilter(fc1.getAcceptAllFileFilter());
        fc1.addChoosableFileFilter(new FileExtensionFilter(new String[] {"rfmt"}, "Report Format Files"));
        // load the bounds from the settings
        setBounds(RegaSettings.getInstance().getReportBuilderFrameBounds());
    }
    
    public int getSelectedIndex() {
        int index = reportFormatList.getSelectedIndex();
        if (index < 0) {
            index = reportFormatList.getModel().getSize() - 1;
        }
        return index;
    }
    
    public void showBusy(boolean busy) {
        if (busy) {
            editMenu.setEnabled(false);
            boolean noProgressBar = (progressBar == null);
            if (noProgressBar) {
                progressBar = new JProgressBar();
            }
            progressBar.setString(busyString);
            progressBar.setStringPainted(true);
            progressBar.setIndeterminate(true);
            if (noProgressBar) {
                getContentPane().add(progressBar, java.awt.BorderLayout.SOUTH);
            }
            pack();
        } else {
            if (progressBar != null) {
                getContentPane().remove(progressBar);
                pack();
                editMenu.setEnabled(true);
                progressBar = null;
            }
        }
    }
    
    public void setBusyString(String busyString) {
        this.busyString = busyString;
        if (progressBar != null) {
            progressBar.setString(busyString);
        }
    }
    
    private void setRunning(boolean running) {
        this.running = running;
        if (running) {
            runButton.setText("Generating report...");
            runButton.setEnabled(false);
        } else {
            runButton.setText("Run");
            runButton.setMnemonic('r');
            runButton.setEnabled(true);
        }
    }
    
    private boolean askOverwritePermission(File f) {
        if (f.exists()) {
            int option = JOptionPane.showConfirmDialog(null, "This file already exists. Do you want to overwrite the file?\nAny current contents of the file will be lost.", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.YES_OPTION) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isOkToLooseData() {
        boolean okToLooseData = false;
        okToLooseData = okToLooseData || (reportFormatModel.getSize() == 1);
        okToLooseData = okToLooseData || (!reportFormatModel.isDirty());
        okToLooseData = okToLooseData || (JOptionPane.showConfirmDialog(this, "Warning : your current report will be lost. Proceed anyway?", "Are you sure ?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
        return okToLooseData;
    }
    
    private void initWorkManagerAndPanel() {
        JPanel threadsPanel = new JPanel();
        workManager = new WorkManager(threadsPanel);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelBottom.add(threadsPanel, gridBagConstraints);
    }
    
    protected WorkManager getWorkManager() {
        return workManager;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPaneList = new javax.swing.JScrollPane();
        reportFormatList = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jScrollPaneSelect = new javax.swing.JScrollPane();
        jSelectPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanelTable = new javax.swing.JPanel();
        jScrollPaneTable = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        runButton = new javax.swing.JButton();
        numberRowsLabel = new javax.swing.JLabel();
        jPanelBottom = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        closeMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        modifyMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        addMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        deleteMenuItem = new javax.swing.JMenuItem();
        tableMenu = new javax.swing.JMenu();
        saveTableMenuItem = new javax.swing.JMenuItem();
        printTableMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Report Builder");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(600, 600));
        jSplitPane2.setDividerLocation(200);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setPreferredSize(new java.awt.Dimension(300, 600));
        jScrollPaneList.setViewportView(reportFormatList);

        jSplitPane2.setTopComponent(jScrollPaneList);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jSelectPanel.setLayout(new javax.swing.BoxLayout(jSelectPanel, javax.swing.BoxLayout.Y_AXIS));

        jScrollPaneSelect.setViewportView(jSelectPanel);

        jPanel2.add(jScrollPaneSelect, java.awt.BorderLayout.CENTER);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setLabelFor(jScrollPaneSelect);
        jLabel1.setText("Select Properties to Report");
        jPanel2.add(jLabel1, java.awt.BorderLayout.NORTH);

        jSplitPane2.setBottomComponent(jPanel2);

        jSplitPane1.setLeftComponent(jSplitPane2);

        jPanelTable.setLayout(new java.awt.BorderLayout());

        jScrollPaneTable.setPreferredSize(new java.awt.Dimension(300, 600));
        jPanelTable.add(jScrollPaneTable, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.GridLayout(1, 2, 5, 0));

        runButton.setMnemonic('r');
        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        jPanel3.add(runButton);

        numberRowsLabel.setText("Rows: ");
        jPanel3.add(numberRowsLabel);

        jPanelTable.add(jPanel3, java.awt.BorderLayout.NORTH);

        jPanelBottom.setLayout(new java.awt.GridBagLayout());

        jPanelBottom.setMinimumSize(new java.awt.Dimension(9, 10));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jLabel2.setMaximumSize(new java.awt.Dimension(5, 100));
        jLabel2.setMinimumSize(new java.awt.Dimension(5, 16));
        jLabel2.setPreferredSize(new java.awt.Dimension(5, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanelBottom.add(jLabel2, gridBagConstraints);

        jPanelTable.add(jPanelBottom, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setRightComponent(jPanelTable);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");
        newMenuItem.setMnemonic('n');
        newMenuItem.setText("New");
        newMenuItem.setToolTipText("Create an empty query");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(newMenuItem);

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Open\u2026");
        openMenuItem.setToolTipText("Load a query from disk");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(openMenuItem);

        fileMenu.add(jSeparator4);

        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Save");
        saveMenuItem.setToolTipText("Save the query to disk");
        saveMenuItem.setEnabled(false);
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText("Save As\u2026");
        saveAsMenuItem.setToolTipText("Save the entire query to a file on disk");
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(saveAsMenuItem);

        fileMenu.add(jSeparator3);

        closeMenuItem.setMnemonic('c');
        closeMenuItem.setText("Close");
        closeMenuItem.setToolTipText("Close the report window");
        closeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(closeMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit");
        modifyMenuItem.setMnemonic('m');
        modifyMenuItem.setText("Modify\u2026");
        modifyMenuItem.setToolTipText("Change the selected data group part");
        modifyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(modifyMenuItem);

        editMenu.add(jSeparator5);

        addMenuItem.setMnemonic('a');
        addMenuItem.setText("Add data group");
        addMenuItem.setToolTipText("Add a data group");
        addMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(addMenuItem);

        editMenu.add(jSeparator1);

        cutMenuItem.setMnemonic('u');
        cutMenuItem.setText("Cut");
        cutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(cutMenuItem);

        copyMenuItem.setMnemonic('c');
        copyMenuItem.setText("Copy");
        copyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(copyMenuItem);

        pasteMenuItem.setMnemonic('p');
        pasteMenuItem.setText("Paste");
        pasteMenuItem.setEnabled(false);
        pasteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(pasteMenuItem);

        editMenu.add(jSeparator2);

        deleteMenuItem.setMnemonic('d');
        deleteMenuItem.setText("Delete");
        deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(deleteMenuItem);

        menuBar.add(editMenu);

        tableMenu.setMnemonic('t');
        tableMenu.setText("Table");
        tableMenu.setEnabled(false);
        saveTableMenuItem.setMnemonic('a');
        saveTableMenuItem.setText("Save As\u2026");
        saveTableMenuItem.setToolTipText("Saves the current table to a CSV or FASTA file");
        saveTableMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveTableMenuItemActionPerformed(evt);
            }
        });

        tableMenu.add(saveTableMenuItem);

        printTableMenuItem.setMnemonic('p');
        printTableMenuItem.setText("Print\u2026");
        printTableMenuItem.setToolTipText("Prints the current table");
        printTableMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printTableMenuItemActionPerformed(evt);
            }
        });

        tableMenu.add(printTableMenuItem);

        menuBar.add(tableMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");
        contentsMenuItem.setMnemonic('c');
        contentsMenuItem.setText("Contents");
        contentsMenuItem.setEnabled(false);
        contentsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentsMenuItemActionPerformed(evt);
            }
        });

        helpMenu.add(contentsMenuItem);

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });

        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents
    
    private void saveTableMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveTableMenuItemActionPerformed
        if (resultTable != null) {
            JTableExporter tableExporter = new JTableExporter();
            tableExporter.export(resultTable);
        }
    }//GEN-LAST:event_saveTableMenuItemActionPerformed
    
    private void printTableMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printTableMenuItemActionPerformed
        if (resultTable != null) {
            com.pharmadm.util.gui.table.TablePrinter.printTable(resultTable);
        }
    }//GEN-LAST:event_printTableMenuItemActionPerformed
    
    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        if (running) {
            setRunning(false);
            if (runningExecution != null) {
                runningExecution.cancel();
            } else {
                JOptionPane.showMessageDialog(this, "Could not stop report building because there was no report being built.", "Cancel report failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Report report = getReportAndInformUserOnError();
            if (report != null) {
                TableModel currentTableModel = resultTable.getModel();
                numberRowsLabel.setText("Busy...");
                resultTable.setModel(new BusyTableModel("Busy...", "Busy calculating query..."));
                
                if ((currentTableModel != null) && (currentTableModel instanceof ReportTableModel)) {
                    ((ReportTableModel)currentTableModel).close();
                }
                setRunning(true);
                try {
                    runningExecution = new ReportBuilderRunnable(report, seedController);
                    Thread executeQuery = new Thread(runningExecution, "Generate Summary Report");
                    executeQuery.start();
                } catch (Exception e) {
                    resultTable.setModel(new BusyTableModel("Error", "Failure while building report"));
                    FrontEndManager.getInstance().getFrontEnd().showException(e, "Exception");
                }
            }
        }
    }//GEN-LAST:event_runButtonActionPerformed
    
    private Report getReportAndInformUserOnError() {
        Report report = reportBuilder.getReport();
        if (!report.getFormat().isValid()) {
            JOptionPane.showMessageDialog(this, "The report format is not valid.\nPlease assign a valid value to all parameters.", "Invalid report format", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        if (!((DataSelectionList)report.getFormat().getSelectionList()).isAnythingSelected()) {
            JOptionPane.showMessageDialog(this, "No output data are selected.", "Empty selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return report;
    }
    
    private void modifyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyMenuItemActionPerformed
        try {
            int index = getSelectedIndex();
            DataGroup currentGroup = (DataGroup)reportFormatModel.getElementAt(index);
            if (currentGroup == null) {
                return;
            }
            editDataGroup(currentGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_modifyMenuItemActionPerformed
    
    private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuItemActionPerformed
        if (isOkToLooseData()) {
            reportBuilder.createNewReport();
            linkToQuery(reportBuilder.getReport().getFormat());
            currentReportFile = null;
            reportFormatList.setModel(reportFormatModel);
            //reportFormatList.setSelectedIndex(-1); // not needed, happens in setModel
            updateEditMode();
        }
    }//GEN-LAST:event_newMenuItemActionPerformed
    
    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed
        saveAs();
    }//GEN-LAST:event_saveAsMenuItemActionPerformed
    
    /**
     * @return true iff save as successful.
     */
    private boolean saveAs() {
        boolean ok = false;
        try {
            int returnVal = fc1.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                if (! fc1.getFileFilter().accept(fc1.getSelectedFile())) {
                    fc1.setSelectedFile(new java.io.File(fc1.getSelectedFile().getCanonicalPath() + "." + ((FileExtensionFilter)fc1.getFileFilter()).getExtensions()[0]));
                }
                File oldReportFile = currentReportFile;
                currentReportFile = fc1.getSelectedFile();
                if (askOverwritePermission(currentReportFile)) {
                    reportFormatModel.saveReportFormat(currentReportFile);
                    ok = true;
                } else {
                    currentReportFile = oldReportFile;
                }
            }
        } catch (java.io.FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
        return ok;
    }
    
    /**
     * @return true iff save as successful.
     */
    private boolean save() {
        boolean ok = false;
        if (currentReportFile != null) {
            try {
                reportFormatModel.saveReportFormat(currentReportFile);
                ok = true;
            } catch (java.io.FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
        } else {
            ok = saveAs();
        }
        return ok;
    }
    
    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        save();
    }//GEN-LAST:event_saveMenuItemActionPerformed
    
    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        if (isOkToLooseData()) {
            try {
                int returnVal = fc1.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    reportFormatModel.loadReportFormat(fc1.getSelectedFile());
                    ReportFormat reportFormat = reportFormatModel.getReportFormat();
                    reportBuilder.getReport().setFormat(reportFormat);
                    linkToQuery(reportFormat);
                    //reportFormat.getUniqueNameContext().assignUniqueNamesToAll(reportFormat); // don't : this happens in loadReportFormat
                    currentReportFile = fc1.getSelectedFile();
                    reportFormatList.setModel(reportFormatModel);
                    //reportFormatList.setSelectedIndex(-1); // not needed, happens in setModel
                    updateEditMode();
                }
            } catch (java.io.FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
        }
    }//GEN-LAST:event_openMenuItemActionPerformed
    
    private void deleteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteMenuItemActionPerformed
        try {
            int index = getSelectedIndex();
            DataGroup currentGroup = (DataGroup)reportFormatModel.getElementAt(index);
            if (currentGroup == null) {
                return;
            }
            reportFormatModel.removeDataGroup(currentGroup, index);
            reportFormatList.setSelectedIndex(-1);
            updateEditMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_deleteMenuItemActionPerformed
    
    private void pasteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteMenuItemActionPerformed
        if (cursorGroup != null) {
            try {
                int index = getSelectedIndex();
                reportFormatModel.addDataGroup(((DataGroup)cursorGroup.clone()), index);
                reportFormatList.setSelectedIndex(-1);
                updateEditMode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_pasteMenuItemActionPerformed
    
    private void copyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyMenuItemActionPerformed
        try {
            int index = getSelectedIndex();
            DataGroup currentGroup = (DataGroup)reportFormatModel.getElementAt(index);
            if (currentGroup == null) {
                return;
            }
            cursorGroup = currentGroup;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_copyMenuItemActionPerformed
    
    private void cutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutMenuItemActionPerformed
        try {
            int index = getSelectedIndex();
            DataGroup currentGroup = (DataGroup)reportFormatModel.getElementAt(index);
            if (currentGroup == null) {
                return;
            }
            reportFormatModel.removeDataGroup(currentGroup, index);
            cursorGroup = currentGroup;
            reportFormatList.setSelectedIndex(-1);
            updateEditMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_cutMenuItemActionPerformed
    
    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
//        master.showAboutBox();
    }//GEN-LAST:event_aboutMenuItemActionPerformed
    
    private void contentsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentsMenuItemActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_contentsMenuItemActionPerformed
    
    private void addMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMenuItemActionPerformed
        try {
            int index = getSelectedIndex();
            Collection prototypeList = reportFormatModel.getAvailableDataGroupPrototypes(index);
            if (seedController != null) {
                prototypeList = seedController.extractSeedablePrototypes(prototypeList);
            }
            if (prototypeList.size() == 0) {
                JOptionPane.showMessageDialog(this, "No query variables are available to build data groups.", "No data grouo seeds", JOptionPane.INFORMATION_MESSAGE);
            } else {
                DataGroupSelectionDialog selectionDialog = new DataGroupSelectionDialog(this, (DataGroup)reportFormatModel.getElementAt(index), prototypeList, seedController, true);
                selectionDialog.show();
                DataGroup newGroup = selectionDialog.getSelectedGroup();
                if (newGroup != null) {
                    reportFormatModel.addDataGroup(newGroup, index);
                    reportFormatList.setSelectedIndex(-1);
                    updateEditMode();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_addMenuItemActionPerformed
    
    private void initResultTable() {
        resultTable = new JTable();
        resultTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPaneTable.setViewportView(resultTable);
    }
    
    private void initSeeder() {
        this.seedController = new QueryOutputReportSeeder(reportBuilder, master.getEditorModel());
        master.getEditorModel().addTreeModelListener(new MyTreeModelListener());
    }
    
    private void initReportFormatList() {
        reportFormatModel = reportBuilder.getFormatEditor();
        reportFormatList.getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        reportFormatList.addListSelectionListener(new MyListSelectionListener());
        reportFormatList.addMouseListener(new MyMouseListener());
        reportFormatList.setModel(reportFormatModel);
        linkToQuery(reportFormatModel.getReportFormat());
        reportFormatList.setCellRenderer(new ReportListCellRenderer(seedController));
        updateEditMode();
        //reportFormatModel.addListDataListener(new MyListDataListener());
        reportFormatModel.addDirtinessListener(new DirtinessListener() {
            public void dirtinessChanged(DirtinessEvent de) {
                saveMenuItem.setEnabled(de.getSavable().isDirty());
            }
        });
        installSelectPanel();
    }
    
    private void installSelectPanel() {
        jSelectPanel = new DataSelectPanel(reportFormatModel, seedController);
        jScrollPaneSelect.setViewportView(jSelectPanel);
    }
    
    
    private void linkToQuery(ReportFormat reportFormat) {
        reportFormat.setUniqueNameContext(master.getEditorModel().getQuery().getUniqueNameContext());
    }
    
    private void showListPopupMenu(Point p) {
        JPopupMenu borrowedMenu = editMenu.getPopupMenu();
        borrowedMenu.show(reportFormatList,p.x+10,p.y); // apparently this sets the invoker of the menu to reportFormatList (YUCK !)
        borrowedMenu.setInvoker(editMenu);   // SO give it back to its rightful owner now
    }
    
    private class MyListSelectionListener implements javax.swing.event.ListSelectionListener {
        
        public void valueChanged(javax.swing.event.ListSelectionEvent e) {
            updateEditMode();
        }
    }
    
    private class MyMouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent evt) {
            int index = reportFormatList.locationToIndex(evt.getPoint());
            if (index >= 0) {
                reportFormatList.setSelectedIndex(index);
                if (index != reportFormatList.getModel().getSize() - 1) {
                    DataGroup currentGroup = (DataGroup)reportFormatModel.getElementAt(index);
                    if ((evt.getClickCount() > 1) && (evt.getModifiers() & java.awt.event.InputEvent.BUTTON1_MASK) != 0) {
                        editDataGroup(currentGroup);
                    }
                }
            }
        }
        
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showListPopupMenu(e.getPoint());
            }
        }
        
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showListPopupMenu(e.getPoint());
            }
        }
    }
    
    private void editDataGroup(DataGroup currentGroup) {
        DataGroupEditor groupEditor = new DataGroupEditor();
        groupEditor.setDataGroup(currentGroup);
        groupEditor.setVisualizationComponentFactory(new VisualizationComponentFactory(groupEditor, seedController));
        new DataGroupEditorDialog(this, groupEditor , true).show();
    }
    
    private void updateEditMode() {
        int index = getSelectedIndex();
        //System.err.println("Selected index = " + index);
        boolean touch = (index != reportFormatList.getModel().getSize() - 1);
        modifyMenuItem.setEnabled(touch);
        copyMenuItem.setEnabled(touch);
        cutMenuItem.setEnabled(touch);
        deleteMenuItem.setEnabled(touch);
        pasteMenuItem.setEnabled(cursorGroup != null);
    }
    
    private void closeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMenuItemActionPerformed
        tryExitForm();
    }//GEN-LAST:event_closeMenuItemActionPerformed
    
    /** Exit the Frame */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        tryExitForm();
    }//GEN-LAST:event_exitForm
    
    
    public boolean tryExitForm() {
        boolean okToLooseData = false;
        okToLooseData = okToLooseData || (reportFormatModel.getSize() == 1);
        okToLooseData = okToLooseData || (!reportFormatModel.isDirty());
        if (!okToLooseData) {
            final String optionDiscard = "Discard";
            final String optionSave = "Save";
            final String optionCancel = "Cancel";
            Object[] options = {optionDiscard, optionSave, optionCancel};
            int option = JOptionPane.showOptionDialog(this, "The report has been changed.\nDo you want to save the changes?",
            "Close report",  JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, optionSave);
            if (option < 0 || option >= options.length) {
                System.err.println("Dialog box returned unexpected option!");
                return false;
            } else if (options[option].equals(optionCancel)) {
                return false;
            } else if (options[option].equals(optionSave)) {
                if (!save()) {
                    return false;
                }
            }
        }
        // store the bounds ...
        RegaSettings.getInstance().setReportBuilderFrameBounds(ReportBuilderFrame.this.getBounds());
        // and save all settings to file
        RegaSettings.getInstance().save();
        dispose();
        return true;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem addMenuItem;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelBottom;
    private javax.swing.JPanel jPanelTable;
    private javax.swing.JScrollPane jScrollPaneList;
    private javax.swing.JScrollPane jScrollPaneSelect;
    private javax.swing.JScrollPane jScrollPaneTable;
    private javax.swing.JPanel jSelectPanel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem modifyMenuItem;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JLabel numberRowsLabel;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JMenuItem printTableMenuItem;
    private javax.swing.JList reportFormatList;
    private javax.swing.JButton runButton;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenuItem saveTableMenuItem;
    private javax.swing.JMenu tableMenu;
    // End of variables declaration//GEN-END:variables
    
    private class MyTreeModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
            reportBuilder.getReport().reset();
            ReportBuilderFrame.this.seedController = new QueryOutputReportSeeder(reportBuilder, master.getEditorModel());
            reportFormatList.setCellRenderer(new ReportListCellRenderer(seedController));
            ((DataSelectPanel)jSelectPanel).setSeedController(seedController);
            reportFormatModel.updateSelectionList();
        }
        public void treeNodesInserted(final TreeModelEvent e) {
            reportBuilder.getReport().reset();
            ReportBuilderFrame.this.seedController = new QueryOutputReportSeeder(reportBuilder, master.getEditorModel());
            reportFormatList.setCellRenderer(new ReportListCellRenderer(seedController));
            ((DataSelectPanel)jSelectPanel).setSeedController(seedController);
            reportFormatModel.updateSelectionList();
        }
        public void treeNodesRemoved(TreeModelEvent e) {
            reportBuilder.getReport().reset();
            ReportBuilderFrame.this.seedController = new QueryOutputReportSeeder(reportBuilder, master.getEditorModel());
            reportFormatList.setCellRenderer(new ReportListCellRenderer(seedController));
            ((DataSelectPanel)jSelectPanel).setSeedController(seedController);
            reportFormatModel.updateSelectionList();
        }
        public void treeStructureChanged(TreeModelEvent e) {
            reportBuilder.getReport().reset();
            ReportBuilderFrame.this.seedController = new QueryOutputReportSeeder(reportBuilder, master.getEditorModel());
            reportFormatList.setCellRenderer(new ReportListCellRenderer(seedController));
            ((DataSelectPanel)jSelectPanel).setSeedController(seedController);
            reportFormatModel.updateSelectionList();
        }
    }
    
    private class ReportBuilderRunnable implements Runnable {
        
        private final QueryOutputReportSeeder seedController;
        private final Report report;
        private boolean canceled = false;
        
        public ReportBuilderRunnable(Report report, QueryOutputReportSeeder seedController) {
            this.report = report;
            this.seedController = seedController;
        }
        
        public void run() {
            try {
                if (!report.isSeeded()) {
                    if (! seedController.areSeedsChosen()) {
                        new QueryOutputReportSeederDialog(ReportBuilderFrame.this, seedController, true).show();
                    }
                    if (! seedController.areSeedsChosen()) {
                        JOptionPane.showMessageDialog(ReportBuilderFrame.this, "The report has insufficient input data.\nPlease assign a value to all inputs.", "Missing input for report", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
                Iterator prepWorksIter = seedController.getQuerier().getQuery().getPreparationWorks().iterator();
                if (prepWorksIter.hasNext()) {
                    final String regularText = runButton.getText();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            runButton.setEnabled(false);  //disable cancel
                            runButton.setText("Preparing query...");
                        }
                    });
                    while (prepWorksIter.hasNext() && !canceled) {
                        Work aPrepWork = (Work)prepWorksIter.next();
                        FrontEndManager.getInstance().getFrontEnd().getWorkManager().executeAndWait(aPrepWork);
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            runButton.setEnabled(true);  //enable cancel
                            runButton.setText(regularText);
                        }
                    });
                }
                
                Work deployResultsWork = seedController.createDeployResultsWork();
                getWorkManager().executeAndWait(deployResultsWork);
                
                if (!report.isSeeded()) {
                    System.err.println("Report has not been seeded.");
                    cancel();
                    return;
                }
                
                report.calculateRows();
                DataSelectionList selectionList = report.getFormat().getSelectionList();
                final ReportTableModel model = new ReportTableModel(report, selectionList.getSelectedColumns(), selectionList.getSelectedColumnNames(seedController));
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (!canceled) {
                            resultTable.setModel(model);
                            tableMenu.setEnabled(true);
                            numberRowsLabel.setText("Rows: " +model.getRowCount());
                            setRunning(false);
                        }
                    }
                });
            } catch (final Exception e) {
                if (!canceled) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            numberRowsLabel.setText("Report Building failed");
                            resultTable.setModel(new BusyTableModel("Error", "Failure while building the report"));
                            FrontEndManager.getInstance().getFrontEnd().showException(e, "Exception");
                        }
                    });
                }
            }
        }
        
        public void cancel() {
            try {
                canceled = true;
                setRunning(false);
                numberRowsLabel.setText("Canceled");
                resultTable.setModel(new BusyTableModel("Canceled", "The report was canceled."));
            } catch (Exception e) {
            	FrontEndManager.getInstance().getFrontEnd().showException(e, "Could not cancel the active report");
            }
        }
    }
}
