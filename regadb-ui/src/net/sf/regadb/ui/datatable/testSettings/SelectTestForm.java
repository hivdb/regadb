package net.sf.regadb.ui.datatable.testSettings;

import net.sf.regadb.db.Test;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectTestForm  extends SelectForm<Test>
{
	private DataTable<Test> dataTable_;

	private ITestDataTable dataTableI_;

	public SelectTestForm(ObjectTreeNode<Test> node) 
	{
		super(tr("form.testSetting.test.selectTestForm"), node);
		init();
	}

	public void init() 
	{
		dataTableI_ = new ITestDataTable(this);
		dataTable_ = new DataTable<Test>(dataTableI_, 10);
		addWidget(dataTable_);
	}
}
