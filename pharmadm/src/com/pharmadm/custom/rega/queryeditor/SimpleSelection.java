/*
 * SimpleSelectionStatus.java
 *
 * Created on September 5, 2003, 10:12 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.io.Serializable;

import com.pharmadm.custom.rega.queryeditor.catalog.DbObject;

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
 * </p>
 */
public abstract class SimpleSelection implements Selection, Serializable {
    
	private DbObject object;
    private Object objectSpec; // specification of the object - the distinction object/spec is important for xml-encoding/decoding
    private boolean selected = false;
    private SelectionList controller = null;
    
    public DbObject getDbObject() {
    	return object;
    }
    
    /** Creates a new instance of SimpleSelection */
    public SimpleSelection(Object objectSpec, DbObject object) {
        this.objectSpec = objectSpec;
        this.object = object;
    }
    
    public SimpleSelection(Object objectSpec, DbObject object, boolean selected) {
        this.objectSpec = objectSpec;
        this.object = object;
        this.selected = selected;
    }
    
    public final Object getObject() {
        return getObject(objectSpec);
    }
    
    // this method should specify how to obtain the object from its specification
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
    
    public java.util.Collection<Selection> getSubSelections() {
        return null;
    }
    
    public SelectionList getController() {
        return controller;
    }    
    
    public void setController(SelectionList controller) {
        this.controller = controller;
    }
    
}
