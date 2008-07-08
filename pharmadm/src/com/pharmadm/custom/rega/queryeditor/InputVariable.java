
/** Java class "InputVariable.java" generated from Poseidon for UML.
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

import java.io.Serializable;

import com.pharmadm.custom.rega.queryeditor.OutputVariable.DescriptionDisplay;
import com.pharmadm.custom.rega.queryeditor.OutputVariable.RelationDisplay;
import com.pharmadm.custom.rega.queryeditor.OutputVariable.UniqueNameDisplay;
import com.pharmadm.custom.rega.queryeditor.catalog.DbObject;
import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;


/**
 * <p>
 * Represents a way to reuse a calculated value (represented by an
 * OutputVariable) at a new point in the Query. The user can specify which
 * OutputVariable's value to use by associating that OutputVariable with
 * this InputVariable, but he has to choose an OutputVariable with a
 * compatible VariableType to ensure meaningfulness.
 * </p>
 * <p>
 * This class supports xml-encoding. 
 * The following new properties are encoded :
 *  outputVariable
 * </p>
 *
 */
public class InputVariable extends Variable implements AWCWord, Cloneable, Serializable {
    
    ///////////////////////////////////////
    // associations
    
    /**
     * <p>
     *
     * </p>
     */
    public OutputVariable outputVariable;
    private boolean valid = true;
    
    /* For xml-encoding purposes only */
    public InputVariable() {
    }
    
    public InputVariable(DbObject object) {
        super(object.getTableObject());
    }
    ///////////////////////////////////////
    // access methods for associations
    
    public OutputVariable getOutputVariable() {
        return outputVariable;
    }
    public void setOutputVariable(OutputVariable outputVariable) {
        this.outputVariable = outputVariable;
    }
    
    public void setvalid(boolean valid) {
    	this.valid = valid;
    }
    
    
    ///////////////////////////////////////
    // operations
    
    
    /**
     * <p>
     * Determines whether the given OutputVariable is compatible with this
     * InputVariable, and can thus be bound to it. This is determined by the
     * type of both Variables.
     * </p>
     * <p>
     *
     * @param ov The OutputVariable for which the compatibility should be
     * determined
     * </p>
     * <p>
     * @return true if the given OutputVariable can be bound to this
     * InputVariable.
     * </p>
     */
    public boolean isCompatible(OutputVariable ov) {
        return getObject().isCompatible(ov.getObject());
    }
    
    public String acceptWhereClause(QueryVisitor visitor) {
        return (outputVariable == null) ? null : outputVariable.acceptWhereClause(visitor);
    }
 
    public String getHumanStringValue() {
    	if (valid) {
    		if (outputVariable == null) {
    			return this.getObject().getDescription();
    		}
    		else {
    			return outputVariable.getName(RelationDisplay.HIDE, DescriptionDisplay.HIDE, UniqueNameDisplay.SHOW);
    		}
    	}
    	else {
    		return "[undefined]";
    	}
    }    
 
    /**
     * Does not clone possible output variable, refers to the same one
     */
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

	public String getImmutableStringValue() {
		return getObject().getTableName();
	}
} // end InputVariable



