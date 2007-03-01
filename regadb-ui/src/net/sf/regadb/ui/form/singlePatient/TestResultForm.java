package net.sf.regadb.ui.form.singlePatient;

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
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.WGroupBox;
import net.sf.witty.wt.widgets.WTable;

public class TestResultForm extends FormWidget
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
    
	public TestResultForm(InteractionState interactionState, WMessage formName, TestResult testResult)
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
        
        fillData();
        
        addControlButtons();
	}
	
	private void fillData()
	{
		Transaction t;
        if(testResult_!=null)
        {
        	t = RegaDBMain.getApp().createTransaction();
            t.update(testResult_);
            t.commit();
        }
        else
        {
        	testResult_ = new TestResult();
        }
                
        dateTF.setDate(testResult_.getTestDate());
        
        t = RegaDBMain.getApp().createTransaction();
        for(TestType testType : t.getTestTypes())
        {
        	testTypeCB.addItem(new DataComboMessage<TestType>(testType, testType.getDescription()));
        }
        
        if(testResult_.getTest()!=null)
        {
        	testTypeCB.selectItem(new DataComboMessage<TestType>(testResult_.getTest().getTestType(), testResult_.getTest().getTestType().getDescription()));
        }
        setTestCombo(t, ((DataComboMessage<TestType>)testTypeCB.currentText()).getValue());

        t.commit();

        if(testResult_.getTest()==null)
        {
        	testResult_.setTest(((DataComboMessage<Test>)testNameCB.currentText()).getValue());
        }
        
        ValueTypes valueType = ValueTypes.getValueType(testResult_.getTest().getTestType().getValueType().getValueTypeIi());
        if(valueType == ValueTypes.NOMINAL_VALUE)
        {
        	testResultNominalValueCB = new ComboBox(getInteractionState(), this);
        	for(TestNominalValue tnv : testResult_.getTest().getTestType().getTestNominalValues())
        	{
        		testResultNominalValueCB.addItem(new DataComboMessage<TestNominalValue>(tnv, tnv.getValue()));
        	}
        	if(testResult_.getTestNominalValue()!=null)
        	{
        		testResultNominalValueCB.selectItem(new DataComboMessage<TestNominalValue>(testResult_.getTestNominalValue(),testResult_.getTestNominalValue().getValue()));
        	}
        	testResultNominalValueCB.setMandatory(true);
        	addLineToTable(generalGroupTable_, testResultL, testResultNominalValueCB);
        }
        else
        {
        	testResultValueTF = getTextField(valueType);
            testResultValueTF.setMandatory(true);
            testResultValueTF.setText(testResult_.getValue());
            addLineToTable(generalGroupTable_, testResultL, testResultValueTF);
        }	
	}
	
	private void setTestCombo(Transaction t, TestType testType)
	{
		testNameCB.clearItems();
		
        for(Test test : t.getTests(testType))
        {
        	testNameCB.addItem(new DataComboMessage<Test>(test, test.getDescription()));
        }
        if(testResult_.getTest()!=null)
        {
        	testNameCB.selectItem(new DataComboMessage<Test>(testResult_.getTest(), testResult_.getTest().getDescription()));
        }
	}

	@Override
	public void saveData()
	{
		
	}
}
