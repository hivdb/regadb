package net.sf.regadb.install.generateGenomes;

import net.sf.regadb.db.Genome;
import net.sf.regadb.install.generateGenomes.refSeqs.HTLV;


public class CreateHtlvRegionMap {
	public static void main(String [] args) {
		CreateRegionMap htlvrm = new CreateRegionMap(HTLV.AB513134);
        GenerateGenome htlvabgg = new GenerateGenome("HTLV AB513134","HTLV AB513134","",GenerateGenome.getReferenceSequence("AB513134.fasta"));
        Genome ab = htlvabgg.generateFromFile("hiv2ben.genome");
        htlvrm.run(HTLV.ATK1, htlvabgg, ab);
	}
}
