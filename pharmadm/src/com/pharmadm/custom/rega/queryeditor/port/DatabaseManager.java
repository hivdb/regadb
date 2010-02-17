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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.catalog.AWCPrototypeCatalog;
import com.pharmadm.custom.rega.queryeditor.constant.SuggestedValuesOption;



/**
 * Manages a JBDC connection.
 *
 * @author  kdg
 */
public class DatabaseManager {
	private static DatabaseManager instance;
	
	private QueryVisitor visitor;
    private DatabaseTableCatalog tableCatalog;
    private AWCPrototypeCatalog catalog;
    private boolean isTableSelectionAllowed;
    private DatabaseConnectorProvider connectorFactory;
    
    private DatabaseManager(DatabaseConnectorProvider connectorFactory, QueryVisitor queryBuilder, boolean isTableSelectionAllowed) {
    	this.visitor = queryBuilder;
    	catalog = new AWCPrototypeCatalog();
    	tableCatalog = new DatabaseTableCatalog();
    	this.isTableSelectionAllowed = isTableSelectionAllowed;
    	this.connectorFactory = connectorFactory;
    }
    
    private void fillCatalog(CatalogBuilder builder) {
    	DatabaseConnector connector = connectorFactory.createConnector();
		tableCatalog.fillCatalog(connector);
    	builder.fillCatalog(connector, catalog);
		connectorFactory.closeConnector(connector);
    }
    
    public static DatabaseManager getInstance() {
    	return instance;
    }
    
    public static DatabaseManager initInstance(DatabaseConnectorProvider connectorFactory, QueryVisitor queryBuilder, CatalogBuilder catalogBuilder, boolean isTableSelectionAllowed) {
    	if (instance == null) {
    		instance = new DatabaseManager(connectorFactory, queryBuilder, isTableSelectionAllowed);
    		instance.fillCatalog(catalogBuilder);
    	}
    	return instance;
    }
    
    public QueryVisitor getQueryBuilder() {
    	return visitor;
    }
    
    public DatabaseTableCatalog getTableCatalog() {
        return tableCatalog;
    }
    
    public AWCPrototypeCatalog getAWCCatalog() {
    	return catalog;
    }
    
    public boolean isTableSelectionAllowed() {
    	return isTableSelectionAllowed;
    }
    
    public List<SuggestedValuesOption> getSuggestedValues(String query) throws SQLException {
    	DatabaseConnector connector = connectorFactory.createConnector();
		QueryResult rs = connector.executeQuery(query);
		List<SuggestedValuesOption> values = new ArrayList<SuggestedValuesOption>();
		for (int i = 0 ; i < rs.size() ; i++) {
			if (rs.get(i, 0) != null) {
				values.add(new SuggestedValuesOption(rs.get(i,0)));
			}
		}
		rs.close();
		connectorFactory.closeConnector(connector);
		return values;
    }
}
