package net.sf.regadb.ui.form.query.querytool;

import java.util.ArrayList;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.QueryEditor;

import net.sf.regadb.ui.form.query.querytool.widgets.WGroupContainer;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WAnchor;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.WTimer;

public class RunGroupBox extends WGroupContainer {
	
	private QueryEditor editor;
	
	private WContainerWidget runStatus;
	private List<QueryToolRunnable> runningQueries;
	private WTimer timer;
	private WText warning;
	
	public RunGroupBox(QueryEditor editor, WContainerWidget parent) {
		super(tr("form.query.querytool.group.run"), parent);
		getStyleClasses().addStyle("resultfield");
		this.editor = editor;
		runningQueries = new ArrayList<QueryToolRunnable>();
		
        
		runStatus = new WContainerWidget(getContentPanel());
		runStatus.setStyleClass("runresults");
		
		timer = new WTimer(this);
		timer.setInterval(1000);
		timer.timeout.addListener(new SignalListener<WEmptyEvent>() {
			public void notify(WEmptyEvent a) {
				updateRunningQueries();
			}
		});
		
		updateRunningQueries();
	}
	
	private void updateRunningQueries() {
		while(runStatus.children().size() > 0) {
			runStatus.removeWidget(runStatus.children().get(0));
		}
		
		if (runningQueries.isEmpty() && warning == null) {
			warning = new WText(tr("form.query.querytool.message.noresults"));
			warning.setStyleClass("warning");
			getContentPanel().addWidget(warning);
		}
		else if (!runningQueries.isEmpty() && warning != null) {
			getContentPanel().removeWidget(warning);
		}
		
		boolean done = true;
		if (runningQueries.size() > 0) {
			WContainerWidget panel = new WContainerWidget(runStatus);
			panel.setStyleClass("runresult");
			WTable table = new WTable(panel);
	        for (int i = runningQueries.size()-1 ; i >= 0 ; i--) {
	        	final QueryToolRunnable qt = runningQueries.get(i);
	
				WText lbl = new WText(lt("" + (i+1) + " :"));
				lbl.setStyleClass("label");
				table.putElementAt(runningQueries.size()-i-1, 0, lbl);
	
				if (qt.isDone()) {
					final WAnchor link = new WAnchor(qt.getDownloadLink(), qt.getStatusText());
					table.putElementAt(runningQueries.size()-i-1, 1, link);
				}
				else {
					final WText status = new WText(qt.getStatusText());
					table.putElementAt(runningQueries.size()-i-1, 1, status);
					if (!qt.isFailed()) {
						done = false;
					}
					
					if (qt.isRunning()) {
						WPushButton cancelButton = new WPushButton(tr("form.query.querytool.pushbutton.cancel"));
						cancelButton.clicked.addListener(new SignalListener<WMouseEvent>() {
							public void notify(WMouseEvent a) {
								qt.cancel();
							}
						});
						table.putElementAt(runningQueries.size()-i-1, 2, cancelButton);
					}
				}
				
				table.elementAt(runningQueries.size()-i-1, 0).setStyleClass("resultNumber");
				table.elementAt(runningQueries.size()-i-1, 1).setStyleClass("resultStatus");
				table.elementAt(runningQueries.size()-i-1, 2).setStyleClass("resultCancel");
	        }		
		}
        if (done) {
        	timer.stop();
        }
	}
	
	public void runQuery() {
		QueryToolThread qt = new QueryToolThread(RegaDBMain.getApp().getLogin().copyLogin(), editor);
		qt.startQueryThread();
		runningQueries.add(qt.getRun());
		timer.start();
		updateRunningQueries();
	}
}
