/*
 * ComposedSelectionStatus.java
 *
 * Created on September 5, 2003, 10:10 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.util.*;

/**
 *
 * @author  kristof
 *
 * <p>
 * This class supports xml-encoding. 
 * The following new properties are encoded : 
 *  objectSpec (in constructor, using SelectionPersistenceDelegate)
 *  selected
 *  controller
 *  subSelections
 * </p>
 */
public abstract class ComposedSelection implements Selection {
    
    private Object objectSpec;
    private boolean selected = false;
    private SelectionList controller = null;
    private Collection<Selection> subSelections;
    
    /** Creates a new instance of ComposedSelection */
    public ComposedSelection(Object objectSpec) {
        this.objectSpec = objectSpec;
    }
    
    /** Creates a new instance of ComposedSelection */
    public ComposedSelection(Object objectSpec, boolean selected) {
        this.objectSpec = objectSpec;
        this.selected = selected;
    }
    
   
    public final Object getObject() {
        return getObject(objectSpec);
    }
    
    public abstract Object getObject(Object objectSpec);
    
    public final Object getObjectSpec() {
        return objectSpec;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public Collection<Selection> getSubSelections() {
        return subSelections;
    }
    
    /* For xml encoding purposes only */
    public void setSubSelections(Collection<Selection> subSelections) {
        this.subSelections = subSelections;
    }
    
    public Selection find(Object object) {
        Iterator<Selection> iter = getSubSelections().iterator(); 
        while (iter.hasNext()) {
            Selection selection = iter.next();
            if (selection.getObject().equals(object)) {
                //System.err.println("Found : " + object);
                return selection;
            }
        }
        //System.err.println("Not Found : " + object);
        return null;
    }
     
    public SelectionList getController() {
        return controller;
    }    
    
    public void setController(SelectionList controller) {
        this.controller = controller;
    }    
    
}
