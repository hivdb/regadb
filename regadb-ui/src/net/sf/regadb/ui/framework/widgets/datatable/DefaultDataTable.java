package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.regadb.ui.framework.forms.SelectForm;

public abstract class DefaultDataTable<Type> implements IDataTable<Type>{

	private SelectForm<Type> form;
	
	public DefaultDataTable(SelectForm<Type> form){
		setSelectForm(form);
	}
	
	public void setSelectForm(SelectForm<Type> form){
		this.form = form;
	}
	public SelectForm<Type> getSelectForm(){
		return form;
	}

	@Override
	public void selectAction(Type selectedItem) {
		getSelectForm().getNode().setSelectedItem(selectedItem);
	}
}
