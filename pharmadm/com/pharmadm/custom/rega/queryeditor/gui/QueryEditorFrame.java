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
package com.pharmadm.custom.rega.queryeditor.gui;

import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.event.*;
import javax.swing.tree.*;

import com.pharmadm.util.gui.mdi.*;
import com.pharmadm.custom.rega.queryeditor.*;
import com.pharmadm.custom.rega.queryeditor.gui.resulttable.QueryResultJTable;
import com.pharmadm.custom.rega.savable.*;
//import com.pharmadm.custom.rega.datamining.SeparateActivesFromInactives;
import com.pharmadm.custom.rega.reporteditor.ReportBuilder;
import com.pharmadm.custom.rega.reporteditor.gui.ReportBuilderFrame;
//import com.pharmadm.dmax.cube.CubeActivationDispatcher;
//import com.pharmadm.dmax.cube.RankDialog;
import com.pharmadm.util.work.Work;


/**
 *
 * @author  kristof, kdg
 */
public class QueryEditorFrame extends javax.swing.JFrame {
    
    private String busyString;
    private JProgressBar progressBar;
    private volatile boolean running = false;
    private ExecuteQueryRunnable runningExecution;
    
    private QueryEditor editorModel;
    private VisualizationComponentFactory testFactory;
    private WhereClause cursorClause = null;
    private File currentQueryFile = null;
    
    private List openReportFrames = new ArrayList();
    
    private QueryResultJTable resultTable;
    private JFileChooser fc1 = new JFileChooser();
    private JFileChooser fc2 = new JFileChooser();
    private JCheckBoxMenuItem toggleShowingMoleculesJCheckBoxMenuItem;
    private JCheckBoxMenuItem toggleSupervisorJCheckBoxMenuItem;
    
//    private CubeActivationDispatcher cubeActivationDispatcher = new CubeActivationDispatcher();
//    private CubeExportDialog jDialogCubeExport;
//    private RankDialog jDialogRank;
    
    private MRUDocumentsList mRUQueries = new MRUDocumentsList("queries", RegaSettings.getInstance().getRecentQueriesSetting(), new DocumentLoader() {
        public void loadDocument(File file) {
            if (file != null && file.exists()) {
                if (isOkToLooseQuery()) {
                    try {
                        editorModel.loadXMLQuery(file);
                        currentQueryFile = file;
                    } catch (java.io.FileNotFoundException fnfe) {
                        fnfe.printStackTrace();
                    }
                }
            }
        }
    });
    
//    private static final Image LOGO_IMG  = com.pharmadm.util.resource.DMaxMedia.getInstance().getImage("logo24.gif");
    
    /** Creates new form QueryEditorFrame */
    public QueryEditorFrame(QueryEditor editorModel) {
        this.editorModel = editorModel;
        initComponents();
        initThreadsPanel();
        initResultTable();
        initQueryTree();
        initRecentMenus();
        fc1.removeChoosableFileFilter(fc1.getAcceptAllFileFilter());
        fc2.removeChoosableFileFilter(fc2.getAcceptAllFileFilter());
        fc1.addChoosableFileFilter(new FileExtensionFilter(new String[] {"query"}, "Query Files"));
        fc2.addChoosableFileFilter(new FileExtensionFilter(new String[] {"qpart"}, "Query Component Files"));
        // load the bounds from the settings
        setBounds(RegaSettings.getInstance().getQueryEditorFrameBounds());
//       setIconImage(LOGO_IMG);
        editorModel.addDirtinessListener(new DirtinessListener() {
            public void dirtinessChanged(DirtinessEvent de) {
                saveMenuItem.setEnabled(de.getSavable().isDirty());
            }
        });
        
//        this.jDialogCubeExport = new CubeExportDialog(QueryEditorFrame.this, "Export cube",false,this.cubeActivationDispatcher);
//        this.jDialogRank = new RankDialog(QueryEditorFrame.this, "Rank subgroups",false);
    }
    
