package net.sf.regadb.ui.framework;

import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.RootItem;
import net.sf.regadb.ui.tree.TreeContent;
import net.sf.witty.wt.widgets.WContainerWidget;

public class Tree extends WContainerWidget
{
	private TreeMenuNode selectedTreeNode_ = null;
	private TreeMenuNode rootTreeNode_;
	
	public Tree(WContainerWidget root)
	{
		super(root);
		
		RootItem rootItem = new RootItem(root);
		root.addWidget(rootItem);
		rootItem.expand();
		rootTreeNode_ = rootItem;
		
		TreeContent tc = new TreeContent();
		selectedTreeNode_ = tc.setContent(rootItem);
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
	
	public void init()
	{
		//selecting the initial treenode
		if(getSelectedTreeNode().getParent()!=null)
			getSelectedTreeNode().getParent().expand();
		getSelectedTreeNode().selectNode();
	}
}
