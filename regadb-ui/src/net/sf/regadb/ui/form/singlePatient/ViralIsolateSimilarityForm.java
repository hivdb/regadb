package net.sf.regadb.ui.form.singlePatient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.regadb.contamination.SequenceDistancesQuery;
import net.sf.regadb.contamination.SequenceDistancesQuery.OutputType;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.sequencedb.SequenceUtils.SequenceDistance;
import net.sf.regadb.ui.form.administrator.IsolateTable;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WBreak;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WDoubleValidator;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WValidator;

public class ViralIsolateSimilarityForm extends WContainerWidget {

	private ViralIsolateForm viralIsolateForm;
	
	private List<IsolateTable> tables;
	private List<NtSequence> sequences;
	
	private WLineEdit minimumSimilarityTF;
	
	public ViralIsolateSimilarityForm(ViralIsolateForm viralIsolateForm){
		super();
		this.viralIsolateForm = viralIsolateForm;
	}
	
	public void initialize(){
		
		new WLabel(WString.tr("form.viralIsolate.similarity.minimum"), this);

		minimumSimilarityTF = new WLineEdit(this);
		WDoubleValidator dval = new WDoubleValidator();
		dval.setMandatory(true);
		minimumSimilarityTF.setValidator(dval);
		
		WPushButton submit = new WPushButton(WString.tr("form.viralIsolate.similarity.submit"),this);
		
		new WBreak(this);
		
		Signal.Listener listener = new Signal.Listener() {
            public void trigger() {
            	if(minimumSimilarityTF.validate() == WValidator.State.Valid)
            		fill(Double.parseDouble(minimumSimilarityTF.getText()));
            }
        };
        
        minimumSimilarityTF.enterPressed().addListener(this,listener);
        submit.clicked().addListener(this,listener);

		tables = new ArrayList<IsolateTable>();
		sequences = new ArrayList<NtSequence>();

		for(NtSequence nt : viralIsolateForm.getViralIsolate().getNtSequences()){
			sequences.add(nt);
			
			new WLabel(nt.getLabel(),this);
			tables.add(new IsolateTable(this));
		}

		double minimumSimilarity = RegaDBSettings.getInstance().getSequenceDatabaseConfig().getMinimumSimilarity();
		minimumSimilarityTF.setText(minimumSimilarity+"");
		fill(minimumSimilarity);
	}
	
	@SuppressWarnings("unchecked")
	public void fill(double minimumSimilarity){
		SequenceDb sdb = SequenceDb.getInstance(RegaDBSettings.getInstance().getSequenceDatabaseConfig().getPath());
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		Query q = t.createQuery("select p.patientIi, p.patientId, v.viralIsolateIi, v.sampleId, n.label "
				+ "from NtSequence n join n.viralIsolate v join v.patient p where n.ntSequenceIi = :sequenceii");
		
		for(int i=0; i<sequences.size(); ++i){
			IsolateTable table = tables.get(i);
			NtSequence sequence = sequences.get(i);
			
			table.clear();
			table.addHeader(tr("form.viralIsolate.similarity.similarity"));
			
			SequenceDistancesQuery sdq = new SequenceDistancesQuery(sequence, OutputType.ExtraPatient);
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
		}
		
		t.commit();
	}
}
