package net.sf.regadb.io.db.uzbrussel;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Utils;

public class ParseOldViralLoad {
	Table oldVLToIgnoreTable;
	
	public static void main(String [] args) {
		ParseOldViralLoad ovl = new ParseOldViralLoad();
		//ovl.run("/home/plibin0/import/jette/old_vl/", new HashM);
	}
	
	public void run(String path, ParseIds parseIds, Map<Integer, Patient> patients, File oldVLToIgnore) {
		oldVLToIgnoreTable = Utils.readTable(oldVLToIgnore.getAbsolutePath());
		System.err.println("Parse old viral load excel files=======================================");
		File pathF = new File(path);
		File [] excelFiles = pathF.listFiles();
		
		Arrays.sort(excelFiles, new Comparator<File>() {
			public int compare(File arg0, File arg1) {
				return getInt(arg0).compareTo(getInt(arg1));
			}
		});
		
		for(File eF : excelFiles) {
			Sheet s = getKwekenSheet(eF);
			if(s!=null) {
				System.err.println("Importing: " + eF);
				parseSheet(s, parseIds, patients);
			} else {
				ConsoleLogger.getInstance().logError("Excel workbook doesn't  contain a 'kweken' sheet: " + eF.getName());
			}
		}
		System.err.println("Parse old viral load excel files=======================================");
	}
	
	private void parseSheet(Sheet s, ParseIds parseIds, Map<Integer, Patient> patients) {
		int dossierNrC = getColPos(s, "dossiernr", "");
		int datumC = getColPos(s, "datum", "");
		int vlC = getColPos(s, "q-rna", "copies/ml");
		int vlLogC = getColPos(s, "q-rna", "log copies/ml");
		for(int i = 0; i<s.getRows(); i++) {
			String dossierNr = s.getCell(dossierNrC, i).getContents().trim();
			if(!dossierNr.equals("") && !dossierNr.toLowerCase().equals("dossiernr")) {
				Integer patientId = parseIds.getPatientId(dossierNr);
				if(patients.get(patientId)==null) {
					if(!ignoreViralLoad(dossierNr)) {
						ConsoleLogger.getInstance().logWarning("Cannot map dossierNr to patientId: " + dossierNr);
					}
				} else {
					String date = s.getCell(datumC, i).getContents().trim();
					String vl = s.getCell(vlC, i).getContents().trim();
					String vlLog = s.getCell(vlLogC, i).getContents().trim();
				}
			}
		}
	}
	
	public boolean ignoreViralLoad(String id) {
		for(int i = 1; i<oldVLToIgnoreTable.numRows(); i++) {
			if(id.equals(oldVLToIgnoreTable.valueAt(0, i))) {
				return true;
			}
		}
		return false;
	}
	
	private int getColPos(Sheet s, String colName, String colName2) {
		for(int i = 0; i<s.getColumns(); i++) {
			Cell c1  = s.getCell(i, 0);
			Cell c2 = s.getCell(i, 1);
			if(c1.getContents().replaceAll(" ", "").trim().toLowerCase().equals(colName.toLowerCase()) && c2.getContents().trim().toLowerCase().equals(colName2.toLowerCase())) {
				return c1.getColumn();
			}
		}
		ConsoleLogger.getInstance().logError("Cannot find col " + colName);
		return -1;
	}
	
	private Integer getInt(File f) {
		return Integer.parseInt(f.getName().substring(2,6));
	}
	
	private Sheet getKwekenSheet(File excelFile) {
		Sheet kwekenSheet = null;
		
		try {
			Workbook wb = Workbook.getWorkbook(excelFile);
			
			for(int i = 0; i<wb.getNumberOfSheets(); i++) {
				if(wb.getSheet(i).getName().toLowerCase().contains("kweken")) {
					if(kwekenSheet==null) {
						kwekenSheet = wb.getSheet(i);
					} else {
						ConsoleLogger.getInstance().logError("Excel workbook contains multiple 'kweken' sheets: " + excelFile.getName());
					}
				}
			}
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return kwekenSheet;
	}
}
