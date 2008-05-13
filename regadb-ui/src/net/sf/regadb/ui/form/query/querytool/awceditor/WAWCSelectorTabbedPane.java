package net.sf.regadb.ui.form.query.querytool.awceditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.regadb.ui.form.query.querytool.WTabbedPane;
import net.sf.witty.wt.WRadioButton;
import net.sf.witty.wt.i8n.WMessage;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;

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
        tabs.addTab(new WMessage(panel.getTitle(), true), panel);
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
		WAWCEditorPanel selectedClause = null;
		for (WAWCSelectorPanel panel : panels.values()) {
			selectedClause = panel.getSelectedClause();
			if (selectedClause != null) {
				return selectedClause;
			}
		}
		return selectedClause;
	}

	@Override
	public boolean isSelected() {
		boolean selected = false;
		for (WAWCSelectorPanel panel : panels.values()) {
			selected = selected || panel.isSelected();
		}
		return selected;
	}

}
