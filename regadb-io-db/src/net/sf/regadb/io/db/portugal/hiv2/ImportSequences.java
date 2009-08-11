package net.sf.regadb.io.db.portugal.hiv2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.analysis.functions.FastaRead;
import net.sf.regadb.analysis.functions.FastaReadStatus;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.ioAssist.IOAssistImportHandler;
import net.sf.regadb.service.wts.RegaDBWtsServer;

import org.apache.commons.io.FileUtils;

public class ImportSequences {
	public static void main(String [] args) {
		ImportSequences is = new ImportSequences();
		is.run("/home/plibin0/import/pt/pt_regadb/hiv2/seqs");
	}
	
	public void run(String path) {
		
        System.setProperty("http.proxyHost", "172.20.201.15");
        System.setProperty("http.proxyPort", "8080");
        
		File root = new File(path);
		
		//recursively search the directory structure
		List<File> consensusFiles = new ArrayList<File>();
		getFile(root, consensusFiles);
		
		for(File f : consensusFiles) {
			FastaRead read = FastaHelper.readFastaFile(f, false);
			if(read.status_ != FastaReadStatus.Valid) {
				System.err.println("Invalid fasta file: " + f.getAbsolutePath() + " -> " + read.status_);
			}
		}

		Test subtype = RegaDBWtsServer.getSubtypeTest();

		try {
			FileWriter fw = new FileWriter("/home/plibin0/import/pt/pt_regadb/hiv2/seqs/combinedSeqs.fasta");
			for(File f : consensusFiles) {
				String fasta = FileUtils.readFileToString(f, null);
				String [] header = fasta.substring(fasta.indexOf('>'), fasta.indexOf("\n")).split(" ");
				fw.write(">"+header[header.length-1]+"\n");
				fw.write(fasta.substring(fasta.indexOf("\n")+1));

					NtSequence ntseq = new NtSequence();
					ntseq.setLabel(header[header.length-1]);
					ntseq.setNucleotides(fasta.substring(fasta.indexOf("\n")+1));
					
					//TODO fixed genome
					TestResult tr = IOAssistImportHandler.doSubtypeAnalysis(ntseq, subtype, StandardObjects.getHiv1Genome());
					System.out.println(header[header.length-1] + "," + tr.getValue());
			}
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void getFile(File start, List<File> consensus) {
		for(File f : start.listFiles()) {
			if(f.isDirectory()) {
				getFile(f, consensus);
			} else {
				if(f.getAbsolutePath().toLowerCase().contains("consensus")) {
					consensus.add(f);
				}
			}
		}
	}
}
