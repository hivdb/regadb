package net.sf.regadb.ui.tree.items.administrator;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class RegisteredUserSelectedItem extends GenericSelectedItem<SettingsUser>
{
    public RegisteredUserSelectedItem(WTreeNode parent) 
    {
        super(parent, "account.select.registered", "{registeredUserSelectedItem}");
    }

    @Override
    public String getArgument(SettingsUser type) 
    {
        return type.getUid();
    }
}
