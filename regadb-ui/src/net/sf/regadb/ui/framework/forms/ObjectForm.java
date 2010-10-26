package net.sf.regadb.ui.framework.forms;

import net.sf.regadb.ui.tree.ObjectTreeNode;
import eu.webtoolkit.jwt.WString;

public abstract class ObjectForm<Type> extends FormWidget{
	private ObjectTreeNode<Type> node;
	private Type object;

	public ObjectForm(WString formName, InteractionState interactionState, ObjectTreeNode<Type> node, Type object) {
		super(formName, interactionState);
		
		setNode(node);
		setObject(object);
	}

	public void setNode(ObjectTreeNode<Type> node){
		this.node = node;
	}
	public ObjectTreeNode<Type> getNode(){
		return node;
	}
	
	public void setObject(Type object){
		this.object = object;
	}
	public Type getObject(){
		return object;
	}
	
	public void redirectAfterSave(){
		node.setSelectedItem(getObject());
		node.getViewNavigationNode().selectNode();
	}
	public void redirectAfterDelete(){
		node.setSelectedItem(null);
		node.getSelectNavigationNode().selectNode();
	}
	public void redirectAfterCancel(){
		if(getInteractionState() == InteractionState.Adding)
			node.getSelectNavigationNode().selectNode();
		else
			node.getViewNavigationNode().selectNode();
	}
}
