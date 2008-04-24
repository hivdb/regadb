
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

import java.io.Serializable;
import java.util.*;

import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;

/**
 * <p>
 * A simplified representation of a database table.
 * </p>
 *
 */
public class Table implements Comparable, Serializable {
    
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
    private Collection<Field> fields = null; // of type Field
    private String comment;
    private int seqId;
    private static Object seqIdLock = new Object();
    
    public Table(String name) {
        if (name != null) {
            this.name = name;
            comment = DatabaseManager.getInstance().getDatabaseConnector().getCommentForTable(name);
            seqId = 1;
        }
    }
    
    public int acquireSeqId() {
        synchronized(seqIdLock) {
            return seqId++;
        }
    }
    
    ///////////////////////////////////////
    // access methods for associations
    
    public Collection<Field> getFields() {
        if (fields == null) {
            List<String> fieldNames = DatabaseManager.getInstance().getDatabaseConnector().getColumnNames(getName());
            List<String> keyNames = DatabaseManager.getInstance().getDatabaseConnector().getPrimaryKeys(getName());
            fields = new ArrayList<Field>();
            Iterator<String> fieldNameIter = fieldNames.iterator();
            while (fieldNameIter.hasNext()) {
                String fieldName = fieldNameIter.next();
                Field field = new Field(fieldName, this, keyNames.contains(fieldName));
                fields.add(field);
            }
        }
        return fields;
    }
    
    public Collection<Field> getPrimaryKeyFields() {
        Collection<Field> res = new ArrayList<Field>();
        Iterator<Field> iter = fields.iterator();
        while (iter.hasNext()) {
            Field field = iter.next();
            if (field.isPrimaryKey()) {
                res.add(field);
            }
        }
        return res;
    }
        
    public Field getField(String name) {
        Iterator<Field> iter = getFields().iterator();
        while (iter.hasNext()) {
            Field field = (Field)iter.next();
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }
    
    ///////////////////////////////////////
    // operations
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
    	return AWCPrototypeCatalog.getInstance().getObjectDescription(getName());
    }
    
    public String getComment() {
        return comment;
    }
    
    /**
     * For subclasses, in case the name was not yet known at the time of construction.
     */
    public void setName(String newName) {
        this.name = newName;
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
    
    public int compareTo(Object obj) {
        if (obj == null) {
            return -1;
        }
        else {
            return this.getName().toLowerCase().compareTo(((Table)obj).getName().toLowerCase());
        }
    }
    
    // end equals
    
} // end Table



