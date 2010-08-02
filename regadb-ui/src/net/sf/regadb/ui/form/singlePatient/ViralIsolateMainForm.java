package net.sf.regadb.ui.form.singlePatient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.AaSequence;
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
import net.sf.regadb.service.qc.QC;
import net.sf.regadb.service.wts.FullAnalysis;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TestComboBox;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.regadb.util.settings.ViralIsolateFormConfig;
import net.sf.regadb.util.settings.ViralIsolateFormConfig.TestItem;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;

public class ViralIsolateMainForm extends WContainerWidget
{
	ViralIsolateForm viralIsolateForm_;
	
	private Set<NtSequence> removedSequences = new HashSet<NtSequence>();
	private Set<NtSequence> addedSequences = new HashSet<NtSequence>();
    
    private Set<AaSequence> removedAaSequences = new HashSet<AaSequence>();
    private Set<TestResult> removedTestResults = new HashSet<TestResult>();

	// General group
	private FormTable table_;
	private Label sampleDateL;
	private DateField sampleDateTF;
	private Label sampleIdL;
	private TextField sampleIdTF;

	// Sequence group
	private WContainerWidget ntSequenceContainer;
	private WPushButton addButton;

    private Label genomeL;
    private TextField genomeTF;
    
    private static final String defaultSequenceLabel_ = "Sequence ";
    
    private List<FormField> testFormFields_ = new ArrayList<FormField>();
    
