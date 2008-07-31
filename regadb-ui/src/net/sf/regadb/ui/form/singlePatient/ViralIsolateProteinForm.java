package net.sf.regadb.ui.form.singlePatient;

import net.sf.regadb.align.view.UIVisualizeAaSequence;
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
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.framework.widgets.warning.WarningMessage;
import net.sf.regadb.ui.framework.widgets.warning.WarningMessage.MessageType;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WFont;
import net.sf.witty.wt.WFontGenericFamily;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WImage;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTimer;

public class ViralIsolateProteinForm extends WContainerWidget
{
	private ViralIsolateForm viralIsolateForm_;
	
	private WGroupBox proteinGroup_;
	private Label ntSequenceComboL_;
	private ComboBox<NtSequence> ntSequenceCombo_;
	private Label aaSequenceComboL_;
	private ComboBox<AaSequence> aaSequenceCombo_;
	private FormTable proteinGroupTable_;
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
    
	private WarningMessage warningMessage;
//    private WPushButton refreshAlignments_;
    WTimer refreshAlignmentsTimer_;
    
	private VisualizeAaSequence visAaSeq_ = new UIVisualizeAaSequence();
	
	public ViralIsolateProteinForm(ViralIsolateForm viralIsolateForm)
	{
		super();
		viralIsolateForm_ = viralIsolateForm;

		init();
	}

	public void init()
	{
        boolean aligning = false;
        for(NtSequence ntseq : viralIsolateForm_.getViralIsolate().getNtSequences())
        {
            if(ntseq.getAaSequences().size()==0)
            {
                aligning = true;
                break;
            }
        }
        
        //alignment refresh
        if(aligning)
        {
        	warningMessage = new WarningMessage(new WImage("pics/formWarning.gif"), tr("message.viralIsolate.aligning"), MessageType.INFO);
        	addWidget(warningMessage);
            refreshAlignmentsTimer_ = new WTimer(warningMessage);
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
		
		
		proteinGroup_ = new WGroupBox(tr("viralIsolate.protein"), this);
		proteinGroupTable_ = new FormTable(proteinGroup_);
		ntSequenceComboL_ = new Label(tr("viralIsolate.ntseq"));
		ntSequenceCombo_ = new ComboBox<NtSequence>(InteractionState.Editing, null);
        proteinGroupTable_.addLineToTable(ntSequenceComboL_, ntSequenceCombo_);
        aaSequenceComboL_ = new Label(tr("viralIsolate.aaseq"));
		aaSequenceCombo_ = new ComboBox<AaSequence>(InteractionState.Editing, null);
		proteinGroupTable_.addLineToTable(aaSequenceComboL_, aaSequenceCombo_);
		proteinL = new Label(tr("viralIsolate.protein"));
		proteinTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		proteinGroupTable_.addLineToTable(proteinL, proteinTF);
		regionL = new Label(tr("viralIsolate.aaseq.region"));
		regionTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		proteinGroupTable_.addLineToTable(regionL, regionTF);
		alignmentL = new Label(tr("viralIsolate.aaseq.alignment"));
		alignmentTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		alignmentTF.decorationStyle().setFont(new WFont(WFontGenericFamily.Monospace, "Courier"));
		proteinGroupTable_.addLineToTable(alignmentL, alignmentTF);
		synonymousL = new Label(tr("viralIsolate.aaseq.synonymous"));
		synonymousTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		proteinGroupTable_.addLineToTable(synonymousL, synonymousTF);
		nonSynonymousL = new Label(tr("viralIsolate.aaseq.nonSynonymous"));
		nonSynonymousTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		proteinGroupTable_.addLineToTable(nonSynonymousL, nonSynonymousTF);
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
        	warningMessage.setText(tr("message.viralIsolate.aligningComplete"));
            refreshAlignmentsTimer_.stop();
            WPushButton refreshAlignments_ = new WPushButton(warningMessage.getContentArea());
            refreshAlignments_.setText(tr("viralIsolate.refreshAlignments"));
            refreshAlignments_.setEnabled(true);
            refreshAlignments_.clicked.addListener(new SignalListener<WMouseEvent>()
            {
                public void notify(WMouseEvent me)
                {
                    fillData(viralIsolateForm_.getViralIsolate());
                    warningMessage.setHidden(true);
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
        ntSequenceCombo_.sort();
		
		setAaSequenceCombo();
		setAaData();
		
		ntSequenceCombo_.addComboChangeListener(new SignalListener<WEmptyEvent>()
				{
					public void notify(WEmptyEvent a)
					{
						setAaSequenceCombo();
						setAaData();
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
        aaSequenceCombo_.sort();
        
        t.commit();
	}
}
