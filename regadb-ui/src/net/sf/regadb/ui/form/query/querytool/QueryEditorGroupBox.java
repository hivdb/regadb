package net.sf.regadb.ui.form.query.querytool;

import java.io.File;
import java.io.IOException;

import com.pharmadm.custom.rega.queryeditor.Query;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.QueryEditor;
import com.pharmadm.custom.rega.queryeditor.QueryEditorComponent;
import com.pharmadm.custom.rega.savable.DirtinessListener;
import com.pharmadm.custom.rega.savable.Savable;

import net.sf.regadb.ui.form.query.querytool.buttons.EditButtonPanel;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.i8n.WMessage;

public class QueryEditorGroupBox extends WGroupBox implements QueryEditorComponent, Savable {
	private QueryEditor editor;
	
	private QueryTreeNode queryContainer;
	private WContainerWidget queryRoot;
	private QueryContext context; 
	private EditButtonPanel buttonPanel;
	
	public QueryEditorGroupBox(Query query, WMessage title,  QueryToolForm parent) {
		super(title, parent);
		this.context = parent;
		this.editor = new QueryEditor(query, this);
		init();
	}
	
	public QueryContext getQueryContext() {
		return context;
	}
	
	public void setEnabled(boolean enabled) {
		queryContainer.setEnabled(enabled);
		buttonPanel.setEnabled(enabled);
	}
	
	public void updateSelection() {
		buttonPanel.updateSelection();
	}
	

	
	
	
	private void init() {
		this.setStyleClass("querytreefield");
		buttonPanel = new EditButtonPanel(this);
		this.addWidget(buttonPanel);
		buttonPanel.setStyleClass(buttonPanel.styleClass() + " toolbar");

		queryRoot = new WContainerWidget(this);
		queryRoot.setStyleClass("content");

		layoutQuery();
		updateSelection();
	}
	
	private void layoutQuery() {
		if (queryContainer != null) {
			queryRoot.removeWidget(queryContainer);
		}
		queryContainer = new WhereClauseNode(editor.getRootClause(), this);
		queryContainer.setStyleClass("tree");
		queryRoot.addWidget(queryContainer);
	}

	public QueryEditor getQueryEditor() {
		return editor;
	}

	public void addDirtinessListener(DirtinessListener listener) {
		editor.addDirtinessListener(listener);
	}

	public boolean isDirty() {
		return editor.isDirty();
	}
	
	public QueryTreeNode getQueryTree() {
		return queryContainer;
	}

	public void load(File file) throws IOException {}
	public void save(File file) throws IOException {}
}


