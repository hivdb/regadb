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
import eu.webtoolkit.jwt.WTableCell;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WTimer;

public class RunGroupBox extends WGroupContainer {
	private QueryToolForm queryToolForm;
	
	private QueryEditor editor;
	
	private WContainerWidget runStatus;
	private List<QueryToolRunnable> runningQueries;
	private WTimer timer;
	private WText warning;
	
	public RunGroupBox(QueryToolForm queryToolForm, QueryEditor editor, WContainerWidget parent) {
		super(tr("form.query.querytool.group.run"), parent);
		getStyleClasses().addStyle("resultfield");
		this.queryToolForm = queryToolForm;
		
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
		while(runStatus.getChildren().size() > 0) {
			runStatus.removeWidget(runStatus.getChildren().get(0));
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
	
				WText lbl = new WText("" + (i+1) + " :");
				lbl.setStyleClass("label");
				table.getElementAt(runningQueries.size()-i-1, 0).addWidget(lbl);
	
				if (qt.isDone()) {
					WTableCell tc = table.getElementAt(runningQueries.size()-i-1, 1);
					WFileResource res = qt.getTableDownloadResource();
					res.suggestFileName("result_"+ (i+1) +".csv");
					WAnchor link = new WAnchor(res, qt.getStatusText());
					tc.addWidget(link);
					if (qt.getFastaDownloadResource() != null) {
						tc.addWidget(new WText("   "));
						res = qt.getFastaDownloadResource();
						res.suggestFileName("result_"+ (i+1) +".fasta");
						link = new WAnchor(res, tr("form.query.querytool.label.fasta").arg(qt.getFastaEntries()));
						tc.addWidget(link);
					}
					if (qt.getSummaryFile() != null) {
						tc.addWidget(new WText("   "));
						
						WText report = new WText(tr("form.query.querytool.label.report"));
						report.setStyleClass("text-link");
						report.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
							public void trigger(WMouseEvent arg) {
								queryToolForm.addReportTab(new ReportContainer(qt.getSummaryFile()));
							}
						});
						tc.addWidget(report);
					}
				}
				else {
					final WText status = new WText(qt.getStatusText());
					table.getElementAt(runningQueries.size()-i-1, 1).addWidget(status);
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
						table.getElementAt(runningQueries.size()-i-1, 2).addWidget(cancelButton);
					}
				}
				
				table.getElementAt(runningQueries.size()-i-1, 0).setStyleClass("resultNumber");
				table.getElementAt(runningQueries.size()-i-1, 1).setStyleClass("resultStatus");
				table.getElementAt(runningQueries.size()-i-1, 2).setStyleClass("resultCancel");
	        }		
		}
        if (done) {
        	timer.stop();
        }
	}
	
	public void runQuery() {
		QueryToolThread qt = new QueryToolThread(RegaDBMain.getApp().getLogin(), editor, queryToolForm.getObject());
		qt.startQueryThread();
		runningQueries.add(qt.getRun());
		timer.start();
		updateRunningQueries();
	}
	
	public boolean isQueryRunning(){
		for(QueryToolRunnable qt : runningQueries)
			if(qt.isRunning())
				return true;
		return false;
	}
}
