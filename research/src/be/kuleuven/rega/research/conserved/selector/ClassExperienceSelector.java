package be.kuleuven.rega.research.conserved.selector;

import net.sf.regadb.db.DrugGeneric;
import be.kuleuven.rega.research.conserved.Selector;
import be.kuleuven.rega.research.conserved.Sequence;

public class ClassExperienceSelector implements Selector {
	private String drugClass;
	
	public ClassExperienceSelector(String drugClass) {
		this.drugClass = drugClass;
	}
	
	public boolean selectSequence(Sequence s) {
		boolean experience = false;
		
		for(DrugGeneric dg : s.drugs) {
			if(dg.getDrugClass().getClassId().equals(drugClass)) {
				experience = true;
				break;
			}
		}
		
		return experience;
	}
}
