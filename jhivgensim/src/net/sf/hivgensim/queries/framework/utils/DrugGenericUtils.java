package net.sf.hivgensim.queries.framework.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.hivgensim.preprocessing.Utils;
import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Protein;
import net.sf.regadb.io.db.drugs.ImportDrugsFromCentralRepos;
import net.sf.regadb.util.settings.RegaDBSettings;

public class DrugGenericUtils {

	public static List<DrugGeneric> getDrugsSortedOnResistanceRanking(List<DrugGeneric> genericDrugs, boolean includeAPV) {
		List<DrugGeneric> resistanceGenericDrugs = new ArrayList<DrugGeneric>();

		for (DrugGeneric dg : genericDrugs) {
			if (dg.getResistanceTableOrder() != null
					&& dg.getDrugClass().getResistanceTableOrder() != null) {
				resistanceGenericDrugs.add(dg);
			}
		}

		Collections.sort(resistanceGenericDrugs, new Comparator<DrugGeneric>() {
			public int compare(DrugGeneric dg1, DrugGeneric dg2) {
				if (dg1.getDrugClass().getClassId().equals(
						dg2.getDrugClass().getClassId())) {
					return dg1.getResistanceTableOrder().compareTo(
							dg2.getResistanceTableOrder());
				} else {
					return dg1.getDrugClass().getResistanceTableOrder()
					.compareTo(
							dg2.getDrugClass()
							.getResistanceTableOrder());
				}
			}
		});

		if(includeAPV) {
			for (DrugGeneric dg : genericDrugs) {
				if(dg.getGenericId().startsWith("APV"))
					resistanceGenericDrugs.add(dg);
			}
		}

		return resistanceGenericDrugs;
	}
	
	//returns true if one class has been found
	public static boolean containsDrugsFromDrugClasses(Set<DrugGeneric> drugs, String[] drugclasses){
		for(DrugGeneric dg : drugs){
			for(int i = 0 ; i < drugclasses.length ; i++){
				if(dg.getDrugClass().getClassId().equalsIgnoreCase(drugclasses[i])){
					return true;
				}
			}
		}
		return false;
	}

	public static boolean containsClass(Collection<DrugClass> before, String drug){
		for (DrugClass drugClass : before) {
			if(contains(drugClass.getDrugGenerics(), drug)){
				return true;
			}
		}
		return false;
	}

	public static boolean contains(Set<DrugGeneric> drugs, String drug){
		for(DrugGeneric dg : drugs){
			if(dg.getGenericId().equalsIgnoreCase(drug)){
				return true;
			}
		}
		return false;
	}

	public static List<DrugGeneric> prepareRegaDrugGenerics() {
		RegaDBSettings.getInstance().getProxyConfig().initProxySettings();
		ImportDrugsFromCentralRepos imDrug = new ImportDrugsFromCentralRepos();
		return imDrug.getGenericDrugs();
	}

	public static String toString(Set<DrugGeneric> drugs){
		String result = "";
		for(DrugGeneric dg : drugs){
			result += " + "+dg.getGenericId();
		}
		return result.length() == 0 ? "" : result.substring(3);
	}

	public static boolean containsOnlyFromDrugClass(Set<DrugGeneric> history, String[] drugClass, String[] drugs) {
		boolean[] found = new boolean[drugs.length];

		for(DrugGeneric dg : history){
			for(String dc : drugClass){
				if(dg.getDrugClass().getClassId().equalsIgnoreCase(dc)){
					boolean ok = false;
					for(int i = 0; i < drugs.length; i++){
						if(dg.getGenericId().equalsIgnoreCase(drugs[i])){
							found[i] = true;
							ok = true;
						}
					}
					if(!ok){
						return false;
					}
				}
			}
		}
		for(boolean b : found){
			if(!b){
				return false;
			}
		}
		return true;
	}

	
	public static Set<String> getPI(){
		Set<String> drugs = new TreeSet<String>();
		drugs.add("APV");
		drugs.add("IDV");
		drugs.add("NFV");
		drugs.add("RTV");
		drugs.add("TPV");
		drugs.add("ATV");
		drugs.add("FPV");
		drugs.add("ATV/r");
		drugs.add("TPV/r");
		drugs.add("PI");
		drugs.add("LPV/r");
		drugs.add("SQV/r");
		drugs.add("IDV/r");
		drugs.add("APV/r");
		drugs.add("DRV");
		drugs.add("DRV/r");
		drugs.add("FPV/r");
		drugs.add("SQV");
		return drugs;
	}

	static Map<String, Protein> drugProteinMap;

	public static Protein getProtein(DrugGeneric drug){
		return getProteinForDrugClass(drug.getDrugClass());
	}
	
	public static String[] getDrugClassForProtein(Protein protein){
		return getDrugClassForProtein(protein.getAbbreviation());
	}

	public static String[] getDrugClassForProtein(String abbreviation) {
		if (abbreviation.equals("PRO") || abbreviation.equals("PR")) {
			return new String[] {"PI"};
		} else if (abbreviation.equals("RT")) {
			return new String[] {"NRTI", "NNRTI"};
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public static Protein getProteinForDrugClass(DrugClass drugClass){
		return getProteinForDrugClass(drugClass.getClassName());
	}

	public static Protein getProteinForDrugClass(String className) {
		if(drugProteinMap == null){
			drugProteinMap = new HashMap<String, Protein>();
			drugProteinMap.put("NRTI", Utils.getProtein("HIV-1", "pol", "RT"));
			drugProteinMap.put("NNRTI", Utils.getProtein("HIV-1", "pol", "RT"));
			drugProteinMap.put("PI", Utils.getProtein("HIV-1", "pol", "PR"));
			drugProteinMap.put("INI", Utils.getProtein("HIV-1", "pol", "IN"));
			drugProteinMap.put("EI", Utils.getProtein("HIV-1", "env", "gp41"));
		}
		Protein result = drugProteinMap.get(className);
		if(result == null){
			throw new IllegalArgumentException("No protein found for given drugclass: "+className);
		}
		return result;
	}
	

}
