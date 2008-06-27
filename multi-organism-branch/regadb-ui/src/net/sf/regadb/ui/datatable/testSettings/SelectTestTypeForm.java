package net.sf.regadb.ui.datatable.testSettings;

import net.sf.regadb.db.TestType;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectTestTypeForm extends SelectForm
{
    private DataTable<TestType> dataTable_;
    private ITestTypeDataTable dataTableI_;
    
    public SelectTestTypeForm()
    {
        super(tr("form.testSetting.testType.selectTestTypeForm"));
        init();
    }

    public void init() 
    {
        dataTableI_ = new ITestTypeDataTable();
        dataTable_ = new DataTable<TestType>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
