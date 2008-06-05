package net.sf.regadb.ui.form.query.querytool.widgets;

import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.WTimer;
import net.sf.witty.wt.i8n.WMessage;

public class WDialog extends WStyledContainerWidget implements StatusbarHolder {
	private WContainerWidget titlePanel;
	private WText titleText;
	
	private WContainerWidget contentArea;
	private WContainerWidget contentPanel;
	
	private WContainerWidget buttonArea;
	private WButtonPanel buttonPanel;
	
	private boolean modal;
	private WContainerWidget insideDialogArea;
	private StyledWidget outSideDialogArea;
	
	private WContainerWidget statusBarArea;
	private WStatusBar statusBar;
	
	/** 
	 * a dialog with content and button panel
	 * @param title the title of the dialog
	 */
	public WDialog(WMessage title) {
		super();
		getStyleClasses().addStyle("dialog");
		insideDialogArea = new WContainerWidget(this);
		titlePanel = new WContainerWidget(insideDialogArea);
		titlePanel.setStyleClass("dialogtitle");
		titleText = new WText(title, titlePanel);
		
		WContainerWidget content = new WContainerWidget(insideDialogArea);
		contentArea = new WContainerWidget(content);
		contentPanel = new WContainerWidget(contentArea);
		content.setStyleClass("dialogcontent");

		buttonArea = new WContainerWidget(content);
		
		statusBarArea = new WContainerWidget(content);
		
		outSideDialogArea = new WStyledContainerWidget(this);
	}
	
	public void setTitle(WMessage title) {
		titleText.setText(title);
	}
	
	public WMessage getTitle() {
		return titleText.text();
	}
	
	public WContainerWidget getContentPanel() {
		return contentPanel;
	}
	
	public void setContentPanel(WContainerWidget content) {
		if (contentPanel != null) {
			contentArea.removeWidget(contentPanel);
		}
		contentArea.addWidget(content);
		this.contentPanel = content;
	}
	
	public void setButtonPanel(WButtonPanel buttonPanel) {
		if (this.buttonPanel != null) {
			buttonArea.removeWidget(this.buttonPanel);
		}
		buttonArea.addWidget(buttonPanel);
		this.buttonPanel = buttonPanel;
	}
	
	public WButtonPanel getButtonPanel() {
		return buttonPanel;
	}
	
	/**
	 * make this dialog modal
	 * @param modal true to make the dialog modal
	 */
	public void setModal(boolean modal) {
		if (modal) {
			insideDialogArea.setStyleClass("modalDialog");
			outSideDialogArea.getStyleClasses().addStyle("unfocusArea");
			WTimer.singleShot(1, new SignalListener<WEmptyEvent>() {
				public void notify(WEmptyEvent a) {
					outSideDialogArea.getStyleClasses().addStyle("fixed");
				}
			});
		}
		else {
			insideDialogArea.setStyleClass("");
			outSideDialogArea.getStyleClasses().clearStyle();
		}
		this.modal = modal;
	}
	
	public boolean isModal() {
		return modal;
	}

	public void setStatusBar(WStatusBar statusBar) {
		if (this.statusBar != null) {
			statusBarArea.removeWidget(this.statusBar);
		}
		statusBarArea.addWidget(statusBar);
		this.statusBar = statusBar;
	}}
