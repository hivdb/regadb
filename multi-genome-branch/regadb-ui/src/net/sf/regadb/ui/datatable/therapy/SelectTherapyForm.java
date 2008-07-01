package net.sf.regadb.ui.datatable.therapy;

import net.sf.regadb.db.Therapy;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectTherapyForm extends SelectForm
{
	private DataTable<Therapy> dataTable_;
	private ITherapyDataTable dataTableI_;
	
	public SelectTherapyForm()
	{
		super(tr("form.patient.selectTherapyForm"));
        init();
	}

    public void init() 
    {
        dataTableI_ = new ITherapyDataTable();
        dataTable_ = new DataTable<Therapy>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
