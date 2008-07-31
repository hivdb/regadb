package net.sf.regadb.ui.form.batchtest;

import java.util.ArrayList;

import net.sf.regadb.db.Test;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.WTimer;
import net.sf.witty.wt.i8n.WMessage;

public class BatchTestRunningForm extends FormWidget {
	private static ArrayList<BatchTestRunningTest> runningList = new ArrayList<BatchTestRunningTest>();
	private SimpleTable table;
	private WTimer timer = new WTimer();
	
	public BatchTestRunningForm(WMessage formName, InteractionState interactionState, boolean literal) {
		super(formName, interactionState, literal);
		timer.setInterval(1000);
		timer.timeout.addListener(new SignalListener<WEmptyEvent>() {
			public void notify(WEmptyEvent a) {
				refreshRunning();
			}
		});
		init();
		refreshRunning();
	}
	
	public void init() {
		WGroupBox runGroup = new WGroupBox(tr("batch.select"), this);
		table = new SimpleTable(runGroup);
		addControlButtons();
	}
	
	public void refreshRunning() {
		table.clear();
		table.setHeaders(tr("test.form"),
				tr("general.status"),
				tr("general.status.progress"));
		
		table.setWidths(60,20,20);
		table.elementAt(0, 3).setStyleClass("column-action");
		
		
		int row = 1;
		int needRefreshCount = 0;
		
		for(final BatchTestRunningTest run : runningList) {
			if ( run.isRunning()) {
				needRefreshCount++;
			}
			
			table.putElementAt(row, 0, new WText( run.testName() ));
			table.putElementAt(row, 1, new WText( run.getStatusMessage() ));
			table.putElementAt(row, 2, new WText( run.getPercent() ));
			table.elementAt(row, 3).setStyleClass("column-action");
			
			if (run.getStatus() == BatchTestStatus.RUNNING) {
				final WPushButton cancelButton = new WPushButton(tr("general.cancel"), table.elementAt(row, 3));
				cancelButton.clicked.addListener(new SignalListener<WMouseEvent>() {
					public void notify(WMouseEvent a) {
						if (run.isRunning()) {
							run.cancel();
							cancelButton.disable();
							cancelButton.setText(tr("general.status.canceling"));
						}
					}
				});
			}
			
			if (run.getStatus() == BatchTestStatus.CANCELING) {
				final WPushButton cancelButton = new WPushButton(tr("general.status.canceling"), table.elementAt(row, 3));
				cancelButton.disable();
			}
			
			if (run.getStatus() == BatchTestStatus.DONE ||
					run.getStatus() == BatchTestStatus.FAILED ||
					run.getStatus() == BatchTestStatus.CANCELED) {
				WPushButton clearButton = new WPushButton(tr("general.clear"), table.elementAt(row, 3));
				clearButton.clicked.addListener(new SignalListener<WMouseEvent>() {
					public void notify(WMouseEvent a) {
						int row = runningList.indexOf(run);
						table.deleteRow(row+1);
						runningList.remove(run);
					}
				});
			}
			
			row++;
		}
		
		if ( needRefreshCount == 0  && timer.isActive()) {
			 timer.stop();
		}
		else if (needRefreshCount > 0 && !timer.isActive()){
			timer.start();
		}
	}
	
	public static void runTest(Test t) {
		BatchTestRunningTest testing = new BatchTestRunningTest(t);
		runningList.add(testing);
		testing.start();
	}
	
	public static boolean containsRunningTest(Test t) {
		for(BatchTestRunningTest run : runningList) {
			if ( run.isTest(t) ) {
				if ( run.isRunning() )
					return true;
				else
					return false;
			}
		}
		return false;
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
