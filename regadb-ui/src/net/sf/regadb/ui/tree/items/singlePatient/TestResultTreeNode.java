package net.sf.regadb.ui.tree.items.singlePatient;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.ui.datatable.measurement.SelectMeasurementForm;
import net.sf.regadb.ui.form.singlePatient.MeasurementForm;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.date.DateUtils;
import eu.webtoolkit.jwt.WTreeNode;
import eu.webtoolkit.jwt.WWidget;

public class TestResultTreeNode extends ObjectTreeNode<TestResult>{

	public TestResultTreeNode(WTreeNode root) {
		super("patient.testresult", root);
	}

	@Override
	protected void doAdd() {
		setSelectedItem(null);
		RegaDBMain.getApp().getFormContainer().setForm(new MeasurementForm(InteractionState.Adding, WWidget.tr("form.measurement.add"), null));
	}

	@Override
	protected void doDelete() {
		RegaDBMain.getApp().getFormContainer().setForm(new MeasurementForm(InteractionState.Deleting, WWidget.tr("form.measurement.delete"), getSelectedItem()));
	}

	@Override
	protected void doEdit() {
		RegaDBMain.getApp().getFormContainer().setForm(new MeasurementForm(InteractionState.Editing, WWidget.tr("form.measurement.edit"), getSelectedItem()));
	}

	@Override
	protected void doSelect() {
		RegaDBMain.getApp().getFormContainer().setForm(new SelectMeasurementForm());
	}

	@Override
	protected void doView() {
		RegaDBMain.getApp().getFormContainer().setForm(new MeasurementForm(InteractionState.Viewing, WWidget.tr("form.measurement.view"), getSelectedItem()));
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
}
