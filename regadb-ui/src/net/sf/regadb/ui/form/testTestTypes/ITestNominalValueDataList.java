package net.sf.regadb.ui.form.testTestTypes;

import net.sf.regadb.db.TestType;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.IEditableTable;
import net.sf.witty.wt.WWidget;

public class ITestNominalValueDataList implements IEditableTable <TestNominalValue>
{
	private FormWidget form_;
    private static final String [] headers_ = {"editableTable.testNominvalValue.colName.name"};
    
    private TestType testType_;
    private Transaction transaction_;
    
    public ITestNominalValueDataList(FormWidget form, TestType testType)
    {
        form_ = form;
        testType_ = testType;
    }
    
    public void addData(WWidget[] widgets) 
    {
        TestNominalValue anv = new TestNominalValue(testType_, ((TextField)widgets[0]).text());
        testType_.getTestNominalValues().add(anv);
    }

    public void changeData(TestNominalValue type, WWidget[] widgets) 
    {
        for(TestNominalValue anv : testType_.getTestNominalValues())
        {
            if(type.getNominalValueIi().equals(anv.getNominalValueIi()))
            {
                anv.setValue(((TextField)widgets[0]).text());
                break;
            }
        }
    }

    public void deleteData(TestNominalValue type) 
    {
    	testType_.getTestNominalValues().remove(type);
    	if(type!=null && type.getNominalValueIi()!=null)
    	{
    		transaction_.delete(type);
    	}
    }

    public InteractionState getInteractionState() 
    {
        return form_.getInteractionState();
    }

    public String[] getTableHeaders() 
    {
        return headers_;
    }

    public WWidget[] getWidgets(TestNominalValue type) 
    {
        TextField tf = new TextField(form_.getInteractionState(), form_);
                
        WWidget[] widgets = new WWidget[1];
        widgets[0] = tf;
        
        tf.setText(type.getValue());
        
        return widgets;
    }

    public TestType getTest() 
    {
        return testType_;
    }

    public void setTest(TestType testType) 
    {
        this.testType_ = testType;
    }

    public void setTransaction(Transaction transaction) 
    {
        this.transaction_ = transaction;
    }

    public WWidget[] addRow() 
    {
        TextField tf = new TextField(form_.getInteractionState(), form_);
        
        WWidget[] widgets = new WWidget[1];
        widgets[0] = tf;
        
        return widgets;
    }

    public WWidget[] fixAddRow(WWidget[] widgets) 
    {
        TextField tf = new TextField(form_.getInteractionState(), form_);
        
        WWidget[] widgetsToReturn = new WWidget[1];
        widgetsToReturn[0] = tf;
        
        tf.setText(((TextField)widgets[0]).text());
        
        return widgetsToReturn;
    }
}