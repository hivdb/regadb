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
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TestComboBox;
import net.sf.regadb.ui.framework.forms.fields.TestTypeComboBox;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;

public class MeasurementForm extends ObjectForm<TestResult>
{
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
    
	public MeasurementForm(WString formName, InteractionState interactionState, ObjectTreeNode<TestResult> node, TestResult testResult)
	{
		super(formName, interactionState, node, testResult);
		if(RegaDBMain.getApp().isPatientInteractionAllowed(interactionState))
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
        Transaction t = RegaDBMain.getApp().createTransaction();
        testTypeCB.fill(t, true, RegaDBSettings.getInstance().getInstituteConfig().getOrganismFilter());
        testTypeCB.selectIndex(0);

        t.commit();
        
        fillData();
        
        addControlButtons();
	}
	
	private void fillData()
	{
		if(!(getInteractionState()==InteractionState.Adding))
		{
	       	testTypeCB.selectItem(getObject().getTest().getTestType());
	        testNameCB.selectItem(getObject().getTest());
	        
	        dateTF.setDate(getObject().getTestDate());
            
            sampleIdTF_.setText(getObject().getSampleId());
		}
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        TestType type = testTypeCB.currentValue();
        setTestCombo(t, type);
        t.commit();
        
        setResultField(type.getValueType(), type);
        
        if(!(getInteractionState()==InteractionState.Adding))
        {
            if(testResultField_ instanceof ComboBox)
            {
                ((ComboBox)testResultField_).selectItem(getObject().getTestNominalValue().getValue());
            }
            else if(ValueTypes.getValueType(getObject().getTest().getTestType().getValueType()) == ValueTypes.DATE)
            {
            	((DateField) testResultField_).setDate(DateUtils.parseDate(getObject().getValue()));
            }
            else
            {
                testResultField_.setText(getObject().getValue());
            }
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
			setObject(p.createTestResult(test));
		}
		else
		{
			getObject().setTest(test);
		}
		
		getObject().setTestDate(dateTF.getDate());
		getObject().setSampleId(sampleIdTF_.text());
			    
		if(testResultField_ instanceof ComboBox)
		{
			getObject().setTestNominalValue(((DataComboMessage<TestNominalValue>)((ComboBox)testResultField_).currentItem()).getDataValue());
			getObject().setValue(null);
        }
		else if(ValueTypes.getValueType(getObject().getTest().getTestType().getValueType()) == ValueTypes.DATE)
		{
			getObject().setValue(DateUtils.parse(testResultField_.text()).getTime()+"");
			getObject().setTestNominalValue(null);
		}
		else
		{
			getObject().setValue(testResultField_.text());
			getObject().setTestNominalValue(null);
		}
		
		update(getObject(), t);
		t.commit();
	}
    
    @Override
    public void cancel()
    {

    }
    
    @Override
    public WString deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
        p.getTestResults().remove(getObject());
        
        t.delete(getObject());
        
        t.commit();
        
        return null;
    }
}
