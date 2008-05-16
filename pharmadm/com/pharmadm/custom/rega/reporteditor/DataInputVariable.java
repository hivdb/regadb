
/** Java class "InputVariable.java" generated from Poseidon for UML and copy-pasted
 *  by Kristof from Belleghem to "DataInputVariable.java".
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

import java.util.*;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.InputVariable;

/**
 * <p>
 * Represents a way to reuse a calculated value (represented by a 
 * DataOutputVariable) at a new point in the ReportFormat. The user can specify 
 * which DataOutputVariable's value to use by associating that DataOutputVariable 
 * with this DataInputVariable, but he has to choose a DataOutputVariable with a
 * compatible VariableType to ensure meaningfulness.
 * </p>
 * <p>
 * This class supports xml-encoding. 
 * The following new properties are encoded :
 *  outputVariable
 * </p>
 *
 */
public class DataInputVariable extends com.pharmadm.custom.rega.queryeditor.Variable implements DataGroupWord, ValueSpecifier, Cloneable {
  
    // Clearly there is a strong connection between this class and com.pharmadm.custom.rega.queryeditor.InputVariable
    // (see also the similarities in (Data)InputVariableController and gui.J(Data)InputVariableConfigurer)
    // Eventually, they may become subclasses of a common GeneralizedInputVariable class.
  
    
    ///////////////////////////////////////
    // associations
    
    /**
     * <p>
     *
     * </p>
     */
    private DataOutputVariable outputVariable;
    
    /* For xml-encoding purposes only */
    public DataInputVariable() {
    }
    
    public DataInputVariable(com.pharmadm.custom.rega.queryeditor.VariableType type) {
        super(type);
    }
    ///////////////////////////////////////
    // access methods for associations
    
    public DataOutputVariable getOutputVariable() {
        return outputVariable;
    }
    public void setOutputVariable(DataOutputVariable outputVariable) {
        this.outputVariable = outputVariable;
    }
    
    ///////////////////////////////////////
    // operations
    
    
    /**
     * <p>
     * Determines whether the given DataOutputVariable is compatible with this
     * DataInputVariable, and can thus be bound to it. This is determined by the
     * type of both Variables.
     * </p>
     * <p>
     *
     * @param ov The DataOutputVariable for which the compatibility should be
     * determined
     * </p>
     * <p>
     * @return true iff the given DataOutputVariable can be bound to this
     * DataInputVariable.
     * </p>
     */
    public boolean isCompatible(DataOutputVariable ov) {
        return getVariableType().isCompatibleType(ov.getVariableType());
    }
    
    public String getHumanStringValue() {
        DataOutputVariable ovar = getOutputVariable();
        return (((ovar = getOutputVariable()) == null) ? null : ovar.getHumanStringValue());
    }    
 
    /**
     * Does not clone possible output variable, refers to the same one
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    /* implementing ValueSpecifier */
    public ValueSpecifier cloneInContext(java.util.Map originalToCloneMap) throws CloneNotSupportedException {
        return (ValueSpecifier)originalToCloneMap.get(this);
    }
   
    /* implementing ValueSpecifier */
    public Object getValue(DataRow dataRow) {
        return getOutputVariable().getStoredValue(dataRow); 
    }
    
    /* implementing ValueSpecifier */
    public Class getValueType() {
        return getVariableType().getValueType();
    }
    
    public String getHumanStringValue(OutputReportSeeder context) {
        DataOutputVariable ovar = getOutputVariable();
        return (((ovar = getOutputVariable()) == null) ? null : ovar.getHumanStringValue(context));
    }

	public String getImmutableStringValue() {
		return getVariableType().getName();
	}
} // end DataInputVariable



