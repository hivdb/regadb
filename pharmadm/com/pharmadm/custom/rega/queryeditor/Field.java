
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
public class Field {
 
  ///////////////////////////////////////
  // attributes

/**
 * <p>
 * Represents ...
 * </p>
 */
    private boolean primaryKey;
    
    private String name; 
    
    private final String comment;
    
   ///////////////////////////////////////
   // associations

/**
 * <p>
 * 
 * </p>
 */
    private Table table; 
    
    public Field(String name, Table table, boolean primaryKey) {
        this.name = name;
        this.table = table;
        this.primaryKey = primaryKey;
        this.comment = DatabaseManager.getInstance().getDatabaseConnector().getCommentForColumn(table.getName(), name);
    }
    

   ///////////////////////////////////////
   // access methods for associations

    public Table getTable() {
        return table;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isPrimaryKey() {
        return primaryKey;
    }
    
    public String getComment() {
        return comment;
    }

} // end Field



