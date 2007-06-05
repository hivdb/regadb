package net.sf.regadb.ui.form.testTestTypes;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.datatable.testSettings.IAnalysisDataEditableTable;
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
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WLineEditEchoMode;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.wts.client.meta.WtsMetaClient;

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
    private Label serviceL;
    private TextField serviceTF;
    private Label urlL;
    private TextField urlTF;
    private Label analysisTypeL;
    private ComboBox analysisTypeCB;
    private Label accountL;
    private TextField accountTF;
    private Label passwordL;
    private TextField passwordTF;
    private WPushButton refreshButton;
    private Label baseInputFileL;
    private ComboBox baseInputFileCB;
    private Label baseOutputFileL;
    private ComboBox baseOutputFileCB;
    
    //analysis data group
    private WGroupBox analysisDataGroup_;
    private EditableTable<AnalysisData> analysisDataET;
    private IAnalysisDataEditableTable ianalysisDataET;
    //analysis data group -end-
  
	public TestForm(InteractionState interactionState, WMessage formName, Test test ) 
	{
		super(formName, interactionState);
		
		test_ = test;
		
		init();
		filldata();
	}

	private void init() 
	{
        //main -start-
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
        analysisCK.clicked.addListener(new SignalListener<WMouseEvent>()
        {
            public void notify(WMouseEvent a)
            {
                  setAnalysisGroup();
            }
        });
        
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
        //main -end-
     
        //analysis -start-
        analysisGroup_= new WGroupBox(tr("form.testSettings.test.editView.analysisGroup"), this);
        analysisTable_  = new WTable(analysisGroup_);
        
        analysisTypeL = new Label(tr("form.testSettings.test.editView.analysis.analysisType"));
        analysisTypeCB = new ComboBox(getInteractionState(), this);
        analysisTypeCB.setMandatory(true);
        addLineToTable(analysisTable_,analysisTypeL, analysisTypeCB);
        urlL = new Label(tr("form.testSettings.test.editView.analysis.URL"));
        urlTF = new TextField(getInteractionState(), this);
        urlTF.setMandatory(true);
        addLineToTable(analysisTable_,urlL, urlTF);
        serviceL = new Label(tr("form.testSettings.test.editView.analysis.service.name"));
        serviceTF = new TextField(getInteractionState(), this);
        serviceTF.setMandatory(true);
        addLineToTable(analysisTable_,serviceL, serviceTF);
        accountL = new Label(tr("form.testSettings.test.editView.analysis.account"));
        accountTF= new TextField(getInteractionState(), this);
        accountTF.setMandatory(true);
        addLineToTable(analysisTable_,accountL, accountTF);
        passwordL = new Label(tr("form.testSettings.test.editView.analysis.password"));
        passwordTF = new TextField(getInteractionState(), this);
        passwordTF.setEchomode(WLineEditEchoMode.Password);
        passwordTF.setMandatory(true);
        addLineToTable(analysisTable_,passwordL, passwordTF);
        if(getInteractionState()==InteractionState.Editing || getInteractionState()==InteractionState.Adding)
        {
            refreshButton = new WPushButton(tr("form.testSettings.test.editView.analysis.refreshButton"));
            refreshButton.clicked.addListener(new SignalListener<WMouseEvent>()
            {
                public void notify(WMouseEvent a)
                {
                    setBaseFields();
                }
            });
            analysisTable_. putElementAt(analysisTable_.numRows()-1, 2, refreshButton);
        }
        
        baseInputFileL = new Label(tr("form.testSettings.test.editView.analysis.baseInputFile"));
        baseInputFileCB= new ComboBox(getInteractionState(), this);
        baseInputFileCB.setMandatory(true);
        addLineToTable(analysisTable_,baseInputFileL, baseInputFileCB);

        baseOutputFileL = new Label(tr("form.testSettings.test.editView.analysis.baseOutputFile"));
        baseOutputFileCB= new ComboBox(getInteractionState(), this);
        baseOutputFileCB.setMandatory(true);
        addLineToTable(analysisTable_,baseOutputFileL, baseOutputFileCB);
        
        toSelect = null;
        t = RegaDBMain.getApp().createTransaction();
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
        //analysis -end-
        
        //analysis data
        analysisDataGroup_ = new WGroupBox(tr("form.testSettings.test.editView.analysisDataGroup"),this);
        analysisDataGroup_.setHidden(true);
        //analysis data -end-
	       
 	    addControlButtons();
	}
    
	private void setAnalysisGroup()
	{
        boolean state = !analysisCK.isChecked();
        
        analysisGroup_.setHidden(state);
        analysisTypeCB.setHidden(state);
        urlTF.setHidden(state);
        serviceTF.setHidden(state);
        accountTF.setHidden(state);
        passwordTF.setHidden(state);
        baseInputFileCB.setHidden(state);
        baseOutputFileCB.setHidden(state);
	}
    
    private void setBaseFields()
    {
        boolean state = urlTF.text().equals("") || accountTF.text().equals("") || passwordTF.text().equals("") || serviceTF.text().equals("");
        
        baseInputFileCB.setEnabled(!state);
        baseOutputFileCB.setEnabled(!state);
        
        baseInputFileCB.clearItems();
        baseOutputFileCB.clearItems();
        
        urlTF.flagValid();
        serviceTF.flagValid();
        
        analysisDataGroup_.setHidden(true);
        
        if(!state)
        {
            WtsMetaClient wtsMC = new WtsMetaClient(urlTF.text());
            byte[] array = null;
            try
            {
                array = wtsMC.getServiceDescription(serviceTF.text());
            }
            catch(RemoteException re)
            {
                if(re.getMessage().equals("No client transport named 'null' found!"))
                {
                    urlTF.flagErroneous();
                }
                else if(re.getMessage().contains("java.net.UnknownHostException"))
                {
                    urlTF.flagErroneous();
                }
                else if(re.getMessage().equals("java.rmi.RemoteException: Service " + '"' + serviceTF.text() + '"' +" is not available"))
                {
                    serviceTF.flagErroneous();
                }
                else
                {
                    serviceTF.flagErroneous();
                    urlTF.flagErroneous();
                }
            } 
            catch (MalformedURLException e) 
            {
                urlTF.flagErroneous();
            }
            
            if(array!=null)
            {
                for(String input : wtsMC.parseInputNames(array))
                {
                    baseInputFileCB.addItem(lt(input));
                }
                
                for(String output : wtsMC.parseOutputNames(array))
                {
                    baseOutputFileCB.addItem(lt(output));
                }
                
                showAnalysisData(wtsMC.parseInputNames(array));
            }
        }
    }
    
    private void showAnalysisData(ArrayList<String> selectableItems)
    {
        if(ianalysisDataET==null)
        {
            ArrayList<AnalysisData> data = new ArrayList<AnalysisData>();
            if(test_.getAnalysis()!=null)
            {
                for(AnalysisData ad : test_.getAnalysis().getAnalysisDatas())
                {
                    data.add(ad);
                }
            }
            ianalysisDataET = new IAnalysisDataEditableTable(this);
            analysisDataET = new EditableTable<AnalysisData>(analysisDataGroup_, ianalysisDataET, data);
        }
        else
        {
            analysisDataET.removeAllRows();
        }
        
        if(selectableItems!=null)
        {
            ianalysisDataET.setInputFileNames(selectableItems);
            analysisDataET.refreshAddRow();
        }
        
        analysisDataGroup_.setHidden(false);
    }
	
	private void filldata() 
	{
        if(getInteractionState()!=InteractionState.Adding)
        {
            Transaction t = RegaDBMain.getApp().createTransaction();
            
            t.attach(test_);
            t.commit();
        }
        else
        {
            test_=new Test();
        }
            
        testTF.setText(test_.getDescription());
        if(test_.getTestType()!=null)
        {
            testTypeCB.selectItem(new DataComboMessage<TestType>(test_.getTestType(), test_.getTestType().getDescription()));
        }
        
        if(test_.getAnalysis()!=null)
        {
            analysisCK.setChecked(true);
            
            analysisTypeCB.selectItem(new DataComboMessage<AnalysisType>(test_.getAnalysis().getAnalysisType(), test_.getAnalysis().getAnalysisType().getType()));
            urlTF.setText(test_.getAnalysis().getUrl());
            serviceTF.setText(test_.getAnalysis().getServiceName());
            accountTF.setText(test_.getAnalysis().getAccount());
            passwordTF.setText(test_.getAnalysis().getPassword());
            
            if(getInteractionState()!=InteractionState.Viewing)
                setBaseFields();
            else
                showAnalysisData(null);
            
            baseInputFileCB.selectItem(lt(test_.getAnalysis().getBaseinputfile()));
            baseOutputFileCB.selectItem(lt(test_.getAnalysis().getBaseoutputfile()));
        }
        
        setAnalysisGroup();
	}

	@Override
	public void saveData()
	{
		Transaction t = RegaDBMain.getApp().createTransaction();
        if(getInteractionState()!=InteractionState.Adding)
        {
            t.attach(test_);
        }
        
        TestType tt = ((DataComboMessage<TestType>)testTypeCB.currentText()).getValue();
        t.attach(tt);

        test_.setDescription(testTF.text());
        test_.setTestType(tt);
        
        if(analysisCK.isChecked())
        {
            if(test_.getAnalysis()==null)
            {
                test_.setAnalysis(new Analysis());
            }
            
            test_.getAnalysis().setAnalysisType(((DataComboMessage<AnalysisType>)analysisTypeCB.currentText()).getValue());
            test_.getAnalysis().setUrl(urlTF.text());
            test_.getAnalysis().setServiceName(serviceTF.text());
            test_.getAnalysis().setAccount(accountTF.text());
            test_.getAnalysis().setPassword(passwordTF.text());
            test_.getAnalysis().setBaseinputfile(baseInputFileCB.currentText().value());
            test_.getAnalysis().setBaseoutputfile(baseOutputFileCB.currentText().value());
            
            ianalysisDataET.setTransaction(t);
            ianalysisDataET.setAnalysis(test_.getAnalysis());
            analysisDataET.saveData();
            
            update(test_.getAnalysis(), t);
        }
        else
        {
            test_.setAnalysis(null);
        }

        update(test_, t);
        t.commit();
        
        RegaDBMain.getApp().getTree().getTreeContent().testSelected.setSelectedItem(test_);
        redirectToView(RegaDBMain.getApp().getTree().getTreeContent().testSelected, RegaDBMain.getApp().getTree().getTreeContent().testView);
    }
    
    @Override
    public void cancel()
    {
        if(getInteractionState()==InteractionState.Adding)
        {
            redirectToSelect(RegaDBMain.getApp().getTree().getTreeContent().test, RegaDBMain.getApp().getTree().getTreeContent().testSelect);
        }
        else
        {
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().testSelected, RegaDBMain.getApp().getTree().getTreeContent().testView);
        } 
    }
    
    @Override
    public void deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        if(test_.getAnalysis()!=null)
        t.delete(test_.getAnalysis());        
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