package net.sf.regadb.ui.datatable.patient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.framework.widgets.datatable.RowSelector;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WGroupBox;

public class SelectPatientForm extends WGroupBox implements IForm
{
	private DataTable<Patient> dataTable_;
	private IPatientDataTable dataTableI_;
	
	public SelectPatientForm()
	{
		super(tr("form.patient.selectPatientForm"));
		
		dataTableI_ = new IPatientDataTable();
		dataTable_ = new DataTable<Patient>(dataTableI_, new RowSelector(), 10);
		addWidget(dataTable_);
	}
	
	public void addFormField(IFormField field)
	{
		
	}

	public WContainerWidget getWContainer()
	{
		return this;
	}
}
