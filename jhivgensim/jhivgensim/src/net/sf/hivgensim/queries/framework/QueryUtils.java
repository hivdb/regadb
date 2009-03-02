package net.sf.hivgensim.queries.framework;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ViralIsolate;

public abstract class QueryUtils {
	
	public static Set<NtSequence> getLatestNtSequence(Set<NtSequence> sequences){
		ViralIsolate latest = null;
		for(NtSequence seq : sequences){
			if(latest == null || seq.getViralIsolate().getSampleDate().after(latest.getSampleDate())){
				latest = seq.getViralIsolate();
			}
		}
		if(latest != null){
			return latest.getNtSequences();
		}
		return null;
	}
	

	public static boolean hasClassExperience(String drugClass, Therapy t) {
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

	public static boolean hasDrugExperience(String drug, Therapy t) {
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

	public static List<Therapy> sortTherapies(List<Therapy> therapies){
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

	public static List<Therapy> sortTherapies(Set<Therapy> t){
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

	public static Set<NtSequence> getLatestSequencesDuringTherapy(Patient p, Therapy t){
		Date stop = t.getStopDate();
		Date start = t.getStartDate();
		Date sampleDate;
		ViralIsolate latestVi = null;
		for(ViralIsolate vi : p.getViralIsolates()){
			sampleDate = vi.getSampleDate();
			if(sampleDate != null && start != null && stop!=null 
					&& !sampleDate.before(start) && !sampleDate.after(getWindowEndDateFor(stop))){ // sample is taken during therapy
				if(latestVi == null || vi.getSampleDate().after(latestVi.getSampleDate())){
					latestVi = vi;
				}
			}
		}		
		return latestVi == null ? null : latestVi.getNtSequences(); 
	}
	
	
	public static Set<NtSequence> getAllSequencesDuringTherapy(Patient p, Therapy t){
		Set<NtSequence> result = new HashSet<NtSequence>();
		Date stop = t.getStopDate();
		Date start = t.getStartDate();
		Date sampleDate;
		for(ViralIsolate vi : p.getViralIsolates()){
			sampleDate = vi.getSampleDate();
			if(sampleDate != null && start != null && stop!=null 
					&& sampleDate.after(start) && !sampleDate.after(stop) // sample is taken during therapy
//					&& (sampleDate.after(start) || start.equals(stop)) // check if sample is not taken in begin of therapy 
			){// maybe later add a certain interval but what to do with start=stop
				result.addAll(vi.getNtSequences());
			}
		}		
		return result; 
	}
	
	public static boolean isGoodExperienceTherapy(Therapy t, String[] druggenerics, Set<String> history){
		boolean result = true;
		//check if all wanted drugs are included in the therapy or in the history
		for(String drug : druggenerics){
			if(!history.contains(drug)) {
				result=false;															
			}
		}		
		return result && isGoodPreviousTherapy(t,druggenerics);
	}
	
	public static boolean isGoodExperienceTherapy(Therapy t, String[] druggenerics){
		boolean result = true;
		//check if all wanted drugs are included in the therapy
		for(String drug : druggenerics){
			if(!QueryUtils.hasDrugExperience(drug, t)) {
				result = false;											
			}
		}		
		return result && isGoodPreviousTherapy(t,druggenerics);
	}

	public static boolean isGoodPreviousTherapy(Therapy t, String[] druggenerics){
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
			//not a good therapy if we don't know the therapy:
			if(tg.getId().getDrugGeneric().getGenericId().equals("Unknown")){
				return false;
			}
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
	
	public static Date getWindowEndDateFor(Date therapyStop){
		Calendar c = Calendar.getInstance();
		c.setTime(therapyStop);
		c.add(Calendar.MONTH, 1); // <= edit the window time here
		return c.getTime();
	}

}

