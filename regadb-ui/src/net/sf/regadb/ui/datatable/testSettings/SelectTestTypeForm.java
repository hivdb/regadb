package net.sf.regadb.ui.datatable.testSettings;

import net.sf.regadb.db.TestType;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectTestTypeForm extends SelectForm<TestType>
{
    private DataTable<TestType> dataTable_;
    private ITestTypeDataTable dataTableI_;
    
    public SelectTestTypeForm(ObjectTreeNode<TestType> node)
    {
        super(tr("form.testSetting.testType.selectTestTypeForm"),node);
        init();
    }

    public void init() 
    {
        dataTableI_ = new ITestTypeDataTable(this);
        dataTable_ = new DataTable<TestType>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
