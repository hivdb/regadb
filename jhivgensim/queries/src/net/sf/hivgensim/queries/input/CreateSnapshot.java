package net.sf.hivgensim.queries.input;

import java.io.File;

import net.sf.hivgensim.queries.output.ToSnapshot;

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
		if(args.length != 3){
			System.err.println("Usage: CreateSnapshot snapshot.output login password");
			System.exit(0);
		}
		long start = System.currentTimeMillis();
		ToSnapshot tss = new ToSnapshot(new File(args[0]));
		tss.generateOutput(new FromDatabase(args[1],args[2]));
		long stop = System.currentTimeMillis();
		System.err.println("done in " + (stop - start) + " ms");
	}
}
