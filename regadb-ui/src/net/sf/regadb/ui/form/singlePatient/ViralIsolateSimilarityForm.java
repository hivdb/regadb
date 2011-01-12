package net.sf.regadb.ui.form.singlePatient;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.regadb.contamination.SequenceDistancesQuery;
import net.sf.regadb.contamination.SequenceDistancesQuery.OutputType;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.sequencedb.SequenceUtils.SequenceDistance;
import net.sf.regadb.ui.form.administrator.IsolateTable;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.AnchorTarget;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WBreak;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WDoubleValidator;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WResource;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WValidator;
import eu.webtoolkit.jwt.servlet.WebRequest;
import eu.webtoolkit.jwt.servlet.WebResponse;

public class ViralIsolateSimilarityForm extends TabForm {

	private ViralIsolateForm viralIsolateForm;
	
	private Map<String,IsolateTable> tables;
	private Map<String,Boolean> dirty;
	private Map<String,NtSequence> sequences;
	
	private WLineEdit minimumSimilarityTF;
	
	private WComboBox sequenceCombo;
	
	private WAnchor downloadSequences;
	
	public ViralIsolateSimilarityForm(ViralIsolateForm viralIsolateForm){
		super();
		this.viralIsolateForm = viralIsolateForm;
	}
	
	@Override
	public void initialize() {
		WTable layout = new WTable(this);
		
        new WLabel(WString.tr("form.viralIsolate.similarity.sequenceLabel"),layout.getElementAt(0,0));
        sequenceCombo = new WComboBox(layout.getElementAt(0,1));

		layout.getElementAt(1, 0).addWidget(new WLabel(WString.tr("form.viralIsolate.similarity.minimum")));
		minimumSimilarityTF = new WLineEdit(layout.getElementAt(1, 1));
		WDoubleValidator dval = new WDoubleValidator();
		dval.setMandatory(true);
		minimumSimilarityTF.setValidator(dval);
				
		WPushButton submit = new WPushButton(WString.tr("form.viralIsolate.similarity.submit"),layout.getElementAt(1, 2));
		
		new WBreak(this);
		
		Signal.Listener listener = new Signal.Listener() {
            public void trigger() {
            	if(validate()){
            		setAllDirty();
            		showSequence();
            	}
            }
        };
        
        minimumSimilarityTF.enterPressed().addListener(this,listener);
        
        submit.clicked().addListener(this,listener);

		tables = new TreeMap<String,IsolateTable>();
		dirty = new TreeMap<String,Boolean>();
		sequences = new TreeMap<String,NtSequence>();

		for(final NtSequence nt : viralIsolateForm.getViralIsolate().getNtSequences()){
			
			IsolateTable table = new IsolateTable(this);
			table.hide();
			
			sequences.put(nt.getLabel(),nt);
			tables.put(nt.getLabel(),table);
			dirty.put(nt.getLabel(), true);
			
			sequenceCombo.addItem(nt.getLabel());
		}
		downloadSequences = new WAnchor(this);
		downloadSequences.setText(tr("form.viralIsolate.similarity.downloadSequences"));
		downloadSequences.setTarget(AnchorTarget.TargetNewWindow);
		downloadSequences.setHidden(true);
		
		sequenceCombo.setCurrentIndex(0);
		sequenceCombo.changed().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				showSequence();
			}
		});

		double minimumSimilarity = RegaDBSettings.getInstance().getSequenceDatabaseConfig().getMinimumSimilarity();
		minimumSimilarityTF.setText(minimumSimilarity+"");
		showSequence();
	}
		
	private void getSimilarSequences(NtSequence sequence, List<NtSequence> similarSequences, List<Double> similarities) {
		SequenceDb sdb = SequenceDb.getInstance(RegaDBSettings.getInstance().getSequenceDatabaseConfig().getPath());
		
		SequenceDistancesQuery sdq = new SequenceDistancesQuery(sequence, OutputType.ExtraPatient, null);
		sdb.query(sequence.getViralIsolate().getGenome(), sdq);
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		
		final double minimumSimilarity = Double.parseDouble(minimumSimilarityTF.getText());
		
		for(Map.Entry<Integer, SequenceDistance> sd : sdq.getSequenceDistances().entrySet()){ 
			double diff = (double)sd.getValue().numberOfDifferences / sd.getValue().numberOfPositions;
			double similarity = 1.0 - diff;
			if(similarity >= minimumSimilarity) {
				similarSequences.add(t.getSequence(sd.getKey()));
				if (similarities != null)
					similarities.add(similarity);
			}
		}
		
		t.commit();
	}
	
	private void writeFasta(NtSequence ntSeq, Writer writer) throws IOException {
		List<NtSequence> similarSequences = new ArrayList<NtSequence>();
		getSimilarSequences(ntSeq, similarSequences, null);
	
		for (NtSequence sequence : similarSequences) {
			writer.append(">" + 
					new Patient(sequence.getViralIsolate().getPatient(), Privileges.READONLY.getValue()).getPatientId() +
					"_" + sequence.getViralIsolate().getSampleId() +
					"_" + sequence.getLabel().replace(' ', '_') +
					"\n");
			writer.append(ntSeq.getNucleotides() + "\n");
		}
	}

	private void setAllDirty(){
		for(Map.Entry<String, Boolean> me : dirty.entrySet()){
			me.setValue(true);
		}
	}
	
	private boolean validate(){
		return minimumSimilarityTF.validate() == WValidator.State.Valid;
	}
	
	private void showSequence() {
		showSequence(sequenceCombo.getCurrentText().getValue(),
				Double.parseDouble(minimumSimilarityTF.getText()));
	}
	
	public void showSequence(String sequenceLabel, double minimumSimilarity){
		if(dirty.get(sequenceLabel)){
			fill(sequenceLabel,minimumSimilarity);
			dirty.put(sequenceLabel, false);
		}
		
		for(Map.Entry<String, IsolateTable> me : tables.entrySet()){
			if(me.getKey().equals(sequenceLabel))
				me.getValue().show();
			else
				me.getValue().hide();
		}
		
		if(tables.get(sequenceLabel).getRowCount() > 1){ //count header
			final NtSequence nt = sequences.get(sequenceLabel);
			downloadSequences.setResource(new WResource(){
				protected void handleRequest(WebRequest request, WebResponse response) throws IOException {
					writeFasta(nt, response.out());
				}
			});
			
			downloadSequences.show();
		}
		else
			downloadSequences.hide();
	}
	
	public void fill(String sequenceLabel, double minimumSimilarity){
		IsolateTable table = tables.get(sequenceLabel);
	
		List<NtSequence> similarSequences = new ArrayList<NtSequence>();
		List<Double> similarities = new ArrayList<Double>();
		getSimilarSequences(sequences.get(sequenceLabel), similarSequences, similarities);
		
		for (int i = 0 ; i < similarSequences.size(); i++) {
			NtSequence sequence = similarSequences.get(i);
			Patient p = new Patient(sequence.getViralIsolate().getPatient(), Privileges.READONLY.getValue());
			
			table.addRow(p.getPatientIi(),
				  	p.getPatientId(),
				  	sequence.getViralIsolate().getViralIsolateIi(),
				  	sequence.getViralIsolate().getSampleId(),
				  	sequence.getLabel(),
				  	similarities.get(i) + "");
		}
	}
}
