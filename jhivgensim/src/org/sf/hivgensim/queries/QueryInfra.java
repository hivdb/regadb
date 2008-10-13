package org.sf.hivgensim.queries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ViralIsolate;

public abstract class QueryInfra {

	static boolean hasClassExperience(String drugClass, Therapy t) {
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

	static boolean hasDrugExperience(String drug, Therapy t) {
		for(TherapyCommercial tc : t.getTherapyCommercials()) {
			for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
				if(dg.getGenericId().equals(drug)) {
					return true;
				}
			}
		}
		for(TherapyGeneric tg : t.getTherapyGenerics()) {
			if(tg.getId().getDrugGeneric().getGenericId().equals(drug)) {
				return true;
			}
		}
		return false;
	}

	static List<Therapy> sortTherapies(List<Therapy> therapies){
		Comparator<Therapy> c = new Comparator<Therapy>(){
			public int compare(Therapy o1, Therapy o2) {
				if(o1.getStartDate().before(o2.getStartDate()))
					return -1;
				if(o1.getStartDate().after(o2.getStartDate()))
					return 1;
				return 0;
			}			
		};

		Collections.sort(therapies,c);
		return therapies;
	}

	static List<Therapy> sortTherapies(Set<Therapy> t){
		List<Therapy> result = new ArrayList<Therapy>(t.size());
		result.addAll(t);

		Comparator<Therapy> c = new Comparator<Therapy>(){
			public int compare(Therapy o1, Therapy o2) {
				if(o1.getStartDate().before(o2.getStartDate()))
					return -1;
				if(o1.getStartDate().after(o2.getStartDate()))
					return 1;
				return 0;
			}			
		};
		Collections.sort(result,c);
		return result;
	}

	static Set<NtSequence> getLatestExperiencedSequences(Patient p, Therapy t){
		Date stop = t.getStopDate();
		Date start = t.getStartDate();
		Date sampleDate;
		ViralIsolate latestVi = null;
		for(ViralIsolate vi : p.getViralIsolates()){
			sampleDate = vi.getSampleDate();
			if(sampleDate != null && start != null && stop!=null 
					&& !sampleDate.before(start) && !sampleDate.after(stop)){ // sample is taken during therapy
				if(latestVi == null || vi.getSampleDate().after(latestVi.getSampleDate())){
					latestVi = vi;
				}
			}
		}		
		return latestVi == null ? null : latestVi.getNtSequences(); 
	}

	static boolean isGoodExperienceTherapy(Therapy t, String[] druggenerics){
		boolean result = true;
		//check if all wanted drugs are included in the therapy
		for(String drug : druggenerics){
			if(!QueryInfra.hasDrugExperience(drug, t)) {
				result = false;											
			}
		}		
		return result;
	}

	static boolean isGoodPreviousTherapy(Therapy t, String[] druggenerics){
		boolean ok = true;
		for(TherapyCommercial tc : t.getTherapyCommercials()) {
			for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()) { //for every commercial drug, get all generic drugs
				for(DrugGeneric dg2 : dg.getDrugClass().getDrugGenerics()){ // for every generic drug, get all generic drugs belonging to the same class
					for(String dgcheck : druggenerics){
						if(dg2.getGenericId().equals(dgcheck)){
							ok = false;
							// dg belongs to same class as dgcheck
							// so now check if dg equals to one of the given druggenerics
							for(String dgcheck2 : druggenerics){
								if(dgcheck2.equals(dg.getGenericId())){
									ok = true;
								}
							}							
						}
						if(!ok)
							return false;
					}
				}
			}
		}
		for(TherapyGeneric tg : t.getTherapyGenerics()) {
			for(DrugGeneric dg2 : tg.getId().getDrugGeneric().getDrugClass().getDrugGenerics()){ // for every generic drug, get all generic drugs belonging to the same class
				for(String dgcheck : druggenerics){
					if(dg2.getGenericId().equals(dgcheck)){ 
						// tg belongs to same class as dgcheck
						// so now check if tg equals to one of the given druggenerics
						ok = false;
						for(String dgcheck2 : druggenerics){
							if(dgcheck2.equals(tg.getId().getDrugGeneric().getGenericId())){
								ok = true;								
							}
						}							
					}
					if(!ok)
						return false;
				}
			}
		}
		return ok;
	}

}

