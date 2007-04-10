package net.sf.regadb.ui.datatable.therapy;

import net.sf.regadb.db.Therapy;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;

public class SelectTherapyForm extends WGroupBox implements IForm
{
	private DataTable<Therapy> dataTable_;
	private ITherapyDataTable dataTableI_;
	
	public SelectTherapyForm()
	{
		super(tr("form.patient.selectTherapyForm"));
        init();
	}
	
	public void addFormField(IFormField field)
	{
		
	}

	public WContainerWidget getWContainer()
	{
		return this;
	}

    public void init() 
    {
        dataTableI_ = new ITherapyDataTable();
        dataTable_ = new DataTable<Therapy>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
