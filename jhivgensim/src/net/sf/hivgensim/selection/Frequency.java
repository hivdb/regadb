package net.sf.hivgensim.selection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public abstract class Frequency extends AbstractSelection{
	
	public Frequency(File complete, File selection) throws FileNotFoundException{
		super(complete,selection);
	}
	
	public int[] getCounts(){
		Scanner s = null;
		try {
			s = new Scanner(getCompleteTable());
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}
		String[] names = s.nextLine().trim().split(",");
		int[] counts = new int[names.length];
						
		int i = 0;
		while(s.hasNextLine()){
			i = 0;
			for(String b : s.nextLine().split(",")){
				if(b.equals("y")){
					counts[i]++;
				}
				i++;
			}
		}
		s.close();
		return counts;
	}	

}
