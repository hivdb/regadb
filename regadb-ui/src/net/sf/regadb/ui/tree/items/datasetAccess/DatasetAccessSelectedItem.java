package net.sf.regadb.ui.tree.items.datasetAccess;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class DatasetAccessSelectedItem extends GenericSelectedItem<SettingsUser>
{
    public DatasetAccessSelectedItem(WTreeNode parent)
    {
        super(parent, "menu.dataset.acces.SelectedItem", "{userSelectedItem}");
    }

    @Override
    public String getArgument(SettingsUser type) 
    {
        return type.getUid();
    }
}
