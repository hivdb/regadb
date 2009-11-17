package net.sf.regadb.io.db.fiocruz.htlv;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.csv.Table;

public class ImportHtlv {
	public static void main(String [] args) throws FileNotFoundException, UnsupportedEncodingException {
		Table t = Table.readTable("/home/pieter/projects/htlv/htlv.csv");
		Map<String, Integer> map = getColumnMap(t);
		for (String s : map.keySet()) {
			System.err.println(s);
		}
		for (int i = 1; i < t.numRows(); i++) {
			String accession = t.valueAt(map.get("a_number"), i);
			String region = t.valueAt(map.get("genomic_region"), i);
			String status = t.valueAt(map.get("statuss"), i);
			String isolated = t.valueAt(map.get("isolated"), i);
			//print(accession, region, status, isolated);
			
			String gender = t.valueAt(map.get("genre"), i);
			String age = t.valueAt(map.get("age"), i);
			String ethnicity = t.valueAt(map.get("ethnic"), i);
			String country = t.valueAt(map.get("country"), i);
			String clinicalStatus = t.valueAt(map.get("clinical_status"), i);
			//print(gender, age, ethnicity, country, clinicalStatus);
			
			String proviralLoad = t.valueAt(map.get("proviral_load"), i);
			String cd4 = t.valueAt(map.get("cd4_count"), i);
			String cd8 = t.valueAt(map.get("cd8_count"), i);
			
			String contact = t.valueAt(map.get("contact"), i);
			String article = t.valueAt(map.get("article"), i);
			String authors = t.valueAt(map.get("authors"), i);
			String journal = t.valueAt(map.get("journal"), i);
			String sequence = t.valueAt(map.get("sequence"), i);
			String size = t.valueAt(map.get("size"), i);
			//size =! sequence size
			
			if (!sequence.trim().equals("NULL") && !size.trim().equals("NULL")){
			
			if(sequence.trim().length() != Integer.parseInt(size.split(" ")[0]))
				System.err.println(sequence.trim().length() + " -> " +size.split(" ")[0]);
			}
		}		
	}
	
	private static void addViralIsolateTest(String name, String value) {
		
	}
	
	private static void print(String ... strings) {
		for (String s : strings) {
			if (!s.trim().equals("NULL"))
				System.err.print(s.trim() + " - ");
			if (s.trim().equals(""))
				System.err.println("ERROR");
		}
		System.err.print("\n");
	}
	
	private static Map<String, Integer> getColumnMap(Table t) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < t.numColumns(); i++) {
			map.put(t.valueAt(i, 0).toLowerCase(), i);
		}
		return map;
	}
}
