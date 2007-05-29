package net.sf.regadb.ui.datatable.datasetSettings;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;

public class SelectDatasetForm extends WGroupBox implements IForm 
{
	private DataTable<Dataset> dataTable_;

	private IDatasetDataTable dataTableI_;

	public SelectDatasetForm() 
	{
		super(tr("form.DatasetSettings.dataset.selectDatasetForm"));
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
		dataTableI_ = new IDatasetDataTable();
		dataTable_ = new DataTable<Dataset>(dataTableI_, 10);
		addWidget(dataTable_);
	}
}
