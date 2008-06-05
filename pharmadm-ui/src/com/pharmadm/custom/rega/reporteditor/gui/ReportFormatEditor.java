/*
 * ReportFormatEditor.java
 *
 * Created on November 28, 2003, 12:45 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor.gui;

import java.io.*;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.*;

import com.pharmadm.custom.rega.queryeditor.*;
import com.pharmadm.custom.rega.queryeditor.persist.DataGroupPersistenceDelegate;
import com.pharmadm.custom.rega.queryeditor.persist.FilePersistenceDelegate;
import com.pharmadm.custom.rega.queryeditor.persist.ObjectListVariablePersistenceDelegate;
import com.pharmadm.custom.rega.queryeditor.persist.SelectionPersistenceDelegate;
import com.pharmadm.custom.rega.reporteditor.DataGroup;
import com.pharmadm.custom.rega.reporteditor.DataGroupPrototypeCatalog;
import com.pharmadm.custom.rega.reporteditor.DataOutputListSelection;
import com.pharmadm.custom.rega.reporteditor.DataOutputSelection;
import com.pharmadm.custom.rega.reporteditor.ObjectListVariable;
import com.pharmadm.custom.rega.reporteditor.POJOListSelection;
import com.pharmadm.custom.rega.reporteditor.POJOSelection;
import com.pharmadm.custom.rega.reporteditor.PropertySelection;
import com.pharmadm.custom.rega.reporteditor.ReportFormat;
import com.pharmadm.custom.rega.savable.*;

/**
 *
 * @author  kristof
 */

/**
 * <p>
 * The controller ('Controller' pattern) for editing a ReportFormat.
 * </p>
 *
 */
public class ReportFormatEditor extends AbstractListModel implements Savable {
    
    private ReportFormat format;
    private DataGroup dummyGroup; // dummy "sentinel" group to allow for user-friendly inserting
    private boolean dirty = false;
    private final List dirtinessListeners = new ArrayList();
    private Collection selectionListChangeListeners = new ArrayList();
    
    private final SelectionChangeListener selectionChangeListener = new SelectionChangeListener() {
        public void selectionChanged() {
            setDirty(true);
        }
    };
    
    /** Creates a new instance of ReportFormatEditor */
    public ReportFormatEditor(ReportFormat format) {
        this.format = format;
        this.dummyGroup = DataGroup.dummyGroup(format);
        addDirtinessListenerToFormat();
        //updateSelectionList();
    }
    
    private void addDirtinessListenerToFormat() {
        format.getSelectionList().addSelectionChangeListener(selectionChangeListener);
    }
    
    private void removeDirtinessListenerFromFormat() {
        format.getSelectionList().removeSelectionChangeListener(selectionChangeListener);
    }
    
    /* implementing abstract method of AbstractListModel */
    public Object getElementAt(int index) {
        if (index == getSize() - 1) {
            return dummyGroup;
        } else {
            return getReportFormat().getDataGroups().get(index);
        }
    }
    
    /* implementing abstract method of AbstractListModel */
    public int getSize() {
        return getReportFormat().getDataGroups().size() + 1;
    }
    
    ///////////////////////////////////////
    // access methods for associations
    
    public ReportFormat getReportFormat() {
        return format;
    }
    
    // assume the given format has been saved somewhere else (not dirty)
    public void setReportFormat(ReportFormat format) {
        if (this.format != null) {
            removeDirtinessListenerFromFormat();
        }
        this.format = format;
        this.dummyGroup = DataGroup.dummyGroup(format);
        addDirtinessListenerToFormat();
        setDirty(false);
        fireContentsChanged(this, 0, getSize() - 1);
        updateSelectionList();
    }
    
    public void updateSelectionList() {
        getReportFormat().getSelectionList().update();
        notifySelectionListChangeListeners();
    }
    
    public DataGroupPrototypeCatalog getDataGroupPrototypeCatalog() {
        return DataGroupPrototypeCatalog.getInstance();
    }
    
    public void createNewFormat() {
        setReportFormat(new ReportFormat());
        setDirty(false);
    }
    
    
    public void addDataGroup(DataGroup dataGroup) {
        getReportFormat().addDataGroup(dataGroup);
        setDirty(true);
        fireIntervalAdded(this, getSize() - 1, getSize() - 1);
        updateSelectionList();
    }
    
