package net.sf.regadb.ui.datatable.datasetAccess;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;

public class SelectDatasetAccessUserForm extends WGroupBox implements IForm
{
    private DataTable<SettingsUser> dataTable_;
    private ISelectDatasetAccessUserDataTable dataTableI_;
    
    public SelectDatasetAccessUserForm()
    {
        super(tr("form.dataset.access.select"));
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
        dataTableI_ = new ISelectDatasetAccessUserDataTable();
        dataTable_ = new DataTable<SettingsUser>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}