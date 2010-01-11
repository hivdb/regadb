package net.sf.regadb.ui.form.query.querytool.widgets;


import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;


/*
TODO remove
*/
public class WStatusBar extends WStyledContainerWidget {
	private WTable table;
	private WButtonPanel buttonPanel;
	private WText warningText;
	
	public WStatusBar() {
		super();
		init();
	}
	
	private void init() {
		getStyleClasses().addStyle("statusbar");
		table = new WTable(this);
		table.getElementAt(0, 0).setStyleClass("statustext");
		warningText = new WText("", getTextContainer());
		warningText.setStyleClass("warning");
	}
	
	public WContainerWidget getTextContainer() {
		return table.getElementAt(0, 0);
	}
	
	public WButtonPanel getButtonPanel() {
		return buttonPanel;
	}
	
	/**
	 * show the given warning message in the status bar
	 * and assign it the given style class
	 * @param message
	 */
	public void showMessage(WString message, String cssClass) {
		warningText.setText(message);
		warningText.setStyleClass(cssClass);
	}	
	
	/** 
	 * install a new button panel in the status bar
	 * @param buttonPanel
	 */
	public void setButtonPanel(WButtonPanel buttonPanel) {
		if (this.buttonPanel != null) {
			table.getElementAt(0, 1).removeWidget(this.buttonPanel);
		}
		table.getElementAt(0, 1).addWidget(buttonPanel);
	}
	
	public WString getStatusText() {
		return warningText.getText();
	}
	
	/**
	 * called when the progress bar is done
	 */
	protected void loadingComplete() {
		update();
	}
	
	
	/**
	 * override this method to provide update
	 * capabilities to the status bar
	 */
	public void update() {
		
	}
}
