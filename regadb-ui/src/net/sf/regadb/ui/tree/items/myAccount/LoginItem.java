package net.sf.regadb.ui.tree.items.myAccount;

import net.sf.regadb.ui.forms.login.LoginForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.forms.action.PutFormAction;
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
		return new PutFormAction(new LoginForm());
	}

	@Override
	public boolean isEnabled()
	{
		return RegaDBMain.getApp().getLogin()==null;
	}
}
