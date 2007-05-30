package net.sf.regadb.ui.form.testTestTypes;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.datatable.testSettings.ITestAnalysisDataList;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.CheckBox;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WLineEditEchoMode;
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
    private Label analysisL;
    private CheckBox analysisCK;
    
    //analysis group
    private WGroupBox analysisGroup_;
    private WTable analysisTable_;
    private Label typeL;
    private TextField analysisTypeTF;
    private Label urlL;
    private TextField urlTF;
    private Label analysisTypeL;
    private ComboBox analysisTypeCB;
    private Label accountL;
    private TextField accountTF;
    private Label passwordL;
    private TextField passwordTF;
    private Label baseInputFileL;
    private TextField baseInputFileTF;
    private Label baseOutputFileL;
    private TextField baseOutputFileTF;
  
    //analysis data group
    private WGroupBox analysisDataGroup_;
   /* private WTable analysisDataTable_;
    private Label nameL;
    private TextField nameTF;
    private Label dataL;
    private TextField dataTF;
    private WPushButton addButton;
    private WPushButton removeButton;
    private CheckBox removeCK;*/
    
    private EditableTable<AnalysisData> analysisDataList_;
    private ITestAnalysisDataList iAnalysisDataEditableList_; 
    
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
	    analysisL = new Label(tr("form.testSettings.test.editView.analysis"));
	    analysisCK = new CheckBox(getInteractionState(),this);
	    addLineToTable(mainFrameTable_,analysisL, analysisCK);
	    
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
     
        t = RegaDBMain.getApp().createTransaction();
	    
        analysisGroup_= new WGroupBox(tr("form.testSettings.test.editView.analysisGroup"), this);
        analysisTable_  = new WTable(analysisGroup_);
	    
	    typeL = new Label(tr("form.testSettings.test.editView.analysis.type"));
	    analysisTypeTF= new TextField(getInteractionState(), this);
	    addLineToTable(analysisTable_,typeL, analysisTypeTF);
	    urlL = new Label(tr("form.testSettings.test.editView.analysis.URL"));
	    urlTF = new TextField(getInteractionState(), this);
	    addLineToTable(analysisTable_,urlL, urlTF);
	    analysisTypeL = new Label(tr("form.testSettings.test.editView.analysis.analysisType"));
	    analysisTypeCB = new ComboBox(getInteractionState(), this);;
	    addLineToTable(analysisTable_,analysisTypeL, analysisTypeCB);
	    accountL = new Label(tr("form.testSettings.test.editView.analysis.account"));
	    accountTF= new TextField(getInteractionState(), this);
	    addLineToTable(analysisTable_,accountL, accountTF);
	    passwordL = new Label(tr("form.testSettings.test.editView.analysis.password"));
	    passwordTF = new TextField(getInteractionState(), this);
	    passwordTF.setEchomode(WLineEditEchoMode.Password);
	    passwordTF.setMandatory(true);
	   addLineToTable(analysisTable_,passwordL, passwordTF);
	    baseInputFileL = new Label(tr("form.testSettings.test.editView.analysis.baseInputFile"));;
	    baseInputFileTF= new TextField(getInteractionState(), this);
	    addLineToTable(analysisTable_,baseInputFileL, baseInputFileTF);
	    baseOutputFileL = new Label(tr("form.testSettings.test.editView.analysis.baseOutputFile"));;
	    baseOutputFileTF= new TextField(getInteractionState(), this);
	    addLineToTable(analysisTable_,baseOutputFileL, baseOutputFileTF);
	    toSelect = null;
	    List<AnalysisType> analysisTypes =t.getAnalysisTypes();
		first = true;
	    toSelect = null;
	    for(AnalysisType at : analysisTypes)
	    {
	    	msg = new DataComboMessage<AnalysisType>(at, at.getType());
	    	if(first)
	    	{
	    		toSelect = msg;
	    		first = false;
	    	}
	    	analysisTypeCB.addItem(msg);
	    }
	    analysisTypeCB.selectItem(toSelect);
	    t.commit();   
	    analysisDataGroup_ = new WGroupBox(tr("form.testSettings.test.editView.analysisDataGroup"),this);
	    /*analysisDataTable_ =new  WTable(analysisDataGroup_);

	    nameL = new Label(tr("form.testSettings.test.editView.analysisData.name"));
	    nameTF= new TextField(getInteractionState(), this);
	    dataL = new Label(tr("form.testSettings.test.editView.analysisData.data"));
	    dataTF= new TextField(getInteractionState(), this);
	    addButton=new WPushButton(tr("form.testSettings.test.editView.analysisData.addButton"));
	    removeButton=new WPushButton(tr("form.testSettings.test.editView.analysisData.removeButton"));
	    removeCK= new CheckBox(getInteractionState(),this);
	    analysisDataTable_.putElementAt(0, 0, nameL);
	    analysisDataTable_.putElementAt(0, 1, dataL);
	    analysisDataTable_.putElementAt(0, 2, addButton);
	    analysisDataTable_.putElementAt(0, 3, removeButton);
	    analysisDataTable_.putElementAt(1, 0, nameTF);
	    analysisDataTable_.putElementAt(1, 1, dataTF);
	    analysisDataTable_.putElementAt(1, 3, removeCK);*/
	    addControlButtons();    
	}
	private void setAnalysisGroup()
	{
		 if (!analysisCK.isChecked())
		 {
			 analysisGroup_.setHidden(false);	
			 analysisDataGroup_.setHidden(false);
		 }
		 else
		 {
			 analysisGroup_.setHidden(true);
			 analysisDataGroup_.setHidden(true);
					 
		 }
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
		setAnalysisGroup();
		analysisCK.dataChanged.addListener(new SignalListener<WEmptyEvent>()
                {
            public void notify(WEmptyEvent a)
            {
            	setAnalysisGroup();
            }
        });
		
		 List<AnalysisData> ads = new ArrayList<AnalysisData>();
	     for(AnalysisData ad : test_.getAnalysis().getAnalysisDatas())
	        {
	            ads.add(ad);
	        }
		iAnalysisDataEditableList_ = new ITestAnalysisDataList(this, test_);
		analysisDataList_= new EditableTable<AnalysisData>(analysisDataGroup_,iAnalysisDataEditableList_,ads);
		
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
        
        iAnalysisDataEditableList_.setTransaction(t);
        analysisDataList_.saveData();
        
        update(test_, t);
        t.commit();
        
        RegaDBMain.getApp().getTree().getTreeContent().testSelected.setSelectedItem(test_);
        RegaDBMain.getApp().getTree().getTreeContent().testSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().testSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().testView.selectNode();
       }
    
    @Override
    public void cancel()
    {
        redirectToView(RegaDBMain.getApp().getTree().getTreeContent().testSelected, RegaDBMain.getApp().getTree().getTreeContent().testView);
    }
    
    @Override
    public void deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        t.delete(test_);
        
        t.commit();
    }

    @Override
    public void redirectAfterDelete() 
    {
        RegaDBMain.getApp().getTree().getTreeContent().testSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().testSelected.setSelectedItem(null);
    }
}