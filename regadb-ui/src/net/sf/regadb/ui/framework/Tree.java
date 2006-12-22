package net.sf.regadb.ui.framework;

import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.TreeContent;
import net.sf.witty.wt.widgets.WContainerWidget;

public class Tree extends WContainerWidget
{
	private TreeMenuNode selectedTreeNode_ = null;
	private TreeMenuNode rootTreeNode_;
	
	public Tree(WContainerWidget root)
	{
		super(root);
		TreeContent tc = new TreeContent();
		rootTreeNode_ = tc.setContent(root);
	}

	public TreeMenuNode getSelectedTreeNode()
	{
		return selectedTreeNode_;
	}

	public void setSelectedTreeNode(TreeMenuNode selectedTreeNode)
	{
		this.selectedTreeNode_ = selectedTreeNode;
	}

	public TreeMenuNode getRootTreeNode()
	{
		return rootTreeNode_;
	}
}
