package net.sf.regadb.ui.form.query.querytool;

import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WStackedWidget;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.extra.WMenu;
import net.sf.witty.wt.widgets.extra.WMenuOrientation;

public class WTabbedPane extends WContainerWidget {
	private WMenu tabs;
	
	public WTabbedPane() {
		WTable table = new WTable(this);
		table.setStyleClass("tabbedpane");
		
		WStackedWidget menuContents = new WStackedWidget();
		table.elementAt(1, 0).setStyleClass("tabstack");
		table.putElementAt(1, 0, menuContents);
		
		tabs = new WMenu(menuContents, WMenuOrientation.Horizontal);
		tabs.setStyleClass("tabmenu");
		table.putElementAt(0, 0, tabs);
	}
	
	public void addTab(WMessage name, WContainerWidget contents) {
		contents.setStyleClass(contents.styleClass() + " tab");
		tabs.addItem(name, contents);
	}
}
