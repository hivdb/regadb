package net.sf.regadb.ui.form.query.querytool;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.ui.form.query.querytool.widgets.WGroupContainer;
import net.sf.regadb.ui.framework.RegaDBMain;

import com.pharmadm.custom.rega.queryeditor.QueryEditor;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WFileResource;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WTimer;

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
		timer.timeout().addListener(this, new Signal.Listener() {
			public void trigger() {
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
				table.elementAt(runningQueries.size()-i-1, 0).addWidget(lbl);
	
				if (qt.isDone()) {
					WFileResource res = qt.getDownloadResource();
					res.suggestFileName("result_"+ (i+1) +".csv");
					final WAnchor link = new WAnchor(res, qt.getStatusText());
					table.elementAt(runningQueries.size()-i-1, 1).addWidget(link);
				}
				else {
					final WText status = new WText(qt.getStatusText());
					table.elementAt(runningQueries.size()-i-1, 1).addWidget(status);
					if (!qt.isFailed()) {
						done = false;
					}
					
					if (qt.isRunning()) {
						WPushButton cancelButton = new WPushButton(tr("form.query.querytool.pushbutton.cancel"));
						cancelButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
							public void trigger(WMouseEvent a) {
								qt.cancel();
							}
						});
						table.elementAt(runningQueries.size()-i-1, 2).addWidget(cancelButton);
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
		QueryToolThread qt = new QueryToolThread(RegaDBMain.getApp().getLogin(), editor);
		qt.startQueryThread();
		runningQueries.add(qt.getRun());
		timer.start();
		updateRunningQueries();
	}
}
