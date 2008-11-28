package net.sf.hivgensim.preprocessing;

import java.io.File;
import java.io.FileNotFoundException;

import net.sf.hivgensim.fastatool.FastaClean;
import net.sf.hivgensim.fastatool.FastaRegion;
import net.sf.hivgensim.fastatool.SelectionWindow;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;

public class Main {
	
	public static void main(String[] args) {
		
		long startTime;
		long stopTime;
		String workDir = "/home/gbehey0/jhivgensim";
		String[] naiveDrugClasses = {"NRTI"};
		String[] drugs = {"AZT","3TC"};
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
		
		String organismName = "HIV-1";
		String orfName = "pol";
		String proteinAbbreviation = "RT";
						
		//queries
		//input and output
//		startTime = System.currentTimeMillis();
//		QueryInput input = new FromSnapshot(new File("/home/gbehey0/queries/stanford.snapshot"));
//		input.getOutputList();
//		stopTime = System.currentTimeMillis();
//		System.out.println("time to read in snapshot: "+(stopTime-startTime)+" ms");
		
		//naive
//		startTime = System.currentTimeMillis();
//		Query<NtSequence> qn = new GetNaiveSequences(input,naiveDrugClasses);
//		QueryOutput<NtSequence> output = new ToFasta(new File(workDir + File.separator + "naive.seqs.fasta"));
//		output.generateOutput(qn);
//		stopTime = System.currentTimeMillis();
//		System.out.println("found "+ qn.getOutputList().size() + " naive seqs in "+(stopTime-startTime)+" ms");
		
		//experienced
//		startTime = System.currentTimeMillis();
//		Query<NtSequence> qe = new GetExperiencedSequences(input,drugs);
//		output = new ToFasta(new File(workDir + File.separator + "experienced.seqs.fasta"));
//		output.generateOutput(qe);
//		stopTime = System.currentTimeMillis();
//		System.out.println("found "+ qe.getOutputList().size() + " experienced seqs in "+(stopTime-startTime)+" ms");
		
		//align
//		SequenceTool st = new SequenceTool();
//		Utils.createReferenceSequenceFile(login, organismName, orfName, workDir + File.separator + "reference.fasta");
//		st.align(workDir + File.separator + "reference.fasta",
//				 workDir + File.separator + "experienced.seqs.fasta",
//				 workDir + File.separator + "experienced.aligned.seqs.fasta");
		
		//clean: remove too short seqs
		try {
			FastaClean fc = new FastaClean( new SelectionWindow[]{
					new SelectionWindow(Utils.getProtein(login, organismName, orfName, proteinAbbreviation),44,200)
			});
			fc.clean(workDir + File.separator + "in.fasta",
							 workDir + File.separator + "new.out.fasta");
							
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			FastaRegion fr = new FastaRegion( new SelectionWindow[]{
					new SelectionWindow(Utils.getProtein(login, organismName, orfName, proteinAbbreviation),1,560)
			});
			fr.getRegion(workDir + File.separator + "new.out.fasta",
							 workDir + File.separator + "new.region.fasta");
							
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		
		
		
	}

}
