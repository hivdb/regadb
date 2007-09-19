package net.sf.regadb.ui.form.query;

import java.util.List;
import java.util.Set;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.QueryDefinitionParameter;
import net.sf.regadb.db.QueryDefinitionParameterTypes;
import net.sf.regadb.db.QueryDefinitionRun;
import net.sf.regadb.db.QueryDefinitionRunParameter;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.i8n.WMessage;

public class QueryDefinitionRunParameterGroupBox extends WGroupBox
{
	private InteractionState interactionState;
	
	private QueryDefinitionRunForm queryDefinitionRunForm;
	private QueryDefinitionRun queryDefinitionRun;
	
	private Set<QueryDefinitionRunParameter> qdrps;
	
	private WTable parameterTable;
    
    public QueryDefinitionRunParameterGroupBox(InteractionState interactionState, WMessage message, QueryDefinitionRunForm queryDefinitionRunForm)
    {
    	super(message, queryDefinitionRunForm);
        
    	this.interactionState = interactionState;
        this.queryDefinitionRunForm = queryDefinitionRunForm;
        this.queryDefinitionRun = queryDefinitionRunForm.getQueryDefinitionRun();
        
        init();
    }
    
    public void init()
    {
    	qdrps = queryDefinitionRun.getQueryDefinitionRunParameters();
    	
    	parameterTable = new WTable(this);
    	parameterTable.setStyleClass("datatable");
    	
    	TextField parameterL = new TextField(InteractionState.Viewing, queryDefinitionRunForm);
    	parameterL.setText("Parameter");
    	parameterL.setStyleClass("table-header");
    	
    	TextField valueL = new TextField(InteractionState.Viewing, queryDefinitionRunForm);
    	valueL.setText("Value");
    	valueL.setStyleClass("table-header");
    	
    	parameterTable.putElementAt(0, 0, parameterL);
    	parameterTable.putElementAt(0, 1, valueL);
    	
    	if(interactionState == InteractionState.Adding)
    	{
    		Set<QueryDefinitionParameter> qdps = queryDefinitionRun.getQueryDefinition().getQueryDefinitionParameters();
    		
    		for(QueryDefinitionParameter qdp : qdps)
    		{
    			QueryDefinitionRunParameter qdrp = new QueryDefinitionRunParameter();
    			qdrp.setQueryDefinitionParameter(qdp);
    			
    			qdrps.add(qdrp);
    		}
    	}
    	
    	int i = 1;
    	
    	for(QueryDefinitionRunParameter qdrp : qdrps)
    	{
    		TextField qdpL = new TextField(InteractionState.Viewing, queryDefinitionRunForm);
    		qdpL.setText(qdrp.getQueryDefinitionParameter().getName());
    		
    		WWidget w = new TextField(interactionState, queryDefinitionRunForm);
    		Transaction t;
    		
    		QueryDefinitionParameterTypes type = QueryDefinitionParameterTypes.getQueryDefinitionParameterType(qdrp.getQueryDefinitionParameter().getQueryDefinitionParameterType());
    		
    		switch (type) 
        	{
		        case STRING:
		        	w = new TextField(interactionState, queryDefinitionRunForm);
		        	
					((TextField)w).setText(qdrp.getValue());
		        break;
		        case INTEGER:
		        	w = new TextField(interactionState, queryDefinitionRunForm);
		        	
		        	((TextField)w).setText(qdrp.getValue());
		        break;
		        case DOUBLE:
		        	w = new TextField(interactionState, queryDefinitionRunForm);
		        	
					((TextField)w).setText(qdrp.getValue());
		        break;
		        case DATE:
		        	w = new DateField(interactionState, queryDefinitionRunForm);

		        	((DateField)w).setText(qdrp.getValue());
		        break;
		        case GENERICDRUG:
		        	w = new ComboBox(interactionState, queryDefinitionRunForm);
		        	
		        	t = RegaDBMain.getApp().getLogin().createTransaction();
        			
        			List<DrugGeneric> genericDrugs = t.getGenericDrugs();
        			
        			for(DrugGeneric dg: genericDrugs)
			        {
			            ((ComboBox)w).addItem(new DataComboMessage<DrugGeneric>(dg, dg.getGenericName()));
			        }
                    ((ComboBox)w).sort();
			        
			        t.commit();
			        
			        if(interactionState != InteractionState.Adding)
			        {
			        	((ComboBox)w).selectItem(qdrp.getValue());
			        }
		        break;
		        case COMMERCIALDRUG:
		        	w = new ComboBox(interactionState, queryDefinitionRunForm);
		        	
		        	t = RegaDBMain.getApp().getLogin().createTransaction();
        			
        			List<DrugCommercial> commercialDrugs = t.getCommercialDrugs();
        			
        			for(DrugCommercial dc: commercialDrugs)
			        {
			            ((ComboBox)w).addItem(new DataComboMessage<DrugCommercial>(dc, dc.getName()));
			        }
                    ((ComboBox)w).sort();
                    
			        t.commit();
			        
			        if(interactionState != InteractionState.Adding)
			        {
			        	((ComboBox)w).selectItem(qdrp.getValue());
			        }
		        break;
		        case TEST:
		        	w = new ComboBox(interactionState, queryDefinitionRunForm);
		        	
		        	t = RegaDBMain.getApp().getLogin().createTransaction();
        			
        			List<Test> tests = t.getTests();
        			
        			for(Test test: tests)
			        {
			            ((ComboBox)w).addItem(new DataComboMessage<Test>(test, test.getDescription()));
			        }
                    ((ComboBox)w).sort();
			        
			        t.commit();
			        
			        if(interactionState != InteractionState.Adding)
			        {
			        	((ComboBox)w).selectItem(qdrp.getValue());
			        }
		        break;
		        case TESTTYPE:
		        	w = new ComboBox(interactionState, queryDefinitionRunForm);
		        	
		        	t = RegaDBMain.getApp().getLogin().createTransaction();
        			
        			List<TestType> testTypes = t.getTestTypes();
        			
        			for(TestType tt: testTypes)
			        {
			            ((ComboBox)w).addItem(new DataComboMessage<TestType>(tt, tt.getDescription()));
			        }
                    ((ComboBox)w).sort();
                    
			        t.commit();
			        
			        if(interactionState != InteractionState.Adding)
			        {
			        	((ComboBox)w).selectItem(qdrp.getValue());
			        }
		        break;
		        case PROTEIN:
		        	w = new ComboBox(interactionState, queryDefinitionRunForm);
		        	
		        	t = RegaDBMain.getApp().getLogin().createTransaction();
        			
        			List<Protein> proteins = t.getProteins();
        			
        			for(Protein p: proteins)
			        {
			            ((ComboBox)w).addItem(new DataComboMessage<Protein>(p, p.getFullName()));
			        }
                    ((ComboBox)w).sort();
			        
			        t.commit();
			        
			        if(interactionState != InteractionState.Adding)
			        {
			        	((ComboBox)w).selectItem(qdrp.getValue());
			        }
		        break;
		        case ATTRIBUTE:
		        	w = new ComboBox(interactionState, queryDefinitionRunForm);
		        	
		        	t = RegaDBMain.getApp().getLogin().createTransaction();
        			
        			List<Attribute> attributes = t.getAttributes();
        			
        			for(Attribute a: attributes)
			        {
			            ((ComboBox)w).addItem(new DataComboMessage<Attribute>(a, a.getName()));
			        }
                    ((ComboBox)w).sort();
			        
			        t.commit();
			        
			        if(interactionState != InteractionState.Adding)
			        {
			        	((ComboBox)w).selectItem(qdrp.getValue());
			        }
		        break;
		        case ATTRIBUTEGROUP:
		        	w = new ComboBox(interactionState, queryDefinitionRunForm);
		        	
		        	t = RegaDBMain.getApp().getLogin().createTransaction();
        			
        			List<AttributeGroup> attributeGroups = t.getAttributeGroups();
        			
        			for(AttributeGroup ag: attributeGroups)
			        {
			            ((ComboBox)w).addItem(new DataComboMessage<AttributeGroup>(ag, ag.getGroupName()));
			        }
                    ((ComboBox)w).sort();
			        
			        t.commit();
			        
			        if(interactionState != InteractionState.Adding)
			        {
			        	((ComboBox)w).selectItem(qdrp.getValue());
			        }
		        break;
        	}
    		
    		parameterTable.putElementAt(i, 0, qdpL);
    		parameterTable.putElementAt(i, 1, w);
    		
    		i++;
    	}
    }
    
    
    public boolean saveData()
    {
    	boolean saved = true;
    	
    	int i = 1;
    	
    	for(QueryDefinitionRunParameter qdrp : qdrps)
    	{
    		QueryDefinitionParameterTypes type = QueryDefinitionParameterTypes.getQueryDefinitionParameterType(qdrp.getQueryDefinitionParameter().getQueryDefinitionParameterType());
    		
    		switch (type) 
        	{
		        case STRING:
		        	saved = saveDataFormField(qdrp, i);
		        break;
		        case INTEGER:
		        	saved = saveDataFormField(qdrp, i);
		        break;
		        case DOUBLE:
		        	saved = saveDataFormField(qdrp, i);
		        break;
		        case DATE:
		        	saved = saveDataFormField(qdrp, i);
		        break;
		        case GENERICDRUG:
		        	saved = saveDataComboBox(qdrp, i);
		        break;
		        case COMMERCIALDRUG:
		        	saved = saveDataComboBox(qdrp, i);
		        break;
		        case TEST:
		        	saved = saveDataComboBox(qdrp, i);
		        break;
		        case TESTTYPE:
		        	saved = saveDataComboBox(qdrp, i);
		        break;
		        case PROTEIN:
		        	saved = saveDataComboBox(qdrp, i);
		        break;
		        case ATTRIBUTE:
		        	saved = saveDataComboBox(qdrp, i);
		        break;
		        case ATTRIBUTEGROUP:
		        	saved = saveDataComboBox(qdrp, i);
		        break;
        	}
    		
    		i++;
    	}
    	
    	return saved;
    }
    
    public Set<QueryDefinitionRunParameter> getQueryDefinitionRunParameters()
    {
    	return qdrps;
    }
    
    private boolean saveDataFormField(QueryDefinitionRunParameter qdrp, int i)
    {
    	if(!((((FormField)(parameterTable.elementAt(i,1).children().get(i - 1))).text()).equals("")))
    	{
    		qdrp.setValue(((FormField)(parameterTable.elementAt(i,1).children().get(i - 1))).text());
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    private boolean saveDataComboBox(QueryDefinitionRunParameter qdrp, int i)
    {
    	if(((ComboBox)(parameterTable.elementAt(i,1).children().get(i - 1))).currentItem() != null)
    	{
    		qdrp.setValue(((ComboBox)(parameterTable.elementAt(i,1).children().get(i - 1))).text());
    		
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
}
