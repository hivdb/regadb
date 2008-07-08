/*
 * DataSelectionList.java
 *
 * Created on September 5, 2003, 11:38 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

import java.util.*;
import com.pharmadm.custom.rega.queryeditor.*;

/**
 *
 * @author  kristof
 * <p>
 * This class supports xml-encoding.
 * The following new properties are encoded :
 *  reportFormat
 *  selections
 * </p>
 */

public class DataSelectionList implements SelectionList {
    
    // a lot of this implementation is stolen from SelectionStatusList
    // again, a common superclass (SelectionList ?) might be a good idea
    
    private ReportFormat reportFormat;
    private List<Selection> selections = new ArrayList(); // of type Selection
    private Collection selectionChangeListeners = new ArrayList();
    
    /** for xml-encoding purposes only */
    public DataSelectionList() {
    }
    
    /** Creates a new instance of DataSelectionList */
    public DataSelectionList(ReportFormat reportFormat) {
        this.reportFormat = reportFormat;
        Iterator iter = reportFormat.getDataOutputVariables().iterator();
        while (iter.hasNext()) {
            DataOutputVariable ovar = (DataOutputVariable)iter.next();
            addVariable(ovar);
        }
    }
    
    /** for xml-encoding purposes only */
    public ReportFormat getReportFormat() {
        return reportFormat;
    }
    
    /** for xml-encoding purposes only */
    public void setReportFormat(ReportFormat reportFormat) {
        this.reportFormat = reportFormat;
    }
    
    public List<Selection> getSelections() {
        return selections;
    }
    
    /**
     * For XMLdecoding only!
     */
    public void setSelections(List selections) {
        this.selections = selections;
    }
    
    public boolean isSelected(DataOutputVariable ovar) {
        return find(ovar).isSelected();
    }
    
    public void setSelected(DataOutputVariable ovar, boolean selected) {
        Selection s = find(ovar);
        if (s.isSelected() != selected) {
            s.setSelected(selected);
            notifySelectionChangeListeners();
        }
    }
    
    public boolean isSelected(DataOutputVariable ovar, Property property) {
        return ((ComposedSelection)find(ovar)).find(property).isSelected();
    }
    
    public void setSelected(DataOutputVariable ovar, Property property, boolean selected) {
        ComposedSelection cs = ((ComposedSelection)find(ovar));
        Selection fs = cs.find(property);
        //System.out.println(property.getName() + " " + property.getOutputVariable());
        if (fs.isSelected() != selected) {
            fs.setSelected(selected);
            notifySelectionChangeListeners();
        }
    }
    