    public QueryEditor getEditorModel() {
        return editorModel;
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
            runButton.setText("Cancel");
            runButton.setMnemonic('c');
        } else {
            runButton.setText("Run");
            runButton.setMnemonic('r');
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
    
    private void initThreadsPanel() {
        JPanel threadsPanel = QueryEditorApp.getInstance().getThreadsPanel();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelBottom.add(threadsPanel, gridBagConstraints);
    }
    
    private void initRecentMenus() {
        int pos=0;
        while ((pos < fileMenu.getItemCount()) && (fileMenu.getItem(pos)!=openMenuItem))  {
            pos++;
        }
        fileMenu.add(mRUQueries.getMenu(), pos+1);
    }
    
    private boolean isOkToLooseQuery() {
        boolean okToLooseQuery = false;
        okToLooseQuery = okToLooseQuery || (editorModel.getRootClause().getChildCount() == 0);
        okToLooseQuery = okToLooseQuery || (!editorModel.isDirty());
        okToLooseQuery = okToLooseQuery || (JOptionPane.showConfirmDialog(this, "Warning : your current query will be lost. Proceed anyway?", "Are you sure ?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
        return okToLooseQuery;
    }
    
    public void openDocumentAtStartup(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                editorModel.loadXMLQuery(file);
                currentQueryFile = file;
                runButtonActionPerformed(null);
            } catch (java.io.IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    
    public void showDialog(JDialog jDialog, boolean b) {
        if (b) {
            addDialog(jDialog);
        } else {
            jDialog.setVisible(false);
        }
    }
    
    private void addDialog(JDialog dialog) {
        //dialog.setLocationRelativeTo(QueryEditorFrame.this);
        dialog.setLocationRelativeTo(null);
        dialog.validate();
        dialog.pack();
        dialog.setVisible(true);
    }
    
//    public CubeActivationDispatcher getCubeActivationDispatcher() {
//        return this.cubeActivationDispatcher;
//    }
//    
//    public CubeExportDialog getCubeExportDialog() {
//        return this.jDialogCubeExport;
//    }
//    
//    public RankDialog getRankDialog() {
//        return this.jDialogRank;
//    }
    
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
        jScrollPaneTree = new javax.swing.JScrollPane();
        queryTree = new javax.swing.JTree();
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
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        saveSubqueryMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        impMolsMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        editDrugStocksMenuItem = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        modifyMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        addMenu = new javax.swing.JMenu();
        addAtomicMenuItem = new javax.swing.JMenuItem();
        addFromFileMenuItem = new javax.swing.JMenuItem();
        addAndMenuItem = new javax.swing.JMenuItem();
        addOrMenuItem = new javax.swing.JMenuItem();
        addNotMenuItem = new javax.swing.JMenuItem();
        wrapMenu = new javax.swing.JMenu();
        wrapAndMenuItem = new javax.swing.JMenuItem();
        wrapOrMenuItem = new javax.swing.JMenuItem();
        wrapNotMenuItem = new javax.swing.JMenuItem();
        unwrapMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        deleteMenuItem = new javax.swing.JMenuItem();
        tableMenu = new javax.swing.JMenu();
        saveTableMenuItem = new javax.swing.JMenuItem();
        printTableMenuItem = new javax.swing.JMenuItem();
        reportMenu = new javax.swing.JMenu();
        foldResistanceMenuItem = new javax.swing.JMenuItem();
        reportEditorMenuItem = new javax.swing.JMenuItem();
        moleculeReportMenuItem = new javax.swing.JMenuItem();
        dataMiningMenu = new javax.swing.JMenu();
        markActivesMenuItem = new javax.swing.JMenuItem();
        forgetActivityMenuItem = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        generateModelMenuItem = new javax.swing.JMenuItem();
        generateModelActivesMenuItem = new javax.swing.JMenuItem();
        openCubeMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        sqlViewMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(com.pharmadm.custom.rega.queryeditor.QueryEditorApp.getInstance().getProduct());
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
        jScrollPaneTree.setViewportView(queryTree);

        jSplitPane2.setTopComponent(jScrollPaneTree);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jSelectPanel.setLayout(new javax.swing.BoxLayout(jSelectPanel, javax.swing.BoxLayout.Y_AXIS));

        jScrollPaneSelect.setViewportView(jSelectPanel);

        jPanel2.add(jScrollPaneSelect, java.awt.BorderLayout.CENTER);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setLabelFor(jScrollPaneSelect);
        jLabel1.setText("Select Fields");
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

        saveSubqueryMenuItem.setMnemonic('q');
        saveSubqueryMenuItem.setText("Save SubQuery");
        saveSubqueryMenuItem.setToolTipText("Save the currently selected query node");
        saveSubqueryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSubqueryMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(saveSubqueryMenuItem);

        fileMenu.add(jSeparator3);

        impMolsMenuItem.setMnemonic('i');
        impMolsMenuItem.setText("Import molfiles\u2026");
        impMolsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                impMolsMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(impMolsMenuItem);

        fileMenu.add(jSeparator6);

        editDrugStocksMenuItem.setMnemonic('d');
        editDrugStocksMenuItem.setText("Drug stocks\u2026");
        editDrugStocksMenuItem.setToolTipText("Edit the drug stock data");
        editDrugStocksMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editDrugStocksMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(editDrugStocksMenuItem);

        fileMenu.add(jSeparator8);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.setToolTipText("Exit the application");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit");
        modifyMenuItem.setMnemonic('m');
        modifyMenuItem.setText("Modify\u2026");
        modifyMenuItem.setToolTipText("Change the selected query part");
        modifyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(modifyMenuItem);

        editMenu.add(jSeparator5);

        addMenu.setMnemonic('a');
        addMenu.setText("Add");
        addAtomicMenuItem.setMnemonic('s');
        addAtomicMenuItem.setText("Simple clause\u2026");
        addAtomicMenuItem.setToolTipText("Add a simple query clause");
        addAtomicMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAtomicMenuItemActionPerformed(evt);
            }
        });

        addMenu.add(addAtomicMenuItem);

        addFromFileMenuItem.setMnemonic('f');
        addFromFileMenuItem.setText("From File\u2026");
        addFromFileMenuItem.setToolTipText("Add a subquery as specified in a file on disk");
        addFromFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFromFileMenuItemActionPerformed(evt);
            }
        });

        addMenu.add(addFromFileMenuItem);

        addAndMenuItem.setIcon(new javax.swing.ImageIcon(""));
        addAndMenuItem.setMnemonic('a');
        addAndMenuItem.setText("AND");
        addAndMenuItem.setToolTipText("Add an AND node to the selected node");
        addAndMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAndMenuItemActionPerformed(evt);
            }
        });

        addMenu.add(addAndMenuItem);

        addOrMenuItem.setMnemonic('o');
        addOrMenuItem.setText("OR");
        addOrMenuItem.setToolTipText("Add an (inclusive) OR node to the selected node");
        addOrMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOrMenuItemActionPerformed(evt);
            }
        });

        addMenu.add(addOrMenuItem);

        addNotMenuItem.setMnemonic('n');
        addNotMenuItem.setText("NOT");
        addNotMenuItem.setToolTipText("Add a NOT node to the selected node");
        addNotMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNotMenuItemActionPerformed(evt);
            }
        });

        addMenu.add(addNotMenuItem);

        editMenu.add(addMenu);

        wrapMenu.setMnemonic('w');
        wrapMenu.setText("Wrap");
        wrapMenu.setEnabled(false);
        wrapAndMenuItem.setText("AND");
        wrapAndMenuItem.setToolTipText("Wrap an AND node around the selected subquery");
        wrapAndMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wrapAndMenuItemActionPerformed(evt);
            }
        });

        wrapMenu.add(wrapAndMenuItem);

        wrapOrMenuItem.setText("OR");
        wrapOrMenuItem.setToolTipText("Wraps an (inclusive) OR node around the selected subquery");
        wrapOrMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wrapOrMenuItemActionPerformed(evt);
            }
        });

        wrapMenu.add(wrapOrMenuItem);

        wrapNotMenuItem.setText("NOT");
        wrapNotMenuItem.setToolTipText("Wraps a NOT node around the selected subquery");
        wrapNotMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wrapNotMenuItemActionPerformed(evt);
            }
        });

        wrapMenu.add(wrapNotMenuItem);

        editMenu.add(wrapMenu);

        unwrapMenuItem.setMnemonic('d');
        unwrapMenuItem.setText("Unwrap");
        unwrapMenuItem.setEnabled(false);
        unwrapMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unwrapMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(unwrapMenuItem);

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

        reportMenu.setMnemonic('r');
        reportMenu.setText("Report");
        foldResistanceMenuItem.setMnemonic('f');
        foldResistanceMenuItem.setText("Fold resistance\u2026");
        foldResistanceMenuItem.setToolTipText("Start the Fold Resistance wizard");
        foldResistanceMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                foldResistanceMenuItemActionPerformed(evt);
            }
        });

        reportMenu.add(foldResistanceMenuItem);

        reportEditorMenuItem.setMnemonic('q');
        reportEditorMenuItem.setText("Query Report\u2026");
        reportEditorMenuItem.setToolTipText("Open a Report Editor window");
        reportEditorMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportEditorMenuItemActionPerformed(evt);
            }
        });

        reportMenu.add(reportEditorMenuItem);

        moleculeReportMenuItem.setMnemonic('m');
        moleculeReportMenuItem.setText("Molecule Report/Chart\u2026");
        moleculeReportMenuItem.setToolTipText("Generate a report or chart about the molecules");
        moleculeReportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moleculeReportMenuItemActionPerformed(evt);
            }
        });

        reportMenu.add(moleculeReportMenuItem);

        menuBar.add(reportMenu);

        dataMiningMenu.setMnemonic('d');
        dataMiningMenu.setText("Data mining");
        markActivesMenuItem.setMnemonic('e');
        markActivesMenuItem.setText("Mark active/inactive by EC50/CC50\u2026");
        markActivesMenuItem.setToolTipText("Set activity and inactivity using measured values for EC50 and CC50 (applies to current table)");
        markActivesMenuItem.setEnabled(false);
        markActivesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                markActivesMenuItemActionPerformed(evt);
            }
        });

        dataMiningMenu.add(markActivesMenuItem);

        forgetActivityMenuItem.setMnemonic('f');
        forgetActivityMenuItem.setText("Forget all");
        forgetActivityMenuItem.setToolTipText("Clears all active/inactive marks");
        forgetActivityMenuItem.setEnabled(false);
        forgetActivityMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forgetActivityMenuItemActionPerformed(evt);
            }
        });

        dataMiningMenu.add(forgetActivityMenuItem);

        dataMiningMenu.add(jSeparator7);

        generateModelMenuItem.setMnemonic('g');
        generateModelMenuItem.setText("Generate rules for activity/inactivity\u2026");
        generateModelMenuItem.setToolTipText("Generate rules to discriminate between active and inactive molecules");
        generateModelMenuItem.setEnabled(false);
        generateModelMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateModelMenuItemActionPerformed(evt);
            }
        });

        dataMiningMenu.add(generateModelMenuItem);

        generateModelActivesMenuItem.setMnemonic('a');
        generateModelActivesMenuItem.setText("Generate rules for activity only\u2026");
        generateModelActivesMenuItem.setToolTipText("Generate descriptive properties of active molecules");
        generateModelActivesMenuItem.setDisplayedMnemonicIndex(19);
        generateModelActivesMenuItem.setEnabled(false);
        generateModelActivesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateModelActivesMenuItemActionPerformed(evt);
            }
        });

        dataMiningMenu.add(generateModelActivesMenuItem);

        openCubeMenuItem.setMnemonic('c');
        openCubeMenuItem.setText("Open cube...");
        openCubeMenuItem.setToolTipText("Open a model in cube format.");
        openCubeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openCubeMenuItemActionPerformed(evt);
            }
        });

        dataMiningMenu.add(openCubeMenuItem);

        menuBar.add(dataMiningMenu);

        viewMenu.setMnemonic('v');
        viewMenu.setText("View");
        sqlViewMenuItem.setMnemonic('s');
        sqlViewMenuItem.setText("SQL\u2026");
        sqlViewMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sqlViewMenuItemActionPerformed(evt);
            }
        });

        viewMenu.add(sqlViewMenuItem);

        menuBar.add(viewMenu);

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
    
    private void forgetActivityMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forgetActivityMenuItemActionPerformed
