
/** Java class "Field.java" generated from Poseidon for UML.
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

import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;

/**
 * <p>
 * Represents a column of a database table.
 * </p>
 * 
 */
public class Field implements Comparable<Field>{
 
  ///////////////////////////////////////
  // attributes

/**
 * <p>
 * Represents ...
 * </p>
 */
    private boolean primaryKey;
    private boolean primitive;
    private String name; 
    private final String comment;
    private Table table;
    private int dataType;
    private String description;
    
   ///////////////////////////////////////
   // associations

/**
 * <p>
 * 
 * </p>
 */
    
    public Field(String name, Table table, String comment, boolean primaryKey, int dataType, boolean primitive) {
        this.name = name;
        this.primaryKey = primaryKey;
        this.table = table;
        this.comment = comment;
        this.dataType = dataType;
        this.primitive = primitive;
        description = null;
    }
    

   ///////////////////////////////////////
   // access methods for associations

    public Table getTable() {
    	return table;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
    	if (description == null) {
            description = DatabaseManager.getInstance().getAWCCatalog().getObject(table.getName(),name).getDescription();
    	}
    	return description;
    }
    
    public boolean isPrimaryKey() {
        return primaryKey;
    }
    
    public String getComment() {
        return comment;
    }
    
    public boolean equals(Object o) {
    	Field f = (Field) o;
    	return f.name.equals(name) && f.table.equals(table);
    }
    
    public int getDataType() {
    	return dataType;
    }

	public int compareTo(Field o) {
		if (o == null) return -1;
		return o.name.compareTo(name);
	}


	public boolean isPrimitive() {
		return primitive;
	}

} // end Field



