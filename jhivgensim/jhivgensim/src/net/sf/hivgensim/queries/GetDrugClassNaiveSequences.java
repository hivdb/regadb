package net.sf.hivgensim.queries;

import java.util.Date;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.ViralIsolate;

public class GetDrugClassNaiveSequences extends Query<Patient,NtSequence> {

	String[] drugclasses = new String[]{"Unknown","PI","NRTI","NNRTI","INI","EI"};

	public GetDrugClassNaiveSequences(IQuery<NtSequence> nextQuery) {
		super(nextQuery);
	}

	public GetDrugClassNaiveSequences(String[] drugclasses,IQuery<NtSequence> nextQuery) {
		super(nextQuery);
		this.drugclasses = drugclasses;
	}

	@Override
	public void process(Patient p) {
		Date sampleDate;
		for(ViralIsolate vi : p.getViralIsolates()){
			sampleDate = vi.getSampleDate();
			for(NtSequence seq : vi.getNtSequences()){
				boolean seqIsNaive = true;
				for(Therapy t : p.getTherapies()){
					if(t.getStartDate().before(sampleDate) ||
							//what to do if start == stop == sample ???
							//or stop == null?
							//for now consider them non-naive 
							(t.getStartDate().equals(sampleDate) && t.getStopDate() != null && t.getStopDate().equals(sampleDate))								
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
					getNextQuery().process(seq);
				}
			}
		}

	}	
}
