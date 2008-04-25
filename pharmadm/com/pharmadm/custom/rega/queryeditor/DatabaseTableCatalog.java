
/** Java class "DatabaseTableCatalog.java" generated from Poseidon for UML.
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

import com.pharmadm.custom.rega.queryeditor.port.DatabaseConnector;

/**
 * <p>
 * A Set that contains all Tables that are mentioned in the WhereClauses of
 * the Query. (Relevant as well as non-relevant tables.)
 * </p>
 * 
 */
public class DatabaseTableCatalog {

   ///////////////////////////////////////
   // associations

/**
 * <p>
 * 
 * </p>
 */
    
	// all tables
    private HashMap<String, Table> tables;
    
    
    public DatabaseTableCatalog(DatabaseConnector connector) {
    	tables = new HashMap<String, Table>();
    	List<String> tableNames = connector.getTableNames();

    	
    	for (String tableName : tableNames) {
    		List<String> fieldNames = connector.getColumnNames(tableName);
    		List<String> keys = connector.getPrimaryKeys(tableName);
    		HashMap<String,Field> fields = new HashMap<String,Field>();
    		
    		String tableComment = connector.getCommentForTable(tableName);
    		Table table = new Table(tableName, tableComment);
    		for (String fieldName : fieldNames) {
    			String fieldComment = connector.getCommentForColumn(tableName, fieldName);
    			int dataType = connector.getColumnType(tableName, fieldName);
    			boolean isKey = keys.contains(fieldName);
    			Field field = new Field(fieldName, table, fieldComment, isKey, dataType);
    			fields.put(fieldName, field);
    			table.addField(field);
    		}

    		tables.put(tableName, table);
    	}
    }
    
        
    
   ///////////////////////////////////////
   // access methods for associations

    public boolean hasTable(String tableName) {
    	return tables.containsKey(tableName);
    }
    
    public Table getTable(String tableName) {
    	return tables.get(tableName);
    }
    
    

} // end DatabaseTableCatalog
