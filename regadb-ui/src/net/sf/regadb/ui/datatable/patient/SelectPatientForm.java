package net.sf.regadb.ui.datatable.patient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectPatientForm extends SelectForm<Patient>
{
	private DataTable<Object[]> dataTable_;
	private IPatientDataTable dataTableI_;
	
	public SelectPatientForm(ObjectTreeNode<Patient> node)
	{
		super(tr("form.patient.selectPatientForm"),node);
        init();
	}

    public void init() 
    {
        dataTableI_ = new IPatientDataTable();
        dataTable_ = new PatientDataTable(dataTableI_, 10);
        addWidget(dataTable_);    
    }
}
