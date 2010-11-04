package net.sf.regadb.ui.datatable.query;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectQueryToolQueryForm extends SelectForm<QueryDefinition> {
	public SelectQueryToolQueryForm(ObjectTreeNode<QueryDefinition> node) {
		super(tr("form.query.querytool.select"), node);
	}
	
	public DataTable<QueryDefinition> createDataTable() {
        return new DataTable<QueryDefinition>(new SelectQueryToolQueryDefinitionDatatable(this), 10);
	}
}
