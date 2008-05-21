package net.sf.regadb.ui.form.query.querytool.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.IllegalWhereClauseCompositionException;
import com.pharmadm.custom.rega.queryeditor.WhereClause;

import net.sf.regadb.ui.form.query.querytool.CssClasses;
import net.sf.regadb.ui.form.query.querytool.buttons.ButtonPanel;
import net.sf.regadb.ui.form.query.querytool.dialog.WDialog;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public abstract class QueryTreeNode extends WTreeNode {
	private WhereClause object;
	private QueryEditorGroupBox editor;
	private WCheckBox checkBox;
	private ButtonPanel buttonPanel;
	private WDialog editDialog;
	private WTable contentTable;
	
	private CssClasses styleClasses;
	
	public QueryTreeNode(WhereClause clause, QueryEditorGroupBox editor, QueryTreeNode parent) {
		super(new WMessage("", true), null, parent);
		if (clause != null) {
			label().setText(new WMessage(clause.toString(), true));		
		}
		setImagePack("pics/");
		this.object = clause;
		this.editor = editor;
		styleClasses = new CssClasses(this.labelArea());
		init();
	}
	
	public CssClasses getStyleClasses() {
		return styleClasses;
	}
	
	protected void doCollapse() {
		if (editor.isEditable()) {
			super.doCollapse();
		}
	}
	
	protected void undoDoExpand() {
		if (editor.isEditable()) {
			super.undoDoExpand();
		}
	}
	
	private void init() {
		labelArea().removeWidget(label());
		labelArea().removeWidget(childCountLabel_);
		contentTable = new WTable(labelArea());
		contentTable.putElementAt(0, 0, label());
		contentTable.putElementAt(0, 0, childCountLabel_);
		contentTable.setStyleClass("treenodecontent");
		contentTable.elementAt(0, 0).setStyleClass("labelcontent");
		
		checkBox = new WCheckBox();
		checkBox.setStyleClass("check");
		contentTable.putElementAt(0, 2, checkBox);
		contentTable.elementAt(0, 2).setStyleClass("checkbox");
		
		checkBox.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				setChecked(checkBox.isChecked());
			}
		});
		
		if (getClause() != null) {
			Iterator<WhereClause> it = getClause().iterateChildren();
			while (it.hasNext()) {
				WTreeNode node = new WhereClauseNode(it.next(), getEditorModel());
				addChildNode(node);
			}		
		}
		expand();
		
		contentTable.elementAt(0, 0).clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
					setChecked(!checkBox.isChecked());
			}
		});
	}
	
	private void setChecked(boolean checked) {
		if (object == null || object.getParent() == null || !editor.isEditable()) {
			return;
		}
		
		if (checkBox.isChecked() != checked) {
			checkBox.setChecked(checked);
		}
		if (checkBox.isChecked()) {
			styleClasses.addStyle("selectedclause");
		}
		else {
			styleClasses.removeStyle("selectedclause");
		}
		editor.updateSelection();
	}	
	
	public WhereClause getClause() {
		return object;
	}
	
	public QueryEditorGroupBox getEditorModel() {
		return editor;
	}
	
	public WDialog getDialog() {
		return editDialog;
	}
	
	public void setButtonPanel(ButtonPanel panel) {
		if (hasButtonPanel()) {
			contentTable.elementAt(0, 1).removeWidget(buttonPanel);
		}
		contentTable.putElementAt(0, 1, panel);
		buttonPanel = panel;
	}
	
	/**
	 * check if the query is still valid
	 */
	public void revalidate() {
		if (object != null && !object.isValid()) {
			if (object.isAtomic() && !label().text().keyOrValue().equals(object.toString())) {
				label().setText(lt(object.toString()));
			}
			styleClasses.addStyle("invalidclause");
		}
		else {
			styleClasses.removeStyle("invalidclause");
		}
		
		for (WTreeNode child : childNodes()) {
			((QueryTreeNode) child).revalidate();
		}
	}

	/**
	 * hides the previous dialog if present
	 * hides the content
	 * show the given dialog
	 * @param dialog
	 */
	public void showDialog(WDialog dialog) {
		hideDialog();
		getParentNode().expand();
		
		getEditorModel().setContextClause(getParentNode().getClause());
		editDialog = dialog;
		labelArea().addWidget(dialog);
		hideContent();
	}
	
	
	/**
	 * hide the current dialog
	 */
	private void hideDialog() {
		if (hasDialog()) {
			labelArea().removeWidget(editDialog);
		}
		editDialog = null;
	}
	
	private void hideContent() {
		editor.setEditable(false);
		contentTable.setStyleClass("disabledtreenodecontent");
	}
	
	/**
	 * hide the dialog
	 * shows the regular content
	 */
	public void showContent() {
		hideDialog();
		contentTable.setStyleClass("treenodecontent");
		editor.setEditable(true);
		getEditorModel().setContextClause(null);
	}
	
	/**
	 * returns true if this node has a dialog open
	 * @return
	 */
	public boolean hasDialog() {
		return editDialog != null;
	}
	
	private boolean hasButtonPanel() {
		return buttonPanel != null;
	}
	
	public ButtonPanel getButtonPanel() {
		return buttonPanel;
	}
	
	/** 
	 * return the parent of this node
	 * @return
	 */
	public QueryTreeNode getParentNode() {
		return (QueryTreeNode) parentNode_;
	}
	
	/**
	 * enable or disable the controls of this node
	 * @param editable
	 */
	public void setEditable(boolean editable) {
		for (WTreeNode child : childNodes()) {
			((QueryTreeNode) child).setEditable(editable);
		}
		if (hasButtonPanel()) {
			getButtonPanel().setEditable(editable);
		}
		if (editable && getParentNode() != null) {
			checkBox.enable();
		}
		else {
			checkBox.disable();
		}
	}

	/**
	 * get the list of selected nodes
	 * this list includes this nodes if it is selected
	 * @return
	 */
	public List<QueryTreeNode> getSelection() {
		List<QueryTreeNode> selection = new ArrayList<QueryTreeNode>();
		for (WTreeNode child : childNodes()) {
			selection.addAll(((QueryTreeNode) child).getSelection());
		}
		if (checkBox.isChecked()) selection.add(this);
		return selection;
	}
	
	/**
	 * adds a node to this node if possible
	 * @param clause
	 */
	public void addNode(WhereClause clause) {
		if (getClause() != null && getClause().acceptsAdditionalChild()) {
			try {
				editor.getQueryEditor().addChild(getClause(), clause);
				WTreeNode node = new WhereClauseNode(clause, editor);
				addChildNode(node);
				
				// update local enabled state
				this.setEditable(editor.isEditable());
			} catch (IllegalWhereClauseCompositionException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * remove the given node from this node or its children
	 * @param node
	 */
	public void removeNode(QueryTreeNode node) {
		if (childNodes().contains(node)) {
			WhereClause clause = node.getClause();
			this.removeChildNode(node);
			editor.getQueryEditor().removeChild(getClause(), clause);

			// we only need to update the enabled state for the element that contained the node to be removed
			this.setEditable(editor.isEditable());
		}
		else {
			for (WTreeNode childNode : childNodes()) {
				((QueryTreeNode) childNode).removeNode(node);
			}
		}
		
	}

	/**
	 * remove the given list of nodes from this node
	 * @param nodes
	 */
	public void removeAll(List<QueryTreeNode> nodes) {
		List<WhereClause> clauses = new ArrayList<WhereClause>();
		
		for (QueryTreeNode node : nodes) {
			clauses.add(node.getClause());
			this.removeChildNode(node);
		}
		
		for (WhereClause clause : clauses) {
			editor.getQueryEditor().removeChild(getClause(), clause);
		}

		// we only need to update the enabled state for the element that contained the node to be removed
		this.setEditable(editor.isEditable());
	}
	
	/**
	 * open a select simple clause dialog in this node
	 */
	public void selectNewNode() {
		NewWhereClauseTreeNode node = new NewWhereClauseTreeNode(editor, this);
		node.loadContent();
	}
	
	/**
	 * replace the clause in the current node by the given clause
	 * @param newClause
	 */
	public void replaceNode(WhereClause newClause) {
		if (getClause() != null) {
			try {
				WhereClause oldClause = getClause();
				object = newClause;
				editor.getQueryEditor().replaceChild(getParentNode().getClause(), oldClause, newClause);
				label().setText(new WMessage(newClause.toString(), true));
				
				// update local enabled state
				this.setEditable(editor.isEditable());
			} catch (IllegalWhereClauseCompositionException e) {
				e.printStackTrace();
			}
		}
	}
}
