
/** Java class "Constant.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.constant;

import java.io.Serializable;
import java.util.*;

import com.pharmadm.custom.rega.queryeditor.AWCWord;
import com.pharmadm.custom.rega.queryeditor.ValueChangeListener;
import com.pharmadm.custom.rega.queryeditor.catalog.DbObject;
import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;
import com.pharmadm.custom.rega.reporteditor.DataGroupWord;
import com.pharmadm.custom.rega.reporteditor.DataRow;
import com.pharmadm.custom.rega.reporteditor.OutputReportSeeder;
import com.pharmadm.custom.rega.reporteditor.ValueSpecifier;

/**
 * <p>
 * Represents a user-configurable constant value in a Query. The user can
 * set the value of a Constant, and than materialize the current status of
 * the Query into some executable form, e.g. a SQL find string. In
 * this 'frozen'  version, the value appears as a constant. The frozen
 * version can then be submitted to the database for execution.
 * </p>
 *
 * <p>
 * This class supports xml-encoding. 
 * The following new properties are encoded : 
 *  value
 *  suggestedValuesQuery
 *  suggestedValuesMandatory
 * </p>
 */
public abstract class Constant implements Cloneable, AWCWord, DataGroupWord, ValueSpecifier, Serializable {
    
    ///////////////////////////////////////
    // attributes
    
	/**
	 * raw value specified by the user
	 */
    private Object value;
    
    /**
     * list of suggested values
     */
    private SuggestedValues suggestedValues;
    
    // Yes, I *know* ! Mind your own business :-)
    private ArrayList<ValueChangeListener> valueChangeListeners = new ArrayList<ValueChangeListener>();
    
    public Constant() {
    	this(new SuggestedValues());
    }    
    
    /**
     * a new constant with a given list of predefined values
     * @param suggestedValues
     */
    public Constant(SuggestedValues suggestedValues) {
    	value = null;
    	this.suggestedValues = suggestedValues;
    }

    public abstract DbObject getDbObject();
    /**
     * gets the default value for this constant
     * this value may not be null
     * @return
     */
    public abstract Object getdefaultValue();
    
    /**
     * parse the given object into a string representation of the object
     * @param o
     * @return null when the given object is not a valid value for this constant
     */
    protected abstract String parseObject(Object o);
    

    /**
     * tries and set the value of this constant to the given object
     * returns true on success, false when the value is invalid
     * @param o
     * @return
     */
    public boolean setValue(Object o) {
    	String str = parseObject(o);
    	if (str != null) {
    		this.value = o;
    		return true;
    	}
    	return false;
    } 
    
    /**
     * returns true when the given object is a valid value for this
     * constant
     * @param o
     * @return
     */
    public boolean validateValue(Object o) {
    	return parseObject(o) != null;
    }
    
    public Object getValue(DataRow dataRow) {
        return getValue();
    }
    
    public Object getValue() {
        return value;
    }
    
    /**
     * reset this constant to its default value
     */
    public void reset() {
    	value = getdefaultValue();
    	notifyValueChangeListeners();
    }
    
    public String getHumanStringValue() {
    	assignDefaultWhenNull();
    	return parseObject(getValue());
    }
    
    /**
     * if the current value of this constant is null,
     * reset it to its default value
     */
    private void assignDefaultWhenNull() {
    	if (value == null) {
    		value = getdefaultValue();
    	}
    }
    
    public String getHumanStringValue(OutputReportSeeder context) {
        return getHumanStringValue();
    }  
    
    
    public String acceptWhereClause(QueryVisitor visitor) {
        return visitor.visitWhereClauseConstant(this);
    }    
    
    /**
     * returns the class of the values of this constant
     */
    public Class getValueTypeClass() {
    	return getDbObject().getValueTypeClass();
    } 
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }    
    
    /* Implementing ValueSpecifier */
    public ValueSpecifier cloneInContext(java.util.Map originalToCloneMap) throws CloneNotSupportedException {
    	Constant clone = (Constant) originalToCloneMap.get(this);
        if (clone == null) {
        	clone = (Constant) this.clone();
        	clone.value = value;
        	clone.suggestedValues = suggestedValues;
        	clone.valueChangeListeners = valueChangeListeners;
        }
        return (ValueSpecifier) clone;
    }  
    
    public void setSuggestedValues(SuggestedValues values) {
    	this.suggestedValues = values;
    }
    
    public SuggestedValues getSuggestedValues() {
    	return suggestedValues;
    }    
    
    /**
     * return the full list of suggested values
     * this includes both values retrieved from the DB
     * as values manually set
     * @return
     */
    public ArrayList<SuggestedValuesOption> getSuggestedValuesList() {
    	return suggestedValues.getSuggestedValues();
    }
    
    /**
     * sets a query to retrieve suggested values from the DB
     * @param query
     */
    public void setSuggestedValuesQuery(String query) {
    	suggestedValues.setQuery(query);
    }
    
    /**
     * add an option to the list of suggested values
     * @param option
     */
    public void addSuggestedValue(SuggestedValuesOption option) {
    	suggestedValues.addOption(option);
    }
    
    /**
     * Whether values other than those returned by the suggested values string are allowed.
     * If true, than no other values than the suggested are allowed.
     * This can be ignored if the suggested values query is null.
     */
    public boolean areSuggestedValuesMandatory() {
        return suggestedValues.isMandatory();
    }
    
    public void setSuggestedValuesMandatory(boolean mandatory) {
        suggestedValues.setMandatory(true);
    }
    
    public void addValueChangeListener(ValueChangeListener listener) {
        valueChangeListeners.add(listener);
    }
    
    private void notifyValueChangeListeners() {
        Iterator<ValueChangeListener> iter = valueChangeListeners.iterator();
        while (iter.hasNext()) {
            ValueChangeListener listener = (ValueChangeListener)iter.next();
            listener.valueChanged();
        }
    }
    
	public String getImmutableStringValue() {
		return getDbObject().getDescription();
	}
} // end Constant



