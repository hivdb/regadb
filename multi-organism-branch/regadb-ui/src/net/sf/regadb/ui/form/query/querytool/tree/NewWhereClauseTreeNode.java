package net.sf.regadb.ui.form.query.querytool.tree;

import net.sf.regadb.ui.form.query.querytool.QueryToolApp;
import net.sf.regadb.ui.form.query.querytool.dialog.SelectClauseDialog;

/**
 * tree node containing a dialog to
 * select a new clause
 * @author fromba0
 *
 */
public class NewWhereClauseTreeNode extends QueryTreeNode{
	
	public NewWhereClauseTreeNode(QueryToolApp editor, QueryTreeNode parent) {
		super(null, editor, parent);
		init();
	}
	
	private void init() {
		SelectClauseDialog dialog = new SelectClauseDialog(this);
		getStyleClasses().addStyle("atomictreenode");
		showDialog(dialog);
	}
	
	/**
	 * load the all the clauses into the dialog
	 * @param focusGroup the group that should have preference in the dialog
	 */
	public void loadContent(String focusGroup) {
		((SelectClauseDialog) getDialog()).loadContent(getParentNode().getClause(), focusGroup);
	}
}
