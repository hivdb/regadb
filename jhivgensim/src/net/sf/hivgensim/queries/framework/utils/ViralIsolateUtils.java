package net.sf.hivgensim.queries.framework.utils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.ViralIsolate;

public class ViralIsolateUtils {

	public static String getConcatenatedNucleotideSequence(ViralIsolate vi){
		if(vi.getNtSequences().size() == 0)
			return "";
		if(vi.getNtSequences().size() == 1)
			return vi.getNtSequences().iterator().next().getNucleotides();

		String seq = "";
		for(NtSequence sequence : vi.getNtSequences()){
			seq+="+"+sequence.getNucleotides();
		}
		return seq.substring(1);
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
