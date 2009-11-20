package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.date.DateUtils;
import eu.webtoolkit.jwt.WTreeNode;

public class EventTreeNode extends ObjectTreeNode<PatientEventValue> {

	public EventTreeNode(WTreeNode root) {
		super("patient.event", root);
	}

	@Override
	protected void doAdd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doDelete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doEdit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doSelect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getArgument(PatientEventValue type) {
		if(type != null)
			return DateUtils.format(type.getStartDate()) +" - "
				+ type.getEndDate() == null ? "..." : DateUtils.format(type.getStartDate());
		else
			return "";
	}

	@Override
	public ITreeAction getFormAction() {
		// TODO Auto-generated method stub
		return null;
	}

}
