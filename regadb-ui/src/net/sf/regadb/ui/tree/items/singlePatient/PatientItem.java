package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.ui.framework.form.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;

public class PatientItem extends TreeMenuNode implements IGetSinglePatient
{
	private Patient selectedPatient_;
	
	public PatientItem()
	{
		super(tr("menu.singlePatient.mainItem"), null);
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
		return true;
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