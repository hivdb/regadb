package net.sf.regadb.ui.datatable.patient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectPatientForm extends SelectForm
{
	private DataTable<Patient> dataTable_;
	private IPatientDataTable dataTableI_;
	
	public SelectPatientForm()
	{
		super(tr("form.patient.selectPatientForm"));
        init();
	}

    public void init() 
    {
        dataTableI_ = new IPatientDataTable();
        dataTable_ = new DataTable<Patient>(dataTableI_, 10);
        addWidget(dataTable_);    
    }
}
