package net.sf.regadb.ui.tree;

import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.i8n.WArgMessage;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public abstract class GenericSelectedItem<DataType> extends TreeMenuNode
{
    private DataType type_;
    
    private String textVar_;
    
    public GenericSelectedItem(WTreeNode parent, String text, String textVar)
    {
        super(new WArgMessage(text), parent);
        textVar_ = textVar;
        ((WArgMessage)label().text()).addArgument(textVar_, "");
    }

    public DataType getSelectedItem()
    {
        return type_;
    }

    public void setSelectedItem(DataType item)
    {
        type_ = item;
        
        String value = item==null?"":getArgument(type_);
        ((WArgMessage)label().text()).changeArgument(textVar_, value);
        
        refresh();
    }
    
    public abstract String getArgument(DataType type);

    @Override
    public ITreeAction getFormAction()
    {
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return type_!=null;
    }
}
