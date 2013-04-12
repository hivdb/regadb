package net.sf.regadb.ui.datatable.settingsUser;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectSettingsUserForm extends SelectForm<SettingsUser>
{
    public SelectSettingsUserForm(ObjectTreeNode<SettingsUser> node)
    {
        super(tr("form.administrator.user.select"),node);
    }
    
    public DataTable<SettingsUser> createDataTable()
    {
        return new DataTable<SettingsUser>(new ISettingsUserDataTable(this), 10);
    }
}