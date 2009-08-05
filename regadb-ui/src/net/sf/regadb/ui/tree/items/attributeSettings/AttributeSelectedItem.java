package net.sf.regadb.ui.tree.items.attributeSettings;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import eu.webtoolkit.jwt.WTreeNode;

public class AttributeSelectedItem extends GenericSelectedItem<Attribute>
{
    public AttributeSelectedItem(WTreeNode parent)
    {
        super(parent, "menu.attributeSettings.attributeSelectedItem");
    }

    @Override
    public String getArgument(Attribute type) 
    {
        return type.getName();
    }
}
