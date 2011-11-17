package net.sf.regadb.ui.tree;

import java.util.EnumSet;
import java.util.Set;

import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
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
    
    private Set<InteractionState> states;

	public ObjectTreeNode(String resourceName, TreeMenuNode parent) {
		this(resourceName, parent,
				EnumSet.of(	InteractionState.Viewing,
							InteractionState.Adding,
							InteractionState.Editing,
							InteractionState.Deleting));
	}
	
	public ObjectTreeNode(String resourceName, TreeMenuNode parent, Set<InteractionState> states){
		super(getMenuResource(resourceName,"main"), parent);
		this.resourceName = resourceName;
		this.states = states;
		init();
	}
	
	protected void init(){
		selectedItem = null;
		
		select = new FormNavigationNode(getMenuResource("select"), this, true)
        {
			@Override
			public IForm createForm() {
				return ObjectTreeNode.this.createSelectionForm();
			}
			
			@Override
			public String getInternalPath(){
				return getParentNode().getInternalPath();
			}
			
			@Override
			public boolean matchesInternalPath(String[] path, int depth){
				return path.length == depth;
			}
        };
		
        if(canAdd()){
			add = new FormNavigationNode(getMenuResource("add"), this, false)
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
        }
		
        selected = new SelectedItemNavigationNode<Type>(getMenuResource("selected"), this){

			@Override
			public Type getSelectedItem() {
				return selectedItem;
			}
        	
			@Override
			public String getMyInternalPath(){
				WString w = WString.tr(getLabel().getText().getKey());
				
				String path = w.getValue()
					.replace(' ','_')
					.replace('/','-')
					.toLowerCase();
				
				return path.replace("{1}",
						selectedItem == null ? "" : getObjectId(selectedItem));
			}
			
			@Override
			public boolean matchesInternalPath(String[] path, int depth){
				if(depth >= path.length)
					return false;
				if(super.matchesInternalPath(path, depth))
					return true;
				
				String s = WString.tr(getLabel().getText().getKey()).getValue()
					.replace(' ', '_')
					.replace('/', '-')
					.toLowerCase();
				int i = s.indexOf('[');
				if(path[depth].startsWith(s.substring(0, i))){
					i = path[depth].indexOf('[');
					int j = path[depth].indexOf(']');
					
					String id = path[depth].substring(i+1,j);
					
					Transaction t = RegaDBMain.getApp().createTransaction();
					Type object = getObjectById(t, id);
					t.commit();

					if(object != null){
						setSelectedItem(object);
						return true;
					}
				}
				return false;
			}
        };
		selected.getLabel().getText().arg("");

		if(canView()){
	        view = new FormNavigationNode(getMenuResource("view"), selected, true){
				@Override
				public ObjectForm<Type> createForm() {
					ObjectForm<Type> f = ObjectTreeNode.this.createForm(getFormResource(InteractionState.Viewing),
							InteractionState.Viewing, getSelectedItem());
					f.setNode(ObjectTreeNode.this);
					return f;
				}
				
				@Override
				public String getInternalPath(){
					return getParentNode().getInternalPath();
				}
				
				@Override
				public boolean matchesInternalPath(String[] path, int depth){
					return path.length == depth;
				}
	        };
		}

		if(canEdit()){
	        edit = new FormNavigationNode(getMenuResource("edit"), selected, false){
	        	@Override
	        	public ObjectForm<Type> createForm(){
	        		ObjectForm<Type> f = ObjectTreeNode.this.createForm(getFormResource(InteractionState.Editing),
	        				InteractionState.Editing, getSelectedItem());
	        		f.setNode(ObjectTreeNode.this);
	        		return f;
	        	}
	        };
		}

		if(canDelete()){
	        delete = new FormNavigationNode(getMenuResource("delete"), selected, true){
	        	@Override
	        	public ObjectForm<Type> createForm(){
	        		ObjectForm<Type> f = ObjectTreeNode.this.createForm(getFormResource(InteractionState.Deleting),
	        				InteractionState.Deleting, getSelectedItem());
	        		f.setNode(ObjectTreeNode.this);
	        		return f;
	        	}
	        };
		}
	}
	
	protected abstract ObjectForm<Type> createForm(WString name, InteractionState interactionState, Type selectedObject);
	protected abstract IForm createSelectionForm();
	
	protected abstract String getObjectId(Type object);
	protected abstract Type getObjectById(Transaction t, String id);
	
    public String getArgument(Type object){
    	return getObjectId(object);
    }

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
		return tr("menu."+ resourceName +"."+ action);
	}
	private static WString getFormResource(String resourceName, String action){
		return tr("form."+ resourceName +"."+ action);
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
			getSelectedItemNavigationNode().enable();
			getSelectedItemNavigationNode().expand();
			getSelectedItemNavigationNode().selectNode();
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
	
	public Set<InteractionState> getStates(){
		return states;
	}
	
	public boolean canAdd(){
		return states.contains(InteractionState.Adding);
	}
	public boolean canEdit(){
		return states.contains(InteractionState.Editing);
	}
	public boolean canView(){
		return states.contains(InteractionState.Viewing);
	}
	public boolean canDelete(){
		return states.contains(InteractionState.Deleting);
	}
}
