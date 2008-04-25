package net.sf.regadb.io.db.stanford;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class CheckNumber {
	private static String dataPath = "/home/dluypa0/stanford_import/B_data";
	private static int ID = 0, STARTDATE = 1, NUMBER = 2, SEQUENCE = 4;
	
	public static void main( String[]args ) throws FileNotFoundException, IOException {
		fixSequences("BelgimumRT.txt");
	}
	
	public static void fixSequences(String fileName) throws IOException, FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader(new File(dataPath + File.separatorChar + fileName)));
		Map<String, String> pts = new HashMap<String, String>();
		
		while ( br.ready() ) {
			String info = br.readLine();
			ArrayList<String> elements = token2array(info, "\t");
			String id = elements.get(ID);
			String nr = elements.get(NUMBER);
			if ( !pts.containsKey(id) ) {
				if ( pts.containsValue(nr) ) {
					System.err.println("Number " + nr + " is already in the list");
				}
				pts.put(id, nr);
			} else {
				System.err.println("Patient " + id + " already has a number: " + pts.get(id) + ", new number: " + nr);
			}
		}
	}
	
	public static ArrayList<String> token2array(String line) {
		return token2array(line, " ");
	}
	public static ArrayList<String> token2array(String line, String separator) {
		ArrayList<String> elements = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(line, separator);
		
		while ( tokenizer.hasMoreTokens() ) {
			String t = tokenizer.nextToken().trim();
			elements.add(t);
		}
		
		return elements;
	}
}
