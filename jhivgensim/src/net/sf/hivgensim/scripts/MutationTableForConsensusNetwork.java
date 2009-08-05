package net.sf.hivgensim.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import net.sf.hivgensim.preprocessing.MutationTable;
import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.BasicSampler;
import net.sf.hivgensim.queries.CleanSequences;
import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.GetTreatedSequences;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.hivgensim.queries.output.ToObjectList;
import net.sf.regadb.db.NtSequence;

public class MutationTableForConsensusNetwork {
	
	private String workDir = "/home/gbehey0/bn";
	private String snapshotFile = "/home/gbehey0/snapshot";
	private String[] naiveDrugClasses = new String[]{"NRTI","NNRTI"};
	private String[] drugs = new String[]{"3TC","D4T"};
	private SelectionWindow[] smallWindows = new SelectionWindow[]{SelectionWindow.RT_WINDOW_REGION};
	
	public void run(){
		ToObjectList<NtSequence> tol = new ToObjectList<NtSequence>();
		QueryInput query =  new FromSnapshot(new File(snapshotFile),
								  new GetDrugClassNaiveSequences(naiveDrugClasses,								  
								  new CleanSequences(smallWindows,
								  new BasicSampler<NtSequence>(0.20f,tol))));
		query.run();
		List<NtSequence> naive = tol.getList();
		MutationTable mt = new MutationTable(naive,smallWindows);
		try {
			mt.exportAsCsv(new FileOutputStream(new File(workDir + File.separator + "naive.csv")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		
		tol = new ToObjectList<NtSequence>();
		query =	new FromSnapshot(new File(snapshotFile),
				new GetTreatedSequences(drugs,				
				new CleanSequences(smallWindows,
				tol)));
		query.run();
		List<NtSequence> treated = tol.getList();
		mt = new MutationTable(treated,smallWindows);
		try {
			mt.exportAsCsv(new FileOutputStream(new File(workDir + File.separator + "treated.csv")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
