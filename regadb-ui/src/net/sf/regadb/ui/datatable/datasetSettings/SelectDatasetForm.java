package net.sf.regadb.ui.datatable.datasetSettings;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectDatasetForm extends SelectForm<Dataset> 
{
	public SelectDatasetForm(ObjectTreeNode<Dataset> node) 
	{
		super(tr("form.DatasetSettings.dataset.selectDatasetForm"), node);
	}

    public DataTable<Dataset> createDataTable() 
	{
		return new DataTable<Dataset>(new IDatasetDataTable(this), 10);
	}
}
