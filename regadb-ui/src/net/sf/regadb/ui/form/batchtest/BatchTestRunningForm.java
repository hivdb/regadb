package net.sf.regadb.ui.form.batchtest;

import java.util.ArrayList;

import net.sf.regadb.db.Test;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WLabel;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WResource;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

public class BatchTestRunningForm extends FormWidget {
	private static ArrayList<BatchTestRunningTest> runningList = new ArrayList<BatchTestRunningTest>();
	private static WTable table;
	private static WPushButton cmdClear, cmdCancel;
	
	public BatchTestRunningForm(WMessage formName, InteractionState interactionState) {
		super(formName, interactionState);
		init();
		refreshRunning();
	}
	
	public void init() {
		WGroupBox runGroup = new WGroupBox(WResource.tr("form.batchtest.running.title"), this);
		
		table = new WTable(runGroup);
		table.setStyleClass("spacyTable");
		
		cmdClear = new WPushButton(WResource.tr("form.batchtest.running.control.clear"));
		cmdClear.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				clearList();
			}
		});
		
		cmdCancel = new WPushButton(WResource.tr("form.batchtest.running.control.cancel"));
		cmdCancel.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				clearList(true);
			}
		});
		
		addControlButtons();
	}
	
	public static void refreshRunning() {
		table.clear();
		
		String[] headers = {
				"form.batchtest.running.head.test",
				"form.batchtest.running.head.status",
				"form.batchtest.running.head.progress"
				};
		
		for(String head : headers) {
			WLabel wl = new WLabel( new WMessage(head) );
			wl.setStyleClass("table-header-bold");
			table.putElementAt(0, table.numColumns(), wl);
		}
		
		int row = 0;
		for(BatchTestRunningTest run : runningList) {
			row++;
			table.putElementAt(row, 0, new WLabel( run.testName() ));
			table.putElementAt(row, 1, new WLabel( run.getStatusMessage() ));
			table.putElementAt(row, 2, new WLabel( run.getPercent() ));
			
			if ( run.getStatus() != BatchTestStatus.CANCELING ) {
				int col = ( run.isRunning() ) ? 5 : 4;  
				WCheckBox ck = new WCheckBox(new WMessage(" ", true), table.elementAt(row, col));
				run.setCheckBox(ck);
				table.elementAt(row, col).setStyleClass("table-cell-center");
			}
			
//			if ( run.getStatus() == BatchTestStatus.FAILED && run.getLogFile() != null ) {
//				new WAnchor(new WFileResource("text/txt", run.getLogFile().getAbsolutePath()),
//						WResource.tr("form.batchtest.running.log"),
//						table.elementAt(row, 3)).setStyleClass("link");
//			}
		}
		
		row++;
		cmdClear.setParent(table.elementAt(row, 4));
		table.elementAt(row, 4).setStyleClass("table-cell-center");
		cmdCancel.setParent(table.elementAt(row, 5));
		table.elementAt(row, 5).setStyleClass("table-cell-center");
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
	
	public static void clearList() {
		clearList(false);
	}
	public static void clearList(boolean cancelRuns) {
		for( int i=0; i<runningList.size(); i++ ) {
			BatchTestRunningTest run = runningList.get(i);
			
			if ( run.isChecked() && cancelRuns && run.isRunning() ) {
				run.cancel();
			} else if ( run.isChecked() && !cancelRuns && !run.isRunning() ) {
				if ( run.getLogFile() != null ) run.getLogFile().delete();
				runningList.remove(i);
				i--;
			}
		}
		
		refreshRunning();
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
