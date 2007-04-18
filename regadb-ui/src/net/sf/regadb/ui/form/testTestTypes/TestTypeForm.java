package net.sf.regadb.ui.form.testTestTypes;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

public class TestTypeForm extends FormWidget
{
	private TestType testType_;
	
	//Frame 
	private WGroupBox mainFrameGroup_;
    private WTable mainFrameTable_;
    private Label testTypeL ;
    private TextField testTypeTF ;
    private Label valueTypeL;
    private ComboBox valueTypeCB;
    private Label testObjectL;
    private ComboBox testObjectCB;    
    
//  nominal values group
    private WGroupBox nominalValuesGroup_;
    private EditableTable<TestNominalValue> nominalValuesList_;
    private ITestNominalValueDataList iNominalValuesList_;
	
	public TestTypeForm(InteractionState interactionState, WMessage formName, TestType testType ) 
	{
		super(formName, interactionState);
		
		testType_ = testType;
		
		init();
		filldata();
	}

	private void init() 
	{
		mainFrameGroup_= new WGroupBox(tr("form.testSettings.testType.editView.general"), this);
		mainFrameTable_ = new WTable(mainFrameGroup_);
		testTypeL = new Label(tr("form.testSettings.testType.editView.testType"));
		testTypeTF = new TextField(getInteractionState(), this);
	    testTypeTF.setMandatory(true);
	    addLineToTable(mainFrameTable_, testTypeL, testTypeTF);
	    valueTypeL = new Label(tr("form.testSettings.testType.editView.valueType"));
        valueTypeCB = new ComboBox(getInteractionState(), this);
        valueTypeCB.setMandatory(true);
        addLineToTable(mainFrameTable_, valueTypeL, valueTypeCB);
	    testObjectL=new Label(tr("form.testSettings.testType.editView.testobject"));
	    testObjectCB= new ComboBox (getInteractionState(),this);
	    testObjectCB.setMandatory(true);
	    addLineToTable(mainFrameTable_, testObjectL, testObjectCB);
	    Transaction t = RegaDBMain.getApp().createTransaction();
	    List<ValueType> valueTypes=t.getValueTypes();
	    boolean first = true;
        WMessage msg;
        WMessage toSelect = null;
        for(ValueType vt : valueTypes)
        {
            msg = new DataComboMessage<ValueType>(vt, vt.getDescription());
            if(first)
            {
                toSelect = msg;
                first = false;
            }
            valueTypeCB.addItem(msg);
        }
        valueTypeCB.selectItem(toSelect);
	    
        List<TestObject> testObjects=t.getTestObjects();
	    first = true;
        toSelect = null;
        for(TestObject to : testObjects)
        {
            msg = new DataComboMessage<TestObject>(to, to.getDescription());
            if(first)
            {
                toSelect = msg;
                first = false;
            }
            testObjectCB.addItem(msg);
        }
        testObjectCB.selectItem(toSelect);
        
        nominalValuesGroup_ = new WGroupBox(tr("form.testSettings.testType.editView.nominalValues"), this);
               
        addControlButtons();
	}
	
	private void setNominalValuesGroup()
    {
        boolean visible = (ValueTypes.getValueType(((DataComboMessage<ValueType>)valueTypeCB.currentText()).getValue()) == ValueTypes.NOMINAL_VALUE);
        
        if(!visible)
        {
            nominalValuesGroup_.setHidden(true);
        }
        else
        {
            nominalValuesGroup_.setHidden(false);
            if(nominalValuesList_!=null)
            {
                nominalValuesGroup_.removeWidget(nominalValuesList_);
            }
            ArrayList<TestNominalValue> list = new ArrayList<TestNominalValue>();
            if(getInteractionState()!=InteractionState.Adding)
            {
                Transaction t = RegaDBMain.getApp().createTransaction();
                t.attach(testType_);
                
                for(TestNominalValue anv : testType_.getTestNominalValues())
                {
                    list.add(anv);
                }
                t.commit();
            }
            iNominalValuesList_ = new ITestNominalValueDataList(this, testType_);
            nominalValuesList_ = new EditableTable<TestNominalValue>(nominalValuesGroup_, iNominalValuesList_, list);
        }
    }

	private void filldata() 
	{
		if(getInteractionState()==InteractionState.Adding)
        {
			testType_ = new TestType();
        }
		
		if(getInteractionState()!=InteractionState.Adding)
        {
            Transaction t = RegaDBMain.getApp().createTransaction();
            
            t.attach(testType_);
            
            testTypeTF.setText(testType_.getDescription());
            valueTypeCB.selectItem(new DataComboMessage<ValueType>(testType_.getValueType(), testType_.getValueType().getDescription()));
            
            testObjectCB.selectItem(new DataComboMessage<TestObject>(testType_.getTestObject(), testType_.getTestObject().getDescription()));           
            t.commit();
         }
         
		 setNominalValuesGroup();
		 valueTypeCB.addComboChangeListener(new SignalListener<WEmptyEvent>()
	                {
	                    public void notify(WEmptyEvent a)
	                    {
	                        setNominalValuesGroup();
	                    }
	                });
	}

	@Override
	public void saveData() 
	{
		Transaction t = RegaDBMain.getApp().createTransaction();
        if(!(getInteractionState()==InteractionState.Adding))
        {
            t.attach(testType_);
        }
        
        TestObject to = ((DataComboMessage<TestObject>)testObjectCB.currentText()).getValue();
        t.attach(to);
        
        ValueType vt = ((DataComboMessage<ValueType>)valueTypeCB.currentText()).getValue();
        t.attach(vt);
        
        testType_.setDescription(testTypeTF.text());
        testType_.setValueType(vt);
        testType_.setTestObject(to);
          
        if(!nominalValuesGroup_.isHidden())
        {
            iNominalValuesList_.setTest(testType_);
            iNominalValuesList_.setTransaction(t);
            nominalValuesList_.saveData();
        }
        
        t.update(testType_);
        t.commit();
        
        RegaDBMain.getApp().getTree().getTreeContent().testTypeSelected.setSelectedItem(testType_);
        RegaDBMain.getApp().getTree().getTreeContent().testTypeSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().testTypeSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().testTypesView.selectNode();
	}
}