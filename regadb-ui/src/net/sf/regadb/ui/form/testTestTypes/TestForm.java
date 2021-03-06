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
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.forms.fields.CheckBox;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TestTypeComboBox;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.wts.client.meta.WtsMetaClient;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WWidget;

public class TestForm extends ObjectForm<Test>
{
	//Frame 
	private WGroupBox mainFrameGroup_;
    private FormTable mainFrameTable_;
    private Label testL ;
    private TextField testTF ;
    private Label testTypeL;
    private TestTypeComboBox testTypeCB;
    private Label analysisL;
    private CheckBox analysisCK;
    
    //analysis group
    private WGroupBox analysisGroup_;
    private FormTable analysisTable_;
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
    
    private String prevUrl = null;
    private String prevService = null;
  
	public TestForm(WString formName, InteractionState interactionState, ObjectTreeNode<Test> node, Test test ) 
	{
		super(formName, interactionState, node, test);
		
		init();
		filldata();
	}

	private void init() 
	{
        //main -start-
		mainFrameGroup_= new WGroupBox(tr("form.testSettings.test.editView.general"), this);
		mainFrameTable_ = new FormTable(mainFrameGroup_);
		
		testL = new Label(tr("form.testSettings.test.editView.test"));
        testTF = new TextField(getInteractionState(), this);
        testTF.setMandatory(true);
        mainFrameTable_.addLineToTable(testL, testTF);
        
	    testTypeL=new Label(tr("form.testSettings.test.editView.testType"));
	    testTypeCB= new TestTypeComboBox(getInteractionState(),this);
	    testTypeCB.setMandatory(true);
	    mainFrameTable_.addLineToTable(testTypeL, testTypeCB);
	    
	    analysisL = new Label(tr("form.testSettings.test.editView.analysis"));
	    analysisCK = new CheckBox(getInteractionState(),this);
	    mainFrameTable_.addLineToTable(analysisL, analysisCK);
	    
        analysisCK.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
        {
            public void trigger(WMouseEvent a)
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
        analysisTable_  = new FormTable(analysisGroup_);
        
        analysisTypeL = new Label(tr("form.testSettings.test.editView.analysis.analysisType"));
        analysisTypeCB = new ComboBox<AnalysisType>(getInteractionState(), this);
        analysisTypeCB.setMandatory(true);
        analysisTable_.addLineToTable(analysisTypeL, analysisTypeCB);
        
        urlL = new Label(tr("form.testSettings.test.editView.analysis.URL"));
        urlTF = new TextField(getInteractionState(), this);
        urlTF.setMandatory(true);
        analysisTable_.addLineToTable(urlL, urlTF);
        
        serviceL = new Label(tr("form.testSettings.test.editView.analysis.service.name"));
        serviceTF = new TextField(getInteractionState(), this);
        serviceTF.setMandatory(true);
        analysisTable_.addLineToTable(serviceL, serviceTF);
        
        accountL = new Label(tr("form.testSettings.test.editView.analysis.account"));
        accountTF= new TextField(getInteractionState(), this);
        accountTF.setMandatory(true);
        analysisTable_.addLineToTable(accountL, accountTF);
        
        passwordL = new Label(tr("form.testSettings.test.editView.analysis.password"));
        passwordTF = new TextField(getInteractionState(), this);
        passwordTF.setEchomode(WLineEdit.EchoMode.Password);
        passwordTF.setMandatory(true);
        analysisTable_.addLineToTable(passwordL, passwordTF);
        
        if(getInteractionState() == InteractionState.Editing || getInteractionState() == InteractionState.Adding)
        {
            refreshButton = new WPushButton(tr("form.testSettings.test.editView.analysis.refreshButton"));
            Label refreshL = new Label(tr("form.testSettings.test.editView.analysis.refreshButton"));
            
            refreshButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
            {
                public void trigger(WMouseEvent a)
                {
                    refreshForm();
                }
            });
            analysisTable_.addLineToTable(refreshL, refreshButton);
        }
        
        baseInputFileL = new Label(tr("form.testSettings.test.editView.analysis.baseInputFile"));
        baseInputFileCB= new ComboBox<String>(getInteractionState(), this);
        baseInputFileCB.setMandatory(true);
        analysisTable_.addLineToTable(baseInputFileL, baseInputFileCB);

        baseOutputFileL = new Label(tr("form.testSettings.test.editView.analysis.baseOutputFile"));
        baseOutputFileCB= new ComboBox<String>(getInteractionState(), this);
        baseOutputFileCB.setMandatory(true);
        analysisTable_.addLineToTable(baseOutputFileL, baseOutputFileCB);
        
        dataOutputFileL = new Label(tr("form.testSettings.test.editView.analysis.dataOutputFile"));
        dataOutputFileCB= new ComboBox<String>(getInteractionState(), this);
        analysisTable_.addLineToTable(dataOutputFileL, dataOutputFileCB);
        
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
            t.attach(getObject());
            t.commit();
        }
        else
        {
        	setObject(new Test());
        }
            
