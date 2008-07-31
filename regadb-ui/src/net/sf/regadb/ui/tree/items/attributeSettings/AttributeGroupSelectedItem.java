package net.sf.regadb.ui.tree.items.attributeSettings;

import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.ui.tree.GenericSelectedItem;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class AttributeGroupSelectedItem extends GenericSelectedItem<AttributeGroup>
{
    public AttributeGroupSelectedItem(WTreeNode parent)
    {
        super(parent, "attributeGroups.form", "{attributeGroupSelectedItem}");
    }

    @Override
    public String getArgument(AttributeGroup type) 
    {
        return type.getGroupName();
    }
}
