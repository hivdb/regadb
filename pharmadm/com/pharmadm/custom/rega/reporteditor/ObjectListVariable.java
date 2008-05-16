/*
 * ObjectListVariable.java
 *
 * Created on November 18, 2003, 8:10 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

import java.util.*;

import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.VariableType;

/**
 *
 * @author  kristof
 */

/**
 * <p>
 * Represents a fresh variable that can be linked to an input list of objects of
 * the given valueType.
 * </p>
 * <p>
 * This class supports xml-encoding. 
 * The following new properties are encoded :
 *  variableType (in constructor, using ObjectListVariablePersistenceDelegate)
 * </p>
 *
 */
public class ObjectListVariable implements Cloneable, DataGroupWord, ValueSpecifier {
   
    // There is a strong connection between ObjectListVariable and FromVariable (conceptually):
    // they both represent references to lists of objects of the same type, either "in java" or 
    // in the database (one table).
    //
    // In methods, this correspondence is only apparent in the acquireSeqId method and friends.
    // However, observe also that valueType is the equivalent of tableName, and that the
    // columns in the database table also correspond to properties in the class.
    // 
    // So it *might* be useful to generalize the two classes, but for now, we decided not to.
    
    private VariableType variableType;
    
    private long seqId;
    private static long nextSeqId;
    private static Object seqIdLock = new Object();
    
    /** Creates a new instance of ObjectListVariable */
    public ObjectListVariable(VariableType variableType) {
        this.variableType = variableType;
        acquireSeqId();
    }
    
    private void acquireSeqId() {
        synchronized(seqIdLock) {
            seqId = nextSeqId++;
        }
    }
   
    public boolean isCompatible(OutputVariable ov) {
        return getVariableType().isCompatibleType(ov.getVariableType());
    }
    
    public List getList(Report report) {
        return report.getList(this);
    }
    
    public String getUniqueName() {
        return getVariableType().getName()+seqId;
    }
    
    /* Implementing DataGroupWord */
    public String getHumanStringValue() {
        return getUniqueName();
    }
    
    /* Implementing ValueSpecifier 
     *
     * @pre : 0 <= dataRow's index < getSize()
     */
    public Object getValue(DataRow dataRow) {
        return getList(dataRow.getReport()).get(dataRow.getIndex());
    }    
    
    public VariableType getVariableType() {
        return variableType;
    }
    
    /* Implementing ValueSpecifier */
    public Class getValueType() {
        return variableType.getValueType();
    }
    
    public ValueSpecifier cloneInContext(java.util.Map originalToCloneMap) {
        return (ValueSpecifier)originalToCloneMap.get(this);
    }
    
    // The list is not cloned, i.e. the clone refers to the same list 
    public Object clone() throws CloneNotSupportedException {
        ObjectListVariable clone = (ObjectListVariable)super.clone();
        clone.acquireSeqId();
        return clone;
    }

    public String getHumanStringValue(OutputReportSeeder context) {
        if (context == null) {
            return getHumanStringValue();
        } else {
            com.pharmadm.custom.rega.queryeditor.OutputVariable assignedVar = context.getAssignedVariable(this);
            if (assignedVar == null) {
                return getHumanStringValue();
            } else {
                return assignedVar.getHumanStringValue();
            }
        }
    }

	public String getImmutableStringValue() {
		return variableType.getName();
	}
}
