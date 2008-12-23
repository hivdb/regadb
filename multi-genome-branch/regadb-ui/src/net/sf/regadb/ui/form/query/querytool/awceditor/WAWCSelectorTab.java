package net.sf.regadb.ui.form.query.querytool.awceditor;

import java.util.ArrayList;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;

import eu.webtoolkit.jwt.WButtonGroup;
import eu.webtoolkit.jwt.WRadioButton;

public class WAWCSelectorTab extends WAWCSelectorPanel {

	private List<WAWCSelectorPanel> selectorPanels;
	private String title;
	private WButtonGroup group;
	
	private QueryContext context;
	private boolean firstChecked = false;
	
	public WAWCSelectorTab(String title, QueryContext context) {
		super();
		setStyleClass("selectorpanel selectortab");
		this.context = context;
		this.title = title;
		selectorPanels = new ArrayList<WAWCSelectorPanel>();
		group = new WButtonGroup(this);
	}
	
	public String getTitle() {
		return title;
	}
	
	@Override
	public boolean addSelectorPanel(WAWCSelectorPanel panel) {
		selectorPanels.add(panel);
		addWidget(panel);
		return true;
	}

	/**
	 * will add the clause to the first child AWCSelectorPanel that will accept it.
	 * if no suitable child is found a new ComposedAtomicWhereClauseSelectorPanel 
	 * is made for the clause
	 */
	@Override
	public boolean addAtomicWhereClause(AtomicWhereClause clause) {
        for (WAWCSelectorPanel selectorPanel : selectorPanels) {
        	if (selectorPanel.addAtomicWhereClause(clause)) {
        		return true;
        	}
        }
        WAWCSelectorPanel newPanel = new WComposedAWCSelectorPanel(context, clause);
        newPanel.setStyleClass(newPanel.styleClass() + " " + (selectorPanels.size() % 2 == 0 ? "even":"odd"));
        group.addButton(newPanel.getRadioButtons().get(0));
        if (!firstChecked) {
        	newPanel.getRadioButtons().get(0).setChecked(true);
        	firstChecked = true;
        }
        else {
        	newPanel.getRadioButtons().get(0).setChecked(false);
        }
        return addSelectorPanel(newPanel);
	}
	
	@Override
	public boolean isSelected() {
		boolean selected = false;
		for (WAWCSelectorPanel panel : selectorPanels) {
			selected = selected || panel.isSelected();
		}
		return selected;
	}

	@Override
	public WAWCEditorPanel getSelectedClause() {

		WAWCEditorPanel selectedClause = null;
		for (WAWCSelectorPanel panel : selectorPanels) {
			selectedClause = panel.getSelectedClause();
			if (selectedClause != null) {
				return selectedClause;
			}
		}
		return selectedClause;
	}
	
	@Override
    public List<WRadioButton> getRadioButtons() {
    	ArrayList<WRadioButton> buttons = new ArrayList<WRadioButton>();
    	for (WAWCSelectorPanel panel : selectorPanels) {
    		buttons.addAll(panel.getRadioButtons());
    	}
    	return buttons;
    }

	@Override
	public boolean isUseless() {
		for (WAWCSelectorPanel panel: selectorPanels) {
			if (!panel.isUseless()) {
				return false;
			}
		}
		return true;
	}
}
