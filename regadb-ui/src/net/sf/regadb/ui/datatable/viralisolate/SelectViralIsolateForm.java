package net.sf.regadb.ui.datatable.viralisolate;

import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectViralIsolateForm extends SelectForm<ViralIsolate>
{
	private DataTable<ViralIsolate> dataTable_;
	private IViralIsolateDataTable dataTableI_;
	
	public SelectViralIsolateForm(ObjectTreeNode<ViralIsolate> node)
	{
		super(tr("form.patient.selectViralIsolateForm"),node);
        init();
	}

    public void init() 
    {
        dataTableI_ = new IViralIsolateDataTable(this);
        dataTable_ = new DataTable<ViralIsolate>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
