package net.sf.regadb.ui.datatable.measurement;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectMeasurementForm extends SelectForm<TestResult>
{
	public SelectMeasurementForm(ObjectTreeNode<TestResult> node)
	{
		super(tr("form.patient.selectTestForm"),node);
	}
	
    protected DataTable<TestResult> createDataTable() 
    {
        return new DataTable<TestResult>(new IMeasurementDataTable(this), 10);
    }
}
