package net.sf.hivgensim.queries;

import java.util.Date;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.utils.DrugGenericUtils;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.meta.Equals;

public class GetDrugClassNaiveAASequences extends Query<Patient,AaSequence> {

	String[] drugclasses = new String[]{"Unknown","PI","NRTI","NNRTI","INI","EI"};

	public GetDrugClassNaiveAASequences(IQuery<AaSequence> nextQuery) {
		super(nextQuery);
	}

	public GetDrugClassNaiveAASequences(String[] drugclasses,IQuery<AaSequence> nextQuery) {
		super(nextQuery);
		this.drugclasses = drugclasses;
	}

	@Override
	public void process(Patient p) {
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
					for(AaSequence aaseq : seq.getAaSequences()){
						for(String drugClass : drugclasses){
							if(Equals.isSameProtein(aaseq.getProtein(), DrugGenericUtils.getProteinForDrugClass(drugClass))){
								getNextQuery().process(aaseq);
								break;
							}
						}
					}
				}
			}
		}
	}	
}

