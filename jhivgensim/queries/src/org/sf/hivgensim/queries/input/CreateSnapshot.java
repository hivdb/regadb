package org.sf.hivgensim.queries.input;


import java.io.File;

import org.sf.hivgensim.queries.output.ToSnapshot;


/**
 * This class creates a snapshot of the entire database.
 * args[0]: output file
 * args[1]: login
 * args[2]: password 
 * @author gbehey0
 * 
 */

public class CreateSnapshot {
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		ToSnapshot tss = new ToSnapshot(new File(args[0]));
		tss.generateOutput(new FromDatabase(args[1],args[2]));
		long stop = System.currentTimeMillis();
		System.err.println("done in " + (stop - start) + " ms");
	}

}
