package net.sf.regadb.ui.datatable.viralisolate;

import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WGroupBox;

public class SelectViralIsolateForm extends WGroupBox implements IForm
{
	private DataTable<ViralIsolate> dataTable_;
	private IViralIsolateDataTable dataTableI_;
	
	public SelectViralIsolateForm()
	{
		super(tr("form.patient.selectViralIsolateForm"));
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
        dataTableI_ = new IViralIsolateDataTable();
        dataTable_ = new DataTable<ViralIsolate>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
