package net.sf.regadb.ui.form.query.querytool.awceditor;

import java.util.ArrayList;
import java.util.List;

import net.sf.witty.wt.WRadioButton;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;

public class WAWCSelectorPanelContainer extends WAWCSelectorPanel {

	private List<WAWCSelectorPanel> panels = new ArrayList<WAWCSelectorPanel>();
	
	public WAWCSelectorPanelContainer() {
	}

	@Override
	public boolean addAtomicWhereClause(AtomicWhereClause clause) {
		return false;
	}

	@Override
	public boolean addSelectorPanel(WAWCSelectorPanel panel) {
		panels.add(panel);
		this.addWidget(panel);
		return true;
	}

	@Override
	public List<WRadioButton> getRadioButtons() {
		List<WRadioButton> buttons = new ArrayList<WRadioButton>();
		for (WAWCSelectorPanel panel: panels) {
			buttons.addAll(panel.getRadioButtons());
		}
		return buttons;
	}

	@Override
	public WAWCEditorPanel getSelectedClause() {
		for (WAWCSelectorPanel panel: panels) {
			if (panel.isSelected()) {
				return panel.getSelectedClause();
			}
		}
		return null;
	}

	@Override
	public boolean isSelected() {
		for (WAWCSelectorPanel panel: panels) {
			if (panel.isSelected()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isUseless() {
		for (WAWCSelectorPanel panel: panels) {
			if (!panel.isUseless()) {
				return false;
			}
		}
		return true;
	}
}
