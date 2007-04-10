package net.sf.regadb.ui.tree.items.attributeSettings;

import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.i8n.WArgMessage;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class AttributeGroupSelectedItem extends TreeMenuNode
{
    AttributeGroup selectedAttributeGroup_;
    
    public AttributeGroupSelectedItem(WTreeNode parent)
    {
        super(new WArgMessage("menu.attributeGroupSettings.attributeGroupSelectedItem"), parent);
        ((WArgMessage)label().text()).addArgument("{attributeGroupSelectedItem}", "");
    }

    public AttributeGroup getSelectedAttributeGroup()
    {
        return selectedAttributeGroup_;
    }

    public void setSelectedAttributeGroup(AttributeGroup selectedAttributeGroup)
    {
        selectedAttributeGroup_ = selectedAttributeGroup;
        
        ((WArgMessage)label().text()).changeArgument("{attributeGroupSelectedItem}", selectedAttributeGroup.getGroupName());
        
        refresh();
    }

    @Override
    public ITreeAction getFormAction()
    {
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return selectedAttributeGroup_!=null;
    }
}
