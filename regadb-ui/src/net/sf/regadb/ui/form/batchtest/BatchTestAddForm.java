package net.sf.regadb.ui.form.batchtest;

import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.witty.wt.i8n.WMessage;

public class BatchTestAddForm extends FormWidget {
	private ComboBox<Test> cmbTest;
	
	public BatchTestAddForm(WMessage formName, InteractionState interactionState, boolean literal) {
		super(formName, interactionState, literal);
		init();
	}
	
	private void init()
    {
		FormTable table = new FormTable(this);
		Label testL = new Label(tr("test.form"));
		cmbTest = new ComboBox<Test>(getInteractionState(), this);
		cmbTest.setMandatory(true);
		table.addLineToTable(testL, cmbTest);
		
		
		fillData();
		
		addControlButtons();
	}
	
	private void fillData() {
		Transaction tr = RegaDBMain.getApp().createTransaction();
		
		cmbTest.clearItems();
        for(Test t : tr.getTests())
        {
        	if ( t.getAnalysis() != null && !BatchTestRunningForm.containsRunningTest(t) ) {
            	cmbTest.addItem(new DataComboMessage<Test>(t, t.getDescription()));
        	}
        }
    	cmbTest.sort();
    	cmbTest.addNoSelectionItem();
    	cmbTest.selectIndex(0);
	}
	
	@Override
	public void cancel() {
		redirectToView(RegaDBMain.getApp().getTree().getTreeContent().batchTestRunning, RegaDBMain.getApp().getTree().getTreeContent().batchTestRunning);
	}
	
	@Override
	public WMessage deleteObject() {
		return null;
	}
	
	@Override
	public void redirectAfterDelete() {}
	
	@Override
	public void saveData() {
		Test t = cmbTest.currentValue();
		BatchTestRunningForm.runTest(t);
		try { Thread.sleep(100); } catch ( InterruptedException e ) { e.printStackTrace(); }
		redirectToView(RegaDBMain.getApp().getTree().getTreeContent().batchTestRunning, RegaDBMain.getApp().getTree().getTreeContent().batchTestRunning);
	}
}
