package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.ui.form.singlePatient.IPatientEventDataTable;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectPatientEvent extends SelectForm<PatientEventValue> {
	
	public SelectPatientEvent(ObjectTreeNode<PatientEventValue> node) {
		super(tr("form.patientEvent.select"),node);
	}
	
	public DataTable<PatientEventValue> createDataTable() 
    {
		return new DataTable<PatientEventValue>(new IPatientEventDataTable(this), 10);
    }
}