package net.sf.regadb.align.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class CompareFastaAlign {
	public static void main(String [] args) throws IOException {
		File v1 = new File(args[0]);
		File v2 = new File(args[1]);
		
		int count = 0;
		
		List<String> lines_v1 = FileUtils.readLines(v1);
		List<String> lines_v2 = FileUtils.readLines(v2);
		
		if(lines_v1.size()==lines_v2.size()) {
			for(int i = 0; i<lines_v1.size(); i++) {
				String[] s_v1 = lines_v1.get(i).split(" ");
				proToPr(s_v1);
				Arrays.sort(s_v1);
				String[] s_v2 = lines_v2.get(i).split(" ");
				proToPr(s_v2);
				Arrays.sort(s_v2);
				
				if(!Arrays.deepEquals(s_v1, s_v2)) {
					count++;
					System.err.println(i+"-------------------------------------------------");
					System.err.println(Arrays.toString(s_v1));
					System.err.println(Arrays.toString(s_v2));
					System.err.println("-------------------------------------------------");
				}
			}
		} else {
			System.err.println("# lines differ");
		}
		
		System.err.println(count);
	}
	
	public static void proToPr(String [] s_v) {
		for(int j = 0; j<s_v.length; j++) {
			if(s_v[j].startsWith("PRO")) {
				
			}
			else if(s_v[j].startsWith("PR")){
				s_v[j] = "PRO" + s_v[j].substring(2);
			}
		}
	}
}
