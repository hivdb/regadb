package net.sf.hivgensim.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import net.sf.hivgensim.preprocessing.MutationTable;
import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.GetAllSequences;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.input.FromDatabase;
import net.sf.hivgensim.queries.output.ToObjectList;
import net.sf.regadb.db.NtSequence;

public class SequenceToMutationTable {

	public static void main(String[] args) {
		SelectionWindow[] fullWindows = new SelectionWindow[]{
				SelectionWindow.HIV_2_P6_WINDOW,
				SelectionWindow.HIV_2_PR_WINDOW,
				SelectionWindow.HIV_2_RT_WINDOW};
		System.out.println("ok");
		ToObjectList<NtSequence> sequences = new ToObjectList<NtSequence>();
		QueryInput qi = new FromDatabase("admin", "admin",
						new GetAllSequences(sequences));
		qi.run();
		
		MutationTable mt = new MutationTable(sequences.getList(),fullWindows);
		ArrayList<String> temp = new ArrayList<String>();
		temp.add("patient_id");
		for(NtSequence seq : sequences.getList()){
			temp.add(seq.getLabel());
		}
		mt.addColumn(temp,1);
		try {
			mt.exportAsCsv(new FileOutputStream(new File("/home/gbehey0/all_mutations.csv")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}

}
