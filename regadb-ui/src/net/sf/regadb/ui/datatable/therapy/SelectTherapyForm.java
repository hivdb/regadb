package net.sf.regadb.ui.datatable.therapy;

import net.sf.regadb.db.Therapy;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectTherapyForm extends SelectForm<Therapy>
{
	public SelectTherapyForm(ObjectTreeNode<Therapy> node)
	{
		super(tr("form.patient.selectTherapyForm"),node);
	}

    public DataTable<Therapy> createDataTable() 
    {
        return new DataTable<Therapy>(new ITherapyDataTable(this), 10);
    }
}
