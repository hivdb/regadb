package net.sf.regadb.service.qc;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.sf.regadb.db.NtSequence;

public class QC {
	public static Set<NtSequence> trugeneQC(NtSequence sequence) {
		Set<NtSequence> toReturn = new HashSet<NtSequence>();
		
		sequence.setNucleotides(sequence.getNucleotides().toLowerCase());
		
		String nNucleotides = 
			"nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
		
		if (sequence.getNucleotides().contains(nNucleotides)) {
			String pro = sequence.getNucleotides().substring(0, sequence.getNucleotides().indexOf(nNucleotides));
			String rt = sequence.getNucleotides().substring(sequence.getNucleotides().indexOf(nNucleotides) + nNucleotides.length());
			NtSequence proSeq = copy(sequence, " (a)");
			proSeq.setNucleotides(trimNs(pro));
			toReturn.add(proSeq);
			NtSequence rtSeq = copy(sequence, " (b)");
			rtSeq.setNucleotides(trimNs(rt));
			toReturn.add(rtSeq);
		} else 
			toReturn.add(sequence);
		
		return toReturn;
	}
	
	private static String trimNs(String seq) {
		int firstIndex = 0;
		int lastIndex = seq.length() - 2;
		
		for (int i = 0; i < seq.length(); i++) 
			if (seq.charAt(i) != 'n') {
				firstIndex = i;
				break;
			}

		for (int i = seq.length() - 1; i >= 0; i--)
			if (seq.charAt(i) != 'n') {
				lastIndex = i;
				break;
			}
		
		return seq.substring(firstIndex, lastIndex + 1);
	}
	
	private static NtSequence copy(NtSequence seq, String labelSuffix) {
		NtSequence copy = new NtSequence();
		copy.setAligned(false);
		copy.setSequenceDate(seq.getSequenceDate());
		copy.setLabel(seq.getLabel() + labelSuffix);
		return copy;
	}
	
	public static void main(String [] args) {
		NtSequence test = new NtSequence();
		test.setSequenceDate(new Date());
		test.setLabel("Sequence 1");
		test.setNucleotides("NNNNNNNNNACTCTTTGGCAGCGACCCCTTGTYACAATAAAAGTAGGGGGCCARATAAAgGagGCTCTATTAGATACAGGAGCAGATGATACAGTATTAGAAGAAATAGAATTRCCAGGAAAATGGAAACCAAAAATGATAGGRGGAATTGGAGGTTTTATCAAAGTAAGACAGTATGAGCAAATTCTTATAGAAATCTGTGGAAAAAAGGCTATAGGTACAGTRTTAGTAGGMCCCACACCTrTCAACATAATTGGAAGAAATATGTTGACTCAGCTTGGATGCACRCTAAATTTTNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNTGTGAwGAAATGGAGAAGGAAGGAAAAATTTCAAAAATTGGRCCTGAAAATCCATATAACACTCCAGTATTTGCCATAAAAAAGAAGGACAGTACTAAGTGGAGAAAATTAGTAGATTTCAGGGAACTTAATAAAAGAACYCAAGACTTTTGTGAAGTTCAATTAGGAATACCACACCCAGCAGGGTTARAAAAGAARAAATCAGYGACAGTGCTRGATGTGGGGGATGCATATTTTTCAATCCCTTTAGATGAAAGCTTCAGGAAATATACTGCATTCACCATACCYAGYMYAAACAATGCAGCACCAGGGATTAGATATCAATATAATGTGCTTCCACAGGGATGGAAAGGATCACCAGCAATATTCCAGTGTAGCATGACAAAAATCTTAGAGCCCTTTAGGACAAAAAACCCAGACATAGTTATCTATCAATACGTGGATGACTTGTATGTAGSATCTGAYYTAGAAATAGGGCAACATAGAGCAAAAATAGAGGAGTTAAGAGAACATTTATTGAAATGGGGACTTACCACACCAGACAAGAAACATCAGAAAGAACCCCCAYTTCTTTGGATGGGGTATGARCTTCATCCTGACAAATGGACAGTACAGCCTATACAGCTACCA");
		print (test);
		Set<NtSequence> processed = trugeneQC(test);
		for (NtSequence ntseq : processed) {
			print (ntseq);
		}
	}
	
	private static void print(NtSequence ntseq) {
		System.err.println(ntseq.getLabel());
		System.err.println(ntseq.getSequenceDate().toString());
		System.err.println(ntseq.getNucleotides());
		System.err.println("----");
	}
}
