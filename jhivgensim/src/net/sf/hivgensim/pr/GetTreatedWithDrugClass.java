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

public class GetTreatedWithDrugClass extends Query<Patient,SequenceExperience>{
	
	private String drugclass;
	
	public GetTreatedWithDrugClass(String drugclass, IQuery<SequenceExperience> nextQuery){
		super(nextQuery);
		this.drugclass = drugclass;
	}
	
	public void process(Patient p) {
		Set<DrugGeneric> history = new HashSet<DrugGeneric>();
		Set<DrugGeneric> regimen = new HashSet<DrugGeneric>();
		
		for(Therapy t : TherapyUtils.sortTherapies(p.getTherapies())){
			regimen.clear();
			for(TherapyGeneric tg : t.getTherapyGenerics()){
				if(tg.getId().getDrugGeneric().getDrugClass().getClassId().equals(drugclass)){
					regimen.add(tg.getId().getDrugGeneric());
				}
			}
			for(TherapyCommercial tc : t.getTherapyCommercials()){
				for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()){
					if(dg.getDrugClass().getClassId().equals(drugclass)){
						regimen.add(dg);
					}
				}
			}
			history.addAll(regimen);
			if(!regimen.isEmpty()){
				for(NtSequence seq : TherapyUtils.getAllSequencesDuringTherapy(p, t)){
					getNextQuery().process(new SequenceExperience(seq,history));					
				}				
			}
		}
	}

}
