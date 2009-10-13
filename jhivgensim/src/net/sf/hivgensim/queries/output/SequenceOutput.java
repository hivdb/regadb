package net.sf.hivgensim.queries.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.sf.hivgensim.preprocessing.MutationTable;
import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.regadb.db.NtSequence;

public class SequenceOutput implements IQuery<NtSequence> {
	
	private boolean treated = false;
	private SelectionWindow sw;
	private MutationTable mt;
	private File mutationTable;
	private PrintStream fasta;
	private PrintStream csv;
	private ArrayList<String> info = new ArrayList<String>();
	
	public SequenceOutput(File workDir, String filenamePrefix, SelectionWindow sw, MutationTable mt, String drug){
		info.add(drug);
		Set<String> sinfo = new HashSet<String>();
		sinfo.addAll(info);
		
		if(mt == null){
			this.mt = new MutationTable(sw.getAllMutations(),sinfo);
		}else{
			this.treated = true;
			this.mt = mt;
		}
		this.sw = sw;
				
		mutationTable = new File(workDir.getAbsolutePath() + File.separator + filenamePrefix + ".mt");
		try {
			fasta = new PrintStream(new File(workDir.getAbsolutePath() + File.separator + filenamePrefix + ".fasta"));
			csv = new PrintStream(new File(workDir.getAbsolutePath() + File.separator + filenamePrefix + ".csv"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public SequenceOutput(File workDir, String filenamePrefix, SelectionWindow sw, String drug){
		this(workDir,filenamePrefix,sw,null,drug);				
	}

	public void process(NtSequence input) {
//		String aligned = Utils.getAlignedNtSequenceString(input,sw);
		String aligned = input.getNucleotides();
		if(treated){
			mt.addSequence(input,new SelectionWindow[]{sw},info);
		}else{
			mt.addSequence(input,new SelectionWindow[]{sw});
		}
		
		fasta.println(">"+input.getViralIsolate().getSampleId());
		fasta.println(aligned);
		
		csv.println(input.getViralIsolate().getSampleId()+","+input.getLabel()+","+input.getNucleotides());
	}
	
	public MutationTable getMutationTable(){
		return mt;
	}

	public void close() {
		try {
			mt.exportAsCsv(new FileOutputStream(mutationTable));
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}
		fasta.flush();
		fasta.close();
		csv.flush();
		csv.close();
	}

}
