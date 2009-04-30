package be.kuleuven.rega.research.conserved.selector;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.DrugGeneric;
import be.kuleuven.rega.research.conserved.Selector;
import be.kuleuven.rega.research.conserved.Sequence;

public class ClassExperienceSelector implements Selector {
	private String drugClass;
	
	private List<String> excludedDrugs = new ArrayList<String>();
	
	public ClassExperienceSelector(String drugClass) {
		this.drugClass = drugClass;
	}
	
	public boolean selectSequence(Sequence s) {
		boolean experience = false;
		
		for(DrugGeneric dg : s.drugs) {
			for(String ed : excludedDrugs) {
				if(ed.toLowerCase().equals(dg.getGenericId().toLowerCase()))
					return false;
			}
			if(dg.getDrugClass().getClassId().equals(drugClass)) {
				experience = true;
				break;
			}
			
		}
		
		return experience;
	}
	
	public void excludeDrug(String drugGeneric) {
		excludedDrugs.add(drugGeneric);
	}
}
