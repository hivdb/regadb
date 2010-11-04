package net.sf.regadb.ui.framework.forms;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;

public abstract class SelectForm<Type> extends WGroupBox implements IForm {
	private ObjectTreeNode<Type> node;
	private DataTable<Type> datatable;
    
    public SelectForm(WString message, ObjectTreeNode<Type> node) {
        super(message);
        setNode(node);
        
        init();
    }
    
    protected void init(){
    	datatable = createDataTable();
    	addWidget(datatable);
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
	
	public void refreshData(){
		Transaction trans = RegaDBMain.getApp().createTransaction();
		datatable.refreshData(trans, true);
		trans.commit();
	}
	
	protected abstract DataTable<Type> createDataTable();
}
