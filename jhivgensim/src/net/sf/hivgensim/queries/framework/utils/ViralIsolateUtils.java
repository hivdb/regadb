package net.sf.hivgensim.queries.framework.utils;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.ViralIsolate;

public class ViralIsolateUtils {
	
	public static String getConcatenatedNucleotideSequence(ViralIsolate vi){
		int numberOfSequences = vi.getNtSequences().size();
		if(numberOfSequences == 0){
			return "";
		}else if(numberOfSequences == 1){
			return vi.getNtSequences().iterator().next().getNucleotides();
		}else if(numberOfSequences == 2){
			String pr = "";
			String rt = "";
			for(NtSequence seq : vi.getNtSequences()){
				if(seq.getAaSequences().size() == 0){
					continue;
				}
				String abbreviation = seq.getAaSequences().iterator().next().getProtein().getAbbreviation();
				if(abbreviation.equals("PR"))
					pr = seq.getNucleotides();
				else if(abbreviation.equals("RT"))
					rt = seq.getNucleotides();				
			}
			return pr + rt;
		}else{
			System.err.println(numberOfSequences);
			return "";
//			throw new Error("Unable to concatenate >2 NtSequences");
		}		
	}

}
