package net.sf.regadb.genbank.splicing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class ParseFasta {
	int seqCounter = 0;
	
	public void run(File f) {
		try {
			String header = null;
			StringBuilder nucleotides = new StringBuilder();
			BufferedReader input = new BufferedReader(new FileReader(f));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					if(line.startsWith(">")) {
						if(header!=null) {
							seqCounter++;
							handleSequence(header, nucleotides.toString());
							nucleotides.delete(0, nucleotides.length());
						}
						header = line.substring(1);
					} else {
						nucleotides.append(line);
					}
				}
				if(header!=null) {
					handleSequence(header, nucleotides.toString());
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.err.println(seqCounter);
	}
	
	
	
	public abstract void handleSequence(String header, String nucleotides) throws IOException;
}
