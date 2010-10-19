package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.ui.form.singlePatient.PatientEventForm;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.date.DateUtils;
import eu.webtoolkit.jwt.WString;

public class EventTreeNode extends ObjectTreeNode<PatientEventValue> {

	public EventTreeNode(TreeMenuNode parent) {
		super("patient.event", parent);
	}

	@Override
	public String getArgument(PatientEventValue type) {
		if(type != null)
			return DateUtils.format(type.getStartDate());
		else
			return "";
	}

	@Override
	protected IForm createForm(WString name, InteractionState interactionState, PatientEventValue selectedObject) {
		return new PatientEventForm(interactionState, name, selectedObject);
	}

	@Override
	protected IForm createSelectionForm() {
		return new SelectPatientEvent();
	}

}
