package net.sf.hivgensim.selection.mrmr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.hivgensim.preprocessing.MutationTable;
import net.sf.hivgensim.selection.AbstractSelection;
import net.sf.regadb.csv.Table;

public class MutualInformationSelection extends AbstractSelection {

	private String drugName;
	private int number;
	
	public MutualInformationSelection(File input, File output, String drugName, int number) throws FileNotFoundException {
		super(input,output);
		this.drugName = drugName;
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
		SortedMap<Double, String> mutualInformation = new TreeMap<Double, String>(Collections.reverseOrder());
		List<String> drug = t.getColumn(t.findColumn(drugName));
		drug = drug.subList(1, drug.size());
		for(String fName : fNames){
			List<String> feature = t.getColumn(fName);
			feature = feature.subList(1, feature.size());
			MutualInformation mi = new MutualInformation(feature, drug);
			mutualInformation.put(mi.getMI(),fName);			
		}
		List<String> selection = new ArrayList<String>();
		for(Map.Entry<Double, String> e : mutualInformation.entrySet()){
			if(number < selection.size()){
				break;
			}
			System.err.format("MI(%s,"+drugName+") = %4f%n", e.getValue(), e.getKey());
			selection.add(e.getValue());
		}
		
		boolean[] result = new boolean[t.numColumns()];
		ArrayList<String> names = t.getRow(0);
		for(int i = 0; i < result.length; i++){
			if(!fNames.contains(names.get(i)) || selection.contains(names.get(i))){
				result[i] = true;
			}
		}
		return result;		
	}
	
	public static void main(String[] args) throws NumberFormatException, FileNotFoundException {
		if(args.length != 4){
			System.err.println("Usage: MutualInformationSelection in.csv out.csv drugName number");
			System.exit(1);
		}
		new MutualInformationSelection(new File(args[0]), new File(args[1]), args[2], Integer.parseInt(args[3])).select();
	}
	

}
