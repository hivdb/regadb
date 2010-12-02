package net.sf.regadb.ui.form.singlePatient;

import java.util.Date;
import java.util.Iterator;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
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
    private ViralIsolateTransmittedResistanceForm transmittedResistanceForm_;
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
        
        if(getInteractionState()==InteractionState.Adding)
        {
            viralIsolate_ = new ViralIsolate();
            viralIsolate_.getNtSequences().add(new NtSequence(viralIsolate_));
        }
        else
        {
        	RegaDBMain.getApp().createTransaction().refresh(viralIsolate_);
        }

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
			TestType gssTestType = StandardObjects.getTestType(StandardObjects.getGssDescription(), getViralIsolate().getGenome());
			if (gssTestType != null) {
				resistanceForm_ = new ViralIsolateResistanceForm(this);
				tabs.addTab(tr("form.viralIsolate.editView.tab.resistance"), resistanceForm_);
			}
			TestType tdrTestType = StandardObjects.getTestType(StandardObjects.getTDRDescription(), getViralIsolate().getGenome());
			if (tdrTestType != null) {
				transmittedResistanceForm_ = new ViralIsolateTransmittedResistanceForm(this);
				tabs.addTab(tr("form.viralIsolate.editView.tab.transmittedResistance"), transmittedResistanceForm_);
			}
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
			RegaDBMain.getApp().createTransaction().refresh(viralIsolate_);
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

		_mainForm.confirmSequences(t);

		Genome genome = blast(_mainForm.ntSequenceForms.get(0).getNtSequence());
		if(genome == null)
			return;

		if (getInteractionState()==InteractionState.Adding) {
			Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
			t.attach(p);
			p.addViralIsolate(viralIsolate_);
		}

		_mainForm.saveData(t);

		//remove resistance tests
		Iterator<TestResult> i = viralIsolate_.getTestResults().iterator();
		while (i.hasNext()) {
			TestResult test = i.next();
			String description = test.getTest().getTestType().getDescription();
			if (description.equals(StandardObjects.getGssDescription()) ||
					description.equals(StandardObjects.getTDRDescription())) {
				i.remove();
				RegaDBMain.getApp().getSelectedPatient().getTestResults().remove(test);
				t.delete(test);
			}
		}

		viralIsolate_.setGenome(t.getGenome(genome.getOrganismName()));

		t.commit();

		_mainForm.startAnalysis(genome);

		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode().setSelectedItem(viralIsolate_);
		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode().refresh();
		redirectToView(
				RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode().getSelectedActionItem(),
				RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode().getViewActionItem());
	}
	
	private Genome blast(NtSequence ntseq){
	    Genome genome = null;
	    //TODO check ALL sequences?
	    
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
            redirectToSelect(
            		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode(),
            		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode().getSelectActionItem());
        }
        else
        {
            redirectToView(
            		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode().getSelectedActionItem(),
            		RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode().getViewActionItem());
        } 
    }
    
    @Override
    public WString deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
        
        for(TestResult tr : viralIsolate_.getTestResults())
        	p.getTestResults().remove(tr);
        for(NtSequence nt : viralIsolate_.getNtSequences()) {
        	for(TestResult tr : nt.getTestResults())
        		p.getTestResults().remove(tr);
        	
        	if (RegaDBMain.getApp().getSequenceDb() != null) 
        		RegaDBMain.getApp().getSequenceDb().sequenceDeleted(nt);
        }
        
        p.getViralIsolates().remove(viralIsolate_);
        
        t.delete(viralIsolate_);
        
        t.commit();
        
        return null;
    }

    @Override
    public void redirectAfterDelete() 
    {
    	RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode().refresh();
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode()
        	.getSelectActionItem().selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getViralIsolateTreeNode()
        	.setSelectedItem(null);
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
