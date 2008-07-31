package net.sf.regadb.ui.form.query.querytool.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.WhereClause;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;

import net.sf.regadb.ui.form.query.querytool.awceditor.WAWCEditorPanel;
import net.sf.regadb.ui.form.query.querytool.awceditor.WAWCSelectorTabbedPane;
import net.sf.regadb.ui.form.query.querytool.buttons.SelectClauseButtonPanel;
import net.sf.regadb.ui.form.query.querytool.tree.QueryTreeNode;
import net.sf.regadb.ui.form.query.querytool.widgets.WButtonPanel;
import net.sf.regadb.ui.form.query.querytool.widgets.WDialog;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WKeyEvent;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WTimer;

public class SelectClauseDialog extends WDialog {
	private QueryTreeNode owner;
	private WAWCSelectorTabbedPane rootSelector;
	private WButtonPanel buttonPanel;
	private HashMap<String, List<AtomicWhereClause>> clauses = new HashMap<String, List<AtomicWhereClause>>();
	private String focusGroup;
	
	public SelectClauseDialog(QueryTreeNode owner) {
		super(tr("query.querytool.dialog.add"));
		this.owner = owner;
		buttonPanel = new SelectClauseButtonPanel(owner, this);
		setButtonPanel(buttonPanel);
		getStyleClasses().addStyle("newclausedialog");
	}
	
	public void loadContent(final WhereClause parentClause, String focusGroup) {
		this.focusGroup = focusGroup;
		rootSelector = new WAWCSelectorTabbedPane(owner.getQueryApp().getQueryContext());
		getContentPanel().addWidget(rootSelector);
		final Collection<AtomicWhereClause> prototypeList = parentClause.getAvailableAtomicClauses(DatabaseManager.getInstance().getAWCCatalog());
		
		// map results to their groups so we can update tab by tab
		for (AtomicWhereClause clause : prototypeList) {
			for (String group : clause.getGroups()) {
				if (!clauses.containsKey(group)) {
					clauses.put(group, new ArrayList<AtomicWhereClause>());
				}
				clauses.get(group).add(clause);
			}
		}
		
		// fill in the first element of every tab so the tab bar looks complete
		Vector<String> v = new Vector<String>(clauses.keySet());
		Collections.sort(v);
		for (String key : v) {
			rootSelector.addAtomicWhereClause(clauses.get(key).get(0));
		}
		
		if (this.focusGroup == null || !clauses.containsKey(this.focusGroup)) {
			this.focusGroup = clauses.keySet().iterator().next();
		}
		
		// then fill in the active tab
		List<AtomicWhereClause> clauseList = clauses.get(this.focusGroup);
		rootSelector.addAll(clauseList.subList(1, clauseList.size()), this.focusGroup);
		rootSelector.showTab(this.focusGroup);
		
		// only start filling in all the other tabs after the UI
		// has updated
		WTimer.singleShot(1, new SignalListener<WEmptyEvent>() {
			public void notify(WEmptyEvent a) {
				for (String key : clauses.keySet()) {
					if (!key.equals(SelectClauseDialog.this.focusGroup)) {
						List<AtomicWhereClause> clauseList = clauses.get(key);
						rootSelector.addAll(clauseList.subList(1, clauseList.size()), key);
					}
				}
				
				rootSelector.keyWentUp.addListener(new SignalListener<WKeyEvent>() {
					public void notify(WKeyEvent a) {
						if (getSelectedClause() != null) {
							setEditable(!getSelectedClause().getManager().isUseless());
						}
					}
				});
				
				rootSelector.clicked.addListener(new SignalListener<WMouseEvent>() {
					public void notify(WMouseEvent a) {
						if (getSelectedClause() != null) {
							setEditable(!getSelectedClause().getManager().isUseless());
						}
					}
				});
			}
		});
	}
	
	private void setEditable(boolean editable) {
		buttonPanel.setEnabled(editable);
	}
	
	public WAWCEditorPanel getSelectedClause() {
		return rootSelector.getSelectedClause();
	}
}
