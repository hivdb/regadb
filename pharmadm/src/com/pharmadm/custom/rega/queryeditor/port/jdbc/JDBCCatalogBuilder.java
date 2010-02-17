package com.pharmadm.custom.rega.queryeditor.port.jdbc;

import com.pharmadm.custom.rega.queryeditor.catalog.AWCPrototypeCatalog;
import com.pharmadm.custom.rega.queryeditor.port.CatalogBuilder;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseConnector;

public class JDBCCatalogBuilder implements CatalogBuilder {
	private AWCPrototypeCatalog catalog;
	
    public void fillCatalog(DatabaseConnector connector, AWCPrototypeCatalog catalog) {
    	this.catalog = catalog;
    }
}
