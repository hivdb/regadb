package net.sf.regadb.ui.form.singlePatient;

import net.sf.regadb.db.Genome;
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
import net.sf.regadb.ui.framework.forms.fields.GenomeComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TestTypeComboBox;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.date.DateUtils;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.i8n.WMessage;

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
    private Label genomeL;
    private GenomeComboBox genomeCB;
    private Label testTypeL;
    private TestTypeComboBox testTypeCB;
    private Label testNameL;
    private ComboBox<Test> testNameCB;
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
        generalGroupTable_ = new FormTable(generalGroup_);
        sampleIdL_ = new Label(tr("form.testResult.editView.sampleid"));
        sampleIdTF_ = new TextField(getInteractionState(), this);
        generalGroupTable_.addLineToTable(sampleIdL_, sampleIdTF_);
        dateL = new Label(tr("form.testResult.editView.date"));
        dateTF = new DateField(getInteractionState(), this);
        generalGroupTable_.addLineToTable(dateL, dateTF);
        
        genomeL = new Label(tr("form.testResult.editView.genome"));
        genomeCB = new GenomeComboBox(getInteractionState(), this);
        generalGroupTable_.addLineToTable(genomeL, genomeCB);
        
        testTypeL = new Label(tr("form.testResult.editView.testType"));
        testTypeCB = new TestTypeComboBox(getInteractionState(), this);

        testTypeCB.setMandatory(true);
        generalGroupTable_.addLineToTable(testTypeL, testTypeCB);
        testNameL = new Label(tr("form.testResult.editView.testName"));
        testNameCB = new ComboBox<Test>(getInteractionState(), this);
        testNameCB.setMandatory(true);
        generalGroupTable_.addLineToTable(testNameL, testNameCB);
        testResultL = new Label(tr("form.testResult.editView.testResult"));
        testResultL.setLabelUIMandatory(this);
        testResultC = new WContainerWidget();
        int row = generalGroupTable_.numRows();
        generalGroupTable_.putElementAt(row, 0, testResultL);
        generalGroupTable_.putElementAt(row, 1, testResultC);
        generalGroupTable_.elementAt(row,0).setStyleClass("form-label-area");
        
        //set the comboboxes
        Transaction t = RegaDBMain.getApp().createTransaction();
        genomeCB.fill(t);
        testTypeCB.fill(t, null, true);
        testTypeCB.selectIndex(0);

        t.commit();
        
        fillData();
        
        addControlButtons();
	}
	
	private void fillData()
	{
		if(!(getInteractionState()==InteractionState.Adding))
		{
		    genomeCB.selectItem(testResult_.getTest().getTestType().getGenome());
	       	testTypeCB.selectItem(testResult_.getTest().getTestType());
	        testNameCB.selectItem(testResult_.getTest().getDescription());
	        
	        dateTF.setDate(testResult_.getTestDate());
            
            sampleIdTF_.setText(testResult_.getSampleId());
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
                ((ComboBox)testResultField_).selectItem(testResult_.getTestNominalValue().getValue());
            }
            else if(ValueTypes.getValueType(testResult_.getTest().getTestType().getValueType()) == ValueTypes.DATE)
            {
            	((DateField) testResultField_).setDate(DateUtils.parseDate(testResult_.getValue()));
            }
            else
            {
                testResultField_.setText(testResult_.getValue());
            }
        }
        
        genomeCB.addComboChangeListener(new SignalListener<WEmptyEvent>()
                {
                    public void notify(WEmptyEvent a)
                    {
                        Genome g = genomeCB.currentValue();
                        
                        testTypeCB.clearItems();
                        Transaction t = RegaDBMain.getApp().createTransaction();
                        testTypeCB.fill(t, g, true);
                        t.commit();
                    }
                });
		
        testTypeCB.addComboChangeListener(new SignalListener<WEmptyEvent>()
                {
        			public void notify(WEmptyEvent a)
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
		
        for(Test test : t.getTests(testType))
        {
        	testNameCB.addItem(new DataComboMessage<Test>(test, test.getDescription()));
        }
        testNameCB.sort();
        
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
		
		Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
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
			testResult_.setTestNominalValue(((DataComboMessage<TestNominalValue>)((ComboBox)testResultField_).currentItem()).getValue());
            testResult_.setValue(null);
        }
		else if(ValueTypes.getValueType(testResult_.getTest().getTestType().getValueType()) == ValueTypes.DATE)
		{
		    testResult_.setValue(DateUtils.parserEuropeanDate(testResultField_.text()).getTime()+"");
		    testResult_.setTestNominalValue(null);
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
