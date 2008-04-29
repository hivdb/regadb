/*
 * SelectionStatusList.java
 *
 * Created on September 5, 2003, 11:38 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.io.Serializable;
import java.util.*;

import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;

/**
 *
 * @author  kristof
 * <p>
 * This class supports xml-encoding.
 * The following new properties are encoded :
 *  query
 *  selections
 * </p>
 */
public class SelectionStatusList implements SelectionList, Serializable {
    
    private Query query;
    private List<Selection> selections = new ArrayList<Selection>(); // of type Selection
    private transient List<SelectionChangeListener> changeListeners = new ArrayList<SelectionChangeListener>();
    
    /* for xml-encoding purposes only */
    public Query getQuery() {
        return query;
    }
    
    /* for xml-encoding purposes only */
    public void setQuery(Query query) {
        this.query = query;
    }
    
    /* for xml-encoding purposes only */
    public SelectionStatusList() {
    }
    
    /** Creates a new instance of SelectionStatusList */
    public SelectionStatusList(Query query) {
        this.query = query;
        Iterator<OutputVariable> iter = query.getRootClause().getExportedOutputVariables().iterator();
        while (iter.hasNext()) {
            OutputVariable ovar = (OutputVariable)iter.next();
            addVariable(ovar);
        }
    }
    
    public String accept(QueryVisitor visitor) {
    	return visitor.visitSelectionSatusList(this);
    }
    
    // %$ KVB : It is essential that this method returns column names in the same order they are appended in getSelectClause !
    public List<String> getSelectedColumnNames() {
        ArrayList<String> selectedColumns = new ArrayList<String>();
        Iterator<Selection> iter = getSelections().iterator();
        while (iter.hasNext()) {
            Selection selection = (Selection)iter.next();
            if (selection.isSelected()) {
                OutputVariable var = (OutputVariable)selection.getObject();
                String varName = var.getUniqueName();
                if (selection instanceof TableSelection) {
                    Iterator<Selection> fieldIter = ((TableSelection)selection).getSubSelections().iterator();
                    int selectedColumnCount = 0;
                    while (fieldIter.hasNext()) {
                        FieldSelection subSelection = (FieldSelection)fieldIter.next();
                        if (subSelection.isSelected()) {
                            selectedColumns.add(var.getFullColumnName((Field)(subSelection.getObject())));
                            selectedColumnCount++;
                        }
                    }
                    if (selectedColumnCount == 0 && DatabaseManager.getInstance().getDatabaseConnector().isTableSelectionAllowed()) {
                    	String name = var.getUniqueName();
                    	selectedColumns.add(name);
                    }
                }
                else { // selection instanceof OutputSelection
                    selectedColumns.add(varName);
                }
            }
        }
        return selectedColumns;
    }
    
    public List<Selection> getSelections() {
        return selections;
    }
    
    /**
     * For XMLdecoding only!
     */
    public void setSelections(List<Selection> selections) {
        this.selections = selections;
    }
    
    public boolean isSelected(OutputVariable ovar) {
        return find(ovar).isSelected();
    }
    
    public void setSelected(OutputVariable ovar, boolean selected) {
        Selection s = find(ovar);
        if (s.isSelected() != selected) {
            s.setSelected(selected);
            notifySelectionChangeListeners();
        }
    }
    
    public boolean isSelected(OutputVariable ovar, Field field) {
        return ((ComposedSelection)find(ovar)).find(field).isSelected();
    }
    
    public void setSelected(OutputVariable ovar, Field field, boolean selected) {
        ComposedSelection cs = ((ComposedSelection)find(ovar));
        Selection fs = cs.find(field);
        //System.out.println(property.getName() + " " + property.getOutputVariable());
        if (fs.isSelected() != selected) {
            fs.setSelected(selected);
            notifySelectionChangeListeners();
        }
    }
    
    public boolean isAnythingSelected() {
        Iterator<Selection> iter = selections.iterator();
        while (iter.hasNext()) {
            Selection selection = (Selection)iter.next();
            if (selection.isSelected()) {
                if (selection instanceof TableSelection) {
                    Iterator<Selection> fieldIter = ((TableSelection)selection).getSubSelections().iterator();
                    while (fieldIter.hasNext()) {
                        FieldSelection subSelection = (FieldSelection)fieldIter.next();
                        if (subSelection.isSelected()) {
                            return true;
                        }
                    }
                    // only table selected
                   if (DatabaseManager.getInstance().getDatabaseConnector().isTableSelectionAllowed()) {
                    	return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void addVariable(OutputVariable ovar) {
        Selection selection = find(ovar);
        if (selection == null) {
            selection = (ovar.consistsOfSingleFromVariable() ? (Selection)(new TableSelection(ovar, true)) : (Selection)(new OutputSelection(ovar, true)));
            selection.setController(this);
            selections.add(selection);
        }
    }
    
    // do not use this with selectList == this.selections
    private void addOrCopyVariableTo(OutputVariable ovar, Collection<Selection> selectList) {
        Selection selection = find(ovar);
        if (selection == null) {
            selection = (ovar.consistsOfSingleFromVariable() ? (Selection)(new TableSelection(ovar, true)) : (Selection)(new OutputSelection(ovar, true)));
            selection.setController(this);
        } else {
            //System.out.println("Making a copy !");
        }
        selectList.add(selection);
    }
    
    // not used currently, but maybe some day
    private void removeVariable(OutputVariable ovar) {
        selections.remove(find(ovar));
    }
    
    public Selection find(OutputVariable ovar) {
        Iterator<Selection> iter = selections.iterator();
        while (iter.hasNext()) {
            Selection selection = (Selection)iter.next();
            if (selection.getObject() == ovar) {
                return selection;
            }
        }
        return null;
    }
    
    // to be called whenever the list of OutputVariables from the master query changes
    public void update() {
        List<Selection> newSelections = new ArrayList<Selection>();
        Iterator<OutputVariable> iter = query.getRootClause().getExportedOutputVariables().iterator();
        while (iter.hasNext()) {
            OutputVariable ovar = iter.next();
            addOrCopyVariableTo(ovar, newSelections);
        }
        selections = newSelections;
    }
    
    /**
     * A listeners for changes of this selection list only.
     * Changes do NOT include updates, or SelectionList replacements.
     * For these events, listen to SelectionListEvents in the QueryEditor controller.
     *
     * It is highly recommended to unsubscribe from the old list whenever the Selection list is replaced.
     */
    public void addSelectionChangeListener(SelectionChangeListener listener) {
    	if (changeListeners == null) {
    		changeListeners	= new ArrayList<SelectionChangeListener>();    		
    	}
        changeListeners.add(listener);
    }
    
    public void removeSelectionChangeListener(SelectionChangeListener listener) {
        changeListeners.remove(listener);
    }
    
    private void notifySelectionChangeListeners() {
        Iterator<SelectionChangeListener> iter = changeListeners.iterator();
        while (iter.hasNext()) {
            SelectionChangeListener listener = iter.next();
            listener.selectionChanged();
        }
    }
    
}
