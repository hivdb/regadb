package net.sf.regadb.ui.datatable.settingsUser;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;

public class SelectSettingsUserForm extends SelectForm
{
    private DataTable<SettingsUser> dataTable_;
    private ISettingsUserDataTable dataTableI_;
    
    public SelectSettingsUserForm(boolean enabled)
    {
        super((enabled?tr("account.select.registered"):tr("account.select.unregistered")));
        init(enabled);
    }
    
    public void init(boolean enabled)
    {
        dataTableI_ = new ISettingsUserDataTable(enabled);
        dataTable_ = new DataTable<SettingsUser>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}