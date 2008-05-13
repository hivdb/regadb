package net.sf.regadb.ui.form.query.querytool;

public class NewWhereClauseTreeNode extends QueryTreeNode{
	
	
	
	public NewWhereClauseTreeNode(QueryEditorGroupBox editor) {
		super(null, editor);
		init();
	}
	
	private void init() {
		SelectClauseDialog dialog = new SelectClauseDialog(this);
		labelArea().setStyleClass("atomictreenode");
		hideRegularContent(dialog);
	}
	
	public void loadContent() {
		((SelectClauseDialog) getDialog()).loadContent(getParentNode().getClause());
	}
}
