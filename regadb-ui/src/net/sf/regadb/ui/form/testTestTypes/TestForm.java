package net.sf.regadb.ui.form.testTestTypes;

import java.util.List;

import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

public class TestForm extends FormWidget
{
private Test test_;
	
	//Frame 
	private WGroupBox mainFrameGroup_;
    private WTable mainFrameTable_;
    private Label testL ;
    private TextField testTF ;
    private Label testTypeL;
    private ComboBox testTypeCB;
    

	public TestForm(InteractionState interactionState, WMessage formName, Test test ) 
	{
		super(formName, interactionState);
		
		test_ = test;
		
		init();
		filldata();
		
	}

	private void init() 
	{
		mainFrameGroup_= new WGroupBox(tr("form.testSettings.test.editView.general"), this);
		mainFrameTable_ = new WTable(mainFrameGroup_);
		testL = new Label(tr("form.testSettings.test.editView.test"));
		testTF = new TextField(getInteractionState(), this);
	    testTF.setMandatory(true);
	    addLineToTable(mainFrameTable_, testL, testTF);
	    testTypeL=new Label(tr("form.testSettings.test.editView.testType"));
	    testTypeCB= new ComboBox (getInteractionState(),this);
	    testTypeCB.setMandatory(true);
	    addLineToTable(mainFrameTable_, testTypeL, testTypeCB);
	    
	    Transaction t = RegaDBMain.getApp().createTransaction();
	    boolean first = true;
        WMessage msg;
        WMessage toSelect = null;
        List<TestType> testTypes=t.getTestTypes();
	    first = true;
        toSelect = null;
        for(TestType tt : testTypes)
        {
            msg = new DataComboMessage<TestType>(tt, tt.getDescription());
            if(first)
            {
                toSelect = msg;
                first = false;
            }
            testTypeCB.addItem(msg);
        }
        testTypeCB.selectItem(toSelect);
        t.commit();
        
        addControlButtons();    
	}
	
	private void filldata() 
	{
		if(getInteractionState()==InteractionState.Adding)
        {
			test_=new Test();
        }
		
		if(getInteractionState()!=InteractionState.Adding)
        {
            Transaction t = RegaDBMain.getApp().createTransaction();
            
            t.attach(test_);
            
            testTF.setText(test_.getDescription());            
            testTypeCB.selectItem(new DataComboMessage<TestType>(test_.getTestType(), test_.getTestType().getDescription()));           
            t.commit();
         }
	}

	@Override
	public void saveData() 
	{
		Transaction t = RegaDBMain.getApp().createTransaction();
        if(!(getInteractionState()==InteractionState.Adding))
        {
            t.attach(test_);
        }
        TestType tt = ((DataComboMessage<TestType>)testTypeCB.currentText()).getValue();
        t.attach(tt);
    
        test_.setDescription(testTF.text());
        test_.setTestType(tt);     
        t.save(test_);
        t.commit();
        
        RegaDBMain.getApp().getTree().getTreeContent().testSelected.setSelectedTest(test_);
        RegaDBMain.getApp().getTree().getTreeContent().testSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().testSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().testSelected.selectNode();
       }
}