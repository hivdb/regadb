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
import com.pharmadm.util.SimpleTimer;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * A Proxy TableModel that avoids GUI hangups by providing placeholder values
 * if the backing model answers too slowly. The real values are then fetched
 * in the background and the model is updated.  The backing model must have fast a implementation
 * for getRowCount and getColumnCount, because delays in those will be exposed to the GUI.
 *
 * This proxy might be incompatible with e.g. table sorters, since they might
 * get confused by the frequent model's data changes and by the model forgetting
 * anything (without any notification of change) beyond it's cache horizon.
 *
 * In principle, the underlying table model's data can change if you're lucky, but this is NOT tested.
 * Some aspects from the meta model, mainly the number of columns, are known to be unsafe to change.
 *
 * @pattern proxy
 *
 * @author kdg
 */
public class AsynchronousTableModelProxy extends AbstractTableModel {
    
    private TableModel backingModel;
    
    private SortedSet<Integer> placeholders = new TreeSet();
    private volatile boolean lookupOngoing = false;
    private final Cache<Integer, List> cache;
    
    private final Object PLACEHOLDER = new Object() {
        public String toString() {
            return "";
        }
    };
    
    
    // Lock to signal thread stops
    private final Object LOOKUP_LOCK = new Object();
    
    // Represent nulls in the cache.
    private final Object BACKING_NULL = new Object();
    
    // milliseconds that may at max be spend looking up more values before updating the GUI
    // must be strictly larger than zero
    private int updateTreshold = 300;
    
    /**
     * Creates a new SlowTableModelProxy with the given cache size, showing the data of the backingModel.
     *
     * The backing model's getValueAt method should be too slow to hang the GUI thread painting a JTable,
     * but it should have random access characteritics, i.e. every cell access takes about the same amount of time.
     *
     * The cache size must satisfy a number of constraints, see setCacheSize().
     *
     * @see setCacheSize
     *
     * @pre cacheSize large enough
     * @pre slowModel is threadsafe (it will receive calls both from the GUI and from a separate cell value fetch thread)
     */
    public AsynchronousTableModelProxy(TableModel slowModel, int cacheSize) {
        this.backingModel = slowModel;
        this.cache = new Cache(cacheSize);
        backingModel.addTableModelListener(new BackingModelListener());
    }
    
    public TableModel getBackingModel() {
        return backingModel;
    }
    
    public int getCacheSize() {
        return cache.getCapacity();
    }
    
