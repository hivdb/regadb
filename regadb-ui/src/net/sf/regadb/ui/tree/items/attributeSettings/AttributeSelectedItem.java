package net.sf.regadb.ui.tree.items.attributeSettings;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class AttributeSelectedItem extends GenericSelectedItem<Attribute>
{
    public AttributeSelectedItem(WTreeNode parent)
    {
        super(parent, "menu.attributeSettings.attributeSelectedItem", "{attributeSelectedItem}");
    }

    @Override
    public String getArgument(Attribute type) 
    {
        return type.getName();
    }
}
