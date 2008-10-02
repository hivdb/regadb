package org.sf.hivgensim.queries;

import java.util.HashSet;
import java.util.Set;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;

public class GetNaiveSequences extends QueryImpl<Patient,Patient>{
	
	
	protected GetNaiveSequences(Query<Patient> query){
		this.inputQuery = query;
	}
	
	@Override
	protected void populateOutputList() {
		//we use a set to avoid having duplicate patients
		//caused by the "joins/loops" in the query
		Set<Patient> temp = new HashSet<Patient>();
		for(Patient p : inputQuery.getOutputList()){
			for(Therapy t : p.getTherapies()) {
				if(!hasClassExperience("PI", t)) {
					temp.add(p);					
				}
			}		
		}
		outputList.addAll(temp);		
	}
	
	public boolean hasClassExperience(String drugClass, Therapy t) {
		for(TherapyCommercial tc : t.getTherapyCommercials()) {
			for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
				if(dg.getDrugClass().getClassName().equals(drugClass)) {
					return true;
				}
			}
		}
		for(TherapyGeneric tg : t.getTherapyGenerics()) {
			if(tg.getId().getDrugGeneric().getDrugClass().getClassName().equals(drugClass)) {
				return true;
			}
		}
		return false;
	}	
}