//        com.pharmadm.custom.rega.datamining.WritableCompoundActivityMap supervisorMap = QueryEditorApp.getInstance().getDataMiningExperiment().getSupervisorMap();
//        int forgetSize = supervisorMap.getKnownActivities().size();
//        if (forgetSize == 0) {
//            JOptionPane.showMessageDialog(this, "There is nothing to forget.", "Nothing to forget", JOptionPane.INFORMATION_MESSAGE);
//        } else {
//            int option = JOptionPane.showConfirmDialog(null, "All " + forgetSize + " active/inactive marks will be cleared.\nAll molecules will be set to 'Ignore'.", "Clear data mining session", JOptionPane.OK_CANCEL_OPTION);
//            if (option == JOptionPane.YES_OPTION) {
//                supervisorMap.clear();
//                resultTable.repaint();
//            }
//        }
    }//GEN-LAST:event_forgetActivityMenuItemActionPerformed
    
    private void markActivesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markActivesMenuItemActionPerformed
//        List molIds = resultTable.listFirstMoleculeUniqueIds();
//        if (molIds != null) {
//            if (molIds.size() == 0) {
//                JOptionPane.showMessageDialog(this, "The query result set contains zero molecules.", "No molecules", JOptionPane.INFORMATION_MESSAGE);
//            } else {
//                QueryEditorApp.getInstance().getWorkManager().execute(new com.pharmadm.custom.rega.datamining.ec50activitysupervisor.ShowSupervisorPanelWork(this, molIds));
//            }
//        } else {
//            JOptionPane.showMessageDialog(this, "There is no molecule column in the current result table.\nFirst run a query that returns molecules in its result table.", "No molecule column", JOptionPane.INFORMATION_MESSAGE);
//        }
    }//GEN-LAST:event_markActivesMenuItemActionPerformed
    
    private void generateModelActivesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateModelActivesMenuItemActionPerformed
