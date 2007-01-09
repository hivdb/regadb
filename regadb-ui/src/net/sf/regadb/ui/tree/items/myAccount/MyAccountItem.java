package net.sf.regadb.ui.tree.items.myAccount;

import net.sf.regadb.ui.framework.form.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.widgets.WTreeNode;

public class MyAccountItem extends TreeMenuNode
{
	public MyAccountItem(WTreeNode root)
	{
		super(tr("menu.myAccount.myAccount"), root);
	}
	
	@Override
	public ITreeAction getFormAction()
	{
		return null;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}
}
