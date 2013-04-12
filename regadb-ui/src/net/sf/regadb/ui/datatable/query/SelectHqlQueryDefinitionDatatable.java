package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.forms.SelectForm;

public class SelectHqlQueryDefinitionDatatable extends
		ISelectQueryDefinitionDataTable {

	public SelectHqlQueryDefinitionDatatable(SelectForm<QueryDefinition> form) {
		super(form);
	}

	@Override
	public int getQueryType() {
		return StandardObjects.getHqlQueryQueryType();
	}

	@Override
	public String[] getRowTooltips(QueryDefinition type) {
		return null;
	}
}
