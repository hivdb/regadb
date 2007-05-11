package net.sf.regadb.ui.datatable.settingsUser;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;

public class SelectSettingsUserForm extends WGroupBox implements IForm
{
    private DataTable<SettingsUser> dataTable_;
    private ISettingsUserDataTable dataTableI_;
    
    public SelectSettingsUserForm(boolean enabled)
    {
        super((enabled?tr("form.administrator.registeredUser.select"):tr("form.administrator.notRegisteredUser.select")));
        init(enabled);
    }
    
    public void addFormField(IFormField field)
    {
        
    }

    public WContainerWidget getWContainer()
    {
        return this;
    }

    public void init(boolean enabled)
    {
        dataTableI_ = new ISettingsUserDataTable(enabled);
        dataTable_ = new DataTable<SettingsUser>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}