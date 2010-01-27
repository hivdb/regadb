package net.sf.hivgensim.selection;

import java.io.File;
import java.io.FileNotFoundException;

import net.sf.hivgensim.preprocessing.MutationTable;

public class RelativeFrequency extends Frequency {

	private double threshold;
	
	public RelativeFrequency(File complete, File selection, double threshold) throws FileNotFoundException {
		super(complete, selection);
		this.threshold = threshold;
	}

	protected boolean[] calculateSelection() {
		String names[] = getNames();
		int counts[] = getCounts();
		double nbOfRows = (double) getNumberOfRows();
		boolean select[] = new boolean[counts.length];
		for(int i = 0; i < names.length;i++){
			if(!MutationTable.MUT_PATTERN.matcher(names[i]).matches() || counts[i]/nbOfRows > threshold){								
				select[i] = true;
			}
		}
		return select;
	}	

}
