/*
 * DatabaseManager.java
 *
 * Created on September 8, 2003, 9:18 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.port;

import com.pharmadm.custom.rega.queryeditor.catalog.AWCPrototypeCatalog;



/**
 * Manages a JBDC connection.
 *
 * @author  kdg
 */
public class DatabaseManager {
	private static DatabaseManager instance;
	
    private DatabaseConnector DbManager;
	private QueryVisitor visitor;
    private DatabaseTableCatalog tableCatalog;
    private AWCPrototypeCatalog catalog;
    
    private DatabaseManager(QueryVisitor queryBuilder, DatabaseConnector conn) {
    	this.visitor = queryBuilder;
    	DbManager = conn;
    	catalog = new AWCPrototypeCatalog();
    }
    
    public void fillCatalog(CatalogBuilder catalogBuilder) {
		catalogBuilder.fillCatalog(catalog);
    }
    
    public static DatabaseManager getInstance() {
    	return instance;
    }
    
    public static DatabaseManager initInstance(QueryVisitor queryBuilder, DatabaseConnector conn) {
    	if (instance == null) {
    		instance = new DatabaseManager(queryBuilder, conn);
    	}
    	return instance;
    }
    
    public QueryVisitor getQueryBuilder() {
    	return visitor;
    }
    
    public DatabaseTableCatalog getTableCatalog() {
    	if (tableCatalog == null) {
        	tableCatalog = new DatabaseTableCatalog(DbManager);
    	}
        return tableCatalog;
    }
    
    public DatabaseConnector getDatabaseConnector() {
    	return DbManager;    	
    }
    
    public AWCPrototypeCatalog getAWCCatalog() {
    	return catalog;
    }
}
