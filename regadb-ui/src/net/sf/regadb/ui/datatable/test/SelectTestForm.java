package net.sf.regadb.ui.datatable.test;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;

public class SelectTestForm extends WGroupBox implements IForm
{
	private DataTable<TestResult> dataTable_;
	private ITestDataTable dataTableI_;
	
	public SelectTestForm()
	{
		super(tr("form.patient.selectTestForm"));
        init();
	}
	
	public void addFormField(IFormField field)
	{
		
	}

	public WContainerWidget getWContainer()
	{
		return this;
	}

    public void init() 
    {
        dataTableI_ = new ITestDataTable();
        dataTable_ = new DataTable<TestResult>(dataTableI_, 10);
        addWidget(dataTable_);    
    }
}
