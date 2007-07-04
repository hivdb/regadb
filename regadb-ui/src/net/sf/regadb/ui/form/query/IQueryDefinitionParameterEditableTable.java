package net.sf.regadb.ui.form.query;

import java.util.List;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.db.QueryDefinitionParameter;
import net.sf.regadb.db.QueryDefinitionParameterType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.IEditableTable;
import net.sf.witty.wt.WWidget;

public class IQueryDefinitionParameterEditableTable implements IEditableTable<QueryDefinitionParameter>
{
    private FormWidget form;
    private QueryDefinition queryDefinition;
    private Transaction transaction;
    private static final String[] headers = {"editableTable.query.definition.parameters.colName.name", "editableTable.query.definition.parameters.colName.type"};
    
    public IQueryDefinitionParameterEditableTable(FormWidget form, QueryDefinition queryDefinition)
    {
        this.form = form;
        this.queryDefinition = queryDefinition;
    }

	public void addData(WWidget[] widgets) 
	{
		String queryDefinitionParameterName = ((TextField)widgets[0]).getFormText();
		QueryDefinitionParameterType queryDefinitionParameterType = ((DataComboMessage<QueryDefinitionParameterType>)((ComboBox)widgets[1]).currentText()).getValue();
		
		QueryDefinitionParameter qdp = new QueryDefinitionParameter(queryDefinitionParameterType, queryDefinition, queryDefinitionParameterName);
		
        queryDefinition.getQueryDefinitionParameters().add(qdp);
	}

	public void changeData(QueryDefinitionParameter queryDefinitionParameter, WWidget[] widgets) 
	{
		String queryDefinitionParameterName = ((TextField)widgets[0]).getFormText();
		QueryDefinitionParameterType queryDefinitionParameterType = ((DataComboMessage<QueryDefinitionParameterType>)((ComboBox)widgets[1]).currentText()).getValue();
		
		queryDefinitionParameter.setQueryDefinitionParameterType(queryDefinitionParameterType);
		queryDefinitionParameter.setName(queryDefinitionParameterName);
	}

	public void deleteData(QueryDefinitionParameter qdp) 
	{
		queryDefinition.getQueryDefinitionParameters().remove(qdp);
		
		if(qdp != null)
		{
			transaction.delete(qdp);
		}
	}

	public InteractionState getInteractionState() 
	{
		return form.getInteractionState();
	}

	public String[] getTableHeaders() 
	{
		return headers;
	}

	public WWidget[] getWidgets(QueryDefinitionParameter queryDefinitionParameter) 
	{
		ComboBox combo = new ComboBox(form.getInteractionState(), form);
        TextField tf = new TextField(form.getInteractionState(), form);
        
        Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
        
        List<QueryDefinitionParameterType> queryDefinitionParameterTypes = t.getQueryDefinitionParameterTypes();
        
        for(QueryDefinitionParameterType qdpt: queryDefinitionParameterTypes)
        {
            combo.addItem(new DataComboMessage<QueryDefinitionParameterType>(qdpt, qdpt.getName()));
        }
        
        t.commit();
        
        WWidget[] widgets = new WWidget[2];
        widgets[0] = tf;
        widgets[1] = combo;
        
        combo.selectItem(new DataComboMessage<QueryDefinitionParameterType>(queryDefinitionParameter.getQueryDefinitionParameterType(), queryDefinitionParameter.getQueryDefinitionParameterType().getName()));
        tf.setText(queryDefinitionParameter.getName());
        
        return widgets;
	}
	
	public void setTransaction(Transaction transaction) 
    {
        this.transaction = transaction;
    }

	public WWidget[] addRow() 
	{
		ComboBox combo = new ComboBox(form.getInteractionState(), form);
        TextField tf = new TextField(form.getInteractionState(), form);
        
        Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
        
        List<QueryDefinitionParameterType> queryDefinitionParameterTypes = t.getQueryDefinitionParameterTypes();
        
        for(QueryDefinitionParameterType qdpt : queryDefinitionParameterTypes)
        {
            combo.addItem(new DataComboMessage<QueryDefinitionParameterType>(qdpt, qdpt.getName()));
        }
        
        t.commit();
        
        WWidget[] widgets = new WWidget[2];
        widgets[0] = tf;
        widgets[1] = combo;
        
        return widgets;
	}

	public WWidget[] fixAddRow(WWidget[] widgets) 
	{
		String queryDefinitionParameterName = ((TextField)widgets[0]).getFormText();
		QueryDefinitionParameterType queryDefinitionParameterType = ((DataComboMessage<QueryDefinitionParameterType>)((ComboBox)widgets[1]).currentText()).getValue();
		
		QueryDefinitionParameter qdp = new QueryDefinitionParameter(queryDefinitionParameterType, queryDefinition, queryDefinitionParameterName);
        
        return getWidgets(qdp);
	}
	
	public QueryDefinition getQueryDefinition()
	{
		return queryDefinition;
	}
    
    public void flush() 
    {
        transaction.flush();
    }
}
