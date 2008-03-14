/*
 * QueryResultJTable.java
 *
 * Created on October 30, 2003, 1:39 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.gui.resulttable;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.*;

//import com.pharmadm.chem.graphics.MolecularScene;
//import com.pharmadm.chem.graphics.graphics2d.*;
//import com.pharmadm.chem.matter.Molecule;
import com.pharmadm.custom.rega.queryeditor.FrontEndManager;
import com.pharmadm.custom.rega.queryeditor.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.gui.ToggleAction;
import com.pharmadm.util.gui.PopupListener;
import com.pharmadm.util.gui.table.TableSorter;
import com.pharmadm.util.work.WorkManager;

/**
 * A GUI JComponent to show the resultset table of a database query.
 *
 * @author  kdg
 */
public class QueryResultJTable extends JTable {
    
    // either the backend model or (if appropriate) the backend model wrapped in a Clob enhancing model.
    private TableModel clobEnhancedModel;
    
    private final TableSorter tableSorter;
    private final TableModel emptyModel = new DefaultTableModel();
    private final MoleculeVisualizationColumnProvider moleculeVisualizationColumnProvider = new MoleculeVisualizationColumnProvider();
//    private final DataMiningSupervisorColumnProvider dataMiningSupervisorColumnProvider = new DataMiningSupervisorColumnProvider();
    private MergedTableModel moleculeVisualizationMergedModel;
    private MergedTableModel supervisorMergedModel;
    
    private final static Color HYPERLINK_BACKGROUND_COLOR = new Color(255, 247, 145);
    
    private final ToggleAction toggleShowingMoleculesAction = new DecorationToggleAction("Show molecules", true);
    private final ToggleAction toggleSupervisorModeAction = new DecorationToggleAction("Supervisor mode", false);
    
