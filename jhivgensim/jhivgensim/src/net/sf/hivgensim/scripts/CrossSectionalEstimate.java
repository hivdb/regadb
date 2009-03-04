package net.sf.hivgensim.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import net.sf.hivgensim.fastatool.FastaClean;
import net.sf.hivgensim.fastatool.FastaRegion;
import net.sf.hivgensim.fastatool.SelectionWindow;
import net.sf.hivgensim.preprocessing.MutationTable;
import net.sf.hivgensim.preprocessing.RemoveMixtures;
import net.sf.hivgensim.preprocessing.Utils;
import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.GetTreatedSequences;
import net.sf.hivgensim.queries.framework.DefaultQueryOutput;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.input.FromSnapshot;
import net.sf.hivgensim.queries.output.SequencesToFasta;
import net.sf.hivgensim.services.SequenceTool;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.tools.MutPos;

import org.biojava.bio.BioException;

public class CrossSectionalEstimate {
	
	private String workDir = "/home/gbehey0/hivgensim";
	private String[] naiveDrugClasses = {"NRTI"};
	private String[] drugs = {"AZT","3TC"};
	private String organismName = "HIV-1";
	private String orfName = "pol";
	private String proteinAbbreviation = "RT";
	private double threshold = 0.01;
	private boolean lumpValues = false;
	
	private Login login;
	private SelectionWindow[] windows;
	
	public CrossSectionalEstimate(){
		Login login = null;
		try {
			login = Login.authenticate("gbehey0", "bla123");
		} catch (WrongUidException e) {
			e.printStackTrace();
		} catch (WrongPasswordException e) {
			e.printStackTrace();
		} catch (DisabledUserException e) {
			e.printStackTrace();
		}
		
		windows = new SelectionWindow[]{
				new SelectionWindow(Utils.getProtein(login, organismName, orfName, proteinAbbreviation),44,200)
		};
		
	}
	
	public void run(){
		try {
			query();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		align();
		clean();
		region();
		table();
		//make phylo.fasta from naive.clean.fasta and treated.clean.fasta
		
	}
	
	public void query() throws FileNotFoundException{
		long startTime,stopTime;
		
		//queries
		//input and output
		startTime = System.currentTimeMillis();
		QueryInput input = new FromSnapshot(new File("/home/gbehey0/snapshot"));
		input.getOutputList();
		stopTime = System.currentTimeMillis();
		System.out.println("time to read in snapshot: "+(stopTime-startTime)+" ms");

		//naive
		startTime = System.currentTimeMillis();
		Query<NtSequence> qn = new GetDrugClassNaiveSequences(input,naiveDrugClasses);
		DefaultQueryOutput<NtSequence> output = new SequencesToFasta(new File(workDir + File.separator + "naive.seqs.fasta"));
		output.output(qn.getOutputList());
		stopTime = System.currentTimeMillis();
		System.out.println("found "+ qn.getOutputList().size() + " naive seqs in "+(stopTime-startTime)+" ms");

		//experienced
		startTime = System.currentTimeMillis();
		Query<NtSequence> qe = new GetTreatedSequences(input,drugs);
		output = new SequencesToFasta(new File(workDir + File.separator + "treated.seqs.fasta"));
		output.output(qe.getOutputList());
		stopTime = System.currentTimeMillis();
		System.out.println("found "+ qe.getOutputList().size() + " treated seqs in "+(stopTime-startTime)+" ms");
	}
	
	public void align(){
		//align
		SequenceTool st = new SequenceTool();
		Utils.createReferenceSequenceFile(login, organismName, orfName, workDir + File.separator + "reference.fasta");
		st.align(workDir + File.separator + "reference.fasta",
				workDir + File.separator + "naive.seqs.fasta",
				workDir + File.separator + "aligned.naive.fasta");

		st.align(workDir + File.separator + "reference.fasta",
				workDir + File.separator + "treated.seqs.fasta",
				workDir + File.separator + "aligned.treated.fasta");
	}
	
	public void clean(){
		//clean: remove too short seqs
		try {
			FastaClean fc = new FastaClean(
					workDir + File.separator + "aligned.treated.fasta",
					workDir + File.separator + "clean.treated.fasta",
					windows);
			fc.processFastaFile();

			fc = new FastaClean(
					workDir + File.separator + "aligned.naive.fasta",
					workDir + File.separator + "clean.naive.fasta",
					windows);
			fc.processFastaFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void region(){
		try {
			FastaRegion fr = new FastaRegion(
					workDir + File.separator + "clean.naive.fasta",
					workDir + File.separator + "dna_naive.fasta",
					windows);
			fr.processFastaFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void table(){
		try {
			MutationTable mt = new MutationTable(workDir + File.separator + "clean.treated.fasta",windows);
			mt.exportAsCsv(new FileOutputStream(new File(workDir + File.separator + "all.mutations.csv")));

			mt.removeInsertions();
			mt.removeUnknownMutations();
			mt.removeLowPrevalenceMutations(threshold,lumpValues);

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
			
			
			
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (BioException e) {
			e.printStackTrace();
		}
	}		
}

