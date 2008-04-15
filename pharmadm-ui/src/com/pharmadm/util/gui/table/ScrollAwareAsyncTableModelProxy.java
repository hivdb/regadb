/*
 * SlowTableModelProxy.java
 *
 * Created on June 16, 2005, 9:55 AM
 *
 * (C) PharmaDM n.v.  All rights reserved.
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.gui.table;

import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.pharmadm.util.SimpleTimer;

/**
 * A proxy TableModel that avoids GUI hangups by providing placeholder values first,
 * retrieving the real values in the background, and firing a TableModelEvent to anounce new data.
 * If retrieval takes too long, events will be fired when partial data is available.
 * This proxy listens to changes to the viewport to know what rows are displayed, and only retrieves
 * those rows.
 *
 * This TableModel may only be used in the JTable that is the View of the Viewport
 * that is provided in the constructor.
 *
 * This proxy works best if the backing model's getValueAt method is too slow to hang the GUI thread painting a JTable,
 * but it has approximately random access characteristics, i.e. every cell access takes about the same amount of time.
 * Other methods of the backing model should be fast.
 *
 * This proxy might be incompatible with e.g. table sorters, since they might
 * get confused by the frequent model's data changes and by the model forgetting
 * anything beyond what's seen on the screen.
 *
 * The backing TableModel may not announce any changes outside of the GUI thread.
 *
 * Evample usage:
 *
 * <code>
 * TableModel slowModel = new RemoteServerFetchTableModel();
 * JScrollPane scrollPane = new JScrollPane();
 * JTable table = new JTable();
 * scrollPane.setViewportView(table);
 * TableModel proxy = new ScrollAwareAsyncTableModelProxy(slowModel, scrollPane.getViewport();
 * </code>
 *
 * @pattern proxy
 *
 * @author kdg
 */
public class ScrollAwareAsyncTableModelProxy extends AbstractTableModel {
    
    private TableModel backingModel;
    
    private int offset = -1;
    private Object[][] visibleData = new Object[0][];
    private RealValuesLookup lookup;
    
    // Since the executor doesn't get shut down, it is not permitted to have a nonzero core pool.
    private Executor executor = new ThreadPoolExecutor(0, 100, 2000, TimeUnit.MILLISECONDS, new SynchronousQueue());
    
    private final Object PLACEHOLDER = new Object() {
        public String toString() {
            return "";
        }
    };
    
    // Represent nulls in the cache.
    private final Object BACKING_NULL = new Object();
    
    private int updateTreshold = 300;
    
    private JTable jTable;
    private JViewport viewport;
    
    /**
     * Creates a new ScrollAwareAsyncTableModelProxy showing the data of the backingModel,
     * and sets it as the model of the viewport's JTable.
     * This TableModel may only be used in the JTable that is the View of the provided Viewport.
     *
     * @pre viewport != null
     * @pre slowModel != null
     * @pre viewport.getView() != null
     * @pre viewport.getView() instanceof JTable
     * @pre slowModel is threadsafe
     *
     * @param slowModel the slow tablemodel that the new instance is a fast proxy for
     * @param viewport the viewport that contains the JTable
     */
    public ScrollAwareAsyncTableModelProxy(TableModel slowModel, JViewport viewport) {
        this.backingModel = slowModel;
        this.viewport = viewport;
        this.jTable = (JTable)viewport.getView();
        backingModel.addTableModelListener(new BackingModelListener());
        viewport.addChangeListener(new ViewportChangeListener());
        jTable.setModel(this);
        viewportMovedOrResized();
    }
    
    public TableModel getBackingModel() {
        return backingModel;
    }
    
    public int getColumnCount() {
        return backingModel.getColumnCount();
    }
    
    public int getRowCount() {
        return backingModel.getRowCount();
    }
    
    public String getColumnName(int columnIndex) {
        return backingModel.getColumnName(columnIndex);
    }
    
