package net.sf.regadb.service.qc;

import java.util.HashSet;
import java.util.Set;

public class QC {
	private static final int MINIMUM_TRUGENE_FRAGMENT_LENGTH = 20;

	public static Set<String> trugeneQC(String sequence) {
		Set<String> toReturn = new HashSet<String>();
		
		String nNucleotides = 
			"nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";

		int endPro = sequence.toLowerCase().indexOf(nNucleotides);

		if (endPro != -1) {
			int startRt = endPro + nNucleotides.length();

			String pro = trimNs(sequence.substring(0, endPro));
			String rt = trimNs(sequence.substring(startRt));
			
			if (pro.length() < MINIMUM_TRUGENE_FRAGMENT_LENGTH || rt.length() < MINIMUM_TRUGENE_FRAGMENT_LENGTH) {
				toReturn.add(sequence);
				return toReturn;
			}
			
			toReturn.add(pro);
			toReturn.add(rt);
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
	
	public static void main(String [] args) {
		String test = "NNNNNNNNNACTCTTTGGCAGCGACCCCTTGTYACAATAAAAGTAGGGGGCCARATAAAgGagGCTCTATTAGATACAGGAGCAGATGATACAGTATTAGAAGAAATAGAATTRCCAGGAAAATGGAAACCAAAAATGATAGGRGGAATTGGAGGTTTTATCAAAGTAAGACAGTATGAGCAAATTCTTATAGAAATCTGTGGAAAAAAGGCTATAGGTACAGTRTTAGTAGGMCCCACACCTrTCAACATAATTGGAAGAAATATGTTGACTCAGCTTGGATGCACRCTAAATTTTNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNTGTGAwGAAATGGAGAAGGAAGGAAAAATTTCAAAAATTGGRCCTGAAAATCCATATAACACTCCAGTATTTGCCATAAAAAAGAAGGACAGTACTAAGTGGAGAAAATTAGTAGATTTCAGGGAACTTAATAAAAGAACYCAAGACTTTTGTGAAGTTCAATTAGGAATACCACACCCAGCAGGGTTARAAAAGAARAAATCAGYGACAGTGCTRGATGTGGGGGATGCATATTTTTCAATCCCTTTAGATGAAAGCTTCAGGAAATATACTGCATTCACCATACCYAGYMYAAACAATGCAGCACCAGGGATTAGATATCAATATAATGTGCTTCCACAGGGATGGAAAGGATCACCAGCAATATTCCAGTGTAGCATGACAAAAATCTTAGAGCCCTTTAGGACAAAAAACCCAGACATAGTTATCTATCAATACGTGGATGACTTGTATGTAGSATCTGAYYTAGAAATAGGGCAACATAGAGCAAAAATAGAGGAGTTAAGAGAACATTTATTGAAATGGGGACTTACCACACCAGACAAGAAACATCAGAAAGAACCCCCAYTTCTTTGGATGGGGTATGARCTTCATCCTGACAAATGGACAGTACAGCCTATACAGCTACCA";
		System.err.println(test);
		Set<String> processed = trugeneQC(test);
		for (String s: processed) {
			System.err.println(s);
		}
	}
}
