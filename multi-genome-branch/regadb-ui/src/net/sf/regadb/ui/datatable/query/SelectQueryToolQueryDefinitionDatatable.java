package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;

public class SelectQueryToolQueryDefinitionDatatable extends
		ISelectQueryDefinitionDataTable {

	@Override
	public int getQueryType() {
		return StandardObjects.getQueryToolQueryType();
	}

	@Override
	public void selectAction(QueryDefinition selectedItem) {
    	RegaDBMain.getApp().getTree().getTreeContent().queryToolSelected.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().queryToolSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().queryToolSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().queryToolSelectedView.selectNode();
	}
}
