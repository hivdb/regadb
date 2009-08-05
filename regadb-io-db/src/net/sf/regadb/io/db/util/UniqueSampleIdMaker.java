package net.sf.regadb.io.db.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

public class UniqueSampleIdMaker {

	public static void main(String[] args) throws FileNotFoundException {
		if(args.length != 2){
			System.err.println("Usage: UniqueSampleIdMaker infile.xml outfile.xml");
			return;
		}
		Scanner s = new Scanner(new File(args[0]));
		PrintStream ps = new PrintStream(new File(args[1]));
		String line = "";
		String dataset = "";
		while(s.hasNextLine()){
			line = s.nextLine();
			if(line.contains("<Dataset>")){
				ps.println(line);
				line = s.nextLine();
				ps.println(line);
				dataset = line.trim().replaceAll("<(/)?description>", "").toLowerCase(); //retrieve dataset name
				dataset = dataset.contains(" ") ? dataset.substring(0,dataset.indexOf(" ")) : dataset ; //only first part
				continue;
			}
			if(line.contains("<sampleId>")){
				line = line.replace("<sampleId>", "<sampleId>"+dataset);				
			}
			ps.println(line);
		}
		ps.flush();
		ps.close();
	}
}
