package net.sf.regadb.ui.form.query.querytool;



import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.QueryEditorComponent;
import com.pharmadm.custom.rega.queryeditor.WhereClause;
import com.thoughtworks.xstream.XStream;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.form.query.querytool.select.SelectionGroupBox;
import net.sf.regadb.ui.form.query.querytool.tree.QueryEditorGroupBox;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.i8n.WMessage;

public class QueryToolForm extends FormWidget implements QueryContext{

	private QueryEditorGroupBox queryGroup_;
	private RunGroupBox runGroup_;
	private InfoContainer infoContainer;
	private WTabbedPane tabs;
	
    
    private QueryDefinition definition;
	
	public QueryToolForm(WMessage title, InteractionState istate) {
		super(title, istate);
		QueryDefinition query = new QueryDefinition(StandardObjects.getQueryToolQueryType());
		init(query);
	}
    
    
	public QueryToolForm(WMessage title, InteractionState istate, QueryDefinition query) {
		super(title, istate);
		init(query);
	}
	
    public WMessage leaveForm() {
        if(isEditable() && queryGroup_.getQueryEditor().isDirty()) {
            return tr("form.warning.stillEditing");
        } else {
            return null;
        }
    }	
    
    public RunGroupBox getExecuter() {
    	return runGroup_;
    }
    
	public void init(QueryDefinition query) {
		setStyleClass("querytoolform");
		definition = query;
		
		infoContainer = new InfoContainer(query, this);
		queryGroup_ = new QueryEditorGroupBox(this, query);
		
        tabs = new WTabbedPane();
        tabs.setParent(this);
        tabs.addTab(tr("form.query.querytool.group.query"), queryGroup_);
        tabs.addTab(tr("form.query.querytool.group.fields"), new SelectionGroupBox(queryGroup_.getQueryEditor()));
        tabs.addTab(tr("form.query.querytool.group.info"), infoContainer);
        
		runGroup_ = new RunGroupBox(queryGroup_.getQueryEditor(), this);
        
        
		addControlButtons();
		if (!isEditable()) {
			queryGroup_.setEditable(false);
		}
	}
	
	public void confirmAction() {
		super.confirmAction();
        if(!infoContainer.isValid()) {
        	tabs.showTab(infoContainer);
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
		Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
    	
    	definition.setName(infoContainer.getName());
    	definition.setDescription(infoContainer.getDescription());
    	definition.setQuery(new XStream().toXML(this.getEditorModel().getQueryEditor().getQuery()));
    	definition.setSettingsUser(t.getSettingsUser(RegaDBMain.getApp().getLogin().getUid()));
    	
    	update(definition, t);
    	
    	t.commit();
    	
    	RegaDBMain.getApp().getTree().getTreeContent().queryToolSelected.setSelectedItem(definition);
		redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryToolSelected, RegaDBMain.getApp().getTree().getTreeContent().queryToolSelectedView);
	}

	public WhereClause getContextClause() {
		return queryGroup_.getContextClause();
	}

	public QueryEditorComponent getEditorModel() {
		return queryGroup_;
	}
}
