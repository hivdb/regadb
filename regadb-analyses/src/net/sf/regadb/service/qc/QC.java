package net.sf.regadb.service.qc;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.sf.regadb.db.NtSequence;

public class QC {
	private static final int MINIMUM_TRUGENE_FRAGMENT_LENGTH = 20;

	public static Set<NtSequence> trugeneQC(NtSequence sequence) {
		Set<NtSequence> toReturn = new HashSet<NtSequence>();
		
		String nNucleotides = 
			"nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";

		int endPro = sequence.getNucleotides().toLowerCase().indexOf(nNucleotides);

		if (endPro != -1) {
			int startRt = endPro + nNucleotides.length();

			String pro = trimNs(sequence.getNucleotides().substring(0, endPro));
			String rt = trimNs(sequence.getNucleotides().substring(startRt));
			
			if (pro.length() < MINIMUM_TRUGENE_FRAGMENT_LENGTH || rt.length() < MINIMUM_TRUGENE_FRAGMENT_LENGTH) {
				toReturn.add(sequence);
				return toReturn;
			}
			
			toReturn.add(copy(sequence, " (a)", pro));
			toReturn.add(copy(sequence, " (b)", rt));
		} else 
			toReturn.add(sequence);
		
		return toReturn;
	}
	
	private static String trimNs(String seq) {
		int firstIndex = 0;
		int lastIndex = seq.length();
		
		for (int i = 0; i < seq.length(); i++) 
			if (Character.toLowerCase(seq.charAt(i)) != 'n') {
				firstIndex = i;
				break;
			}

		for (int i = seq.length() - 1; i >= 0; i--)
			if (Character.toLowerCase(seq.charAt(i)) != 'n') {
				lastIndex = i + 1;
				break;
			}
		
		return seq.substring(firstIndex, lastIndex);
	}
	
	private static NtSequence copy(NtSequence seq, String labelSuffix, String sequence) {
		NtSequence copy = new NtSequence();
		copy.setAligned(false);
		copy.setSequenceDate(seq.getSequenceDate());
		copy.setLabel(seq.getLabel() + labelSuffix);
		copy.setNucleotides(sequence);
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
