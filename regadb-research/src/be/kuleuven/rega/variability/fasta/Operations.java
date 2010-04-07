package be.kuleuven.rega.variability.fasta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Operations {
	public static interface AnnotationParser {
		public String parse(String annotation);
	}
	
	public static void main(String [] args) throws IOException {
		String operation = args[0].trim();
		
		Map<String, String> db1 = readFasta(new File(args[1]), null);
		Map<String, String> db2 = readFasta(new File(args[2]), null);
		
		Map<String, String> result = null;
		
		if (operation.equals("min"))
			 result = min(db1, db2);
		
		writeFasta(new File(args[3]), result);
	}
	
	public static Map<String, String> min(Map<String, String> db1, Map<String, String> db2) throws IOException {
		Map<String, String> result = new HashMap<String, String>();
		
		for (String name : db1.keySet()) {
			if (!db2.containsKey(name)) {
				result.put(name, db1.get(name));
			}
		}

		return result;
	}
	
	public static void writeFasta(File fasta, Map<String, String> database) throws IOException {
		String nucleotides = "ACGTMRWSYKVHDBN";
		
		FileWriter fw = new FileWriter(fasta);
		for (Map.Entry<String, String> e : database.entrySet()) {
			fw.write(">" + e.getKey() + "\n");
			fw.write(e.getValue() + "\n");
			
			for (char c : e.getValue().toCharArray()) {
				if (!nucleotides.contains(Character.toUpperCase(c) + "")) 
					System.err.println(e.getKey() + " -> seq contains non nucl chars");
			}
			
			for (char c : e.getKey().toCharArray()) {
				if (!Character.isLetterOrDigit(c))
					System.err.println("incorrect seq name? -> " + e.getKey());
			}
		}
	}
	
	public static Map<String, String> readFasta(File fasta, AnnotationParser ap) throws IOException {
		Map<String, String> sequences = new HashMap<String, String>();
		BufferedReader in = new BufferedReader(new FileReader(fasta));
		
		String name = null;
		String sequence = "";
		
		String line;
		while ((line = in.readLine()) != null) {
			if (line.startsWith(">")) {
				if (name != null) {
					sequences.put(name, sequence);
					name = null;
					sequence = "";
				}
				
				name = line.substring(1);
				if (ap != null) 
					name = ap.parse(name);
			} else {
				sequence += line;
			}
		}
		in.close();
		
		return sequences;
	}
}