    /** Creates a new instance of QueryResultJTable */
    public QueryResultJTable(WorkManager workManager) {
        tableSorter = new ManagedTableSorter(workManager);
        setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        setCellSelectionEnabled(true);
        toggleShowingMoleculesAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyStroke.getKeyStroke('m').getKeyCode()));
        toggleShowingMoleculesAction.putValue(Action.SHORT_DESCRIPTION, "Turns the graphical display of molecules on or off");
        redecorateModel();
        tableSorter.addMouseListenerToTableHeader(this);
        addMouseListener(new HyperlinkListener());
        initPopupMenu();
    }
    
    /**
     * Overrides the setModel method of JTable to maintain a selectionlistener that
     * knows how to handle special columns.
     */
    public void setModel(TableModel model) {
        if (ClobModelDecorator.hasClobColumns(model)) {
            clobEnhancedModel = new ClobModelDecorator(model);
        } else {
            clobEnhancedModel = model;
        }
        redecorateModel();
    }
    
    /**
     * Sets the given model directly, without wrapping it in a decorator model.
     */
    private void setAlreadyDecoratedModel(TableModel model) {
        if (tableSorter != null) {
            tableSorter.setModel(model);
            super.setModel(emptyModel);
            super.setModel(tableSorter);
        } else {
            super.setModel(model);
        }
        setCellSelectionEnabled(true);
        initColumnRenderers();
    }
    
    /**
     * Wraps the model in a few layers of decorators.
     */
    private void redecorateModel() {
        TableModel model = clobEnhancedModel;
        if ((toggleShowingMoleculesAction != null) && (toggleShowingMoleculesAction.getState())) {
            moleculeVisualizationMergedModel = new MergedTableModel(model, moleculeVisualizationColumnProvider);
            model = moleculeVisualizationMergedModel;
        } else {
            moleculeVisualizationMergedModel = null;
        }
//        if ((toggleSupervisorModeAction != null) && (toggleSupervisorModeAction.getState())) {
//            supervisorMergedModel = new MergedTableModel(model, dataMiningSupervisorColumnProvider);
//            model = supervisorMergedModel;
//        } else {
            supervisorMergedModel = null;
//        }
        setAlreadyDecoratedModel(model);
    }
    
    /**
     * Gets the action that toggles between showing the molecules or not.
     * A state of 'true' means molecules are being shown.
     */
    public ToggleAction getToggleShowingMoleculesAction() {
        return toggleShowingMoleculesAction;
    }
    
    /**
     * Gets the action that toggles between showing the supervisor column or not.
     * A state of 'true' means supervisor column is being shown.
     */
    public ToggleAction getToggleSupervisorModeAction() {
        return toggleSupervisorModeAction;
    }
    
    /**
     * Returns null if there is no molecule id column, or a list containing the molecule ids
     * of the first column.
     */
    private List listFirstMoleculeIds() {
        for (int column = 0; column < getColumnCount(); column++) {
            if (isMoleculeColumn(getModel(), column)) {
                ArrayList moleculeIds = new ArrayList();
                int rowCount = getRowCount();
                for (int row = 0; row < rowCount; row++) {
                    Object moleculeIDObj = getModel().getValueAt(row, column);
                    if (moleculeIDObj != null) {
                        moleculeIds.add(moleculeIDObj.toString());
                    }
                }
                return moleculeIds;
            }
        }
        return null;
    }
    
    /**
     * Returns null if there is no molecule id column, or a list containing
     * the unique molecule ids of the first column.
     */
    public List listFirstMoleculeUniqueIds() {
        List nonUnique = listFirstMoleculeIds();
        if (nonUnique == null) {
            return null;
        }
        return keepUniqueIds(nonUnique);
    }
    
    public List keepUniqueIds(List ids) {
        HashSet set = new HashSet();
        List uniqueIds = new ArrayList();
        Iterator idIter = ids.iterator();
        while (idIter.hasNext()) {
            Object id = idIter.next();
            if (!set.contains(id)) {
                set.add(id);
                uniqueIds.add(id);
            }
        }
        return uniqueIds;
    }
    
    private void initPopupMenu() {
        final JPopupMenu popupMenu = new JPopupMenu();
        final PopupListener popupListener = new PopupListener(popupMenu);
        final JMenuItem showPieChartMenuItem = new JMenuItem("Show pie chart\u2026");
        showPieChartMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                int colIndex = columnAtPoint(popupListener.getLocation());
                TableColumn col = getColumnModel().getColumn(colIndex);
                FrontEndManager.getInstance().getFrontEnd().getWorkManager().execute(new ShowDistributionWork(getModel(), col, false));
            }
        });
        showPieChartMenuItem.setMnemonic('p');
        popupMenu.add(showPieChartMenuItem);
        final JMenuItem showHistogramMenuItem = new JMenuItem("Show histogram\u2026");
        showHistogramMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                int colIndex = columnAtPoint(popupListener.getLocation());
                TableColumn col = getColumnModel().getColumn(colIndex);
                FrontEndManager.getInstance().getFrontEnd().getWorkManager().execute(new ShowDistributionWork(getModel(), col, true));
            }
        });
        showHistogramMenuItem.setMnemonic('h');
        popupMenu.add(showHistogramMenuItem);
        // If supervisor mode is toggled, add another menu item.
        /*
         *  Not yet implemented because of concerns of what happens if a molecule occurs in several rows.
         *
        final JMenuItem markActivesMenuItem;
        markActivesMenuItem = new JMenuItem("Mark active/inactive\u2026");
        markActivesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                int colIndex = columnAtPoint(popupListener.getLocation());
                TableColumn col = getColumnModel().getColumn(colIndex);
                QueryEditorApp.getInstance().getWorkManager().execute(new ShowSupervisorPanelWork(getModel(), col));
            }
        });
        markActivesMenuItem.setMnemonic('m');
        getToggleSupervisorModeAction().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                if (getToggleSupervisorModeAction().getState()) {
                    popupMenu.add(markActivesMenuItem);
                } else {
                    popupMenu.remove(markActivesMenuItem);
                }
            }
        });
         */
        // Disable histogram for non-numeric types.
        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                int colIndex = columnAtPoint(popupListener.getLocation());
                Class colClass = getColumnClass(colIndex);
                boolean numeric = Number.class.isAssignableFrom(colClass);
                showHistogramMenuItem.setEnabled(numeric);
                // belongs to the commencted code above
                // markActivesMenuItem.setEnabled(numeric);
            }
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        getTableHeader().addMouseListener(popupListener);
    }
    
    private void initColumnRenderers() {
        TableCellRenderer regularCellRenderer = new DefaultTableCellRenderer();
        TableCellRenderer moleculeCellRenderer = null;
        TableCellRenderer hyperlinkCellRenderer = new HyperlinkCellRenderer();
        for (int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++) {
            TableColumn tableColumn = getColumnModel().getColumn(columnIndex);
            if (isPatientColumn(columnIndex) || isMoleculeColumn(getModel(), columnIndex)) {
                tableColumn.setCellRenderer(hyperlinkCellRenderer);
            } 
//            else if (isMoleculeVisualizationColumn(columnIndex)) {
//                if (moleculeCellRenderer == null) {
//                    moleculeCellRenderer = new MoleculeCellRenderer();
//                }
//                tableColumn.setCellRenderer(moleculeCellRenderer);
//            } 
        else {
                tableColumn.setCellRenderer(regularCellRenderer);
            }
        }
        // Allow for more space if molecules are shown
//        if (moleculeCellRenderer == null) {
            setRowHeight(16);
//        } else {
//            setRowHeight(80);
//        }
    }
    
    private boolean isPatientColumn(int column) {
        return getModel().getColumnName(column).endsWith(".PATIENT_II");
    }
    
    private boolean isMoleculeColumn(TableModel model, int column) {
        return moleculeVisualizationColumnProvider.isMoleculeIDColumn(model, column);
    }
    
    private boolean isMoleculeVisualizationColumn(int column) {
        String colName = getModel().getColumnName(column);
        return ((colName != null) && (colName.endsWith(moleculeVisualizationColumnProvider.getNameSuffix())));
    }
    
    private String getPatientIIIfAny(int row, int column) {
        if (isPatientColumn(column)) {
            Object patientII = getModel().getValueAt(row, column);
            if (patientII != null) {
                return patientII.toString();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    // when a row is clicked in the result table, the results of the corresponding patient are visualized (if any)
    private class HyperlinkListener extends java.awt.event.MouseAdapter {
        
        public void mouseClicked(java.awt.event.MouseEvent me) {
            if (me.getClickCount() == 1) {
                java.awt.Point point = me.getPoint();
                int viewColumn = columnAtPoint(point);
                int row = rowAtPoint(point);
                if ((viewColumn != -1) && (row != -1)) {
                    int column = getColumnModel().getColumn(viewColumn).getModelIndex();
//                    showPatientIfAny(row, column);
//                    tearOffMoleculeIfAny(row, column);
//                    cycleSuperVisorValueIfAny(row, column);
                }
            }
        }
        
//        private void showPatientIfAny(int row, int column) {
//            String patientII = getPatientIIIfAny(row, column);
//            if (patientII != null) {
//                try {
//                    new com.pharmadm.custom.rega.visualization.PatientFrame(com.pharmadm.custom.rega.domainclasses.Patient.getPatient(patientII)).show();
//                } catch (SQLException sqle) {
//                    System.err.println("SQL Exception - no chart available. " + sqle.getMessage());
//                }
//            }
//        }
        
//        private void tearOffMoleculeIfAny(int row, int column) {
//            if (isMoleculeColumn(getModel(), column) || isMoleculeVisualizationColumn(column)) {
//                BigDecimal moleculeID = (BigDecimal)getModel().getValueAt(row, column);
//                if (moleculeID != null) {
////                    new MoleculeJFrame(moleculeID).show();
//                }
//            }
//        }
        
//        private void cycleSuperVisorValueIfAny(int row, int column) {
//            if (dataMiningSupervisorColumnProvider != null && dataMiningSupervisorColumnProvider.isSupervisorColumn(getModel(), column)) {
//                getModel().setValueAt(DataMiningSupervisorColumnProvider.CYCLE_CODE, row, column);
//                
//                // this could be done A LOT faster, but TableSorter is currently not compatible with changing data in the underlying model.
//                // FIXME
//                repaint();
//            }
//        }
    }
    
    private class HyperlinkCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String newValue = null;
            if (value != null) {
                newValue = "<html><a href=\"\">" + value.toString() + "</a></html>";
            }
            Component comp = super.getTableCellRendererComponent(table, newValue, isSelected, hasFocus, row, column);
            //comp.setBackground(HYPERLINK_BACKGROUND_COLOR);
            //comp.setForeground(Color.BLUE);
            return comp;
        }
    }
    
//    private class MoleculeCellRenderer extends DefaultTableCellRenderer {
//        
//        private MolecularScene molecularScene;
//
//        public MoleculeCellRenderer() {
//            DisplayOptions2D options = new DisplayOptions2D();
//            molecularScene =  new MolecularScene2D(null, options);
//        }
//        
//        private Molecule mol;
//        
//        public void paint(java.awt.Graphics g) {
//            if (mol != null) {
//                molecularScene.setMolecule(mol);
//                JPanel jPanel = molecularScene.getJPanel();
//                jPanel.setSize(getSize());
//                jPanel.paint(g);
//            } else {
//                Color color = g.getColor();
//                g.setColor(Color.LIGHT_GRAY);
//                java.awt.Rectangle bounds = g.getClipBounds();
//                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
//                g.setColor(color);
//            }
//        }
//        
//        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//            Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//            this.mol = JDBCManager.getInstance().getMolecule((BigDecimal) value);
//            return comp;
//        }
//    }
    
    
    private class DecorationToggleAction extends ToggleAction {
        public DecorationToggleAction(String name, boolean initialState) {
            super(name, initialState);
        }
        public void actionPerformed(ActionEvent evt) {
            super.actionPerformed(evt);
            redecorateModel();
        }
    };
}
