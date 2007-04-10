package net.sf.regadb.ui.datatable.measurement;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;

public class SelectMeasurementForm extends WGroupBox implements IForm
{
	private DataTable<TestResult> dataTable_;
	private IMeasurementDataTable dataTableI_;
	
	public SelectMeasurementForm()
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
        dataTableI_ = new IMeasurementDataTable();
        dataTable_ = new DataTable<TestResult>(dataTableI_, 10);
        addWidget(dataTable_);    
    }
}
