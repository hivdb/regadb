package net.sf.regadb.ui.form.singlePatient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.tools.FastaFile;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FileUpload;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.NucleotideField;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.regadb.util.settings.UITestItem;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.TextFormat;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WValidator;

public class NtSequenceForm extends WContainerWidget{
	private ViralIsolateMainForm viralIsolateMainForm;
	private NtSequence ntSequence;
	
	private FormTable table;
	
	private Label labelL;
	private TextField labelF;
	
	private Label seqDateL;
	private DateField seqDateF;
	
	private Label uploadL;
	private FileUpload uploadF;
	private WCheckBox autofix;
	private WText fastaLabel;
	
	private Label ntL;
	private NucleotideField ntF;
	
	private Label subtypeL;
	private TextField subtypeF;
	
	private List<TestResult> removedTestResult = new ArrayList<TestResult>();
	
	private Label basepairsL;
	private TextField basepairsF;
	
	private WPushButton deleteB;
	
	private TestListWidget testList;

	public NtSequenceForm(ViralIsolateMainForm viralIsolateMainForm, String label, List<UITestItem> testItems){
		this.viralIsolateMainForm = viralIsolateMainForm;
		this.setNtSequence(new NtSequence());
		this.testList = createTestList(getInteractionState(), testItems);
		
		init();
		
		labelF.setText(label);
	}
	
	public NtSequenceForm(ViralIsolateMainForm viralIsolateMainForm, NtSequence ntSequence, List<UITestItem> testItems){
		this.viralIsolateMainForm = viralIsolateMainForm;
		this.setNtSequence(ntSequence);
		this.testList = createTestList(getInteractionState(), testItems);
		
		init();
		
		fillData();
	}
	
	private TestListWidget createTestList(InteractionState is, List<UITestItem> testItems) {
		Set<TestResult> results = null;
		if (ntSequence != null)
			results = ntSequence.getTestResults();
		
		return new TestListWidget(is, testItems, results) {
			@Override
			public void removeTestResult(TestResult tr) {
            	removedTestResult.add(tr);
			}

			@Override
		    public TestResult createTestResult(Test t) {
				TestResult tr = new TestResult();
				tr.setTest(t);
				tr.setNtSequence(ntSequence);
				
				tr.setPatient(viralIsolateMainForm.viralIsolateForm_.getViralIsolate().getPatient());
				ntSequence.getTestResults().add(tr);
				
				return tr;
		    }
		};
	}
	
	private FormField getTextField(TestType tt){
		return getTextField(tt, getInteractionState());
	}
	
	@SuppressWarnings("unchecked")
	private FormField getTextField(TestType tt, InteractionState interactionState){
		ValueTypes vt = ValueTypes.getValueType(tt.getValueType());
		FormField f = FormField.getTextField(
				vt,
				interactionState,
				getViralIsolateForm());
		
		if(vt == ValueTypes.NOMINAL_VALUE){
			@SuppressWarnings("rawtypes")
			ComboBox b = (ComboBox)f;
			
			for(TestNominalValue tnv : tt.getTestNominalValues())
            {
                b.addItem(new DataComboMessage<TestNominalValue>(tnv, tnv.getValue()));
            }
            b.sort();
            b.addNoSelectionItem();
		}
		
		return f;
	}

