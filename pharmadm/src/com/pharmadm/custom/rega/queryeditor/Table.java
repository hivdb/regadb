
/** Java class "Table.java" generated from Poseidon for UML.
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

import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;

/**
 * <p>
 * A simplified representation of a database table.
 * </p>
 *
 */
public class Table implements Comparable<Table> {
    
    ///////////////////////////////////////
    // attributes
    
    
    /**
     * <p>
     * Represents the name of the table. A corresponding table should exist in
     * the database.
     * </p>
     *
     */
    private String name;
    private HashMap<String, Field> fields = null; // of type Field
    private String comment;
    private int seqId;
    private static Object seqIdLock = new Object();
    
    public Table(String name, String comment) {
        this.name = name;
        this.comment = comment;
        seqId = 1;
        this.fields = new HashMap<String, Field>();
    }
    
    public int acquireSeqId() {
        synchronized(seqIdLock) {
            return seqId++;
        }
    }
    
    public void addField(Field field) {
    	fields.put(field.getName(), field);
    }
    
    ///////////////////////////////////////
    // access methods for associations
    
    public Collection<Field> getFields() {
    	List<Field> result = new ArrayList<Field>();
        result.addAll(fields.values());
        Collections.sort(result);
        return result;
    }
    
    public Collection<Field> getPrimitiveFields() {
    	List<Field> result = new ArrayList<Field>();
    	for (Field field : fields.values()) {
    		if (field.isPrimitive()) {
    			result.add(field);
    		}
    	}
        Collections.sort(result);
        return result;
    }
    
    public Collection<Field> getPrimaryKeyFields() {
        Collection<Field> res = new ArrayList<Field>();
        for (Field field: fields.values()) {
        	if (field.isPrimaryKey()) {
        		res.add(field);
        	}
        }
        return res;
    }
        
    public Field getField(String name) {
        return fields.get(name);
    }
    
    public boolean hasField(String name) {
    	return fields.containsKey(name);
    }
    
    ///////////////////////////////////////
    // operations
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
    	return DatabaseManager.getInstance().getAWCCatalog().getObject(getName()).getDescription();
    }
    
    public String getComment() {
        return comment;
    }
    
    /**
     * <p>
     * Determines equality among Tables.
     * </p>
     * <p>
     *
     * @return true iff the argument is a Table with an equal name as this
     * Table.
     * </p>
     * <p>
     * @param obj the reference object with which to compare
     * </p>
     */
    public int hashCode() {
        return getName().toLowerCase().hashCode();
    }
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        else if (! (obj instanceof Table)) {
            return false;
        }
        else {
            return ((Table)obj).getName().equalsIgnoreCase(this.getName());
        }
    }
    
    public int compareTo(Table obj) {
        if (obj == null) {
            return -1;
        }
        else {
            return getName().toLowerCase().compareTo(obj.getName().toLowerCase());
        }
    }
    
    // end equals
    
} // end Table



