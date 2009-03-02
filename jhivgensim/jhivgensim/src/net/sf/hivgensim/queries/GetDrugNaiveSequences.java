package net.sf.hivgensim.queries;

import java.util.Date;

import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryImpl;
import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.ViralIsolate;

public class GetDrugNaiveSequences extends QueryImpl<NtSequence, Patient> {

	private String[] drugs = new String[]{};
	
	protected GetDrugNaiveSequences(Query<Patient> inputQuery) {
		super(inputQuery);
	}
	
	public GetDrugNaiveSequences(Query<Patient> inputQuery, String[] drugs) {
		super(inputQuery);
		this.drugs = drugs;
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
								(t.getStartDate().equals(sampleDate) && t.getStopDate() != null && t.getStopDate().equals(sampleDate))
							){
							if(QueryUtils.hasDrugExperience("Unknown", t)){
								seqIsNaive = false;
							}
							for(String drug : drugs){
								if(QueryUtils.hasDrugExperience(drug,t)){
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
