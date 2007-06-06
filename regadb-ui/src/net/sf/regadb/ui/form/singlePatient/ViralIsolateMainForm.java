package net.sf.regadb.ui.form.singlePatient;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.analysis.functions.FastaRead;
import net.sf.regadb.analysis.functions.FastaReadStatus;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.service.AnalysisPool;
import net.sf.regadb.service.align.AlignmentAnalysis;
import net.sf.regadb.service.wts.NtSequenceAnalysis;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.NucleotideField;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.forms.fields.WNucleotideValidator;
import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WFileUpload;
import net.sf.witty.wt.WFontSize;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.core.utils.WHorizontalAlignment;
import net.sf.witty.wt.core.utils.WVerticalAlignment;

public class ViralIsolateMainForm extends WContainerWidget
{
	private ViralIsolateForm viralIsolateForm_;
    
    private ArrayList<NtSequence> removedSequences = new ArrayList<NtSequence>();
    private Set<AaSequence> removedAaSequences = new HashSet<AaSequence>();
    private Set<TestResult> removedTestResults = new HashSet<TestResult>();

	// General group
	private WGroupBox generalGroup_;
	private WTable generalGroupTable_;
	private Label sampleDateL;
	private DateField sampleDateTF;
	private Label sampleIdL;
	private TextField sampleIdTF;

	// Sequence group
	private WGroupBox sequenceGroup_;
	private WTable sequenceGroupTable_;
	private WComboBox seqComboBox;
	private WPushButton addButton;
	private WPushButton deleteButton;
    private WPushButton confirmButton;
    private WPushButton cancelButton;

	// NtSeq group
	private WGroupBox ntSeqGroup_;
	private WTable ntSeqGroupTable_;
	private Label seqLabel;
	private TextField seqLabelTF;
	private Label seqDateL;
	private DateField seqDateTF;
    private Label upLoadL;
    private WFileUpload upload_;
    private WCheckBox autoFix_;
    private WPushButton uploadFasta_;
    private Label ntL;
    private NucleotideField ntTF;
    private WNucleotideValidator ntValidator;
    private Label typeL;
    private TextField typeTF;
	private Label subTypeL;
	private TextField subTypeTF;
    
    private static final String defaultSequenceLabel_ = "Sequence ";

	public ViralIsolateMainForm(ViralIsolateForm viralIsolateForm)
	{
		super();
		viralIsolateForm_ = viralIsolateForm;

		init();
	}

