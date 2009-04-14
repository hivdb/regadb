package net.sf.hivgensim.queries.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.drugs.ImportDrugsFromCentralRepos;
import net.sf.regadb.util.settings.RegaDBSettings;

public abstract class QueryUtils {

	public static boolean isSequenceInRegion(NtSequence seq, String organism, String protein){
		for(AaSequence aaseq : seq.getAaSequences()){
			if(isSequenceInRegion(aaseq, organism, protein)){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isSequenceInRegion(AaSequence aaseq, String organism, String protein){
		return aaseq.getProtein().getAbbreviation().equalsIgnoreCase(protein) &&
					aaseq.getProtein().getOpenReadingFrame().getGenome().getOrganismName().equals(organism);
	}

	public static String therapyRegimenInBetween(Patient p, NtSequence firstSequence, NtSequence secondSequence){
		Date firstDate = firstSequence.getViralIsolate().getSampleDate();
		Date secondDate = secondSequence.getViralIsolate().getSampleDate();
		ArrayList<Therapy> inBetween = new ArrayList<Therapy>();
		for(Therapy t : sortTherapies(p.getTherapies())){
			if(t.getStartDate() != null && t.getStartDate().before(secondDate)
					&& t.getStopDate() != null && t.getStopDate().after(firstDate)){
				inBetween.add(t);
			}
		}
		return getDrugsString(inBetween);
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

	public static String getDrugsString(Therapy t) {
		return getDrugsString(Arrays.asList(new Therapy[]{t}));
	}

	public static Set<NtSequence> getLatestNtSequences(Collection<NtSequence> sequences){
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
	
	public static boolean betweenInterval(Date d, Date start, Date end) {
		return d.after(start) && d.before(end);
	}
	
	public static boolean betweenOrEqualsInterval(Date d, Date start, Date end) {
		return (d.after(start) || d.equals(start)) && (d.before(end) || d.equals(end));
	}
	
	public static Date getWindowEndDateFor(Date therapyStop){
		Calendar c = Calendar.getInstance();
		c.setTime(therapyStop);
		c.add(Calendar.MONTH, 1); // <= edit the window time here
		return c.getTime();
	}
	
	public static List<DrugGeneric> prepareRegaDrugGenerics() {
		RegaDBSettings.getInstance().getProxyConfig().initProxySettings();
		ImportDrugsFromCentralRepos imDrug = new ImportDrugsFromCentralRepos();
		return imDrug.getGenericDrugs();
	}
}

