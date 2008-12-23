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
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WFont;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WTimer;

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
        	warningMessage = new WarningMessage(new WImage("pics/formWarning.gif"), tr("form.viralIsolate.editView.message.aligning"), MessageType.INFO);
        	addWidget(warningMessage);
            refreshAlignmentsTimer_ = new WTimer(warningMessage);
            refreshAlignmentsTimer_.setInterval(2000);
            refreshAlignmentsTimer_.timeout.addListener(this, new Signal.Listener()
            {
                public void trigger()
                {
                    checkAlignments();
                }
            });
            refreshAlignmentsTimer_.start();
        }        
		
		
		proteinGroup_ = new WGroupBox(tr("form.viralIsolate.editView.group.protein"), this);
		proteinGroupTable_ = new FormTable(proteinGroup_);
		ntSequenceComboL_ = new Label(tr("form.viralIsolate.editView.label.ntSequence"));
		ntSequenceCombo_ = new ComboBox<NtSequence>(InteractionState.Editing, null);
        proteinGroupTable_.addLineToTable(ntSequenceComboL_, ntSequenceCombo_);
        aaSequenceComboL_ = new Label(tr("form.viralIsolate.editView.label.aaSequence"));
		aaSequenceCombo_ = new ComboBox<AaSequence>(InteractionState.Editing, null);
		proteinGroupTable_.addLineToTable(aaSequenceComboL_, aaSequenceCombo_);
		proteinL = new Label(tr("form.viralIsolate.editView.label.protein"));
		proteinTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		proteinGroupTable_.addLineToTable(proteinL, proteinTF);
		regionL = new Label(tr("form.viralIsolate.editView.label.region"));
		regionTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		proteinGroupTable_.addLineToTable(regionL, regionTF);
		alignmentL = new Label(tr("form.viralIsolate.editView.label.alignment"));
		alignmentTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		WFont alignmentFont = new WFont();
		alignmentFont.setFamily(WFont.GenericFamily.Monospace, lt("Courier"));
		alignmentTF.decorationStyle().setFont(alignmentFont);
		proteinGroupTable_.addLineToTable(alignmentL, alignmentTF);
		synonymousL = new Label(tr("form.viralIsolate.editView.label.synonymous"));
		synonymousTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		proteinGroupTable_.addLineToTable(synonymousL, synonymousTF);
		nonSynonymousL = new Label(tr("form.viralIsolate.editView.label.nonSynonymous"));
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
        	warningMessage.setText(tr("form.viralIsolate.editView.message.aligningComplete"));
            refreshAlignmentsTimer_.stop();
            WPushButton refreshAlignments_ = new WPushButton(warningMessage.getContentArea());
            refreshAlignments_.setText(tr("form.viralIsolate.editView.button.refreshAlignments"));
            refreshAlignments_.setEnabled(true);
            refreshAlignments_.clicked.addListener(this, new Signal1.Listener<WMouseEvent>()
            {
                public void trigger(WMouseEvent me)
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
		
		ntSequenceCombo_.addComboChangeListener(new Signal.Listener()
				{
					public void trigger()
					{
						setAaSequenceCombo();
						setAaData();
					}
				});
		
		aaSequenceCombo_.addComboChangeListener(new Signal.Listener()
				{
					public void trigger()
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
