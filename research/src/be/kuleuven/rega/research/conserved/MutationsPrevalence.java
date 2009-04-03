package be.kuleuven.rega.research.conserved;

import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.AaMutation;

public class MutationsPrevalence {
	private Map<Character, Integer> prevalences = new HashMap<Character, Integer>();
	
	public void addMutation(AaMutation aamut) {
		if(aamut.getAaMutation() == null) {
			return;
		}
		
		for(int i = 0; i<aamut.getAaMutation().length(); i++) {
			char c = aamut.getAaMutation().charAt(i);
			Integer count = prevalences.get(c);
			if(count == null) {
				count = 1;
			} else {
				count++;
			}
			prevalences.put(c, count);
		}
	}
	
	public String mutationsString() {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<Character, Integer> e : prevalences.entrySet()) {
			sb.append(e.getKey()+"("+e.getValue()+") ");
		}
		return sb.toString();
	}
	
	public int totalMutations() {
		int total = 0;
		
		for(Map.Entry<Character, Integer> e : prevalences.entrySet()) {
			total += e.getValue();
		}
		
		return total;
	}
}
