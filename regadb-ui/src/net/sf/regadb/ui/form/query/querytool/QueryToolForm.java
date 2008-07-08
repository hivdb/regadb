package net.sf.regadb.ui.form.query.querytool;



import java.io.IOException;

import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.QueryEditorComponent;
import com.pharmadm.custom.rega.savable.Savable;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.form.query.querytool.buttons.EditButtonPanel;
import net.sf.regadb.ui.form.query.querytool.select.SelectionListContainer;
import net.sf.regadb.ui.form.query.querytool.tree.QueryEditorTreeContainer;
import net.sf.regadb.ui.form.query.querytool.tree.QueryStatusBar;
import net.sf.regadb.ui.form.query.querytool.widgets.WTabbedPane;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.i8n.WMessage;

public class QueryToolForm extends FormWidget implements QueryToolApp{

	private WTabbedPane tabs;
	private QueryEditorTreeContainer queryTreeTab;
	private SelectionListContainer selectionTab;
	private InfoContainer infoTab;

	private RunGroupBox runGroup_;
	private QueryStatusBar statusbar;
	
	private Savable queryLoader;
    private QueryDefinition definition;
    
	// is edit mode on
	private boolean editable;
	
	// true when controls are enabled
	private boolean controlsEnabled;
    
	
	public QueryToolForm(WMessage title, InteractionState istate) {
		this(title, istate, new QueryDefinition(StandardObjects.getQueryToolQueryType()));
	}
    
	public QueryToolForm(WMessage title, InteractionState istate, QueryDefinition query) {
		super(title, istate);
		init(query);
	}
	
    public WMessage leaveForm() {
        if(isEditable() && queryTreeTab.getQueryEditor().isDirty()) {
            return tr("form.warning.stillEditing");
        } else {
            return null;
        }
    }	
    
	private void init(QueryDefinition query) {
		setStyleClass("querytoolform");
		definition = query;
		controlsEnabled = true;
		

		queryTreeTab = new QueryEditorTreeContainer(this);
		if (isEditable()) {
			queryTreeTab.setToolbar(new EditButtonPanel(queryTreeTab));
		}
		selectionTab  =	new SelectionListContainer(this);
        infoTab = new InfoContainer(this, this);
        statusbar = new QueryStatusBar(this);

        tabs = new WTabbedPane(this);
        tabs.addTab(tr("form.query.querytool.group.query"), queryTreeTab);
        tabs.addTab(tr("form.query.querytool.group.fields"), selectionTab);
        tabs.addTab(tr("form.query.querytool.group.info"), infoTab);
        tabs.setStatusBar(statusbar);
        
		runGroup_ = new RunGroupBox(queryTreeTab.getQueryEditor(), this);
        
		addControlButtons();
		
		queryLoader = new QueryLoader(this, infoTab, queryTreeTab);
		try {
			queryLoader.load(query);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setQueryEditable(isEditable());
	}
	
	
	public boolean isQueryEditable() {
		return editable;
	}
	
	public void setQueryEditable(boolean enabled) {
		this.editable = enabled;
		updateControls();
		
	}
	

	/**
	 * update controls to reflect editability
	 */
	public void updateControls() {
		boolean editable = isQueryEditable() &&  statusbar.isCatalogLoaded() && getSavable().isLoaded();
		if (editable != controlsEnabled) {
			queryTreeTab.setEditable(editable);
			controlsEnabled = editable;
		}
		queryTreeTab.updateSelection();
		statusbar.update();
	}
	
	public QueryEditorComponent getEditorModel() {
		return queryTreeTab;
	}
	
	public QueryContext getQueryContext() {
		return queryTreeTab;
	}
	
	public Savable getSavable() {
		return queryLoader;
	}	
	
	public void runQuery() {
		runGroup_.runQuery();
	}
	
	
	
	
	
	public void confirmAction() {
		super.confirmAction();
        if(!infoTab.isValid()) {
        	tabs.showTab(infoTab);
        }
	}
	
	public void cancel() {
		if(getInteractionState() == InteractionState.Adding)
		{
			redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryMain, RegaDBMain.getApp().getTree().getTreeContent().queryToolSelect);
		}
		else
		{
			redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryToolSelected, RegaDBMain.getApp().getTree().getTreeContent().queryToolSelectedView);
		}
		
	}

	public WMessage deleteObject() {
		Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
        t.delete(definition);
        t.commit();
        
        return null;
	}

	public void redirectAfterDelete() {
		RegaDBMain.getApp().getTree().getTreeContent().queryToolSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().queryToolSelected.setSelectedItem(null);
	}

	public void saveData() {
		try {
			Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
			queryLoader.save(definition);
	    	definition.setSettingsUser(t.getSettingsUser(RegaDBMain.getApp().getLogin().getUid()));
	    	update(definition, t);
	    	t.commit();
		} catch (IOException e) {}
    	RegaDBMain.getApp().getTree().getTreeContent().queryToolSelected.setSelectedItem(definition);
		redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryToolSelected, RegaDBMain.getApp().getTree().getTreeContent().queryToolSelectedView);
	}
}