    public void addDataGroup(DataGroup dataGroup, int index) {
        getReportFormat().addDataGroup(dataGroup, index);
        setDirty(true);
        fireIntervalAdded(this, index, index);
        updateSelectionList();
    }
    
    public void removeDataGroup(DataGroup dataGroup, int index) {
        if (index < getSize() - 1) {
            getReportFormat().removeDataGroup(dataGroup, index);
            setDirty(true);
            fireIntervalRemoved(this, index, index);
            updateSelectionList();
        }
    }
    
    public Collection getAvailableDataGroupPrototypes(int index) {
        Collection prototypes = getDataGroupPrototypeCatalog().getDataGroupPrototypes(getReportFormat().getOutputVariablesAvailableForImport(index));
        Collection clones = new ArrayList(prototypes.size());
        Iterator iterPrototypes = prototypes.iterator();
        try {
            while (iterPrototypes.hasNext()) {
                DataGroup prototype = (DataGroup)iterPrototypes.next();
                clones.add(prototype.clone());
            }
        } catch (CloneNotSupportedException cnse) {
            System.err.println();
            cnse.printStackTrace();
        }
        return clones;
    }
    
    private void installPersistenceDelegates(java.beans.XMLEncoder encoder) {
        encoder.setPersistenceDelegate(File.class, new FilePersistenceDelegate());
        encoder.setPersistenceDelegate(ObjectListVariable.class, new ObjectListVariablePersistenceDelegate());
        encoder.setPersistenceDelegate(DataGroup.class, new DataGroupPersistenceDelegate());
        encoder.setPersistenceDelegate(PropertySelection.class, new SelectionPersistenceDelegate());
        encoder.setPersistenceDelegate(DataOutputSelection.class, new SelectionPersistenceDelegate());
        encoder.setPersistenceDelegate(POJOSelection.class, new SelectionPersistenceDelegate());
        encoder.setPersistenceDelegate(DataOutputListSelection.class, new SelectionPersistenceDelegate());
        encoder.setPersistenceDelegate(POJOListSelection.class, new SelectionPersistenceDelegate());
    }
    
    private void setDirty(boolean dirty) {
        if (this.dirty != dirty) {
            this.dirty = dirty;
            Iterator dLIter = dirtinessListeners.iterator();
            DirtinessEvent de = new DirtinessEvent(this);
            while (dLIter.hasNext()) {
                ((DirtinessListener)dLIter.next()).dirtinessChanged(de);
            }
        }
    }
    
    /**
     * Whether the report format contains unsaved information.
     */
    public boolean isDirty() {
        return dirty;
    }
    
    public void saveReportFormat(File file) throws java.io.FileNotFoundException {
        java.beans.XMLEncoder encoder = new java.beans.XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)));
        installPersistenceDelegates(encoder);
        encoder.writeObject(format);
        encoder.close();
        setDirty(false);
        //debug code
        System.gc();
        System.gc();
        System.gc();
        long freeMem = Runtime.getRuntime().freeMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        System.err.println("Memory after save: " + (totalMem - freeMem));
    }
    
    public void loadReportFormat(File file) throws java.io.FileNotFoundException {
        java.beans.XMLDecoder decoder = new java.beans.XMLDecoder(new BufferedInputStream(new FileInputStream(file)));
        setReportFormat((ReportFormat)decoder.readObject());
        getReportFormat().getUniqueNameContext().assignUniqueNamesToAll(getReportFormat());
        notifySelectionListChangeListeners();
        setDirty(false);
        // debug code
        System.gc(); // paper + KGA
        System.gc(); // PMD + GFT
        System.gc(); // rest garbage
        long freeMem = Runtime.getRuntime().freeMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        System.err.println("Memory after load: " + (totalMem - freeMem));
    }
    
    public void addDirtinessListener(DirtinessListener listener) {
        dirtinessListeners.add(listener);
    }
    
    
    private void notifySelectionListChangeListeners() {
        Iterator iter = selectionListChangeListeners.iterator();
        while (iter.hasNext()) {
            SelectionListChangeListener listener = (SelectionListChangeListener)iter.next();
            listener.listChanged();
        }
    }
    
    public void addSelectionListChangeListener(SelectionListChangeListener listener) {
        selectionListChangeListeners.add(listener);
    }
    
    public void load(Object file) throws IOException {
        loadReportFormat((File) file);
    }
    
    public void save(Object file) throws IOException {
        saveReportFormat((File) file);
    }

	public boolean isLoaded() {
		return true;
	}
    
}
