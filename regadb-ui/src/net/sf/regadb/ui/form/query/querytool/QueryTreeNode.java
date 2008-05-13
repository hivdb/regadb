package net.sf.regadb.ui.form.query.querytool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.IllegalWhereClauseCompositionException;
import com.pharmadm.custom.rega.queryeditor.WhereClause;

import net.sf.regadb.ui.form.query.querytool.buttons.ButtonPanel;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.extra.WTreeNode;

public abstract class QueryTreeNode extends WTreeNode {
	private WhereClause object;
	private QueryEditorGroupBox editor;
	private WCheckBox checkBox;
	private ButtonPanel buttonPanel;
	private WDialog editDialog;
	
	public QueryTreeNode(WhereClause clause, QueryEditorGroupBox editor) {
		super(new WMessage("", true));
		if (clause != null) {
			label().setText(new WMessage(clause.toString(), true));		
		}
		setImagePack("pics/");
		this.object = clause;
		this.editor = editor;
		init();
	}
	
	private void init() {
		checkBox = new WCheckBox();
		checkBox.setStyleClass("check");
		labelArea().addWidget(checkBox);
		
		checkBox.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				setChecked(checkBox.isChecked());
				editor.updateSelection();
			}
		});
		
		if (getClause() != null) {
			Iterator<WhereClause> it = getClause().iterateChildren();
			while (it.hasNext()) {
				WTreeNode node = new WhereClauseNode(it.next(), getQueryEditor());
				addChildNode(node);
			}		
		}
		expand();
	}
	
	private void setChecked(boolean checked) {
		if (checkBox.isChecked() != checked) {
			checkBox.setChecked(checked);
		}
	}	
	
	public WhereClause getClause() {
		return object;
	}
	
	public QueryEditorGroupBox getQueryEditor() {
		return editor;
	}
	
	public WDialog getDialog() {
		return editDialog;
	}
	
	public void setButtonPanel(ButtonPanel panel) {
		if (buttonPanel != null) {
			labelArea().removeWidget(buttonPanel);
		}
		labelArea().addWidget(panel);
		buttonPanel = panel;
	}
	

	
	public void hideRegularContent(WDialog dialog) {
		if (editDialog != null) {
			labelArea().removeWidget(editDialog);
		}
		
		editDialog = dialog;
		if (dialog != null) {
			editor.setEnabled(false);
			if (hasButtonPanel()) getButtonPanel().hide();
			label().hide();
			checkBox.hide();
			labelArea().addWidget(dialog);
		}
	}
	
	public void showRegularContent() {
		label().show();
		if (hasButtonPanel()) getButtonPanel().show();
		editor.setEnabled(true);
		checkBox.show();

		if (editDialog != null) {
			labelArea().removeWidget(editDialog);
			editDialog = null;
		}
	}
	
	/**
	 * return the clause currently being edited 
	 * @return
	 */
	public WhereClause getContextClause() {
		if (isSelected()) {
			return  getParentNode().getClause();
		}
		else {
			WhereClause clause = null;
			List<WTreeNode> children = childNodes();
			int i = 0;
			while (clause == null && i < children.size()) {
				clause = ((QueryTreeNode) children.get(i)).getContextClause();
				i++;
			}
			return clause;
		}
	}	
	
	public boolean isSelected() {
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
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		for (WTreeNode child : childNodes()) {
			((QueryTreeNode) child).setEnabled(enabled);
		}
		if (hasButtonPanel()) {
			getButtonPanel().setEnabled(enabled);
		}
		if (enabled) {
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
	}
	
	/**
	 * adds a new node
	 */
	public void addNode() {
		NewWhereClauseTreeNode node = new NewWhereClauseTreeNode(editor);
		this.addChildNode(node);
		node.loadContent();
	}
	
	public void replaceNode(WhereClause newClause) {
		if (getClause() != null) {
			try {
				editor.getQueryEditor().replaceChild(getParentNode().getClause(), getClause(), newClause);
				label().setText(new WMessage(newClause.toString(), true));
				object = newClause;
			} catch (IllegalWhereClauseCompositionException e) {
				e.printStackTrace();
			}
		}
	}
}
