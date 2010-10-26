package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.ui.form.singlePatient.IPatientEventDataTable;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectPatientEvent extends SelectForm<PatientEventValue> {
	private DataTable<PatientEventValue> datatable_;
	private IPatientEventDataTable datatableI_;
	
	public SelectPatientEvent(ObjectTreeNode<PatientEventValue> node) {
		super(tr("form.singlePatient.selectEvent"),node);
        init();
	}
	
	public void init() 
    {
		datatableI_ = new IPatientEventDataTable(this);
        datatable_ = new DataTable<PatientEventValue>(datatableI_, 10);
        addWidget(datatable_);
    }
}