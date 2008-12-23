package net.sf.regadb.ui.form.query.querytool.tree;


import net.sf.regadb.ui.form.query.querytool.QueryToolApp;
import net.sf.regadb.ui.form.query.querytool.buttons.AtomicClauseButtonPanel;
import net.sf.regadb.ui.form.query.querytool.buttons.NonAtomicClauseButtonPanel;

import com.pharmadm.custom.rega.queryeditor.WhereClause;

/**
 * tree node containing a WhereClause
 * @author fromba0
 *
 */
public class WhereClauseNode extends QueryTreeNode{
	
	
	public WhereClauseNode(WhereClause clause, QueryToolApp editor) {
		super(clause, editor, null);
	}
	
	protected void createContentTable() {
		super.createContentTable();
		
		if (getClause().isAtomic()) {
			getStyleClasses().removeStyle("composedtreenode");
			getStyleClasses().addStyle("atomictreenode");
			setButtonPanel(new AtomicClauseButtonPanel(this));
		}
		else {
			getStyleClasses().removeStyle("atomictreenode");
			getStyleClasses().addStyle("composedtreenode");
			String label = getClause().toString();
			label = label.substring(0, label.indexOf(' '));
			this.label().setText(lt(label.toLowerCase()));
			
			if (getQueryApp().getEditorModel().getQueryEditor().acceptsAdditionalChild(getClause())) {
				setButtonPanel(new NonAtomicClauseButtonPanel(this));
			}
		}
	}
}
