package net.sf.regadb.genbank.splicing;

import java.io.File;

public class RevTatTest {
	public static void main(String [] args) {
		File sequences = new File("/home/plibin0/projects/splicing/rev_gb");
		RevTatTest rttest = new RevTatTest();
		rttest.run(sequences, "rev");
	}
	
	public void run(File dir, String proteins) {
		
	}
	

}
