package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.PatientEventForm;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.date.DateUtils;
import eu.webtoolkit.jwt.WString;

public class PatientEventTreeNode extends ObjectTreeNode<PatientEventValue> {

	public PatientEventTreeNode(TreeMenuNode parent) {
		super("patientEvent", parent);
	}

	@Override
	public String getArgument(PatientEventValue type) {
		if(type != null)
			return DateUtils.format(type.getStartDate());
		else
			return "";
	}

	@Override
	protected ObjectForm<PatientEventValue> createForm(WString name, InteractionState interactionState, PatientEventValue selectedObject) {
		return new PatientEventForm(name, interactionState, PatientEventTreeNode.this, selectedObject);
	}

	@Override
	protected IForm createSelectionForm() {
		return new SelectPatientEvent(this);
	}

	@Override
	protected String getObjectId(PatientEventValue object) {
		return object.getPatientEventValueIi() +"";
	}

	@Override
	protected PatientEventValue getObjectById(Transaction t, String id) {
		return t.getPatientEventValue(Integer.parseInt(id));
	}
}
