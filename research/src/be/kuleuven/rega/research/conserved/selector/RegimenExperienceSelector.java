package be.kuleuven.rega.research.conserved.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.regadb.db.DrugGeneric;
import be.kuleuven.rega.research.conserved.Selector;
import be.kuleuven.rega.research.conserved.Sequence;

public class RegimenExperienceSelector implements Selector {
	private List<String> regimen = new ArrayList<String>();
	
	/**
	 * List of drugs concatenated by + signs.
	 * 
	 * @param regimen
	 */
	public RegimenExperienceSelector(String regimen) {
		Collections.addAll(this.regimen, regimen.split("+"));
	}
	
	public boolean selectSequence(Sequence s) {
		boolean found;
		
		for(String d : regimen) {
			found = false;
			
			for(DrugGeneric dg : s.drugs) {
				if(d.equals(dg.getGenericId())) {
					found = true;
					break;
				}
			}
			
			if(!found) 
				return found;
		}
		
		return true;
	}
}
