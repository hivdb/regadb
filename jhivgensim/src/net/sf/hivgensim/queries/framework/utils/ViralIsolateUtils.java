package net.sf.hivgensim.queries.framework.utils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.TestResult;
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
	
	public static String extractSubtype(ViralIsolate vi) {
		Set<String> subtypes = new HashSet<String>();

		for(NtSequence ntseq : vi.getNtSequences()) {
			for(TestResult tr : ntseq.getTestResults()) {
				if(tr.getTest().getDescription().equals("Rega Subtype Tool")) {
					subtypes.add(tr.getValue());
				}
			}
		}

		StringBuilder b = new StringBuilder();
		for(String s : subtypes) {
			b.append(s);
			b.append("+");
		}

		if(b.length()==0)
			return "";
		else
			return b.substring(0, b.length()-1);
	}
	
	public static ViralIsolate closestToDate(Set<ViralIsolate> viralIsolates, Date d) {
		long min = Long.MAX_VALUE;
		ViralIsolate closest = null;

		if(viralIsolates.size()==0)
			return null;

		long diff;
		for(ViralIsolate vi : viralIsolates) {
			if(vi.getSampleDate()!=null) {
				diff = Math.abs(vi.getSampleDate().getTime()-d.getTime());
				if(diff<min) {
					min = diff;
					closest = vi;
				}
			}
		}

		return closest;
	}

}