//        generateSeparateModel(true);
    }//GEN-LAST:event_generateModelActivesMenuItemActionPerformed
    
    private void openCubeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openCubeMenuItemActionPerformed
//        new OpenCubeDialog(QueryEditorFrame.this, "Open cube",false).show();
    }//GEN-LAST:event_openCubeMenuItemActionPerformed
    
    private void generateModelMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateModelMenuItemActionPerformed
//        generateSeparateModel(false);
    }//GEN-LAST:event_generateModelMenuItemActionPerformed
    
//    private void generateSeparateModel(final boolean bestClassActives) {
//        new Thread(new Runnable() {
//            public void run() {
//                QueryEditorApp.getInstance().getDataMiningExperiment().doExperiment(new SeparateActivesFromInactives(bestClassActives));
//            }
//        }).start();
//    }
    
    private void editDrugStocksMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editDrugStocksMenuItemActionPerformed
//        List molIds = resultTable.listFirstMoleculeUniqueIds();
//        HibernateViroDB.startDrugStockEditor(molIds);
    }//GEN-LAST:event_editDrugStocksMenuItemActionPerformed
    
    private void impMolsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_impMolsMenuItemActionPerformed
//        new com.pharmadm.custom.rega.chem.dbload.wizard.MoleculeLoaderWizard(QueryEditorApp.getInstance().getWorkManager(), this);
    }//GEN-LAST:event_impMolsMenuItemActionPerformed
    
    private void moleculeReportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moleculeReportMenuItemActionPerformed
