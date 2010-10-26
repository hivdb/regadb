package net.sf.regadb.ui.datatable.settingsUser;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectSettingsUserForm extends SelectForm<SettingsUser>
{
    private DataTable<SettingsUser> dataTable_;
    private ISettingsUserDataTable dataTableI_;
    
    public SelectSettingsUserForm(ObjectTreeNode<SettingsUser> node)
    {
        super(tr("form.administrator.user.select"),node);
        init();
    }
    
    public void init()
    {
        dataTableI_ = new ISettingsUserDataTable(this);
        dataTable_ = new DataTable<SettingsUser>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}