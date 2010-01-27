package net.sf.regadb.ui.datatable.importTool;

import net.sf.regadb.ui.form.importTool.data.ImportDefinition;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectImportToolForm extends SelectForm {
	private DataTable<ImportDefinition> datatable_;
	private ImportDefinitionDataTable datatableI_;
	
	public SelectImportToolForm() {
		super(tr("importTool.form.selectImportDefinition"));
        init();
	}
	
	public void init() 
    {
		datatableI_ = new ImportDefinitionDataTable();
        datatable_ = new DataTable<ImportDefinition>(datatableI_, 10);
        addWidget(datatable_);
    }
}
