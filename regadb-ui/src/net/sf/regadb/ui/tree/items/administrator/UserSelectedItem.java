package net.sf.regadb.ui.tree.items.administrator;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import eu.webtoolkit.jwt.WTreeNode;

public class UserSelectedItem extends GenericSelectedItem<SettingsUser>
{
    public UserSelectedItem(WTreeNode parent) 
    {
        super(parent, "menu.administrator.userSelectedItem");
    }

    @Override
    public String getArgument(SettingsUser type) 
    {
        return type.getUid();
    }
}
