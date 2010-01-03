package net.sf.regadb.align.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DiffAlignResults {
	public static void main(String[] args) throws IOException {
		Map<String, String> java = readData(args[0], "sequence=");
		Map<String, String> cpp = readData(args[1], "sequence=");
		Map<String, String> fasta = readData(args[2], ">");
		int i = 0;
		
		for (Map.Entry<String, String> e : java.entrySet()) {
			if (!cpp.get(e.getKey()).equals(e.getValue())) {
				
				//if (java.get(e.getKey()).contains("ERROR"))
					//continue;
				
				
//				System.err.println(e.getKey());
//				System.err.println("cpp");
//				System.err.print(cpp.get(e.getKey()));
//				System.err.println("java");
//				System.err.print(java.get(e.getKey()));
				
				
				System.out.println(">" + e.getKey());
				System.out.print(fasta.get(e.getKey()));
				i++;
			}
		}
		
		//System.err.println(i);
	} 
	
	private static Map<String, String> readData(String fileName, String prefix) throws IOException {
		Map<String, String> seqs = new HashMap<String, String>();
		
		BufferedReader br = 
			new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(fileName))));

		String line;
		String seq = null;
		String data = "";
		while ((line = br.readLine()) != null) {
			if (line.startsWith(prefix)) {
				if (seq != null) {
					seqs.put(seq, data);
					data = "";
				}
				seq = line.substring(prefix.length()); 
			} else {
				data += line + "\n";
			}
		}
		br.close();

		return seqs;
	}
}
