package net.sf.regadb.ui.tree;

import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.WContainerWidget;

public class RootItem extends TreeMenuNode
{
	public RootItem(WContainerWidget root)
	{
		super(tr("menu.root.rootItem"));
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
