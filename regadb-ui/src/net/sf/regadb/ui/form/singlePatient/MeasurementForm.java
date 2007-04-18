package net.sf.regadb.ui.form.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

public class MeasurementForm extends FormWidget
{
	private TestResult testResult_;
	
	//General group
    private WGroupBox generalGroup_;
    private WTable generalGroupTable_;
    private Label dateL;
    private DateField dateTF;
    private Label testTypeL;
    private ComboBox testTypeCB;
    private Label testNameL;
    private ComboBox testNameCB;
    private Label testResultL;
    private ComboBox testResultNominalValueCB;
    private FormField testResultValueTF;
    
	public MeasurementForm(InteractionState interactionState, WMessage formName, TestResult testResult)
	{
		super(formName, interactionState);
		testResult_ = testResult;
		
		init();
	}
	
	public void init()
	{
        //general group
        generalGroup_ = new WGroupBox(tr("form.testResult.editView.general"), this);
        generalGroupTable_ = new WTable(generalGroup_);
        dateL = new Label(tr("form.testResult.editView.date"));
        dateTF = new DateField(getInteractionState(), this);
        addLineToTable(generalGroupTable_, dateL, dateTF);
        testTypeL = new Label(tr("form.testResult.editView.testType"));
        testTypeCB = new ComboBox(getInteractionState(), this);

        testTypeCB.setMandatory(true);
        addLineToTable(generalGroupTable_, testTypeL, testTypeCB);
        testNameL = new Label(tr("form.testResult.editView.testName"));
        testNameCB = new ComboBox(getInteractionState(), this);
        testNameCB.setMandatory(true);
        addLineToTable(generalGroupTable_, testNameL, testNameCB);
        testResultL = new Label(tr("form.testResult.editView.testResult"));
        
        //set the comboboxes
        Transaction t = RegaDBMain.getApp().createTransaction();
        WMessage first = null;
        WMessage current = null;
        for(TestType testType : t.getTestTypes())
        {
        	if(t.hasTests(testType))
        	{
	        	current = new DataComboMessage<TestType>(testType, testType.getDescription());
	        	if(first==null)
	        	{
	        		first = current;
	        	}
	        	testTypeCB.addItem(current);
        	}
        }
        testTypeCB.selectItem(first);

        TestType type = ((DataComboMessage<TestType>)testTypeCB.currentText()).getValue();
        
        t.commit();
        
        ValueTypes valueType = ValueTypes.getValueType(type.getValueType());
        if(valueType == ValueTypes.NOMINAL_VALUE)
        {
        	testResultNominalValueCB = new ComboBox(getInteractionState(), this);
        	for(TestNominalValue tnv : type.getTestNominalValues())
        	{
        		testResultNominalValueCB.addItem(new DataComboMessage<TestNominalValue>(tnv, tnv.getValue()));
        	}

        	testResultNominalValueCB.setMandatory(true);
        	addLineToTable(generalGroupTable_, testResultL, testResultNominalValueCB);
        }
        else
        {
        	testResultValueTF = getTextField(valueType);
            testResultValueTF.setMandatory(true);
            addLineToTable(generalGroupTable_, testResultL, testResultValueTF);
        }
        
        fillData();
        
        addControlButtons();
	}
	
	private void fillData()
	{
		if(!(getInteractionState()==InteractionState.Adding))
		{
	       	testTypeCB.selectItem(new DataComboMessage<TestType>(testResult_.getTest().getTestType(), testResult_.getTest().getTestType().getDescription()));
	        testNameCB.selectItem(new DataComboMessage<Test>(testResult_.getTest(), testResult_.getTest().getDescription()));
	        
	        dateTF.setDate(testResult_.getTestDate());
	        
	        if(testResultNominalValueCB!=null)
	        {
		    	if(testResult_.getTestNominalValue()!=null)
		    	{
		    		testResultNominalValueCB.selectItem(new DataComboMessage<TestNominalValue>(testResult_.getTestNominalValue(),testResult_.getTestNominalValue().getValue()));
		    	}
	        }
	        else
	        {
	        	testResultValueTF.setText(testResult_.getValue());
	        }
		}
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        TestType type = ((DataComboMessage<TestType>)testTypeCB.currentText()).getValue();
        setTestCombo(t, type);
        t.commit();
		
        testTypeCB.addComboChangeListener(new SignalListener<WEmptyEvent>()
                {
        			public void notify(WEmptyEvent a)
        			{
        				Transaction t = RegaDBMain.getApp().createTransaction();
        				setTestCombo(t, ((DataComboMessage<TestType>)testTypeCB.currentText()).getValue());
        				t.commit();
        			}
                });
	}
	
	private void setTestCombo(Transaction t, TestType testType)
	{
		testNameCB.clearItems();
		
        WMessage first = null;
        WMessage current = null;
        for(Test test : t.getTests(testType))
        {
        	current = new DataComboMessage<Test>(test, test.getDescription());
        	if(first==null)
        	{
        		first = current;
        	}
        	testNameCB.addItem(current);
        }
 
        testNameCB.selectItem(first);
	}

	@Override
	public void saveData()
	{
		Transaction t = RegaDBMain.getApp().createTransaction();
		
		Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
		t.attach(p);
		
		Test test = ((DataComboMessage<Test>)testNameCB.currentText()).getValue();
		
		if(getInteractionState()==InteractionState.Adding)
		{
			testResult_ = p.createTestResult(test);
		}
		else
		{
			testResult_.setTest(test);
		}
		
		testResult_.setTestDate(dateTF.getDate());
			    
		if(testResultNominalValueCB!=null)
		{
			testResult_.setTestNominalValue(((DataComboMessage<TestNominalValue>)testResultNominalValueCB.currentText()).getValue());
		}
		else
		{
			testResult_.setValue(testResultValueTF.text());
		}
		
		update(testResult_, t);
		t.commit();
		
		RegaDBMain.getApp().getTree().getTreeContent().measurementSelected.setSelectedItem(testResult_);
        RegaDBMain.getApp().getTree().getTreeContent().measurementSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().measurementSelected.refreshAllChildren();
		RegaDBMain.getApp().getTree().getTreeContent().measurementView.selectNode();
	}
}
