package net.sf.hivgensim.pr;

import java.util.HashSet;
import java.util.Set;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;

public class GetTreatedWithDrugClass extends Query<Patient,NtSequence>{
	
	private String drugclass;
	
	public GetTreatedWithDrugClass(String drugclass, IQuery<NtSequence> nextQuery){
		super(nextQuery);
		this.drugclass = drugclass;
	}
	
	public void process(Patient p) {
		Set<DrugGeneric> history = new HashSet<DrugGeneric>();
		
		for(Therapy t : TherapyUtils.sortTherapies(p.getTherapies())){
			history.clear();
			for(TherapyGeneric tg : t.getTherapyGenerics()){
				if(tg.getId().getDrugGeneric().getDrugClass().getClassId().equals(drugclass)){
					history.add(tg.getId().getDrugGeneric());
				}
			}
			for(TherapyCommercial tc : t.getTherapyCommercials()){
				for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()){
					if(dg.getDrugClass().getClassId().equals(drugclass)){
						history.add(dg);
					}
				}
			}
			
			if(!history.isEmpty()){
				for(NtSequence seq : TherapyUtils.getAllSequencesDuringTherapy(p, t)){
//					getNextQuery().process(new SequenceExperience(seq,history));
					getNextQuery().process(seq);
				}				
			}
		}
	}

}
