/*
 * ManagedTableSorter.java
 *
 * Created on January 6, 2004, 4:19 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.gui.resulttable;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

import com.pharmadm.util.gui.table.TableSorter;
import com.pharmadm.util.work.WorkAdapter;
import com.pharmadm.util.work.WorkManager;

/**
 * A TableSorter where sorting is managed by a WorkManager.
 *
 * This is reaonably threadsafe. Attempts to change the table model
 * will block until sorting is complete. Of course, the <em>underlying</em> 
 * Tablemodel can still be changed without blocking, which will result 
 * in an undefined state.
 *
 * Reading from the TableModel is possible while sorting,
 * but weird data may be returned, e.g. duplicate rows.
 *
 * @author  kdg
 */
public class ManagedTableSorter extends TableSorter {
    
    private final WorkManager workManager;
    private final Object lock = new Object();
    
    /** Creates a new instance of ManagedTableSorter */
    public ManagedTableSorter(WorkManager workManager) {
        this.workManager = workManager;
    }
    
    public void  sort(final Object sender) {
        workManager.execute(new SortWork(sender));
    }
    
    public void setModel(TableModel model) {
        synchronized (lock) {
            super.setModel(model);
        }
    }
    
    public void setValueAt(Object aValue, int aRow, int aColumn) {
        synchronized (lock) {
            super.setValueAt(aValue, aRow, aColumn);
        }
    }
    
    private void superSort(Object sender) {
        super.sort(sender);
    }
    
    public void sortByColumn(int column, boolean ascending) {
        sortByColumnNoNotify(column, ascending);
        // Only notify after sorting is done.
    }
    
    
    private class SortWork extends WorkAdapter {
        private final Object sender;
        
        public SortWork(Object sender) {
            this.sender = sender;
            setDescription("Sorting...");
            setAbortable(false);
            setPausable(false);
        }
        
        public void execute() {
            synchronized (lock) {
                superSort(sender);
            }
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        fireTableChanged(new TableModelEvent(ManagedTableSorter.this, 0, getRowCount() - 1));
                    }
                });
            } catch (InterruptedException ie) {
                System.err.println("Interrupted while trying to fire a table-changed event:");
                ie.printStackTrace();
            } catch (InvocationTargetException ite) {
                System.err.println("Invocation exception while trying to fire a table-changed event:");
                ite.printStackTrace();
            }
        }
    }
}
