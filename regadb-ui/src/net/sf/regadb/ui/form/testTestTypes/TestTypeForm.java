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
import net.sf.regadb.ui.framework.forms.fields.GenomeComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WString;

public class TestTypeForm extends FormWidget
{
	private TestType testType_;
	
	//Frame 
	private WGroupBox mainFrameGroup_;
    private FormTable mainFrameTable_;
    private Label testTypeL ;
    private TextField testTypeTF ;
    private Label valueTypeL;
    private ComboBox<ValueType> valueTypeCB;
    private Label testObjectL;
    private ComboBox<TestObject> testObjectCB;    
    
    private Label genomeL;
    private GenomeComboBox genomeCB;
    
//  nominal values group
    private WGroupBox nominalValuesGroup_;
    private EditableTable<TestNominalValue> nominalValuesList_;
    private ITestNominalValueDataList iNominalValuesList_;
	
	public TestTypeForm(InteractionState interactionState, WString formName, TestType testType ) 
	{
		super(formName, interactionState);
		
		testType_ = testType;
		
		init();
		filldata();
	}

	private void init() 
	{
		mainFrameGroup_= new WGroupBox(tr("form.testSettings.testType.editView.general"), this);
		mainFrameTable_ = new FormTable(mainFrameGroup_);
		testTypeL = new Label(tr("form.testSettings.testType.editView.testType"));
		testTypeTF = new TextField(getInteractionState()==InteractionState.Editing?InteractionState.Viewing:getInteractionState(), this);
	    testTypeTF.setMandatory(true);
	    mainFrameTable_.addLineToTable(testTypeL, testTypeTF);
	    valueTypeL = new Label(tr("form.testSettings.testType.editView.valueType"));
        valueTypeCB = new ComboBox<ValueType>(getInteractionState(), this);
        valueTypeCB.setMandatory(true);
        mainFrameTable_.addLineToTable(valueTypeL, valueTypeCB);
	    testObjectL=new Label(tr("form.testSettings.testType.editView.testobject"));
	    testObjectCB= new ComboBox<TestObject>(getInteractionState(),this);
	    testObjectCB.setMandatory(true);
	    mainFrameTable_.addLineToTable(testObjectL, testObjectCB);
	    
	    genomeL = new Label(tr("form.testSettings.testType.editView.genome"));
	    genomeCB = new GenomeComboBox(getInteractionState(), this);
	    mainFrameTable_.addLineToTable(genomeL, genomeCB);
	    
	    Transaction t = RegaDBMain.getApp().createTransaction();
	    List<ValueType> valueTypes=t.getValueTypes();
        for(ValueType vt : valueTypes)
        {
            valueTypeCB.addItem(new DataComboMessage<ValueType>(vt, vt.getDescription()));
        }
        valueTypeCB.sort();
        valueTypeCB.selectIndex(0);
	    
        List<TestObject> testObjects=t.getTestObjects();
        for(TestObject to : testObjects)
        {
            testObjectCB.addItem(new DataComboMessage<TestObject>(to, to.getDescription()));
        }
        valueTypeCB.sort();
        testObjectCB.selectIndex(0);
        
        nominalValuesGroup_ = new WGroupBox(tr("form.testSettings.testType.editView.nominalValues"), this);
               
        addControlButtons();
	}
	
	private void setNominalValuesGroup()
    {
        boolean visible = (ValueTypes.getValueType(valueTypeCB.currentValue()) == ValueTypes.NOMINAL_VALUE);
        
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
            valueTypeCB.selectItem(testType_.getValueType().getDescription());
            
            testObjectCB.selectItem(testType_.getTestObject().getDescription());
            t.commit();
         }
         
		 setNominalValuesGroup();
		 valueTypeCB.addComboChangeListener(new Signal.Listener()
	                {
	                    public void trigger()
	                    {
	                        setNominalValuesGroup();
	                    }
	                });
		 
        Transaction t = RegaDBMain.getApp().createTransaction();
        genomeCB.fill(t);
        if(testType_.getGenome() != null)
            genomeCB.selectItem(testType_.getGenome().getOrganismName());
        else
            genomeCB.selectIndex(0);
        t.commit();
	}

	@Override
	public void saveData() 
	{   
		WString duplicates = null;
        if(nominalValuesList_!=null) {
        duplicates = nominalValuesList_.removeDuplicates(0);
        }
        if(duplicates!=null)
        {
        	UIUtils.showWarningMessageBox(this, duplicates);
        }
        
		Transaction t = RegaDBMain.getApp().createTransaction();
        if(!(getInteractionState()==InteractionState.Adding))
        {
            t.attach(testType_);
        }
        
        TestObject to = testObjectCB.currentValue();
        t.attach(to);
        
        ValueType vt = valueTypeCB.currentValue();
        t.attach(vt);
        
        testType_.setDescription(testTypeTF.text());
        testType_.setValueType(vt);
        testType_.setTestObject(to);
        testType_.setGenome(genomeCB.currentValue());
          
        if(!nominalValuesGroup_.isHidden())
        {
            iNominalValuesList_.setTest(testType_);
            iNominalValuesList_.setTransaction(t);
            nominalValuesList_.saveData();
        }
        
        update(testType_, t);
        t.commit();
        
        RegaDBMain.getApp().getTree().getTreeContent().testTypeSelected.setSelectedItem(testType_);
        redirectToView(RegaDBMain.getApp().getTree().getTreeContent().testTypeSelected, RegaDBMain.getApp().getTree().getTreeContent().testTypesView);
	}
    
    @Override
    public void cancel()
    {
        if(getInteractionState()==InteractionState.Adding)
        {
            redirectToSelect(RegaDBMain.getApp().getTree().getTreeContent().testTypes, RegaDBMain.getApp().getTree().getTreeContent().testTypesSelect);
        }
        else
        {
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().testTypeSelected, RegaDBMain.getApp().getTree().getTreeContent().testTypesView);
        } 
    }
    
    @Override
    public WString deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        try
        {
        	t.delete(testType_);
            
            t.commit();
            
            return null;
        }
        catch(Exception e)
        {
        	t.clear();
        	t.rollback();
        	
        	return tr("form.delete.restriction");
        }
    }

    @Override
    public void redirectAfterDelete() 
    {
        RegaDBMain.getApp().getTree().getTreeContent().testTypesSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().testTypeSelected.setSelectedItem(null);
    }
}