	private void init(){
		table = new FormTable(this);
		
		labelL = new Label(tr("form.viralIsolate.editView.seqLabel"));
		labelF = new TextField(getInteractionState(), getViralIsolateForm());
		if(isEditable()){
//TODO remove hard-coded 50 char limit
			labelF.setValidator(new WValidator(true){
				@Override
				public State validate(String input) {
					if(input != null && input.length() > 50)
						return State.Invalid;
					else
						return super.validate(input);
				}
			});
		}
		table.addLineToTable(labelL, labelF);
		
		seqDateL = new Label(tr("form.viralIsolate.editView.seqDate"));
		seqDateF = new DateField(getInteractionState(), getViralIsolateForm(), RegaDBSettings.getInstance().getDateFormat());
		table.addLineToTable(seqDateL, seqDateF);

		subtypeL = new Label(tr("form.viralIsolate.editView.subType"));
		subtypeF = new TextField(getInteractionState(), getViralIsolateForm());
		table.addLineToTable(subtypeL, subtypeF);
		
		testList.init(getInteractionState(), getViralIsolateForm(), table);
		
		basepairsL = new Label(tr("form.viralIsolate.editView.basePairs"));
		basepairsF = new TextField(getInteractionState(), getViralIsolateForm());
		table.addLineToTable(basepairsL, basepairsF);
		
		if(isEditable()){
			subtypeL.setHidden(true);
			subtypeF.setHidden(true);
			basepairsL.setHidden(true);
			basepairsF.setHidden(true);

			WTable uploadTable = new WTable();
			uploadF = new FileUpload(getInteractionState(),getViralIsolateForm());
			uploadTable.getElementAt(1, 0).addWidget(uploadF);
			autofix = new WCheckBox(
					tr("formfield.ntfield.checkbox.autofixSequence"),
					uploadTable.getElementAt(2, 0));
			fastaLabel = new WText(uploadTable.getElementAt(0, 0));
			fastaLabel.setStyleClass("viral-isolate-fasta-label");
			uploadL = new Label(tr("form.viralIsolate.editView.uploadlabel"));
			table.addLineToTable(uploadL, uploadTable);
            
            uploadF.getFileUpload().uploaded().addListener(this, new Signal.Listener()
            {
                   public void trigger() 
                   {                
                	   uploadF.setAnchor("", "");
                       if(uploadF.getFileUpload().getSpoolFileName()!=null)
                       {
                    	   try{
		                	   FastaFile fastaFile = new FastaFile(
		                			   new File(uploadF.getFileUpload().getSpoolFileName()),
		                			   autofix.isChecked());
		                       fastaFile.getFile().delete();
		                            
		                       if(fastaFile.size() == 0)
		                       {
		                    	   UIUtils.showWarningMessageBox(NtSequenceForm.this, tr("form.viralIsolate.warning.invalidFastaFile"));
		                       }
		                       else{
		                    	   if(fastaFile.size() > 1)
		                    		   UIUtils.showWarningMessageBox(NtSequenceForm.this, tr("form.viralIsolate.warning.multipleSequences"));

		                    	   NtSequence nt = fastaFile.get(0);
		                           ntF.setText(nt.getNucleotides());
		                           fastaLabel.setText("["+nt.getLabel()+"]");
		                           labelF.setText(nt.getLabel());
		                       }
                    	   } catch(FileNotFoundException e){
                    		   UIUtils.showWarningMessageBox(NtSequenceForm.this, tr("form.viralIsolate.warning.fastaFileNotFound"));                    		   
                    	   } catch(IOException e){
                    		   UIUtils.showWarningMessageBox(NtSequenceForm.this, tr("form.viralIsolate.warning.invalidFastaFile"));
                    		   e.printStackTrace();
                    	   }
                       }
                   }
            });
        }
		
        ntL = new Label(tr("form.viralIsolate.editView.nucleotides"));
        ntF = new NucleotideField(getInteractionState(), getViralIsolateForm());
        ntF.setMandatory(true);
        ntF.setTextFormat(TextFormat.XHTMLText);
        table.addLineToTable(ntL, ntF);
       
        if (isEditable()) {
            int row = table.getRowCount();
            int col = 1;
            table.getElementAt(row, 0).setStyleClass("form-label-area");
            table.getElementAt(row, col).setStyleClass("navigation");
           
            deleteB = new WPushButton(tr("form.viralIsolate.deleteButton"), table.getElementAt(row, col));
            deleteB.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
	                {
	                    public void trigger(WMouseEvent a) 
	                    {
	                    	getViralIsolateMainForm().delete(NtSequenceForm.this);
	                    }
	                });
        }
	}
	
	void uninit(){
		getViralIsolateForm().removeFormField(labelF);
		getViralIsolateForm().removeFormField(seqDateF);
		getViralIsolateForm().removeFormField(uploadF);
		getViralIsolateForm().removeFormField(ntF);
		getViralIsolateForm().removeFormField(subtypeF);
		getViralIsolateForm().removeFormField(basepairsF);
	}
	
	private ViralIsolateMainForm getViralIsolateMainForm(){
		return viralIsolateMainForm;
	}
	private ViralIsolateForm getViralIsolateForm(){
		return getViralIsolateMainForm().viralIsolateForm_;
	}
	private boolean isEditable(){
		return getViralIsolateForm().isEditable();
	}
	
	private InteractionState getInteractionState(){
		return getViralIsolateForm().getInteractionState();
	}
	
	void fillData(){
		labelF.setText(ntSequence.getLabel());
		seqDateF.setDate(ntSequence.getSequenceDate());
		
		TestResult tr = getSubtype(ntSequence);
		if(tr != null)
			subtypeF.setText(tr.getValue());
		
		ntF.setText(ntSequence.getNucleotides());
		basepairsF.setText(ntSequence.getNucleotides() == null ? "0" : ntSequence.getNucleotides().length()+"");
		
		testList.fillData(ntSequence.getTestResults());
	}
	
	void save(){
		ntSequence.setLabel(labelF.getFormText());
		ntSequence.setSequenceDate(seqDateF.getDate());
		ntSequence.setNucleotides(ntF.getFormText());
		ntSequence.setAligned(false);
		
		testList.saveData(ntSequence.getTestResults());
	}
	
	private TestResult getSubtype(NtSequence ntSequence){
		for(TestResult tr : ntSequence.getTestResults()){
			if(StandardObjects.getSubtypeTestDescription().equals(tr.getTest().getDescription()))
				return tr;
		}
		return null;
	}
	
	public String getNucleotides() {
		return ntF.text();
	}
	
	public void setNucleotides(String nucleotides) {
		ntF.setText(nucleotides);
	}

	void setNtSequence(NtSequence ntSequence) {
		this.ntSequence = ntSequence;
	}

	NtSequence getNtSequence() {
		return ntSequence;
	}
	
	String getLabel(){
		return labelF.getFormText();
	}
	
	List<TestResult> getRemovedTestResults(){
		return removedTestResult;
	}
}
