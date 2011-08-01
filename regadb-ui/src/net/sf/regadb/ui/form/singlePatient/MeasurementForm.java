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
import net.sf.regadb.ui.framework.forms.fields.TestComboBox;
import net.sf.regadb.ui.framework.forms.fields.TestTypeComboBox;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.TextFormat;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;

public class MeasurementForm extends FormWidget
{
	private TestResult testResult_;
	
	//General group
    private WGroupBox generalGroup_;
    private FormTable generalGroupTable_;
    private Label sampleIdL_;
    private TextField sampleIdTF_;
    private Label dateL;
    private DateField dateTF;
    private Label testTypeL;
    private TestTypeComboBox testTypeCB;
    private Label testNameL;
    private TestComboBox testNameCB;
    private Label testResultL;
    private FormField testResultField_;
    private WContainerWidget testResultC;
    
	public MeasurementForm(InteractionState interactionState, WString formName, TestResult testResult)
	{
		super(formName, interactionState);
		testResult_ = testResult;
		
		init();
	}
	
	public void init()
	{
        //general group
        generalGroup_ = new WGroupBox(tr("form.testResult.editView.general"), this);
        generalGroupTable_ = new FormTable(generalGroup_);
        sampleIdL_ = new Label(tr("form.testResult.editView.sampleid"));
        sampleIdTF_ = new TextField(getInteractionState(), this);
        generalGroupTable_.addLineToTable(sampleIdL_, sampleIdTF_);
        dateL = new Label(tr("form.testResult.editView.date"));
        dateTF = new DateField(getInteractionState(), this, RegaDBSettings.getInstance().getDateFormat());
        generalGroupTable_.addLineToTable(dateL, dateTF);
        testTypeL = new Label(tr("form.testResult.editView.testType"));
        testTypeCB = new TestTypeComboBox(getInteractionState(), this);

        testTypeCB.setMandatory(true);
        generalGroupTable_.addLineToTable(testTypeL, testTypeCB);
        testNameL = new Label(tr("form.testResult.editView.testName"));
        testNameCB = new TestComboBox(getInteractionState(), this);
        testNameCB.setMandatory(true);
        generalGroupTable_.addLineToTable(testNameL, testNameCB);
        testResultL = new Label(tr("form.testResult.editView.testResult"));
        testResultL.setLabelUIMandatory(this);
        testResultC = new WContainerWidget();
        int row = generalGroupTable_.getRowCount();
        generalGroupTable_.putElementAt(row, 0, testResultL);
        generalGroupTable_.putElementAt(row, 1, testResultC);
        generalGroupTable_.getElementAt(row,0).setStyleClass("form-label-area");
        
        //set the comboboxes
        if(getInteractionState() != InteractionState.Viewing){
	        Transaction t = RegaDBMain.getApp().createTransaction();
	        testTypeCB.fill(t, true, RegaDBSettings.getInstance().getInstituteConfig().getOrganismFilter());
	        testTypeCB.selectIndex(0);
	        
	        testNameCB.fill(t, testTypeCB.currentValue());
	
	        t.commit();
        }
        
        fillData();
        
        addControlButtons();
	}
	
	private void fillData()
	{
		if(getInteractionState() == InteractionState.Editing
				|| getInteractionState() == InteractionState.Adding)
		{
			if(getInteractionState() == InteractionState.Editing){
				Transaction t = RegaDBMain.getApp().createTransaction();
				TestType type = testResult_.getTest().getTestType();
				setTestCombo(t, type);
				t.commit();
				
				testTypeCB.selectItem(type);
				testNameCB.selectItem(testResult_.getTest());
				
	            setResultField(type.getValueType(), type);
	            
	            if(testResultField_ instanceof ComboBox)
	            {
	                ((ComboBox)testResultField_).selectItem(testResult_.getTestNominalValue().getValue());
	            }
	            else if(ValueTypes.getValueType(type.getValueType()) == ValueTypes.DATE)
	            {
	            	((DateField) testResultField_).setDate(DateUtils.parseDate(testResult_.getValue()));
	            }
	            else
	            {
	                testResultField_.setText(testResult_.getValue());
	            }
			}
			else{
	            setResultField(testTypeCB.currentValue().getValueType(), testTypeCB.currentValue());
			}
			
	        testTypeCB.addComboChangeListener(new Signal.Listener()
            {
    			public void trigger()
    			{
                    TestType testType = testTypeCB.currentValue();
                    
    				Transaction t = RegaDBMain.getApp().createTransaction();
    				setTestCombo(t, testType);
    				t.commit();
                    
                    setResultField(testType.getValueType(), testType);
    			}
            });
		}
		else{
			testTypeCB.setText(
					TestTypeComboBox.getLabel(testResult_.getTest().getTestType()));
			testNameCB.setText(
					TestComboBox.getLabel(testResult_.getTest()));
			
			new WText(	testResult_.getTestNominalValue() == null ?
							testResult_.getValue() : testResult_.getTestNominalValue().getValue(),
						TextFormat.PlainText,
						testResultC);
		}
        
		if(getInteractionState() != InteractionState.Adding){
	        dateTF.setDate(testResult_.getTestDate());
            sampleIdTF_.setText(testResult_.getSampleId());
		}
	}
	
	private void setTestCombo(Transaction t, TestType testType)
	{
		testNameCB.clearItems();
		testNameCB.fill(t, testType);
        testNameCB.selectIndex(0);
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
            ((ComboBox)testResultField_).sort();
        }
        else
        {
            testResultField_ = getTextField(ValueTypes.getValueType(valueType));
        }
        testResultField_.setMandatory(true);
        testResultC.clear();
        testResultC.addWidget(testResultField_);
    }

	@Override
	public void saveData()
	{
		Transaction t = RegaDBMain.getApp().createTransaction();
		
		Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
		t.attach(p);
		
		Test test = testNameCB.currentValue();
		
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
			testResult_.setTestNominalValue(((DataComboMessage<TestNominalValue>)((ComboBox)testResultField_).currentItem()).getDataValue());
            testResult_.setValue(null);
        }
		else if(ValueTypes.getValueType(testResult_.getTest().getTestType().getValueType()) == ValueTypes.DATE)
		{
		    testResult_.setValue(DateUtils.parse(testResultField_.text()).getTime()+"");
		    testResult_.setTestNominalValue(null);
		}
		else
		{
			testResult_.setValue(testResultField_.text());
            testResult_.setTestNominalValue(null);
		}
		
		update(testResult_, t);
		t.commit();
		
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getTestResultTreeNode().setSelectedItem(testResult_);
        redirectToView(
        		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getTestResultTreeNode().getSelectedActionItem(),
        		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getTestResultTreeNode().getViewActionItem());
	}
    
    @Override
    public void cancel()
    {
        if(getInteractionState()==InteractionState.Adding)
        {
            redirectToSelect(
            		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getTestResultTreeNode(),
            		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getTestResultTreeNode().getSelectActionItem());
        }
        else
        {
            redirectToView(
            		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getTestResultTreeNode().getSelectedActionItem(),
            		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getTestResultTreeNode().getViewActionItem());
        } 
    }
    
    @Override
    public WString deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
        p.getTestResults().remove(testResult_);
        
        t.delete(testResult_);
        
        t.commit();
        
        return null;
    }

    @Override
    public void redirectAfterDelete() 
    {
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getTestResultTreeNode()
        	.getSelectActionItem().selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getTestResultTreeNode()
        	.setSelectedItem(null);
    }
}
