package net.sf.regadb.ui.form.singlePatient;

import java.util.Iterator;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.form.query.querytool.widgets.WTabbedPane;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.StandardButton;
import eu.webtoolkit.jwt.WMessageBox;
import eu.webtoolkit.jwt.WString;

public class ViralIsolateForm extends FormWidget
{
	private ViralIsolate viralIsolate_;

	private ViralIsolateMainForm _mainForm;
	private ViralIsolateProteinForm proteinForm_;
    private ViralIsolateResistanceForm resistanceForm_;
    private ViralIsolateReportForm reportForm_;

	public ViralIsolateForm(InteractionState interactionState, WString formName, ViralIsolate viralIsolate)
	{
		super(formName, interactionState);
		viralIsolate_ = viralIsolate;
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        if(getInteractionState()==InteractionState.Adding)
        {
            Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
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
        
        _mainForm.saveData(t);
        
        //remove resistance tests
        Genome genome = ViralIsolateFormUtils.getGenome(viralIsolate_);
        if(genome != null){
            for(Iterator<TestResult> i = viralIsolate_.getTestResults().iterator(); i.hasNext();)
            {
                TestResult test = i.next();
                if(Equals.isSameTestType(StandardObjects.getGssTestType(genome),test.getTest().getTestType()))
                {
                    if(test.getTest().getAnalysis()!=null)
                    {
                        i.remove();
                        t.delete(test);
                    }
                }
            }
        }
        
        update(viralIsolate_, t);
        t.commit();
        
        _mainForm.startAnalysis();
             
        RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected.setSelectedItem(viralIsolate_);
        redirectToView(RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected, RegaDBMain.getApp().getTree().getTreeContent().viralIsolateView);
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
        
        Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
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
            final WMessageBox cmb = UIUtils.createYesNoMessageBox(this, tr("form.confirm.duplicate.viralIsolate.sampleId"));
            cmb.buttonClicked.addListener(this, new Signal1.Listener<StandardButton>(){
				@Override
				public void trigger(StandardButton sb) {
					cmb.destroy();
					if(sb==StandardButton.Yes) {
						doConfirm();
					}
				}
            });
            cmb.show();
        }
        else{
            doConfirm();
        }
    }
    
    public void doConfirm(){
        super.confirmAction();
    }
}
