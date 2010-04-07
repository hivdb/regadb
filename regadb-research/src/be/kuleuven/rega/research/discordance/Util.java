package be.kuleuven.rega.research.discordance;

import java.util.HashMap;
import java.util.Map;

import net.sf.hivgensim.preprocessing.Utils;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Protein;

public class Util {

	static Map<String, Protein> map;
	
	static Protein getProtein(DrugGeneric drug){
		if(map == null){
			map = new HashMap<String, Protein>();
			map.put("NRTI", Utils.getProtein("HIV-1", "pol", "RT"));
			map.put("NNRTI", Utils.getProtein("HIV-1", "pol", "RT"));
			map.put("PI", Utils.getProtein("HIV-1", "pol", "PR"));
			map.put("INI", Utils.getProtein("HIV-1", "pol", "IN"));
			map.put("EI", Utils.getProtein("HIV-1", "env", "gp41"));
		}
		Protein result = map.get(drug.getDrugClass().getClassId());
		if(result == null){
			throw new IllegalArgumentException("No protein found for given drugclass: "+drug.getDrugClass().getClassId());
		}
		return result;
	}
	
}
