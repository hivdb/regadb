package net.sf.regadb.ui.form.query.querytool.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.regadb.ui.form.query.querytool.QueryToolApp;
import net.sf.regadb.ui.form.query.querytool.widgets.CssClasses;
import net.sf.regadb.ui.form.query.querytool.widgets.MyDialog;
import net.sf.regadb.ui.form.query.querytool.widgets.WButtonPanel;
import net.sf.regadb.ui.framework.widgets.UIUtils;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.IllegalWhereClauseCompositionException;
import com.pharmadm.custom.rega.queryeditor.WhereClause;
import com.pharmadm.custom.rega.queryeditor.UniqueNameContext.AssignMode;
import com.pharmadm.custom.rega.queryeditor.catalog.DbObject;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WTreeNode;

public abstract class QueryTreeNode extends WTreeNode {
	private WhereClause object;
	private QueryToolApp mainForm;
	private WCheckBox checkBox;
	private WButtonPanel buttonPanel;
	private MyDialog editDialog;
	private WTable contentTable;
	private String lastAddition;
	
	private CssClasses styleClasses;
	
	public QueryTreeNode(WhereClause clause, QueryToolApp editor, QueryTreeNode parent) {
		super(lt(""), null, parent);
		setImagePack("pics/");
		this.object = clause;
		this.mainForm = editor;
		styleClasses = new CssClasses(this.labelArea());
		init();
	}
	
	public QueryToolApp getQueryApp() {
		return mainForm;
	}
	
	public CssClasses getStyleClasses() {
		return styleClasses;
	}
	
	protected void doCollapse() {
		if (mainForm.isQueryEditable()) {
			super.doCollapse();
		}
	}
	
	protected void undoDoExpand() {
		if (mainForm.isQueryEditable()) {
			super.undoDoExpand();
		}
	}
	
	private void init() {
		this.setChildCountPolicy(ChildCountPolicy.Disabled);
		
		labelArea().removeWidget(label());
		
		createContentTable();
		
		if (getClause() != null) {
			Iterator<WhereClause> it = getClause().iterateChildren();
			while (it.hasNext()) {
				WTreeNode node = new WhereClauseNode(it.next(), mainForm);
				addChildNode(node);
			}		
		}
		expand();
		
	}
	
