package net.sf.hivgensim.queries.framework.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ViralIsolate;

public class TherapyUtils {
	
	public static long daysExperienceWithDrugClass(List<Therapy> therapies, String drugClass) {
		int days = 0;

		for(Therapy t : therapies) {
			if(TherapyUtils.hasClassExperience(drugClass, t)) {
				days+=DateUtils.millisecondsToDays(t.getStopDate().getTime()-t.getStartDate().getTime());
			}
		}

		return days;
	}
	
	public static Set<DrugGeneric> allDrugGenerics(Therapy t){
		Set<DrugGeneric> dgs = new HashSet<DrugGeneric>();
		for(TherapyGeneric tg : t.getTherapyGenerics()){
			dgs.add(tg.getId().getDrugGeneric());
		}
		for(TherapyCommercial tc : t.getTherapyCommercials()){
			dgs.addAll(tc.getId().getDrugCommercial().getDrugGenerics());
		}
		return dgs;
	}

	public static String getDrugsString(Collection<Therapy> therapies){
		SortedSet<DrugGeneric> drugs = new TreeSet<DrugGeneric>(new Comparator<DrugGeneric>(){
			public int compare(DrugGeneric o1, DrugGeneric o2) {
				return o1.getGenericId().compareTo(o2.getGenericId());
			}			
		});
	
		for(Therapy t : therapies){
			for(TherapyGeneric tg : t.getTherapyGenerics()){
				drugs.add(tg.getId().getDrugGeneric());
			}
			for(TherapyCommercial tc : t.getTherapyCommercials()){
				drugs.addAll(tc.getId().getDrugCommercial().getDrugGenerics());
			}
		}
		if(drugs.size() == 0){
			return "";
		}
	
		String delimiter = " + ";
		StringBuffer result = new StringBuffer();
		for(DrugGeneric dg : drugs){
			result.append(delimiter);
			result.append(dg.getGenericId());
		}		
		return result.toString().substring(delimiter.length());
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

	public static List<Therapy> sortTherapiesByStartDate(Set<Therapy> therapies){
		List<Therapy> sortedTherapies = new ArrayList<Therapy>(therapies);

		Collections.sort(sortedTherapies, new Comparator<Therapy>() {
			public int compare(Therapy t1, Therapy t2) {
				return t1.getStartDate().compareTo(t2.getStartDate());
			}
		});

		return sortedTherapies;
	}

	public static Set<NtSequence> getLatestSequencesDuringTherapy(Patient p, Therapy t){
		Date stop = t.getStopDate();
		Date start = t.getStartDate();
		Date sampleDate;
		ViralIsolate latestVi = null;
		for(ViralIsolate vi : p.getViralIsolates()){
			sampleDate = vi.getSampleDate();
			if(sampleDate != null && start != null && stop!=null 
					&& !sampleDate.before(start) && !sampleDate.after(DateUtils.getWindowEndDateFor(stop))){ // sample is taken during therapy
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
		return result && TherapyUtils.isGoodPreviousTherapy(t,druggenerics);
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

	public static List<DrugGeneric> getGenericDrugs(Therapy t) {
		List<DrugGeneric> drugGenerics = new ArrayList<DrugGeneric>();
	
		for(TherapyGeneric tg : t.getTherapyGenerics()) {
			drugGenerics.add(tg.getId().getDrugGeneric());
		}
	
		for(TherapyCommercial tc : t.getTherapyCommercials()) {
			for(DrugGeneric  dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
				drugGenerics.add(dg);
			}
		}
	
		return drugGenerics;
	}

	public static String getDrugsString(Therapy t) {
		return getDrugsString(Arrays.asList(new Therapy[]{t}));
	}

	public static boolean isGoodExperienceTherapy(Therapy t, String[] druggenerics){
		boolean result = true;
		//check if all wanted drugs are included in the therapy
		for(String drug : druggenerics){
			if(!hasDrugExperience(drug, t)) {
				result = false;											
			}
		}		
		return result && isGoodPreviousTherapy(t,druggenerics);
	}

	public static Collection<DrugClass> getClassesBefore(Set<Therapy> therapies, Date testDate) {
		List<DrugClass> result = new ArrayList<DrugClass>();
		Set<String> names = new TreeSet<String>();
		for(Therapy therapy: therapies){
			if(!therapy.getStartDate().before(testDate)){
				continue;
			}
			for (TherapyGeneric generic : therapy.getTherapyGenerics()) {
				DrugClass drugClass = generic.getId().getDrugGeneric().getDrugClass();
				if(names.contains(drugClass.getClassName())) {
					continue;
				}
				result.add(drugClass);
				names.add(drugClass.getClassName());
			}
		}
		return result;
	}

}