    public boolean isAnythingSelected() {
        Iterator iter = selections.iterator();
        while (iter.hasNext()) {
            Selection selection = (Selection)iter.next();
            if (selection.isSelected()) {
                if (selection instanceof ComposedSelection) {
                    Iterator propertyIter = ((ComposedSelection)selection).getSubSelections().iterator();
                    while (propertyIter.hasNext()) {
                        SimpleSelection subSelection = (SimpleSelection)propertyIter.next();
                        if (subSelection.isSelected()) {
                            return true;
                        }
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }
    
    public List getSelectedColumns() {
        ArrayList selectedColumns = new ArrayList();
        Iterator iter = getSelections().iterator();
        while (iter.hasNext()) {
            Selection selection = (Selection)iter.next();
            if (selection.isSelected()) {
                DataOutputVariable var = (DataOutputVariable)selection.getObject();
                if (selection instanceof POJOSelection) {
                    Iterator propIter = ((POJOSelection)selection).getSubSelections().iterator();
                    while (propIter.hasNext()) {
                        PropertySelection subSelection = (PropertySelection)propIter.next();
                        if (subSelection.isSelected()) {
                            selectedColumns.add(subSelection.getObject());
                        }
                    }
                } else if (selection instanceof POJOListSelection) {
                    // this selection applies to all elements of a list (of any size) of DataOutputVariables;
                    // appropriate properties are created for all of these elements at this time
                    Iterator elemIter = ((ListDataOutputVariable)selection.getObject()).getIterator();
                    while (elemIter.hasNext()) {
                        ElementDataOutputVariable edovar = (ElementDataOutputVariable)elemIter.next();
                        Iterator propIter = ((POJOListSelection)selection).getSubSelections().iterator();
                        while (propIter.hasNext()) {
                            PropertySelection subSelection = (PropertySelection)propIter.next();
                            if (subSelection.isSelected()) {
                                Property propertyCopy = ((Property)((Property)subSelection.getObject()).clone());
                                propertyCopy.switchOutputVariable(edovar);
                                selectedColumns.add(propertyCopy);
                            }
                        }
                    }
                } else if (selection instanceof DataOutputListSelection) {
                    // this selection applies to all elements of a list (of any size) of DataOutputVariables;
                    Iterator elemIter = ((ListDataOutputVariable)selection.getObject()).getIterator();
                    while (elemIter.hasNext()) {
                        selectedColumns.add(elemIter.next());
                    }
                } else { // selection instanceof DataOutputSelection
                    selectedColumns.add(selection.getObject());
                }
            }
        }
        return selectedColumns;
    }
    
    // %$ KVB : It is essential that this method returns column names in the same order the columns are collected in getSelectedColumns !
    public List getSelectedColumnNames(OutputReportSeeder context) {
        ArrayList selectedColumns = new ArrayList();
        Iterator iter = getSelections().iterator();
        while (iter.hasNext()) {
            Selection selection = (Selection)iter.next();
            if (selection.isSelected()) {
                DataOutputVariable var = (DataOutputVariable)selection.getObject();
                String varString = var.getHumanStringValue(context);
                if (selection instanceof POJOSelection) {
                    Iterator propIter = ((POJOSelection)selection).getSubSelections().iterator();
                    while (propIter.hasNext()) {
                        PropertySelection subSelection = (PropertySelection)propIter.next();
                        if (subSelection.isSelected()) {
                            selectedColumns.add(varString + "." + ((Property)subSelection.getObject()).getName());
                        }
                    }
                } else if (selection instanceof POJOListSelection) {
                    // this selection applies to all elements of a list (of any size) of DataOutputVariables;
                    // appropriate column names for all of these elements are generated at this time
                    int i = 1;
                    Iterator elemIter = ((ListDataOutputVariable)selection.getObject()).getIterator();
                    while (elemIter.hasNext()) {
                        ElementDataOutputVariable edovar = (ElementDataOutputVariable)elemIter.next();
                        Iterator propIter = ((POJOListSelection)selection).getSubSelections().iterator();
                        while (propIter.hasNext()) {
                            PropertySelection subSelection = (PropertySelection)propIter.next();
                            if (subSelection.isSelected()) {
                                selectedColumns.add(varString + "." + i + "." + ((Property)subSelection.getObject()).getName());
                            }
                        }
                        i++;
                    }
                } else if (selection instanceof DataOutputListSelection) {
                    // this selection applies to all elements of a list (of any size) of DataOutputVariables;
                    // appropriate column names for all of these elements are generated at this time
                    int i = 1;
                    Iterator elemIter = ((ListDataOutputVariable)selection.getObject()).getIterator();
                    while (elemIter.hasNext()) {
                        selectedColumns.add(varString + "." + i);
                        elemIter.next();
                        i++;
                    }
                } else { // selection instanceof DataOutputSelection
                    selectedColumns.add(varString);
                }
            }
        }
        return selectedColumns;
    }
    
    private void addVariable(DataOutputVariable ovar) {
        Selection selection = find(ovar);
        if (selection == null) {
            if (ovar instanceof ListDataOutputVariable) {
                ElementDataOutputVariable ovar0 = ((ListDataOutputVariable)ovar).getElement();
                selection = (ovar0.hasDomainClassType() ? (Selection)(new POJOListSelection((ListDataOutputVariable)ovar, true)) : (Selection)(new DataOutputListSelection((ListDataOutputVariable)ovar, true)));
            } else {
                selection = (ovar.hasDomainClassType() ? (Selection)(new POJOSelection(ovar, true)) : (Selection)(new DataOutputSelection(ovar, true)));
            }
            selection.setController(this);
            selections.add(selection);
        }
    }
    
    // do not use this with selectList == this.selections
    private void addOrCopyVariableTo(DataOutputVariable ovar, Collection selectList) {
        Selection selection = find(ovar);
        if (selection == null) {
            if (ovar instanceof ListDataOutputVariable) {
                ElementDataOutputVariable ovar0 = ((ListDataOutputVariable)ovar).getElement();
                selection = (ovar0.hasDomainClassType() ? (Selection)(new POJOListSelection((ListDataOutputVariable)ovar, true)) : (Selection)(new DataOutputListSelection((ListDataOutputVariable)ovar, true)));
            } else {
                selection = (ovar.hasDomainClassType() ? (Selection)(new POJOSelection(ovar, true)) : (Selection)(new DataOutputSelection(ovar, true)));
            }
            selection.setController(this);
        } else {
            //System.out.println("Making a copy !");
        }
        selectList.add(selection);
    }
    
    // not used currently, but maybe some day
    private void removeVariable(DataOutputVariable ovar) {
        selections.remove(find(ovar));
    }
    
    public Selection find(DataOutputVariable ovar) {
        Iterator iter = selections.iterator();
        while (iter.hasNext()) {
            Selection selection = (Selection)iter.next();
            if (selection.getObject() == ovar) {
                //System.err.println("Found : " + ovar);
                return selection;
            }
        }
        //System.err.println("Not found : " + ovar);
        return null;
    }
    
    // to be called whenever the list of DataOutputVariables from the master query changes
    public void update() {
        List newSelections = new ArrayList();
        Iterator iter = reportFormat.getDataOutputVariables().iterator();
        while (iter.hasNext()) {
            DataOutputVariable ovar = (DataOutputVariable)iter.next();
            addOrCopyVariableTo(ovar, newSelections);
        }
        selections = newSelections;
    }
    
    private void notifySelectionChangeListeners() {
        Iterator iter = selectionChangeListeners.iterator();
        while (iter.hasNext()) {
            SelectionChangeListener listener = (SelectionChangeListener)iter.next();
            listener.selectionChanged();
        }
    }
    
    public void addSelectionChangeListener(SelectionChangeListener listener) {
        selectionChangeListeners.add(listener);
    }
    
    public void removeSelectionChangeListener(SelectionChangeListener listener) {
        selectionChangeListeners.remove(listener);
    }

	@Override
	public boolean isValid() {
		boolean valid = true;
		for (Selection sel : getSelections()) {
			valid &= sel.isValid();
		}
		return valid;
	}
}
