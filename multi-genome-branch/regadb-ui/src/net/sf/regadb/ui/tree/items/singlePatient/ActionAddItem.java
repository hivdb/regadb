package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTreeNode;

public class ActionAddItem extends TreeMenuNode
{
	public ActionAddItem(WString text, WTreeNode root)
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
