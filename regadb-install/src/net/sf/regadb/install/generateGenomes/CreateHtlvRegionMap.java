package net.sf.regadb.install.generateGenomes;

import net.sf.regadb.db.Genome;
import net.sf.regadb.install.generateGenomes.refSeqs.HTLV;


public class CreateHtlvRegionMap {
	public static void main(String [] args) {
		CreateRegionMap htlvrm = new CreateRegionMap(HTLV.AB513134);
        GenerateGenome htlvafgg = new GenerateGenome("HTLV AF033817","HTLV AF033817","",GenerateGenome.getReferenceSequence("AF033817.fasta"));
        Genome af = htlvafgg.generateFromFile("htlv-af.genome");
        htlvrm.run(HTLV.ATK1, htlvafgg, af);
	}
}
