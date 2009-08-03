package net.sf.regadb.ui.form.query.querytool.awceditor;

import java.util.ArrayList;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedAWCEditorPanel;

import eu.webtoolkit.jwt.Key;
import eu.webtoolkit.jwt.KeyboardModifier;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WKeyEvent;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WRadioButton;
import eu.webtoolkit.jwt.WTable;

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
		if (isUseless()) {
			radioButton.disable();
		}
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
    	table.getElementAt(0, 0).addWidget(radioButton);
    	table.getElementAt(0, 0).resize(new WLength(2, WLength.Unit.FontEm), new WLength());
    	table.getElementAt(0, 1).addWidget(editPanel);
    	if (!isUseless()) {
	    	this.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
				public void trigger(WMouseEvent a) {
					radioButton.setChecked(true);
					radioButton.refresh();
				}
	    	});
	    	this.keyPressed().addListener(this, new Signal1.Listener<WKeyEvent>() {
				public void trigger(WKeyEvent a) {
					if (a.getKey() != Key.Key_Tab && ! a.getModifiers().contains(KeyboardModifier.MetaModifier)) {
						radioButton.setChecked(true);
						radioButton.refresh();
					}
				}
	    	});
    	}
    }

	@Override
	public boolean isUseless() {
		return editPanel.getManager().isUseless();
	}    
}
