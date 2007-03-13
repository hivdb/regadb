package net.sf.regadb.ui.tree.items.attributeSettings;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.i8n.WArgMessage;
import net.sf.witty.wt.widgets.WTreeNode;

public class AttributeSelectedItem extends TreeMenuNode
{
    Attribute selectedAttribute_;
    
    public AttributeSelectedItem(WTreeNode parent)
    {
        super(new WArgMessage("menu.attributeSettings.attributeSelectedItem"), parent);
        ((WArgMessage)label().text()).addArgument("{attributeId}", "");
    }

    public Attribute getSelectedAttribute()
    {
        return selectedAttribute_;
    }

    public void setSelectedAttribute(Attribute selectedAttribute)
    {
        selectedAttribute_ = selectedAttribute;
        
        ((WArgMessage)label().text()).changeArgument("{attributeId}", selectedAttribute.getName());
        
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
        return selectedAttribute_!=null;
    }
}
