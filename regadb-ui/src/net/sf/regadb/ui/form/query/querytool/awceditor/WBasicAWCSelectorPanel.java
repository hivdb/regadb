package net.sf.regadb.ui.form.query.querytool.awceditor;

import java.util.ArrayList;
import java.util.List;

import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WRadioButton;
import net.sf.witty.wt.WTable;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedAWCEditorPanel;

public class WBasicAWCSelectorPanel extends WAWCSelectorPanel {

    protected WAWCEditorPanel editPanel;
    private WRadioButton radioButton;
    protected QueryContext context;
	
    /** Creates new form AtomicWhereClauseSelectorPanel */
    public WBasicAWCSelectorPanel(QueryContext context, AtomicWhereClause clause) {
		this.radioButton = new WRadioButton();
		this.context = context;
		this.editPanel = new WAWCEditorPanel(new WAtomicWhereClauseEditor(context, clause));
		this.setStyleClass("selectorpanel");
		initMoreComponents();
    }
    
    protected ComposedAWCEditorPanel getEditorPanel() {
        return editPanel;
    }

	@Override
	/**
	 * can not add additional panels to this panel
	 */
	public boolean addSelectorPanel(WAWCSelectorPanel panel) {
		return false;
	}

	@Override
	public WAWCEditorPanel getSelectedClause() {
		if (isSelected()) {
			return editPanel;
		}
		return null;
	}
	
	@Override
	public List<WRadioButton> getRadioButtons() {
		ArrayList<WRadioButton> buttons = new ArrayList<WRadioButton>();
		buttons.add(radioButton);
		return buttons;
	}
	
	@Override
    public boolean isSelected() {
        return radioButton.isChecked();
    }
    
	/**
	 * can not add additional clauses to this panel
	 */
	public boolean addAtomicWhereClause(AtomicWhereClause clause) {
		return false;
	}
	
    private void initMoreComponents() {
    	WTable table = new WTable(this);
		radioButton.setStyleClass("selectorradio");
    	table.putElementAt(0, 0, radioButton);
    	table.putElementAt(0, 1, editPanel);
    	editPanel.clicked.addListener(new SignalListener<WMouseEvent>(){
			public void notify(WMouseEvent a) {
				radioButton.setChecked(!radioButton.isChecked());
			}
    	});
    }    
}
