package net.sf.regadb.ui.framework.forms;

import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;

public class SelectForm<Type> extends WGroupBox implements IForm {
	private ObjectTreeNode<Type> node;
    
    public SelectForm(WString message, ObjectTreeNode<Type> node) {
        super(message);
        setNode(node);
    }
    
    public void addFormField(IFormField field) {
        
    }

    public WContainerWidget getWContainer() {
        return this;
    }

    public WString leaveForm() {
        return null;
    }

	public void removeFormField(IFormField field) {
		
	}
	
	public void setNode(ObjectTreeNode<Type> node){
		this.node = node;
	}
	public ObjectTreeNode<Type> getNode(){
		return this.node;
	}
}
