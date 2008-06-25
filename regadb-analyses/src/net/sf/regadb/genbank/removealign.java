package net.sf.regadb.genbank;

import java.io.File;
import java.io.IOException;

import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.analysis.functions.FastaRead;

import org.apache.commons.io.FileUtils;

public class removealign {
	public static void main(String [] args) {
		FastaRead read1 = FastaHelper.readFastaFile(new File("/home/plibin0/projects/genbank/hiv2/M15390_compendium.fasta"), false);
		FastaRead read2 = FastaHelper.readFastaFile(new File("/home/plibin0/projects/genbank/hiv2/M33262_compendium.fasta"), false);
	
		String fasta1 = ">" + read1.fastaHeader_ + "\n" + read1.xna_.replace("-", "");
		String fasta2 = ">" + read2.fastaHeader_ + "\n" + read2.xna_.replace("-", "");
		try {
			FileUtils.writeStringToFile(new File("/home/plibin0/projects/genbank/hiv2/M15390_compendium_noalign.fasta"), fasta1, null);
			FileUtils.writeStringToFile(new File("/home/plibin0/projects/genbank/hiv2/M33262_compendium_noalign.fasta"), fasta2, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
