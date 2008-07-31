package net.sf.regadb.ui.datatable.patient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.util.pair.Pair;

public class SelectPatientForm extends SelectForm
{
	private DataTable<Pair<Patient,PatientAttributeValue>> dataTable_;
	private IPatientDataTable dataTableI_;
	
	public SelectPatientForm()
	{
		super(tr("patient.form"));
        init();
	}

    public void init() 
    {
        dataTableI_ = new IPatientDataTable();
        dataTable_ = new PatientDataTable(dataTableI_, 10);
        addWidget(dataTable_);    
    }
}
