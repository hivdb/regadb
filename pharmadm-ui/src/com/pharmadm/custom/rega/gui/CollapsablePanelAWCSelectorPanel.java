package com.pharmadm.custom.rega.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JRadioButton;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;

/**
 * takes CollapsableAWCSelectorPanels 
 * all clauses will be added to CollapsableAWCSelectorPanel coresponding to
 * the groups the clauses belong to
 * @author fromba0
 *
 */
public class CollapsablePanelAWCSelectorPanel extends AWCSelectorPanel{

	private QueryContext context;
	private HashMap<String, AWCSelectorPanel> panels;
	
	public CollapsablePanelAWCSelectorPanel(QueryContext context) {
		this.context = context;
        panels = new HashMap<String, AWCSelectorPanel>(); 
        setLayout(new GridBagLayout());
	}
	
	@Override
	public boolean addAtomicWhereClause(AtomicWhereClause clause) {
        for (String group : clause.getGroups()) {
    		if (!panels.containsKey(group)) {
    			addSelectorPanel(new CollapsableAWCSelectorPanel(group, context));
    		}
    		
    		panels.get(group).addAtomicWhereClause(clause);
        }
        return true;
	}

	@Override
	public boolean addSelectorPanel(AWCSelectorPanel panel) {
		if (panel instanceof CollapsableAWCSelectorPanel) {
			CollapsableAWCSelectorPanel collapsablePanel = (CollapsableAWCSelectorPanel) panel;
			addSelectorPanel(collapsablePanel);
			return true;
		}
		return false;
	}
	
	private void addSelectorPanel(CollapsableAWCSelectorPanel panel) {
		panels.put(panel.getTitle(), panel);
    	GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        add(panel ,gridBagConstraints);
	}

	@Override
	public void freeResources() {
		for (AWCSelectorPanel panel : panels.values()) {
			panel.freeResources();
		}
	}

	@Override
	public List<JRadioButton> getRadioButtons() {
    	ArrayList<JRadioButton> buttons = new ArrayList<JRadioButton>();
    	for (AWCSelectorPanel panel : panels.values()) {
    		buttons.addAll(panel.getRadioButtons());
    	}
    	return buttons;
	}

	@Override
	public AWCEditorPanel getSelectedClause() {
		AWCEditorPanel selectedClause = null;
		for (AWCSelectorPanel panel : panels.values()) {
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
		for (AWCSelectorPanel panel : panels.values()) {
			selected = selected || panel.isSelected();
		}
		return selected;
	}
}