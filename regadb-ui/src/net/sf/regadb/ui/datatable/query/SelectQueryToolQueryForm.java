package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectQueryToolQueryForm extends SelectForm<QueryDefinition> {
    private DataTable<QueryDefinition> dataTable_;
    private ISelectQueryDefinitionDataTable dataTableI_;

	public SelectQueryToolQueryForm(ObjectTreeNode<QueryDefinition> node) {
		super(tr("form.query.querytool.select"), node);
		init();
	}
	
	private void init() {
        dataTableI_ = new SelectQueryToolQueryDefinitionDatatable(this);
        dataTable_ = new DataTable<QueryDefinition>(dataTableI_, 10);

        addWidget(dataTable_);
	}
}
