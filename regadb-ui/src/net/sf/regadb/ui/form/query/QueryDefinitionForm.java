package net.sf.regadb.ui.form.query;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.db.QueryDefinitionParameter;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextArea;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WWidget;

public class QueryDefinitionForm extends ObjectForm<QueryDefinition>
{
    private WGroupBox queryDefinitionGroup_;
    private FormTable queryDefinitionGroupTable;
    
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
    
    public QueryDefinitionForm(WString formName, InteractionState interactionState,
    		ObjectTreeNode<QueryDefinition> node, QueryDefinition queryDefinition)
    {
    	super(formName, interactionState, node,
    			queryDefinition == null ? new QueryDefinition(StandardObjects.getHqlQueryQueryType()) : queryDefinition);
        init();
        fillData();
    }
    
    public void init()
    {         
    	queryDefinitionGroup_ = new WGroupBox(tr("form.query.definition.general"), this);
    	
    	queryDefinitionGroupTable = new FormTable(queryDefinitionGroup_);
    	
    	nameL = new Label(tr("form.query.definition.label.name"));
    	nameTF = new TextField(getInteractionState(), this);
        nameTF.setMandatory(true);
        queryDefinitionGroupTable.addLineToTable(nameL, nameTF);
        
        descriptionL = new Label(tr("form.query.definition.label.description"));
        descriptionTA = new TextArea(getInteractionState(), this);
        descriptionTA.setMandatory(true);
        queryDefinitionGroupTable.addLineToTable(descriptionL, descriptionTA);
        
        queryL = new Label(tr("form.query.definition.label.query"));
        queryTA = new TextArea(getInteractionState(), this);
        queryTA.setMandatory(true);
        queryDefinitionGroupTable.addLineToTable(queryL, queryTA);
        
        if(getInteractionState() == InteractionState.Viewing)
        {
        	creatorL = new Label(tr("form.query.definition.label.creator"));
            creatorTF = new TextField(getInteractionState(), this);
            queryDefinitionGroupTable.addLineToTable( creatorL, creatorTF);
        }
        
        queryDefinitionParameterGroup_ = new WGroupBox(tr("form.query.definition.editableTable.parameters"), this);
    	
    	addControlButtons();
    }
    
    private void fillData()
    {
    	Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
    	
        if(getInteractionState() != InteractionState.Adding)
        {
            nameTF.setText(getObject().getName());
            descriptionTA.setText(getObject().getDescription());
            queryTA.setText(getObject().getQuery());
        }
        
        if(getInteractionState() == InteractionState.Viewing)
        {
            creatorTF.setText(getObject().getSettingsUser().getUid());
        }
        
        t.commit();
            
        List<QueryDefinitionParameter> qdpl = new ArrayList<QueryDefinitionParameter>();
        
        for(QueryDefinitionParameter qdp : getObject().getQueryDefinitionParameters())
        {
        	qdpl.add(qdp);
        }
        
        queryDefinitionParameterEditableTable = new IQueryDefinitionParameterEditableTable(this, getObject());
        queryDefinitionParameterList = new EditableTable<QueryDefinitionParameter>(queryDefinitionParameterGroup_, queryDefinitionParameterEditableTable, qdpl);
    }

    @Override
    public void saveData()
    {
    	if (validateQuery())
    	{
    		Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
        	
    		getObject().setName(nameTF.getFormText());
    		getObject().setDescription(descriptionTA.getFormText());
    		getObject().setQuery(queryTA.getFormText());
    		getObject().setSettingsUser(t.getSettingsUser(RegaDBMain.getApp().getLogin().getUid()));
        	
        	queryDefinitionParameterEditableTable.setTransaction(t);
        	queryDefinitionParameterList.saveData();
    		
        	update(getObject(), t);
        	
        	t.commit();
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
    	
    	String[] validQueryParameters = t.validateQuery(queryTA.text());
    	
    	t.commit();
    	
    	if(validQueryParameters != null)
    	{
    		queryTA.flagValid();
    		
    		int validParameters = 0;
    		
    		if(qdpn.length == 0)
    		{
    			if(validQueryParameters.length == 0)
    			{
    				return true;
    			}
    			else
    			{
    				UIUtils.showWarningMessageBox(this, tr("form.query.validate.parameters.null"));
    				
    				return false;
    			}
    		}
    		else
    		{
    			List<String> al = new ArrayList<String>();
    			
    			for (String s : qdpn)
    			{
    				if(!(al.contains(s)))
    				{
    					al.add(s);
    				}
    			}
    			
    			if(al.size() == qdpn.length)
    			{
    				for(String s : qdpn)
    				{
    					for(String str : validQueryParameters)
    					{
    						if(s.equals(str))
    						{
    							validParameters++;
    						}
    					}
    				}
    				
    				if(validParameters == qdpn.length)
    				{
    					return true;
    				}
    				else
    				{
    					UIUtils.showWarningMessageBox(this, tr("form.query.validate.parameters.error"));
    					
    					return false;
    				}
    			}
    			else
    			{
    				UIUtils.showWarningMessageBox(this, tr("form.query.validate.parameters.duplicate"));
    				
    				return false;
    			}
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
	}

	@Override
	public WString deleteObject() 
	{
		Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
        t.delete(getObject());
        t.commit();
        
        return null;
	}
}
