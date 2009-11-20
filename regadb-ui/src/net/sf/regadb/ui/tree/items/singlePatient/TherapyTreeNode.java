package net.sf.regadb.ui.tree.items.singlePatient;

import eu.webtoolkit.jwt.WTreeNode;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.date.DateUtils;

public class TherapyTreeNode extends ObjectTreeNode<Therapy>{

	public TherapyTreeNode(WTreeNode root) {
		super("patient.therapy", root);
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
	public String getArgument(Therapy type) {
		if(type != null)
			return DateUtils.format(type.getStartDate()) +" - "
				+ type.getStopDate() == null ? "..." : DateUtils.format(type.getStopDate());
		else
			return "";
	}

	@Override
	public ITreeAction getFormAction() {
		// TODO Auto-generated method stub
		return null;
	}

}
