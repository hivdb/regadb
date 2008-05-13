package net.sf.regadb.ui.form.query.querytool;


import com.pharmadm.custom.rega.queryeditor.WhereClause;

import net.sf.regadb.ui.form.query.querytool.buttons.AtomicClauseButtonPanel;
import net.sf.regadb.ui.form.query.querytool.buttons.NonAtomicClauseButtonPanel;
import net.sf.witty.wt.i8n.WMessage;

public class WhereClauseNode extends QueryTreeNode{
	
	public WhereClauseNode(WhereClause clause, QueryEditorGroupBox editor) {
		super(clause, editor);
		init();
	}
	
	private void init() {
		if (getClause().isAtomic()) {
			labelArea().setStyleClass("atomictreenode");
			setButtonPanel(new AtomicClauseButtonPanel(this));
		}
		else {
			labelArea().setStyleClass("composedtreenode");
			String label = getClause().toString();
			label = label.substring(0, label.indexOf(' '));
			this.label().setText(new WMessage(label.toLowerCase(), true));
			
			if (getQueryEditor().getQueryEditor().acceptsAdditionalChild(getClause())) {
				setButtonPanel(new NonAtomicClauseButtonPanel(this));
			}
		}
	}
}
