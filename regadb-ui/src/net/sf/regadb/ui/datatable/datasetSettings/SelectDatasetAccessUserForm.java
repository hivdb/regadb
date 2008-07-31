package net.sf.regadb.ui.datatable.datasetSettings;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectDatasetAccessUserForm extends SelectForm
{
    private DataTable<SettingsUser> dataTable_;
    private ISelectDatasetAccessUserDataTable dataTableI_;
    
    public SelectDatasetAccessUserForm()
    {
        super(tr("account.user"));
        init();
    }

    public void init()
    {
        dataTableI_ = new ISelectDatasetAccessUserDataTable();
        dataTable_ = new DataTable<SettingsUser>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}