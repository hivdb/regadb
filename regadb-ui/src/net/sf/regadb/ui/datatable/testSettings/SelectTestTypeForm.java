package net.sf.regadb.ui.datatable.testSettings;

import net.sf.regadb.db.TestType;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;

public class SelectTestTypeForm extends WGroupBox implements IForm
{
    private DataTable<TestType> dataTable_;
    private ITestTypeDataTable dataTableI_;
    
    public SelectTestTypeForm()
    {
        super(tr("form.testSetting.testType.selectTestTypeForm"));
        init();
    }
    
    public void addFormField(IFormField field)
    {
        
    }

    public WContainerWidget getWContainer()
    {
        return this;
    }

    public void init() 
    {
        dataTableI_ = new ITestTypeDataTable();
        dataTable_ = new DataTable<TestType>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}
