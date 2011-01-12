package net.sf.regadb.ui.tree.items.myAccount;

import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.util.settings.AccessPolicyConfig.AccessMode;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WTreeNode;

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
	public boolean isDisabled()
	{
		return RegaDBSettings.getInstance().getAccessPolicyConfig().getAccessMode() == AccessMode.INTEGRATED;
	}
}
