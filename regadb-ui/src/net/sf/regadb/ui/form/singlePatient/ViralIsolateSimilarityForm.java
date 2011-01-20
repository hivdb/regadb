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
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.sequencedb.SequenceUtils.SequenceDistance;
import net.sf.regadb.ui.form.administrator.IsolateTable;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.warning.WarningMessage;
import net.sf.regadb.ui.framework.widgets.warning.WarningMessage.MessageType;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.AnchorTarget;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WBreak;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WDoubleValidator;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WInteractWidget;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WResource;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WTimer;
import eu.webtoolkit.jwt.WValidator;
import eu.webtoolkit.jwt.servlet.WebRequest;
import eu.webtoolkit.jwt.servlet.WebResponse;

public class ViralIsolateSimilarityForm extends TabForm {

	private ViralIsolateForm viralIsolateForm;

	private Map<String, NtSequence> sequences;

	private WLineEdit minimumSimilarityTF;
	private WComboBox sequenceCombo;
	private WAnchor downloadSequences;
	private WComboBox visualCombo;
	private WInteractWidget visualization;
	
	private boolean loadingDone;
	private WarningMessage warningMessage = new WarningMessage(new WImage("pics/formWarning.gif"), tr("form.viralIsolate.similarity.visualization.loading"), MessageType.INFO);
	private WTimer timer = new WTimer(warningMessage);
	
	private List<NtSequence> similarSequences = new ArrayList<NtSequence>();
	private List<NtSequence> patientSequences = new ArrayList<NtSequence>();
 	private List<Double> similarities = new ArrayList<Double>();

	private enum Visualization {
		TABLE(WString.tr("form.viralIsolate.similarity.table")), TREE(WString.tr("form.viralIsolate.similarity.tree"));

		private WString name;

		private Visualization(WString name) {
			this.name = name;
		}

		public String toString() {
			return name.toString();
		}
	};

	public ViralIsolateSimilarityForm(ViralIsolateForm viralIsolateForm) {
		super();
		this.viralIsolateForm = viralIsolateForm;
	}

	@Override
	public void initialize() {
		WTable layout = new WTable(this);

		Signal.Listener listener = new Signal.Listener() {
			public void trigger() {
				if (validate()) {
					showSequence();
				}
			}
		};

		new WLabel(WString.tr("form.viralIsolate.similarity.sequenceLabel"), layout.getElementAt(0, 0));
		sequenceCombo = new WComboBox(layout.getElementAt(0, 1));

		new WLabel(WString.tr("form.viralIsolate.similarity.minimum"), layout.getElementAt(1, 0));
		minimumSimilarityTF = new WLineEdit(layout.getElementAt(1, 1));
		WDoubleValidator dval = new WDoubleValidator();
		dval.setMandatory(true);
		minimumSimilarityTF.setValidator(dval);
		minimumSimilarityTF.enterPressed().addListener(this, listener);
		double minimumSimilarity = RegaDBSettings.getInstance().getSequenceDatabaseConfig().getMinimumSimilarity();
		minimumSimilarityTF.setText(minimumSimilarity + "");

		new WLabel(WString.tr("form.viralIsolate.similarity.visualizationLabel"), layout.getElementAt(2, 0));
		visualCombo = new WComboBox(layout.getElementAt(2, 1));
		for (Visualization v : Visualization.values()) {
			visualCombo.addItem(v.toString());
		}
		visualCombo.setCurrentIndex(0);
		visualCombo.changed().addListener(this, listener);

		WPushButton submit = new WPushButton(WString.tr("form.viralIsolate.similarity.submit"), layout.getElementAt(2, 2));
		submit.clicked().addListener(this, listener);

		new WBreak(this);

		addWidget(warningMessage);
		
		sequences = new TreeMap<String, NtSequence>();
		for (final NtSequence nt : viralIsolateForm.getViralIsolate().getNtSequences()) {
			sequences.put(nt.getLabel(), nt);
			sequenceCombo.addItem(nt.getLabel());
		}

		sequenceCombo.setCurrentIndex(0);
		sequenceCombo.changed().addListener(this, listener);

		showSequence();
	}

	private void fillSimilarSequences(NtSequence sequence) {
		SequenceDb sdb = SequenceDb.getInstance(RegaDBSettings.getInstance().getSequenceDatabaseConfig().getPath());

		SequenceDistancesQuery sdq = new SequenceDistancesQuery(sequence, OutputType.ExtraPatient, null);
		sdb.query(sequence.getViralIsolate().getGenome(), sdq);

		Transaction t = RegaDBMain.getApp().createTransaction();

		final double minimumSimilarity = Double.parseDouble(minimumSimilarityTF.getText());

		similarSequences.clear();
		similarities.clear();

		for (Map.Entry<Integer, SequenceDistance> sd : sdq.getSequenceDistances().entrySet()) {
			double diff = (double) sd.getValue().numberOfDifferences / sd.getValue().numberOfPositions;
			double similarity = 1.0 - diff;
			if (similarity >= minimumSimilarity) {
				similarSequences.add(t.getSequence(sd.getKey()));
				if (similarities != null)
					similarities.add(similarity);
			}
		}

		t.commit();
		
		patientSequences.clear();
		Patient p = new Patient(sequence.getViralIsolate().getPatient(), Privileges.READONLY.getValue());
		for(ViralIsolate vi : p.getViralIsolates()) {
			for(NtSequence seq : vi.getNtSequences()) {
				patientSequences.add(seq);				
			}
		}
	}

