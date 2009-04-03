package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.ui.datatable.patient.IPatientDataTable;
import net.sf.regadb.ui.form.singlePatient.SinglePatientForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import eu.webtoolkit.jwt.WTreeNode;
import eu.webtoolkit.jwt.WWidget;

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
				RegaDBMain.getApp().getTree().getTreeContent().patientSelected.setSelectedItem(null);
                RegaDBMain.getApp().getFormContainer().setForm(new SinglePatientForm(InteractionState.Adding, WWidget.tr("form.singlePatient.add"), new Patient()));
			
                IPatientDataTable.clearItems();
			}
		};
	}

	@Override
	public boolean isEnabled()
	{
		return RegaDBMain.getApp().getLogin()!=null;
	}
}
