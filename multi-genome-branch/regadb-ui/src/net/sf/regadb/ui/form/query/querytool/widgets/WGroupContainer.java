package net.sf.regadb.ui.form.query.querytool.widgets;

import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;

public class WGroupContainer extends WGroupBox implements StyledWidget, StatusbarHolder, ToolbarHolder {

	private CssClasses style;
	private WStyledContainerWidget content;
	
	private WContainerWidget toolbarArea;
	private WButtonPanel toolbar;
	
	private WContainerWidget statusbarArea;
	private WStatusBar statusbar;
	
	public WGroupContainer(WString title, WContainerWidget parent) {
		super(title, parent);
		init();
	}

	public WGroupContainer(WString titleMessage) {
		super(titleMessage);
		init();
	}

	private void init() {
		style = new CssClasses(this);
		style.addStyle("prettyfieldset");
		
		content = new WStyledContainerWidget(this);
		content.getStyleClasses().addStyle("content");
		
		statusbarArea = new WContainerWidget();
	}
	
	public WStyledContainerWidget getContentPanel() {
		return content;
	}

	public CssClasses getStyleClasses() {
		return style;
	}

	public void setStatusBar(WStatusBar statusBar) {
		if (this.statusbar != null) {
			statusbarArea.removeWidget(this.statusbar);
		}
		statusbarArea.addWidget(statusBar);
		this.statusbar = statusBar;
	}

	public void setToolbar(WButtonPanel panel) {
		if (toolbar != null) {
			toolbar.getStyleClasses().removeStyle("toolbar");
			toolbarArea.removeWidget(toolbar);
		}
		
		toolbar = panel;
		if (toolbar != null) {
			toolbarArea.addWidget(toolbar);
			toolbar.getStyleClasses().addStyle("toolbar");
		}
	}
}
