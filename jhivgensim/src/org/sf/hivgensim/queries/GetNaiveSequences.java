package org.sf.hivgensim.queries;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;

public class GetNaiveSequences extends QueryInfra {
	List<Patient> naivePatients = new ArrayList<Patient>();
	
	public GetNaiveSequences() {
		
	}

	@Override
	protected void performQuery(Patient p) {
		List<Therapy> proteaseTherapies = new ArrayList<Therapy>();
		for(Therapy t : p.getTherapies()) {
			if(!hasClassExperience("PI", t)) {
				naivePatients.add(p);
			}
		}
	}
	
	public static void main(String [] args) {
		GetNaiveSequences gns = new GetNaiveSequences();
		long start = System.currentTimeMillis();
		gns.run(new File("/home/plibin0/Desktop/patients-export.xml"));
		long stop = System.currentTimeMillis();
		System.err.println("done" + (stop - start));
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
