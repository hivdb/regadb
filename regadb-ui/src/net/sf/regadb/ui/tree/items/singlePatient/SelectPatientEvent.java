package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.ui.form.singlePatient.IPatientEventDataTable;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectPatientEvent extends SelectForm {
	private DataTable<PatientEventValue> datatable_;
	private IPatientEventDataTable datatableI_;
	
	public SelectPatientEvent() {
		super(tr("form.singlePatient.selectEvent"));
        init();
	}
	
	public void init() 
    {
		datatableI_ = new IPatientEventDataTable();
        datatable_ = new DataTable<PatientEventValue>(datatableI_, 10);
        addWidget(datatable_);
    }
}