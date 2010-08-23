package net.sf.regadb.ui.tree;

import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import eu.webtoolkit.jwt.WTreeNode;

public abstract class GenericSelectedItem<DataType> extends TreeMenuNode
{
    private DataType type_;
    
    public GenericSelectedItem(WTreeNode parent, String text)
    {
        super(tr(text), parent);
        getLabel().getText().arg("");
    }

    public DataType getSelectedItem()
    {
        return type_;
    }

    public void setSelectedItem(DataType item)
    {
        type_ = item;
        
        String value = item==null?"":getArgument(type_);
        getLabel().getText().changeArg(0, value);
        
        refresh();
    }
    
    public abstract String getArgument(DataType type);

    @Override
    public ITreeAction getFormAction()
    {
        return null;
    }
    
	public boolean isEnabled(){
		return super.isEnabled() && getSelectedItem() != null;
	}
}
