package net.sf.regadb.ui.form.singlePatient;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.AnalysisPool;
import net.sf.regadb.service.wts.FullAnalysis;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FileUpload;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.NucleotideField;
import net.sf.regadb.ui.framework.forms.fields.TestComboBox;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.MyComboBox;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.TextFormat;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WContainerWidget;
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
	private FormTable table_;
	private Label sampleDateL;
	private DateField sampleDateTF;
	private Label sampleIdL;
	private TextField sampleIdTF;

	// Sequence group
	private MyComboBox seqComboBox;
	private WPushButton addButton;
	private WPushButton deleteButton;

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
    
    private List<FormField> testFormFields_ = new ArrayList<FormField>();

	public ViralIsolateMainForm(ViralIsolateForm viralIsolateForm)
	{
		super();
		viralIsolateForm_ = viralIsolateForm;

		init();
	}

	public void init()
	{
		// General group
		table_ = new FormTable(this);
		sampleDateL = new Label(tr("form.viralIsolate.editView.sampleDate"));
		sampleDateTF = new DateField(viralIsolateForm_.getInteractionState(), viralIsolateForm_, RegaDBSettings.getInstance().getDateFormat());
		sampleDateTF.setMandatory(true);
		table_.addLineToTable(sampleDateL, sampleDateTF);
		sampleIdL = new Label(tr("form.viralIsolate.editView.sampleId"));
		sampleIdTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_){
		    public boolean checkUniqueness(){
		        return checkSampleId(getFormText());
		    }
		};
		sampleIdTF.setMandatory(true);
		table_.addLineToTable(sampleIdL, sampleIdTF);
		
		Transaction tr = RegaDBMain.getApp().createTransaction();
        for(Test t : tr.getTests(StandardObjects.getViralIsolateAnalysisTestObject())) {
            Label l = new Label(TestComboBox.getLabel(t));
            FormField testResultField;
            if(ValueTypes.getValueType(t.getTestType().getValueType()) == ValueTypes.NOMINAL_VALUE) {
                testResultField = new ComboBox(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
                for(TestNominalValue tnv : t.getTestType().getTestNominalValues()) {
                    ((ComboBox)testResultField).addItem(new DataComboMessage<TestNominalValue>(tnv, tnv.getValue()));
                }
                ((ComboBox)testResultField).sort();
            } else {
                testResultField = viralIsolateForm_.getTextField(ValueTypes.getValueType(t.getTestType().getValueType()));
            }

            table_.addLineToTable(l, testResultField);
            testFormFields_.add(testResultField);
        }

	    // Sequence group
		Label currentSequenceL = new Label(
				tr("form.viralIsolate.label.currentSequence"));
		seqComboBox = new MyComboBox();
		table_.addLineToTable(currentSequenceL, seqComboBox);

		// NtSeq group
		seqLabel = new Label(tr("form.viralIsolate.editView.seqLabel"));
		seqLabelTF = new TextField(viralIsolateForm_.getInteractionState(),
				viralIsolateForm_);
		seqLabelTF.setMandatory(true);
		table_.addLineToTable(seqLabel, seqLabelTF);
		seqDateL = new Label(tr("form.viralIsolate.editView.seqDate"));
		seqDateTF = new DateField(viralIsolateForm_.getInteractionState(),
				viralIsolateForm_, RegaDBSettings.getInstance().getDateFormat());
		table_.addLineToTable(seqDateL, seqDateTF);
		genomeL = new Label(tr("form.viralIsolate.editView.genome"));
		genomeTF = new TextField(viralIsolateForm_.getInteractionState(),
				viralIsolateForm_);
		table_.addLineToTable(genomeL, genomeTF);
		subTypeL = new Label(tr("form.viralIsolate.editView.subType"));
		subTypeTF = new TextField(viralIsolateForm_.getInteractionState(),
				viralIsolateForm_);
		table_.addLineToTable(subTypeL, subTypeTF);

		if (viralIsolateForm_.isEditable()) {
			genomeL.setHidden(true);
			genomeTF.setHidden(true);
			subTypeL.setHidden(true);
			subTypeTF.setHidden(true);
			WTable uploadTable = new WTable();
			upload_ = new FileUpload(viralIsolateForm_.getInteractionState(),
					viralIsolateForm_);
			uploadTable.getElementAt(1, 0).addWidget(upload_);
			autoFix_ = new WCheckBox(
					tr("formfield.ntfield.checkbox.autofixSequence"),
					uploadTable.getElementAt(2, 0));
			fastaLabel_ = new WText(uploadTable.getElementAt(0, 0));
			fastaLabel_.setStyleClass("viral-isolate-fasta-label");
			upLoadL = new Label(tr("form.viralIsolate.editView.uploadlabel"));
			table_.addLineToTable(upLoadL, uploadTable);
            
            upload_.getFileUpload().uploaded().addListener(this, new Signal.Listener()
            {
                   public void trigger() 
                   {                
                	   upload_.setAnchor("", "");
                       if(upload_.getFileUpload().getSpoolFileName()!=null)
                       {
	                	   File fastaFile = new File(upload_.getFileUpload().getSpoolFileName());
	                            
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
	                           fastaLabel_.setText("["+read.fastaHeader_+"]");
	                       }
	                       else
	                       {
	                           ntTF.setText(read.xna_);
	                           seqComboBox.disable();
	                           addButton.disable();
	                           fastaLabel_.setText("["+read.fastaHeader_+"]");
	                       }
                       }
                   }
            });
        }
		
        ntL = new Label(tr("form.viralIsolate.editView.nucleotides"));
        ntTF = new NucleotideField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
        ntTF.setMandatory(true);
        ntTF.setTextFormat(TextFormat.XHTMLText);
        table_.addLineToTable(ntL, ntTF);
       
        if (viralIsolateForm_.isEditable()) {
            int row = table_.getRowCount();
            int col = 1;
            table_.getElementAt(row, 0).setStyleClass("form-label-area");
            table_.getElementAt(row, col).setStyleClass("navigation");
           
            addButton = new WPushButton(tr("form.viralIsolate.addButton"), table_.getElementAt(row, col));
            deleteButton = new WPushButton(tr("form.viralIsolate.deleteButton"), table_.getElementAt(row, col));
        }
	}
	
	@SuppressWarnings("unchecked")
    public void fillData(ViralIsolate vi)
	{
		sampleDateTF.setDate(vi.getSampleDate());
		sampleIdTF.setText(vi.getSampleId());
		
		List<Test> tests = 
			RegaDBMain.getApp().createTransaction()
			.getTests(StandardObjects.getViralIsolateAnalysisTestObject());
		
		for(int i = 0; i < tests.size(); i++) {
			TestResult theTr = null;
			for (TestResult tr : vi.getTestResults()) {
				if (tr.getTest().getDescription().equals(tests.get(i).getDescription())) 
					theTr = tr;
			}

			FormField f = testFormFields_.get(i);
			if (theTr != null) {
				if (f instanceof ComboBox) {
					((ComboBox) f).selectItem(theTr.getTestNominalValue().getValue());
				} else {
					if (theTr.getValue() != null)
						f.setText(theTr.getValue());
					else 
						f.setText(new String(theTr.getData()));
				}
			} else {
				//hide?
			}
		}
        
        for(NtSequence ntseq : vi.getNtSequences())
        {
            if(ntseq.getLabel()==null || ntseq.getLabel().equals(""))
			{
				ntseq.setLabel(getUniqueSequenceLabel(vi));
            }
            seqComboBox.addItem(new DataComboMessage<NtSequence>(ntseq, ntseq.getLabel()));
        }
        seqComboBox.setCurrentIndex(0);
       
        setSequenceData(((DataComboMessage<NtSequence>)seqComboBox.getCurrentText()).getDataValue());
        
        setFieldListeners();
        
        seqComboBox.changed().addListener(this, new Signal.Listener()
            {
                public void trigger() 
                {
                    setSequenceData(((DataComboMessage<NtSequence>)seqComboBox.getCurrentText()).getDataValue());
                }
            });

        if (viralIsolateForm_.isEditable()) {
	        addButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
	                {
	                    public void trigger(WMouseEvent a) 
	                    {
	                        DataComboMessage<NtSequence> msg = addSeqData();
	
	                        seqComboBox.setCurrentItem(msg);
	                        setSequenceData(msg.getDataValue());
	                    }
	                });
	        
	        deleteButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
	                {
	                    public void trigger(WMouseEvent a) 
	                    {
	                        if(seqComboBox.getCount()==1)
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
            if(tr.getTest().getDescription().equals(StandardObjects.getSubtypeTestDescription())
            		&& tr.getTest().getTestType().getDescription().equals(StandardObjects.getSubtypeTestTypeDescription())){
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
        NtSequence currentSequence = ((DataComboMessage<NtSequence>)seqComboBox.getCurrentText()).getDataValue();
        removedSequences.add(currentSequence);
        seqComboBox.removeCurrentItem();
        
        seqComboBox.setCurrentIndex(seqComboBox.getCount()-1);
        
        setSequenceData(((DataComboMessage<NtSequence>)seqComboBox.getCurrentText()).getDataValue());
    }
    
    void confirmSequence()
    {
        if(validateSequenceFields())
        {
            NtSequence currentSeq = ((DataComboMessage<NtSequence>)seqComboBox.getCurrentText()).getDataValue();
            currentSeq.setLabel(seqLabelTF.getFormText());
            currentSeq.setSequenceDate(seqDateTF.getDate());
            currentSeq.setNucleotides(ntTF.getFormText());
            
            for(AaSequence aaseq : currentSeq.getAaSequences())
            {
                removedAaSequences.add(aaseq);
            }
            
            for(TestResult tr : currentSeq.getTestResults())
            {
                if(tr.getTest().getDescription().equals(StandardObjects.getSubtypeTestDescription())
                		&& tr.getTest().getTestType().getDescription().equals(StandardObjects.getSubtypeTestTypeDescription())){
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
    
    public void startAnalysis(Genome genome)
    {
        ViralIsolate vi = viralIsolateForm_.getViralIsolate();
        if(vi.getNtSequences().size() > 0){
            FullAnalysis fullAnalysis = new FullAnalysis(viralIsolateForm_.getViralIsolate(), genome);
            AnalysisPool.getInstance().launchAnalysis(fullAnalysis, RegaDBMain.getApp().getLogin());
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
    
    public MyComboBox getSeqComboBox(){
        return seqComboBox;
    }
    
    public void setSampleId(String sampleId){
        sampleIdTF.setText(sampleId);
    }
    public void setSampleDate(Date sampleDate){
        sampleDateTF.setDate(sampleDate);
    }
}
