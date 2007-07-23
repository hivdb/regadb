package net.sf.regadb.ui.form.singlePatient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
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
    private Label sampleIdL_;
    private TextField sampleIdTF_;
    private Label dateL;
    private DateField dateTF;
    private Label testTypeL;
    private ComboBox testTypeCB;
    private Label testNameL;
    private ComboBox testNameCB;
    private Label testResultL;
    private FormField testResultField_;
    private WContainerWidget testResultC;
    
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
        sampleIdL_ = new Label(tr("form.testResult.editView.sampleid"));
        sampleIdTF_ = new TextField(getInteractionState(), this);
        addLineToTable(generalGroupTable_, sampleIdL_, sampleIdTF_);
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
        testResultL.setLabelUIMandatory(this);
        testResultC = new WContainerWidget();
        int row = generalGroupTable_.numRows();
        generalGroupTable_.putElementAt(row, 0, testResultL);
        generalGroupTable_.putElementAt(row, 1, testResultC);
        
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
            
            sampleIdTF_.setText(testResult_.getSampleId());
		}
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        TestType type = ((DataComboMessage<TestType>)testTypeCB.currentText()).getValue();
        setTestCombo(t, type);
        t.commit();
        
        setResultField(type.getValueType(), type);
        
        if(!(getInteractionState()==InteractionState.Adding))
        {
            if(testResultField_ instanceof ComboBox)
            {
                ((ComboBox)testResultField_).selectItem(new DataComboMessage<TestNominalValue>(testResult_.getTestNominalValue(),testResult_.getTestNominalValue().getValue()));
            }
            else
            {
                testResultField_.setText(testResult_.getValue());
            }
        }
		
        testTypeCB.addComboChangeListener(new SignalListener<WEmptyEvent>()
                {
        			public void notify(WEmptyEvent a)
        			{
                        TestType testType = ((DataComboMessage<TestType>)testTypeCB.currentText()).getValue();
                        
        				Transaction t = RegaDBMain.getApp().createTransaction();
        				setTestCombo(t, testType);
        				t.commit();
                        
                        setResultField(testType.getValueType(), testType);
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
    
    private void setResultField(ValueType valueType, TestType type)
    {
        removeFormField(testResultField_);
        if(ValueTypes.getValueType(valueType) == ValueTypes.NOMINAL_VALUE)
        {
            testResultField_ = new ComboBox(getInteractionState(), this);
            for(TestNominalValue tnv : type.getTestNominalValues())
            {
                ((ComboBox)testResultField_).addItem(new DataComboMessage<TestNominalValue>(tnv, tnv.getValue()));
            }
        }
        else
        {
            testResultField_ = getTextField(ValueTypes.getValueType(valueType));
        }
        addFormField(testResultField_);
        testResultField_.setMandatory(true);
        testResultC.clear();
        testResultC.addWidget(testResultField_);
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
        testResult_.setSampleId(sampleIdTF_.text());
			    
		if(testResultField_ instanceof ComboBox)
		{
			testResult_.setTestNominalValue(((DataComboMessage<TestNominalValue>)((ComboBox)testResultField_).currentText()).getValue());
            testResult_.setValue(null);
        }
		else
		{
			testResult_.setValue(testResultField_.text());
            testResult_.setTestNominalValue(null);
		}
		
		update(testResult_, t);
		t.commit();
		
        RegaDBMain.getApp().getTree().getTreeContent().measurementSelected.setSelectedItem(testResult_);
        redirectToView(RegaDBMain.getApp().getTree().getTreeContent().measurementSelected, RegaDBMain.getApp().getTree().getTreeContent().measurementView);
	}
    
    @Override
    public void cancel()
    {
        if(getInteractionState()==InteractionState.Adding)
        {
            redirectToSelect(RegaDBMain.getApp().getTree().getTreeContent().measurements, RegaDBMain.getApp().getTree().getTreeContent().measurementsSelect);
        }
        else
        {
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().measurementSelected, RegaDBMain.getApp().getTree().getTreeContent().measurementView);
        } 
    }
    
    @Override
    public WMessage deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
        p.getTestResults().remove(testResult_);
        
        t.delete(testResult_);
        
        t.commit();
        
        return null;
    }

    @Override
    public void redirectAfterDelete() 
    {
        RegaDBMain.getApp().getTree().getTreeContent().measurementsSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().measurementSelected.setSelectedItem(null);
    }
}
