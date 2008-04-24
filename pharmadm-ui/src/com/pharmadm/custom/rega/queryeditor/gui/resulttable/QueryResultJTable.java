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
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ToggleAction;
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
    
    
    
    /** Creates a new instance of QueryResultJTable */
    public QueryResultJTable(WorkManager workManager) {
        tableSorter = new ManagedTableSorter(workManager);
        setCellSelectionEnabled(true);
        redecorateModel();
        tableSorter.addMouseListenerToTableHeader(this);
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
        setAlreadyDecoratedModel(model);
    }
    
    private void initColumnRenderers() {
        TableCellRenderer regularCellRenderer = new DefaultTableCellRenderer();
        for (int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++) {
            TableColumn tableColumn = getColumnModel().getColumn(columnIndex);
            tableColumn.setCellRenderer(regularCellRenderer);
        }
        setRowHeight(16);
    }
}
