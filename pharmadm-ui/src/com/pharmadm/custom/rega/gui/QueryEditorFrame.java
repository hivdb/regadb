package com.pharmadm.custom.rega.gui;


import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import com.pharmadm.custom.rega.queryeditor.AWCPrototypeCatalog;
import com.pharmadm.custom.rega.queryeditor.AndClause;
import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.InclusiveOrClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.QueryResultTableModel;
import com.pharmadm.custom.rega.queryeditor.NotClause;
import com.pharmadm.custom.rega.queryeditor.Query;
import com.pharmadm.custom.rega.queryeditor.QueryEditor;
import com.pharmadm.custom.rega.queryeditor.WhereClause;
import com.pharmadm.custom.rega.queryeditor.WhereClauseTreeNode;
import com.pharmadm.custom.rega.queryeditor.gui.resulttable.QueryResultJTable;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.port.QueryResult;
import com.pharmadm.custom.rega.queryeditor.port.QueryStatement;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.AtomicWhereClauseEditor;
import com.pharmadm.custom.rega.savable.DirtinessEvent;
import com.pharmadm.custom.rega.savable.DirtinessListener;
import com.pharmadm.util.gui.mdi.DocumentLoader;
import com.pharmadm.util.gui.mdi.MRUDocumentsList;
import com.pharmadm.util.settings.RegaSettings;
import com.pharmadm.util.work.Work;

/**
 *
 * @author  kristof, kdg
 */
public class QueryEditorFrame extends javax.swing.JFrame implements QueryContext {
    
    private String busyString;
    private JProgressBar progressBar;
    private volatile boolean running = false;
    private ExecuteQueryRunnable runningExecution;
    
    private QueryEditor editorModel;
    private List<WhereClause> cursorClauses = null;
    private File currentQueryFile = null;
    
    
    private QueryResultJTable resultTable;
    private JFileChooser fc1 = new JFileChooser();
    private JFileChooser fc2 = new JFileChooser();
    
    
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
    
