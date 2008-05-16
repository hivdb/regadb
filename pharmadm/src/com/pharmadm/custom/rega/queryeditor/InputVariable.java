
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
    
    /* For xml-encoding purposes only */
    public InputVariable() {
    }
    
    public InputVariable(VariableType type) {
        super(type);
    }
    ///////////////////////////////////////
    // access methods for associations
    
    public OutputVariable getOutputVariable() {
        return outputVariable;
    }
    public void setOutputVariable(OutputVariable outputVariable) {
        this.outputVariable = outputVariable;
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
     * @return true iff the given OutputVariable can be bound to this
     * InputVariable.
     * </p>
     */
    public boolean isCompatible(OutputVariable ov) {
        return getVariableType().isCompatibleType(ov.getVariableType());
    }
    
    public String acceptWhereClause(QueryVisitor visitor) {
        return (outputVariable == null) ? null : outputVariable.acceptWhereClause(visitor);
    }
 
    public String getHumanStringValue() {
    	return (outputVariable == null) ? this.getVariableType().getName() : outputVariable.getName(RelationDisplay.HIDE, DescriptionDisplay.HIDE, UniqueNameDisplay.SHOW);
    }    
 
    /**
     * Does not clone possible output variable, refers to the same one
     */
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

	public String getImmutableStringValue() {
		return getVariableType().getName();
	}
} // end InputVariable



