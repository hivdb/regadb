package net.sf.regadb.ui.form.query.querytool.widgets;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.ui.framework.widgets.UIUtils;
import eu.webtoolkit.jwt.Orientation;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WInteractWidget;
import eu.webtoolkit.jwt.WMenu;
import eu.webtoolkit.jwt.WMenuItem;
import eu.webtoolkit.jwt.WStackedWidget;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WWidget;
import eu.webtoolkit.jwt.WMenuItem.LoadPolicy;

public class WTabbedPane extends WStyledContainerWidget implements StatusbarHolder {
	private WMenu tabs;
	
	private int selectedIndex;
	private List<WContainerWidget> tabItems;
	private ArrayList<WString> titles;
	private WStatusBar statusbar;
	
	private WTable table;
	
	
	public WTabbedPane() {
		this(null);
	}
	
	public WTabbedPane(WContainerWidget parent) {
		super(parent);
		tabItems = new ArrayList<WContainerWidget>();
		titles = new ArrayList<WString>();
		table = new WTable(this);
		table.setStyleClass("tabbedpane");
		
		WStackedWidget menuContents = new WStackedWidget();
		table.elementAt(1, 0).setStyleClass("tabstack");
		table.elementAt(1, 0).addWidget(menuContents);
		
		tabs = new WMenu(menuContents, Orientation.Horizontal);
		tabs.setStyleClass("tabmenu");
		table.elementAt(0, 0).addWidget(tabs);
	}
	
	public void addTab(WString title, final WContainerWidget contents) {
		WWidget widget = (WWidget) contents;
		widget.setStyleClass(widget.styleClass() + " tab");
		
		final WMenuItem wmi = new WTabMenuItem(title, widget, LoadPolicy.LazyLoading);
		tabs.addItem(wmi);
		titles.add(title);
		tabItems.add(contents);
		
		((WInteractWidget)wmi.itemWidget()).clicked().addListener(this, new Signal.Listener() {
			public void trigger() {
				contents.show();
				selectedIndex = tabItems.indexOf(contents);
			}
		});
	}
	
	public void showTab(int index) {
		if (index >= 0 && index < tabItems.size()) {
			tabs.select(index);
			tabItems.get(index).show();
			selectedIndex = index;
		}
	}
	
	public void showTab(WString title) {
		for (int i = 0 ; i < titles.size() ; i++) {
			if (UIUtils.keyOrValue(title).equals(UIUtils.keyOrValue(titles.get(i)))) {
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
