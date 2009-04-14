package be.kuleuven.rega.research.conserved;

import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.AaMutation;

public class MutationsPrevalence {
	private Map<String, Integer> prevalences = new HashMap<String, Integer>();
	
	public void addMutation(AaMutation aamut) {
		if(aamut.getAaMutation() == null) {
			return;
		}
		
		Integer count = prevalences.get(aamut.getAaMutation());
		if(count == null) {
			count = 0;
		}
		prevalences.put(aamut.getAaMutation(), ++count);
	}
	
	public String mutationsString() {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, Integer> e : prevalences.entrySet()) {
			sb.append(e.getKey()+"("+e.getValue()+") ");
		}
		return sb.toString();
	}

	public int totalMutations() {
		int total = 0;
		
		for(Map.Entry<String, Integer> e : prevalences.entrySet()) {
			total += e.getValue();
		}
		
		return total;
	}
}
