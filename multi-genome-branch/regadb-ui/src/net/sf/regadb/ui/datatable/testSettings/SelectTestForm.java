package net.sf.regadb.ui.datatable.testSettings;

import net.sf.regadb.db.Test;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectTestForm  extends SelectForm
{
	private DataTable<Test> dataTable_;

	private ITestDataTable dataTableI_;

	public SelectTestForm() 
	{
		super(tr("form.testSetting.test.selectTestForm"));
		init();
	}

	public void init() 
	{
		dataTableI_ = new ITestDataTable();
		dataTable_ = new DataTable<Test>(dataTableI_, 10);
		addWidget(dataTable_);
	}
}
