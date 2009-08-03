package net.sf.regadb.ui.form.query.querytool.widgets;

import java.util.ArrayList;
import java.util.List;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WTimer;

//TODO REMOVE

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
		setText("");

		time.start();
		time.timeout().addListener(this, new Signal.Listener() {
			public void trigger() {
				if (reporter.isDone()) {
					time.stop();
				}
				setProgress(reporter.getProgress());
				setText(reporter.getMessage());
			}
		});
	}
	
	private void setProgress(int progress) {
		progressBar.resize(new WLength(progress, WLength.Unit.Percentage), progressBar.getHeight());
		
		for (ProgressListener l : progressListeners) {
			l.progressChanged(reporter);
		}
		this.progress = progress;
	}
	
	public int getProgress() {
		return progress;
	}
	
	public boolean isDone() {
		return reporter.isDone();
	}
	
	public void setText(CharSequence txt) {
		progressText.setText( "" + getProgress() + "% " + txt.toString() );
	}
	
	public interface ProgressListener {
		public void progressChanged(ProgressReporter reporter);
	}
}
