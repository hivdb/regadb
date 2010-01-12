package be.kuleuven.rega.research.conserved.avd;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import be.kuleuven.rega.research.conserved.MutationsPrevalence;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.AaMutation;

public class CountPrevalence {
	private static Map<String, Map<Integer, MutationsPrevalence>> regionsPrevalencePerGroup = new HashMap<String, Map<Integer, MutationsPrevalence>>(); 
	private static Map<String, Integer> sequencesPerGroup = new HashMap<String, Integer>();
	
	private static Map<String, String> idSubtype = new HashMap<String, String>();
	
	public static void main(String [] args) throws FileNotFoundException, UnsupportedEncodingException {
		System.err.println("start");
		Table t_pre = Table.readTable(args[0]);
		Table t_post = Table.readTable(args[1]);
		for(int i = 0; i<t_pre.numRows(); i++) {
			String id = t_pre.valueAt(0, i);
			String subtype = t_pre.valueAt(1, i);
			idSubtype.put(id, subtype);
		}
		
		int counter = 0;
		
		for(int i = 1; i<t_post.numRows(); i++) {
			String id = t_post.valueAt(0, i);
			String subtype = idSubtype.get(id);
			
			boolean ignore = false;
			for(int j = 4; j<t_post.numColumns(); j++) {
				String aas = t_post.valueAt(j, i);
				
				if(aas.equals("") || aas==null) {
					ignore = true;
					break;
				}
			}
			
			if(ignore) {
				counter++;
				continue;
			}
			
			Map<Integer, MutationsPrevalence> prev = regionsPrevalencePerGroup.get(subtype);
			if(prev==null) {
				prev = new HashMap<Integer, MutationsPrevalence>();
				regionsPrevalencePerGroup.put(subtype, prev);
			}
			Integer amount = sequencesPerGroup.get(subtype);
			if(amount==null) {
				amount = 0;
			}
			sequencesPerGroup.put(subtype, ++amount);
			
			for(int j = 4; j<t_post.numColumns(); j++) {
				String aas = t_post.valueAt(j, i);
				
				addMut(prev, j, aas);
			}
		}
		

		for(Map.Entry<String, Integer> e : sequencesPerGroup.entrySet()) {
			System.out.println("\n"+e.getKey() + " ("+e.getValue()+")");
			Map<Integer, MutationsPrevalence> prev = regionsPrevalencePerGroup.get(e.getKey());
			for(int j = 4; j<t_post.numColumns(); j++) {
				MutationsPrevalence mp = prev.get(j);
				System.out.print(j + ": " + mp.mutationsString() + "\n");
			}
		}
		
		System.err.println(counter++);
	}
	
	public static void addMut(Map<Integer, MutationsPrevalence> prev, int pos, String aa) {
		MutationsPrevalence mp = prev.get(pos);
		if(mp==null) {
			mp=new MutationsPrevalence();
			prev.put(pos, mp);
		}
		
		AaMutation aamut = new AaMutation();
		aamut.setAaMutation(aa);
		mp.addMutation(aamut);
	}
}