    /**
     * Sets the size of the cache, measured in the number of rows to be stored.
     * The cache size must be large enough to contain all rows visible in the GUI at any single point in time.
     * Keep in mind that you cannot change the cache size, so make it large enough to support
     * a maximize of your table on a very-high-resolution screen.  Don't make the cache way too large either,
     * because it has superlinear time complexity.
     *
     * WARNING: if the cache is smaller than the number of rows visible in the JTable,
     *          an infinite repaint loop will result.
     */
    public void setCacheSize(int size) {
        cache.setCapacity(size);
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
        Integer rowInt = new Integer(row);
        Object value = null;
        List values = cache.get(rowInt);
        if (values != null) {
            value = values.get(column);
            if (value == BACKING_NULL) {
                return null;
            } else {
                return value;
            }
        }
        lookupRealValues(rowInt);
        value = PLACEHOLDER;
        return value;
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
    
    private void lookupRealValues(Integer rowInt) {
        synchronized (LOOKUP_LOCK) {
            if (placeholders.size() > cache.getCapacity()) {
                // This occurs when the user quickly scrolls over more rows that we have cache for.
                // Avoid to fetch too many rows that are not visible anymore and that would trash the cache.
                // Clear the entire queue and start over with the now-visible rows.
                placeholders.clear();
                fireTableDataChanged();
            } else {
                if (placeholders.add(rowInt)) {
                    if (!lookupOngoing) {
                        lookupOngoing = true;
                        Thread t = new Thread(new RealValuesLookup());
                        t.setPriority(Thread.MIN_PRIORITY);
                        t.setName("Table value lookup");
                        t.start();
                    }
                }
            }
        }
    }
    
    private Integer pollNextPlaceholder() {
        synchronized (LOOKUP_LOCK) {
            if (placeholders.isEmpty()) {
                lookupOngoing = false;
                return null;
            } else {
                Integer firstRow = placeholders.first();
                placeholders.remove(firstRow);
                return firstRow;
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
                    // Alas, it is not enough to just fire the event below,
                    // for under some circumstances, it may result in placeholders
                    // not being replaced with real values.
                    // fireTableRowsUpdated(firstRow, lastRow);
                }
            });
        }
    }
    
    private class RealValuesLookup implements Runnable {
        /**
         * @pre the placeholders queue is not empty
         * @pre nobody else gets placeholders off the queue
         */
        public void run() {
            SimpleTimer lookupTimer = new SimpleTimer();
            Integer rowInt;
            synchronized (LOOKUP_LOCK) {
                rowInt = placeholders.first();  // first time always non-null
            }
            boolean dataChanged = false;
            while (rowInt != null) {
                lookupTimer.start();
                int row = rowInt;
                int colCount = getColumnCount();
                List values = new ArrayList(colCount);
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
                    values.add(value);
                }
                cache.put(rowInt, values);
                dataChanged = true;
                lookupTimer.stop();
                if (lookupTimer.getTotalElapsedMilliSeconds() > updateTreshold) {
                    fireTableDataChangedLater();
                    dataChanged = false;
                    lookupTimer.reset();
                }
                rowInt = pollNextPlaceholder();
            }
            if (dataChanged) {
                fireTableDataChangedLater();
            }
        }
    }
    
    private static class Cache<K, V> {
        private final SortedMap<K, V> map = new TreeMap();
        private final LinkedHashSet<K> recentlyAccessed = new LinkedHashSet();
        private int cacheSize;
        
        /**
         * @pre cacheSize > 0
         */
        public Cache(int cacheSize) {
            this.cacheSize = cacheSize;
        }
        
        public synchronized V get(K key) {
            recentlyAccessed.add(key);
            if (recentlyAccessed.size() > cacheSize) {
                Iterator iter = recentlyAccessed.iterator();
                if (iter.hasNext()) {
                    iter.next();
                    iter.remove();
                }
            }
            return map.get(key);
        }
        
        public synchronized void put(K key, V value) {
            if (!map.containsKey(key)) {
                map.put(key, value);
                map.keySet().retainAll(recentlyAccessed);
            } else {
                map.put(key, value);
            }
        }
        
        /**
         * Evicts all values for keys from fromKey (inclusive) to toKey (exclusive)
         * from this cache.
         */
        public synchronized void evict(K fromKey, K toKey) {
            SortedMap submap = map.subMap(fromKey, toKey);
            submap.clear();
        }
        
        public synchronized void clear() {
            map.clear();
            recentlyAccessed.clear();
        }
        
        public int getCapacity() {
            return cacheSize;
        }
        
        public synchronized void setCapacity(int newCapacity) {
            int excess = recentlyAccessed.size() - newCapacity;
            Iterator<K> iter = recentlyAccessed.iterator();
            while (excess > 0) {
                K key = iter.next();
                iter.remove();
                map.remove(key);
                excess--;
            }
            cacheSize = newCapacity;
        }
    }
    
    private class BackingModelListener implements TableModelListener {
        public void tableChanged(TableModelEvent e) {
            switch (e.getType()) {
                case TableModelEvent.INSERT:
                case TableModelEvent.DELETE:
                    cache.clear();
                    break;
                case TableModelEvent.UPDATE:
                    cache.evict(e.getFirstRow(), (e.getLastRow() + 1));
                    break;
            }
            // propagate event
            fireTableChanged(e);
        }
    }
}
