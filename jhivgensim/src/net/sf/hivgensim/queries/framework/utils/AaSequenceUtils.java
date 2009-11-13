package net.sf.hivgensim.queries.framework.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;

public class AaSequenceUtils {
	
	public static boolean coversRegion(AaSequence aaseq, String organism, String protein){
		return aaseq.getProtein().getAbbreviation().equalsIgnoreCase(protein) &&
			aaseq.getProtein().getOpenReadingFrame().getGenome().getOrganismName().equalsIgnoreCase(organism);
	}
	
	public static String toString(AaSequence seq, String reference){
		Map<Integer, String> ambiguities = toCharSequence(seq, reference);
		String result = "";
		for(Entry<Integer, String> position: ambiguities.entrySet()){
			String aas = position.getValue();
			if(aas.length() > 1){
				aas = "X";
			}
			result += aas;
		}
		return result;
	}
	
	public static Map<Integer, String> toCharSequence(AaSequence seq, String reference){
		HashMap<Short,AaMutation> aamuts = new HashMap<Short,AaMutation>();
		for(AaMutation mut : seq.getAaMutations()){
			assert(aamuts.get(mut.getId().getMutationPosition()) == null);
			aamuts.put(mut.getId().getMutationPosition(), mut);
		}
		
		Map<Integer, String> result = new HashMap<Integer, String>();
		for (short pos = seq.getFirstAaPos(); pos <= seq.getLastAaPos(); pos++) {
			AaMutation mut = aamuts.get(pos);
			if(mut == null){
				//reference
				char refAA = reference.charAt(pos-1);
				result.put((int)pos, new String(new char[] {refAA}));
			} else if(mut.getAaMutation() == null){
				result.put((int)pos, "-");
			} else {
				result.put((int)pos, mut.getAaMutation());
			}
		}
		return result;
	}

}
