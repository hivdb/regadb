package net.sf.regadb.ui.framework;

import net.sf.regadb.ui.framework.tree.RootMenuNode;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.TreeContent;
import eu.webtoolkit.jwt.WContainerWidget;

public class Tree extends WContainerWidget
{
	private TreeMenuNode selectedTreeNode_ = null;
	private RootMenuNode rootTreeNode_;
    private TreeContent treeContent_ = new TreeContent();
    
	public Tree(WContainerWidget root)
	{
		super(root);
		
		rootTreeNode_ = new RootMenuNode();
		this.addWidget(rootTreeNode_);
		rootTreeNode_.expandFromRoot();
	}

	public TreeMenuNode getSelectedTreeNode()
	{
		return selectedTreeNode_;
	}

	public void setSelectedTreeNode(TreeMenuNode selectedTreeNode)
	{
		this.selectedTreeNode_ = selectedTreeNode;
	}

	public RootMenuNode getRootTreeNode()
	{
		return rootTreeNode_;
	}
    
    public TreeContent getTreeContent()
    {
        return treeContent_;
    }
	
	public void init()
	{
		selectedTreeNode_ = treeContent_.createNavigation(rootTreeNode_);
		setStyleClass("main-tree-content");
		
		//selecting the initial treenode
		selectedTreeNode_.selectNode();
	}
}
