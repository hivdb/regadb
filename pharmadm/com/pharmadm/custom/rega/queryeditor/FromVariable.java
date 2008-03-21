
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

import java.util.*;
//import com.pharmadm.custom.rega.chem.search.*;

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
public class FromVariable implements AWCWord, Cloneable {
    
    ///////////////////////////////////////
    // associations
    
    /**
     * <p>
     *
     * </p>
     */

    private String tableName;
    private Table table;
    
    private long seqId;
//    private static long nextSeqId;
//    private static Object seqIdLock = new Object();
    private boolean locked;
    
    public FromVariable(String tableName) {
        this.tableName = tableName;
        this.table = DatabaseManager.getInstance().getTableCatalog().doGetTable(tableName);
        locked = false;
    }
    
    private void acquireSeqId() {
    	seqId = table.acquireSeqId();
        locked = true;
    }
    
    ///////////////////////////////////////
    // access methods for associations
    
    public Table getTable() {
        return table;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public String getUniqueName() {
    	if (!locked) {
    		acquireSeqId();
    	}
        return AWCPrototypeCatalog.getInstance().getGoodDbName(tableName) + seqId;
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
    
    public void unlock() {
    	locked = false;
    }
    
    protected Object clone() throws CloneNotSupportedException {
        FromVariable clone = (FromVariable)super.clone();
//        clone.unlock();
//        clone.acquireSeqId();
        return clone;
    }
    
    
    
}
