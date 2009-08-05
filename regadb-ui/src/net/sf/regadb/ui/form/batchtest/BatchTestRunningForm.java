package net.sf.regadb.ui.form.batchtest;

import java.util.ArrayList;

import net.sf.regadb.db.Test;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WTimer;

public class BatchTestRunningForm extends FormWidget {
	private static ArrayList<BatchTestRunningTest> runningList = new ArrayList<BatchTestRunningTest>();
	private SimpleTable table;
	private WTimer timer = new WTimer();
	
	public BatchTestRunningForm(WString formName, InteractionState interactionState) {
		super(formName, interactionState);
		timer.setInterval(1000);
		timer.timeout().addListener(this, new Signal.Listener() {
			public void trigger() {
				refreshRunning();
			}
		});
		init();
		refreshRunning();
	}
	
	public void init() {
		WGroupBox runGroup = new WGroupBox(tr("form.batchtest.running.title"), this);
		table = new SimpleTable(runGroup);
		addControlButtons();
	}
	
	public void refreshRunning() {
		table.clear();
		table.setHeaders(tr("form.batchtest.running.head.test"),
				tr("form.batchtest.running.head.status"),
				tr("form.batchtest.running.head.progress"));
		
		table.setWidths(60,20,20);
		table.getElementAt(0, 3).setStyleClass("column-action");
		
		
		int row = 1;
		int needRefreshCount = 0;
		
		for(final BatchTestRunningTest run : runningList) {
			if ( run.isRunning()) {
				needRefreshCount++;
			}
			
			table.getElementAt(row, 0).addWidget(new WText( run.testName() ));
			table.getElementAt(row, 1).addWidget(new WText( run.getStatusMessage()));
			table.getElementAt(row, 2).addWidget(new WText( run.getPercent() ));
			table.getElementAt(row, 3).setStyleClass("column-action");
			
			if (run.getStatus() == BatchTestStatus.RUNNING) {
				final WPushButton cancelButton = new WPushButton(tr("form.batchtest.running.control.cancel"), table.getElementAt(row, 3));
				cancelButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
					public void trigger(WMouseEvent a) {
						if (run.isRunning()) {
							run.cancel();
							cancelButton.disable();
							cancelButton.setText(tr("form.batchtest.running.control.canceling"));
						}
					}
				});
			}
			
			if (run.getStatus() == BatchTestStatus.CANCELING) {
				final WPushButton cancelButton = new WPushButton(tr("form.batchtest.running.control.canceling"), table.getElementAt(row, 3));
				cancelButton.disable();
			}
			
			if (run.getStatus() == BatchTestStatus.DONE ||
					run.getStatus() == BatchTestStatus.FAILED ||
					run.getStatus() == BatchTestStatus.CANCELED) {
				WPushButton clearButton = new WPushButton(tr("form.batchtest.running.control.clear"), table.getElementAt(row, 3));
				clearButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
					public void trigger(WMouseEvent a) {
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
	public WString deleteObject() {
		return null;
	}
	
	@Override
	public void redirectAfterDelete() {}
	
	@Override
	public void saveData() {}
}
