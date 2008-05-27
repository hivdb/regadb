package net.sf.regadb.ui.form.query.querytool.dialog;

import net.sf.regadb.ui.form.query.querytool.buttons.ButtonPanel;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.i8n.WMessage;

public class WDialog extends WContainerWidget {
	private WContainerWidget titlePanel;
	private WContainerWidget contentArea;
	private WContainerWidget buttonArea;
	
	
	public WDialog(WMessage title) {
		super();
		this.setStyleClass("dialog");
		
		titlePanel = new WContainerWidget(this);
		titlePanel.setStyleClass("dialogtitle");
		new WText(title, titlePanel);
		
		WContainerWidget contentPanel = new WContainerWidget(this);
		contentPanel.setStyleClass("dialogcontent");
		
		contentArea = new WContainerWidget(contentPanel);
		buttonArea = new WContainerWidget(contentPanel);
	}
	
	public WContainerWidget getContentArea() {
		return contentArea;
	}
	
	public void setButtonPanel(ButtonPanel buttonPanel) {
		while(buttonArea.children().size() > 0) {
			buttonArea.removeWidget(buttonArea.children().get(0));
		}
		buttonArea.addWidget(buttonPanel);
	}
}
