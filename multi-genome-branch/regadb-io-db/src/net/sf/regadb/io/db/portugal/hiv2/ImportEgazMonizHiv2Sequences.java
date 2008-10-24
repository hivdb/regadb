package net.sf.regadb.io.db.portugal.hiv2;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.regadb.db.Patient;
import net.sf.regadb.io.db.util.ConsoleLogger;

public class ImportEgazMonizHiv2Sequences {
	public void run(Map<String, Patient> patientMap, File seqDir) throws BiffException, IOException {
		File xlsFile = new File(seqDir.getAbsolutePath()+File.separatorChar+"seqs.xls");

		Workbook book = Workbook.getWorkbook(xlsFile);
		for(Sheet sheet : book.getSheets()){
			if(sheet.getName().startsWith("Seqs")) {
				for(int i = 1; i<sheet.getRows(); i++) {
					String patientID = sheet.getCell(0, i).getContents();
					String sampleID = sheet.getCell(2, i).getContents();
					if(!patientID.equals("") && !sampleID.equals("")) {
						Patient p = patientMap.get(patientID);
//						if(p!=null) {
//							
//						} else {
//	                        ConsoleLogger.getInstance().logWarning("Cannot retrieve patient with ID"+patientID);
//						}
					}
				}
			}
		}
	}
	
	private File findFastaRecursively(String sampleID, File dir) {
		for(File f : dir.listFiles()) {
			if(f.isFile() && (f.getName().endsWith("fsta") || f.getName().endsWith("fasta"))) {
				System.err.println(sampleID);
			}
		}
		return null;
	}
}
