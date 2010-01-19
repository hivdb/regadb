
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
package com.pharmadm.custom.rega.queryeditor.port;

import java.util.*;

import com.pharmadm.custom.rega.queryeditor.Field;
import com.pharmadm.custom.rega.queryeditor.Table;

/**
 * <p>
 * A Set that contains all Tables that are mentioned in the WhereClauses of
 * the Query. (Relevant as well as non-relevant tables.)
 * </p>
 * 
 */
public class DatabaseTableCatalog {

	// all tables
    private HashMap<String, Table> tables;
    
    
    public DatabaseTableCatalog() {
    	tables = new HashMap<String, Table>();
    }
    
    public void fillCatalog(DatabaseConnector connector) {
    	List<String> tableNames = connector.getTableNames();

    	
    	for (String tableName : tableNames) {
    		List<String> fieldNames = connector.getPrimitiveColumnNames(tableName);
    		List<String> keys = connector.getPrimaryKeys(tableName);
    		HashMap<String,Field> fields = new HashMap<String,Field>();
    		
    		String tableComment = connector.getCommentForTable(tableName);
    		String simpleName = tableName.substring(tableName.lastIndexOf('.')+1);
    		Table table = new Table(simpleName, tableComment);
    		for (String fieldName : fieldNames) {
    			String field_name = fieldName;
    			
    			// used for quick emulation of bug 106
//    			if (fieldName.equals("startDate")) {
//    				field_name =  "startDateTest";
//    			}
    			
    			String fieldComment = connector.getCommentForColumn(tableName, fieldName);
    			int dataType = connector.getColumnType(tableName, fieldName);
    			boolean isKey = keys.contains(fieldName);
    			Field field = new Field(field_name, table, fieldComment, isKey, dataType, true);
    			fields.put(field_name, field);
    			table.addField(field);
    		}
    		
    		fieldNames = connector.getNonPrimitiveColumnNames(tableName);
    		for (String fieldName : fieldNames) {
    			String fieldComment = "";
    			int dataType = 0;
    			boolean isKey = keys.contains(fieldName);
    			Field field = new Field(fieldName, table, fieldComment, isKey, dataType, false);
    			fields.put(fieldName, field);
    			table.addField(field);
    		}    		

    		tables.put(simpleName, table);
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
