package net.sf.regadb.ui.tree;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import eu.webtoolkit.jwt.WString;

public abstract class FormNavigationNode extends TreeMenuNode {

	private IForm form;
	
	public FormNavigationNode(WString name, TreeMenuNode parent) {
		super(name, parent);
	}

	@Override
	public void doAction() {
		if(form == null)
			form = createForm();
		else{
			if(form instanceof SelectForm)
				((SelectForm)form).refreshData();
			else{
				form = createForm();
			}
		}
		
		RegaDBMain.getApp().getFormContainer().setForm(form);
	}
	
	public void reset(){
		form = null;
	}

	public abstract IForm createForm();
}
