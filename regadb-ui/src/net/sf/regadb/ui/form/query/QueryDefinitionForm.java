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
import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.i8n.WMessage;

public class QueryDefinitionForm extends FormWidget
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
    private Label creatorL;
    private TextField creatorTF;
    
    public QueryDefinitionForm(WMessage formName, InteractionState interactionState, QueryDefinition queryDefinition)
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
        
        if(getInteractionState() == InteractionState.Viewing)
        {
        	creatorL = new Label(tr("form.query.definition.label.creator"));
            creatorTF = new TextField(getInteractionState(), this);
            addLineToTable(queryDefinitionGroupTable, creatorL, creatorTF);
        }
        
        queryDefinitionParameterGroup_ = new WGroupBox(tr("form.query.definition.editableTable.parameters"), this);
    	
    	addControlButtons();
    }
    
    private void fillData()
    {
    	Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
    	
        if(getInteractionState() != InteractionState.Adding)
        {
            nameTF.setText(queryDefinition.getName());
            descriptionTA.setText(queryDefinition.getDescription());
            queryTA.setText(queryDefinition.getQuery());
        }
        
        if(getInteractionState() == InteractionState.Viewing)
        {
            creatorTF.setText(queryDefinition.getSettingsUser().getUid());
        }
        
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
    		redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelected, RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelectedView);
    	}
    }
    
    private boolean validateQuery()
    {
    	ArrayList<WWidget> queryDefinitionParameterNames = queryDefinitionParameterList.getAllWidgets(0);
    	
    	String[] qdpn = new String[queryDefinitionParameterNames.size()];
    	
    	for(int i = 0; i < qdpn.length; i++)
    	{
    		qdpn[i] = ((TextField)queryDefinitionParameterNames.get(i)).getFormText();
    	}
    	
    	Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
    	
    	boolean validQuery = t.validateQuery(queryTA.text());
    	
    	t.commit();
    	
    	if(validQuery)
    	{
    		queryTA.flagValid();
    		
    		Transaction trans = RegaDBMain.getApp().getLogin().createTransaction();
    		
    		String validQueryParameters = t.validateQueryParameters(queryTA.text(), qdpn);
        	
        	trans.commit();
        	
        	if(validQueryParameters == null)
        	{
        		return true;
        	}
        	else
        	{
        		MessageBox.showWarningMessage(tr(validQueryParameters));
        		
        		return false;
        	}
    	}
    	else
    	{
    		queryTA.flagErroneous();
    		
    		return false;
    	}
    }

	@Override
	public void cancel() 
	{
		redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelected, RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelectedView);
	}

	@Override
	public void deleteObject() 
	{
		Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
        
		queryDefinition = RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelected.getSelectedItem();
        
        t.delete(queryDefinition);
        
        t.commit();
	}

	@Override
	public void redirectAfterDelete() 
	{
		RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelected.setSelectedItem(null);
	}
}
