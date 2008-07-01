package net.sf.regadb.ui.datatable.viralisolate;

import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectViralIsolateForm extends SelectForm
{
	private DataTable<ViralIsolate> dataTable_;
	private IViralIsolateDataTable dataTableI_;
	
	public SelectViralIsolateForm()
	{
		super(tr("form.patient.selectViralIsolateForm"));
        init();
	}

    public void init() 
    {
        dataTableI_ = new IViralIsolateDataTable();
        dataTable_ = new DataTable<ViralIsolate>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
