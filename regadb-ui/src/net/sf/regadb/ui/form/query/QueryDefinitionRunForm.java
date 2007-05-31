package net.sf.regadb.ui.form.query;

import java.util.Date;

import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.db.QueryDefinitionRunStatus;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextArea;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

public class QueryDefinitionRunForm extends FormWidget
{
	private QueryDefinitionRun queryDefinitionRun;
	
    private WGroupBox queryDefinitionRunGroup_;
    private WTable queryDefinitionRunGroupTable;
    
    private QueryDefinitionRunParameterGroupBox queryDefinitionRunParameterGroup;
    
    private Label nameL;
    private TextField nameTF;
    private Label descriptionL;
    private TextArea descriptionTA;
    private Label queryL;
    private TextArea queryTA;
    private Label startDateL;
    private DateField startDateDF;
    private Label endDateL;
    private DateField endDateDF;
    private Label statusL;
    private TextField statusTF;
    
    public QueryDefinitionRunForm(WMessage formName, InteractionState interactionState, QueryDefinitionRun queryDefinitionRun)
    {
        super(formName, interactionState);
        
        this.queryDefinitionRun = queryDefinitionRun;
        
        init();
        
        fillData();
        
        addControlButtons();
    }
    
    public void init()
    {
    	if(getInteractionState() == InteractionState.Adding)
        {
        	queryDefinitionRun.setQueryDefinition(RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelected.getSelectedItem());
        }
    	
    	queryDefinitionRunGroup_ = new WGroupBox(tr("form.query.definition.run.general"), this);
    	
    	queryDefinitionRunGroupTable = new WTable(queryDefinitionRunGroup_);
    	
    	nameL = new Label(tr("form.query.definition.label.name"));
    	nameTF = new TextField(InteractionState.Viewing, this);
        addLineToTable(queryDefinitionRunGroupTable, nameL, nameTF);
        
        descriptionL = new Label(tr("form.query.definition.label.description"));
        descriptionTA = new TextArea(InteractionState.Viewing, this);
        addLineToTable(queryDefinitionRunGroupTable, descriptionL, descriptionTA);
        
        queryL = new Label(tr("form.query.definition.label.query"));
        queryTA = new TextArea(InteractionState.Viewing, this);
        addLineToTable(queryDefinitionRunGroupTable, queryL, queryTA);
        
        if(getInteractionState() == InteractionState.Viewing)
        {
        	startDateL = new Label(tr("form.query.definition.run.label.startdate"));
            startDateDF = new DateField(getInteractionState(), this);
            addLineToTable(queryDefinitionRunGroupTable, startDateL, startDateDF);
            
            endDateL = new Label(tr("form.query.definition.run.label.enddate"));
            endDateDF = new DateField(getInteractionState(), this);
            addLineToTable(queryDefinitionRunGroupTable, endDateL, endDateDF);
            
            statusL = new Label(tr("form.query.definition.run.label.status"));
            statusTF = new TextField(getInteractionState(), this);
            addLineToTable(queryDefinitionRunGroupTable, statusL, statusTF);
        }
        
        queryDefinitionRunParameterGroup = new QueryDefinitionRunParameterGroupBox(getInteractionState(), tr("form.query.definition.run.parameters"), this);
    }
    
    private void fillData()
    {
    	Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
    	
    	nameTF.setText(queryDefinitionRun.getQueryDefinition().getName());
        descriptionTA.setText(queryDefinitionRun.getQueryDefinition().getDescription());
        queryTA.setText(queryDefinitionRun.getQueryDefinition().getQuery());
        
        if(getInteractionState() == InteractionState.Viewing)
        {
        	startDateDF.setText(queryDefinitionRun.getStartdate().toString());
        	
        	if(queryDefinitionRun.getEnddate() != null)
        	{
        		endDateDF.setText(queryDefinitionRun.getEnddate().toString());
        	}
        	
        	statusTF.setText(QueryDefinitionRunStatus.getQueryDefinitionRunStatus(queryDefinitionRun).toString());
        }
        
        t.commit();
    }
    
    @Override
	public void saveData()
	{
    	Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
    	
    	queryDefinitionRun.setSettingsUser(t.getSettingsUser(RegaDBMain.getApp().getLogin().getUid()));
    	queryDefinitionRun.setStatus(0);
    	queryDefinitionRun.setStartdate(new Date(System.currentTimeMillis()));
    	
    	queryDefinitionRunParameterGroup.saveData();
    	queryDefinitionRun.setQueryDefinitionRunParameters(queryDefinitionRunParameterGroup.getQueryDefinitionRunParameters());
		
    	update(queryDefinitionRun, t);
    	
    	t.commit();
    	
    	RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunSelected.setSelectedItem(queryDefinitionRun);
    	
    	redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunMain, RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunMain);
		redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunSelected, RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunSelectedView);
	}

	@Override
	public void cancel()
	{
		if(getInteractionState() == InteractionState.Adding)
		{
			redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelected, RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionSelectedView);
		}
		else
		{
			redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunSelected, RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunSelectedView);
		}
	}

	@Override
	public void deleteObject()
	{
		Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
        
		queryDefinitionRun = RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunSelected.getSelectedItem();
        
        t.delete(queryDefinitionRun);
        
        t.commit();
	}

	@Override
	public void redirectAfterDelete()
	{
		RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunSelected.setSelectedItem(null);
	}
	
	public QueryDefinitionRun getQueryDefinitionRun()
	{
		return queryDefinitionRun;
	}
}
