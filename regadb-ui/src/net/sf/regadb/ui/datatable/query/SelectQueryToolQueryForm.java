package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectQueryToolQueryForm extends SelectForm {
    private DataTable<QueryDefinition> dataTable_;
    private ISelectQueryDefinitionDataTable dataTableI_;

	public SelectQueryToolQueryForm() {
		super(tr("form.query.querytool.select"));
		init();
	}
	
	private void init() {
        dataTableI_ = new ISelectQueryDefinitionDataTable();
        dataTable_ = new DataTable<QueryDefinition>(dataTableI_, 10);

        addWidget(dataTable_);
	}
}
