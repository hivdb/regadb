package net.sf.regadb.ui.datatable.measurement;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectMeasurementForm extends SelectForm<TestResult>
{
	private DataTable<TestResult> dataTable_;
	private IMeasurementDataTable dataTableI_;
	
	public SelectMeasurementForm(ObjectTreeNode<TestResult> node)
	{
		super(tr("form.patient.selectTestForm"),node);
        init();
	}
	
    public void init() 
    {
        dataTableI_ = new IMeasurementDataTable(this);
        dataTable_ = new DataTable<TestResult>(dataTableI_, 10);
        addWidget(dataTable_);    
    }
}
