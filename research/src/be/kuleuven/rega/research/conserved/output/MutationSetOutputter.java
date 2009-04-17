package be.kuleuven.rega.research.conserved.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.csv.Table;
import be.kuleuven.rega.research.conserved.MutationsPrevalence;

public class MutationSetOutputter implements ConservedRegionsOutputter {
	private Map<Integer, Character> mutations = new HashMap<Integer, Character>();
	private Table table = new Table();
	private File tableFileName;
	
	public MutationSetOutputter(File tableFileName, String ... mutations) {
		for(String m : mutations) {
			this.mutations.put(Integer.parseInt(m.substring(0, m.length()-1)), m.charAt(m.length()-1));
		}
		
		this.tableFileName = tableFileName;
		
		ArrayList<String> wantedMutations = new ArrayList<String>();
		wantedMutations.add("");
		Collections.addAll(wantedMutations, mutations);
		
		table.addRow(wantedMutations);
	}
	
	public void export(String group, Map<Integer, MutationsPrevalence> prevalences, int amountOfSequences) {
		String groupName = group + "(" + amountOfSequences + ")";
		
		ArrayList<String> row = new ArrayList<String>();
		row.add(groupName);
		for(Map.Entry<Integer, Character> e : mutations.entrySet()) {
			row.add(prevalences.get(e.getKey()).totalMutations(e.getValue())+"");
		}
		
		table.addRow(row);
	}
	
	public void writeTable() {
		try {
			OutputStream fos = new FileOutputStream(tableFileName);
			table.exportAsCsv(fos, ',', true);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
