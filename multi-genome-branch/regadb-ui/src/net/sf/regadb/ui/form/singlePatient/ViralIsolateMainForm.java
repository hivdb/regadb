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
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.service.AnalysisPool;
import net.sf.regadb.service.wts.BlastAnalysis;
import net.sf.regadb.service.wts.FullAnalysis;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.service.wts.BlastAnalysis.UnsupportedGenomeException;
import net.sf.regadb.service.wts.ServiceException.ServiceUnavailableException;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FileUpload;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.NucleotideField;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.MyComboBox;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;

public class ViralIsolateMainForm extends WContainerWidget
{
	private ViralIsolateForm viralIsolateForm_;
    
    private ArrayList<NtSequence> removedSequences = new ArrayList<NtSequence>();
    private Set<AaSequence> removedAaSequences = new HashSet<AaSequence>();
    private Set<TestResult> removedTestResults = new HashSet<TestResult>();

	// General group
	private WGroupBox generalGroup_;
	private FormTable generalGroupTable_;
	private Label sampleDateL;
	private DateField sampleDateTF;
	private Label sampleIdL;
	private TextField sampleIdTF;

	// Sequence group
	private WGroupBox sequenceGroup_;
	private MyComboBox seqComboBox;
	private WPushButton addButton;
	private WPushButton deleteButton;
    private WPushButton confirmButton;
    private WPushButton cancelButton;