	protected void createContentTable() {
		if (getClause() != null) {
			label().setText(lt(getClause().toString()));		
		}	
		
		if (contentTable != null) {
			contentTable.elementAt(0, 0).removeWidget(label());
			contentTable.elementAt(0, 2).removeWidget(checkBox);
			if (hasButtonPanel()) {
				contentTable.elementAt(0, 1).removeWidget(buttonPanel);
				buttonPanel = null;
			}
			labelArea().removeWidget(contentTable);
		}
		
		contentTable = new WTable(labelArea());
		contentTable.elementAt(0, 0).addWidget(label());
		contentTable.setStyleClass("treenodecontent");
		contentTable.elementAt(0, 0).setStyleClass("labelcontent");
		
		if (checkBox == null) {
			checkBox = new WCheckBox();
			checkBox.setStyleClass("check");
		}
		contentTable.elementAt(0, 2).addWidget(checkBox);
		contentTable.elementAt(0, 2).setStyleClass("checkbox");
		
		checkBox.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				setSelected(checkBox.isChecked());
			}
		});
		
		contentTable.elementAt(0, 0).clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
					setSelected(!checkBox.isChecked());
			}
		});
	}
	
	public void setSelected(boolean checked) {
		if (object == null || object.getParent() == null || !mainForm.isQueryEditable()) {
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
		mainForm.updateControls();
	}	
	
	public WhereClause getClause() {
		return object;
	}
	
	public MyDialog getDialog() {
		return editDialog;
	}
	
	public void setButtonPanel(WButtonPanel panel) {
		if (hasButtonPanel()) {
			contentTable.elementAt(0, 1).removeWidget(buttonPanel);
		}
		contentTable.elementAt(0, 1).addWidget(panel);
		buttonPanel = panel;
	}
	
	/**
	 * check if the query is still valid
	 */
	public void revalidate() {
		if (object != null && object.isAtomic() && !UIUtils.keyOrValue(label().text()).equals(object.toString())) {
			label().setText(lt(object.toString()));
		}
		if (object != null && !object.isValid()) {
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
	public void showDialog(MyDialog dialog) {
		hideContent();
		hideDialog();
		getParentNode().expand();
		
		mainForm.getQueryContext().setContextClause(getParentNode().getClause());
		editDialog = dialog;
		labelArea().addWidget(dialog);
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
		mainForm.setQueryEditable(false);
		contentTable.setStyleClass("disabledtreenodecontent");
	}
	
	/**
	 * hide the dialog
	 * shows the regular content
	 */
	public void showContent() {
		contentTable.setStyleClass("treenodecontent");
		mainForm.setQueryEditable(true);
		hideDialog();
		mainForm.getQueryContext().setContextClause(null);
	}
	
	/**
	 * returns true if this node has a dialog open
	 * @return
	 */
	private boolean hasDialog() {
		return editDialog != null;
	}
	
	private boolean hasButtonPanel() {
		return buttonPanel != null;
	}
	
	public WButtonPanel getButtonPanel() {
		return buttonPanel;
	}
	
	/** 
	 * return the parent of this node
	 * @return
	 */
	public QueryTreeNode getParentNode() {
		return (QueryTreeNode) parentNode();
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
			getButtonPanel().setEnabled(editable);
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
	public QueryTreeNode addNode(WhereClause clause, AssignMode mode) {
		QueryTreeNode node = null;
		if (getClause() != null && getClause().acceptsAdditionalChild()) {
			try {
				mainForm.getEditorModel().getQueryEditor().addChild(getClause(), clause, mode);
				node = new WhereClauseNode(clause, mainForm);
				addChildNode(node);
				if (clause.isAtomic()) {
					if (!((AtomicWhereClause) clause).getOutputVariables().isEmpty()) {
						setLastAddition(((AtomicWhereClause) clause).getOutputVariables().get(0).getObject());
					}
				}
				
				// update local enabled state
				this.setEditable(mainForm.isQueryEditable());
			} catch (IllegalWhereClauseCompositionException e) {
				e.printStackTrace();
			}
		}
		return node;
	}
	
	/**
	 * remove the given node from this node or its children
	 * @param node
	 */
	public void removeNode(QueryTreeNode node) {
		if (childNodes().contains(node)) {
			WhereClause clause = node.getClause();
			this.removeChildNode(node);
			mainForm.getEditorModel().getQueryEditor().removeChild(getClause(), clause);

			// we only need to update the enabled state for the element that contained the node to be removed
			this.setEditable(mainForm.isQueryEditable());
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
			mainForm.getEditorModel().getQueryEditor().removeChild(getClause(), clause);
		}

		// we only need to update the enabled state for the element that contained the node to be removed
		this.setEditable(mainForm.isQueryEditable());
	}
	
	/**
	 * open a select simple clause dialog in this node
	 */
	public void selectNewNode() {
		NewWhereClauseTreeNode node = new NewWhereClauseTreeNode(mainForm, this);
		node.loadContent(getLastAddition());
	}
	
	
	/**
	 * removing a child from a treenode causes witty errors
	 * if other updates happen in the tree during the same refresh
	 * cause most likely somewhere in updateChildren
	 * this is a workaround that removes the child without calling
	 * updateChildren. 
	 * However, it makes somes serious assumptions about the internal
	 * state of the treenode and should bve used with caution
	 * @param node
	 */
	public void removeChildNodeBugFix(WTreeNode node)
	{
		childNodes().remove(node);
		((WContainerWidget) node.parent()).removeWidget(node);
	}	
	
	/**
	 * replace the clause in the current node by the given clause
	 * @param newClause
	 */
	public QueryTreeNode replaceNode(WhereClause newClause) {
		if (getClause() != null) {
			try {
				WhereClause oldClause = getClause();
				if (!oldClause.isAtomic()) {
					while (!this.childNodes().isEmpty()) {
						this.removeChildNodeBugFix(this.childNodes().get(0));
					}
				}
				object = newClause;
				mainForm.getEditorModel().getQueryEditor().replaceChild(oldClause.getParent(), oldClause, newClause);
				createContentTable();
				
				if (newClause.isAtomic()) {
					if (!((AtomicWhereClause) newClause).getOutputVariables().isEmpty()) {
						setLastAddition(((AtomicWhereClause) newClause).getOutputVariables().get(0).getObject());
					}
				}
				// update local enabled state
				this.setEditable(mainForm.isQueryEditable());
			} catch (IllegalWhereClauseCompositionException e) {
				e.printStackTrace();
			}
		}
		return this;
	}
	
	private void setLastAddition(DbObject object) {
		if (object.isPrimitive() || object.isField()) {
			lastAddition = object.getValueType().toString();
		}
		else {
			lastAddition = object.getDescription();
		}
	}
	
	private String getLastAddition() {
		if (lastAddition == null && getParentNode() != null) {
			return getParentNode().getLastAddition();
		}
		else {
			return lastAddition;
		}
	}
}
