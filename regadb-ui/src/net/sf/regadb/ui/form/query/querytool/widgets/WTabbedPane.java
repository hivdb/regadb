package net.sf.regadb.ui.form.query.querytool.widgets;

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

public class WTabbedPane extends WStyledContainerWidget implements StatusbarHolder {
	private WMenu tabs;
	
	private int selectedIndex;
	private List<WContainerWidget> tabItems;
	private ArrayList<WMessage> titles;
	private WStatusBar statusbar;
	
	private WTable table;
	
	
	public WTabbedPane() {
		this(null);
	}
	
	public WTabbedPane(WContainerWidget parent) {
		super(parent);
		tabItems = new ArrayList<WContainerWidget>();
		titles = new ArrayList<WMessage>();
		table = new WTable(this);
		table.setStyleClass("tabbedpane");
		
		WStackedWidget menuContents = new WStackedWidget();
		table.elementAt(1, 0).setStyleClass("tabstack");
		table.putElementAt(1, 0, menuContents);
		
		tabs = new WMenu(menuContents, WMenuOrientation.Horizontal);
		tabs.setStyleClass("tabmenu");
		table.putElementAt(0, 0, tabs);
	}
	
	public void addTab(WMessage title, final WContainerWidget contents) {
		WWidget widget = (WWidget) contents;
		widget.setStyleClass(widget.styleClass() + " tab");
		
		final WMenuItem wmi = new WTabMenuItem(title, widget, WMenuLoadPolicy.LazyLoading);
		wmi.itemWidget().clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				contents.show();
				selectedIndex = tabItems.indexOf(contents);
			}
		});
		
		tabs.addItem(wmi);
		titles.add(title);
		tabItems.add(contents);
	}
	
	public void showTab(int index) {
		if (index >= 0 && index < tabItems.size()) {
			tabs.select(index);
			tabItems.get(index).show();
			selectedIndex = index;
		}
	}
	
	public void showTab(WMessage title) {
		for (int i = 0 ; i < titles.size() ; i++) {
			if (title.keyOrValue().equals(titles.get(i).keyOrValue())) {
				showTab(i);
			}
		}
	}
	
	public void showTab(WContainerWidget tab) {
		showTab(tabItems.indexOf(tab));
	}
	
	public WContainerWidget getSelectedTab() {
		return tabItems.get(selectedIndex);
	}
	
	public int getSelectedTabIndex() {
		return selectedIndex;
	}
	
	public WContainerWidget getTab(int index) {
		return tabItems.get(index);
	}

	public void setStatusBar(WStatusBar statusBar) {
		if (statusbar != null) {
			table.elementAt(1, 0).removeWidget(statusbar);
		}
		table.elementAt(1, 0).addWidget(statusBar);
		statusbar = statusBar;
	}
}
