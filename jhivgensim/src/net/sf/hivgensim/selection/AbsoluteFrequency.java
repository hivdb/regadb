package net.sf.hivgensim.selection;

import java.io.File;
import java.io.FileNotFoundException;

import net.sf.hivgensim.preprocessing.MutationTable;

/*
 * This class does not use the MutationTable class because often the table is too big 
 * to store in memory. It reads it twice to avoid storing too much data in memory.
 */

public class AbsoluteFrequency extends Frequency {
	
	private int threshold;
		
	public AbsoluteFrequency(File full, File selection, int count) throws FileNotFoundException{
		super(full, selection);
		this.threshold = count;		
	}
	
	protected boolean[] calculateSelection() {
		String names[] = getNames();
		int occurs[] = getCounts();
		boolean select[] = new boolean[occurs.length];
		for(int i = 0; i < names.length;i++){
			if(!MutationTable.MUT_PATTERN.matcher(names[i]).matches() || occurs[i] > threshold){								
				select[i] = true;
			}
		}
		return select;
	}

}
