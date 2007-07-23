package net.sf.regadb.ui.form.singlePatient;

import net.sf.regadb.align.view.VisualizeAaSequence;
import net.sf.regadb.analysis.functions.MutationHelper;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WFont;
import net.sf.witty.wt.WFontGenericFamily;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WTimer;

public class ViralIsolateProteinForm extends WContainerWidget
{
	private ViralIsolateForm viralIsolateForm_;
	
	private WGroupBox proteinGroup_;
	private Label ntSequenceComboL_;
	private ComboBox<NtSequence> ntSequenceCombo_;
	private Label aaSequenceComboL_;
	private ComboBox<AaSequence> aaSequenceCombo_;
	private WTable proteinGroupTable_;
	private Label proteinL;
	private TextField proteinTF;
	private Label regionL;
	private TextField regionTF;
	private Label alignmentL;
	private TextField alignmentTF;
	private Label synonymousL;
	private TextField synonymousTF;
	private Label nonSynonymousL;
	private TextField nonSynonymousTF;
    
    private WPushButton refreshAlignments_;
    private WTimer refreshAlignmentsTimer_;
    
	private VisualizeAaSequence visAaSeq_ = new VisualizeAaSequence();
	
	public ViralIsolateProteinForm(ViralIsolateForm viralIsolateForm)
	{
		super();
		viralIsolateForm_ = viralIsolateForm;

		init();
	}

	public void init()
	{
		proteinGroup_ = new WGroupBox(tr("form.viralIsolate.editView.group.protein"), this);
		proteinGroupTable_ = new WTable(proteinGroup_);
		ntSequenceComboL_ = new Label(tr("form.viralIsolate.editView.label.ntSequence"));
		ntSequenceCombo_ = new ComboBox<NtSequence>(InteractionState.Editing, null);
        int row = viralIsolateForm_.addLineToTable(proteinGroupTable_, ntSequenceComboL_, ntSequenceCombo_);
        //alignment refresh
        boolean aligning = false;
        for(NtSequence ntseq : viralIsolateForm_.getViralIsolate().getNtSequences())
        {
            if(ntseq.getAaSequences().size()==0)
            {
                aligning = true;
                break;
            }
        }
        if(aligning)
        {
            refreshAlignments_ = new WPushButton(tr("form.viralIsolate.editView.button.aligning"));
            refreshAlignments_.setEnabled(false);
            proteinGroupTable_.putElementAt(row, 2, refreshAlignments_);
            refreshAlignmentsTimer_ = new WTimer(refreshAlignments_);
            refreshAlignmentsTimer_.setInterval(2000);
            refreshAlignmentsTimer_.timeout.addListener(new SignalListener<WEmptyEvent>()
            {
                public void notify(WEmptyEvent a)
                {
                    checkAlignments();
                }
            });
            refreshAlignmentsTimer_.start();
        }
        //alignment refresh
        aaSequenceComboL_ = new Label(tr("form.viralIsolate.editView.label.aaSequence"));
		aaSequenceCombo_ = new ComboBox<AaSequence>(InteractionState.Editing, null);
		viralIsolateForm_.addLineToTable(proteinGroupTable_, aaSequenceComboL_, aaSequenceCombo_);
		proteinL = new Label(tr("form.viralIsolate.editView.label.protein"));
		proteinTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(proteinGroupTable_, proteinL, proteinTF);
		regionL = new Label(tr("form.viralIsolate.editView.label.region"));
		regionTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(proteinGroupTable_, regionL, regionTF);
		alignmentL = new Label(tr("form.viralIsolate.editView.label.alignment"));
		alignmentTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		alignmentTF.decorationStyle().setFont(new WFont(WFontGenericFamily.Monospace, "Courier"));
        viralIsolateForm_.addLineToTable(proteinGroupTable_, alignmentL, alignmentTF);
		synonymousL = new Label(tr("form.viralIsolate.editView.label.synonymous"));
		synonymousTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(proteinGroupTable_, synonymousL, synonymousTF);
		nonSynonymousL = new Label(tr("form.viralIsolate.editView.label.nonSynonymous"));
		nonSynonymousTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(proteinGroupTable_, nonSynonymousL, nonSynonymousTF);
	}
    
    private void checkAlignments()
    {
        boolean aligning = false;
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        t.refresh(viralIsolateForm_.getViralIsolate());
        t.commit();
        
        for(NtSequence ntseq : viralIsolateForm_.getViralIsolate().getNtSequences())
        {
            if(ntseq.getAaSequences().size()==0)
            {
                aligning = true;
                break;
            }
        }
        
        if(!aligning)
        {
            refreshAlignmentsTimer_.stop();
            refreshAlignments_.setText(tr("form.viralIsolate.editView.button.refreshAlignments"));
            refreshAlignments_.setEnabled(true);
            refreshAlignments_.clicked.addListener(new SignalListener<WMouseEvent>()
            {
                public void notify(WMouseEvent me)
                {
                    fillData(viralIsolateForm_.getViralIsolate());
                    refreshAlignments_.setHidden(true);
                }
            });
        }
    }
	
	void fillData(ViralIsolate vi)
	{
        ntSequenceCombo_.clearItems();
        aaSequenceCombo_.clearItems();
        
		for(NtSequence ntseq : vi.getNtSequences())
		{
			if(ntseq.getAaSequences()!=null && ntseq.getAaSequences().size()!=0)
			{
				ntSequenceCombo_.addItem(new DataComboMessage<NtSequence>(ntseq, ntseq.getLabel()));
			}
		}
		
		setAaSequenceCombo();
		setAaData();
		
		ntSequenceCombo_.addComboChangeListener(new SignalListener<WEmptyEvent>()
				{
					public void notify(WEmptyEvent a)
					{
						setAaSequenceCombo();
					}
				});
		
		aaSequenceCombo_.addComboChangeListener(new SignalListener<WEmptyEvent>()
				{
					public void notify(WEmptyEvent a)
					{
						setAaData();
					}
				});
			
	}
	
	private void setAaData()
	{
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        AaSequence aaSequence = aaSequenceCombo_.currentValue();
		
        if(aaSequence!=null)
        {
            t.attach(aaSequence);
        
    		proteinTF.setText(aaSequence.getProtein().getAbbreviation());
    		regionTF.setText(aaSequence.getFirstAaPos() + " - " + aaSequence.getLastAaPos());
            alignmentTF.setText("<pre>" + visAaSeq_.getAlignmentView(aaSequence)+"</pre>");
            synonymousTF.setText(MutationHelper.getSynonymousMutations(aaSequence));
            nonSynonymousTF.setText(MutationHelper.getNonSynonymousMutations(aaSequence));
            
            t.commit();
        }
	}
	
	private void setAaSequenceCombo()
	{
		NtSequence ntSeq = ntSequenceCombo_.currentValue();
		
		aaSequenceCombo_.clearItems();
		
        Transaction t = RegaDBMain.getApp().createTransaction();

        if(ntSeq!=null)
        for(AaSequence aaseq : ntSeq.getAaSequences())
		{
			aaSequenceCombo_.addItem(new DataComboMessage<AaSequence>(aaseq, aaseq.getProtein().getAbbreviation()));
		}
        
        t.commit();
	}
}
