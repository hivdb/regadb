package net.sf.regadb.ui.tree;

import net.sf.regadb.db.Privileges;
import net.sf.regadb.ui.framework.forms.FormListener;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import eu.webtoolkit.jwt.WString;

public abstract class ObjectTreeNode<Type> extends DefaultNavigationNode implements FormListener {
	private String resourceName;
	private Type selectedItem;

	private FormNavigationNode select;
	private FormNavigationNode add;
	private SelectedItemNavigationNode<Type> selected;

    private FormNavigationNode view;
	private FormNavigationNode edit;
    private FormNavigationNode delete;

	public ObjectTreeNode(String resourceName, TreeMenuNode parent) {
		super(getMenuResource(resourceName,"main"), parent);
		this.resourceName = resourceName;
		init();
	}
	
	protected void init(){
		selectedItem = null;
		
		select = new FormNavigationNode(getMenuResource("select"), this)
        {
			@Override
			public IForm createForm() {
				return ObjectTreeNode.this.createSelectionForm();
			}
        };
		
		add = new FormNavigationNode(getMenuResource("add"), this)
        {
			@Override
			public void doAction(){
				ObjectTreeNode.this.setSelectedItem(null);
				super.doAction();
			}
			
			@Override
			public ObjectForm<Type> createForm() {
				ObjectForm<Type> f = ObjectTreeNode.this.createForm(getFormResource(InteractionState.Adding),
						InteractionState.Adding, null);
				f.setNode(ObjectTreeNode.this);
				return f;
			}
        };
		
        selected = new SelectedItemNavigationNode<Type>(getMenuResource("selected"), this){

			@Override
			public Type getSelectedItem() {
				return selectedItem;
			}
        	
        };
//		selected = new ActionItem(getResource("selected"), this, new ITreeAction()
//        {
//            public void performAction(TreeMenuNode node) 
//            {
//                doSelected();
//            }
//        });
		selected.getLabel().getText().arg("");
//		selected.disable();

        view = new FormNavigationNode(getMenuResource("view"), selected){
			@Override
			public ObjectForm<Type> createForm() {
				ObjectForm<Type> f = ObjectTreeNode.this.createForm(getFormResource(InteractionState.Viewing),
						InteractionState.Viewing, getSelectedItem());
				f.setNode(ObjectTreeNode.this);
				return f;
			}
        };

        edit = new FormNavigationNode(getMenuResource("edit"), selected){
        	@Override
        	public ObjectForm<Type> createForm(){
        		ObjectForm<Type> f = ObjectTreeNode.this.createForm(getFormResource(InteractionState.Editing),
        				InteractionState.Editing, getSelectedItem());
        		f.setNode(ObjectTreeNode.this);
        		return f;
        	}
        };
        
        delete = new FormNavigationNode(getMenuResource("delete"), selected){
        	@Override
        	public ObjectForm<Type> createForm(){
        		ObjectForm<Type> f = ObjectTreeNode.this.createForm(getFormResource(InteractionState.Deleting),
        				InteractionState.Deleting, getSelectedItem());
        		f.setNode(ObjectTreeNode.this);
        		return f;
        	}
        };
	}
	
	protected abstract ObjectForm<Type> createForm(WString name, InteractionState interactionState, Type selectedObject);
	protected abstract IForm createSelectionForm();
	
	protected String getStateName(InteractionState interactionState){
		switch(interactionState){
		case Adding: return "add";
		case Editing: return "edit";
		case Deleting: return "delete";
		default: return "view";
		}
	}
	
	protected WString getMenuResource(String action){
		return getMenuResource(resourceName, action);
	}
	protected WString getFormResource(String action){
		return getFormResource(resourceName, action);
	}
	protected WString getFormResource(InteractionState state){
		return getFormResource(getStateName(state));
	}
	
	private static WString getMenuResource(String resourceName, String action){
		return WString.tr("menu."+ resourceName +"."+ action);
	}
	private static WString getFormResource(String resourceName, String action){
		return WString.tr("form."+ resourceName +"."+ action);
	}


	public Type getSelectedItem(){
		return selectedItem;
	}

	public void setSelectedItem(Type item){
		if(item != selectedItem)
			getSelectedItemNavigationNode().reset();
		
		selectedItem = item;
		String value = item==null?"":getArgument(item);
        selected.getLabel().getText().changeArg(0, value);
        
        if(item != null){
        	getSelectedItemNavigationNode().expand();
        	getSelectedItemNavigationNode().enable();
        	getViewNavigationNode().selectNode();
        }
        else{
        	getSelectedItemNavigationNode().collapse();
        	getSelectedItemNavigationNode().disable();
        }
        	
        refresh();
    }
	
	public FormNavigationNode getSelectNavigationNode(){
		return select;
	}
	public FormNavigationNode getAddNavigationNode(){
		return add;
	}
	public SelectedItemNavigationNode<Type> getSelectedItemNavigationNode(){
		return selected;
	}
	public FormNavigationNode getViewNavigationNode(){
		return view;
	}
	public FormNavigationNode getEditNavigationNode(){
		return edit;
	}
	public FormNavigationNode getDeleteNavigationNode(){
		return delete;
	}
	
    public abstract String getArgument(Type type);	

	public void applyPrivileges(Privileges priv){
		boolean disabled = priv != Privileges.READWRITE; 
		getAddNavigationNode().setDisabled(disabled);
		getEditNavigationNode().setDisabled(disabled);
		getDeleteNavigationNode().setDisabled(disabled);
	}
	
	public void canceled(IForm form, InteractionState interactionState){
		switch(interactionState){
		case Editing: getViewNavigationNode().selectNode(); break;
		case Deleting: getViewNavigationNode().selectNode(); break;
		default: getSelectNavigationNode().selectNode();
		}
	}
	public void confirmed(IForm form, InteractionState interactionState){
		switch(interactionState){
		case Deleting:
			setSelectedItem(null);
			getSelectNavigationNode().selectNode();
			break;
		default: getViewNavigationNode().selectNode();
		}
	}
}
