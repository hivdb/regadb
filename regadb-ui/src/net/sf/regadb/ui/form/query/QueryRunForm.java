package net.sf.regadb.ui.form.query;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.db.QueryDefinitionParameter;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextArea;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

public class QueryRunForm extends FormWidget
{
	private QueryDefinition queryDefinition;
	
    private WGroupBox queryDefinitionGroup_;
    private WTable queryDefinitionGroupTable;
    
    private WGroupBox queryDefinitionParameterGroup_;
    private EditableTable<QueryDefinitionParameter> queryDefinitionParameterList;
    private IQueryDefinitionParameterEditableTable queryDefinitionParameterEditableTable;
    
    private Label nameL;
    private TextField nameTF;
    private Label descriptionL;
    private TextArea descriptionTA;
    private Label queryL;
    private TextArea queryTA;
    
    public QueryRunForm(WMessage formName, InteractionState interactionState, QueryDefinition queryDefinition)
    {
        super(formName, interactionState);
        
        this.queryDefinition = queryDefinition;
        
        init();
        
        fillData();
    }
    
    public void init()
    {         
    	queryDefinitionGroup_ = new WGroupBox(tr("form.query.definition.general"), this);
    	
    	queryDefinitionGroupTable = new WTable(queryDefinitionGroup_);
    	
    	nameL = new Label(tr("form.query.definition.label.name"));
    	nameTF = new TextField(getInteractionState(), this);
        nameTF.setMandatory(true);
        addLineToTable(queryDefinitionGroupTable, nameL, nameTF);
        
        descriptionL = new Label(tr("form.query.definition.label.description"));
        descriptionTA = new TextArea(getInteractionState(), this);
        descriptionTA.setMandatory(true);
        addLineToTable(queryDefinitionGroupTable, descriptionL, descriptionTA);
        
        queryL = new Label(tr("form.query.definition.label.query"));
        queryTA = new TextArea(getInteractionState(), this);
        queryTA.setMandatory(true);
        addLineToTable(queryDefinitionGroupTable, queryL, queryTA);
        
        queryDefinitionParameterGroup_ = new WGroupBox(tr("form.query.definition.editableTable.parameters"), this);
    }
    
    private void fillData()
    {
    	Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
    	
    	nameTF.setText(queryDefinition.getName());
        descriptionTA.setText(queryDefinition.getDescription());
        queryTA.setText(queryDefinition.getQuery());
        
        t.commit();
            
        List<QueryDefinitionParameter> qdpl = new ArrayList<QueryDefinitionParameter>();
        
        for(QueryDefinitionParameter qdp : queryDefinition.getQueryDefinitionParameters())
        {
        	qdpl.add(qdp);
        }
        
        queryDefinitionParameterEditableTable = new IQueryDefinitionParameterEditableTable(this, queryDefinition);
        queryDefinitionParameterList = new EditableTable<QueryDefinitionParameter>(queryDefinitionParameterGroup_, queryDefinitionParameterEditableTable, qdpl);
    }

    @Override
    public void saveData()
    {
    	if (validateQuery())
    	{
    		Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
        	
        	queryDefinition.setName(nameTF.getFormText());
        	queryDefinition.setDescription(descriptionTA.getFormText());
        	queryDefinition.setQuery(queryTA.getFormText());
        	queryDefinition.setSettingsUser(t.getSettingsUser(RegaDBMain.getApp().getLogin().getUid()));
        	
        	queryDefinitionParameterEditableTable.setTransaction(t);
        	queryDefinitionParameterList.saveData();
        	
        	update(queryDefinition, t);
        	
        	t.commit();
        	
        	RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelected.setSelectedItem(queryDefinition);
            RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelected.expand();
            RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelected.refreshAllChildren();
            RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelectedView.selectNode();
    	}
    }
    
    private boolean validateQuery()
    {
    	boolean valid;
    	
    	if (true)
    	{
    		queryTA.flagValid();
    		
    		valid = true;
    	}
    	else
    	{
    		queryTA.flagErroneous();
    		
    		valid = false;
    	}
    	
    	return valid;
    }

	@Override
	public void cancel() {
		RegaDBMain.getApp().getTree().getTreeContent().queryRunSelected.expand();
		RegaDBMain.getApp().getTree().getTreeContent().queryRunSelected.refreshAllChildren();
		RegaDBMain.getApp().getTree().getTreeContent().queryRunSelectedView.selectNode();
	}

	@Override
	public void deleteObject() {
		//
	}

	@Override
	public void redirectAfterDelete() {
		
	}
}
