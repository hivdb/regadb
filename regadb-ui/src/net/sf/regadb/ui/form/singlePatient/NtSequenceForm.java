package net.sf.regadb.ui.form.singlePatient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.analysis.functions.FastaRead;
import net.sf.regadb.analysis.functions.FastaReadStatus;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.io.util.StandardObjects;
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
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.TextFormat;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;

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
	
	private int testRow;
	private List<Test> tests = new ArrayList<Test>();
	private List<FormField> testInputs = new ArrayList<FormField>();
	private List<TestResult> testResults = new ArrayList<TestResult>();
	private List<TestResult> removedTestResult = new ArrayList<TestResult>();
	
	private Label basepairsL;
	private TextField basepairsF;
	
	private WPushButton deleteB;

	public NtSequenceForm(ViralIsolateMainForm viralIsolateMainForm, String label, List<Test> tests){
		this.viralIsolateMainForm = viralIsolateMainForm;
		this.setNtSequence(new NtSequence());
		this.tests = tests;
		init();
		
		labelF.setText(label);
	}
	
	public NtSequenceForm(ViralIsolateMainForm viralIsolateMainForm, NtSequence ntSequence, List<Test> tests){
		this.viralIsolateMainForm = viralIsolateMainForm;
		this.setNtSequence(ntSequence);
		this.tests = tests;
		init();
		fillData();
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
		labelF.setMandatory(true);
		table.addLineToTable(labelL, labelF);
		
		seqDateL = new Label(tr("form.viralIsolate.editView.seqDate"));
		seqDateF = new DateField(getInteractionState(), getViralIsolateForm(), RegaDBSettings.getInstance().getDateFormat());
		table.addLineToTable(seqDateL, seqDateF);

		subtypeL = new Label(tr("form.viralIsolate.editView.subType"));
		subtypeF = new TextField(getInteractionState(), getViralIsolateForm());
		testRow = table.addLineToTable(subtypeL, subtypeF)+1;
		
		for(Test test : tests){
			Label label = null;
			FormField input = null;
			if(isEditable() && isEditable(test)){
				label = new Label(test.getDescription());
				input = getTextField(test.getTestType());
				table.addLineToTable(label,input);
			}
			testInputs.add(input);
			testResults.add(null);
		}
		
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
	                	   File fastaFile = new File(uploadF.getFileUpload().getSpoolFileName());
	                            
	                       FastaRead read = FastaHelper.readFastaFile(fastaFile, autofix.isChecked());
	                            
	                       fastaFile.delete();
	                            
	                       if(read.status_==FastaReadStatus.Invalid)
	                       {
	                    	   UIUtils.showWarningMessageBox(NtSequenceForm.this, tr("form.viralIsolate.warning.invalidFastaFile"));
	                       }
	                       else if(read.status_==FastaReadStatus.FileNotFound)
	                       {
	                    	   UIUtils.showWarningMessageBox(NtSequenceForm.this, tr("form.viralIsolate.warning.fastaFileNotFound"));
	                       }
	                       else if(read.status_==FastaReadStatus.MultipleSequences)
	                       {
	                    	   UIUtils.showWarningMessageBox(NtSequenceForm.this, tr("form.viralIsolate.warning.multipleSequences"));
	                       }
	                       else if (read.status_==FastaReadStatus.ValidButFixed)
	                       {
	                    	   UIUtils.showWarningMessageBox(NtSequenceForm.this, tr("form.viralIsolate.warning.autoFixedSequence"));
	                           ntF.setText(read.xna_);
	                           fastaLabel.setText("["+read.fastaHeader_+"]");
	                       }
	                       else
	                       {
	                           ntF.setText(read.xna_);
	                           fastaLabel.setText("["+read.fastaHeader_+"]");
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
	
	private boolean isEditable(Test test){
		return !test.getDescription().equals(StandardObjects.getContaminationClusterFactorTest().getDescription());
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
		
		for(TestResult trr : ntSequence.getTestResults()){
			if(trr.getTest().getAnalysis() == null){
				for(int i=0; i<tests.size(); ++i){
					if(Equals.isSameTest(trr.getTest(),tests.get(i))){
						FormField input;
						
						if(!isEditable() || !isEditable(trr.getTest())){
							Label label = new Label(tests.get(i).getDescription());
							input = getTextField(tests.get(i).getTestType(),InteractionState.Viewing);
							testInputs.set(i, input);
							table.addLineToTable(testRow, label, input);
						}
						else{
							input = testInputs.get(i);
						}
						
						input.setText(
								trr.getTestNominalValue() == null ? trr.getValue() : trr.getTestNominalValue().getValue());
						testResults.set(i, trr);
						
						break;
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	void save(){
		ntSequence.setLabel(labelF.getFormText());
		ntSequence.setSequenceDate(seqDateF.getDate());
		ntSequence.setNucleotides(ntF.getFormText());
		ntSequence.setAligned(false);
		
		for(int i=0; i<tests.size(); ++i){
			Test t = tests.get(i);
			if(t == null)
				continue;
			
			if(isEditable(t)){
				FormField f = testInputs.get(i);
				ValueTypes vt = ValueTypes.getValueType(t.getTestType().getValueType());
				
				if(vt == ValueTypes.NOMINAL_VALUE){
					TestNominalValue value = (TestNominalValue)((ComboBox)f).currentValue();
					if(value == null){
						if(testResults.get(i) != null)
							removedTestResult.add(testResults.get(i));
					}
					else{
						TestResult tr = testResults.get(i);
						if(tr == null){
							tr = new TestResult();
							tr.setNtSequence(ntSequence);
							ntSequence.getTestResults().add(tr);
							tr.setTest(t);
						}
						tr.setTestNominalValue(value);
					}
				}
				else{
					String value = null;
					if(vt == ValueTypes.DATE){
						value = ((DateField)f).getDate() == null ? null : ((DateField)f).getDate().getTime() +"";
					}
					else{
						value = f.getFormText();
						if("".equals(value))
							value = null;
					}
					
					if(value == null){
						if(testResults.get(i) != null)
							removedTestResult.add(testResults.get(i));
					}
					else{
						TestResult tr = testResults.get(i);
						if(tr == null){
							tr = new TestResult();
							tr.setNtSequence(ntSequence);
							ntSequence.getTestResults().add(tr);
							tr.setTest(t);
						}
						tr.setValue(value);
					}
				}
			}
			else{ // not editable, calculated automatically
				TestResult tr = testResults.get(i);
				if(tr != null)
					removedTestResult.add(tr);
			}
		}
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
