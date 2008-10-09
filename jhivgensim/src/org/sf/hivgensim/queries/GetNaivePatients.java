package org.sf.hivgensim.queries;

import java.util.HashSet;
import java.util.Set;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;

public class GetNaivePatients extends QueryImpl<Patient,Patient>{
	
	private String[] drugclasses = new String[]{"Unknown","PI","NRTI","NNRTI","INI","EI"};
	
	public GetNaivePatients(Query<Patient> query){
		super(query);
	}
	
	public GetNaivePatients(Query<Patient> query, String[] therapyTypes){
		super(query);
		this.drugclasses = therapyTypes;		
	}
	
	@Override
	protected void populateOutputList() {
		//we use a set to avoid having duplicate patients
		//caused by the "joins/loops" in the query
		Set<Patient> temp = new HashSet<Patient>();
		for(Patient p : inputQuery.getOutputList()){
			for(Therapy t : p.getTherapies()) {
				boolean naive = true;
				for(String tT : drugclasses){
					if(hasClassExperience(tT, t)) {
						naive = false;					
					}
				}
				if(naive){
					temp.add(p);
				}
			}		
		}
		outputList.addAll(temp);		
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
