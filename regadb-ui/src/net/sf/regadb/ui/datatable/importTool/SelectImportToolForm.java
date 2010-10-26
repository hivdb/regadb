package net.sf.regadb.ui.datatable.importTool;

import net.sf.regadb.ui.form.importTool.data.ImportDefinition;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectImportToolForm extends SelectForm<ImportDefinition> {
	private DataTable<ImportDefinition> datatable_;
	private ImportDefinitionDataTable datatableI_;
	
	public SelectImportToolForm(ObjectTreeNode<ImportDefinition> node) {
		super(tr("importTool.form.selectImportDefinition"),node);
        init();
	}
	
	public void init() 
    {
		datatableI_ = new ImportDefinitionDataTable(this);
        datatable_ = new DataTable<ImportDefinition>(datatableI_, 10);
        addWidget(datatable_);
    }
}
