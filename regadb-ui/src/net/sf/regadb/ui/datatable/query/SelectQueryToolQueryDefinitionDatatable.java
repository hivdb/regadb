package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.forms.SelectForm;

public class SelectQueryToolQueryDefinitionDatatable extends
		ISelectQueryDefinitionDataTable {

	public SelectQueryToolQueryDefinitionDatatable(
			SelectForm<QueryDefinition> form) {
		super(form);
	}

	@Override
	public int getQueryType() {
		return StandardObjects.getQueryToolQueryType();
	}
}
