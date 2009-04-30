package be.kuleuven.rega.research.conserved.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import net.sf.regadb.csv.Table;
import be.kuleuven.rega.research.conserved.MutationsPrevalence;

public class MutationSetOutputter implements ConservedRegionsOutputter {
	private String[] mutations;
	private Table table = new Table();
	private File tableFileName;
	
	public MutationSetOutputter(File tableFileName, String ... mutations) {
		this.mutations = mutations;
		
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
		
		int position;
		char aminoAcid;
		for(String m : mutations) {
			position = Integer.valueOf(m.substring(0,m.length()-1));
			aminoAcid = m.charAt(m.length()-1);
			row.add(prevalences.get(position).totalMutations(aminoAcid)+"");
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
