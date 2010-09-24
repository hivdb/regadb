package net.sf.regadb.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import net.sf.regadb.db.NtSequence;

@SuppressWarnings("serial")
public class FastaFile extends TreeMap<String, NtSequence>{
	private static String possibleNucleotides  = "ACGTMRWSYKVHDBN";
	
	public FastaFile(File input) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(input));
		
		String line;
		String label = null;
		StringBuilder nucl = null;
		
		while((line = br.readLine()) != null){
			if(line.startsWith(">")){
				if(label != null)
					add(label,nucl.toString());
				
				label = line.substring(1).trim();
				nucl = new StringBuilder();
			}
			else{
				if(label != null){
					nucl.append(line.toLowerCase().trim());
				}
			}
		}
		if(label != null)
			add(label,nucl.toString());
		
		br.close();
	}
	
	public void add(String label, String nucleotides){
		NtSequence nt = new NtSequence();
		nt.setLabel(label);
		nt.setNucleotides(clearNucleotides(nucleotides));
		put(label, nt);
	}
	
	public void add(NtSequence nt){
		put(nt.getLabel(), nt);
	}
	
	public void print(PrintStream out){
		for(Map.Entry<String, NtSequence> me : entrySet()){
			out.println('>'+ me.getValue().getLabel());
			out.println(me.getValue().getNucleotides());
		}
	}
	
	public void write(File output) throws FileNotFoundException{
		PrintStream out = new PrintStream(new FileOutputStream(output));
		print(out);
		out.close();
	}
	
	public static String clearNucleotides(String nucleotides){
		StringBuffer toReturn = new StringBuffer();
	    for(char c : nucleotides.toCharArray()) 
	    {
	        if(possibleNucleotides.contains(Character.toUpperCase(c)+"")) {
	            toReturn.append(c);
	        }
	    }
	    return toReturn.toString().toLowerCase();
	}
}
