package net.sf.hivgensim.queries;

import java.util.Date;


import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryImpl;
import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.ViralIsolate;

public class GetDrugClassNaiveSequences extends QueryImpl<NtSequence, Patient> {
	
	String[] drugclasses = new String[]{"Unknown","PI","NRTI","NNRTI","INI","EI"};

	public GetDrugClassNaiveSequences(Query<Patient> inputQuery) {
		super(inputQuery);
	}

	public GetDrugClassNaiveSequences(Query<Patient> inputQuery, String[] drugclasses) {
		super(inputQuery);
		this.drugclasses = drugclasses;
	}

	@Override
	public void populateOutputList() {
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
							if(QueryUtils.hasClassExperience("Unknown", t)){
								seqIsNaive = false;
							}
							for(String dc : drugclasses){
								if(QueryUtils.hasClassExperience(dc,t)){
									seqIsNaive = false;
								}
							}
						}
					}
					if(seqIsNaive){
						//how to avoid having seqs from same patient?
						//not necessary for the moment
						outputList.add(seq);
					}
				}
			}
		}
	}	
}
