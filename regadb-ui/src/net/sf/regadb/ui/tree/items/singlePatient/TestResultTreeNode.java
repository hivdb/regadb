package net.sf.regadb.ui.tree.items.singlePatient;

import eu.webtoolkit.jwt.WTreeNode;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.date.DateUtils;

public class TestResultTreeNode extends ObjectTreeNode<TestResult>{

	public TestResultTreeNode(WTreeNode root) {
		super("patient.testresult", root);
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
	public String getArgument(TestResult type) {
		if(type != null){
			return type.getTest().getDescription() +" "+ DateUtils.format(type.getTestDate());
		}
		else{
			return "";
		}
	}

	@Override
	public ITreeAction getFormAction() {
		// TODO Auto-generated method stub
		return null;
	}

}
