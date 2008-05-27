package net.sf.regadb.ui.form.query.querytool.dialog;

import java.util.Collection;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.WhereClause;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;

import net.sf.regadb.ui.form.query.querytool.awceditor.WAWCEditorPanel;
import net.sf.regadb.ui.form.query.querytool.awceditor.WAWCSelectorTabbedPane;
import net.sf.regadb.ui.form.query.querytool.buttons.ButtonPanel;
import net.sf.regadb.ui.form.query.querytool.buttons.SelectClauseButtonPanel;
import net.sf.regadb.ui.form.query.querytool.tree.QueryTreeNode;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WButtonGroup;
import net.sf.witty.wt.WKeyEvent;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WRadioButton;
import net.sf.witty.wt.i8n.WMessage;

public class SelectClauseDialog extends WDialog {
	private QueryTreeNode owner;
	private WAWCSelectorTabbedPane rootSelector;
	private ButtonPanel buttonPanel;
	
	public SelectClauseDialog(QueryTreeNode owner) {
		super(new WMessage("form.query.querytool.dialog.add"));
		this.owner = owner;
		buttonPanel = new SelectClauseButtonPanel(owner, this);
		setButtonPanel(buttonPanel);
		setStyleClass(styleClass() + " newclausedialog");
	}
	
	public void loadContent(WhereClause parentClause) {
		WButtonGroup group = new WButtonGroup(getContentArea());
		
		rootSelector = new WAWCSelectorTabbedPane(owner.getEditorModel().getQueryContext());
		getContentArea().addWidget(rootSelector);
		
        Collection<AtomicWhereClause> prototypeList = parentClause.getAvailableAtomicClauses(DatabaseManager.getInstance().getAWCCatalog());
		for (AtomicWhereClause clause : prototypeList) {
			rootSelector.addAtomicWhereClause(clause);
		}
		
		for (WRadioButton button : rootSelector.getRadioButtons()) {
			group.addButton(button);
		}
		rootSelector.keyWentUp.addListener(new SignalListener<WKeyEvent>() {
			public void notify(WKeyEvent a) {
				buttonPanel.setEditable(getSelectedClause().isUseless());
			}
		});
		
		rootSelector.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				buttonPanel.setEditable(!getSelectedClause().isUseless());
			}
		});
	}
	
	public WAWCEditorPanel getSelectedClause() {
		return rootSelector.getSelectedClause();
	}
}
