package net.sf.regadb.ui.tree.items.datasetSettings;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import eu.webtoolkit.jwt.WTreeNode;

public class DatasetAccessSelectedItem extends GenericSelectedItem<SettingsUser>
{
    public DatasetAccessSelectedItem(WTreeNode parent)
    {
        super(parent, "menu.dataset.acces.SelectedItem");
    }

    @Override
    public String getArgument(SettingsUser type) 
    {
        return type.getUid();
    }
}
