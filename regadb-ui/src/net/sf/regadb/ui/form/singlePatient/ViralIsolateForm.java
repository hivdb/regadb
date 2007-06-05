package net.sf.regadb.ui.form.singlePatient;

import java.io.Serializable;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
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
            redirectToSelect(RegaDBMain.getApp().getTree().getTreeContent().viralIsolates, RegaDBMain.getApp().getTree().getTreeContent().viralIsolatesSelect);
        }
        else
        {
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected, RegaDBMain.getApp().getTree().getTreeContent().viralIsolateView);
        } 
    }
    
    @Override
    public void deleteObject()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Patient p = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
        p.getViralIsolates().remove(viralIsolate_);
        
        t.delete(viralIsolate_);
        
        t.commit();
    }

    @Override
    public void redirectAfterDelete() 
    {
        RegaDBMain.getApp().getTree().getTreeContent().viralIsolatesSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().viralIsolateSelected.setSelectedItem(null);
    }
}