	private void writeFasta(NtSequence ntSeq, Writer writer) throws IOException {
		for (NtSequence sequence : patientSequences) {
			writeFastaSequence(sequence, writer);
		}
		for (NtSequence sequence : similarSequences) {
			writeFastaSequence(sequence, writer);
		}		
	}
	
	private void writeFastaSequence(NtSequence sequence, Writer writer) throws IOException {
		writer.append(">" + new Patient(sequence.getViralIsolate().getPatient(), Privileges.READONLY.getValue()).getPatientId() + "_" + sequence.getViralIsolate().getSampleId() + "_"
				+ sequence.getLabel().replace(' ', '_') + "\n");
		writer.append(sequence.getNucleotides() + "\n");
	}
	
	private boolean validate() {
		return minimumSimilarityTF.validate() == WValidator.State.Valid;
	}

	private void showSequence() {
		showSequence(sequenceCombo.getCurrentText().getValue(), Double.parseDouble(minimumSimilarityTF.getText()));
	}

	private void showSequence(String sequenceLabel, double minimumSimilarity) {
		// remove and reset widget
		if (visualization != null && downloadSequences != null) {
			removeWidget(visualization);
			visualization = null;
			removeWidget(downloadSequences);
			downloadSequences = null;
		}
		
		loadingDone = false;
		warningMessage.show();
		timer.setInterval(500);
		timer.timeout().addListener(this, new Signal.Listener() {
			public void trigger() {
				checkProgress();
			}
		});
		timer.start();
		refresh();
		
		fillSimilarSequences(sequences.get(sequenceLabel));

		if (similarSequences.size() != 0) {
			// create new widget
			if (visualCombo.getCurrentIndex() == Visualization.TABLE.ordinal()) {
				visualization = createTable(sequenceLabel, minimumSimilarity);
			} else if (visualCombo.getCurrentIndex() == Visualization.TREE.ordinal()) {
				visualization = createTree(sequenceLabel, minimumSimilarity);
			} 

			// create download fasta widget
			showDownloadSequence(sequenceLabel);
		}
		
		loadingDone = true;
	}
	
	private void checkProgress() {
		if(loadingDone) {
			timer.stop();
			warningMessage.hide();
			if(visualization != null && downloadSequences != null) {
				visualization.show();
				downloadSequences.show();
			}
			refresh();
		}
	}

	private void showDownloadSequence(String sequenceLabel) {
		downloadSequences = new WAnchor(this);
		downloadSequences.hide();
		
		downloadSequences.setText(tr("form.viralIsolate.similarity.downloadSequences"));
		downloadSequences.setTarget(AnchorTarget.TargetNewWindow);

		final NtSequence nt = sequences.get(sequenceLabel);
		downloadSequences.setResource(new WResource() {
			protected void handleRequest(WebRequest request, WebResponse response) throws IOException {
				writeFasta(nt, response.out());
			}
		});
	}

	public IsolateTable createTable(String sequenceLabel, double minimumSimilarity) {
		IsolateTable table = new IsolateTable(this);
		table.hide();
		table.addHeader(WString.tr("form.viralIsolate.similarity.similarity"));
		
		for (int i = 0; i < similarSequences.size(); i++) {
			NtSequence sequence = similarSequences.get(i);
			Patient p = new Patient(sequence.getViralIsolate().getPatient(), Privileges.READONLY.getValue());
			table.addRow(p.getPatientIi(), p.getPatientId(), sequence.getViralIsolate().getViralIsolateIi(), sequence.getViralIsolate().getSampleId(), sequence.getLabel(), similarities.get(i) + "");
		}

		return table;
	}

	public ContaminationTree createTree(String sequenceLabel, double minimumSimilarity) {
		String organism = sequences.get(sequenceLabel).getAaSequences().iterator().next().getProtein().getOpenReadingFrame().getGenome().getOrganismName();
		Map<String, Map<String, String>> sequencesAttributes = new TreeMap<String, Map<String, String>>();
		for (NtSequence sequence : similarSequences) {
			sequencesAttributes.put(""+sequence.getNtSequenceIi(), createAttributeMap(sequence));
		}
		for (NtSequence sequence : patientSequences) {
			sequencesAttributes.put(""+sequence.getNtSequenceIi(), createAttributeMap(sequence));
		}
		ContaminationTree tree = new ContaminationTree(this, organism, sequencesAttributes, sequences.get(sequenceLabel));
		tree.hide();
		return tree;
	}

	private Map<String, String> createAttributeMap(NtSequence sequence) {
		Map<String, String> attributes = new TreeMap<String, String>();
		Patient p = new Patient(sequence.getViralIsolate().getPatient(), Privileges.READONLY.getValue());
		attributes.put("dataset", p.getDatasets().iterator().next().getDescription());
		attributes.put("patient", p.getPatientId());
		attributes.put("sample", sequence.getViralIsolate().getSampleId());
		attributes.put("sequence", sequence.getLabel());
		attributes.put("sequence_ii", ""+sequence.getNtSequenceIi());
		attributes.put("nucleotides", ""+sequence.getNucleotides());
		return attributes;
	}
}
