package com.pharmadm.custom.rega.queryeditor.port;

import com.pharmadm.custom.rega.queryeditor.catalog.AWCPrototypeCatalog;

public interface CatalogBuilder {
	public void fillCatalog(DatabaseConnector connector, AWCPrototypeCatalog catalog);
}
