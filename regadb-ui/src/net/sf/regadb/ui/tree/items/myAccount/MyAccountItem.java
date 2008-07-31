package net.sf.regadb.ui.tree.items.myAccount;

import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class MyAccountItem extends TreeMenuNode
{
	public MyAccountItem(WTreeNode root)
	{
		super(tr("menu.myAccount.myAccount"), root);
	}
	
	@Override
	public ITreeAction getFormAction()
	{
		return new ITreeAction()
		{
			public void performAction(TreeMenuNode node)
			{
			    getChildren().get(2).prograSelectNode();
			}
		};
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}
}
