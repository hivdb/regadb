package net.sf.regadb.ui.datatable.viralisolate;

import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectViralIsolateForm extends SelectForm<ViralIsolate>
{
	public SelectViralIsolateForm(ObjectTreeNode<ViralIsolate> node)
	{
		super(tr("form.patient.selectViralIsolateForm"),node);
	}

    public DataTable<ViralIsolate> createDataTable()
    {
        return new DataTable<ViralIsolate>(new IViralIsolateDataTable(this), 10);
    }
}
