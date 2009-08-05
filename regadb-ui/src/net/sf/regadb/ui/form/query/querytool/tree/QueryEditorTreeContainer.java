package net.sf.regadb.ui.form.query.querytool.tree;

import java.util.Collections;
import java.util.List;

import net.sf.regadb.ui.form.query.querytool.QueryToolApp;
import net.sf.regadb.ui.form.query.querytool.widgets.ToolbarHolder;
import net.sf.regadb.ui.form.query.querytool.widgets.WButtonPanel;

import com.pharmadm.custom.rega.queryeditor.Query;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.QueryEditor;
import com.pharmadm.custom.rega.queryeditor.QueryEditorComponent;
import com.pharmadm.custom.rega.queryeditor.SelectionChangeListener;
import com.pharmadm.custom.rega.queryeditor.SelectionListChangeListener;
import com.pharmadm.custom.rega.queryeditor.WhereClause;

import eu.webtoolkit.jwt.WContainerWidget;

public class QueryEditorTreeContainer extends WContainerWidget implements QueryEditorComponent, QueryContext, ToolbarHolder {
	private QueryEditor editor = null;
	private WhereClause contextClause;
	private QueryToolApp mainForm; 
	
	private QueryTreeNode queryRootNode;
	private WContainerWidget queryContainer;
	
	private WContainerWidget toolbarContainer;
	private WButtonPanel buttonPanel;
	
	private boolean validate;

	public QueryEditorTreeContainer(QueryToolApp parent) {
		super();
		init(parent);
	}
	
	public void setQuery(Query query) {
		if (queryRootNode != null) {
			queryContainer.removeWidget(queryRootNode);
		}
		editor.setQuery(query);
		
		queryRootNode = new WhereClauseNode(editor.getRootClause(), mainForm);
		queryRootNode.setStyleClass("tree");
		queryContainer.addWidget(queryRootNode);
		
		editor.addSelectionListChangeListener(new SelectionListChangeListener() {
			public void listChanged() {
				validate();
				mainForm.updateControls();
			}
		});	
		
		mainForm.getEditorModel().getQueryEditor().getQuery().getSelectList().addSelectionChangeListener(new SelectionChangeListener() {
			public void selectionChanged() {
				mainForm.updateControls();
			}
		});		
	}
	
	
	public void setValidation(boolean enabled) {
		validate = enabled;
		validate();
	}
	
	public List<QueryTreeNode> getSelection() {
		if (queryRootNode != null) {
			return queryRootNode.getSelection();
		}
		return Collections.emptyList();
	}
	
	/**
	 * update controls to reflect changes in selection
	 */
	public void updateSelection() {
		if (buttonPanel != null) {
			buttonPanel.update();
		}
	}
	
	public void setEditable(boolean editable) {
		if (getRootNode() != null) {
			getRootNode().setEditable(editable);
		}
		else {
			editable = false;
		}
		if (buttonPanel != null) {
			buttonPanel.setEnabled(editable);
		}
	}
	
	/**
	 * validate the query and highlight errors
	 */
	private void validate() {
		if (validate) {
			queryRootNode.revalidate();
		}
	}
	
	public void setToolbar(WButtonPanel panel) {
		buttonPanel = panel;
		if (buttonPanel != null) {
			toolbarContainer.addWidget(buttonPanel);
			buttonPanel.getStyleClasses().addStyle("toolbar");
		}
	}
	
	private void init(final QueryToolApp parent) {
		this.mainForm = parent;
		this.setStyleClass("querytreefield");
		validate = true;
		
		toolbarContainer = new WContainerWidget(this);
		WContainerWidget panel = new WContainerWidget(this);
		panel.setStyleClass("content");
		
		queryContainer = new WContainerWidget(panel);
		queryContainer.setStyleClass("treeroot");
		editor = new QueryEditor(new Query(), parent.getSavable());
	}
	
	public QueryEditor getQueryEditor() {
		return editor;
	}

	/**
	 * get the root tree item
	 * @return
	 */
	public QueryTreeNode getRootNode() {
		return queryRootNode;
	}

	public WhereClause getContextClause() {
		return contextClause;
	}

	public void setContextClause(WhereClause clause) {
		contextClause = clause;
	}

	public QueryEditorComponent getEditorModel() {
		return this;
	}
}


