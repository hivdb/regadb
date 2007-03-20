package net.sf.regadb.ui.form.singlePatient;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.core.utils.WLength;
import net.sf.witty.wt.core.utils.WLengthUnit;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.WMenu;
import net.sf.witty.wt.widgets.WMenuOrientation;
import net.sf.witty.wt.widgets.WStackedWidget;
import net.sf.witty.wt.widgets.WTable;

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
		proteinForm_ = new ViralIsolateProteinForm(this);
		tabForm_.addItem(tr("form.viralIsolate.editView.tab.proteins"), proteinForm_);
        
        fillData();
        
        addControlButtons();
	}

	private void fillData()
	{
        if(getInteractionState()==InteractionState.Adding)
        {
            viralIsolate_ = new ViralIsolate();
        }
		if(getInteractionState()!=InteractionState.Adding)
		{
			Transaction t;
			t = RegaDBMain.getApp().createTransaction();
	        t.attach(viralIsolate_);
	        t.commit();
	        
            _mainForm.fillData(viralIsolate_);
            proteinForm_.fillData(viralIsolate_);
		}
	}
    
    public ViralIsolate getViralIsolate()
    {
        return viralIsolate_;
    }
	
	@Override
	public void saveData()
	{
        Transaction t = RegaDBMain.getApp().createTransaction();
        t.update(viralIsolate_);
        
        _mainForm.saveData(t);
        t.update(viralIsolate_);
        t.commit();
	}
}
