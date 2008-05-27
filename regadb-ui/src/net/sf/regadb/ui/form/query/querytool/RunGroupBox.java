package net.sf.regadb.ui.form.query.querytool;

import java.util.ArrayList;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.QueryEditor;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WAnchor;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.WTimer;

public class RunGroupBox extends WGroupBox {
	
	private QueryEditor editor;
	
	private WContainerWidget content;
	private WContainerWidget runStatus;
	private List<QueryToolRunnable> runningQueries;
	private WTimer timer;
	
	public RunGroupBox(QueryEditor editor, WContainerWidget parent) {
		super(tr("form.query.querytool.group.run"), parent);
		setStyleClass("resultfield");
		this.editor = editor;
		runningQueries = new ArrayList<QueryToolRunnable>();
		
		content = new WContainerWidget(this);
		content.setStyleClass("content");
        
		runStatus = new WContainerWidget(content);
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
		
		if (!runningQueries.isEmpty()) {
			this.setStyleClass("resultfield");
		}
		else {
			this.setStyleClass("resultfieldempty");
		}
		
		boolean done = true;
        for (int i = runningQueries.size()-1 ; i >= 0 ; i--) {
        	QueryToolRunnable qt = runningQueries.get(i);
 			WContainerWidget panel = new WContainerWidget(runStatus);
			panel.setStyleClass("runresult");
			WTable table = new WTable(panel);

			WText lbl = new WText(lt("" + (i+1) + " :"));
			lbl.setStyleClass("label");
			table.putElementAt(0, 0, lbl);

			if (qt.isDone()) {
				final WAnchor link = new WAnchor(qt.getDownloadLink(), qt.getStatusText());
				table.putElementAt(0, 1, link);
			}
			else {
				final WText status = new WText(qt.getStatusText());
				table.putElementAt(0, 1, status);
				if (!qt.isFailed()) {
					done = false;
				}
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
