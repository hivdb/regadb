package net.sf.regadb.ui.form.query.querytool.widgets;

import java.util.ArrayList;
import java.util.List;

import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.WTimer;
import net.sf.witty.wt.core.utils.WLength;
import net.sf.witty.wt.core.utils.WLengthUnit;
import net.sf.witty.wt.i8n.WMessage;

public class WProgressbar extends WStyledContainerWidget {
	private int progress;
	
	private WText progressText;
	private WContainerWidget progressBar;
	private WTimer time;
	private ProgressReporter reporter;
	
	public List<ProgressListener> progressListeners;
	
	public WProgressbar(ProgressReporter reporter) {
		this.reporter = reporter;
		init();
	}
	
	public void addProgressChangeListeners(ProgressListener listener) {
		progressListeners.add(listener);
	}
	
	private void init() {
		getStyleClasses().addStyle("progressbar");
		progressListeners = new ArrayList<ProgressListener>();
		progressBar = new WContainerWidget(this);
		progressText = new WText(this);
		progressText.setStyleClass("progresstext");
		time = new WTimer();
		time.setInterval(100);
		setProgress(0);
		setText(lt(""));

		time.start();
		time.timeout.addListener(new SignalListener<WEmptyEvent>() {
			public void notify(WEmptyEvent a) {
				if (reporter.isDone()) {
					time.stop();
				}
				setProgress(reporter.getProgress());
				setText(reporter.getMessage());
			}
		});
	}
	
	private void setProgress(int progress) {
		progressBar.resize(new WLength(progress, WLengthUnit.Percentage), progressBar.height());
		
		if (progress != this.progress) {
			for (ProgressListener l : progressListeners) {
				l.progressChanged(reporter);
			}
		}
		this.progress = progress;
	}
	
	public int getProgress() {
		return progress;
	}
	
	public boolean isDone() {
		return reporter.isDone();
	}
	
	public void setText(WMessage txt) {
		progressText.setText( lt("" + getProgress() + "% " + txt.value()) );
	}
	
	public interface ProgressListener {
		public void progressChanged(ProgressReporter reporter);
	}
}
