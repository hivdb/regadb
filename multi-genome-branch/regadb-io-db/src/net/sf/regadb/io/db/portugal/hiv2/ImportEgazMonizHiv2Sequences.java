package net.sf.regadb.io.db.portugal.hiv2;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.analysis.functions.FastaRead;
import net.sf.regadb.analysis.functions.FastaReadStatus;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Utils;

public class ImportEgazMonizHiv2Sequences {
	public void run(ImportEgazMonizHiv2 mainImport, File seqDir) throws BiffException, IOException {
		List<File> fastas = new ArrayList<File>();
		findFastaRecursively(fastas, new File(seqDir.getAbsolutePath()+File.separatorChar));
		
		File xlsFile = new File(seqDir.getAbsolutePath()+File.separatorChar+"seqs.xls");

		Workbook book = Workbook.getWorkbook(xlsFile);
		for(Sheet sheet : book.getSheets()){
			if(sheet.getName().startsWith("Seqs")) {
				for(int i = 1; i<sheet.getRows(); i++) {
					String patientID = sheet.getCell(0, i).getContents();
					String processNr = sheet.getCell(1, i).getContents();
					String sampleID = sheet.getCell(2, i).getContents();
					String sampleDate = sheet.getCell(3, i).getContents();
					
					if(!patientID.equals("") && !sampleID.equals("")) {
						Patient p = mainImport.patientMap.get(patientID);
						File fasta = getFastaFromSampleID(fastas, sampleID);
						if(fasta==null) {
							ConsoleLogger.getInstance().logWarning("Cannot retrieve fasta for sample with ID"+sampleID);
						} else {
							if(p==null) {
								p = new Patient();
								p.setPatientId(patientID);
								mainImport.patientMap.put(patientID, p);
								if(!processNr.equals("") && !processNr.equals("0")) {
									p.createPatientAttributeValue(mainImport.clinicalFileNumberA).setValue(processNr);
								} else {
									System.err.println("ERR(seqs): No processNr for PatientNr=" + patientID);
								}
							}
				            FastaRead fr = FastaHelper.readFastaFile(fasta, true);
				            if(fr.status_ == FastaReadStatus.Valid || fr.status_ == FastaReadStatus.ValidButFixed) {
				            	String nucleotides = fr.seq_.seqString();
				            	nucleotides = Utils.clearNucleotides(nucleotides);
			                    ViralIsolate vi = p.createViralIsolate();
			                    
			                    try {
			                    	Date d = mainImport.dateFormatter.parse(sampleDate);
			                    	vi.setSampleDate(d);
			                    } catch (ParseException e) {
			                    	ConsoleLogger.getInstance().logWarning("Cannot parse data " + sampleDate + " " + sampleID);
			                    }
			                    
			                    vi.setSampleId(sampleID);

			                    NtSequence nts = new NtSequence(vi);
			                    vi.getNtSequences().add(nts);
			                    nts.setNucleotides(nucleotides);
			                    nts.setLabel("Sequence 1");
				            } else {
								ConsoleLogger.getInstance().logWarning("Bad fasta file "+fasta + " ->" + fr.status_);
				            }
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
