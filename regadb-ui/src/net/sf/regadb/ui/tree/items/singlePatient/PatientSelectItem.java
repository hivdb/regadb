package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.ui.form.singlePatient.PatientSelect;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.forms.action.PutFormAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.widgets.WTreeNode;

public class PatientSelectItem extends TreeMenuNode
{
	public PatientSelectItem(WTreeNode root)
	{
		super(tr("menu.singlePatient.patientSelectItem"), root);
	}

	@Override
	public ITreeAction getFormAction()
	{
		return new PutFormAction(new PatientSelect());
	}
	
	@Override
	public boolean isEnabled()
	{
		return RegaDBMain.getApp().getLogin()!=null;
	}
}
