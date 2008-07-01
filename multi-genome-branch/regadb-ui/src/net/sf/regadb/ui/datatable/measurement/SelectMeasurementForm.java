package net.sf.regadb.ui.datatable.measurement;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectMeasurementForm extends SelectForm
{
	private DataTable<TestResult> dataTable_;
	private IMeasurementDataTable dataTableI_;
	
	public SelectMeasurementForm()
	{
		super(tr("form.patient.selectTestForm"));
        init();
	}
	
    public void init() 
    {
        dataTableI_ = new IMeasurementDataTable();
        dataTable_ = new DataTable<TestResult>(dataTableI_, 10);
        addWidget(dataTable_);    
    }
}
