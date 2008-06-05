/*
 * ListDataOutputVariable.java
 *
 * Created on December 12, 2003, 11:50 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

import com.pharmadm.custom.rega.queryeditor.catalog.DbObject;

import java.util.*;

/**
 *
 * @author  kristof
 *
 * <p>
 * This class supports xml-encoding. 
 * The following new properties are encoded :
 *  parent
 *  pos
 * </p> 
 */
public class ElementDataOutputVariable extends DataOutputVariable {
    
    private ListDataOutputVariable parent;
    private int pos;
    
    /** Creates a new instance of ListDataOutputVariable */
    public ElementDataOutputVariable(DbObject object, ListDataOutputVariable parent, int pos) {
        super(object);
        this.parent = parent;
        this.pos = pos;
    }
    
    /* For xml-encoding purposes */
    public ElementDataOutputVariable() {
    }
    
    public String getFormalName() {
    	return parent.getFormalName() + "." + pos;    	
    }
    
    public int getPos() {
        return pos;
    }
    
    /* For xml-encoding purposes */
    public void setPos(int pos) {
        this.pos = pos;
    }
    
    public ListDataOutputVariable getParent() {
        return parent;
    }
    
    /* For xml-encoding purposes */
    public void setParent(ListDataOutputVariable parent) {
        this.parent = parent;
    }
    
    public String getUniqueName() {
        return parent.getUniqueName() + "." + pos;
    }
    
    public boolean consistsOfSingleObjectListVariable() {
        return false;
    }
    
    public ValueSpecifier getSpecifier() {
        // not supposed to be used : determined by parent
        return null;
    }
    
    public Object getStoredValue(DataRow row) {
        ArrayList parentValue = ((ArrayList)parent.getStoredValue(row));
        if (parentValue == null) {
            //System.err.println("Null value for " + parent + " (" + parent.getClass() + ")");
            return null;
        } else if (pos <= parentValue.size()) {
            return parentValue.get(pos - 1); // 1-based here, parent is 0-based
        } else {
            return null;
        }
    }
    
}
