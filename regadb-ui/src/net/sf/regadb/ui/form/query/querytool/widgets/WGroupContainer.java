package net.sf.regadb.ui.form.query.querytool.widgets;

import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.i8n.WMessage;

public class WGroupContainer extends WGroupBox implements StyledWidget, StatusbarHolder, ToolbarHolder {

	private CssClasses style;
	private WStyledContainerWidget content;
	
	private WContainerWidget toolbarArea;
	private WButtonPanel toolbar;
	
	private WContainerWidget statusbarArea;
	private WStatusBar statusbar;
	
	public WGroupContainer(WMessage title, WContainerWidget parent) {
		super(title, parent);
		init();
	}

	public WGroupContainer(WMessage titleMessage) {
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

	@Override
	public CssClasses getStyleClasses() {
		return style;
	}

	@Override
	public void setStatusBar(WStatusBar statusBar) {
		if (this.statusbar != null) {
			statusbarArea.removeWidget(this.statusbar);
		}
		statusbarArea.addWidget(statusBar);
		this.statusbar = statusBar;
	}

	@Override
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
