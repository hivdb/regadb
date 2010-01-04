package be.kuleuven.rega.research;

import java.util.HashMap;
import java.util.Map;

import be.kuleuven.rega.research.conserved.groupers.SubtypeGrouper;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.ViralIsolate;

public class FilterVISubtype {

	final static private SubtypeGrouper grouper = new SubtypeGrouper();
	final static private String noSubtype = "Check the bootscan";

	public static String determineSubtype(ViralIsolate vi) {
		String majority = null;
		int maxVote = 0;
		Map<String, Integer> subtypeVotes = new HashMap<String, Integer>();

		if(vi.getNtSequences().size()==1){
			majority = grouper.getGroup(vi.getNtSequences().iterator().next(), null);
			if(majority==null || majority.equals(noSubtype)){
				majority = null;
			}

		} else {
			for (NtSequence seq : vi.getNtSequences()) {
				String subtype = grouper.getGroup(seq, null);
				if(subtype==null || subtype.equals(noSubtype)){
					continue;
				}
				if(!subtypeVotes.containsKey(subtype))
					subtypeVotes.put(subtype, 0);
				int newVote = subtypeVotes.get(subtype)+1;
				subtypeVotes.put(subtype, newVote);
				if(newVote > maxVote){
					maxVote = newVote;
					majority = subtype;
				}
			}
		}
		return majority;
	}

}