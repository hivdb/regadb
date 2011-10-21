package net.sf.regadb.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.ioAssist.IOAssistImportHandler;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.NominalArgument;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

public class SubTypeBatch {
	public static void main(String [] args) throws Exception{
		Arguments as = new Arguments();
		PositionalArgument input = as.addPositionalArgument("input-file", true);
		PositionalArgument url = as.addPositionalArgument("wts-url", false);
		NominalArgument type = as.addNominalArgument("t", Arrays.asList("csv","fasta"), false);
		type.setValue("csv");
		ValueArgument confDir = as.addValueArgument("c", "conf-dir", false);
		
		if(!as.handle(args))
			return;
		
		if(confDir.isSet())
			RegaDBSettings.createInstance(confDir.getValue());
		else
			RegaDBSettings.createInstance();
		
		Test subtype = RegaDBWtsServer.getSubtypeTest();
		if(url.isSet()) {
			subtype.getAnalysis().setUrl(url.getValue());
		}
		
		Genome genome = StandardObjects.getHiv1Genome();
		
		if(type.getValue().equals("csv")){
			Table inputTable = null;
			try {
				inputTable = new Table(new BufferedInputStream(new FileInputStream(input.getValue())), false);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			for(int i =1; i<inputTable.numRows(); i++) {
				subtype(subtype, genome, inputTable.valueAt(0, i), inputTable.valueAt(1, i));
			}
		}
		else if(type.getValue().equals("fasta")){
			FastaFile ff = new FastaFile(new File(input.getValue()));
			
			for(int i=0; i<ff.size(); ++i){
				NtSequence nt = ff.get(i);
				subtype(subtype, genome, nt.getLabel(), nt.getNucleotides());
			}
		}
	}
	
	public static void subtype(Test subtype, Genome genome, String label, String nucleotides){
		NtSequence ntseq = new NtSequence();
		ntseq.setLabel(label);
		ntseq.setNucleotides(nucleotides);
		
		//TODO fixed genome
		TestResult tr = IOAssistImportHandler.doSubtypeAnalysis(ntseq, subtype, genome);
		
		System.out.println(label + "," + tr.getValue());
	}
}
