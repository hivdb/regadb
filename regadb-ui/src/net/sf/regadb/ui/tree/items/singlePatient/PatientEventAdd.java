package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.PatientEventForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.witty.wt.WResource;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public class PatientEventAdd extends ActionItem {
	public PatientEventAdd(WTreeNode root) {
		super(WResource.tr("menu.singlePatient.event.add"), root, new ITreeAction()
        {
			public void performAction(TreeMenuNode node) {
				RegaDBMain.getApp().getTree().getTreeContent().patientEventSelected.setSelectedItem(null);
				RegaDBMain.getApp().getFormContainer().setForm(new PatientEventForm(InteractionState.Adding, WWidget.tr("menu.singlePatient.event.add"), null));
			}
        });
	}
	
	@Override
	public boolean isEnabled() {
		if ( RegaDBMain.getApp().getLogin() == null )
			return false;
		else {
			Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
			return (t.getEvents().size() > 0);
		}
	}
}
