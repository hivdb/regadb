package net.sf.regadb.ui.form.query.querytool.dialog;

import java.util.Collection;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.WhereClause;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;

import net.sf.regadb.ui.form.query.querytool.awceditor.WAWCEditorPanel;
import net.sf.regadb.ui.form.query.querytool.awceditor.WAWCSelectorPanel;
import net.sf.regadb.ui.form.query.querytool.awceditor.WAWCSelectorTabbedPane;
import net.sf.regadb.ui.form.query.querytool.buttons.SelectClauseButtonPanel;
import net.sf.regadb.ui.form.query.querytool.tree.QueryTreeNode;
import net.sf.witty.wt.WButtonGroup;
import net.sf.witty.wt.WRadioButton;
import net.sf.witty.wt.i8n.WMessage;

public class SelectClauseDialog extends WDialog {
	private QueryTreeNode owner;
	private WAWCSelectorPanel rootSelector;
	
	public SelectClauseDialog(QueryTreeNode owner) {
		super(new WMessage("form.query.querytool.dialog.add"));
		this.owner = owner;
		setButtonPanel(new SelectClauseButtonPanel(owner, this));
	}
	
	public void loadContent(WhereClause parentClause) {
		WButtonGroup group = new WButtonGroup(getContentArea());
		
		rootSelector = new WAWCSelectorTabbedPane(owner.getQueryEditor().getQueryContext());
		getContentArea().addWidget(rootSelector);
		
        Collection<AtomicWhereClause> prototypeList = parentClause.getAvailableAtomicClauses(DatabaseManager.getInstance().getAWCCatalog());
		for (AtomicWhereClause clause : prototypeList) {
			rootSelector.addAtomicWhereClause(clause);
		}
		
		for (WRadioButton button : rootSelector.getRadioButtons()) {
			group.addButton(button);
		}
	}
	
	public WAWCEditorPanel getSelectedClause() {
		return rootSelector.getSelectedClause();
	}
}
