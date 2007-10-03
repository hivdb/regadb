package net.sf.regadb.ui.form.singlePatient;

import java.util.Iterator;
import java.util.List;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.AnalysisPool;
import net.sf.regadb.service.wts.ResistanceInterpretationAnalysis;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.WStackedWidget;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.core.utils.WLength;
import net.sf.witty.wt.core.utils.WLengthUnit;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.extra.WMenu;
import net.sf.witty.wt.widgets.extra.WMenuOrientation;

public class ViralIsolateForm extends FormWidget
{
	private ViralIsolate viralIsolate_;

	private WMenu tabForm_;
	
	private ViralIsolateMainForm _mainForm;
	private ViralIsolateProteinForm proteinForm_;
    private ViralIsolateResistanceForm resistanceForm_;
    private ViralIsolateReportForm reportForm_;

	public ViralIsolateForm(InteractionState interactionState, WMessage formName, ViralIsolate viralIsolate)
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
		WTable layout = new WTable(this);
		layout.resize(new WLength(100, WLengthUnit.Percentage), new WLength());

		WStackedWidget menuContents = new WStackedWidget(layout.elementAt(1, 0));
		tabForm_ = new WMenu(menuContents, WMenuOrientation.Horizontal, layout.elementAt(0, 0));
		tabForm_.resize(new WLength(100, WLengthUnit.Percentage), new WLength());
		layout.elementAt(1, 0).resize(new WLength(20, WLengthUnit.FontEx), new WLength());

		_mainForm = new ViralIsolateMainForm(this);
		tabForm_.addItem(tr("form.viralIsolate.editView.tab.viralIsolate"), _mainForm);
        //tabForm_.setStyleClass("tab-menu");
        if(getInteractionState()==InteractionState.Viewing)
        {
            proteinForm_ = new ViralIsolateProteinForm(this);
            tabForm_.addItem(tr("form.viralIsolate.editView.tab.proteins"), proteinForm_);
            proteinForm_.setStyleClass("viralisolateform");
            
            resistanceForm_ = new ViralIsolateResistanceForm(this);
            tabForm_.addItem(tr("form.viralIsolate.editView.tab.resistance"), resistanceForm_);
            resistanceForm_.setStyleClass("viralisolateform");
            
            reportForm_ = new ViralIsolateReportForm(this);
            tabForm_.addItem(tr("form.viralIsolate.editView.tab.report"), reportForm_);
            reportForm_.setStyleClass("viralisolateform");
        }
		tabForm_.setStyleClass("tabmenu");
		_mainForm.setStyleClass("viralisolateform");
		
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
        for(Iterator<TestResult> i = viralIsolate_.getTestResults().iterator(); i.hasNext();)
        {
            TestResult test = i.next();
            if(StandardObjects.getGssId().equals(test.getTest().getTestType().getDescription()))
            {
                if(test.getTest().getAnalysis()!=null)
                {
                    i.remove();
                    t.delete(test);
                }
            }
        }
        
        update(viralIsolate_, t);
        
        startViralIsolateAnalysis(t);
        
        t.commit();
        
        _mainForm.startAnalysis();
                
        RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected.setSelectedItem(viralIsolate_);
        redirectToView(RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected, RegaDBMain.getApp().getTree().getTreeContent().viralIsolateView);
	}
    
    private void startViralIsolateAnalysis(Transaction t)
    {
        List<Test> tests = t.getTests();
        String uid = RegaDBMain.getApp().getLogin().getUid();
        for(Test test : tests)
        {
            if(StandardObjects.getGssId().equals(test.getTestType().getDescription()))
            {
                if(test.getAnalysis()!=null)
                {
                    AnalysisPool.getInstance().launchAnalysis(new ResistanceInterpretationAnalysis(viralIsolate_, test, uid), RegaDBMain.getApp().getLogin());
                }
            }
        }
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
    public WMessage deleteObject()
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
    public WMessage leaveForm() {
        if(proteinForm_.refreshAlignmentsTimer_!=null)
            proteinForm_.refreshAlignmentsTimer_.stop();
        return super.leaveForm();
    }
}
