package net.sf.regadb.io.export.hicdep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class SimpleCsvMapper {
	
	private String a = null;
	private String b = null;
	
	private Map<String,String> a2b = new TreeMap<String,String>();
	private Map<String,String> b2a = new TreeMap<String,String>();
	
	public SimpleCsvMapper(File csvFile){
		load(csvFile);
	}
	
	public SimpleCsvMapper(BufferedReader br){
		load(br);
	}
	
	protected void load(BufferedReader br){
		try {
			String line = br.readLine();
			if(line == null)
				return;
			
			String[] c = line.split(",");
			a = c[0];
			b = c[1];
			
			while((line = br.readLine()) != null){
				c = line.split(",",-1);
				
				if(!a2b.containsKey(c[0]))
					a2b.put(c[0], c[1]);
				
				if(!b2a.containsKey(c[1]))
					b2a.put(c[1], c[0]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void load(File csvFile){
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(csvFile));
			load(br);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public String a2b(String keyA){
		return keyA == null ? null : a2b.get(keyA);
	}
	
	public String b2a(String keyB){
		return keyB == null ? null : b2a.get(keyB);
	}
	
	public String getA(){
		return a;
	}
	
	public String getB(){
		return b;
	}
}
