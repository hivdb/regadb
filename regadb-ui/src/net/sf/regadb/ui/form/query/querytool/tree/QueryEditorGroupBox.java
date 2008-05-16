package net.sf.regadb.ui.form.query.querytool.tree;

import java.io.File;
import java.io.IOException;

import com.pharmadm.custom.rega.queryeditor.Query;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.QueryEditor;
import com.pharmadm.custom.rega.queryeditor.QueryEditorComponent;
import com.pharmadm.custom.rega.queryeditor.SelectionChangeListener;
import com.pharmadm.custom.rega.queryeditor.SelectionListChangeListener;
import com.pharmadm.custom.rega.savable.DirtinessListener;
import com.pharmadm.custom.rega.savable.Savable;

import net.sf.regadb.ui.form.query.querytool.QueryToolForm;
import net.sf.regadb.ui.form.query.querytool.buttons.ButtonPanel;
import net.sf.regadb.ui.form.query.querytool.buttons.EditButtonPanel;
import net.sf.regadb.ui.framework.widgets.messagebox.ConfirmMessageBox;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.i8n.WMessage;

public class QueryEditorGroupBox extends WGroupBox implements QueryEditorComponent, Savable {
	private QueryEditor editor = null;
	
	private QueryTreeNode queryContainer;
	private WContainerWidget queryRoot;
	private WContainerWidget warnings;
	private WText warningText;
	private WPushButton runButton;
	private QueryContext context; 
	private ButtonPanel buttonPanel;
	private boolean enabled;

	public QueryEditorGroupBox(WMessage title, QueryToolForm parent, QueryEditor editor) {
		super(title, parent);
		init(parent);
		prepareEditor((editor==null?newQuery(): editor));
	}
	
	private void prepareEditor(QueryEditor editor) {
		this.editor = editor;
		layoutQuery();
		updateSelection();
		
		editor.addSelectionListChangeListener(new SelectionListChangeListener() {
			public void listChanged() {
				revalidate();
			}
		});	
		
		editor.getQuery().getSelectList().addSelectionChangeListener(new SelectionChangeListener() {
			public void selectionChanged() {
				updateStatus();
			}
		});
		
		editor.addSelectionListChangeListener(new SelectionListChangeListener() {
			public void listChanged() {
				updateStatus();
			}
		});		
		setEnabled(true);
		updateStatus();
	}
	
	public void requestNewQuery() {
		if (editor != null && editor.isDirty()) {
            final ConfirmMessageBox cmb = new ConfirmMessageBox(tr("msg.warning.newquery"));
			cmb.yes.clicked.addListener(new SignalListener<WMouseEvent>() {
				public void notify(WMouseEvent a) {
					cmb.hide();
					prepareEditor(newQuery());
				}
			});
			cmb.no.clicked.addListener(new SignalListener<WMouseEvent>() {
				public void notify(WMouseEvent a) {
					cmb.hide();
				}
			});
		}
		else {
			prepareEditor(newQuery());
		}
	}
	
	private QueryEditor newQuery() {
		QueryEditor editor = null;
		if (editor == null) {
			editor = new QueryEditor(new Query(), this);
		}
		else {
			editor.setQuery(new Query());
		}
		return editor;
	}
	
	public QueryContext getQueryContext() {
		return context;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		updateEnabledState();
	}
	
	public boolean getEnabledState() {
		return enabled;
	}
	
	public void updateEnabledState() {
		queryContainer.setEnabled(enabled);
		if (buttonPanel != null) {
			buttonPanel.setEnabled(enabled);
		}
	}
	
	public void updateSelection() {
		if (buttonPanel != null) {
			buttonPanel.update();
		}
	}
	
	/**
	 * validate the query and highlight errors
	 */
	public void revalidate() {
		queryContainer.revalidate();
	}
	
	private void init(final QueryToolForm parent) {
		this.context = parent;
		this.setStyleClass("querytreefield");
		
		if (parent.isEditable()) {
			buttonPanel = new EditButtonPanel(this);
		}
		if (buttonPanel != null) {
			this.addWidget(buttonPanel);
			buttonPanel.setStyleClass(buttonPanel.styleClass() + " toolbar");
		}
		WContainerWidget panel = new WContainerWidget(this);
		panel.setStyleClass("content");
		
		queryRoot = new WContainerWidget(panel);
		queryRoot.setStyleClass("treeroot");
		
		
		warnings = new WContainerWidget(panel);
		warnings.setStyleClass("warnings");

		WTable table = new WTable(warnings);
		warningText = new WText(tr("form.query.querytool.message.ok"), table.elementAt(0, 0));
		warningText.setStyleClass("warning");

		runButton = new WPushButton(tr("form.query.querytool.pushbutton.run"), table.elementAt(0,1));
		runButton.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				parent.getExecuter().runQuery();
			}
        });  
		table.elementAt(0, 1).setStyleClass("runbutton");
	}
	
	public void updateStatus() {
		Query query = editor.getQuery();
		
		boolean hasWarning = true;
		
        if (!query.isValid()) {
        	showError("form.query.querytool.message.unassigned");
        }
        else if (!query.hasFromVariables()) {
        	showWarning("form.query.querytool.message.noselection");
        }
        else if (!query.getSelectList().isAnythingSelected()) {
        	showError("form.query.querytool.message.emptyselection");
        }
        else {
        	showInfo("form.query.querytool.message.ok");
        	hasWarning = false;
        }
        
        if (!hasWarning) {
        	runButton.enable();
        }
        else {
        	runButton.disable();
        }
	}
	
	private void showWarning(String message) {
		warningText.setText(tr(message));
		warningText.setStyleClass("warning");
	}	
	
	private void showError(String message) {
		warningText.setText(tr(message));
		warningText.setStyleClass("error");
	}	
	
	private void showInfo(String message) {
		warningText.setText(tr(message));
		warningText.setStyleClass("info");
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


