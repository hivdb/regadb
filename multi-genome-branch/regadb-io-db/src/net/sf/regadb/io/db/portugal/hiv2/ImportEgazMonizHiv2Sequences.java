package net.sf.regadb.io.db.portugal.hiv2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.regadb.db.Patient;
import net.sf.regadb.io.db.util.ConsoleLogger;

import org.apache.commons.io.FileUtils;

public class ImportEgazMonizHiv2Sequences {
	public void run(Map<String, Patient> patientMap, File seqDir) throws BiffException, IOException {
		List<File> fastas = new ArrayList<File>();
		findFastaRecursively(fastas, new File(seqDir.getAbsolutePath()+File.separatorChar));
		
		File xlsFile = new File(seqDir.getAbsolutePath()+File.separatorChar+"seqs.xls");

		Workbook book = Workbook.getWorkbook(xlsFile);
		for(Sheet sheet : book.getSheets()){
			if(sheet.getName().startsWith("Seqs")) {
				for(int i = 1; i<sheet.getRows(); i++) {
					String patientID = sheet.getCell(0, i).getContents();
					String sampleID = sheet.getCell(2, i).getContents();
					if(!patientID.equals("") && !sampleID.equals("")) {
						Patient p = patientMap.get(patientID);
						File fasta = getFastaFromSampleID(fastas, sampleID);
						if(fasta==null) {
							ConsoleLogger.getInstance().logWarning("Cannot retrieve fasta for sample with ID"+sampleID);
						}
						if(p==null) {
							p = new Patient();
							p.setPatientId(patientID);
							patientMap.put(patientID, p);
							//TODO
							//processnumber -> clinical file number
						}
					}
				}
			}
		}
		
		System.err.println("=======fastas not in excell========");
		for(File f : fastas) {
			System.err.println(f.getAbsolutePath());
		}
		System.err.println("=======fastas not in excell========");
	}
	
	private File getFastaFromSampleID(List<File> fastaList, String sampleID) {
		File toReturn = null;
		for(File f : fastaList) {
			String filePath = f.getAbsolutePath();
			filePath = filePath.substring(0,filePath.lastIndexOf(File.separatorChar));
			String fastaSampleID = filePath.substring(filePath.lastIndexOf(File.separatorChar)+1);
			if(sampleID.equals(fastaSampleID)) {
				toReturn = f;
			}
		}
		if(toReturn!=null) {
			fastaList.remove(toReturn);
		}
		return toReturn;
	}
	
	private void findFastaRecursively(List<File> fastaList, File dir) {
		for(File f : dir.listFiles()) {
			if(f.isFile() && (f.getName().endsWith("fsta") || f.getName().endsWith("fasta")) && f.getName().toLowerCase().contains("consensus")) {
				fastaList.add(f);
			} else if(f.isDirectory()) {
				findFastaRecursively(fastaList, f);
			}
		}
	}
}
