package net.sf.regadb.align.local;

import org.biojava.bio.BioException;
import org.biojava.bio.alignment.SubstitutionMatrix;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceTools;

public class TestNeedlemanWunsh {

	static enum type { DNA, PROTEIN , ASPROT};
	
	public static void main(String[] args) throws BioException {
		type t = type.ASPROT;
		String sref = "cctcaggtcactctttggcaacgacccctcgtcacaataaagataggggggcaactaaaggaagctctattagatacaggagcagatgatacagtattagaagaaatgagtttgccaggaagatggaaaccaaaaatgatagggggaattggaggttttatcaaagtaagacagtatgatcagatactcatagaaatctgtggacataaagctataggtacagtattagtaggacctacacctgtcaacataattggaagaaatctgttgactcagattggttgcactttaaattttcccattagccctattgagactgtaccagtaaaattaaagccaggaatggatggcccaaaagttaaacaatggccattgacagaagaaaaaataaaagcattagtagaaatttgtacagagatggaaaaggaagggaaaatttcaaaaattgggcctgaaaatccatacaatactccagtatttgccataaagaaaaaagacagtactaaatggagaaaattagtagatttcagagaacttaataagagaactcaagacttctgggaagttcaattaggaataccacatcccgcagggttaaaaaagaaaaaatcagtaacagtactggatgtgggtgatgcatatttttcagttcccttagatgaagacttcaggaagtatactgcatttaccatacctagtataaacaatgagacaccagggattagatatcagtacaatgtgcttccacagggatggaaaggatcaccagcaatattccaaagtagcatgacaaaaatcttagagccttttagaaaacaaaatccagacatagttatctatcaatacatggatgatttgtatgtaggatctgacttagaaatagggcagcatagaacaaaaatagaggagctgagacaacatctgttgaggtggggacttaccacaccagacaaaaaacatcagaaagaacctccattcctttggatgggttatgaactccatcctgataaatggacagtacagcctatagtgctgccagaaaaagacagctggactgtcaatgacatacagaagttagtggggaaattgaattgggcaagtcagatttacccagggattaaagtaaggcaattatgtaaactccttagaggaaccaaagcactaacagaagtaataccactaacagaagaagcagagctagaactggcagaaaacagagagattctaaaagaaccagtacatggagtgtattatgacccatcaaaagacttaatagcagaaatacagaagcaggggcaaggccaatggacatatcaaatttatcaagagccatttaaaaatctgaaaacaggaaaatatgcaagaatgaggggtgcccacactaatgatgtaaaacaattaacagaggcagtgcaaaaaataaccacagaaagcatagtaatatggggaaagactcctaaatttaaactgcccatacaaaaggaaacatgggaaacatggtggacagagtattggcaagccacctggattcctgagtgggagtttgttaatacccctcccttagtgaaattatggtaccagttagagaaagaacccatagtaggagcagaaaccttc";
		String star = "actctttggcaacgacccctygtcacartaaaaatagrrgggcaastaaaggaagctctattagatacaggagcagatgatacagtatthgaagaaatgratttgccaggaaratggamaccaaaawtgatagggggaattggaggttttrtcaragtaagacagtatgatcaratamscrtagaaatctgtggrcataaagctawakgtwcagtrttaataggacctacacctwscaacataattggaagaaayctgwtgactcagattggttgcactttaaatttttgtacagaawtggaaaakgamggraaaatttcaaaaattgggcctgaaaatccatayaatactccartatttgccataaagaaaaarracagtgataaatggagaaaattagtagayttyagagaacttaataagagaactcaagacttctgkgaagtkcaattaggaataccacatcccgcagggttaaaraagaamaaatcagyaacagtactrgatrtrggtgatgcatatttttcarttcccttagatmmaracttcaggaagtayactgcatttaccatacctagtataaacaatgagayrccagggattagrtatcagtacaatgtgcttccacagggatggaaaggatcaccagcaatattycagagyagcatgacmaraatcttagagcctttyagaaaacmraayccagahatagttatctaycaatacgtggatsayttgtatgtaggatctsacttagaaatwgggcatcaggarbcaatwarakkraatctragacaayatctgtkgargtggggrttttwcacmccagacmaaaaacatcarmargaacctccattctaytggatgggttatgaactycatcctgataaatggacagtacagcctatagkgctgcca".replace("-","");
		Sequence ref,tar;
		NeedlemanWunsch nmw;
		SubstitutionMatrix sm;
		if(t == type.DNA){
			ref = DNATools.createDNASequence(sref, "ref");
			tar = DNATools.createDNASequence(star, "tar");
			sm = new SubstitutionMatrix(DNATools.getDNA(), CodonAlign.nuc4_4, "NUC4.4");
		} else if (t == type.PROTEIN){			
			ref = ProteinTools.createProteinSequence(sref, "ref");
			tar = ProteinTools.createProteinSequence(star, "tar");
			sm = new SubstitutionMatrix(ProteinTools.getTAlphabet(), CodonAlign.blosum30,"BLOSUM30");
		} else {
			ref = SequenceTools.createSequence(RNATools.translate(DNATools.toRNA(DNATools.createDNASequence(sref, "ref"))),"refAA", "refAA", null);
			tar = SequenceTools.createSequence(RNATools.translate(DNATools.toRNA(DNATools.createDNASequence(star, "tar"))),"tarAA", "tarAA", null);
			sm = new SubstitutionMatrix(ProteinTools.getTAlphabet(), CodonAlign.blosum30,"BLOSUM30");
		}
		nmw = new NeedlemanWunsch(-10, -3.3, sm);
		ScoredAlignment sa = nmw.pairwiseAlignment(ref, tar);
		System.out.println(sa.getAlignment().symbolListForLabel("refAA").seqString());
		System.out.println(sa.getAlignment().symbolListForLabel("tarAA").seqString());
	}

}
