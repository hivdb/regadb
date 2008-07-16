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
import net.sf.regadb.ui.form.singlePatient.StringComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.CheckBox;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TestTypeComboBox;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WLineEditEchoMode;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WWidget;
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
    private TestTypeComboBox testTypeCB;
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
    private ComboBox<AnalysisType> analysisTypeCB;
    private Label accountL;
    private TextField accountTF;
    private Label passwordL;
    private TextField passwordTF;
    private WPushButton refreshButton;
    private Label baseInputFileL;
    private ComboBox<String> baseInputFileCB;
    private Label baseOutputFileL;
    private ComboBox<String> baseOutputFileCB;
    private Label dataOutputFileL;
    private ComboBox<String> dataOutputFileCB;
    
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
	    testTypeCB= new TestTypeComboBox(getInteractionState(),this);
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
        
        testTypeCB.fill(t, false);
        
        t.commit();
        //main -end-
     
        //analysis -start-
        analysisGroup_= new WGroupBox(tr("form.testSettings.test.editView.analysisGroup"), this);
        analysisTable_  = new WTable(analysisGroup_);
        
        analysisTypeL = new Label(tr("form.testSettings.test.editView.analysis.analysisType"));
        analysisTypeCB = new ComboBox<AnalysisType>(getInteractionState(), this);
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
        
        if(getInteractionState() == InteractionState.Editing || getInteractionState() == InteractionState.Adding)
        {
            refreshButton = new WPushButton(tr("form.testSettings.test.editView.analysis.refreshButton"));
            
            refreshButton.clicked.addListener(new SignalListener<WMouseEvent>()
            {
                public void notify(WMouseEvent a)
                {
                    refreshForm();
                }
            });
            
            analysisTable_. putElementAt(analysisTable_.numRows()-1, 2, refreshButton);
        }
        
        baseInputFileL = new Label(tr("form.testSettings.test.editView.analysis.baseInputFile"));
        baseInputFileCB= new ComboBox<String>(getInteractionState(), this);
        baseInputFileCB.setMandatory(true);
        addLineToTable(analysisTable_,baseInputFileL, baseInputFileCB);

        baseOutputFileL = new Label(tr("form.testSettings.test.editView.analysis.baseOutputFile"));
        baseOutputFileCB= new ComboBox<String>(getInteractionState(), this);
        baseOutputFileCB.setMandatory(true);
        addLineToTable(analysisTable_,baseOutputFileL, baseOutputFileCB);
        
        dataOutputFileL = new Label(tr("form.testSettings.test.editView.analysis.dataOutputFile"));
        dataOutputFileCB= new ComboBox<String>(getInteractionState(), this);
        addLineToTable(analysisTable_, dataOutputFileL, dataOutputFileCB);
        
        t = RegaDBMain.getApp().createTransaction();
        
        List<AnalysisType> analysisTypes = t.getAnalysisTypes();
        
        for(AnalysisType at : analysisTypes)
        {
            analysisTypeCB.addItem(new DataComboMessage<AnalysisType>(at, at.getType()));
        }
        analysisTypeCB.sort();
        
        t.commit();
        
        analysisDataGroup_ = new WGroupBox(tr("form.testSettings.test.editView.analysisDataGroup"),this);
        analysisDataGroup_.setHidden(true);
	       
 	    addControlButtons();
	}
	
	private void filldata() 
	{
        if(getInteractionState() != InteractionState.Adding)
        {
            Transaction t = RegaDBMain.getApp().createTransaction();
            t.attach(test_);
            t.commit();
        }
        else
        {
            test_ = new Test();
        }
            
        testTF.setText(test_.getDescription());
        
        if(test_.getTestType() != null)
        {
            testTypeCB.selectItem(test_.getTestType());
        }
        
        if(test_.getAnalysis() != null)
        {
            analysisCK.setChecked(true);
            
            analysisTypeCB.selectItem(test_.getAnalysis().getAnalysisType().getType());
            urlTF.setText(test_.getAnalysis().getUrl());
            serviceTF.setText(test_.getAnalysis().getServiceName());
            accountTF.setText(test_.getAnalysis().getAccount());
            passwordTF.setText(test_.getAnalysis().getPassword());
        }
        else
        {
        	analysisGroup_.setHidden(true);
            analysisDataGroup_.setHidden(true);
        }
        
        setAnalysisGroup();
        
        fillComboBox();
	}
    
	private void setAnalysisGroup()
	{
		boolean checked = analysisCK.isChecked();
        
        analysisTypeCB.setMandatory(checked);
    	urlTF.setMandatory(checked);
    	serviceTF.setMandatory(checked);
    	accountTF.setMandatory(checked);
    	passwordTF.setMandatory(checked);
    	baseInputFileCB.setMandatory(checked);
    	baseOutputFileCB.setMandatory(checked);
    	
    	analysisGroup_.setHidden(!checked);
        	
        if (checked) {
        	refreshForm();
        }
        else {
            analysisDataGroup_.setHidden(true);
        }
	}
	
	private void fillComboBox() {
		if(test_.getAnalysis() != null) {
		baseInputFileCB.selectItem(test_.getAnalysis().getBaseinputfile());
        baseOutputFileCB.selectItem(test_.getAnalysis().getBaseoutputfile());
        dataOutputFileCB.selectItem(test_.getAnalysis().getDataoutputfile());
        }
	}
	
	private boolean dataFilled()
	{
		return !(urlTF.text().equals("") || serviceTF.text().equals(""));
	}
	
	private void refreshForm()
	{
		if ((this.getInteractionState() == InteractionState.Adding) || (this.getInteractionState() == InteractionState.Editing))
		{
			urlTF.flagValid();
			serviceTF.flagValid();
			accountTF.flagValid();
			passwordTF.flagValid();
			baseInputFileCB.flagValid();
			baseOutputFileCB.flagValid();
		}
		
		baseInputFileCB.setEnabled(false);
        baseOutputFileCB.setEnabled(false);
		
		baseInputFileCB.clearItems();
        baseOutputFileCB.clearItems();
        dataOutputFileCB.clearItems();
        
		dataOutputFileL.setHidden(true);
		dataOutputFileCB.setHidden(true);
        
        analysisDataGroup_.setHidden(true);
        
        if (checkForm())
        {
        	baseInputFileCB.setEnabled(true);
            baseOutputFileCB.setEnabled(true);
        	
        	analysisDataGroup_.setHidden(false);
        }
	}
	
	private boolean checkForm()
	{
		if(analysisCK.isChecked())
		{
			if(dataFilled())
	        {
	            WtsMetaClient wtsMC = new WtsMetaClient(urlTF.text());
	            
	            byte[] array = getServices(wtsMC);
	            
	            if (array != null)
	            {
	            	setBaseFields(wtsMC, array);
	            	showAnalysisData(wtsMC.parseInputNames(array));
	            	
	            	return true;
	            }
	            else
	            {
	            	return false;
	            }
	        }
			else
			{
				return false;
			}
		}
		else
		{
			return true;
		}
		
	}
    
    private byte[] getServices(WtsMetaClient wtsMC)
    {
    	byte[] array = null;
    	
    	try
        {
            array = wtsMC.getServiceDescription(serviceTF.text());
        }
        catch(RemoteException re)
        {
            if(this.getInteractionState() == InteractionState.Adding || this.getInteractionState() == InteractionState.Editing)
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
        } 
        catch (MalformedURLException e) 
        {
            if(this.getInteractionState() == InteractionState.Adding || this.getInteractionState() == InteractionState.Editing)
            {
                urlTF.flagErroneous();
            }
        }
    	
    	return array;
    }
    
    private void setBaseFields(WtsMetaClient wtsMC, byte[] array)
    {
    	for(String input : wtsMC.parseInputNames(array))
        {
            baseInputFileCB.addItem(new StringComboMessage(input));
        }
        baseInputFileCB.sort();
        
        for(String output : wtsMC.parseOutputNames(array))
        {
            baseOutputFileCB.addItem(new StringComboMessage(output));
        }
        baseOutputFileCB.sort();
        
        if (wtsMC.parseOutputNames(array).size() > 1) {
        	for(String output : wtsMC.parseOutputNames(array))
            {
                dataOutputFileCB.addItem(new StringComboMessage(output));
            }
            dataOutputFileCB.sort();
        	
        	dataOutputFileL.setHidden(false);
        	dataOutputFileCB.setHidden(false);
        }
    }
    
    private void showAnalysisData(ArrayList<String> selectableItems)
    {
        if(ianalysisDataET == null)
        {
            ArrayList<AnalysisData> data = new ArrayList<AnalysisData>();
            
            if(test_.getAnalysis() != null)
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
        	for (WWidget w : analysisDataET.getAllWidgets(0))
    		{
        		if (!((TextField)w).text().equals(baseInputFileCB.currentValue()))
        		{
        			analysisDataET.removeAllRows();
        		}
        	}
        }
        
        if(selectableItems != null)
        {
            ianalysisDataET.setInputFileNames(selectableItems);
            analysisDataET.refreshAddRow();
        }
    }

	@Override
	public void saveData()
	{
		if (checkForm())
		{
			Transaction t = RegaDBMain.getApp().createTransaction();
	        if(getInteractionState() != InteractionState.Adding)
	        {
	            t.attach(test_);
	        }
	        
	        TestType tt = testTypeCB.currentValue();
	        t.attach(tt);

	        test_.setDescription(testTF.text());
	        test_.setTestType(tt);
	        
	        Analysis analysis = null;
	        
	        if(analysisCK.isChecked())
	        {
	            if(test_.getAnalysis()==null)
	            {
	                test_.setAnalysis(new Analysis());
	            }
	            
	            test_.getAnalysis().setAnalysisType(analysisTypeCB.currentValue());
	            test_.getAnalysis().setUrl(urlTF.text());
	            test_.getAnalysis().setServiceName(serviceTF.text());
	            test_.getAnalysis().setAccount(accountTF.text());
	            test_.getAnalysis().setPassword(passwordTF.text());
	            test_.getAnalysis().setBaseinputfile(baseInputFileCB.currentValue());
	            test_.getAnalysis().setBaseoutputfile(baseOutputFileCB.currentValue());
	            
	            if (!dataOutputFileCB.isHidden()) {
	            	test_.getAnalysis().setDataoutputfile(dataOutputFileCB.currentValue());
	            }
	            else {
	            	test_.getAnalysis().setDataoutputfile(null);
	            }
	            
	            ianalysisDataET.setTransaction(t);
	            ianalysisDataET.setAnalysis(test_.getAnalysis());
	            analysisDataET.saveData();
	            
	            if(test_.getAnalysis().getAnalysisIi() == null)
	            {
	                t.save(test_.getAnalysis());
	            }
	            else
	            {
	                update(test_.getAnalysis(), t);
	            }
	        }
	        else
	        {
	        	analysis = test_.getAnalysis();
	        	
	            test_.setAnalysis(null);
	        }

	        update(test_, t);
			t.commit();
	        
	        if (analysis != null)
	        {
	        	t = RegaDBMain.getApp().createTransaction();
	        	
	        	t.delete(analysis);
	        	
	        	t.commit();
	        }
	        
	        RegaDBMain.getApp().getTree().getTreeContent().testSelected.setSelectedItem(test_);
	        redirectToView(RegaDBMain.getApp().getTree().getTreeContent().testSelected, RegaDBMain.getApp().getTree().getTreeContent().testView);
		}
		else
		{
			MessageBox.showWarningMessage(tr("form.testSetting.test.warning"));
		}
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
    public WMessage deleteObject()
    {
    	Transaction t = RegaDBMain.getApp().createTransaction();
    	
    	try
    	{
    		if(test_.getAnalysis()!=null)
    		{
    	        t.delete(test_.getAnalysis());        
    		}
    		
    		t.delete(test_);
	        
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
        RegaDBMain.getApp().getTree().getTreeContent().testSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().testSelected.setSelectedItem(null);
    }
}