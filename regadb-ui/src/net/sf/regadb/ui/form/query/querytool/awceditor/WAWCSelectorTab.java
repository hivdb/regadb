package net.sf.regadb.ui.form.query.querytool.awceditor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.basic.BasicBorders.RadioButtonBorder;

import net.sf.witty.wt.WRadioButton;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;

public class WAWCSelectorTab extends WAWCSelectorPanel {

	private List<WAWCSelectorPanel> selectorPanels;
	private String title;
	
	private QueryContext context;
	
	public WAWCSelectorTab(String title, QueryContext context) {
		super();
		setStyleClass("selectorpanel selectortab");
		this.context = context;
		this.title = title;
		selectorPanels = new ArrayList<WAWCSelectorPanel>();
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

	public void show() {
		super.show();
		int i = 0;
		boolean selected = false;
		List<WRadioButton> radioButtons = getRadioButtons();
		while (i < radioButtons.size() && !selected) {
			if (!selectorPanels.get(i).isUseless()) {
				radioButtons.get(i).setChecked(true);
				selected = true;
			}
		}
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
