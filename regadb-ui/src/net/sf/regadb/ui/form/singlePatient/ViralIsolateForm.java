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
import net.sf.regadb.service.wts.BlastAnalysis.UnsupportedGenomeException;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.service.wts.ServiceException.ServiceUnavailableException;
import net.sf.regadb.ui.form.query.querytool.widgets.WTabbedPane;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WString;

public class ViralIsolateForm extends ObjectForm<ViralIsolate>
{
	private ViralIsolateMainForm _mainForm;
	private ViralIsolateProteinForm proteinForm_;
    private ViralIsolateResistanceForm resistanceForm_;
    private ViralIsolateTransmittedResistanceForm transmittedResistanceForm_;
    private ViralIsolateSimilarityForm similarityForm;

    public ViralIsolateForm(WString formName, InteractionState interactionState, ObjectTreeNode<ViralIsolate> node, String sampleId, Date sampleDate){
        this(formName,interactionState,node,null);
        
        _mainForm.setSampleId(sampleId);
        _mainForm.setSampleDate(sampleDate);
    }
    
	public ViralIsolateForm(WString formName, InteractionState interactionState, ObjectTreeNode<ViralIsolate> node, ViralIsolate viralIsolate)
	{
		super(formName, interactionState, node, viralIsolate);
        
		if(RegaDBMain.getApp().isPatientInteractionAllowed(interactionState)){
			if(getInteractionState()==InteractionState.Adding){
				setObject(new ViralIsolate());
				getObject().getNtSequences().add(new NtSequence(getObject()));
			}
			else{
				for (NtSequence s : getObject().getNtSequences())
					RegaDBMain.getApp().createTransaction().refresh(s);
				RegaDBMain.getApp().createTransaction().refresh(getObject());
			}
	
			init();
		}
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
			
			Transaction t = RegaDBMain.getApp().createTransaction();
			if (t.getResRepTemplates().size() > 0)
				tabs.addTab(tr("form.viralIsolate.editView.tab.report"), new ViralIsolateReportForm(this));
			
			if(getObject() != null 
					&& getObject().getGenome() != null 
					&& RegaDBSettings.getInstance().getSequenceDatabaseConfig().isConfigured()) {
				similarityForm = new ViralIsolateSimilarityForm(this);
				tabs.addTab(tr("form.viralIsolate.editView.tab.similarity"), similarityForm);
			}
        }
        
        fillData();
        
        addControlButtons();
	}

	private void fillData()
	{
		if(getInteractionState()!=InteractionState.Adding)
		{
			RegaDBMain.getApp().createTransaction().refresh(getObject());
		}

        if(proteinForm_!=null)
        {
            proteinForm_.fillData(getObject());
        }
        _mainForm.fillData(getObject());
	}
    
    public ViralIsolate getViralIsolate()
    {
        return getObject();
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
			p.addViralIsolate(getObject());
		}

		_mainForm.saveData(t);

		//remove resistance tests
		Iterator<TestResult> i = getObject().getTestResults().iterator();
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

		getObject().setGenome(t.getGenome(genome.getOrganismName()));

		t.commit();

		_mainForm.startAnalysis(genome);
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
    }
    
    @Override
    public WString deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
        
        for(TestResult tr : getObject().getTestResults())
        	p.getTestResults().remove(tr);
        for(NtSequence nt : getObject().getNtSequences()) {
        	for(TestResult tr : nt.getTestResults())
        		p.getTestResults().remove(tr);
        	
        	if (RegaDBMain.getApp().getSequenceDb() != null) 
        		RegaDBMain.getApp().getSequenceDb().sequenceDeleted(nt);
        }
        
        p.getViralIsolates().remove(getObject());
        
        t.delete(getObject());
        
        t.commit();
        
        return null;
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
