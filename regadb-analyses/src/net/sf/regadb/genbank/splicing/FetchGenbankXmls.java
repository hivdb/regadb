package net.sf.regadb.genbank.splicing;

import java.io.File;
import java.io.IOException;

import net.sf.regadb.genbank.ParseGenBankXml;

public class FetchGenbankXmls extends ParseFasta {
	File dir = new File("/home/plibin0/projects/splicing/rev_gb");
	
	public void handleSequence(String header, String nucleotides) throws IOException {
		ParseGenBankXml.efetchGenbankXmlFile(header, "nucleotide", new File(dir.getAbsolutePath()+File.separatorChar+header+".xml"));
//		for(GBORF orf : organism.orfs) {
//			if(!orf.name.toLowerCase().startsWith("null") && orf.name.toLowerCase().contains(protein))
//				counter.add(header);
//		}
		System.err.println(seqCounter);	
	}
	
	public static void main(String [] args) {
		FetchGenbankXmls fgbxmls = new FetchGenbankXmls();
		fgbxmls.dir.mkdir();
		fgbxmls.run(new File("/home/plibin0/projects/splicing/losalamos_rev.fasta"));
	}

}
