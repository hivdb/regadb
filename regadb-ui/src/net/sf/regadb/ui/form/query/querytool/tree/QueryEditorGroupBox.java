package net.sf.regadb.ui.form.query.querytool.tree;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.pharmadm.custom.rega.queryeditor.Query;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.QueryEditor;
import com.pharmadm.custom.rega.queryeditor.QueryEditorComponent;
import com.pharmadm.custom.rega.queryeditor.SelectionChangeListener;
import com.pharmadm.custom.rega.queryeditor.SelectionListChangeListener;
import com.pharmadm.custom.rega.queryeditor.WhereClause;
import com.pharmadm.custom.rega.savable.DirtinessListener;
import com.pharmadm.custom.rega.savable.Savable;
import com.thoughtworks.xstream.XStream;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.form.query.querytool.QueryToolForm;
import net.sf.regadb.ui.form.query.querytool.buttons.ButtonPanel;
import net.sf.regadb.ui.form.query.querytool.buttons.EditButtonPanel;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WText;

public class QueryEditorGroupBox extends WContainerWidget implements QueryEditorComponent, Savable {
	private QueryEditor editor = null;
	
	private WhereClause contextClause;
	
	private QueryTreeNode queryRootNode;
	private WContainerWidget queryContainer;
	private WText warningText;
	private WPushButton runButton;
	private QueryContext context; 
	private ButtonPanel buttonPanel;
	private boolean editable;
	private boolean validate;

	public QueryEditorGroupBox(QueryToolForm parent, QueryDefinition def) {
		super();
		init(parent);
		initEditor(loadQuery(def));
	}
	
	/**
	 * create a query editor from the given query definition
	 * @param def
	 * @return
	 */
	private QueryEditor loadQuery(QueryDefinition def) {
		if (def.getQuery() == null) {
			return new QueryEditor(new Query(), this);
		}
		else {
	    	XStream xs = new XStream();
	    	return new QueryEditor((Query) xs.fromXML(def.getQuery()), this);
		}
	}
	
	private void initEditor(QueryEditor editor) {
		this.editor = editor;
		
		queryRootNode = new WhereClauseNode(editor.getRootClause(), this);
		queryRootNode.setStyleClass("tree");
		queryContainer.addWidget(queryRootNode);		
		
		editor.addSelectionListChangeListener(new SelectionListChangeListener() {
			public void listChanged() {
				revalidate();
				updateSelection();
			}
		});	
		
		editor.getQuery().getSelectList().addSelectionChangeListener(new SelectionChangeListener() {
			public void selectionChanged() {
				updateStatusBar();
			}
		});
		
		setEditable(true);
		updateStatusBar();
	}
	
	public QueryContext getQueryContext() {
		return context;
	}
	
	public void setValidation(boolean enabled) {
		validate = enabled;
		revalidate();
	}
	
	/**
	 * enable or disable editing of the query
	 * @param enabled
	 */
	public void setEditable(boolean enabled) {
		this.editable = enabled;
		updateControls();
	}
	
	public boolean isEditable() {
		return editable;
	}
	
	/**
	 * update controls to reflect editability
	 */
	public void updateControls() {
		queryRootNode.setEditable(editable);
		if (buttonPanel != null) {
			buttonPanel.setEditable(editable);
		}
	}
	
	/**
	 * update controls to reflect changes in selection
	 */
	public void updateSelection() {
		if (buttonPanel != null) {
			buttonPanel.update();
		}
	}
	
	/**
	 * validate the query and highlight errors
	 */
	public void revalidate() {
		if (validate) {
			queryRootNode.revalidate();
			updateStatusBar();
		}
	}
	
	private void init(final QueryToolForm parent) {
		this.context = parent;
		this.setStyleClass("querytreefield");
		validate = true;
		
		if (parent.isEditable()) {
			buttonPanel = new EditButtonPanel(this);
		}
		if (buttonPanel != null) {
			this.addWidget(buttonPanel);
			buttonPanel.setStyleClass(buttonPanel.styleClass() + " toolbar");
		}
		WContainerWidget panel = new WContainerWidget(this);
		panel.setStyleClass("content");
		
		queryContainer = new WContainerWidget(panel);
		queryContainer.setStyleClass("treeroot");
		
		
		WContainerWidget warnings = new WContainerWidget(panel);
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
	
	private void updateStatusBar() {
		Query query = editor.getQuery();
		
		boolean hasWarning = true;
		
        if (!query.isValid()) {
        	showMessage("form.query.querytool.message.unassigned", "error");
        }
        else if (!query.hasFromVariables()) {
        	showMessage("form.query.querytool.message.noselection", "warning");
        }
        else if (!query.getSelectList().isAnythingSelected()) {
        	showMessage("form.query.querytool.message.emptyselection", "error");
        }
        else {
        	showMessage("form.query.querytool.message.ok", "info");
        	hasWarning = false;
        }
        
        if (!hasWarning) {
        	runButton.enable();
//        	try {
//				warningText.setText(lt(query.getQueryString()));
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        }
        else {
        	runButton.disable();
        }
	}
	
	/**
	 * show the given warning message in the status bar
	 * and assign it the given style class
	 * @param message
	 */
	private void showMessage(String message, String cssClass) {
		warningText.setText(tr(message));
		warningText.setStyleClass(cssClass);
	}

	public QueryEditor getQueryEditor() {
		return editor;
	}

	public void addDirtinessListener(DirtinessListener listener) {
		editor.addDirtinessListener(listener);
	}

	/**
	 * return true if the contained query has unsaved changed
	 */
	public boolean isDirty() {
		return editor.isDirty();
	}
	
	/**
	 * get the root tree item
	 * @return
	 */
	public QueryTreeNode getQueryTree() {
		return queryRootNode;
	}

	public void load(File file) throws IOException {}
	public void save(File file) throws IOException {}

	public WhereClause getContextClause() {
		return contextClause;
	}

	public void setContextClause(WhereClause clause) {
		contextClause = clause;
	}
}


