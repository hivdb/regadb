package net.sf.regadb.io.db.ghb.lis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.Arguments.ArgumentException;

public class TestResultExamples {
	public static void testMatch(String pat, String str){
		System.out.println(pat +" "+ str +" -> "+ match(pat,str));
	}
	public static boolean match(String pat, String str){
		if(pat.equals("*"))
			return str.length() > 0;

		int spos;
		int ppos = pat.indexOf('*');
		if(ppos == -1){
			return pat.equals(str);
		}
		else{
			String pat1;
			if(ppos == 0){
				ppos = pat.indexOf('*', ppos+1);
				if(ppos == -1)
					return str.endsWith(pat.substring(1));
				pat1 = pat.substring(1, ppos);
				
				spos = str.indexOf(pat1);
				if(spos == -1)
					return false;
				spos += pat1.length();
				return match(pat.substring(ppos),str.substring(spos));
			}
			else{
				if(!str.startsWith(pat.substring(0,ppos)))
					return false;
						
				pat1 = pat.substring(ppos);
				return match(pat1,str.substring(ppos));
			}
		}
	}
	
	public static void main(String args[]) throws ArgumentException, IOException{
		
		Arguments as = new Arguments();
		PositionalArgument tests  = as.addPositionalArgument("tests-file", true);
		PositionalArgument lisdir = as.addPositionalArgument("lis-dir", true);
		
		as.parse(args);
		
		if(!as.isValid()){
			as.printUsage(System.err);
			return;
		}
		
		ArrayList<String> headers = new ArrayList<String>();
		ArrayList<Integer> hmap;
		BufferedReader br = new BufferedReader(new FileReader(tests.getValue()));
		
		String line = br.readLine();
		for(String field : split(line)){
			headers.add(parse(field));
		}
		
		String prevHeaders = "";
		
		while((line = br.readLine()) != null){
			int total = 0;
			boolean printed = false;
			
			if(line.trim().length() == 0)
				continue;
			
			String fields[] = split(line);
			
			for(int i=0; i<fields.length; ++i)
				fields[i] = parse(fields[i]);
			
			for(File f : new File(lisdir.getValue()).listFiles()){
				if(!f.getName().startsWith("rega"))
					continue;
				
				BufferedReader br2 = new BufferedReader(new FileReader(f));
				
				String headers2 = br2.readLine();
				hmap = new ArrayList<Integer>();

				String fields2[] = split(headers2);
				for(String header : headers){
					for(int i=0; i<fields2.length; ++i){
						if(fields2[i].equals(header)){
							hmap.add(i);
							break;
						}
					}
				}
				
				String line2, match = null;
				boolean found = false;
				while((line2 = br2.readLine()) != null){
					if(line2.trim().length() == 0)
						continue;
					
					fields2 = split(line2);
					
					found = true;
					for(int i = 0; i<hmap.size(); ++i){
						if(hmap.get(i) >= fields2.length)
							continue;
						
						String field2 = fields2[hmap.get(i)];
						if((i >= fields.length && (field2 != null && field2.trim().length() != 0))
								|| (i < fields.length && !fields[i].equals(field2))){
							found = false;
							break;
						}
					}
					
					if(found){
						++total;
						if(!printed)
							match = line2;
					}
				}
				
				br2.close();
				
				if(!printed && match != null){
					printed=true;
					if(!headers2.equals(prevHeaders)){
						System.out.println(headers2);
						prevHeaders = headers2;
					}
					System.out.print(match);
				}
			}
			if(printed)
				System.out.println("\t"+total);
			else
				System.out.println("not found");
		}
	}

	public static String[] split(String line){
		return line.split("\t");
	}
	
	public static String parse(String field){
		return field.replace("\"", "").trim();
	}
}
