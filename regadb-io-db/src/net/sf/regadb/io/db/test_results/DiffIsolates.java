package net.sf.regadb.io.db.test_results;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiffIsolates {
	public static void main(String[] args) throws IOException {
		if (args.length < 4) {
			System.err.println("diff-isolates a.csv b.csv a-out.csv b-out.csv");
			System.exit(0);
		}
		
		File a = new File(args[0]);
		File b = new File(args[1]);
		File a_out = new File(args[2]);
		File b_out = new File(args[3]);
		
		Map<String, List<String>> a_map = new HashMap<String, List<String>>();
		Map<String, List<String>> b_map = new HashMap<String, List<String>>();
	
		readFileInMap(a, a_map);
		readFileInMap(b, b_map);
		
		for (String k : a_map.keySet()) {
			List<String> v = b_map.get(k);
			if (v==null) {
				System.err.println("deleted isolate: " + k);
			}
			else {
				boolean d = diff(a_map.get(k), b_map.get(k));
				if (d) {
					System.err.println("diff isolates: " + k);
					
					List<String> v_a = a_map.get(k);
					List<String> v_b = b_map.get(k);
					
					append(k, v_a, a_out);	
					append(k, v_b, b_out);
				}
			}
		}
		
		for (String k : b_map.keySet()) {
			List<String> v = a_map.get(k);
			if (v==null)
				System.err.println("added isolate: " + k);
		}
	}
	
	private static void append(String key, List<String> l, File f) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
		for (String s : l) 
			out.append(key+s+"\n");
		out.close();
	}
	
	private static boolean diff(List<String> a, List<String> b) {
		if (a.size() != b.size())
			return true;
		
		List<String> a_copy = new ArrayList<String>();
		a_copy.addAll(a);
		List<String> b_copy = new ArrayList<String>();
		b_copy.addAll(b);
		
		Collections.sort(a_copy);
		Collections.sort(b_copy);
		
		for (int i = 0; i < a_copy.size(); i++) {
			if (!a_copy.get(i).equals(b_copy.get(i)))
				return true;
		}
		
		return false;
	}
	
	private static void readFileInMap(File f, Map<String, List<String>> m) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
	    try {
	        String line = br.readLine();

	        while (line != null) {
	            String isolate_id = line.split(",")[0];
	            String rest = line.substring(isolate_id.length());
	            
	            List<String> l = m.get(isolate_id);
	            if (l == null)
	            	l = new ArrayList<String>();
	            l.add(rest);
	            m.put(isolate_id, l);
	        	
	            line = br.readLine();
	        }
	    } finally {
	        br.close();
	    }
	}
}
