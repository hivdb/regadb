package net.sf.regadb.ui.form.query.querytool.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.IllegalWhereClauseCompositionException;
import com.pharmadm.custom.rega.queryeditor.WhereClause;

import net.sf.regadb.ui.form.query.querytool.buttons.ButtonPanel;
import net.sf.regadb.ui.form.query.querytool.dialog.WDialog;
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
	private boolean isContext;
	private boolean haltPropagate;
	
	private HashSet<String> styleClasses;
	
	public QueryTreeNode(WhereClause clause, QueryEditorGroupBox editor) {
		super(new WMessage("", true));
		if (clause != null) {
			label().setText(new WMessage(clause.toString(), true));		
		}
		setImagePack("pics/");
		this.object = clause;
		this.editor = editor;
		styleClasses = new HashSet<String>();
		init();
	}
	
	public void addStyle(String style) {
		styleClasses.add(style);
		updateStyle();
	}
	
	public void removeStyle(String style) {
		styleClasses.remove(style);
		updateStyle();
	}
	
	private void updateStyle() {
		String classes = "";
		for (String st : styleClasses) {
			classes += st + " ";
		}
		labelArea().setStyleClass(classes.trim());
	}
	
	private void init() {
		checkBox = new WCheckBox();
		checkBox.setStyleClass("check");
		labelArea().addWidget(checkBox);
		
		checkBox.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				haltPropagate = true;
				setChecked(checkBox.isChecked());
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
		
		
		labelArea().clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				if (!haltPropagate) {
					setChecked(!checkBox.isChecked());
				}
				haltPropagate = false;
			}
		});
	}
	
	private void setChecked(boolean checked) {
		// can't select root node
		if (object == null || hasDialog() || object.getParent() == null) {
			return;
		}
		
		if (checkBox.isChecked() != checked) {
			checkBox.setChecked(checked);
		}
		if (checked) {
			addStyle("selectedclause");
		}
		else {
			removeStyle("selectedclause");
		}
		editor.updateSelection();
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
		buttonPanel.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				System.err.println("button");
			}
		});
	}
	
	/**
	 * check if the query is still valid
	 */
	public void revalidate() {
		if (object != null && !object.isValid()) {
			addStyle("invalidclause");
		}
		else {
			removeStyle("invalidclause");
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
		hideContent();
		
		isContext = true;
		editDialog = dialog;
		labelArea().addWidget(dialog);
		editDialog.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				haltPropagate = true;
			}
		});
	}
	
	
	/**
	 * hide the current dialog
	 */
	private void hideDialog() {
		if (editDialog != null) {
			labelArea().removeWidget(editDialog);
		}
		editDialog = null;
	}
	
	private void hideContent() {
		editor.setEnabled(false);
		if (hasButtonPanel()) getButtonPanel().hide();
		label().hide();
		checkBox.hide();
	}
	
	/**
	 * hide the dialog
	 * shows the regular content
	 */
	public void showContent() {
		hideDialog();
		
		label().show();
		if (hasButtonPanel()) getButtonPanel().show();
		editor.setEnabled(true);
		checkBox.show();
		isContext = false;
	}
	
	/**
	 * mark this clause as the context clause
	 */
	public void markAsContext() {
		isContext = true;
	}
	
	/**
	 * finds the node that has a dialog open 
	 * in this node or its children
	 * @return
	 */
	public WhereClause getContextClause() {
		if (isContext) {
			System.err.println("context requested");
			if (getClause() != null) {
				System.err.println(getClause());
				if (getClause() instanceof AtomicWhereClause) {
					System.err.println(((AtomicWhereClause) getClause()).getVisualizationClauseList().toString());
				}
			}
			System.err.println("p:" + getParentNode().getClause());
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
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		for (WTreeNode child : childNodes()) {
			((QueryTreeNode) child).setEnabled(enabled);
		}
		if (hasButtonPanel()) {
			getButtonPanel().setEnabled(enabled);
		}
		if (enabled && getParentNode() != null) {
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
				this.setEnabled(editor.getEnabledState());
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
			this.setEnabled(editor.getEnabledState());
		}
		else {
			for (WTreeNode childNode : childNodes()) {
				((QueryTreeNode) childNode).removeNode(node);
			}
		}
		editor.updateSelection();
		
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
		editor.updateSelection();

		// we only need to update the enabled state for the element that contained the node to be removed
		this.setEnabled(editor.getEnabledState());
	}
	
	/**
	 * open a select simple clause dialog in this node
	 */
	public void selectNewNode() {
		NewWhereClauseTreeNode node = new NewWhereClauseTreeNode(editor);
		this.addChildNode(node);
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
				this.setEnabled(editor.getEnabledState());
				
				editor.revalidate();
				
			} catch (IllegalWhereClauseCompositionException e) {
				e.printStackTrace();
			}
		}
	}
}
