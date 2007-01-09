package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.form.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.widgets.WTreeNode;

public class PatientAddItem extends TreeMenuNode
{
	public PatientAddItem(WTreeNode root)
	{
		super(tr("menu.singlePatient.patientAddItem"), root);
	}

	@Override
	public ITreeAction getFormAction()
	{
		return new ITreeAction()
		{
			public void performAction(TreeMenuNode node)
			{

			}
		};
	}

	@Override
	public boolean isEnabled()
	{
		return RegaDBMain.getApp().getLogin()!=null;
	}
}
