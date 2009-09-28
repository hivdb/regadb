package net.sf.hivgensim.queries.framework.utils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.io.db.drugs.ImportDrugsFromCentralRepos;
import net.sf.regadb.util.settings.RegaDBSettings;

public class DrugGenericUtils {

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
}
