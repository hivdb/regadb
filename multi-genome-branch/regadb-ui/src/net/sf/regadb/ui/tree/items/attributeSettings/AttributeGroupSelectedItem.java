package net.sf.regadb.ui.tree.items.attributeSettings;

import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import eu.webtoolkit.jwt.WTreeNode;

public class AttributeGroupSelectedItem extends GenericSelectedItem<AttributeGroup>
{
    public AttributeGroupSelectedItem(WTreeNode parent)
    {
        super(parent, "menu.attributeGroupSettings.attributeGroupSelectedItem");
    }

    @Override
    public String getArgument(AttributeGroup type) 
    {
        return type.getGroupName();
    }
}
