package net.sf.regadb.ui.datatable.datasetSettings;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectDatasetForm extends SelectForm<Dataset> 
{
	private DataTable<Dataset> dataTable_;

	private IDatasetDataTable dataTableI_;

	public SelectDatasetForm(ObjectTreeNode<Dataset> node) 
	{
		super(tr("form.DatasetSettings.dataset.selectDatasetForm"), node);
		init();
	}

    public void init() 
	{
		dataTableI_ = new IDatasetDataTable(this);
		dataTable_ = new DataTable<Dataset>(dataTableI_, 10);
		addWidget(dataTable_);
	}
}
