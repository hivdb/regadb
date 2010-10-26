package net.sf.regadb.ui.datatable.therapy;

import net.sf.regadb.db.Therapy;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectTherapyForm extends SelectForm<Therapy>
{
	private DataTable<Therapy> dataTable_;
	private ITherapyDataTable dataTableI_;
	
	public SelectTherapyForm(ObjectTreeNode<Therapy> node)
	{
		super(tr("form.patient.selectTherapyForm"),node);
        init();
	}

    public void init() 
    {
        dataTableI_ = new ITherapyDataTable(this);
        dataTable_ = new DataTable<Therapy>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
