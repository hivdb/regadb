package net.sf.regadb.ui.datatable.importTool;

import net.sf.regadb.ui.form.importTool.data.ImportDefinition;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectImportToolForm extends SelectForm<ImportDefinition> {
	public SelectImportToolForm(ObjectTreeNode<ImportDefinition> node) {
		super(tr("importTool.form.selectImportDefinition"),node);
	}
	
	public DataTable<ImportDefinition> createDataTable() 
    {
        return new DataTable<ImportDefinition>(new ImportDefinitionDataTable(this), 10);
    }
}
