package net.sf.regadb.ui.form.singlePatient;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;

import eu.webtoolkit.jwt.AnchorTarget;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WBreak;
import eu.webtoolkit.jwt.WCheckBox;
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
	
	private List<IsolateTable> tables;
	private List<NtSequence> sequences;
	private List<WAnchor> downloadAnchors;
	
	private WLineEdit minimumSimilarityTF;
	private WCheckBox includePatientIsolatesCB;
	
	public ViralIsolateSimilarityForm(ViralIsolateForm viralIsolateForm){
		super();
		this.viralIsolateForm = viralIsolateForm;
	}
	
	@Override
	public void initialize() {
		WTable layout = new WTable(this);
		layout.getElementAt(0, 0).addWidget(new Label(WString.tr("form.viralIsolate.similarity.minimum")));
		minimumSimilarityTF = new WLineEdit(layout.getElementAt(0, 1));
		WDoubleValidator dval = new WDoubleValidator();
		dval.setMandatory(true);
		minimumSimilarityTF.setValidator(dval);
		
		layout.getElementAt(1, 0).addWidget(new Label(WString.tr("form.viralIsolate.similarity.includePatientIsolates")));
		includePatientIsolatesCB = new WCheckBox(layout.getElementAt(1, 1));
		
		WPushButton submit = new WPushButton(WString.tr("form.viralIsolate.similarity.submit"),layout.getElementAt(1, 2));
		
		new WBreak(this);
		
		Signal.Listener listener = new Signal.Listener() {
            public void trigger() {
            	fill();
            }
        };
        
        minimumSimilarityTF.enterPressed().addListener(this,listener);
        includePatientIsolatesCB.changed().addListener(this, listener);
        
        submit.clicked().addListener(this,listener);

		tables = new ArrayList<IsolateTable>();
		sequences = new ArrayList<NtSequence>();
		downloadAnchors = new ArrayList<WAnchor>();

		for(final NtSequence nt : viralIsolateForm.getViralIsolate().getNtSequences()){
			sequences.add(nt);
			
			new WLabel(nt.getLabel(),this);
			IsolateTable table = new IsolateTable(this);
			tables.add(table);
			
			WAnchor downloadSequences = new WAnchor(this);
			downloadSequences.setText(tr("form.viralIsolate.similarity.downloadSequences"));
			downloadSequences.setResource(new WResource(){
				protected void handleRequest(WebRequest request, WebResponse response) throws IOException {
					writeFasta(nt, response.out());
				}
			});
			downloadSequences.setTarget(AnchorTarget.TargetNewWindow);
			downloadAnchors.add(downloadSequences);
			downloadSequences.setHidden(true);
		}

		double minimumSimilarity = RegaDBSettings.getInstance().getSequenceDatabaseConfig().getMinimumSimilarity();
		minimumSimilarityTF.setText(minimumSimilarity+"");
		fill(minimumSimilarity, false);
	}
	
	private void fill() {
		if(minimumSimilarityTF.validate() == WValidator.State.Valid)
			fill(Double.parseDouble(minimumSimilarityTF.getText()),
					includePatientIsolatesCB.isChecked());
	}
	
	private OutputType getOutputType(boolean includePatientIsolates) {
		OutputType type = null;
		if (!includePatientIsolates)
			type = OutputType.ExtraPatient;
		return type;
	}
	
	//TODO refactor
	private void writeFasta(NtSequence sequence, Writer writer) throws IOException {
		SequenceDb sdb = SequenceDb.getInstance(RegaDBSettings.getInstance().getSequenceDatabaseConfig().getPath());
		
		SequenceDistancesQuery sdq = new SequenceDistancesQuery(sequence, getOutputType(includePatientIsolatesCB.isChecked()), null);
		sdb.query(sequence.getViralIsolate().getGenome(), sdq);
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		
		final double minimumSimilarity = Double.parseDouble(minimumSimilarityTF.getText());
		
		for(Map.Entry<Integer, SequenceDistance> sd : sdq.getSequenceDistances().entrySet()){ 
			double diff = (double)sd.getValue().numberOfDifferences / sd.getValue().numberOfPositions;
			double similarity = 1.0 - diff;
			if(similarity >= minimumSimilarity){
				NtSequence ntSeq = t.getSequence(sd.getKey());
				
				writer.append(">" + 
						new Patient(sequence.getViralIsolate().getPatient(), Privileges.READONLY.getValue()).getPatientId() +
						"_" + sequence.getViralIsolate().getSampleId() +
						"_" + sequence.getLabel().replace(' ', '_') +
						"\n");
				writer.append(ntSeq.getNucleotides() + "\n");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void fill(double minimumSimilarity, boolean includePatientIsolates){
		SequenceDb sdb = SequenceDb.getInstance(RegaDBSettings.getInstance().getSequenceDatabaseConfig().getPath());
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		Query q = t.createQuery("select p.patientIi, p.patientId, v.viralIsolateIi, v.sampleId, n.label "
				+ "from NtSequence n join n.viralIsolate v join v.patient p where n.ntSequenceIi = :sequenceii");
		
		for(int i=0; i<sequences.size(); ++i){
			IsolateTable table = tables.get(i);
			NtSequence sequence = sequences.get(i);
			
			table.clear();
			table.addHeader(tr("form.viralIsolate.similarity.similarity"));
			
			SequenceDistancesQuery sdq = new SequenceDistancesQuery(sequence, getOutputType(includePatientIsolates), null);
			sdb.query(sequence.getViralIsolate().getGenome(), sdq);
			
			for(Map.Entry<Integer, SequenceDistance> sd : sdq.getSequenceDistances().entrySet()){
				double diff = (double)sd.getValue().numberOfDifferences / sd.getValue().numberOfPositions;
				double similarity = 1.0 - diff;
				if(similarity >= minimumSimilarity){
					q.setParameter("sequenceii", sd.getKey());
					List<Object[]> l = q.list();
					
					if(l.size() > 0){
						Object[] o = l.get(0);
						table.addRow((Integer)o[0],
								  	(String)o[1],
								  	(Integer)o[2],
								  	(String)o[3],
								  	(String)o[4],
								  	similarity + "");
					}
				}
			}
			if (table.getRowCount() > 0)
				downloadAnchors.get(i).setHidden(false);
		}
		
		t.commit();
	}
}
