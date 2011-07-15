package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.datatable.measurement.SelectMeasurementForm;
import net.sf.regadb.ui.form.singlePatient.MeasurementForm;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.date.DateUtils;
import eu.webtoolkit.jwt.WString;

public class TestResultTreeNode extends ObjectTreeNode<TestResult>{

	public TestResultTreeNode(TreeMenuNode parent) {
		super("testResult", parent);
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
	protected ObjectForm<TestResult> createForm(WString name, InteractionState interactionState, TestResult selectedObject) {
		return new MeasurementForm(name, interactionState, TestResultTreeNode.this, selectedObject);
	}

	@Override
	protected IForm createSelectionForm() {
		return new SelectMeasurementForm(this);
	}

	@Override
	protected String getObjectId(TestResult object) {
		return object.getTestResultIi() +"";
	}

	@Override
	protected TestResult getObjectById(Transaction t, String id) {
		return t.getTestResult(Integer.parseInt(id));
	}
}
