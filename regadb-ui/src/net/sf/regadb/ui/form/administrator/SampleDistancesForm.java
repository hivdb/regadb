package net.sf.regadb.ui.form.administrator;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.regadb.contamination.SequenceDistancesQuery;
import net.sf.regadb.contamination.SequenceDistancesQuery.OutputType;
import net.sf.regadb.contamination.SequenceDistancesQuery.Range;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.sequencedb.SequenceUtils.SequenceDistance;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;

import eu.webtoolkit.jwt.AnchorTarget;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WResource;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.servlet.WebRequest;
import eu.webtoolkit.jwt.servlet.WebResponse;

public class SampleDistancesForm extends FormWidget {

	private WComboBox organismsCombo;
	private WComboBox orfsCombo;
	private RegionWidget regionWidget;
	private WAnchor downloadDistances;

	private List<Genome> genomes;
	private List<OpenReadingFrame> orfs;
	private List<Protein> proteins;

	private Signal.Listener organismChangedListener = new Signal.Listener() {
		public void trigger() {
			orfsCombo.clear();
			for (OpenReadingFrame orf : orfs) {
				if (orf.getGenome().getOrganismName().equals(organismsCombo.getCurrentText().toString())) {
					orfsCombo.addItem(orf.getName());
				}
			}
			orfsCombo.setCurrentIndex(0);
			orfsCombo.show();
			orfChangedListener.trigger();
		}
	};

	private Signal.Listener orfChangedListener = new Signal.Listener() {
		public void trigger() {
			regionWidget.reset();
		}
	};

	class RegionWidget extends WContainerWidget {

		private WComboBox proteinsCombo;
		private WLineEdit start;
		private WLineEdit stop;

		int getStart() {
			return Integer.parseInt(start.getText());
		}

		int getStop() {
			return Integer.parseInt(stop.getText());
		}

		private Signal.Listener proteinsChangedListener = new Signal.Listener() {
			public void trigger() {
				if (proteinsCombo.getCurrentText().toString().equals(WString.tr("form.sample-distances.custom").toString())) {
					start.enable();
					stop.enable();
					for (OpenReadingFrame orf : orfs) {
						if (orf.getGenome().getOrganismName().equals(organismsCombo.getCurrentText().toString()) && orf.getName().equals(orfsCombo.getCurrentText().toString())) {
							start.setText("" + 1);
							stop.setText("" + (orf.getReferenceSequence().length() + 1));
							break;
						}
					}
				} else {
					for (Protein protein : proteins) {
						if (protein.getOpenReadingFrame().getGenome().getOrganismName().equals(organismsCombo.getCurrentText().toString())
								&& protein.getOpenReadingFrame().getName().equals(orfsCombo.getCurrentText().toString()) && protein.getFullName().equals(proteinsCombo.getCurrentText().toString())) {
							start.setText("" + protein.getStartPosition());
							stop.setText("" + protein.getStopPosition());
						}
					}
					start.disable();
					stop.disable();
				}
			}
		};

		public RegionWidget(WContainerWidget parent) {
			super(parent);
			proteinsCombo = new WComboBox(this);
			proteinsCombo.changed().addListener(this, proteinsChangedListener);
			start = new WLineEdit(this);
			stop = new WLineEdit(this);
		}

		public void reset() {
			proteinsCombo.clear();
			proteinsCombo.addItem(WString.tr("form.sample-distances.custom"));
			for (Protein protein : proteins) {
				if (protein.getOpenReadingFrame().getGenome().getOrganismName().equals(organismsCombo.getCurrentText().toString())
						&& protein.getOpenReadingFrame().getName().equals(orfsCombo.getCurrentText().toString())) {
					proteinsCombo.addItem(protein.getFullName());
				}
			}
			proteinsCombo.setCurrentIndex(0);
			proteinsCombo.show();
			proteinsChangedListener.trigger();
		}

	}

	public SampleDistancesForm() {
		super(WString.tr("menu.sample-distances"), InteractionState.Viewing);
		initRegionData();

		WTable layout = new WTable(this);
		// organism
		new WLabel(WString.tr("form.sample-distances.organism"), layout.getElementAt(0, 0));
		organismsCombo = new WComboBox(layout.getElementAt(0, 1));
		for (Genome g : genomes) {
			organismsCombo.addItem(g.getOrganismName());
		}
		organismsCombo.setCurrentIndex(0);
		organismsCombo.changed().addListener(this, organismChangedListener);

		// orf
		new WLabel(WString.tr("form.sample-distances.orf"), layout.getElementAt(1, 0));
		orfsCombo = new WComboBox(layout.getElementAt(1, 1));
		orfsCombo.changed().addListener(this, orfChangedListener);

		// protein
		new WLabel(WString.tr("form.sample-distances.range"), layout.getElementAt(2, 0));
		regionWidget = new RegionWidget(layout.getElementAt(2, 1));
		organismChangedListener.trigger();

		downloadDistances = new WAnchor(this);
		downloadDistances.setText(tr("form.sample-distances.download"));
		downloadDistances.setTarget(AnchorTarget.TargetNewWindow);

		downloadDistances.setResource(new WResource() {
			protected void handleRequest(WebRequest request, WebResponse response) throws IOException {
				writeDistances(response.out());
			}
		});
	}

	private void initRegionData() {
		Login copyLogin = RegaDBMain.getApp().getLogin().copyLogin();
		Transaction t = copyLogin.createTransaction();
		genomes = new ArrayList<Genome>();
		genomes.addAll(t.getGenomes());
		orfs = new ArrayList<OpenReadingFrame>();
		orfs.addAll(t.getOpenReadingFrames());
		proteins = new ArrayList<Protein>();
		proteins.addAll(t.getProteins());
		t.commit();
		copyLogin.closeSession();
	}

	public void writeDistances(Writer writer) throws IOException {
		//FIXME check organism 
		//TODO ZIP - separate files 
		
		Login login = RegaDBMain.getApp().getLogin().copyLogin();
		Transaction t = login.createTransaction();
		Query q = t.createQuery("from NtSequence");
		q.setCacheMode(CacheMode.IGNORE);
		ScrollableResults r = q.scroll();

		Range range = new Range(orfsCombo.getCurrentText().toString(), regionWidget.getStart(), regionWidget.getStop());
		SequenceDb db = SequenceDb.getInstance(RegaDBSettings.getInstance().getSequenceDatabaseConfig().getPath());

		int i = 0;
		int o = 0;
		while (r.next()) {
			NtSequence seq = (NtSequence) r.get(0);
			for (OutputType outputType : OutputType.values()) {
				SequenceDistancesQuery distances = new SequenceDistancesQuery(seq, outputType, range);
				db.query(seq.getViralIsolate().getGenome(), distances);

				for (Map.Entry<Integer, SequenceDistance> e : distances.getSequenceDistances().entrySet()) {
					if (e.getKey().equals(seq.getNtSequenceIi())) {
						continue;
					}

					SequenceDistance f = e.getValue();

					double diff = ((double) f.numberOfDifferences / f.numberOfPositions);
					if (f.numberOfPositions != 0) {
						if (outputType == OutputType.IntraPatient) {
							writer.write("I," + diff + "\n");
							i++;
						} else {
							writer.write("O," + diff + "\n");
							o++;
						}
						writer.flush();
					}
				}
			}

			if (i >= 10000 & o >= 10000) {
				break;
			}
		}
		t.commit();
		login.closeSession();
	}

	@Override
	public void saveData() {

	}

	@Override
	public void cancel() {

	}

	@Override
	public WString deleteObject() {
		return null;
	}

	@Override
	public void redirectAfterDelete() {

	}

}
