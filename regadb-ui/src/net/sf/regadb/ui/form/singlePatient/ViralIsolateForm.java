package net.sf.regadb.ui.form.singlePatient;

import java.util.Date;
import java.util.Iterator;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.BlastAnalysis;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.service.wts.BlastAnalysis.UnsupportedGenomeException;
import net.sf.regadb.service.wts.ServiceException.ServiceUnavailableException;
import net.sf.regadb.ui.form.query.querytool.widgets.WTabbedPane;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import eu.webtoolkit.jwt.WString;

public class ViralIsolateForm extends FormWidget
{
	private ViralIsolate viralIsolate_;

	private ViralIsolateMainForm _mainForm;
	private ViralIsolateProteinForm proteinForm_;
    private ViralIsolateResistanceForm resistanceForm_;
    private ViralIsolateReportForm reportForm_;

    public ViralIsolateForm(InteractionState interactionState, WString formName, String sampleId, Date sampleDate){
        this(interactionState,formName,null);
        
        _mainForm.setSampleId(sampleId);
        _mainForm.setSampleDate(sampleDate);
    }
    
	public ViralIsolateForm(InteractionState interactionState, WString formName, ViralIsolate viralIsolate)
	{
		super(formName, interactionState);
		viralIsolate_ = viralIsolate;
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        if(getInteractionState()==InteractionState.Adding)
        {
            Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
            t.attach(p);
            viralIsolate_ = p.createViralIsolate();
            viralIsolate_.getNtSequences().add(new NtSequence(viralIsolate_));
        }
        else
        {
            t.refresh(viralIsolate_);
        }
        t.commit();

		init();
		
	}

	public void init()
	{
		WTabbedPane tabs = new WTabbedPane(this);
		_mainForm = new ViralIsolateMainForm(this);
		tabs.addTab(tr("form.viralIsolate.editView.tab.viralIsolate"), _mainForm);
		
        if(getInteractionState()==InteractionState.Viewing) {
	        proteinForm_ = new ViralIsolateProteinForm(this);
			tabs.addTab(tr("form.viralIsolate.editView.tab.proteins"), proteinForm_);
	        resistanceForm_ = new ViralIsolateResistanceForm(this);
			tabs.addTab(tr("form.viralIsolate.editView.tab.resistance"), resistanceForm_);
	        reportForm_ = new ViralIsolateReportForm(this);
			tabs.addTab(tr("form.viralIsolate.editView.tab.report"), reportForm_);
        }
        
        fillData();
        
        addControlButtons();
	}

	private void fillData()
	{
		if(getInteractionState()!=InteractionState.Adding)
		{
			Transaction t;
			t = RegaDBMain.getApp().createTransaction();
	        t.attach(viralIsolate_);
	        t.commit();
		}

        if(proteinForm_!=null)
        {
            proteinForm_.fillData(viralIsolate_);
        }
        _mainForm.fillData(viralIsolate_);
	}
    
    public ViralIsolate getViralIsolate()
    {
        return viralIsolate_;
    }
	
	@Override
	public void saveData()
	{                
        Transaction t = RegaDBMain.getApp().createTransaction();
        t.attach(viralIsolate_);
        
        _mainForm.confirmSequence();
        Genome genome = blast();
        if(genome == null)
            return;
        
        _mainForm.saveData(t);
        
        //remove resistance tests
        Genome oldgenome = ViralIsolateFormUtils.getGenome(viralIsolate_);
        
        Iterator<TestResult> i = viralIsolate_.getTestResults().iterator();
		while (i.hasNext()) {
			TestResult test = i.next();
			if (test.getTest().getTestType().getDescription().equals(
					StandardObjects.getGssDescription())) {
				i.remove();
				t.delete(test);
			}
		}
        
        update(viralIsolate_, t);
        t.commit();
        
        _mainForm.startAnalysis(genome);
             
        RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected.setSelectedItem(viralIsolate_);
        redirectToView(RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected, RegaDBMain.getApp().getTree().getTreeContent().viralIsolateView);
	}
	
	private Genome blast(){
	    Genome genome = null;
	    NtSequence ntseq = ((DataComboMessage<NtSequence>)_mainForm.getSeqComboBox().getCurrentText()).getDataValue();
	    
        if(ntseq != null){
            BlastAnalysis blastAnalysis = new BlastAnalysis(ntseq, RegaDBMain.getApp().getLogin().getUid());
            try{
                blastAnalysis.launch();
                genome = blastAnalysis.getGenome();
            }
            catch(UnsupportedGenomeException e){
                UIUtils.showWarningMessageBox(this, tr("form.viralIsolate.warning.unsupportedGenome"));
            }
            catch(ServiceUnavailableException e){
                UIUtils.showWarningMessageBox(this, tr("msg.warning.serviceUnavailable"));
            }
            catch(ServiceException e){
                e.printStackTrace();
            }            
        }
        return genome;
	}
    
    @Override
    public void cancel()
    {
        if(getInteractionState()==InteractionState.Adding)
        {
        	deleteObject();
        	
            redirectToSelect(RegaDBMain.getApp().getTree().getTreeContent().viralIsolates, RegaDBMain.getApp().getTree().getTreeContent().viralIsolatesSelect);
        }
        else
        {
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected, RegaDBMain.getApp().getTree().getTreeContent().viralIsolateView);
        } 
    }
    
    @Override
    public WString deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
        p.getViralIsolates().remove(viralIsolate_);
        
        t.delete(viralIsolate_);
        
        t.commit();
        
        return null;
    }

    @Override
    public void redirectAfterDelete() 
    {
        RegaDBMain.getApp().getTree().getTreeContent().viralIsolatesSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected.setSelectedItem(null);
    }
    
    @Override
    public WString leaveForm() {
        if(proteinForm_!=null && proteinForm_.refreshAlignmentsTimer_!=null)
            proteinForm_.refreshAlignmentsTimer_.stop();
        return super.leaveForm();
    }
    
    @Override
    public void confirmAction()
    {
        if(!_mainForm.checkSampleId()){
            UIUtils.showWarningMessageBox(this, tr("form.confirm.duplicate.viralIsolate.sampleId"));
        }
        else{
            doConfirm();
        }
    }
    
    public void doConfirm(){
        super.confirmAction();
    }
}
