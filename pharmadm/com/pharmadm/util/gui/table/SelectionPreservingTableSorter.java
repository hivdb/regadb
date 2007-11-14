/*
 * SelectionPreservingTableSorter.java
 *
 * Created on November 3, 2003, 3:40 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.gui.table;

import java.util.*;
import javax.swing.DefaultListSelectionModel;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelEvent;

/**
 *
 * @author  kristof
 *
 * An extension of TableSorter which preserves the subscribed DefaultListSelectionModels
 *
 */
public class SelectionPreservingTableSorter extends TableSorter {
    
    private ArrayList preservableSelectionModels = new ArrayList();
    
    /**
     * The most straightforward use of this class is by using this constructor with
     * a JTable's basic TableModel and SelectionModel as parameters.
     * It creates a new instance of SelectionPreservingTableSorter wrapped around the given
     * TableModel, and with the associated DefaultListSelectionModel that will be preserved.
     */
    public SelectionPreservingTableSorter(TableModel model, DefaultListSelectionModel selection) {
        super(model);
        addPreservableSelectionModel(selection);
    }
    
    public SelectionPreservingTableSorter(TableModel model) {
        super(model);
    }
    
    /** Creates a new instance of SelectionPreservingTableSorter */
    public SelectionPreservingTableSorter() {
        super();
    }
    
    /** Add a DefaultListSelectionModel to the list of models to be preserved */
    public void addPreservableSelectionModel(DefaultListSelectionModel selection) {
        if (selection.getSelectionMode() == DefaultListSelectionModel.SINGLE_INTERVAL_SELECTION) {
            System.err.println("Warning ! SelectionPreservingTableSorter is resetting " + selection + 
                  "'s selection mode from SINGLE_INTERVAL_SELECTION to MULTIPLE_INTERVAL_SELECTION.");
            selection.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
        preservableSelectionModels.add(selection);
    }
    
    public void sort(Object sender) {
        int[] oldIndexes = (int[])indexes.clone();
        super.sort(sender);
        updateSelections(oldIndexes);
    }

    protected void updateSelections(int[] oldIndexes) {
        Iterator iter = preservableSelectionModels.iterator();
        while (iter.hasNext()) {
            try {
                DefaultListSelectionModel selection = (DefaultListSelectionModel)iter.next();
                DefaultListSelectionModel oldSelection = (DefaultListSelectionModel)selection.clone();
                selection.clearSelection();
                for (int i = 0; i < oldIndexes.length; i++) {
                    if (oldSelection.isSelectedIndex(i)) {
                        for (int j = 0; j < indexes.length; j++) {
                            if (indexes[j] == oldIndexes[i]) {
                                selection.addSelectionInterval(j,j);
                                break;
                            }
                        }
                    }
                }
            } catch (CloneNotSupportedException cnse) {
                cnse.printStackTrace();
            }
            
        }
    }
    
}
