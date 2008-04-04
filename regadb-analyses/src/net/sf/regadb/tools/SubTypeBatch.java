package net.sf.regadb.tools;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.ValueType;
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
		
		Test subtype = RegaDBWtsServer.getHIV1SubTypeTest(new TestObject("Sequence analysis", 1), new AnalysisType("wts"), new ValueType("string"));
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
			ntseq.setLabel("label");
			ntseq.setNucleotides(inputTable.valueAt(0, i));
			TestResult tr = IOAssistImportHandler.ntSeqAnalysis(ntseq, subtype);
			System.out.println(inputTable.valueAt(0, i) + "," + inputTable.valueAt(1, i) + "," + tr.getValue());
		}
	}
}
