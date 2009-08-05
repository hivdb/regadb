package net.sf.hivgensim.queries.framework.utils;

import net.sf.regadb.db.AaSequence;

public class AaSequenceUtils {
	
	public static boolean coversRegion(AaSequence aaseq, String organism, String protein){
		return aaseq.getProtein().getAbbreviation().equalsIgnoreCase(protein) &&
			aaseq.getProtein().getOpenReadingFrame().getGenome().getOrganismName().equalsIgnoreCase(organism);
	}

}
