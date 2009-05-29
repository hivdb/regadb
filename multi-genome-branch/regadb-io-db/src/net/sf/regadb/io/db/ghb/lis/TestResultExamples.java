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
			if(line.trim().length() == 0)
				continue;
			
			String fields[] = split(line);
			
			for(int i=0; i<fields.length; ++i)
				fields[i] = parse(fields[i]);
			
			for(File f : new File(lisdir.getValue()).listFiles()){
				if(!f.getName().startsWith("GHB_"))
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
				
				String line2;
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
					
					if(found)
						break;
				}
				
				br2.close();
				
				if(found){
					if(!headers2.equals(prevHeaders)){
						System.out.println(headers2);
						prevHeaders = headers2;
					}
					System.out.println(line2);
					break;
				}
			}
		}
	}

	public static String[] split(String line){
		return line.split("\t");
	}
	
	public static String parse(String field){
		return field.replace("\"", "").trim();
	}
}
