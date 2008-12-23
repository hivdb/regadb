package net.sf.regadb.ui.tree.items.administrator;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import eu.webtoolkit.jwt.WTreeNode;

public class RegisteredUserSelectedItem extends GenericSelectedItem<SettingsUser>
{
    public RegisteredUserSelectedItem(WTreeNode parent) 
    {
        super(parent, "menu.administrator.registeredUserSelectedItem");
    }

    @Override
    public String getArgument(SettingsUser type) 
    {
        return type.getUid();
    }
}
