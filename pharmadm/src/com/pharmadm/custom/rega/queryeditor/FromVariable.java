
/** Java class "FromVariable.java" generated from Poseidon for UML.
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

import com.pharmadm.custom.rega.queryeditor.catalog.DbObject;
import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;


/**
 * <p>
 * Represents a fresh variable that is declarated in the from-part of the
 * query.
 * </p>
 * <p>
 * This class supports xml-encoding. 
 * The following new properties are encoded :
 *  tableName (in constructor, using FromVariablePersistenceDelegate)
 * </p>
 *
 */
public class FromVariable extends Variable implements AWCWord, Cloneable, Serializable {
    
    ///////////////////////////////////////
    // associations
    
    /**
     * <p>
     *
     * </p>
     */

    private long seqId;
    private boolean locked;
    
    public FromVariable(DbObject obj) {
    	super(obj.getTableObject());
        locked = false;
    }
    
    private void acquireSeqId() {
    	seqId = getObject().getTable().acquireSeqId();
        locked = true;
    }
    
    ///////////////////////////////////////
    // access methods for associations
    
    public String getUniqueName() {
    	if (!locked) {
    		acquireSeqId();
    	}
        return getObject().getSqlAlias() + seqId;
    }
    
    public String getFromClauseStringValue(QueryVisitor visitor) {
    	return visitor.visitFromClauseFromVariable(this);
    }
    
    public String acceptWhereClause(QueryVisitor visitor) {
        return visitor.visitWhereClauseFromVariable(this);
    }
    
    public String getHumanStringValue() {
        return getUniqueName();
    }
    
    public String toString() {
    	return getUniqueName();
    }
    
    public void unlock() {
    	locked = false;
    }
    
    protected Object clone() throws CloneNotSupportedException {
        FromVariable clone = (FromVariable)super.clone();
        unlock();
        return clone;
    }

	public String getImmutableStringValue() {
		return getObject().getTableName();
	}
}
