package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.WTreeNode;

public class ActionItem extends TreeMenuNode
{
    private ITreeAction action_;
    
	public ActionItem(WMessage text, WTreeNode root, ITreeAction action)
	{
		super(text, root);
        action_ = action;
	}
    
    public ActionItem(WMessage text, WTreeNode root)
    {
        this(text, root, null);
    }

	@Override
	public ITreeAction getFormAction()
	{
        return action_;
	}

	@Override
	public boolean isEnabled()
	{
		return getParent().isEnabled();
	}
}
