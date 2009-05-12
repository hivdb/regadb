package net.sf.hivgensim.queries;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;

public class GetNaivePatients extends Query<Patient,Patient>{

	private String[] drugClasses = new String[]{"Unknown","PI","NRTI","NNRTI","INI","EI"};

	public GetNaivePatients(IQuery<Patient> nextQuery){
		super(nextQuery);
	}

	public GetNaivePatients(IQuery<Patient> nextQuery, String[] drugClasses){
		super(nextQuery);
		this.drugClasses = drugClasses;		
	}

	public void process(Patient p) {
		//we use a set to avoid having duplicate patients
		//caused by the "joins/loops" in the query
		boolean naive = true;
		for(Therapy t : p.getTherapies()) {
			//always check for unknown therapies
			if(TherapyUtils.hasClassExperience("Unknown", t)){
				naive = false;
			}
			for(String tT : drugClasses){
				if(TherapyUtils.hasClassExperience(tT, t)) {
					naive = false;					
				}
			}	
		}
		if(naive){
			getNextQuery().process(p);
		}
	}	
}