    List<NtSequenceForm> ntSequenceForms = new ArrayList<NtSequenceForm>();

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
		sampleDateTF.setMandatory(RegaDBSettings.getInstance().getInstituteConfig().isSampleDateMandatory());
		table_.addLineToTable(sampleDateL, sampleDateTF);
		sampleIdL = new Label(tr("form.viralIsolate.editView.sampleId"));
		sampleIdTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_){
		    public boolean checkUniqueness(){
		        return checkSampleId();
		    }
		};
		sampleIdTF.setMandatory(true);
		table_.addLineToTable(sampleIdL, sampleIdTF);
		
		Transaction tr = RegaDBMain.getApp().createTransaction();
		ViralIsolateFormConfig config = RegaDBSettings.getInstance().getInstituteConfig().getViralIsolateFormConfig();
        if (config != null)
		for(TestItem ti : config.getTests()) {
        	Test t = tr.getTest(ti.description);
        	if(t != null){
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
        	else{
        		System.err.println("ViralIsolateForm: test does not exist: '"+ ti.description +'\'');
        	}
        }
        tr.commit();

		genomeL = new Label(tr("form.viralIsolate.editView.genome"));
		genomeTF = new TextField(viralIsolateForm_.getInteractionState(),
				viralIsolateForm_);
		table_.addLineToTable(genomeL, genomeTF);

		if (viralIsolateForm_.isEditable()) {
			genomeL.setHidden(true);
			genomeTF.setHidden(true);
        }
		
        if (viralIsolateForm_.isEditable()) {
            int row = table_.getRowCount();
            int col = 1;
            table_.getElementAt(row, 0).setStyleClass("form-label-area");
            table_.getElementAt(row, col).setStyleClass("navigation");
            
            ntSequenceContainer = new WContainerWidget(this);
        	addButton = new WPushButton(tr("form.viralIsolate.addButton"), this);
        }
        else
        	ntSequenceContainer = new WContainerWidget(this);
	}
	
	@SuppressWarnings("unchecked")
    public void fillData(ViralIsolate vi)
	{
		genomeTF.setText(vi.getGenome() == null ? "" : vi.getGenome().getOrganismName());
		
		sampleDateTF.setDate(vi.getSampleDate());
		sampleIdTF.setText(vi.getSampleId());
		
		Transaction trans = RegaDBMain.getApp().createTransaction();
		ViralIsolateFormConfig config = RegaDBSettings.getInstance().getInstituteConfig().getViralIsolateFormConfig();
        if(config != null){
			for(int i = 0; i < config.getTests().size(); i++) {
				Test test = trans.getTest(config.getTests().get(i).description);
				if(test == null)
					continue;
				
				TestResult theTr = null;
				for (TestResult tr : vi.getTestResults()) {
					if (tr.getTest().getDescription().equals(test.getDescription())){
						theTr = tr;
						break;
					}
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
        }
        
        for(NtSequence ntseq : vi.getNtSequences())
        {
            if(ntseq.getLabel()==null || ntseq.getLabel().equals(""))
			{
				ntseq.setLabel(getUniqueSequenceLabel(vi));
            }
        }

        List<NtSequence> sortedSeqs = new ArrayList<NtSequence>(viralIsolateForm_.getViralIsolate().getNtSequences());
        Collections.sort(sortedSeqs, new Comparator<NtSequence>() {
			public int compare(NtSequence seq1, NtSequence seq2) {
				return seq1.getLabel().compareTo(seq2.getLabel());
			}
		});

        for(NtSequence ntSequence : sortedSeqs){
        	addSequenceForm(ntSequence, trans);
        }
        
        if (viralIsolateForm_.isEditable()) {
        	 addButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
	                {
	                    public void trigger(WMouseEvent a) 
	                    {
	                    	Transaction t = RegaDBMain.getApp().createTransaction();
	                    	addSequenceForm(t);
	                    	t.commit();
	                    }
	                });
        }
        trans.commit();
	}
    
    private String getUniqueSequenceLabel(ViralIsolate vi)
    {
    	String label;
        int n = 1;
        boolean dupe = true;

        do{
        	dupe = false;
        	label = defaultSequenceLabel_+n;
        	
	        for(NtSequence ntseq : vi.getNtSequences()){
	        	if(label.equals(ntseq.getLabel())){
	        		dupe = true;
	        		++n;
	        		break;
	        	}
	        }
        }
        while(dupe);
        
        return label;
    }
    private String getUniqueSequenceLabel(List<NtSequenceForm> ntfs)
    {
    	String label;
        int n = ntfs.size()+1;
        boolean dupe = true;

        do{
        	dupe = false;
        	label = defaultSequenceLabel_+n;
        	
	        for(NtSequenceForm ntf : ntfs){
	        	if(label.equals(ntf.getLabel())){
	        		dupe = true;
	        		++n;
	        		break;
	        	}
	        }
        }
        while(dupe);
        
        return label;
    }
    
    private NtSequenceForm addSequenceForm(NtSequence ntSequence, Transaction t){
    	t = RegaDBMain.getApp().createTransaction();
		List<Test> alltests = t.getTests(StandardObjects.getSequenceAnalysisTestObject());
		List<Test> tests = new ArrayList<Test>();
		for(Test test : alltests){
			if(test.getAnalysis() == null){
				tests.add(test);
			}
		}
    	
    	NtSequenceForm ntsf = new NtSequenceForm(this, ntSequence, tests);
    	ntSequenceForms.add(ntsf);
    	ntSequenceContainer.addWidget(ntsf);
    	
    	return ntsf;
    }
    
    private NtSequenceForm addSequenceForm(Transaction t){
	    String label = getUniqueSequenceLabel(ntSequenceForms);
	    NtSequence newSeq = new NtSequence(viralIsolateForm_.getViralIsolate());
	    newSeq.setLabel(label);
	    addSequence(newSeq);
	    
	    return addSequenceForm(newSeq, t);
    }
	
    private void addSequence(NtSequence ntSequence){
    	if(!removedSequences.remove(ntSequence))
    		addedSequences.add(ntSequence);
    }
    
    public void delete(NtSequenceForm ntSequenceForm) {
		if(ntSequenceForms.size()==1)
            UIUtils.showWarningMessageBox(ViralIsolateMainForm.this, tr("form.viralIsolate.warning.minimumOneSequence"));
        else{
            deleteSequence(ntSequenceForm.getNtSequence());
            ntSequenceForms.remove(ntSequenceForm);
            ntSequenceContainer.removeWidget(ntSequenceForm);
            ntSequenceForm.uninit();
        }		
	}
    
    private void deleteSequence(NtSequence ntSequence)
    {
    	if(!addedSequences.remove(ntSequence))
    		removedSequences.add(ntSequence);
    }
    
    public void confirmSequences(Transaction t){
    	Set<String> trugeneAdded = new HashSet<String>();
    	if (RegaDBSettings.getInstance().getInstituteConfig().isTrugeneFix()) {
	    	for(NtSequenceForm ntsf : ntSequenceForms) {
	        	Set<String> seqs = QC.trugeneQC(ntsf.getNucleotides());
	        	if (seqs.size() == 2) {
	        		Iterator<String> i = seqs.iterator();
	        		ntsf.setNucleotides(i.next());
	        		trugeneAdded.add(i.next());
	        	}
	    	}
    	}
    	
    	for (String ntSeq : trugeneAdded) {
    		addSequenceForm(t).setNucleotides(ntSeq);
    	}
    	
    	for(NtSequenceForm ntsf : ntSequenceForms){
        	ntsf.save();
        	
        	removedTestResults.addAll(ntsf.getRemovedTestResults());
        	
        	NtSequence nt = ntsf.getNtSequence();
        	ViralIsolate vi = viralIsolateForm_.getViralIsolate();
        	nt.setViralIsolate(vi);
        	vi.getNtSequences().add(nt);
        	
    		for (AaSequence aaseq : nt.getAaSequences()) {
    			removedAaSequences.add(aaseq);
    		}

    		for (TestResult tr : nt.getTestResults()) {
    			if (tr.getTest().getDescription().equals(
    					StandardObjects.getSubtypeTestDescription())
    					&& tr.getTest().getTestType().getDescription().equals(
    							StandardObjects.getSubtypeTestTypeDescription())) {
    				removedTestResults.add(tr);
    				break;
    			}
    		}
        }    	
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
        
		ViralIsolateFormConfig config = RegaDBSettings.getInstance().getInstituteConfig().getViralIsolateFormConfig();
        if (config != null)
		for(int i = 0; i < config.getTests().size(); i++) {
            TestResult tr = null;
            for (TestResult vi_tr : viralIsolateForm_.getViralIsolate().getTestResults()) {
            	if (vi_tr.getTest().getDescription().equals(config.getTests().get(i).description)){
            		tr = vi_tr;
            		break;
            	}
            }
            FormField f = testFormFields_.get(i);
            if(f instanceof ComboBox) {
                if(((DataComboMessage<TestNominalValue>)((ComboBox)f).currentItem()).getValue()!=null) {
                	if (tr == null)
                		tr = createTestResult(t.getTest(config.getTests().get(i).description));
                    tr.setTestNominalValue(((DataComboMessage<TestNominalValue>)((ComboBox)f).currentItem()).getDataValue());
                }
            } else {
                if(f.text()!=null && !f.text().trim().equals("")) {
                	if (tr == null)
                		tr = createTestResult(t.getTest(config.getTests().get(i).description));
                    tr.setValue(f.text());
                }
                else if(tr != null){
                	viralIsolateForm_.getViralIsolate().getTestResults().remove(tr);
                	t.delete(tr);
                }
            }
        }
        
        viralIsolateForm_.getViralIsolate().setSampleDate(sampleDateTF.getDate());
        viralIsolateForm_.getViralIsolate().setSampleId(sampleIdTF.getFormText());
    }
    
    public TestResult createTestResult(Test t) {
		TestResult tr = new TestResult();
		tr.setTest(t);
		tr.setViralIsolate(viralIsolateForm_.getViralIsolate());
		
		tr.setPatient(viralIsolateForm_.getViralIsolate().getPatient());
		viralIsolateForm_.getViralIsolate().getTestResults().add(tr);
		
		return tr;
    }
    
    public void startAnalysis(Genome genome)
    {
        ViralIsolate vi = viralIsolateForm_.getViralIsolate();
        if(vi.getNtSequences().size() > 0){
            FullAnalysis fullAnalysis = new FullAnalysis(viralIsolateForm_.getViralIsolate(), genome);
            AnalysisPool.getInstance().launchAnalysis(fullAnalysis, RegaDBMain.getApp().getLogin());
        }
    }

    public boolean checkSampleId(){
    	Transaction tr = RegaDBMain.getApp().createTransaction();
    	boolean b = ViralIsolateFormUtils.checkSampleId(sampleIdTF.getFormText(), 
    			viralIsolateForm_.getViralIsolate(),
    			RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem().getDatasets(), tr);
    	tr.commit();
    	return b;
    }
    
    public void setSampleId(String sampleId){
        sampleIdTF.setText(sampleId);
    }
    public void setSampleDate(Date sampleDate){
        sampleDateTF.setDate(sampleDate);
    }
}

