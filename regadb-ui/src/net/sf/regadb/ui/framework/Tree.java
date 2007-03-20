package net.sf.regadb.ui.framework;

import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.RootItem;
import net.sf.regadb.ui.tree.TreeContent;
import net.sf.witty.wt.widgets.WContainerWidget;

public class Tree extends WContainerWidget
{
	private TreeMenuNode selectedTreeNode_ = null;
	private TreeMenuNode rootTreeNode_;
    private TreeContent treeContent_ = new TreeContent();
	
	public Tree(WContainerWidget root)
	{
		super(root);
		
		RootItem rootItem = new RootItem(root);
		root.addWidget(rootItem);
		rootItem.expand();
		rootTreeNode_ = rootItem;
        this.setStyleClass("treeFixed");
		
		selectedTreeNode_ = treeContent_.setContent(rootItem);
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
    
    public TreeContent getTreeContent()
    {
        return treeContent_;
    }
	
	public void init()
	{
		//selecting the initial treenode
		selectedTreeNode_.prograSelectNode();
	}
}
