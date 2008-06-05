package com.pharmadm.custom.rega.queryeditor.port.jdbc;

import com.pharmadm.custom.rega.queryeditor.catalog.AWCPrototypeCatalog;
import com.pharmadm.custom.rega.queryeditor.port.*;

public class JDBCCatalogBuilder implements CatalogBuilder {
	private AWCPrototypeCatalog catalog;
	
    public void fillCatalog(AWCPrototypeCatalog catalog) {
    	this.catalog = catalog;
    }
}
