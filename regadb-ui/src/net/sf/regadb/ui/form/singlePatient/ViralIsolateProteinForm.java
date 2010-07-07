package net.sf.regadb.ui.form.singlePatient;

import net.sf.regadb.align.view.UIVisualizeAaSequence;
import net.sf.regadb.align.view.VisualizeAaSequence;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.tools.MutationHelper;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.framework.widgets.warning.WarningMessage;
import net.sf.regadb.ui.framework.widgets.warning.WarningMessage.MessageType;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WFont;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WTimer;

public class ViralIsolateProteinForm extends WContainerWidget
{
	private ViralIsolateForm viralIsolateForm_;
	
	private Label proteinComboL_;
	private ComboBox<AaSequence> proteinCombo_;
	private FormTable proteinGroupTable_;
	private Label sequenceL;
	private TextField sequenceTF;
	private Label regionL;
	private TextField regionTF;
	private Label alignmentL;
	private WText alignmentTF;
	private Label synonymousL;
	private TextField synonymousTF;
	private Label nonSynonymousL;
	private TextField nonSynonymousTF;
    
	private WarningMessage warningMessage;
	private WarningMessage noProteinData;
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
        boolean aligning = isAligning();
        
        //alignment refresh
        if(aligning)
        {
        	warningMessage = new WarningMessage(new WImage("pics/formWarning.gif"), tr("form.viralIsolate.editView.message.aligning"), MessageType.INFO);
        	addWidget(warningMessage);
            refreshAlignmentsTimer_ = new WTimer(warningMessage);
            refreshAlignmentsTimer_.setInterval(2000);
            refreshAlignmentsTimer_.timeout().addListener(this, new Signal.Listener()
            {
                public void trigger()
                {
                    checkAlignments();
                }
            });
            refreshAlignmentsTimer_.start();
        }        
		
		proteinGroupTable_ = new FormTable(this);
        proteinComboL_ = new Label(tr("form.viralIsolate.editView.label.protein"));
        proteinCombo_ = new ComboBox<AaSequence>(InteractionState.Editing, null);
		proteinGroupTable_.addLineToTable(proteinComboL_, proteinCombo_);
		sequenceL = new Label(tr("form.viralIsolate.editView.label.ntSequence"));
		sequenceTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		proteinGroupTable_.addLineToTable(sequenceL, sequenceTF);
		regionL = new Label(tr("form.viralIsolate.editView.label.region"));
		regionTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		proteinGroupTable_.addLineToTable(regionL, regionTF);
		alignmentL = new Label(tr("form.viralIsolate.editView.label.alignment"));
		alignmentTF = new WText(viralIsolateForm_);
		WFont alignmentFont = new WFont();
		alignmentFont.setFamily(WFont.GenericFamily.Monospace, "Courier");
		alignmentTF.getDecorationStyle().setFont(alignmentFont);
		proteinGroupTable_.addLineToTable(alignmentL, alignmentTF);
		synonymousL = new Label(tr("form.viralIsolate.editView.label.synonymous"));
		synonymousTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		proteinGroupTable_.addLineToTable(synonymousL, synonymousTF);
		nonSynonymousL = new Label(tr("form.viralIsolate.editView.label.nonSynonymous"));
		nonSynonymousTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		proteinGroupTable_.addLineToTable(nonSynonymousL, nonSynonymousTF);
		
		proteinCombo_.addComboChangeListener(new Signal.Listener(){
			public void trigger() {
				setAaData();
			}
		});
		
		proteinGroupTable_.setHidden(true);
	}
    
	private boolean isAligning() {
		for(NtSequence ntseq : viralIsolateForm_.getViralIsolate().getNtSequences()) {
            if(!ntseq.isAligned())
                return true;
        }
		
		return false;
	}
	
    private void checkAlignments()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        t.refresh(viralIsolateForm_.getViralIsolate());
        t.commit();
        
        boolean aligning = isAligning();
        
        if(!aligning)
        {
        	warningMessage.setText(tr("form.viralIsolate.editView.message.aligningComplete"));
            refreshAlignmentsTimer_.stop();
            fillData(viralIsolateForm_.getViralIsolate());
            warningMessage.setHidden(true);
        }
    }
	
	void fillData(ViralIsolate vi)
	{
        proteinCombo_.clearItems();
        for (NtSequence ntseq : vi.getNtSequences()) {
            for(AaSequence aaseq : ntseq.getAaSequences()) {
            	proteinCombo_.addItem(new DataComboMessage<AaSequence>(aaseq, aaseq.getProtein().getAbbreviation()));
    		}
        }
        proteinCombo_.sort();
        
		if (proteinCombo_.size() == 0 && !isAligning()) {
			noProteinData = new WarningMessage(new WImage("pics/formWarning.gif"), tr("form.viralIsolate.editView.message.noProteinData"), MessageType.INFO);
			addWidget(noProteinData);
		} else if (proteinCombo_.size() != 0 && !isAligning()) {
			proteinGroupTable_.setHidden(false);
			setAaData();
		}
	}
	
	private void setAaData()
	{
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        AaSequence aaSequence = proteinCombo_.currentValue();
		
        if(aaSequence!=null) {
            t.attach(aaSequence);
        
    		sequenceTF.setText(aaSequence.getNtSequence().getLabel());
    		regionTF.setText(aaSequence.getFirstAaPos() + " - " + aaSequence.getLastAaPos());
    		alignmentTF.setText("<pre>" + visAaSeq_.getAlignmentView(aaSequence)+"</pre>");
            synonymousTF.setText(MutationHelper.getSynonymousMutations(aaSequence));
            nonSynonymousTF.setText(MutationHelper.getNonSynonymousMutations(aaSequence));
        }
        
        t.commit();
	}
}
