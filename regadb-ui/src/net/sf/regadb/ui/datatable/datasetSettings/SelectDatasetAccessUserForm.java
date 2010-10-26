package net.sf.regadb.ui.datatable.datasetSettings;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;

public class SelectDatasetAccessUserForm extends SelectForm<SettingsUser>
{
    private DataTable<SettingsUser> dataTable_;
    private ISelectDatasetAccessUserDataTable dataTableI_;
    
    public SelectDatasetAccessUserForm(ObjectTreeNode<SettingsUser> node)
    {
        super(tr("form.dataset.access.select"),node);
        init();
    }

    public void init()
    {
        dataTableI_ = new ISelectDatasetAccessUserDataTable(this);
        dataTable_ = new DataTable<SettingsUser>(dataTableI_, 10);
        addWidget(dataTable_);
    }
}