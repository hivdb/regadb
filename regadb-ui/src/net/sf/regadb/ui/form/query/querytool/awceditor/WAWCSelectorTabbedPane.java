package net.sf.regadb.ui.form.query.querytool.awceditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.regadb.ui.form.query.querytool.widgets.WTabbedPane;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;

import eu.webtoolkit.jwt.WRadioButton;
import eu.webtoolkit.jwt.WString;

public class WAWCSelectorTabbedPane extends WAWCSelectorPanel {

	private QueryContext context;
	private HashMap<String, WAWCSelectorPanel> panels;
	private WTabbedPane tabs;
	
	public WAWCSelectorTabbedPane(QueryContext context) {
		super();
		setStyleClass("selectorpanel selectortabbedpane");
		tabs = new WTabbedPane();
		this.addWidget(tabs);
		this.context = context;
        panels = new HashMap<String, WAWCSelectorPanel>(); 
	}
	
	public void showTab(String tabName) {
		tabs.showTab(tabName);
	}
	
	@Override
	public boolean addAtomicWhereClause(AtomicWhereClause clause) {
        for (String group : clause.getGroups()) {
    		if (!panels.containsKey(group)) {
    			addSelectorPanel(new WAWCSelectorTab(group, context));
    		}
    		
    		panels.get(group).addAtomicWhereClause(clause);
        }
        return true;
	}
	
	/**
	 * adds all the given clauses to the given tab
	 */
	public boolean addAll(List<AtomicWhereClause> clauses, String tabName) {
		WAWCSelectorPanel panel = panels.get(tabName);
		if (panel != null) {
			boolean ok = true;
			for (AtomicWhereClause clause : clauses) {
				ok = ok && panel.addAtomicWhereClause(clause);
			}
			return ok;
		}
		return false;
	}

	@Override
	public boolean addSelectorPanel(WAWCSelectorPanel panel) {
		if (panel instanceof WAWCSelectorTab) {
			WAWCSelectorTab collapsablePanel = (WAWCSelectorTab) panel;
			addSelectorPanel(collapsablePanel);
			return true;
		}
		return false;
	}
	
	private void addSelectorPanel(WAWCSelectorTab panel) {
		panels.put(panel.getTitle(), panel);
        tabs.addTab(new WString(panel.getTitle()), panel);
	}

	@Override
	public List<WRadioButton> getRadioButtons() {
    	ArrayList<WRadioButton> buttons = new ArrayList<WRadioButton>();
    	for (WAWCSelectorPanel panel : panels.values()) {
    		buttons.addAll(panel.getRadioButtons());
    	}
    	return buttons;
	}

	@Override
	public WAWCEditorPanel getSelectedClause() {
		if (tabs.getSelectedTab() != null) {
			WAWCSelectorPanel p = (WAWCSelectorPanel) tabs.getSelectedTab();
			return p.getSelectedClause();
		}		
		return null;
	}

	@Override
	public boolean isSelected() {
		boolean selected = false;
		if (tabs.getSelectedTab() != null) {
			WAWCSelectorPanel p = (WAWCSelectorPanel) tabs.getSelectedTab();
			selected = p.isSelected();
		}
		return selected;
	}

	@Override
	public boolean isUseless() {
		for (WAWCSelectorPanel panel: panels.values()) {
			if (!panel.isUseless()) {
				return false;
			}
		}
		return true;
	}
}
