package net.sf.regadb.ui.tree.items.myAccount;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.form.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.widgets.WTreeNode;

public class LoginItem extends TreeMenuNode
{
	public LoginItem(WTreeNode root)
	{
		super(tr("menu.myAccount.login"), root);
	}
	
	@Override
	public ITreeAction getFormAction()
	{
		return new ITreeAction()
		{
			public void performAction(TreeMenuNode node)
			{
				RegaDBMain.getApp().login("plibin0", "xqeyiopln234");
				RegaDBMain.getApp().getTree().getRootTreeNode().refresh();
			}
		};
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}
}
