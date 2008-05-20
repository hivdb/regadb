package net.sf.regadb.ui.form.query.querytool.tree;


import com.pharmadm.custom.rega.queryeditor.WhereClause;

import net.sf.regadb.ui.form.query.querytool.buttons.AtomicClauseButtonPanel;
import net.sf.regadb.ui.form.query.querytool.buttons.NonAtomicClauseButtonPanel;
import net.sf.witty.wt.i8n.WMessage;

public class WhereClauseNode extends QueryTreeNode{
	
	
	public WhereClauseNode(WhereClause clause, QueryEditorGroupBox editor) {
		super(clause, editor, null);
		init();
	}
	
	private void init() {
		if (getClause().isAtomic()) {
			getStyleClasses().addStyle("atomictreenode");
			setButtonPanel(new AtomicClauseButtonPanel(this));
		}
		else {
			getStyleClasses().addStyle("composedtreenode");
			String label = getClause().toString();
			label = label.substring(0, label.indexOf(' '));
			this.label().setText(new WMessage(label.toLowerCase(), true));
			
			if (getEditorModel().getQueryEditor().acceptsAdditionalChild(getClause())) {
				setButtonPanel(new NonAtomicClauseButtonPanel(this));
			}
		}
	}
}
