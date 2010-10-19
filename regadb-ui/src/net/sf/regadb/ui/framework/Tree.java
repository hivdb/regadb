package net.sf.regadb.ui.framework;

import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.RootItem;
import net.sf.regadb.ui.tree.TreeContent;
import eu.webtoolkit.jwt.WContainerWidget;

public class Tree extends WContainerWidget
{
	private TreeMenuNode selectedTreeNode_ = null;
	private TreeMenuNode rootTreeNode_;
    private TreeContent treeContent_ = new TreeContent();
    
    RootItem rootItem;
	
	public Tree(WContainerWidget root)
	{
		super(root);
		
		rootItem = new RootItem(root);
		
		this.addWidget(rootItem);
		rootItem.expand();
		rootTreeNode_ = rootItem;
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
		selectedTreeNode_ = treeContent_.createNavigation(rootItem);
		setStyleClass("main-tree-content");
		
		//selecting the initial treenode
		selectedTreeNode_.selectNode();
	}
}
