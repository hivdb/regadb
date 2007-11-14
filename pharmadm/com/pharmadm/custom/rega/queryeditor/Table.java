
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

/**
 * <p>
 * A simplified representation of a database table.
 * </p>
 *
 */
public class Table implements Comparable {
    
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
    private String singularName;
    private Collection fields = null; // of type Field
    private String comment;
    
    public Table(String name) {
        if (name != null) {
            this.name = name;
            this.singularName = name.toLowerCase();
            comment = JDBCManager.getInstance().getCommentForTable(name);
        }
    }
    
    ///////////////////////////////////////
    // access methods for associations
    
    public Collection getFields() {
        if (fields == null) {
            List fieldNames = JDBCManager.getInstance().getColumnNames(getName());
            List keyNames = JDBCManager.getInstance().getPrimaryKeys(getName());
            fields = new ArrayList();
            Iterator fieldNameIter = fieldNames.iterator();
            while (fieldNameIter.hasNext()) {
                String fieldName = (String)fieldNameIter.next();
                Field field = new Field(fieldName, this, keyNames.contains(fieldName));
                fields.add(field);
            }
        }
        return fields;
    }
    
    public Collection getPrimaryKeyFields() {
        Collection res = new ArrayList();
        Iterator iter = fields.iterator();
        while (iter.hasNext()) {
            Field field = (Field)iter.next();
            if (field.isPrimaryKey()) {
                res.add(field);
            }
        }
        return res;
    }
        
    public Field getField(String name) {
        Iterator iter = getFields().iterator();
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
    
    public String getSingularName() {
        return singularName;
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
    
    protected void setSingularName(String newSingularName) {
        this.singularName = newSingularName;
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



