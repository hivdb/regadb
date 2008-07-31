package net.sf.regadb.ui.tree.items.datasetSettings;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class DatasetAccessSelectedItem extends GenericSelectedItem<SettingsUser>
{
    public DatasetAccessSelectedItem(WTreeNode parent)
    {
        super(parent, "datasetaccess.form", "{userSelectedItem}");
    }

    @Override
    public String getArgument(SettingsUser type) 
    {
        return type.getUid();
    }
}
