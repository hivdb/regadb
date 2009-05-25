package net.sf.regadb.ui.form.query.querytool.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import net.sf.regadb.ui.form.query.querytool.awceditor.WAWCEditorPanel;
import net.sf.regadb.ui.form.query.querytool.awceditor.WAWCSelectorTabbedPane;
import net.sf.regadb.ui.form.query.querytool.buttons.SelectClauseButtonPanel;
import net.sf.regadb.ui.form.query.querytool.tree.QueryTreeNode;
import net.sf.regadb.ui.form.query.querytool.widgets.MyDialog;
import net.sf.regadb.ui.form.query.querytool.widgets.WButtonPanel;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.WhereClause;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WKeyEvent;
import eu.webtoolkit.jwt.WMouseEvent;

public class SelectClauseDialog extends MyDialog {
	private QueryTreeNode owner;
	private WAWCSelectorTabbedPane rootSelector;
	private WButtonPanel buttonPanel;
	private HashMap<String, List<AtomicWhereClause>> clauses = new HashMap<String, List<AtomicWhereClause>>();
	private String focusGroup;
	
	public SelectClauseDialog(QueryTreeNode owner) {
		super(tr("form.query.querytool.dialog.add"));
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
//		UIUtils.singleShot(this, 1, new Signal.Listener() {
//			public void trigger() {
				for (String key : clauses.keySet()) {
					if (!key.equals(SelectClauseDialog.this.focusGroup)) {
						clauseList = clauses.get(key);
						rootSelector.addAll(clauseList.subList(1, clauseList.size()), key);
					}
				}
				
				rootSelector.keyWentUp().addListener(SelectClauseDialog.this, new Signal1.Listener<WKeyEvent>() {
					public void trigger(WKeyEvent a) {
						if (getSelectedClause() != null) {
							setEditable(!getSelectedClause().getManager().isUseless());
						}
					}
				});
				
				rootSelector.clicked().addListener(SelectClauseDialog.this, new Signal1.Listener<WMouseEvent>() {
					public void trigger(WMouseEvent a) {
						if (getSelectedClause() != null) {
							setEditable(!getSelectedClause().getManager().isUseless());
						}
					}
				});
//			}
//		});
	}
	
	private void setEditable(boolean editable) {
		buttonPanel.setEnabled(editable);
	}
	
	public WAWCEditorPanel getSelectedClause() {
		return rootSelector.getSelectedClause();
	}
}