	public void init()
	{
		// General group
		generalGroup_ = new WGroupBox(tr("form.viralIsolate.editView.general"), this);
		generalGroupTable_ = new WTable(generalGroup_);
		sampleDateL = new Label(tr("form.viralIsolate.editView.sampleDate"));
		sampleDateTF = new DateField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(generalGroupTable_, sampleDateL, sampleDateTF);
		sampleIdL = new Label(tr("form.viralIsolate.editView.sampleId"));
		sampleIdTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(generalGroupTable_, sampleIdL, sampleIdTF);

		// Sequence group
		sequenceGroup_ = new WGroupBox(tr("form.viralIsolate.editView.sequence"), this);
		sequenceGroupTable_ = new WTable(sequenceGroup_);
		// comboBox
		seqComboBox = new WComboBox();
        sequenceGroupTable_.putElementAt(0, 0, seqComboBox);

		// NtSeq group
		sequenceGroupTable_.elementAt(1, 0).setColumnSpan(2);
		ntSeqGroup_ = new WGroupBox(tr("form.viralIsolate.editView.ntSeq"), sequenceGroupTable_.elementAt(1, 0));
		ntSeqGroupTable_ = new WTable(ntSeqGroup_);
		seqLabel = new Label(tr("form.viralIsolate.editView.seqLabel"));
		seqLabelTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
        seqLabelTF.setMandatory(true);
		viralIsolateForm_.addLineToTable(ntSeqGroupTable_, seqLabel, seqLabelTF);
		seqDateL = new Label(tr("form.viralIsolate.editView.seqDate"));
		seqDateTF = new DateField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(ntSeqGroupTable_, seqDateL, seqDateTF);
        typeL = new Label(tr("form.viralIsolate.editView.sampleType"));
        typeTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
        viralIsolateForm_.addLineToTable(ntSeqGroupTable_, typeL, typeTF);
        subTypeL = new Label(tr("form.viralIsolate.editView.subType"));
        subTypeTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
        viralIsolateForm_.addLineToTable(ntSeqGroupTable_, subTypeL, subTypeTF);
        
        if(viralIsolateForm_.isEditable())
        {
            WTable ntFileTable = new WTable(ntSeqGroupTable_.elementAt(2, 1));
            WTable buttonTable = new WTable(ntFileTable.elementAt(0, 1));
            upLoadL = new Label(tr("form.viralIsolate.editView.uploadlabel"));
            ntSeqGroupTable_.putElementAt(2, 0, upLoadL);
            upload_ = new WFileUpload(buttonTable.elementAt(0, 0));
            autoFix_ = new WCheckBox(tr("formfield.ntfield.checkbox.autofixSequence"), buttonTable.elementAt(1, 0));
            uploadFasta_ = new WPushButton(tr("formfield.ntfield.button.uploadFastaFile"), buttonTable.elementAt(2, 0));
            ntFileTable.elementAt(0, 1).setVerticalAlignment(WVerticalAlignment.AlignBottom);
            upload_.decorationStyle().font().setSize(WFontSize.Smaller);
            
            ntValidator = new WNucleotideValidator();
            uploadFasta_.clicked.addListener(new SignalListener<WMouseEvent>()
            {
                   public void notify(WMouseEvent a) 
                   {                
                       upload_.upload();
                   }
            });
            
            upload_.uploaded.addListener(new SignalListener<WEmptyEvent>()
            {
                   public void notify(WEmptyEvent a) 
                   {                
                       File fastaFile = new File(upload_.spoolFileName());
                            
                       FastaRead read = FastaHelper.readFastaFile(fastaFile, autoFix_.isChecked());
                            
                       fastaFile.delete();
                            
                       if(read.status_==FastaReadStatus.Invalid)
                       {
                           MessageBox.showWarningMessage(tr("form.viralIsolate.warning.invalidFastaFile"));
                       }
                       else if(read.status_==FastaReadStatus.FileNotFound)
                       {
                           MessageBox.showWarningMessage(tr("form.viralIsolate.warning.fastaFileNotFound"));
                       }
                       else if(read.status_==FastaReadStatus.MultipleSequences)
                       {
                           MessageBox.showWarningMessage(tr("form.viralIsolate.warning.multipleSequences"));
                       }
                       else if (read.status_==FastaReadStatus.ValidButFixed)
                       {
                           MessageBox.showWarningMessage(tr("form.viralIsolate.warning.autoFixedSequence"));
                           ntTF.setText(read.xna_);
                           seqComboBox.disable();
                           addButton.disable();
                       }
                       else
                       {
                           ntTF.setText(read.xna_);
                           seqComboBox.disable();
                           addButton.disable();
                       }
                   }
            });
        }
        ntL = new Label(tr("form.viralIsolate.editView.nucleotides"));
        ntTF = new NucleotideField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
        ntTF.setMandatory(true);
        viralIsolateForm_.addLineToTable(ntSeqGroupTable_, ntL, ntTF);
        
        addButtons();
	}
	
