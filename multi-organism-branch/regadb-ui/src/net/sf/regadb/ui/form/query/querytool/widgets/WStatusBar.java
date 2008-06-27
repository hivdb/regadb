package net.sf.regadb.ui.form.query.querytool.widgets;


import net.sf.regadb.ui.form.query.querytool.widgets.WProgressbar.ProgressListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.i8n.WMessage;

public class WStatusBar extends WStyledContainerWidget {
	private WTable table;
	private WButtonPanel buttonPanel;
	private WText warningText;
	private WProgressbar progressbar;
	
	public WStatusBar() {
		super();
		init();
	}
	
	private void init() {
		getStyleClasses().addStyle("statusbar");
		table = new WTable(this);
		warningText = new WText(tr("form.query.querytool.message.ok"), getTextContainer());
		warningText.setStyleClass("warning");
	}
	
	public WContainerWidget getTextContainer() {
		return table.elementAt(0, 0);
	}
	
	public WButtonPanel getButtonPanel() {
		return buttonPanel;
	}
	
	/**
	 * show the given warning message in the status bar
	 * and assign it the given style class
	 * @param message
	 */
	public void showMessage(String message, String cssClass) {
		warningText.setText(tr(message));
		warningText.setStyleClass(cssClass);
	}	
	
	/** 
	 * install a new button panel in the status bar
	 * @param buttonPanel
	 */
	public void setButtonPanel(WButtonPanel buttonPanel) {
		if (this.buttonPanel != null) {
			table.elementAt(0, 1).removeWidget(this.buttonPanel);
		}
		table.elementAt(0, 1).addWidget(buttonPanel);
	}
	
	public WMessage getStatusText() {
		return warningText.text();
	}
	
	/**
	 * show a progress bar for the given progress reporter
	 * a progress bar can only be added if there is no other
	 * progress bar running
	 */
	public void showProgressBar(ProgressReporter rep) {
		if (progressbar != null && progressbar.isDone()) {
			getTextContainer().removeWidget(progressbar);
			progressbar = null;
		}
		if (progressbar == null) {
			progressbar = new WProgressbar(rep);
			getTextContainer().addWidget(progressbar);
			initProgressBar(rep);
		}
	}
	

	private void initProgressBar(ProgressReporter rep) {
		if (!rep.isDone()) {
			showLoadingBar();
			progressbar.addProgressChangeListeners(new ProgressListener() {
				public void progressChanged(ProgressReporter reporter) {
					if (reporter.isDone()) {
						loadingComplete();
					}
				}
			});
		}
		else {
			hideLoadingBar();
		}		
	}	
	
	/**
	 * called when the progress bar is done
	 */
	protected void loadingComplete() {
		hideLoadingBar();
		update();
	}
	
	
	/**
	 * override this method to provide update
	 * capabilities to the status bar
	 */
	public void update() {
		
	}
	
	/**
	 * called when progress bar is initially shown
	 */
	private void showLoadingBar() {
		warningText.hide();
		progressbar.show();
	}
	
	/**
	 * called when progress bar gets hidden
	 */
	private void hideLoadingBar() {
		progressbar.hide();
		warningText.show();
	}
}
