package net.sf.regadb.ui.form.batchtest;

import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WResource;
import net.sf.witty.wt.i8n.WMessage;

public class BatchTestAddForm extends FormWidget {
	private WComboBox cmbTest;
	private WPushButton cmdRun;
	
	public BatchTestAddForm(WMessage formName, InteractionState interactionState) {
		super(formName, interactionState);
		init();
	}
	
	@SuppressWarnings("unchecked")
	private void init()
    {
		WGroupBox addGroup = new WGroupBox(WResource.tr("form.batchtest.add.title"), this);
		
		cmbTest = new WComboBox(addGroup);
		
		cmdRun = new WPushButton(WResource.tr("form.batchtest.run"), addGroup);
		cmdRun.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				Test t = ((DataComboMessage<Test>)cmbTest.currentText()).getValue();
				BatchTestRunningForm.runTest(t);
				try { Thread.sleep(100); } catch ( InterruptedException e ) { e.printStackTrace(); }
				redirectToView(RegaDBMain.getApp().getTree().getTreeContent().batchTestRunning, RegaDBMain.getApp().getTree().getTreeContent().batchTestRunning);
				}
		});
		
		fillData();
		
		addControlButtons();
	}
	
	private void fillData() {
		Transaction tr = RegaDBMain.getApp().createTransaction();
		
		cmbTest.clear();
		
        for(Test t : tr.getTests())
        {
        	if ( t.getAnalysis() != null && !BatchTestRunningForm.containsRunningTest(t) ) {
            	cmbTest.addItem(new DataComboMessage<Test>(t, t.getDescription()));
//            	System.err.println("kernel hacking ... " + t.getTestType().getTestObject().getDescription());
        	}
        }
        
        if ( cmbTest.count() == 0 ) {
        	cmbTest.setHidden(true);
        	cmdRun.setEnabled(false);
        } else {
        	cmbTest.setHidden(false);
        	cmdRun.setEnabled(true);
        	cmbTest.sort();
        }
	}
	
	@Override
	public void cancel() {}
	
	@Override
	public WMessage deleteObject() {
		return null;
	}
	
	@Override
	public void redirectAfterDelete() {}
	
	@Override
	public void saveData() {}
}
