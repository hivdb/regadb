package net.sf.regadb.ui.datatable.datasetSettings;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectDatasetAccessUserForm extends SelectForm<SettingsUser>
{
    public SelectDatasetAccessUserForm(ObjectTreeNode<SettingsUser> node)
    {
        super(tr("form.dataset.access.select"),node);
    }

    public DataTable<SettingsUser> createDataTable()
    {
        return new DataTable<SettingsUser>(new ISelectDatasetAccessUserDataTable(this), 10);
    }
}