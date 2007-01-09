package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.form.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.widgets.WTreeNode;

public class PatientItem extends TreeMenuNode implements IGetSinglePatient
{
	private Patient selectedPatient_;
	
	public PatientItem(WTreeNode root)
	{
		super(tr("menu.singlePatient.mainItem"), root);
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

	public Patient getSelectedPatient()
	{
		return selectedPatient_;
	}

	public void setSelectedPatient(Patient selectedPatient)
	{
		this.selectedPatient_ = selectedPatient;
	}
}