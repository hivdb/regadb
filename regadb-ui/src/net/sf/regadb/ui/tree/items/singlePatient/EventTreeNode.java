package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.ui.form.singlePatient.PatientEventForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.date.DateUtils;
import eu.webtoolkit.jwt.WTreeNode;

public class EventTreeNode extends ObjectTreeNode<PatientEventValue> {

	public EventTreeNode(WTreeNode root) {
		super("patient.event", root);
	}

	@Override
	protected void doAdd() {
		setSelectedItem(null);
		RegaDBMain.getApp().getFormContainer().setForm(new PatientEventForm(InteractionState.Adding, getResource("add"), null));
	}

	@Override
	protected void doDelete() {
		RegaDBMain.getApp().getFormContainer().setForm(new PatientEventForm(InteractionState.Deleting, getResource("delete"), getSelectedItem()));
	}

	@Override
	protected void doEdit() {
		RegaDBMain.getApp().getFormContainer().setForm(new PatientEventForm(InteractionState.Editing, getResource("edit"), getSelectedItem()));		
	}

	@Override
	protected void doSelect() {
		RegaDBMain.getApp().getFormContainer().setForm(new SelectPatientEvent());		
	}

	@Override
	protected void doView() {
		RegaDBMain.getApp().getFormContainer().setForm(new PatientEventForm(InteractionState.Viewing, getResource("view"), getSelectedItem()));		
	}

	@Override
	public String getArgument(PatientEventValue type) {
		if(type != null)
			return DateUtils.format(type.getStartDate());
		else
			return "";
	}

}
