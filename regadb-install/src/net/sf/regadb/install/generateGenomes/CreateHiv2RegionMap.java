package net.sf.regadb.install.generateGenomes;

import net.sf.regadb.db.Genome;
import net.sf.regadb.genbank.Hiv2;


public class CreateHiv2RegionMap {
	public static void main(String [] args) {
		CreateRegionMap chiv2rm = new CreateRegionMap(Hiv2.benAligned);
        GenerateGenome hiv2benGen = new GenerateGenome("HIV-2-BEN","HIV-2-BEN","",GenerateGenome.getReferenceSequence("NC_001722.fasta"));
        Genome ben = hiv2benGen.generateFromFile("hiv2ben.genome");
		chiv2rm.run(Hiv2.ehoAligned, hiv2benGen, ben);
	}
}
