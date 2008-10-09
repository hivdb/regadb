package org.sf.hivgensim.queries;

import java.util.Date;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ViralIsolate;

public class GetNaiveSequences extends QueryImpl<NtSequence, Patient> {
	
	String[] drugclasses = new String[]{"Unknown","PI","NRTI","NNRTI","INI","EI"};

	protected GetNaiveSequences(Query<Patient> inputQuery) {
		super(inputQuery);
	}

	protected GetNaiveSequences(Query<Patient> inputQuery, String[] drugclasses) {
		super(inputQuery);
		this.drugclasses = drugclasses;
	}

	@Override
	protected void populateOutputList() {
		Date sampleDate;
		for(Patient p : inputQuery.getOutputList()){
			for(ViralIsolate vi : p.getViralIsolates()){
				sampleDate = vi.getSampleDate();
				for(NtSequence seq : vi.getNtSequences()){
					boolean seqIsNaive = true;
					for(Therapy t : p.getTherapies()){
						if(t.getStartDate().before(sampleDate) ||
								//what to do if start == stop == sample ???
								//for now consider them non-naive 
								(t.getStartDate().equals(sampleDate) && t.getStopDate().equals(sampleDate))
							){ 
							for(String dc : drugclasses){
								if(hasClassExperience(dc,t)){
									seqIsNaive = false;
								}
							}
						}
					}
					if(seqIsNaive){
						//how to avoid having seqs from same patient?
						outputList.add(seq);
					}
				}
			}
		}
	}
	
	private boolean hasClassExperience(String drugClass, Therapy t) {
		for(TherapyCommercial tc : t.getTherapyCommercials()) {
			for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
				if(dg.getDrugClass().getClassId().equals(drugClass)) {
					return true;
				}
			}
		}
		for(TherapyGeneric tg : t.getTherapyGenerics()) {
			if(tg.getId().getDrugGeneric().getDrugClass().getClassId().equals(drugClass)) {
				return true;
			}
		}
		return false;
	}

}