    /** Creates new form QueryEditorFrame */
    public QueryEditorFrame(QueryEditor editorModel) {
        this.editorModel = editorModel;
        cursorClauses = new ArrayList<WhereClause>();
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
        editorModel.addDirtinessListener(new DirtinessListener() {
            public void dirtinessChanged(DirtinessEvent de) {
                saveMenuItem.setEnabled(de.getSavable().isDirty());
            }
        });
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
        dialog.setLocationRelativeTo(null);
        dialog.validate();
        dialog.pack();
        dialog.setVisible(true);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
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
        viewMenu = new javax.swing.JMenu();
        sqlViewMenuItem = new javax.swing.JMenuItem();
        runQueryMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(QueryEditorApp.getInstance().getProduct());
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

        runQueryMenuItem.setMnemonic('r');
        runQueryMenuItem.setText("run test query");
        runQueryMenuItem.setToolTipText("run a custom query");
        runQueryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runQueryMenuItemActionPerformed(evt);
            }

        });
        
        fileMenu.add(runQueryMenuItem);
        
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


        setJMenuBar(menuBar);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents
    
    
    private void saveSubqueryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSubqueryMenuItemActionPerformed
        try {
            WhereClause currentClause = getLastSelectedNonAtomicClause();
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
            WhereClause parentClause = getLastSelectedNonAtomicClause();
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
                new SQLViewer(this, true, queryString).setVisible(true);
            }
        }
    }//GEN-LAST:event_sqlViewMenuItemActionPerformed
    
    private void unwrapMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unwrapMenuItemActionPerformed
        editorModel.unwrap(getClauses(getSelectedNodes()));
    }//GEN-LAST:event_unwrapMenuItemActionPerformed
    
    
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
    
    private void runQueryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
    	new SQLEditor(this, false, "").setVisible(true);
    }

    
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
            runQuery(query);
        }
    }//GEN-LAST:event_runButtonActionPerformed
    
    public void runQuery(Query query) {
        if (query != null) {
            numberRowsLabel.setText("Busy...");
            resultTable.setModel(new BusyTableModel("Busy...", "Busy calculating query..."));
            resultTable.getColumnModel().getColumn(0).setPreferredWidth(200);
            if (runningExecution != null) {
                runningExecution.close();
            }
            setRunning(true);
            try {
                QueryStatement runningStatement = DatabaseManager.getInstance().getDatabaseConnector().createScrollableReadOnlyStatement();
                runningExecution = new ExecuteQueryRunnable(runningStatement, query);
                Thread executeQuery = new Thread(runningExecution, "Execute SQL query");
                executeQuery.start();
            } catch (SQLException sqle) {
                resultTable.setModel(new BusyTableModel("Error", "Failure while executing the query"));
                QueryEditorApp.getInstance().showException(sqle, "SQL Exception");
            }
        }
    }
    
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
        } 
        return queryString;
    }
    
    private void modifyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyMenuItemActionPerformed
        try {
            WhereClauseTreeNode currentNode = getSelectedNodes().get(0);
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
        	List<WhereClauseTreeNode> selection = getSelectedNodes();
        	for (WhereClauseTreeNode node : selection) {
        		if (node != null) {
	        		WhereClauseTreeNode parentNode  = (WhereClauseTreeNode) node.getParent();
	        		if (parentNode != null) {
		        		WhereClause parentClause  = (WhereClause) parentNode.getUserObject();
		                WhereClause currentClause = (WhereClause)node.getUserObject();
		                if (currentClause != null && parentClause != null) {
		                	editorModel.removeChild(parentClause, currentClause);
		                }
	        		}
        		}
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_deleteMenuItemActionPerformed
    
    private void pasteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteMenuItemActionPerformed
        if (cursorClauses.size() > 0) {
            try {
                WhereClause parentClause = getLastSelectedNonAtomicClause();
                for (WhereClause clause : cursorClauses) {
                    if (parentClause.acceptsAdditionalChild()) {
                        editorModel.addChild(parentClause, (WhereClause) clause.clone());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_pasteMenuItemActionPerformed
    
    private void copyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyMenuItemActionPerformed
        try {
            List<WhereClauseTreeNode> selection = getSelectedNodes();
            cursorClauses = getClauses(selection);
            updateEditMode(selection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_copyMenuItemActionPerformed
    
    private void cutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutMenuItemActionPerformed
        try {
            List<WhereClauseTreeNode> selection = getSelectedNodes();
            cursorClauses = getClauses(selection);
            
            for (WhereClause clause : cursorClauses) {
                editorModel.removeChild(clause.getParent(), clause);
            }
            updateEditMode(selection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_cutMenuItemActionPerformed
    
    private void wrapNotMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wrapNotMenuItemActionPerformed
        editorModel.wrapNot(getClauses(getSelectedNodes()));
    }//GEN-LAST:event_wrapNotMenuItemActionPerformed
    
    private void wrapOrMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wrapOrMenuItemActionPerformed
        editorModel.wrapOr(getClauses(getSelectedNodes()));
    }//GEN-LAST:event_wrapOrMenuItemActionPerformed
    
    private void wrapAndMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wrapAndMenuItemActionPerformed
        editorModel.wrapAnd(getClauses(getSelectedNodes()));
    }//GEN-LAST:event_wrapAndMenuItemActionPerformed
    
    private void addAtomicMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAtomicMenuItemActionPerformed
        try {
            WhereClause parentClause = getLastSelectedNonAtomicClause();
            if (parentClause.acceptsAdditionalChild()) {
                Collection<AtomicWhereClause> prototypeList = parentClause.getAvailableAtomicClauses(AWCPrototypeCatalog.getInstance());
                AtomicClauseSelectionDialog selectionDialog = new AtomicClauseSelectionDialog(this, this, prototypeList, true);
                selectionDialog.setVisible(true);
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
            WhereClause parentClause = getLastSelectedNonAtomicClause();
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
            WhereClause parentClause = getLastSelectedNonAtomicClause();
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
            WhereClause parentClause = getLastSelectedNonAtomicClause();
        	
            if (parentClause.acceptsAdditionalChild()) {
                WhereClause newClause = new AndClause();
                editorModel.addChild(parentClause, newClause);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_addAndMenuItemActionPerformed
    
    private WhereClause getLastSelectedNonAtomicClause() {
        WhereClauseTreeNode node = getSelectedNodes().get(0);
        if (node == null) {
        	node = (WhereClauseTreeNode)editorModel.getRoot();
        }
        WhereClause parentClause = (WhereClause)node.getUserObject();
        
        if (parentClause.isAtomic()) {
        	parentClause = parentClause.getParent();
        }
    	
        return parentClause;
    }
     
    private void initResultTable() {
        resultTable = new QueryResultJTable(QueryEditorApp.getInstance().getWorkManager());
        jScrollPaneTable.setViewportView(resultTable);
    }
    
    private void initQueryTree() {
        queryTree.getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        queryTree.setShowsRootHandles(true);
        queryTree.putClientProperty("JTree.lineStyle", "Angled");
        queryTree.addTreeSelectionListener(new MyTreeSelectionListener());
        queryTree.addMouseListener(new MyMouseListener());
        queryTree.setModel(editorModel);
        queryTree.setCellRenderer(new QueryTreeCellRenderer());
        
        List<WhereClauseTreeNode> selection = new ArrayList<WhereClauseTreeNode>();
        selection.add(new WhereClauseTreeNode(editorModel.getRootClause()));
        updateEditMode(selection);
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
            updateEditMode(getSelectedNodes());
        	
        }
    }
    
    private List<WhereClauseTreeNode> getSelectedNodes() {
    	List<WhereClauseTreeNode> selection = new ArrayList<WhereClauseTreeNode>();
    	TreePath[] paths = queryTree.getSelectionPaths();
    	if (paths != null) {
	    	for (TreePath path : paths) {
	    		selection.add(  (WhereClauseTreeNode) path.getLastPathComponent());
	    	}
    	}
    	if (selection.size() == 0) {
    		queryTree.setSelectionRow(0);
    		selection.add((WhereClauseTreeNode)editorModel.getRoot());
    	}
    	return selection;
    }
    
    private List<WhereClause> getClauses(List<WhereClauseTreeNode> nodes) {
    	List<WhereClause> clauses = new ArrayList<WhereClause>();
    	for (WhereClauseTreeNode node : nodes) {
    		if (node.getUserObject() != null) {
    			clauses.add((WhereClause) node.getUserObject());
    		}
    	}
    	return clauses;
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
        	super.mouseClicked(evt);
        	selectNode(evt);
        }
        
        private void selectNode(MouseEvent evt) {
            int x = evt.getPoint().x;
            int y = evt.getPoint().y;
            final TreePath tp = queryTree.getPathForLocation(x,y);
            if (tp != null) {
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
        
        public void mousePressed(MouseEvent evt) {
        	selectNode(evt);
            if (evt.isPopupTrigger()) {
                showTreePopupMenu(evt.getPoint());
            }
        }
        
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showTreePopupMenu(e.getPoint());
            }
        }
    }
    
    private void editAtomicClause(AtomicWhereClause currentClause) {
        AtomicWhereClauseEditor atomEditor = new AtomicWhereClauseEditor(this, currentClause);
        new AtomicClauseEditorDialog(this, atomEditor , true).setVisible(true);
    }
    
    private void updateEditMode(List<WhereClauseTreeNode> selection) {
        if (hasNullNodes(selection)) {
            modifyMenuItem.setEnabled(false);
            addMenu.setEnabled(false);
            wrapMenu.setEnabled(false);
            unwrapMenuItem.setEnabled(false);
            cutMenuItem.setEnabled(false);
            copyMenuItem.setEnabled(false);
            pasteMenuItem.setEnabled(false);
            deleteMenuItem.setEnabled(false);
        } else {
        	WhereClause firstClause = (WhereClause) selection.get(0).getUserObject();
            boolean firstIsAtomic = firstClause.isAtomic();
            boolean haveSameParent = haveSameParent(selection);
            boolean containsRoot = containsRootClause(selection);
            
            modifyMenuItem.setEnabled(firstIsAtomic && selection.size() == 1);
            wrapMenu.setEnabled(haveSameParent);

            WhereClause parentClause = firstClause.getParent();
            unwrapMenuItem.setEnabled(haveSameParent && parentClause.getParent() != null);
            
            copyMenuItem.setEnabled(haveSameParent);
            cutMenuItem.setEnabled(haveSameParent);
            pasteMenuItem.setEnabled((! firstIsAtomic) && (cursorClauses.size() > 0) && selection.size() == 1);
            deleteMenuItem.setEnabled(!containsRoot);
            
            addMenu.setEnabled(haveSameParent || selection.get(0).equals(editorModel.getRoot()));
        }
    }
    
    private boolean hasNullNodes(List<WhereClauseTreeNode> selection) {
    	boolean nullNodes = false;
    	for (WhereClauseTreeNode node : selection) {
        	WhereClause clause = (WhereClause) node.getUserObject();
    		if (clause == null) {
    			nullNodes = true;
    		}
    	}
    	return nullNodes;
    	
    }
    
    private boolean haveSameParent(List<WhereClauseTreeNode> selection) {
    	boolean same = true;
    	WhereClauseTreeNode parentNode = (WhereClauseTreeNode) selection.get(0).getParent();
    	for (WhereClauseTreeNode node : selection) {
    		if (node.getParent() == null || !node.getParent().equals(parentNode)) {
    			same = false;
    		}
    	}
    	return same;
    }
    
    private boolean containsRootClause(List<WhereClauseTreeNode> selection) {
    	boolean root = false;
    	WhereClauseTreeNode parentNode = (WhereClauseTreeNode) selection.get(0).getParent();
    	for (WhereClauseTreeNode node : selection) {
    		WhereClause clause = (WhereClause) node.getUserObject();
    		if (clause.getParent() == null) {
    			root = true;
    		}
    	}
    	return root;
    }
    
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        exitForm(null);
    }//GEN-LAST:event_exitMenuItemActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
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
    }//GEN-LAST:event_exitForm
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addAndMenuItem;
    private javax.swing.JMenuItem addAtomicMenuItem;
    private javax.swing.JMenuItem addFromFileMenuItem;
    private javax.swing.JMenu addMenu;
    private javax.swing.JMenuItem addNotMenuItem;
    private javax.swing.JMenuItem addOrMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem modifyMenuItem;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JLabel numberRowsLabel;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JMenuItem printTableMenuItem;
    private javax.swing.JTree queryTree;
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
    private javax.swing.JMenuItem runQueryMenuItem;
    // End of variables declaration//GEN-END:variables
    
    
    private static int eqrCounter = 0;
    
    private class ExecuteQueryRunnable implements Runnable {
        
        private final int id = eqrCounter++;
        
        private final QueryStatement statement;
        private final Query query;
        private volatile boolean canceled = false;
        private final Object cancelLock = new Object();
        
        public ExecuteQueryRunnable(QueryStatement statement, Query query) {
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
                    QueryResult resultSet = statement.executeQuery(queryString);
                    final QueryResultTableModel model = new QueryResultTableModel(resultSet, query.getSelectList().getSelectedColumnNames());
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
            } catch (final Exception sqle) {
            	sqle.printStackTrace();
                synchronized (cancelLock) {
                    if (!canceled) {
                        canceled = true;
                        if (statement.exists()) {
                            System.err.println("About to close statement " + id + " after an SQL exception...");
                            statement.close();
                            System.err.println("Closing the statement was successful.");
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
                    canceled = true;
                    setRunning(false);
                    numberRowsLabel.setText("Canceled");
                    resultTable.setModel(new BusyTableModel("Canceled", "The query was canceled."));
                    System.err.println("User Cancel:: about to cancel statement " + id + "...");
                    statement.cancel();
                    System.err.println("User Cancel:: cancel statement done.");
                }
            }
        }
        
        public void close() {
            synchronized (cancelLock) {
                // the Oracle 9i driver locks up on close after cancel...
                if (!canceled) {
                	statement.close();
                }
            }
        }
    }

	@Override
	public WhereClause getContextClause() {
		return getLastSelectedNonAtomicClause();
	}

}
