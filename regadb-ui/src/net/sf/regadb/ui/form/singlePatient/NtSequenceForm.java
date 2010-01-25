package net.sf.regadb.ui.form.singlePatient;

import java.io.File;

import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.analysis.functions.FastaRead;
import net.sf.regadb.analysis.functions.FastaReadStatus;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FileUpload;
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
	
	private Label basepairsL;
	private TextField basepairsF;
	
	private WPushButton deleteB;

	public NtSequenceForm(ViralIsolateMainForm viralIsolateMainForm, String label){
		this.viralIsolateMainForm = viralIsolateMainForm;
		this.setNtSequence(new NtSequence());
		init();
		
		labelF.setText(label);
	}
	
	public NtSequenceForm(ViralIsolateMainForm viralIsolateMainForm, NtSequence ntSequence){
		this.viralIsolateMainForm = viralIsolateMainForm;
		this.setNtSequence(ntSequence);
		init();
		fillData();
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
		table.addLineToTable(subtypeL, subtypeF);
		
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
	}
	
	void save(){
		ntSequence.setLabel(labelF.getFormText());
		ntSequence.setSequenceDate(seqDateF.getDate());
		ntSequence.setNucleotides(ntF.getFormText());
		ntSequence.setAligned(false);
	}
	
	private TestResult getSubtype(NtSequence ntSequence){
		for(TestResult tr : ntSequence.getTestResults()){
			if(StandardObjects.getSubtypeTestDescription().equals(tr.getTest().getDescription()))
				return tr;
		}
		return null;
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
}
