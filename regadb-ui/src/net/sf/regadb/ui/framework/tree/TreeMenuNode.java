package net.sf.regadb.ui.framework.tree;

import java.util.ArrayList;

import net.sf.regadb.ui.framework.RegaDBApplication;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.form.action.ITreeAction;
import net.sf.witty.event.SignalListener;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.WTreeNode;
import net.sf.witty.wt.widgets.event.WMouseEvent;

public abstract class TreeMenuNode extends WTreeNode
{
	public TreeMenuNode(WMessage intlText, WTreeNode root)
	{
		super(intlText, null, root, true);
		
		setLabelIcon(null);
		
		setImagePack("pics/");
		
		childCountLabel_.setHidden(true);
		
		setStyle();
		
		label().clicked.addListener(new SignalListener<WMouseEvent>()
		{
			public void notify(WMouseEvent a)
			{
				if(isEnabled())
				{
					TreeMenuNode formerSelected = RegaDBMain.getApp().getTree().getSelectedTreeNode();
					if(formerSelected!=null)
					{
						formerSelected.unselect();
					}
					
					select();
					RegaDBMain.getApp().getTree().setSelectedTreeNode(TreeMenuNode.this);
					
					openOnlyOneMenuPath();
					if(childNodes().size()>0)
					{
					expand();
					}
					
					for(WTreeNode node : childNodes())
					{
						if(node instanceof TreeMenuNode)
						{
							((TreeMenuNode)node).refresh();
						}
					}
					
					getFormAction().performAction(TreeMenuNode.this);
				}
			}
		});
	}
	
	private void setStyle()
	{
		if(isEnabled())
		{
			label().setStyleClass("treemenu-menuItem");
		}
		else
		{
			label().setStyleClass("treemenu-disabledMenuItem");
		}
	}
	
	public TreeMenuNode(WMessage intlText)
	{
		this(intlText, null);
	}
	
	public ArrayList<TreeMenuNode> getChildren()
	{
		ArrayList<TreeMenuNode> treeMenuNodeList = new ArrayList<TreeMenuNode>();
		
		for(WTreeNode node : childNodes())
		{
			if(node instanceof TreeMenuNode)
			{
				treeMenuNodeList.add((TreeMenuNode)node);
			}
		}
		
		return treeMenuNodeList;
	}
	
	public TreeMenuNode getParent()
	{
		return (TreeMenuNode)this.parentNode_;
	}
	
	public TreeMenuNode findChild(String intlKey)
	{
		return findChildInNode(this, intlKey, false);
	}
	
	private static TreeMenuNode findChildInNode(TreeMenuNode rootNode, String intlKey, boolean deep)
	{
		if(((TreeMenuNode)rootNode).label().message().key().equals(intlKey))
		{
			return rootNode;
		}
		
		for(WTreeNode node : rootNode.childNodes())
		{
			if(node instanceof TreeMenuNode)
			{
				if(((TreeMenuNode)node).label().message().key().equals(intlKey))
				{
					return (TreeMenuNode)node;
				}
				else if (deep)
				{
					TreeMenuNode tempNode = findChildInNode((TreeMenuNode)node, intlKey, deep);
					if(tempNode!=null)
						return tempNode;
				}
			}
		}
		return null;
	}
	
	public TreeMenuNode findDeepChild(String intlKey)
	{
		return findChildInNode(this, intlKey, true);
	}
	
	public void select()
	{
		if(isEnabled())
		{
			label().setStyleClass("treemenu-selectedMenuItem");
		}
		else
		{
			label().setStyleClass("treemenu-disabledMenuItem");
		}
	}
	
	public void unselect()
	{
		if(isEnabled())
		{
			label().setStyleClass("treemenu-menuItem");
		}
		else
		{
			label().setStyleClass("treemenu-disabledMenuItem");
		}
	}
	
	@Override
	public void refresh()
	{
		super.refresh();
		setStyle();
		if(RegaDBMain.getApp().getTree().getSelectedTreeNode()==this)
		{
			select();
		}
	}
	
	public void openOnlyOneMenuPath()
	{
		if(getParent()==null)
		{
		return;	
		}
		
		for(WTreeNode node : getParent().childNodes())
		{
			if(node.expanded() && node!=this)
			{
				node.collapse();
			}
		}
	}

	public abstract ITreeAction getFormAction();
	
	public abstract boolean isEnabled();
}
