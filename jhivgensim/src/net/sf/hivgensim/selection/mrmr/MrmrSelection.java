package net.sf.hivgensim.selection.mrmr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.sf.hivgensim.preprocessing.MutationTable;
import net.sf.hivgensim.selection.AbstractSelection;
import net.sf.regadb.csv.Table;

public class MrmrSelection extends AbstractSelection {
	
	private String targetName;
	private int number;
	
	public MrmrSelection(File completeTable, File selectionTable, String targetName, int number) throws FileNotFoundException {
		super(completeTable, selectionTable);
		this.targetName = targetName;
		this.number = number;
	}

	protected boolean[] calculateSelection() {
		Table t = null;
		try {
			t = Table.readTable(getCompleteTable().getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ArrayList<String> fNames = new ArrayList<String>();
		for(String s : t.getRow(0)){
			if(MutationTable.MUT_PATTERN.matcher(s).matches()){
				fNames.add(s);
			}
		}
		String[] sarray = new String[fNames.size()];
		MrmrAlgorithm alg = new MrmrAlgorithm(t, targetName, fNames.toArray(sarray));
		List<String> selection = alg.selectFeatures(number);
		
		boolean[] result = new boolean[t.numColumns()];
		ArrayList<String> names = t.getRow(0);
		for(int i = 0; i < result.length; i++){
			if(!fNames.contains(names.get(i)) || selection.contains(names.get(i))){
				result[i] = true;
			}
		}
		return result;
	}

}