	public void fillData(ViralIsolate vi)
	{
		sampleDateTF.setDate(vi.getSampleDate());
		sampleIdTF.setText(vi.getSampleId());
        
        for(NtSequence ntseq : vi.getNtSequences())
        {
            if(ntseq.getLabel()==null || ntseq.getLabel().equals(""))
			{
				ntseq.setLabel(getUniqueSequenceLabel(vi));
            }
            seqComboBox.addItem(new DataComboMessage<NtSequence>(ntseq, ntseq.getLabel()));
        }
        seqComboBox.setCurrentIndex(0);
       
        setSequenceData(((DataComboMessage<NtSequence>)seqComboBox.currentText()).getValue());
        
        setFieldListeners();
        
        seqComboBox.changed.addListener(new SignalListener<WEmptyEvent>()
            {
                public void notify(WEmptyEvent a) 
                {
                    setSequenceData(((DataComboMessage<NtSequence>)seqComboBox.currentText()).getValue());
                }
            });

        addButton.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        DataComboMessage<NtSequence> msg = addSeqData();

                        seqComboBox.setCurrentItem(msg);
                        setSequenceData(msg.getValue());
                    }
                });
        
        deleteButton.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        if(seqComboBox.count()==1)
                        {
                            MessageBox.showWarningMessage(tr("form.viralIsolate.warning.minimumOneSequence"));
                        }
                        else
                        {
                            deleteSequence();
                            seqComboBox.enable();
                            addButton.enable();
                        }
                    }
                });
        
        cancelButton.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {                        
                        setSequenceData(((DataComboMessage<NtSequence>)seqComboBox.currentText()).getValue());
                        
                        seqComboBox.enable();
                        addButton.enable();
                    }
                });
        
        confirmButton.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {                
                        confirmSequence();
                        
                        seqComboBox.enable();
                        addButton.enable();
                    }
                });
	}
    
    private DataComboMessage<NtSequence> addSeqData()
    {
        String label = getUniqueSequenceLabel(viralIsolateForm_.getViralIsolate());
        NtSequence newSeq = new NtSequence(viralIsolateForm_.getViralIsolate());
        newSeq.setLabel(label);
        viralIsolateForm_.getViralIsolate().getNtSequences().add(newSeq);
        
        DataComboMessage<NtSequence> msg = new DataComboMessage<NtSequence>(newSeq, label);
        seqComboBox.addItem(msg);
        
        return msg;
    }
    
    private void setSequenceData(NtSequence seq)
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        t.update(seq);
        t.commit();
        
        seqLabelTF.setText(seq.getLabel());
        seqDateTF.setDate(seq.getSequenceDate());
        ntTF.setText(seq.getNucleotides());
        
        for(TestResult tr : seq.getTestResults())
        {
            if(tr.getTest().getDescription().equals(RegaDBWtsServer.getSubTypeTest()) && tr.getTest().getTestType().getDescription().equals(RegaDBWtsServer.getSubTypeTestType()))
                subTypeTF.setText(tr.getValue());   
            if(tr.getTest().getDescription().equals(RegaDBWtsServer.getTypeTest()) && tr.getTest().getTestType().getDescription().equals(RegaDBWtsServer.getTypeTestType()))
                typeTF.setText(tr.getValue());
        }
    }
    
    private String getUniqueSequenceLabel(ViralIsolate vi)
    {
        int largestLabel = 0;
        List<Integer> labelNumbers = new ArrayList<Integer>();
        int labelNumber = 0;
        
        for(NtSequence ntseq : vi.getNtSequences())
        {
            String label = ntseq.getLabel();

            if(label!=null)
            {
                if(label.startsWith(defaultSequenceLabel_));
                {
                    label = label.substring(label.lastIndexOf(" ")+1, ntseq.getLabel().length());
                    try
                    {
                        labelNumber = Integer.parseInt(label);
                        labelNumbers.add(labelNumber);
                    }
                    catch(NumberFormatException e)
                    {
                        //do nothing if it isn't a parsable number
                    }
                }
            }
        }
        Collections.sort(labelNumbers);
        largestLabel++;
        
        
        return defaultSequenceLabel_ + (labelNumbers.size()!=0?(labelNumbers.get(labelNumbers.size()-1) + 1):1);
    }
    
    private void deleteSequence()
    {
        NtSequence currentSequence = ((DataComboMessage<NtSequence>)seqComboBox.currentText()).getValue();
        removedSequences.add(currentSequence);
        seqComboBox.removeItem(seqComboBox.currentIndex());
        
        seqComboBox.setCurrentIndex(seqComboBox.count()-1);
        
        setSequenceData(((DataComboMessage<NtSequence>)seqComboBox.currentText()).getValue());
    }
    
    private void confirmSequence()
    {
        if(validateSequenceFields())
        {
            NtSequence currentSeq = ((DataComboMessage<NtSequence>)seqComboBox.currentText()).getValue();
            currentSeq.setLabel(seqLabelTF.getFormText());
            currentSeq.setSequenceDate(seqDateTF.getDate());
            currentSeq.setNucleotides(ntTF.getFormText());
            
            for(AaSequence aaseq : currentSeq.getAaSequences())
            {
                removedAaSequences.add(aaseq);
            }
            
            for(TestResult tr : currentSeq.getTestResults())
            {
                if(tr.getTest().getDescription().equals(RegaDBWtsServer.getSubTypeTest()) && tr.getTest().getTestType().getDescription().equals(RegaDBWtsServer.getSubTypeTestType()))
                    removedTestResults.add(tr);
                else if(tr.getTest().getDescription().equals(RegaDBWtsServer.getTypeTest()) && tr.getTest().getTestType().getDescription().equals(RegaDBWtsServer.getTypeTestType()))
                    removedTestResults.add(tr);
            }
        }
    }
    
    private boolean validateSequenceFields()
    {
        boolean valid = true;
                
        if(seqLabelTF.validate())
        {
            seqLabelTF.flagValid();
        }
        else
        {
            seqLabelTF.flagErroneous();
            valid = false;
        }
        
        if(seqDateTF.validate())
        {
            seqDateTF.flagValid();
        }
        else
        {
            seqDateTF.flagErroneous();
            valid = false;
        }
        
        if(ntTF.validate())
        {
            ntTF.flagValid();
        }
        else
        {
            ntTF.flagErroneous();
            valid = false;
        }
        
        return valid;
    }
    
    public void saveData(Transaction t)
    {
        confirmSequence();
        
        for(NtSequence ntseq : removedSequences)
        {
            t.delete(ntseq);
            viralIsolateForm_.getViralIsolate().getNtSequences().remove(ntseq);
        }
        
        NtSequence ntseqref;
        for(AaSequence aaseq : removedAaSequences)
        {
            ntseqref = aaseq.getNtSequence();
            ntseqref.getAaSequences().remove(aaseq);
            t.delete(aaseq);
        }
        
        for(TestResult tr : removedTestResults)
        {
            ntseqref = tr.getNtSequence();
            ntseqref.getTestResults().remove(tr);
            t.delete(tr);
        }
        
        viralIsolateForm_.getViralIsolate().setSampleDate(sampleDateTF.getDate());
        viralIsolateForm_.getViralIsolate().setSampleId(sampleIdTF.getFormText());
    }
    
    public void startAnalysis()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        Test subTypeTest = t.getTest(RegaDBWtsServer.getSubTypeTest(), RegaDBWtsServer.getSubTypeTestType());
        Test typeTest = t.getTest(RegaDBWtsServer.getTypeTest(), RegaDBWtsServer.getTypeTestType());
        t.commit();
        
        for(NtSequence ntseq : viralIsolateForm_.getViralIsolate().getNtSequences())
        {
            if(ntseq.getAaSequences().size()==0)
            {
            AnalysisPool.getInstance().launchAnalysis(new AlignmentAnalysis(ntseq.getNtSequenceIi(), RegaDBMain.getApp().getLogin().getUid()), RegaDBMain.getApp().getLogin());
            AnalysisPool.getInstance().launchAnalysis(new NtSequenceAnalysis(   ntseq.getNtSequenceIi(), 
                                                                                RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem(), 
                                                                                subTypeTest, 
                                                                                RegaDBMain.getApp().getLogin().getUid()), RegaDBMain.getApp().getLogin()); 
            AnalysisPool.getInstance().launchAnalysis(new NtSequenceAnalysis(   ntseq.getNtSequenceIi(),
                                                                                RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem(), 
                                                                                typeTest,
                                                                                RegaDBMain.getApp().getLogin().getUid()), RegaDBMain.getApp().getLogin());
            }
        }
    }

    private void addButtons()
    {
        sequenceGroupTable_.elementAt(0, 1).setContentAlignment(WHorizontalAlignment.AlignRight);
        WTable buttonTable = new WTable(sequenceGroupTable_.elementAt(0, 1));
        sequenceGroupTable_.elementAt(0, 1).setInline(true);
        // add-delete
        addButton = new WPushButton(tr("form.viralIsolate.addButton"), buttonTable.elementAt(0, 0));
        deleteButton = new WPushButton(tr("form.viralIsolate.deleteButton"), buttonTable.elementAt(0, 1));
        // confirm-cancel
        int numRow = ntSeqGroupTable_.numRows();
        WTable buttonsTable = new WTable(ntSeqGroupTable_.elementAt(numRow, 1));
        confirmButton = new WPushButton(tr("form.viralIsolate.confirmButton"), buttonsTable.elementAt(0, 0));
        cancelButton = new WPushButton(tr("form.viralIsolate.cancelButton"), buttonsTable.elementAt(0, 1));
        sequenceGroupTable_.elementAt(3, 1).setContentAlignment(WHorizontalAlignment.AlignRight);
       
        boolean sensitivities = viralIsolateForm_.isEditable();
        addButton.setEnabled(sensitivities);
        deleteButton.setEnabled(sensitivities);
        confirmButton.setEnabled(sensitivities);
        cancelButton.setEnabled(sensitivities);
    }
    
    private void setFieldListeners()
    {
        seqLabelTF.addChangeListener(new SignalListener<WEmptyEvent>()
                    {
                        public void notify(WEmptyEvent a) 
                        {
                            seqComboBox.disable();
                            addButton.disable();
                        }
                    });
                    
        seqDateTF.addChangeListener(new SignalListener<WEmptyEvent>()
                    {
                        public void notify(WEmptyEvent a) 
                        {
                            seqComboBox.disable();
                            addButton.disable();
                        }
                    });
                    
        ntTF.addChangeListener(new SignalListener<WEmptyEvent>()
                    {
                        public void notify(WEmptyEvent a) 
                        {
                            seqComboBox.disable();
                            addButton.disable();
                        }
                    });
    }
}
