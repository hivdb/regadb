package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class ActionAddItem extends TreeMenuNode
{
	public ActionAddItem(WMessage text, WTreeNode root)
	{
		super(text, root);
	}

	@Override
	public ITreeAction getFormAction()
	{
		return new ITreeAction()
		{
			public void performAction(TreeMenuNode node)
			{

			}
		};
	}

	@Override
	public boolean isEnabled()
	{
		return getParent().isEnabled();
	}
}
