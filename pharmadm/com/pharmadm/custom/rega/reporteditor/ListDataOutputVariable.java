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

import com.pharmadm.custom.rega.queryeditor.*;
import com.pharmadm.custom.rega.queryeditor.constant.DoubleConstant;

import java.util.*;

/**
 *
 * @author  kristof
 *
 * <p>
 * This class supports xml-encoding. 
 * The following new properties are encoded :
 *  varList
 *  sizeConstant
 * </p> 
 */
public class ListDataOutputVariable extends DataOutputVariable {
    
    private ArrayList varList;
    private DoubleConstant sizeConstant;
    private ConstantValueChangeListener listener = null;
    
    /** Creates a new instance of ListDataOutputVariable linked to a maximum-size constant */
    public ListDataOutputVariable(VariableType elementType, DoubleConstant sizeCst, String formalName) {
        super(new VariableType("ArrayList"), formalName);
        this.varList = new ArrayList();
        // zero element serves as a template, it has no actual value
        for (int i = 0; i <= ((Double)sizeCst.getValue()).intValue(); i++) {
            varList.add(new ElementDataOutputVariable(elementType, this, i));
        }
        this.sizeConstant = sizeCst;
        this.listener = new ConstantValueChangeListener(this);
        sizeCst.addValueChangeListener(listener);
    }
    
    /* For xml-encoding purposes */
    public ListDataOutputVariable() {
        this.listener = new ConstantValueChangeListener(this);
    }
    
    public Class getValueType() {
        return java.util.ArrayList.class;
    }
    
    public DoubleConstant getSizeConstant() {
        return sizeConstant;
    }
    
    /* For xml-encoding purposes */
    public void setSizeConstant(DoubleConstant sizeConstant) {
        // remove old listener ?
        this.sizeConstant = sizeConstant;
        if (sizeConstant != null) {
            sizeConstant.addValueChangeListener(listener);
        }
    }
    
    public int getSize() {
        return varList.size() - 1;
    }
    
    public ArrayList getVarList() {
        return varList;
    }
    
    /* For xml-encoding purposes */
    public void setVarList(ArrayList varList) {
        this.varList = varList;
    }
    
    /* Return the template element (which always exists) */
    public ElementDataOutputVariable getElement() {
        return (ElementDataOutputVariable)varList.get(0);
    }
    
    /* Return the i'th element variable in the list (1-based) */
    public DataOutputVariable getElement(int i) {
        if (i > 0) {
            return (DataOutputVariable)varList.get(i);
        } else {
            System.err.println("Trying to take zero'th element of a 1-based list");
            return null;
        }
    }
    
    /* return an iterator over all actual values in this list (not the template) */
    public Iterator getIterator() {
        Iterator iter = varList.iterator();
        iter.next(); // skip first element
        return iter;
    }
    
    public VariableType getElementType() {
        return getElement().getVariableType();
    }
    
    public Class getElementValueType() {
        return getElement().getValueType();
    }
    
    public Property getElementProperty(String name) {
        return getElement().getProperty(name);
    }
    
    public List getElementProperties() {
        return getElement().getProperties();
    }
    
    public Object getStoredValue(DataRow row, int i) {
        if (i <= getSize()) {
            return getElement(i).getStoredValue(row);
        } else {
            return null;
        }
    }
    
    // clone the list of element variables in addition to any other clone operations for DataOutputVariable;
    // make sure each cloned element refers to the cloned list, and store each element in the context map
    protected DataOutputVariable cloneInContext(Map originalToCloneMap) throws CloneNotSupportedException {
        final ListDataOutputVariable clone = (ListDataOutputVariable)super.cloneInContext(originalToCloneMap);
        clone.setVarList(new ArrayList());
        //System.err.println("Constant is " + getSizeConstant());
        clone.setSizeConstant((DoubleConstant)originalToCloneMap.get(getSizeConstant()));
        clone.listener = new ConstantValueChangeListener(clone);
        //System.err.println("Cloned constant is " + clone.getSizeConstant());
        DoubleConstant constantClone = clone.getSizeConstant();
        constantClone.addValueChangeListener(clone.listener);
        Iterator iter = varList.iterator();
        while (iter.hasNext()) {
            ElementDataOutputVariable var = (ElementDataOutputVariable)iter.next();
            ElementDataOutputVariable cloneVar = (ElementDataOutputVariable)var.cloneInContext(originalToCloneMap);
            cloneVar.setParent(clone);
            clone.getVarList().add(cloneVar);
        }
        return clone; 
    }
    
    protected void resize(int newSize) {
        if (newSize == getSize()) {
            return;
        } else if (newSize < getSize()) {
            for (int i = getSize(); i > newSize; i--) {
                varList.remove(i);
            }
        } else {
            for (int i = getSize() + 1; i <= newSize; i++) {
                varList.add(new ElementDataOutputVariable(getElementType(), this, i));
            }
        }
    }
    
    private class ConstantValueChangeListener implements ValueChangeListener {
          
        private ListDataOutputVariable receiver;
        
        protected ConstantValueChangeListener(ListDataOutputVariable receiver) {
            this.receiver = receiver;
        }
        
        public void valueChanged() {
            receiver.resize(((Number)receiver.getSizeConstant().getValue()).intValue());
        }
    }
           
}
