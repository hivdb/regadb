package net.sf.regadb.ui.datatable.patient;

import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectPatientForm extends SelectForm
{
	private DataTable<Object[]> dataTable_;
	private IPatientDataTable dataTableI_;
	
	public SelectPatientForm()
	{
		super(tr("form.patient.selectPatientForm"));
        init();
	}

    public void init() 
    {
        dataTableI_ = new IPatientDataTable();
        dataTable_ = new PatientDataTable(dataTableI_, 10);
        addWidget(dataTable_);    
    }
}
