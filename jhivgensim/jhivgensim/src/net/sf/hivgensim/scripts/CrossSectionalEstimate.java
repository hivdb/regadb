package net.sf.hivgensim.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.hivgensim.fastatool.FastaConcat;
import net.sf.hivgensim.preprocessing.MutationTable;
import net.sf.hivgensim.preprocessing.RemoveMixtures;
import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.CleanSequences;
import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.GetTreatedSequences;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.input.FromSnapshot;
import net.sf.hivgensim.queries.output.SequencesToFasta;
import net.sf.hivgensim.queries.output.ToObjectList;
import net.sf.hivgensim.services.Paup;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.tools.MutPos;

public class CrossSectionalEstimate {
	
	//initialized variables
	private String workDir = "/home/gbehey0/3tcd4t";
	private String[] naiveDrugClasses = {"NRTI","NNRTI"};
	private String[] drugs = {"3TC","D4T"};
//	private String organismName = "HIV-1";
//	private String orfName = "pol";
//	private String proteinAbbreviation = "RT";
	private SelectionWindow[] smallWindows = new SelectionWindow[]{SelectionWindow.RT_WINDOW_CLEAN};
	private SelectionWindow[] fullWindows = new SelectionWindow[]{SelectionWindow.RT_WINDOW_REGION};
	private double threshold = 0.01;
	private boolean lumpValues = false;
	
	public CrossSectionalEstimate(){
		
	}
	
	public void run() throws FileNotFoundException{
		long startTime,stopTime;
		
		//queries
		//naive dirty
		QueryInput query =  new FromSnapshot(new File("/home/gbehey0/snapshot"),
							new GetDrugClassNaiveSequences(naiveDrugClasses,
							new SequencesToFasta(new File(workDir + File.separator + "naive.fasta"))));
		query.run();
		//naive clean
		ToObjectList<NtSequence> tol = new ToObjectList<NtSequence>();
		query =	new FromSnapshot(new File("/home/gbehey0/snapshot"),
				new GetDrugClassNaiveSequences(naiveDrugClasses,
				new CleanSequences(smallWindows,
				tol)));
		query.run();
		List<NtSequence> cleanNaive = tol.getList();
		
		//treated
		query = new FromSnapshot(new File("/home/gbehey0/snapshot"),
				new GetTreatedSequences(drugs,
				new SequencesToFasta(new File(workDir + File.separator + "treated.fasta"))));
		query.run();
		//treated clean
		tol = new ToObjectList<NtSequence>();
		query =	new FromSnapshot(new File("/home/gbehey0/snapshot"),
				new GetTreatedSequences(drugs,
				new CleanSequences(smallWindows,
				tol)));
		query.run();
		List<NtSequence> cleanTreated = tol.getList();
	
		//mutation table
		MutationTable mt = new MutationTable(cleanTreated,fullWindows);
		mt.exportAsCsv(new FileOutputStream(new File(workDir + File.separator + "all.mutations.csv")));

		mt.removeInsertions();
		mt.removeUnknownMutations();
		mt.removeLowPrevalenceMutations(threshold,lumpValues);
		//remove certain mutations: known/transmission/... ?
		
		mt.exportAsCsv(new FileOutputStream(workDir + File.separator + "all.mutations.selection.csv"),',', false);
		
		ArrayList<String> mutations = MutPos.execute(new String[]{
				workDir + File.separator + "mut.treated.selection.csv",
				workDir + File.separator + "mutations",
				workDir + File.separator + "positions",
				workDir + File.separator + "wildtypes"});
	
		mt.selectColumns(mutations);
		mt.exportAsCsv(new FileOutputStream(workDir + File.separator + "mut_treated.csv"),',', false);
		
		RemoveMixtures rm = new RemoveMixtures(mt);
		rm.removeMixtures();
		mt.exportAsCsv(new FileOutputStream(workDir + File.separator + "mut_treated_nomix.csv"),',', false);
		mt.deleteColumn(0);
		mt.exportAsVdFiles(
				new FileOutputStream(workDir + File.separator + "mut_treated.vd"),
				new FileOutputStream(workDir + File.separator + "mut_treated.idt"));
		
		//phylo
		FastaConcat fc = new FastaConcat(
						workDir + File.separator + "naive.fasta",
						workDir + File.separator + "treated.fasta",
						workDir + File.separator + "phylo.fasta");
		fc.processFastaFile();
		
		Paup paup = new Paup();
		paup.run(workDir + File.separator + "phylo.fasta",workDir + File.separator + "tree.phy");
		
		
		
	}
	
	
}