    public Class getColumnClass(int columnIndex) {
        return backingModel.getColumnClass(columnIndex);
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return backingModel.isCellEditable(rowIndex, columnIndex);
    }
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        backingModel.setValueAt(aValue, rowIndex, columnIndex);
    }
    
    /**
     * @return either the same value as the underlying backing tablemodel would give, or a placeholder.
     *         If a placeholder is returned, a TableModelEvent will be issued to listeners when new real values are available.
     */
    public Object getValueAt(int row, int column) {
        int visibleRowIndex = row - offset;
        if (visibleRowIndex >= 0 && visibleRowIndex < visibleData.length) {
            Object[] values = null;
            synchronized (visibleData) {
                values = visibleData[visibleRowIndex];
            }
            if (values != null) {
                Object value = values[column];
                if (value == BACKING_NULL) {
                    return null;
                } else {
                    return value;
                }
            }
        }
        return PLACEHOLDER;
    }
    
    /**
     * Gets the number of milliseconds that may be spent looking up more values
     * from the backing tablemodel before updating the GUI.
     */
    public int getUpdateTreshold() {
        return updateTreshold;
    }
    
    /**
     * Sets the number of milliseconds that may be spent looking up more values
     * from the backing tablemodel before updating the GUI. Setting it to zero
     * will cause the GUI to be updated for every row, unless the retrieval of
     * a row from the backing tablemodel is faster than repainting the GUI.
     *
     * @pre updateTreshold >= 0
     */
    public void setUpdateTreshold(int updateTreshold) {
        this.updateTreshold = updateTreshold;
    }
    
    /**
     * Distinguishes real values from temporary placeholder values that will be
     * replaced with real values after they have been calculated.  E.g., using this
     * method, a JTable can render cells containing placeholders differently than
     * a regular Object with an empty toString.
     */
    public boolean isPlaceHolder(Object o) {
        return o == PLACEHOLDER;
    }
    
    private void viewportMovedOrResized() {
        Rectangle visibleRect = viewport.getViewRect();
        Point topPoint = viewport.getViewPosition();
        int topY = topPoint.y;
        Point bottomPoint = new Point(0, topY + viewport.getExtentSize().height-1);
        int newOffset = jTable.rowAtPoint(topPoint);
        if (newOffset == -1) {
            newOffset = 0;
        }
        int newLastRow = jTable.rowAtPoint(bottomPoint);
        if (newLastRow == -1) {
            newLastRow = backingModel.getRowCount() - 1;
        }
        int newExtent = (1 + newLastRow - newOffset);
        if ((newOffset != offset) || (newExtent != visibleData.length)) {
            boolean allDataAvailable = (newExtent == 0);
            int extent = visibleData.length;
            Object[][] newVisibleData = new Object[newExtent][];
            if ((newOffset < (offset + extent)) && ((newOffset + newExtent) > offset)) {  // ensure overlap exists
                int srcPos = Math.max(0, (newOffset - offset));
                int destPos = Math.max(0, (offset - newOffset));
                int copyExtent = Math.min((offset + extent), (newOffset + newExtent)) - Math.max(offset, newOffset);
                System.arraycopy(visibleData, srcPos, newVisibleData, destPos, copyExtent);
                allDataAvailable = (copyExtent == newExtent);
                if (allDataAvailable) {
                    for (Object[]  data : newVisibleData) {
                        if (data == null) {
                            allDataAvailable = false;
                        }
                    }
                }
            }
            if (!allDataAvailable) {
                if (lookup != null) {
                    lookup.stopAndWaitFor();
                }
                offset = newOffset;
                visibleData = newVisibleData;
                lookup = new RealValuesLookup(visibleData, offset);
                executor.execute(lookup);
            } else {
                offset = newOffset;
                visibleData = newVisibleData;
            }
        }
    }
    
    
    // Prefer (no guarantee) to have no more than one event outstanding at a time.
    // (Maybe superfluous events get AWT-coalesced anyway, don't know.
    //  Should be investigated...)
    private volatile boolean fireBurning = false;
    private void fireTableDataChangedLater() {
        if (!fireBurning) {
            fireBurning = true;
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    fireBurning = false;
                    fireTableRowsUpdated(0, getRowCount());
                    // With some more synchronization, it might be possible to only throw an event like this:
                    // fireTableRowsUpdated(firstRow, lastRow);
                    // In the current implementation, under some circumstances, it may result in placeholders
                    // not being replaced with real values.
                }
            });
        }
    }
    
    private class RealValuesLookup implements Runnable {
        private Semaphore semaphore = new Semaphore(0);
        private volatile boolean stop;
        private final Object[][] visibleData;
        private final int offset;
        
        public RealValuesLookup(Object[][] visibleData, int offset) {
            this.visibleData = visibleData;
            this.offset = offset;
        }
        
        public void run() {
            // System.out.println("new runnable using thread " + Thread.currentThread().getId());
            SimpleTimer lookupTimer = new SimpleTimer();
            boolean dataChanged = false;
            for (int i = 0; (i<visibleData.length) && (!stop); i++) {
                int row = offset + i;
                if (visibleData[i] == null) {
                    lookupTimer.start();
                    int colCount = getColumnCount();
                    Object[] values = new Object[colCount];
                    for (int c = 0; c < colCount; c++) {
                        Object value = null;
                        try {
                            value = backingModel.getValueAt(row, c);
                        } catch (Exception e) {
                            e.printStackTrace();
                            value = BACKING_NULL;
                        }
                        if (value == null) {
                            value = BACKING_NULL;
                        }
                        values[c] = value;
                    }
                    // the synchronize is required to make the values visible to the working memory
                    // of the event dispatch thread (in the getValueAt method)
                    synchronized (visibleData) {  
                        visibleData[i] = values;
                    }
                    if (!stop) {
                        dataChanged = true;
                        lookupTimer.stop();
                        if (lookupTimer.getTotalElapsedMilliSeconds() > getUpdateTreshold()) {
                            fireTableDataChangedLater();
                            dataChanged = false;
                            lookupTimer.reset();
                        }
                    }
                }
            }
            if (dataChanged) {
                fireTableDataChangedLater();
            }
            semaphore.release();
        }
        
        public void stopAndWaitFor() {
            stop = true;
            try {
                semaphore.acquire();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
    
    // This could be made a lot smarter, keeping cached rows that are not changed.
    private class BackingModelListener implements TableModelListener {
        public void tableChanged(TableModelEvent e) {
            switch (e.getType()) {
                case TableModelEvent.INSERT:
                case TableModelEvent.DELETE:
                case TableModelEvent.UPDATE:
                    if (lookup != null) {
                        lookup.stopAndWaitFor();
                    }
                    visibleData = new Object[0][];
                    viewportMovedOrResized();
                    break;
                default:
                    fireTableChanged(e);
            }
        }
    }
    
    private class ViewportChangeListener implements ChangeListener {
        public void stateChanged(javax.swing.event.ChangeEvent changeEvent) {
            viewportMovedOrResized();
        }
    }
    
}
