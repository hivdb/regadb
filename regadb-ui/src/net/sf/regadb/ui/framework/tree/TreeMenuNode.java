package net.sf.regadb.ui.framework.tree;

import java.util.ArrayList;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTreeNode;

public abstract class TreeMenuNode extends WTreeNode
{
	public TreeMenuNode(WString intlText, WTreeNode root)
	{
		super(intlText, null, root);
		
		setInteractive(false);
		setLabelIcon(null);
		
		setImagePack("pics/");
		
		this.setChildCountPolicy(ChildCountPolicy.Disabled);
		
		setStyle();
		
		getLabel().clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
		{
			public void trigger(WMouseEvent a)
			{
                IForm form = RegaDBMain.getApp().getFormContainer().getForm();
                
                WString leaveMessage = null;
                if(form!=null)
                    leaveMessage = form.leaveForm();
                
                if(leaveMessage==null)
                    selectNode();
                else
                	UIUtils.showWarningMessageBox(TreeMenuNode.this, leaveMessage);
			}
		});
	}
	
	public void prograSelectNode()
	{
		if(getParent()!=null)
			getParentNode().expand();	
		selectNode();
	}
	
	public void selectNode()
	{
		if(isEnabled())
		{
			TreeMenuNode formerSelected = null;
			formerSelected = RegaDBMain.getApp().getTree().getSelectedTreeNode();

			if(formerSelected!=null)
			{
				formerSelected.unselect();
			}
			
			select();
			RegaDBMain.getApp().getTree().setSelectedTreeNode(TreeMenuNode.this);
			
			openOnlyOneMenuPath();
			if(getChildNodes().size()>0)
			{
			expand();
			}
			
			this.refresh();
			
			doAction();
			
			if(!RegaDBMain.getApp().getInternalPath().equals(getInternalPath())){
				RegaDBMain.getApp().setInternalPath(getInternalPath());
				RegaDBMain.getApp().setTitle(getInternalPath());
			}
		}
	}

	
	private void setStyle()
	{
		if(isEnabled())
		{
			getLabel().setStyleClass("treemenu-menuItem");
		}
		else
		{
			getLabel().setStyleClass("treemenu-disabledMenuItem");
		}
	}
	
	public TreeMenuNode(WString intlText)
	{
		this(intlText, null);
	}
	
	public ArrayList<TreeMenuNode> getChildren()
	{
		ArrayList<TreeMenuNode> treeMenuNodeList = new ArrayList<TreeMenuNode>();
		
		for(WTreeNode node : getChildNodes())
		{
			if(node instanceof TreeMenuNode)
			{
				treeMenuNodeList.add((TreeMenuNode)node);
			}
		}
		
		return treeMenuNodeList;
	}
	
	public TreeMenuNode getParentNode()
	{
		return (TreeMenuNode)super.getParentNode();
	}
	
	public TreeMenuNode findChild(String intlKey)
	{
		return findChildInNode(this, intlKey, false);
	}
	
	private static TreeMenuNode findChildInNode(TreeMenuNode rootNode, String intlKey, boolean deep)
	{
		if(UIUtils.keyOrValue(((TreeMenuNode)rootNode).getLabel().getText()).equals(intlKey))
		{
			return rootNode;
		}
		
		for(WTreeNode node : rootNode.getChildNodes())
		{
			if(node instanceof TreeMenuNode)
			{
				if(UIUtils.keyOrValue(((TreeMenuNode)node).getLabel().getText()).equals(intlKey))
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
	
	protected void select()
	{
		if(isEnabled())
		{
			getLabel().setStyleClass("treemenu-selectedMenuItem");
		}
		else
		{
			getLabel().setStyleClass("treemenu-disabledMenuItem");
		}
	}
	
	protected void unselect()
	{
		if(isEnabled())
		{
			getLabel().setStyleClass("treemenu-menuItem");
		}
		else
		{
			getLabel().setStyleClass("treemenu-disabledMenuItem");
		}
	}
	
	@Override
	public void refresh()
	{
		super.refresh();
		setDisabled(isDisabled());
		setStyle();
		if(RegaDBMain.getApp().getTree().getSelectedTreeNode()==this)
		{
			select();
		}
	}
	
	public void openOnlyOneMenuPath()
	{
		if(getParentNode()==null)
			return;	
		else{
			for(WTreeNode node : getParentNode().getChildNodes())
			{
				if(node.isExpanded() && node!=this)
				{
					node.collapse();
				}
			}
			
			getParentNode().openOnlyOneMenuPath();
		}
	}
	
	public void expandFromRoot(){
		if(getParentNode() != null)
			getParentNode().expandFromRoot();
		
		if(!isExpanded() && getChildNodes().size() > 0)
			expand();
	}

	public String getMyInternalPath(){
		return getLabel().getText().getValue()
			.replace(' ', '_')
			.replace('/', '-')
			.toLowerCase();
	}
	public String getInternalPath() {
		TreeMenuNode node = (TreeMenuNode)this.getParentNode();
		return node==null?"":node.getInternalPath() + "/" + getMyInternalPath();
	}
	
	public boolean matchesInternalPath(String[] path, int depth){
		if(depth < path.length)
			return path[depth].equals(getMyInternalPath());
		else
			return false;
	}

	public boolean gotoInternalPath(String[] path, int depth){
		if(matchesInternalPath(path, depth)){
			doAction();
			
			for(TreeMenuNode child : getChildren()){
				if(child.gotoInternalPath(path, depth+1)){
					return true;
				}
			}
		}
		return false;
	}
	
	public abstract void doAction();
	
	public void reset(){
		for(WTreeNode n : getChildNodes())
			if(n instanceof TreeMenuNode)
				((TreeMenuNode)n).reset();
	}
}
