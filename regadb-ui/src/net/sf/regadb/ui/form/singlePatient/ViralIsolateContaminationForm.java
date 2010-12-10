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
import net.sf.regadb.ui.form.administrator.ContaminationTable;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WBreak;
import eu.webtoolkit.jwt.WDoubleValidator;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WValidator;

public class ViralIsolateContaminationForm extends TabForm {

	private ViralIsolateForm viralIsolateForm;
	
	private List<ContaminationTable> tables;
	private List<NtSequence> sequences;
	
	private WLineEdit thresholdTF;
	
	public ViralIsolateContaminationForm(ViralIsolateForm viralIsolateForm){
		super(viralIsolateForm);
		this.viralIsolateForm = viralIsolateForm;
	}
	
	public void initialize(){
		
		new WLabel(WString.tr("form.viralIsolate.contamination.threshold"), this);

		thresholdTF = new WLineEdit(this);
		WDoubleValidator dval = new WDoubleValidator();
		dval.setMandatory(true);
		thresholdTF.setValidator(dval);
		
		WPushButton submit = new WPushButton(WString.tr("form.viralIsolate.contamination.submit"),this);
		
		new WBreak(this);
		
		Signal.Listener listener = new Signal.Listener() {
            public void trigger() {
            	if(thresholdTF.validate() == WValidator.State.Valid)
            		fill(Double.parseDouble(thresholdTF.getText()));
            }
        };
        
        thresholdTF.enterPressed().addListener(this,listener);
        submit.clicked().addListener(this,listener);

		tables = new ArrayList<ContaminationTable>();
		sequences = new ArrayList<NtSequence>();

		for(NtSequence nt : viralIsolateForm.getViralIsolate().getNtSequences()){
			sequences.add(nt);
			
			new WLabel(nt.getLabel(),this);
			tables.add(new ContaminationTable(this));
		}

		//TODO default?
		double defaultThreshold = 0.5;
		thresholdTF.setText(defaultThreshold+"");
		fill(defaultThreshold);
	}
	
	@SuppressWarnings("unchecked")
	public void fill(double threshold){
		SequenceDb sdb = SequenceDb.getInstance(RegaDBSettings.getInstance().getSequenceDatabaseConfig().getPath());
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		Query q = t.createQuery("select p.patientIi, p.patientId, v.viralIsolateIi, v.sampleId, n.label "
				+ "from NtSequence n join n.viralIsolate v join v.patient p where n.ntSequenceIi = :sequenceii");
		
		for(int i=0; i<sequences.size(); ++i){
			ContaminationTable table = tables.get(i);
			NtSequence sequence = sequences.get(i);
			
			table.clear();
			table.addHeader();
			
			SequenceDistancesQuery sdq = new SequenceDistancesQuery(sequence, OutputType.ExtraPatient);
			sdb.query(sequence.getViralIsolate().getGenome(), sdq);
			
			for(Map.Entry<Integer, SequenceDistance> sd : sdq.getSequenceDistances().entrySet()){
				double r = (double)sd.getValue().numberOfDifferences / sd.getValue().numberOfPositions;
				if(r >= threshold){
					q.setParameter(0, sd.getKey());
					List<Object[]> l = q.list();
					
					if(l.size() > 0){
						Object[] o = l.get(0);
						table.add((Integer)o[0],
								  (String)o[1],
								  (Integer)o[2],
								  (String)o[3],
								  (String)o[4],
								  r +"");
					}
				}
			}
		}
		
		t.commit();
	}
}
