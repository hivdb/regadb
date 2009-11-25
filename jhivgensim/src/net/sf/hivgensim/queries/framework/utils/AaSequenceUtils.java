package net.sf.hivgensim.queries.framework.utils;

import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;

public class AaSequenceUtils {
	
	public static boolean coversRegion(AaSequence aaseq, String organism, String protein){
		return aaseq.getProtein().getAbbreviation().equalsIgnoreCase(protein) &&
			aaseq.getProtein().getOpenReadingFrame().getGenome().getOrganismName().equalsIgnoreCase(organism);
	}
	
	public static String toString(AaSequence seq, String reference){
		Map<Short, String> mutations = toCharSequence(seq, reference);

		String result = "";
		for (short pos = seq.getFirstAaPos(); pos <= seq.getLastAaPos(); pos++) {
			if(mutations.containsKey(pos)){
				if(mutations.get(pos).length() > 1){
					result += "X";
				} else {
					result += mutations.get(pos);
				}
			} else {
				result += reference.charAt(pos-1);
			}
		}
		
		return result;
	}
	
	/**
	 * @param seq
	 * 		  the sequence
	 * @param reference
	 * 		  the reference
	 * @return hashmap mapping positions to mutations (if no entry -> reference)
	 */
	public static Map<Short, String> toCharSequence(AaSequence seq, String reference){
		Map<Short, String> result = new HashMap<Short, String>();
		for(AaMutation mut : seq.getAaMutations()){
			short pos = mut.getId().getMutationPosition();
			if(mut.getAaMutation() == null){
				result.put(pos, "-");
			} else {
				if(!mut.getAaMutation().equals(""+reference.charAt(pos-1))){
					result.put(pos, mut.getAaMutation());
				}
			}
		}
		
		return result;
	}

}
