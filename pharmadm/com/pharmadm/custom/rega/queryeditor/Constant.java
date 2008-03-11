
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
package com.pharmadm.custom.rega.queryeditor;

import java.text.Format;
import java.util.*;
import com.pharmadm.custom.rega.reporteditor.DataGroupWord;
import com.pharmadm.custom.rega.reporteditor.DataRow;
import com.pharmadm.custom.rega.reporteditor.ValueSpecifier;

/**
 * <p>
 * Represents a user-configurable constant value in a Query. The user can
 * set the value of a Constant, and than materialize the current status of
 * the Query into some executable form, e.g. a Hibernate find string. In
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
public abstract class Constant implements Cloneable, AWCWord, DataGroupWord, ValueSpecifier {
    
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
    
    private boolean suggestedValuesMandatory = false;
    private String suggestedValuesQuery = null;
    
    // Yes, I *know* ! Mind your own business :-)
    private ArrayList valueChangeListeners = new ArrayList();
    
    
    ///////////////////////////////////////
    // associations
    
    
    ///////////////////////////////////////
    // access methods for associations
    
    public abstract Format getFormat();
    
    ///////////////////////////////////////
    // operations
    
    
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
    public Object parseValue(String s) throws java.text.ParseException {
        Object value = getFormat().parseObject(s);
        setValue(value);
        return value;
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
    
    /**
     * A String that represents a query to ask the database for possible values for this field.
     * The query should return a result set consisting of a single column.
     */
    public String getSuggestedValuesQuery() {
        return suggestedValuesQuery;
    }
    
    public void setSuggestedValuesQuery(String query) {
        this.suggestedValuesQuery = query;
    }
    
    /**
     * Whether values other than those returned by the suggested values string are allowed.
     * If true, than no other values than the suggested are allowed.
     * This can be ignored if the suggested values query is null.
     */
    public boolean areSuggestedValuesMandatory() {
        return suggestedValuesMandatory;
    }
    
    public boolean isSuggestedValuesMandatory() {
        return suggestedValuesMandatory;
    }
    
    public boolean getSuggestedValuesMandatory() {
        return suggestedValuesMandatory;
    }
    
    public void setSuggestedValuesMandatory(boolean mandatory) {
        this.suggestedValuesMandatory = mandatory;
    }
    
    public String getHumanStringValue(com.pharmadm.custom.rega.reporteditor.QueryOutputReportSeeder context) {
        // context-independent
        return getHumanStringValue();
    }    
      
    public void addValueChangeListener(ValueChangeListener listener) {
        valueChangeListeners.add(listener);
    }
    
    private void notifyValueChangeListeners() {
        Iterator iter = valueChangeListeners.iterator();
        while (iter.hasNext()) {
            ValueChangeListener listener = (ValueChangeListener)iter.next();
            listener.valueChanged();
        }
    }
    
} // end Constant



