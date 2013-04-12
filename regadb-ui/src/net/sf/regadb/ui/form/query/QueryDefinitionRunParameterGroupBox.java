package net.sf.regadb.ui.form.query;

import java.util.List;
import java.util.Map;
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
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WWidget;

public class QueryDefinitionRunParameterGroupBox extends WGroupBox
{
	private InteractionState interactionState;
	
	private QueryDefinitionRunForm queryDefinitionRunForm;
	private QueryDefinitionRun queryDefinitionRun;
	
	private Set<QueryDefinitionRunParameter> qdrps;
	
	private FormTable parameterTable;
    
    public QueryDefinitionRunParameterGroupBox(InteractionState interactionState, WString message, QueryDefinitionRunForm queryDefinitionRunForm)
    {
    	super(message, queryDefinitionRunForm);
        
    	this.interactionState = interactionState;
        this.queryDefinitionRunForm = queryDefinitionRunForm;
        this.queryDefinitionRun = queryDefinitionRunForm.getObject();
        
        init();
    }
    
    public void init()
    {
    	qdrps = queryDefinitionRun.getQueryDefinitionRunParameters();
    	
    	parameterTable = new FormTable(this);
    	
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
    	
    	int i = 0;
    	
    	for(QueryDefinitionRunParameter qdrp : qdrps)
    	{
    		Label qdpL = new Label(qdrp.getQueryDefinitionParameter().getName());
    		
    		WWidget w = new TextField(interactionState, queryDefinitionRunForm);
    		Transaction t;
    		
    		QueryDefinitionParameterTypes type = QueryDefinitionParameterTypes.getQueryDefinitionParameterType(qdrp.getQueryDefinitionParameter().getQueryDefinitionParameterType());
    		
    		switch (type) 
        	{
		        case STRING:
		        	w = new TextField(interactionState, queryDefinitionRunForm, FieldType.ALFANUMERIC);
		        	
					((TextField)w).setText(qdrp.getValue());
		        break;
		        case INTEGER:
		        	w = new TextField(interactionState, queryDefinitionRunForm, FieldType.INTEGER);
		        	
		        	((TextField)w).setText(qdrp.getValue());
		        break;
		        case DOUBLE:
		        	w = new TextField(interactionState, queryDefinitionRunForm, FieldType.DOUBLE);
		        	
					((TextField)w).setText(qdrp.getValue());
		        break;
		        case DATE:
		        	w = new DateField(interactionState, queryDefinitionRunForm, RegaDBSettings.getInstance().getDateFormat());

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
    
    
    public boolean saveData(Map<String, Object> params)
    {
        boolean saved = true;
    	
    	int i = 0;
    	
    	for(QueryDefinitionRunParameter qdrp : qdrps)
    	{
    		QueryDefinitionParameterTypes type = QueryDefinitionParameterTypes.getQueryDefinitionParameterType(qdrp.getQueryDefinitionParameter().getQueryDefinitionParameterType());
    		
    		switch (type) 
        	{
		        case STRING:
		        	saved = saveDataFormField(qdrp, i, params);
		        break;
		        case INTEGER:
		        	saved = saveDataFormField(qdrp, i, params);
		        break;
		        case DOUBLE:
		        	saved = saveDataFormField(qdrp, i, params);
		        break;
		        case DATE:
		        	saved = saveDataFormField(qdrp, i, params);
		        break;
		        case GENERICDRUG:
		        	saved = saveDataComboBox(qdrp, i, params);
		        break;
		        case COMMERCIALDRUG:
		        	saved = saveDataComboBox(qdrp, i, params);
		        break;
		        case TEST:
		        	saved = saveDataComboBox(qdrp, i, params);
		        break;
		        case TESTTYPE:
		        	saved = saveDataComboBox(qdrp, i, params);
		        break;
		        case PROTEIN:
		        	saved = saveDataComboBox(qdrp, i, params);
		        break;
		        case ATTRIBUTE:
		        	saved = saveDataComboBox(qdrp, i, params);
		        break;
		        case ATTRIBUTEGROUP:
		        	saved = saveDataComboBox(qdrp, i, params);
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
    
    private boolean saveDataFormField(QueryDefinitionRunParameter qdrp, int i, Map<String, Object> params)
    {
    	if(!((((FormField)(parameterTable.getElementAt(i,1).getChildren().get(i))).text()).equals("")))
    	{
    		qdrp.setValue(((FormField)(parameterTable.getElementAt(i,1).getChildren().get(i))).text());
            params.put(qdrp.getQueryDefinitionParameter().getName(), ((FormField)(parameterTable.getElementAt(i,1).getChildren().get(i))).text());
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    private boolean saveDataComboBox(QueryDefinitionRunParameter qdrp, int i, Map<String, Object> params)
    {
    	if(((ComboBox)(parameterTable.getElementAt(i,1).getChildren().get(i))).currentItem() != null)
    	{
    		qdrp.setValue(((ComboBox)(parameterTable.getElementAt(i,1).getChildren().get(i))).text());
            params.put(qdrp.getQueryDefinitionParameter().getName(), ((ComboBox)(parameterTable.getElementAt(i,1).getChildren().get(i))).currentValue());
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
}