//        List molIds = resultTable.listFirstMoleculeUniqueIds();
//        if (molIds != null) {
//            if (molIds.size() == 0) {
//                JOptionPane.showMessageDialog(this, "There are no molecules to include in the report.", "No molecules", JOptionPane.INFORMATION_MESSAGE);
//            } else {
//                new com.pharmadm.custom.rega.ec50report.wizard.EC50Wizard(molIds, QueryEditorApp.getInstance().getWorkManager());
//            }
//        } else {
//            JOptionPane.showMessageDialog(this, "There is no molecule column in the current result table.\nFirst run a query that returns molecules in its result table.", "No molecule column", JOptionPane.INFORMATION_MESSAGE);
//        }
    }//GEN-LAST:event_moleculeReportMenuItemActionPerformed
    
    private void reportEditorMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportEditorMenuItemActionPerformed
        ReportBuilder reportBuilder = new ReportBuilder();
        final ReportBuilderFrame rbFrame = new ReportBuilderFrame(reportBuilder, this);
        openReportFrames.add(rbFrame);
        rbFrame.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent we) {
                openReportFrames.remove(rbFrame);
            }
        });
        rbFrame.show();
    }//GEN-LAST:event_reportEditorMenuItemActionPerformed
    
    
    private void saveSubqueryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSubqueryMenuItemActionPerformed
        try {
            WhereClauseTreeNode currentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
            if (currentNode == null) {
                return;
            }
            WhereClause currentClause = (WhereClause)currentNode.getUserObject();
            try {
                int returnVal = fc2.showSaveDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    if (! fc2.getFileFilter().accept(fc2.getSelectedFile())) {
                        fc2.setSelectedFile(new java.io.File(fc2.getSelectedFile().getCanonicalPath() + "." + ((FileExtensionFilter)fc2.getFileFilter()).getExtensions()[0]));
                    }
                    if (!askOverwritePermission(fc2.getSelectedFile())) {
                        return;
                    }
                    editorModel.saveSubquery(currentClause, fc2.getSelectedFile());
                }
            } catch (java.io.FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_saveSubqueryMenuItemActionPerformed
    
    private void addFromFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFromFileMenuItemActionPerformed
        try {
            WhereClauseTreeNode parentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
            if (parentNode == null) {
                parentNode = (WhereClauseTreeNode)editorModel.getRoot();
            }
            WhereClause parentClause = (WhereClause)parentNode.getUserObject();
            if (parentClause.acceptsAdditionalChild()) {
                try {
                    int returnVal = fc2.showOpenDialog(this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        WhereClause newClause = editorModel.loadSubquery(fc2.getSelectedFile());
                        if (newClause != null) {
                            newClause = (WhereClause)newClause.clone();
                            editorModel.addChild(parentClause, newClause);
                        }
                    }
                } catch (java.io.FileNotFoundException fnfe) {
                    fnfe.printStackTrace();
                } catch (java.io.IOException ioe) {
                    QueryEditorApp.getInstance().showException(ioe, "Could not resolve canonical pathname of " + fc2.getSelectedFile().getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_addFromFileMenuItemActionPerformed
    
    
    private void sqlViewMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sqlViewMenuItemActionPerformed
        Query query = getQueryAndInformUserOnError();
        if (query != null) {
            String queryString = getQueryStringAndInformUserOnError(query);
            if (queryString != null) {
                new SQLViewer(this, true, queryString).show();
            }
        }
    }//GEN-LAST:event_sqlViewMenuItemActionPerformed
    
    private void unwrapMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unwrapMenuItemActionPerformed
        WhereClauseTreeNode currentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
        if (currentNode == null) {
            return;
        }
        WhereClause currentClause = (WhereClause)currentNode.getUserObject();
        editorModel.unwrap(currentClause);
    }//GEN-LAST:event_unwrapMenuItemActionPerformed
    
    private void foldResistanceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_foldResistanceMenuItemActionPerformed
//        new com.pharmadm.custom.rega.foldresistance.FoldResistance2();
    }//GEN-LAST:event_foldResistanceMenuItemActionPerformed
    
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
                runningExecution = null;
            } else {
                JOptionPane.showMessageDialog(this, "Could not stop query because there was no query runnning.", "Cancel query failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Query query = getQueryAndInformUserOnError();
            if (query != null) {
                TableModel currentTableModel = resultTable.getModel();
                numberRowsLabel.setText("Busy...");
                resultTable.setModel(new BusyTableModel("Busy...", "Busy calculating query..."));
                resultTable.getColumnModel().getColumn(0).setPreferredWidth(200);
                if (runningExecution != null) {
                    runningExecution.close();
                }
                setRunning(true);
                try {
                    Statement runningStatement = JDBCManager.getInstance().createScrollableReadOnlyStatement();
                    runningExecution = new ExecuteQueryRunnable(runningStatement, query);
                    Thread executeQuery = new Thread(runningExecution, "Execute SQL query");
                    executeQuery.start();
                } catch (SQLException sqle) {
                    resultTable.setModel(new BusyTableModel("Error", "Failure while executing the query"));
                    QueryEditorApp.getInstance().showException(sqle, "SQL Exception");
                }
            }
        }
    }//GEN-LAST:event_runButtonActionPerformed
    
    private Query getQueryAndInformUserOnError() {
        Query query = editorModel.getQuery();
        if (!query.getRootClause().isValid()) {
            JOptionPane.showMessageDialog(this, "The query is not valid.\nPlease assign a valid value to all parameters.", "Invalid query", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        if (!query.getSelectList().isAnythingSelected()) {
            JOptionPane.showMessageDialog(this, "There are no output columns selected.", "Empty selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return query;
    }
    
    private String getQueryStringAndInformUserOnError(Query query) {
        String queryString = null;
        try {
            queryString = query.getQueryString();
        } catch (SQLException sqle) {
            QueryEditorApp.getInstance().showException(sqle, "Database error while evaluating query.");
            return null;
        } // catch (com.pharmadm.custom.rega.chem.search.MoleculeIndexingException mie) {
//            QueryEditorApp.getInstance().showException(mie, "Molecule processing error while building database query.");
//            return null;
//        }
        return queryString;
    }
    
    private void modifyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyMenuItemActionPerformed
        try {
            WhereClauseTreeNode currentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
            if (currentNode == null) {
                return;
            }
            WhereClause currentClause = (WhereClause)currentNode.getUserObject();
            if (currentClause.isAtomic()) {
                editAtomicClause((AtomicWhereClause)currentClause);
                editorModel.nodeChanged(currentNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_modifyMenuItemActionPerformed
    
    private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuItemActionPerformed
        if (isOkToLooseQuery()) {
            editorModel.createNewQuery();
            currentQueryFile = null;
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
                File oldQueryFile = currentQueryFile;
                currentQueryFile = fc1.getSelectedFile();
                if (askOverwritePermission(currentQueryFile)) {
                    editorModel.saveXMLQuery(currentQueryFile);
                    ok = true;
                    mRUQueries.addDocument(currentQueryFile);
                } else {
                    currentQueryFile = oldQueryFile;
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
        if (currentQueryFile != null) {
            try {
                editorModel.saveXMLQuery(currentQueryFile);
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
        if (isOkToLooseQuery()) {
            try {
                int returnVal = fc1.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    editorModel.loadXMLQuery(fc1.getSelectedFile());
                    currentQueryFile = fc1.getSelectedFile();
                    mRUQueries.addDocument(currentQueryFile);
                }
            } catch (java.io.FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
        }
    }//GEN-LAST:event_openMenuItemActionPerformed
    
    private void deleteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteMenuItemActionPerformed
        try {
            WhereClauseTreeNode currentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
            if (currentNode == null) {
                return;
            }
            WhereClauseTreeNode parentNode = (WhereClauseTreeNode)currentNode.getParent();
            if (parentNode == null) {
                return; // can not remove top node
            }
            WhereClause parentClause = (WhereClause)parentNode.getUserObject();
            WhereClause currentClause = (WhereClause)currentNode.getUserObject();
            editorModel.removeChild(parentClause, currentClause);
            // %$ KVB set selection to parent node ?
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_deleteMenuItemActionPerformed
    
    private void pasteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteMenuItemActionPerformed
        if (cursorClause != null) {
            try {
                WhereClauseTreeNode parentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
                if (parentNode == null) {
                    parentNode = (WhereClauseTreeNode)editorModel.getRoot();
                }
                WhereClause parentClause = (WhereClause)parentNode.getUserObject();
                if (parentClause.acceptsAdditionalChild()) {
                    editorModel.addChild(parentClause, (WhereClause)cursorClause.clone());
                    //treeModel.insertNodeInto(new WhereClauseTreeNode(newClause), parentNode, parentNode.getChildCount());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_pasteMenuItemActionPerformed
    
    private void copyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyMenuItemActionPerformed
        try {
            WhereClauseTreeNode currentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
            if (currentNode == null) {
                return;
            }
            WhereClause currentClause = (WhereClause)currentNode.getUserObject();
            cursorClause = currentClause;
            updateEditMode(currentClause);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_copyMenuItemActionPerformed
    
    private void cutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutMenuItemActionPerformed
        try {
            WhereClauseTreeNode currentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
            if (currentNode == null) {
                return;
            }
            WhereClauseTreeNode parentNode = (WhereClauseTreeNode)currentNode.getParent();
            if (parentNode == null) {
                return; // can not remove top node
            }
            WhereClause parentClause = (WhereClause)parentNode.getUserObject();
            WhereClause currentClause = (WhereClause)currentNode.getUserObject();
            cursorClause = currentClause;
            editorModel.removeChild(parentClause, currentClause);
            updateEditMode(null);
            //treeModel.removeNodeFromParent(currentNode);
            // %$ KVB set selection to parent node ?
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_cutMenuItemActionPerformed
    
    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        showAboutBox();
    }//GEN-LAST:event_aboutMenuItemActionPerformed
    
    public void showAboutBox() {
        final String aboutMsg =
                QueryEditorApp.getInstance().getProduct() + " version "  + QueryEditorApp.getInstance().getVersion()
                + "\nA database querying and exploration tool for the Rega Institute for Medical Research. "
                + "\nTranslated version - use on English language database schema exclusively. "
                + "\n\nWritten by Kurt De Grave, Kristof Van Belleghem and Luc Dehaspe."
                +"\n\u00a9 2003-2005 PharmaDM nv. All rights reserved."
                + "\n\nThis software contains DMax technology for data mining, molecule search acceleration"
                + "\nand substructure calculations."
                +"\n\u00a9 2000-2005 PharmaDM nv. All rights reserved."
                + "\n\nThis software uses the Hibernate and JFreeChart libraries, which are licensed under the terms of"
                +"\nthe GNU Lesser General Public Licence (LGPL).  See http://hibernate.org and http://jfree.org. "
                + "\n\nThis product includes software developed by the Apache Software Foundation."
                +"\nSee http://www.apache.org"
                
                + "\n\n'PharmaDM', 'DMax', 'ViroDM' and the clock/radar logo are trademarks of PharmaDM nv."
                + "\n\nhttp://www.pharmadm.com";
        final String aboutTitle = "About " + getTitle();
        
        java.net.URL url = getClass().getResource("/resources/img/" + QueryEditorApp.getInstance().getSplashImageName());
//        Icon fullLogo = new javax.swing.ImageIcon(url);
        
        JOptionPane.showMessageDialog(this, aboutMsg, aboutTitle, JOptionPane.INFORMATION_MESSAGE, null);
    }
    
    private void contentsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentsMenuItemActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_contentsMenuItemActionPerformed
    
    private void wrapNotMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wrapNotMenuItemActionPerformed
        WhereClauseTreeNode currentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
        if (currentNode == null) {
            return;
        }
        WhereClause currentClause = (WhereClause)currentNode.getUserObject();
        editorModel.wrapNot(currentClause);
    }//GEN-LAST:event_wrapNotMenuItemActionPerformed
    
    private void wrapOrMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wrapOrMenuItemActionPerformed
        WhereClauseTreeNode currentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
        if (currentNode == null) {
            return;
        }
        WhereClause currentClause = (WhereClause)currentNode.getUserObject();
        editorModel.wrapOr(currentClause);
    }//GEN-LAST:event_wrapOrMenuItemActionPerformed
    
    private void wrapAndMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wrapAndMenuItemActionPerformed
        WhereClauseTreeNode currentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
        if (currentNode == null) {
            return;
        }
        WhereClause currentClause = (WhereClause)currentNode.getUserObject();
        editorModel.wrapAnd(currentClause);
    }//GEN-LAST:event_wrapAndMenuItemActionPerformed
    
    private void addAtomicMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAtomicMenuItemActionPerformed
        try {
            WhereClauseTreeNode parentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
            if (parentNode == null) {
                parentNode = (WhereClauseTreeNode)editorModel.getRoot();
            }
            WhereClause parentClause = (WhereClause)parentNode.getUserObject();
            if (parentClause.acceptsAdditionalChild()) {
                Collection prototypeList = parentClause.getAvailableAtomicClauses(AWCPrototypeCatalog.getInstance());
                AtomicClauseSelectionDialog selectionDialog = new AtomicClauseSelectionDialog(this, editorModel, parentClause, prototypeList, true);
                selectionDialog.show();
                WhereClause newClause = selectionDialog.getSelectedClause();
                if (newClause != null) {
                    editorModel.addChild(parentClause, newClause);
                    //treeModel.insertNodeInto(new WhereClauseTreeNode(newClause), parentNode, parentNode.getChildCount());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_addAtomicMenuItemActionPerformed
    
    private void addNotMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNotMenuItemActionPerformed
        try {
            WhereClauseTreeNode parentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
            if (parentNode == null) {
                parentNode = (WhereClauseTreeNode)editorModel.getRoot();
            }
            WhereClause parentClause = (WhereClause)parentNode.getUserObject();
            if (parentClause.acceptsAdditionalChild()) {
                WhereClause newClause = new NotClause();
                editorModel.addChild(parentClause, newClause);
                //treeModel.insertNodeInto(new WhereClauseTreeNode(newClause), parentNode, parentNode.getChildCount());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_addNotMenuItemActionPerformed
    
    private void addOrMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addOrMenuItemActionPerformed
        try {
            WhereClauseTreeNode parentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
            if (parentNode == null) {
                parentNode = (WhereClauseTreeNode)editorModel.getRoot();
            }
            WhereClause parentClause = (WhereClause)parentNode.getUserObject();
            if (parentClause.acceptsAdditionalChild()) {
                WhereClause newClause = new InclusiveOrClause();
                editorModel.addChild(parentClause, newClause);
                //treeModel.insertNodeInto(new WhereClauseTreeNode(newClause), parentNode, parentNode.getChildCount());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_addOrMenuItemActionPerformed
    
    private void addAndMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAndMenuItemActionPerformed
        try {
            WhereClauseTreeNode parentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
            if (parentNode == null) {
                parentNode = (WhereClauseTreeNode)editorModel.getRoot();
            }
            WhereClause parentClause = (WhereClause)parentNode.getUserObject();
            if (parentClause.acceptsAdditionalChild()) {
                WhereClause newClause = new AndClause();
                editorModel.addChild(parentClause, newClause);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_addAndMenuItemActionPerformed
    
    private void initResultTable() {
        resultTable = new QueryResultJTable(QueryEditorApp.getInstance().getWorkManager());
        jScrollPaneTable.setViewportView(resultTable);
        toggleShowingMoleculesJCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        ToggleAction toggleShowingMoleculesAction = resultTable.getToggleShowingMoleculesAction();
        toggleShowingMoleculesJCheckBoxMenuItem.setSelected(toggleShowingMoleculesAction.getState());
        toggleShowingMoleculesJCheckBoxMenuItem.setAction(toggleShowingMoleculesAction);
        viewMenu.add(toggleShowingMoleculesJCheckBoxMenuItem);
        toggleSupervisorJCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        final ToggleAction toggleSupervisorModeAction = resultTable.getToggleSupervisorModeAction();
        toggleSupervisorJCheckBoxMenuItem.setSelected(toggleSupervisorModeAction.getState());
        toggleSupervisorJCheckBoxMenuItem.setAction(toggleSupervisorModeAction);
        dataMiningMenu.add(toggleSupervisorJCheckBoxMenuItem, 0);
        toggleSupervisorModeAction.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                forgetActivityMenuItem.setEnabled(toggleSupervisorModeAction.getState());
                markActivesMenuItem.setEnabled(toggleSupervisorModeAction.getState());
                generateModelMenuItem.setEnabled(toggleSupervisorModeAction.getState());
                generateModelActivesMenuItem.setEnabled(toggleSupervisorModeAction.getState());
            }
        });
    }
    
    private void initQueryTree() {
        queryTree.getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
        queryTree.setShowsRootHandles(true);
        queryTree.putClientProperty("JTree.lineStyle", "Angled");
        queryTree.addTreeSelectionListener(new MyTreeSelectionListener());
        queryTree.addMouseListener(new MyMouseListener());
        queryTree.setModel(editorModel);
        queryTree.setCellRenderer(new QueryTreeCellRenderer());
        updateEditMode(editorModel.getRootClause());
        editorModel.addTreeModelListener(new MyTreeModelListener());
        installSelectPanel();
    }
    
    private void installSelectPanel() {
        jSelectPanel = new SelectPanel(editorModel);
        jScrollPaneSelect.setViewportView(jSelectPanel);
    }
    
    private void showTreePopupMenu(Point p) {
        JPopupMenu borrowedMenu = editMenu.getPopupMenu();
        borrowedMenu.show(queryTree,p.x+10,p.y); // apparently this sets the invoker of the menu to queryTree (YUCK !)
        borrowedMenu.setInvoker(editMenu);   // SO give it back to its rightful owner now
    }
    
    private class MyTreeSelectionListener implements javax.swing.event.TreeSelectionListener {
        public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
            WhereClauseTreeNode currentNode = (WhereClauseTreeNode)queryTree.getLastSelectedPathComponent();
            if (currentNode == null) {
                currentNode = (WhereClauseTreeNode)editorModel.getRoot();
            }
            WhereClause currentClause = (WhereClause)currentNode.getUserObject();
            updateEditMode(currentClause);
        }
    }
    
    private class MyTreeModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
        }
        public void treeNodesInserted(final TreeModelEvent e) {
            SwingUtilities.invokeLater(new Runnable() {public void run() {
                queryTree.scrollPathToVisible(new TreePath(((WhereClauseTreeNode)e.getChildren()[0]).getPath()));
            }});
        }
        public void treeNodesRemoved(TreeModelEvent e) {
        }
        public void treeStructureChanged(TreeModelEvent e) {
        }
    }
    
    
    private class MyMouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent evt) {
            int x = evt.getPoint().x;
            int y = evt.getPoint().y;
            final TreePath tp = queryTree.getPathForLocation(x,y);
            if (tp != null) {
                queryTree.setSelectionPath(tp);
                WhereClauseTreeNode currentNode = (WhereClauseTreeNode)tp.getLastPathComponent();
                WhereClause currentClause = (WhereClause)currentNode.getUserObject();
                if ((evt.getClickCount() > 1) && (evt.getModifiers() & java.awt.event.InputEvent.BUTTON1_MASK) != 0) {
                    if (currentClause.isAtomic()) {
                        editAtomicClause((AtomicWhereClause)currentClause);
                        editorModel.nodeChanged(currentNode);
                    }
                }
            }
        }
        
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showTreePopupMenu(e.getPoint());
            }
        }
        
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showTreePopupMenu(e.getPoint());
            }
        }
    }
    
    private void editAtomicClause(AtomicWhereClause currentClause) {
        AtomicWhereClauseEditor atomEditor = new AtomicWhereClauseEditor(editorModel);
        atomEditor.setAtomicWhereClause(currentClause);
        atomEditor.setVisualizationComponentFactory(new VisualizationComponentFactory(atomEditor));
        new AtomicClauseEditorDialog(this, atomEditor , true).show();
    }
    
    private void updateEditMode(WhereClause forClause) {
        if (forClause == null) {
            modifyMenuItem.setEnabled(false);
            addMenu.setEnabled(false);
            wrapMenu.setEnabled(false);
            unwrapMenuItem.setEnabled(false);
            cutMenuItem.setEnabled(false);
            copyMenuItem.setEnabled(false);
            pasteMenuItem.setEnabled(false);
            deleteMenuItem.setEnabled(false);
        } else {
            boolean currentIsAtomic = forClause.isAtomic();
            modifyMenuItem.setEnabled(currentIsAtomic);
            addMenu.setEnabled(! currentIsAtomic);
            wrapMenu.setEnabled(forClause.getParent() != null);
            WhereClause parentClause = forClause.getParent();
            unwrapMenuItem.setEnabled(parentClause != null && parentClause.getParent() != null && parentClause.getChildCount() == 1);
            copyMenuItem.setEnabled(true);
            cutMenuItem.setEnabled(forClause.getParent() != null);
            pasteMenuItem.setEnabled((! currentIsAtomic) && (cursorClause != null));
            deleteMenuItem.setEnabled(forClause.getParent() != null);
        }
    }
    
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        exitForm(null);
    }//GEN-LAST:event_exitMenuItemActionPerformed
    
    /**
     * Tries to close all open reports.  (Potentially requiring user intervention to decide on save or not.)
     *
     * @return true iff all open reports wre closed
     */
    private boolean closeAllReports() {
        boolean exitOk = true;
        while ((openReportFrames.size() > 0) && (exitOk)) {
            ReportBuilderFrame rbFrame = (ReportBuilderFrame)openReportFrames.get(openReportFrames.size()-1);
            int currentState = rbFrame.getExtendedState();
            if( ( currentState & JFrame.ICONIFIED ) == JFrame.ICONIFIED) {
                rbFrame.setExtendedState(JFrame.NORMAL);
            }
            rbFrame.toFront();
            exitOk = rbFrame.tryExitForm();
            if (exitOk) {
                openReportFrames.remove(openReportFrames.size()-1);
            }
        }
        return (exitOk);
    }
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        if (closeAllReports()) {
            boolean okToLooseData = false;
            okToLooseData = okToLooseData || (editorModel.getRootClause().getChildCount() == 0);
            okToLooseData = okToLooseData || (!editorModel.isDirty());
            if (!okToLooseData) {
                final String optionDiscard = "Discard";
                final String optionSave = "Save";
                final String optionCancel = "Cancel";
                Object[] options = {optionDiscard, optionSave, optionCancel};
                int option = JOptionPane.showOptionDialog(this, "The query has been changed.\nDo you want to save the changes?",
                        "Close report",  JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, optionSave);
                if (option < 0 || option >= options.length) {
                    System.err.println("Dialog box returned unexpected option!");
                    return;
                } else if (options[option].equals(optionCancel)) {
                    return;
                } else if (options[option].equals(optionSave)) {
                    if (!save()) {
                        return;
                    }
                }
            }
            // store the bounds ...
            RegaSettings.getInstance().setQueryEditorFrameBounds(QueryEditorFrame.this.getBounds());
            // and save all settings to file
            RegaSettings.getInstance().save();
            System.exit(0);
        }
    }//GEN-LAST:event_exitForm
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem addAndMenuItem;
    private javax.swing.JMenuItem addAtomicMenuItem;
    private javax.swing.JMenuItem addFromFileMenuItem;
    private javax.swing.JMenu addMenu;
    private javax.swing.JMenuItem addNotMenuItem;
    private javax.swing.JMenuItem addOrMenuItem;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenu dataMiningMenu;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenuItem editDrugStocksMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem foldResistanceMenuItem;
    private javax.swing.JMenuItem forgetActivityMenuItem;
    private javax.swing.JMenuItem generateModelActivesMenuItem;
    private javax.swing.JMenuItem generateModelMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem impMolsMenuItem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelBottom;
    private javax.swing.JPanel jPanelTable;
    private javax.swing.JScrollPane jScrollPaneSelect;
    private javax.swing.JScrollPane jScrollPaneTable;
    private javax.swing.JScrollPane jScrollPaneTree;
    private javax.swing.JPanel jSelectPanel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JMenuItem markActivesMenuItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem modifyMenuItem;
    private javax.swing.JMenuItem moleculeReportMenuItem;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JLabel numberRowsLabel;
    private javax.swing.JMenuItem openCubeMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JMenuItem printTableMenuItem;
    private javax.swing.JTree queryTree;
    private javax.swing.JMenuItem reportEditorMenuItem;
    private javax.swing.JMenu reportMenu;
    private javax.swing.JButton runButton;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenuItem saveSubqueryMenuItem;
    private javax.swing.JMenuItem saveTableMenuItem;
    private javax.swing.JMenuItem sqlViewMenuItem;
    private javax.swing.JMenu tableMenu;
    private javax.swing.JMenuItem unwrapMenuItem;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JMenuItem wrapAndMenuItem;
    private javax.swing.JMenu wrapMenu;
    private javax.swing.JMenuItem wrapNotMenuItem;
    private javax.swing.JMenuItem wrapOrMenuItem;
    // End of variables declaration//GEN-END:variables
    
    
    private static int eqrCounter = 0;
    
    private class ExecuteQueryRunnable implements Runnable {
        
        private final int id = eqrCounter++;
        
        private final Statement statement;
        private final Query query;
        private volatile boolean canceled = false;
        private final Object cancelLock = new Object();
        
        public ExecuteQueryRunnable(Statement statement, Query query) {
            this.statement = statement;
            this.query = query;
        }
        
        public void run() {
            try {
                Iterator prepWorksIter = query.getPreparationWorks().iterator();
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
                        QueryEditorApp.getInstance().getWorkManager().executeAndWait(aPrepWork);
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            runButton.setEnabled(true);  //enable cancel
                            runButton.setText(regularText);
                        }
                    });
                }
                if  (!canceled) {
                    String queryString = getQueryStringAndInformUserOnError(query);
                    System.out.println(queryString);
                    statement.setFetchSize(50);
                    ResultSet resultSet = statement.executeQuery(queryString);
                    final JDBCTableModel model = new JDBCTableModel(resultSet, query.getSelectList().getSelectedColumnNames());
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
                }
            } catch (final SQLException sqle) {
                synchronized (cancelLock) {
                    if (!canceled) {
                        canceled = true;
                        if (statement != null) {
                            try {
                                System.err.println("About to close statement " + id + " after an SQL exception...");
                                statement.close();
                                System.err.println("Closing the statement was successful.");
                            } catch (SQLException sqle2) {
                                System.err.println("Closing database statement failed.");
                                sqle2.printStackTrace();
                            }
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                setRunning(false);
                                numberRowsLabel.setText("Query execution failed");
                                resultTable.setModel(new BusyTableModel("Error", "Failure while executing the query"));
                                QueryEditorApp.getInstance().showException(sqle, "SQL Exception");
                            }
                        });
                    }
                }
            }
        }
        
        public void cancel() {
            synchronized (cancelLock) {
                if (!canceled) {
                    try {
                        canceled = true;
                        setRunning(false);
                        numberRowsLabel.setText("Canceled");
                        resultTable.setModel(new BusyTableModel("Canceled", "The query was canceled."));
                        System.err.println("User Cancel:: about to cancel statement " + id + "...");
                        statement.cancel();
                        System.err.println("User Cancel:: cancel statement done.");
                    } catch (SQLException sqle) {
                        QueryEditorApp.getInstance().showException(sqle, "Could not cancel the running query");
                    }
                }
            }
        }
        
        public void close() {
            synchronized (cancelLock) {
                // the Oracle 9i driver locks up on close after cancel...
                if (!canceled) {
                    JDBCManager.getInstance().closeStatement(statement);
                }
            }
        }
    }
    
}