        testTF.setText(getObject().getDescription());
        
        if(getObject().getTestType() != null)
        {
            testTypeCB.selectItem(getObject().getTestType());
        }
        
        if(getObject().getAnalysis() != null)
        {
            analysisCK.setChecked(true);
            
            analysisTypeCB.selectItem(getObject().getAnalysis().getAnalysisType().getType());
            urlTF.setText(getObject().getAnalysis().getUrl());
            serviceTF.setText(getObject().getAnalysis().getServiceName());
            accountTF.setText(getObject().getAnalysis().getAccount());
            passwordTF.setText(getObject().getAnalysis().getPassword());
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
		if(getObject().getAnalysis() != null) {
		baseInputFileCB.selectItem(getObject().getAnalysis().getBaseinputfile());
        baseOutputFileCB.selectItem(getObject().getAnalysis().getBaseoutputfile());
        dataOutputFileCB.selectItem(getObject().getAnalysis().getDataoutputfile());
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
				if(urlTF.text().equals(prevUrl) && serviceTF.text().equals(prevService)){
					return true;
				}
				else{
		            WtsMetaClient wtsMC = new WtsMetaClient(RegaDBSettings.getInstance().getInstituteConfig().getWtsUrl(urlTF.text()));
		            
		            byte[] array = getServices(wtsMC);
		            
		            if (array != null)
		            {
		            	setBaseFields(wtsMC, array);
		            	showAnalysisData(wtsMC.parseInputNames(array));
		            	
		            	prevUrl = urlTF.text();
		            	prevService = serviceTF.text();
		            	return true;
		            }
		            else
		            {
		            	return false;
		            }
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
            
            if(getObject().getAnalysis() != null)
            {
                for(AnalysisData ad : getObject().getAnalysis().getAnalysisDatas())
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
	            t.attach(getObject());
	        }
	        
	        TestType tt = testTypeCB.currentValue();
	        t.attach(tt);

	        getObject().setDescription(testTF.text());
	        getObject().setTestType(tt);
	        
	        Analysis analysis = null;
	        
	        if(analysisCK.isChecked())
	        {
	            if(getObject().getAnalysis()==null)
	            {
	            	getObject().setAnalysis(new Analysis());
	            }
	            
	            getObject().getAnalysis().setAnalysisType(analysisTypeCB.currentValue());
	            getObject().getAnalysis().setUrl(urlTF.text());
	            getObject().getAnalysis().setServiceName(serviceTF.text());
	            getObject().getAnalysis().setAccount(accountTF.text());
	            getObject().getAnalysis().setPassword(passwordTF.text());
	            getObject().getAnalysis().setBaseinputfile(baseInputFileCB.currentValue());
	            getObject().getAnalysis().setBaseoutputfile(baseOutputFileCB.currentValue());
	            
	            if (!dataOutputFileCB.isHidden()) {
	            	getObject().getAnalysis().setDataoutputfile(dataOutputFileCB.currentValue());
	            }
	            else {
	            	getObject().getAnalysis().setDataoutputfile(null);
	            }
	            
	            ianalysisDataET.setTransaction(t);
	            ianalysisDataET.setAnalysis(getObject().getAnalysis());
	            analysisDataET.saveData();
	            
	            if(getObject().getAnalysis().getAnalysisIi() == null)
	            {
	                t.save(getObject().getAnalysis());
	            }
	            else
	            {
	                update(getObject().getAnalysis(), t);
	            }
	        }
	        else
	        {
	        	analysis = getObject().getAnalysis();
	        	
	        	getObject().setAnalysis(null);
	        }

	        update(getObject(), t);
			t.commit();
	        
	        if (analysis != null)
	        {
	        	t = RegaDBMain.getApp().createTransaction();
	        	
	        	t.delete(analysis);
	        	
	        	t.commit();
	        }
		}
		else
		{
			UIUtils.showWarningMessageBox(this, tr("form.testSetting.test.warning"));
		}
    }
    
    @Override
    public void cancel()
    {
    }
    
    @Override
    public WString deleteObject()
    {
    	Transaction t = RegaDBMain.getApp().createTransaction();
    	
    	try
    	{
    		if(getObject().getAnalysis()!=null)
    		{
    	        t.delete(getObject().getAnalysis());        
    		}
    		
    		t.delete(getObject());
	        
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
}