package net.sf.regadb.ui.tree;

import eu.webtoolkit.jwt.WContainerWidget;

public class RootItem extends DefaultNavigationNode
{
	public RootItem(WContainerWidget root)
	{
		super(tr("menu.root.rootItem"), null);
	}
}
