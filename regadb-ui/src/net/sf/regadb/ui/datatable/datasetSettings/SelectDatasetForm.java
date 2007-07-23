package net.sf.regadb.ui.datatable.datasetSettings;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectDatasetForm extends SelectForm 
{
	private DataTable<Dataset> dataTable_;

	private IDatasetDataTable dataTableI_;

	public SelectDatasetForm() 
	{
		super(tr("form.DatasetSettings.dataset.selectDatasetForm"));
		init();
	}

    public void init() 
	{
		dataTableI_ = new IDatasetDataTable();
		dataTable_ = new DataTable<Dataset>(dataTableI_, 10);
		addWidget(dataTable_);
	}
}
