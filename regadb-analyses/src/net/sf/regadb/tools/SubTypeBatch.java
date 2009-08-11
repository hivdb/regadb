package net.sf.regadb.tools;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.ioAssist.IOAssistImportHandler;
import net.sf.regadb.service.wts.RegaDBWtsServer;

public class SubTypeBatch {
	public static void main(String [] args) {
		if(args.length<1) {
			System.err.println("Usage input.csv [wts-url]");
			System.exit(0);
		}
		String wtsServer = null;
		if(args.length>1) {
			wtsServer = args[1];
		}
		
		Test subtype = RegaDBWtsServer.getSubtypeTest();
		if(wtsServer!=null) {
			subtype.getAnalysis().setUrl(wtsServer);
		}
		
		Table inputTable = null;
		try {
			inputTable = new Table(new BufferedInputStream(new FileInputStream(args[0])), false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for(int i =1; i<inputTable.numRows(); i++) {
			NtSequence ntseq = new NtSequence();
			ntseq.setLabel(inputTable.valueAt(0, i));
			ntseq.setNucleotides(inputTable.valueAt(1, i));
			
			//TODO fixed genome
			TestResult tr = IOAssistImportHandler.doSubtypeAnalysis(ntseq, subtype, StandardObjects.getHiv1Genome());
			
			System.out.println(inputTable.valueAt(0, i) + "," + tr.getValue());
		}
	}
}
