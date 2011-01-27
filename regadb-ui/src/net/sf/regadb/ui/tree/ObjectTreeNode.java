package net.sf.regadb.ui.tree;

import net.sf.regadb.db.Privileges;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.items.singlePatient.ActionItem;
import eu.webtoolkit.jwt.WResource;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTreeNode;

public abstract class ObjectTreeNode<Type> extends TreeMenuNode{
	private String resourceName;
	private Type selectedItem;

	private ActionItem select;
	private ActionItem add;
	private ActionItem selected;

    private ActionItem view;
	private ActionItem edit;
    private ActionItem delete;

	public ObjectTreeNode(String resourceName, WTreeNode root) {
		super(getResource(resourceName,"main"), root);
		this.resourceName = resourceName;
		init();
	}
	
	protected void init(){
		selectedItem = null;
		
		select = new ActionItem(getResource("select"), this, new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                doSelect();
            }
        });
		
		add = new ActionItem(getResource("add"), this, new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                doAdd();
            }
        });
		
		selected = new ActionItem(getResource("selected"), this, new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                doSelected();
            }
        });
		selected.getLabel().getText().arg("");
		selected.disable();
		
		view = new ActionItem(getResource("view"), selected, new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                doView();
            }
        });
		
		edit = new ActionItem(getResource("edit"), selected, new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                doEdit();
            }
        });
		
		delete = new ActionItem(getResource("delete"), selected, new ITreeAction()
        {
            public void performAction(TreeMenuNode node) 
            {
                doDelete();
            }
        });
	}
	
	protected WString getResource(String action){
		return getResource(resourceName, action);
	}
	private static WString getResource(String resourceName, String action){
		return WResource.tr("menu."+ resourceName +"."+ action);
	}

	public Type getSelectedItem(){
		return selectedItem;
	}

	public void setSelectedItem(Type item){
		selectedItem = item;
		String value = item==null?"":getArgument(item);
        selected.getLabel().getText().changeArg(0, value);
        
        if(item != null){
        	getSelectedActionItem().enable();
        	getSelectedActionItem().expand();
        	getViewActionItem().selectNode();
        }
        else{
        	getSelectedActionItem().collapse();
        	getSelectedActionItem().disable();
        }
        	
        refresh();
    }
	
	public ActionItem getSelectActionItem(){
		return select;
	}
	public ActionItem getAddActionItem(){
		return add;
	}
	public ActionItem getSelectedActionItem(){
		return selected;
	}
	public ActionItem getViewActionItem(){
		return view;
	}
	public ActionItem getEditActionItem(){
		return edit;
	}
	public ActionItem getDeleteActionItem(){
		return delete;
	}
	
	public ITreeAction getFormAction()
	{
		return new ITreeAction()
		{
			public void performAction(TreeMenuNode node)
			{
			    getChildren().get(0).prograSelectNode();
			}
		};
	}
    
    public abstract String getArgument(Type type);	

	protected abstract void doSelect();
	protected abstract void doAdd();
	protected abstract void doView();
	protected abstract void doEdit();
	protected abstract void doDelete();
	
	protected void doSelected(){
		getViewActionItem().selectNode();
	}
	
	public void applyPrivileges(Privileges priv){
		boolean disabled = priv != Privileges.READWRITE; 
		getAddActionItem().setDisabled(disabled);
		getEditActionItem().setDisabled(disabled);
		getDeleteActionItem().setDisabled(disabled);
	}
}
