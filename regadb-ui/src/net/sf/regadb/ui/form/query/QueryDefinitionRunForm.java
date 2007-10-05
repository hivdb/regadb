package net.sf.regadb.ui.form.query;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.witty.wt.WAnchor;
import net.sf.witty.wt.WFileResource;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

import org.apache.commons.io.FileUtils;

public class QueryDefinitionRunForm extends FormWidget
{
	private QueryDefinitionRun queryDefinitionRun;
	
    private WGroupBox queryDefinitionRunGroup_;
    private WTable queryDefinitionRunGroupTable;
    
    private QueryDefinitionRunParameterGroupBox queryDefinitionRunParameterGroup;
    
    private Label nameL;
    private TextField nameTF;
    private Label queryNameL;
    private TextField queryNameTF;
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
    private Label resultL;
    private WAnchor resultLink;
    
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
    	
    	nameL = new Label(tr("form.query.definition.run.label.name"));
    	nameTF = new TextField(getInteractionState(), this);
    	nameTF.setMandatory(true);
        addLineToTable(queryDefinitionRunGroupTable, nameL, nameTF);
    	
    	queryNameL = new Label(tr("form.query.definition.run.label.query"));
    	queryNameTF = new TextField(InteractionState.Viewing, this);
        addLineToTable(queryDefinitionRunGroupTable, queryNameL, queryNameTF);
        
        descriptionL = new Label(tr("form.query.definition.run.label.description"));
        descriptionTA = new TextArea(InteractionState.Viewing, this);
        addLineToTable(queryDefinitionRunGroupTable, descriptionL, descriptionTA);
        
        queryL = new Label(tr("form.query.definition.label.query"));
        queryTA = new TextArea(InteractionState.Viewing, this);
        addLineToTable(queryDefinitionRunGroupTable, queryL, queryTA);
        
        if(getInteractionState() == InteractionState.Viewing || getInteractionState() == InteractionState.Deleting)
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
    	
    	nameTF.setText(queryDefinitionRun.getName());
    	queryNameTF.setText(queryDefinitionRun.getQueryDefinition().getName());
        descriptionTA.setText(queryDefinitionRun.getQueryDefinition().getDescription());
        queryTA.setText(queryDefinitionRun.getQueryDefinition().getQuery());
        
        if(getInteractionState() == InteractionState.Viewing || getInteractionState() == InteractionState.Deleting)
        {
        	startDateDF.setDate(queryDefinitionRun.getStartdate());
        	
        	if(queryDefinitionRun.getEnddate() != null)
        	{
        		endDateDF.setDate(queryDefinitionRun.getEnddate());
        	}
        	
        	statusTF.setText(QueryDefinitionRunStatus.getQueryDefinitionRunStatus(queryDefinitionRun).toString());
        	
        	int row = queryDefinitionRunGroupTable.numRows();
            
            resultL = new Label(tr("form.query.definition.run.label.result"));
            queryDefinitionRunGroupTable.putElementAt(row, 0, resultL);
            
            if(queryDefinitionRun.getStatus() != QueryDefinitionRunStatus.Running.getValue())
            {
            	resultLink = new WAnchor((String)null, lt(queryDefinitionRun.getResult()), queryDefinitionRunGroupTable.elementAt(row, 1));
                resultLink.setStyleClass("link");
                resultLink.setRef(new WFileResource("application/excel", RegaDBSettings.getInstance().getPropertyValue("regadb.query.resultDir") + File.separatorChar + queryDefinitionRun.getResult()).generateUrl());
            }
        }
        
        t.commit();
    }
    
    @Override
	public void saveData()
	{
        Map<String, Object> paramObjects = new HashMap<String, Object>();
        
    	if(queryDefinitionRunParameterGroup.saveData(paramObjects))
    	{
    		Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
        	
        	queryDefinitionRun.setName(nameTF.getFormText());
        	queryDefinitionRun.setSettingsUser(t.getSettingsUser(RegaDBMain.getApp().getLogin().getUid()));
        	queryDefinitionRun.setStatus(0);
        	queryDefinitionRun.setStartdate(new Date(System.currentTimeMillis()));
        	
        	queryDefinitionRun.setQueryDefinitionRunParameters(queryDefinitionRunParameterGroup.getQueryDefinitionRunParameters());
    		
        	update(queryDefinitionRun, t);
        	
        	t.commit();
        	
        	QueryThread qt = new QueryThread(RegaDBMain.getApp().getLogin().copyLogin(), queryDefinitionRun, paramObjects);
        	
        	qt.startQueryThread();
        	
        	RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunSelected.setSelectedItem(queryDefinitionRun);
        	
        	redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunMain, RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunMain);
    		redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunSelected, RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunSelectedView);
    	}
    	else
    	{
    		MessageBox.showWarningMessage(tr("form.query.definition.run.validate.parameters.null"));
    	}
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
	public WMessage deleteObject()
	{
		Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
        
		queryDefinitionRun = RegaDBMain.getApp().getTree().getTreeContent().queryDefinitionRunSelected.getSelectedItem();
		
		if(queryDefinitionRun.getStatus() == QueryDefinitionRunStatus.Running.getValue())
		{
			QueryThread.stopQueryThread(queryDefinitionRun.getResult());
		}
        
        t.delete(queryDefinitionRun);
        
        t.commit();
                
        try
        {
			FileUtils.forceDelete(new File(RegaDBSettings.getInstance().getPropertyValue("regadb.query.resultDir") + File.separatorChar + queryDefinitionRun.getResult()));
		}
        catch (IOException e)
        {
			e.printStackTrace();
		}
        
        return null;
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
