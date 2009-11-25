package net.sf.hivgensim.queries;

import java.util.ArrayList;
import java.util.Date;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.utils.NtSequenceUtils;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
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
		ArrayList<NtSequence> pSeqs = new ArrayList<NtSequence>();
		Date sampleDate;
		for(ViralIsolate vi : p.getViralIsolates()){
			sampleDate = vi.getSampleDate();
			if(sampleDate == null){
				continue;
			}
			for(NtSequence seq : vi.getNtSequences()){
				boolean seqIsNaive = true;
				for(Therapy t : p.getTherapies()){
					if(t.getStartDate().before(sampleDate) ||
							//what to do if start == stop == sample ???
							//or stop == null?
							//for now consider them non-naive 
							(t.getStartDate().equals(sampleDate) && t.getStopDate() != null && t.getStopDate().equals(sampleDate))								
					){
						if(TherapyUtils.hasClassExperience("Unknown", t)){
							seqIsNaive = false;
						}
						for(String dc : drugclasses){
							if(TherapyUtils.hasClassExperience(dc,t)){
								seqIsNaive = false;
							}
						}
					}
				}
				if(seqIsNaive){
					pSeqs.add(seq);
				}
			}
		}
		if(pSeqs.isEmpty()){
			return;
		}
		Date max = null;
		for(NtSequence seq : pSeqs){
			if((max == null || seq.getViralIsolate().getSampleDate().after(max)) &&
					NtSequenceUtils.coversRegion(seq, "HIV-1", "PR") && seq.getAaSequences().iterator().next().getFirstAaPos() <= 10 && seq.getAaSequences().iterator().next().getLastAaPos() >= 95){
				max = seq.getViralIsolate().getSampleDate();
			}
		}
		if(max == null)
			return;
		NtSequence result = null;
		String rt = "";
		for(NtSequence seq : pSeqs){
			if(!seq.getViralIsolate().getSampleDate().equals(max)){
				continue;
			}
			if(NtSequenceUtils.coversRegion(seq, "HIV-1", "PR") && seq.getAaSequences().iterator().next().getFirstAaPos() <= 10 && seq.getAaSequences().iterator().next().getLastAaPos() >= 95){
				result = seq;
			}
			else if(NtSequenceUtils.coversRegion(seq, "HIV-1", "RT")){
				rt = seq.getNucleotides();
			}	
		}
		if(result != null){
			result.setNucleotides(result.getNucleotides()+rt);
			getNextQuery().process(result);
		}
	}
}
