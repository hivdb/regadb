package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;

public class SelectHqlQueryDefinitionDatatable extends
		ISelectQueryDefinitionDataTable {

	@Override
	public int getQueryType() {
		return StandardObjects.getHqlQueryQueryType();
	}

	@Override
	public void selectAction(QueryDefinition selectedItem) {
    	RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelected.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelectedView.selectNode();
	}
	
	@Override
	public String[] getRowTooltips(QueryDefinition type) {
		return null;
	}
}
