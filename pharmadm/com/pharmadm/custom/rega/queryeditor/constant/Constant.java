
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
import java.text.Format;
import java.util.*;

import com.pharmadm.custom.rega.queryeditor.AWCWord;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.InputVariable;
import com.pharmadm.custom.rega.queryeditor.ValueChangeListener;
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
     * <p>
     * Represents the value of the Constant as specified by the user. Might be
     * null.
     * </p>
     *
     */
    private Object value = null;
    private SuggestedValues suggestedValues = new SuggestedValues();
    
    // Yes, I *know* ! Mind your own business :-)
    private ArrayList<ValueChangeListener> valueChangeListeners = new ArrayList<ValueChangeListener>();
    
    
    ///////////////////////////////////////
    // associations
    
    
    ///////////////////////////////////////
    // access methods for associations
    
    public abstract Format getFormat();
    public abstract String getValueTypeString();
    
    ///////////////////////////////////////
    // operations
    
    
    public Constant(SuggestedValues suggestedValues) {
    	this.suggestedValues = suggestedValues;
    }

    
    public Constant() {
    	
    }
    
    /**
     * <p>
     * Does ...
     * </p><p>
     *
     * @return a Object with ...
     * </p><p>
     * @param s ...
     * </p>
     */
    public Object parseValue(Object s) throws java.text.ParseException {
        Object value = getFormat().parseObject(s.toString());
        setValue(value);
        return value;
    } 
    
    public void setSuggestedValues(SuggestedValues values) {
    	this.suggestedValues = values;
    }
    
    public SuggestedValues getSuggestedValues() {
    	return suggestedValues;
    }
    
    
    /* Implementing ValueSpecifier */
    public abstract Class getValueType();
    
    /* Implementing ValueSpecifier */
    public Object getValue(DataRow dataRow) {
        return value;
    }
    
    public Object getValue() {
        return value;
    }
    
    /*
     * For efficiency reasons, this does NOT check for mandatory values compliance.
     */
    public void setValue(Object newVal) {
        value = newVal;
        notifyValueChangeListeners();
    }
    
    public String acceptWhereClause(QueryVisitor visitor) {
        return visitor.visitWhereClauseConstant(this);
    }
    
    public String getHumanStringValue() {
        try {
            return getFormat().format(value);
        } catch (IllegalArgumentException iae) {
        	iae.printStackTrace();
            return null;
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    /* Implementing ValueSpecifier */
    public ValueSpecifier cloneInContext(java.util.Map originalToCloneMap) throws CloneNotSupportedException {
        return (ValueSpecifier)originalToCloneMap.get(this);
    }
    
    public ArrayList<SuggestedValuesOption> getSuggestedValuesList() {
    	return suggestedValues.getSuggestedValues();
    }
    
    public void setSuggestedValuesQuery(String query) {
    	suggestedValues.setQuery(query);
    }
    
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
    
    public String getHumanStringValue(OutputReportSeeder context) {
        // context-independent
        return getHumanStringValue();
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
} // end Constant



