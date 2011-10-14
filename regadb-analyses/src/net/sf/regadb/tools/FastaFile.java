package net.sf.regadb.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.regadb.db.NtSequence;

public class FastaFile{
	private static String possibleNucleotides  = "ACGTUMRWSYKVHDBN";
	
	private File file = null;
	
	private Map<String, NtSequence> map = new TreeMap<String, NtSequence>();
	private List<NtSequence> list = new ArrayList<NtSequence>();
	
	public FastaFile(File input) throws IOException{
		this(input, false);
	}
	
	public FastaFile(File input, boolean clearNucleotides) throws IOException{
		this.file = input;
		
		BufferedReader br = null;
		
		try{
			br = new BufferedReader(new FileReader(input));
			
			String line;
			String label = null;
			StringBuilder nucl = null;
			
			while((line = br.readLine()) != null){
				if(line.startsWith(">")){
					if(label != null)
						add(label, nucl.toString(), clearNucleotides);
					
					label = line.substring(1).trim();
					nucl = new StringBuilder();
				}
				else{
					if(label != null){
						nucl.append(line.toLowerCase().trim());
					}
				}
			}
			if(label != null){
				add(label, nucl.toString(), clearNucleotides);
			}
		}finally{
			if(br != null)
				br.close();
		}
	}
	
	public File getFile(){
		return file;
	}
	
	public void add(String label, String nucleotides, boolean clearNucleotides){
		NtSequence nt = new NtSequence();
		nt.setLabel(label);
		nt.setNucleotides(
				clearNucleotides ? clearNucleotides(nucleotides) : nucleotides);
		add(nt);
	}
	
	public void add(NtSequence nt){
		map.put(nt.getLabel(), nt);
		list.add(nt);
	}
	
	public void print(PrintStream out){
		for(NtSequence nt : list){
			out.println('>'+ nt.getLabel());
			out.println(nt.getNucleotides());
		}
	}
	
	public void write(File output) throws FileNotFoundException{
		PrintStream out = new PrintStream(new FileOutputStream(output));
		print(out);
		out.close();
	}
	
	public NtSequence get(int index){
		return list.get(index);
	}
	
	public NtSequence get(String label){
		return map.get(label);
	}
	
	public int size(){
		return list.size();
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