	// NtSeq group
	private Label seqLabel;
	private TextField seqLabelTF;
	private Label seqDateL;
	private DateField seqDateTF;
    private Label upLoadL;
    private FileUpload upload_;
    private WCheckBox autoFix_;
    private Label ntL;
    private NucleotideField ntTF;
    private Label genomeL;
    private TextField genomeTF;
	private Label subTypeL;
	private TextField subTypeTF;
	private WText fastaLabel_;
    
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
		generalGroupTable_ = new FormTable(generalGroup_);
		sampleDateL = new Label(tr("form.viralIsolate.editView.sampleDate"));
		sampleDateTF = new DateField(viralIsolateForm_.getInteractionState(), viralIsolateForm_, RegaDBSettings.getInstance().getDateFormat());
		sampleDateTF.setMandatory(true);
		generalGroupTable_.addLineToTable(sampleDateL, sampleDateTF);
		sampleIdL = new Label(tr("form.viralIsolate.editView.sampleId"));
		sampleIdTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_){
		    public boolean checkUniqueness(){
		        return checkSampleId(getFormText());
		    }
		};
		sampleIdTF.setMandatory(true);
		generalGroupTable_.addLineToTable(sampleIdL, sampleIdTF);

	    // Sequence group
		sequenceGroup_ = new WGroupBox(
				tr("form.viralIsolate.editView.sequence"), this);
		FormTable sequenceSelectForm = new FormTable(sequenceGroup_);
		Label currentSequenceL = new Label(
				tr("form.viralIsolate.label.currentSequence"));
		seqComboBox = new MyComboBox();
		sequenceSelectForm.addLineToTable(currentSequenceL, seqComboBox);

		// NtSeq group
		seqLabel = new Label(tr("form.viralIsolate.editView.seqLabel"));
		seqLabelTF = new TextField(viralIsolateForm_.getInteractionState(),
				viralIsolateForm_);
		seqLabelTF.setMandatory(true);
		sequenceSelectForm.addLineToTable(seqLabel, seqLabelTF);
		seqDateL = new Label(tr("form.viralIsolate.editView.seqDate"));
		seqDateTF = new DateField(viralIsolateForm_.getInteractionState(),
				viralIsolateForm_, RegaDBSettings.getInstance().getDateFormat());
		sequenceSelectForm.addLineToTable(seqDateL, seqDateTF);
		genomeL = new Label(tr("form.viralIsolate.editView.genome"));
		genomeTF = new TextField(viralIsolateForm_.getInteractionState(),
				viralIsolateForm_);
		sequenceSelectForm.addLineToTable(genomeL, genomeTF);
		subTypeL = new Label(tr("form.viralIsolate.editView.subType"));
		subTypeTF = new TextField(viralIsolateForm_.getInteractionState(),
				viralIsolateForm_);
		sequenceSelectForm.addLineToTable(subTypeL, subTypeTF);

		if (viralIsolateForm_.isEditable()) {
			genomeL.setHidden(true);
			genomeTF.setHidden(true);
			subTypeL.setHidden(true);
			subTypeTF.setHidden(true);
			WTable uploadTable = new WTable();
			upload_ = new FileUpload(viralIsolateForm_.getInteractionState(),
					viralIsolateForm_);
			uploadTable.elementAt(1, 0).addWidget(upload_);
			autoFix_ = new WCheckBox(
					tr("formfield.ntfield.checkbox.autofixSequence"),
					uploadTable.elementAt(2, 0));
			fastaLabel_ = new WText(uploadTable.elementAt(0, 0));
			fastaLabel_.setStyleClass("viral-isolate-fasta-label");
			upLoadL = new Label(tr("form.viralIsolate.editView.uploadlabel"));
			sequenceSelectForm.addLineToTable(upLoadL, uploadTable);
            
            upload_.getFileUpload().uploaded.addListener(this, new Signal.Listener()
            {
                   public void trigger() 
                   {                
                	   upload_.setAnchor(lt(""), "");
                       if(upload_.getFileUpload().spoolFileName()!=null)
                       {
	                	   File fastaFile = new File(upload_.getFileUpload().spoolFileName());
	                            
	                       FastaRead read = FastaHelper.readFastaFile(fastaFile, autoFix_.isChecked());
	                            
	                       fastaFile.delete();
	                            
	                       if(read.status_==FastaReadStatus.Invalid)
	                       {
	                    	   UIUtils.showWarningMessageBox(ViralIsolateMainForm.this, tr("form.viralIsolate.warning.invalidFastaFile"));
	                       }
	                       else if(read.status_==FastaReadStatus.FileNotFound)
	                       {
	                    	   UIUtils.showWarningMessageBox(ViralIsolateMainForm.this, tr("form.viralIsolate.warning.fastaFileNotFound"));
	                       }
	                       else if(read.status_==FastaReadStatus.MultipleSequences)
	                       {
	                    	   UIUtils.showWarningMessageBox(ViralIsolateMainForm.this, tr("form.viralIsolate.warning.multipleSequences"));
	                       }
	                       else if (read.status_==FastaReadStatus.ValidButFixed)
	                       {
	                    	   UIUtils.showWarningMessageBox(ViralIsolateMainForm.this, tr("form.viralIsolate.warning.autoFixedSequence"));
	                           ntTF.setText(read.xna_);
	                           seqComboBox.disable();
	                           addButton.disable();
	                           fastaLabel_.setText(lt("["+read.fastaHeader_+"]"));
	                       }
	                       else
	                       {
	                           ntTF.setText(read.xna_);
	                           seqComboBox.disable();
	                           addButton.disable();
	                           fastaLabel_.setText(lt("["+read.fastaHeader_+"]"));
	                       }
                       }
                   }
            });
        }
		
        ntL = new Label(tr("form.viralIsolate.editView.nucleotides"));
        ntTF = new NucleotideField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
        ntTF.setMandatory(true);
        sequenceSelectForm.addLineToTable(ntL, ntTF);
       
        if (viralIsolateForm_.isEditable()) {
            int row = sequenceSelectForm.rowCount();
            int col = 0;
            sequenceSelectForm.elementAt(row, col).setColumnSpan(2);
            sequenceSelectForm.elementAt(row, col).setStyleClass("navigation");
           
            confirmButton = new WPushButton(tr("form.viralIsolate.confirmButton"), sequenceSelectForm.elementAt(row, col));
            cancelButton = new WPushButton(tr("form.viralIsolate.cancelButton"), sequenceSelectForm.elementAt(row, col));
            addButton = new WPushButton(tr("form.viralIsolate.addButton"), sequenceSelectForm.elementAt(row, col));
            deleteButton = new WPushButton(tr("form.viralIsolate.deleteButton"), sequenceSelectForm.elementAt(row, col));
        }
	}
	
	@SuppressWarnings("unchecked")
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
        
        seqComboBox.changed.addListener(this, new Signal.Listener()
            {
                public void trigger() 
                {
                    setSequenceData(((DataComboMessage<NtSequence>)seqComboBox.currentText()).getValue());
                }
            });

        if (viralIsolateForm_.isEditable()) {
	        addButton.clicked.addListener(this, new Signal1.Listener<WMouseEvent>()
	                {
	                    public void trigger(WMouseEvent a) 
	                    {
	                        DataComboMessage<NtSequence> msg = addSeqData();
	
	                        seqComboBox.setCurrentItem(msg);
	                        setSequenceData(msg.getValue());
	                    }
	                });
	        
	        deleteButton.clicked.addListener(this, new Signal1.Listener<WMouseEvent>()
	                {
	                    public void trigger(WMouseEvent a) 
	                    {
	                        if(seqComboBox.count()==1)
	                        {
	                            UIUtils.showWarningMessageBox(ViralIsolateMainForm.this, tr("form.viralIsolate.warning.minimumOneSequence"));
	                        }
	                        else
	                        {
	                            deleteSequence();
	                            seqComboBox.enable();
	                            addButton.enable();
	                        }
	                    }
	                });
        }
        
        if (viralIsolateForm_.isEditable()) {
	        cancelButton.clicked.addListener(this, new Signal1.Listener<WMouseEvent>()
	                {
	                    public void trigger(WMouseEvent a) 
	                    {                        
	                        setSequenceData(((DataComboMessage<NtSequence>)seqComboBox.currentText()).getValue());
	                        
	                        seqComboBox.enable();
	                        addButton.enable();
	                        fastaLabel_.setText(lt(""));
	                    }
	                });
	        
	        confirmButton.clicked.addListener(this, new Signal1.Listener<WMouseEvent>()
	                {
	                    public void trigger(WMouseEvent a) 
	                    {                
	                        confirmSequence();
	                        
	                        seqComboBox.enable();
	                        addButton.enable();
	                    }
	                });
        }
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
        if(seq.getNtSequenceIi()!=null) {
        	Transaction t = RegaDBMain.getApp().createTransaction();
        	t.update(seq);
        	t.commit();
        }
        
        seqLabelTF.setText(seq.getLabel());
        seqDateTF.setDate(seq.getSequenceDate());
        ntTF.setText(seq.getNucleotides());
        
        for(TestResult tr : seq.getTestResults()){
            if(tr.getTest().getDescription().equals(RegaDBWtsServer.getSubtypeTest()) && tr.getTest().getTestType().getDescription().equals(RegaDBWtsServer.getSubtypeTestType())){
                subTypeTF.setText(tr.getValue());
                break;
            }
        }
        
        Genome genome = getGenome(seq);
        if(genome != null)
            genomeTF.setText(genome.getOrganismName());
    }
    
    //TODO move these two functions to a more appropriate place
    public static Genome getGenome(ViralIsolate vi){
        Genome genome=null;
        if(vi.getNtSequences().size() > 0){
            NtSequence ntSeq = vi.getNtSequences().iterator().next();
            genome = getGenome(ntSeq);
        }
        return genome;
    }
    public static Genome getGenome(NtSequence ntSeq){
        Genome genome=null;
        if(ntSeq.getAaSequences().size() > 0){
            AaSequence aaSeq = ntSeq.getAaSequences().iterator().next();
            genome = aaSeq.getProtein().getOpenReadingFrame().getGenome();
        }
        return genome;
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
    
    @SuppressWarnings("unchecked")
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
                if(tr.getTest().getDescription().equals(RegaDBWtsServer.getSubtypeTest()) && tr.getTest().getTestType().getDescription().equals(RegaDBWtsServer.getSubtypeTestType())){
                    removedTestResults.add(tr);
                    break;
                }
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
        ViralIsolate vi = viralIsolateForm_.getViralIsolate();
        if(vi.getNtSequences().size() > 0){
            BlastAnalysis blastAnalysis = new BlastAnalysis(vi.getNtSequences().iterator().next(), RegaDBMain.getApp().getLogin().getUid());
            try{
                blastAnalysis.launch(RegaDBMain.getApp().getLogin());
                Genome genome = blastAnalysis.getGenome();
                
                if(genome != null){
                    FullAnalysis fullAnalysis = new FullAnalysis(viralIsolateForm_.getViralIsolate(), genome);
                    AnalysisPool.getInstance().launchAnalysis(fullAnalysis, RegaDBMain.getApp().getLogin());
                }
            }
            catch(UnsupportedGenomeException e){
                UIUtils.showWarningMessageBox(viralIsolateForm_, tr("form.viralIsolate.warning.unsupportedGenome"));
            }
            catch(ServiceUnavailableException e){
                
            }
            catch(ServiceException e){
                
            }
        }
    }

    private void setFieldListeners()
    {
        seqLabelTF.addChangeListener(new Signal.Listener()
                    {
                        public void trigger() 
                        {
                            seqComboBox.disable();
                            addButton.disable();
                        }
                    });
                    
        seqDateTF.addChangeListener(new Signal.Listener()
                    {
                        public void trigger() 
                        {
                            seqComboBox.disable();
                            addButton.disable();
                        }
                    });
                    
        ntTF.addChangeListener(new Signal.Listener()
                    {
                        public void trigger() 
                        {
                            seqComboBox.disable();
                            addButton.disable();
                        }
                    });
    }
    
    public boolean checkSampleId(){
        return checkSampleId(sampleIdTF.getFormText());
    }
    
    public boolean checkSampleId(String id){
        boolean unique=true;

        Transaction t = RegaDBMain.getApp().createTransaction();
        Integer ii = viralIsolateForm_.getViralIsolate().getViralIsolateIi();
        
        for(Dataset ds : RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem().getDatasets()){
            ViralIsolate vi = t.getViralIsolate(ds, id);
            if(vi != null && !vi.getViralIsolateIi().equals(ii)){
                unique = false;
                break;
            }
        }
        
        t.commit();
        return unique;
    }
}
