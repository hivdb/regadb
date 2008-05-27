package net.sf.regadb.ui.form.query.querytool;

import java.util.ArrayList;
import java.util.List;

import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WStackedWidget;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.extra.WMenu;
import net.sf.witty.wt.widgets.extra.WMenuItem;
import net.sf.witty.wt.widgets.extra.WMenuLoadPolicy;
import net.sf.witty.wt.widgets.extra.WMenuOrientation;

public class WTabbedPane extends WContainerWidget {
	private WMenu tabs;
	private int selectedIndex;
	
	private List<WContainerWidget> tabItems;
	private List<WMessage> titles;
	
	
	public WTabbedPane() {
		super();
		tabItems = new ArrayList<WContainerWidget>();
		titles = new ArrayList<WMessage>();
		WTable table = new WTable(this);
		table.setStyleClass("tabbedpane");
		
		WStackedWidget menuContents = new WStackedWidget();
		table.elementAt(1, 0).setStyleClass("tabstack");
		table.putElementAt(1, 0, menuContents);
		
		tabs = new WMenu(menuContents, WMenuOrientation.Horizontal);
		tabs.setStyleClass("tabmenu");
		table.putElementAt(0, 0, tabs);
	}
	
	public void addTab(WMessage name, final WContainerWidget contents) {
		WWidget widget = (WWidget) contents;
		widget.setStyleClass(widget.styleClass() + " tab");
		
		final WMenuItem wmi = new WMenuItem(name, widget, WMenuLoadPolicy.LazyLoading);
		wmi.itemWidget().clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				contents.show();
				selectedIndex = tabItems.indexOf(contents);
			}
		});
		
		tabs.addItem(wmi);
		titles.add(name);
		tabItems.add(contents);
	}
	
	public void showTab(int index) {
		if (index >= 0 && index < tabItems.size()) {
			tabs.select(index);
			tabItems.get(index).show();
		}
	}
	
	public void showTab(WMessage msg) {
		showTab(titles.indexOf(msg));
	}
	
	public void showTab(WContainerWidget tab) {
		showTab(tabItems.indexOf(tab));
	}
	
	public WContainerWidget getSelectedTab() {
		return tabItems.get(selectedIndex);
	}
}
