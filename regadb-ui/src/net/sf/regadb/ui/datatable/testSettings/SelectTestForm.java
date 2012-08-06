package net.sf.regadb.ui.datatable.testSettings;

import net.sf.regadb.db.Test;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectTestForm  extends SelectForm<Test>
{
	public SelectTestForm(ObjectTreeNode<Test> node) 
	{
		super(tr("form.testSettings.test.selectForm"), node);
	}

	public DataTable<Test> createDataTable() 
	{
		return new DataTable<Test>(new ITestDataTable(this), 10);
	}
}
