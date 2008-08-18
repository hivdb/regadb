package net.sf.regadb.io.db.uzbrussel;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

public class ParseOldViralLoad {
	Table oldVLToIgnoreTable;
	
	private static DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy");
	private static DateFormat dateFormatter2 = new SimpleDateFormat("dd MM yyyy");
	
	private static DateFormat dateFormatterSeqMap = new SimpleDateFormat("yyyy.MM.dd");
	
	private String old_vl_seq_map = "";
	public File seqMathOldVL;
	
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
		
		try {
			seqMathOldVL = File.createTempFile("seq_match_old_vl", "csv");
			FileUtils.writeStringToFile(seqMathOldVL, old_vl_seq_map);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.err.println("Parse old viral load excel files=======================================");
	}
	
	private void parseSheet(Sheet s, ParseIds parseIds, Map<Integer, Patient> patients) {
		int dossierNrC = getColPos(s, "dossiernr", "");
		int datumC = getColPos(s, "datum", "");
		int vlC = getColPos(s, "q-rna", "copies/ml");
		int vlLogC = getColPos(s, "q-rna", "log copies/ml");
		int grC = getColPos(s, "g.r.", "");
		int volgNrC = getColPos(s, "volgnr", "");
		for(int i = 0; i<s.getRows(); i++) {
			String dossierNr = s.getCell(dossierNrC, i).getContents().trim();
			if(!dossierNr.equals("") && !dossierNr.toLowerCase().equals("dossiernr")) {
				Integer patientId = parseIds.getPatientId(dossierNr);
				if(patients.get(patientId)==null) {
					if(!ignoreViralLoad(dossierNr)) {
						ConsoleLogger.getInstance().logWarning("Cannot map dossierNr to patientId: " + dossierNr);
					}
				} else {
					Patient p = patients.get(patientId);
					String date = s.getCell(datumC, i).getContents().trim();
					String vl = s.getCell(vlC, i).getContents().trim().replace(" ", "").replace("*", "");
					String vlLog = s.getCell(vlLogC, i).getContents().trim().replace(" ", "").replace("*", "");
					String gr = s.getCell(grC, i).getContents().trim();
					String volgNr = s.getCell(volgNrC, i).getContents().trim();
					
					if(!vl.equals(""))
						storeViralLoad(p, vl, date, StandardObjects.getGenericHiv1ViralLoadTest());
					
					if(!vlLog.equals(""))
						storeViralLoad(p, vlLog, date, StandardObjects.getGenericHiv1ViralLoadLog10Test());
					
					if(!gr.equals("") && !gr.equals("VLTL")) {
						Colour c = s.getCell(grC, i).getCellFormat().getBackgroundColour();
						if(c.getDescription().equals("light green")) {
							Date d = parseDate(date);
							this.old_vl_seq_map += dossierNr + ";" + getCorrectSeqId(volgNr) + ";" + this.dateFormatterSeqMap.format(d) + ";ZBR\n";
						}
					}
				}
			}
		}
	}
	
	private String getCorrectSeqId(String seqId) {
		String [] parts = seqId.split("\\/");
		
		if(parts.length==2 && parts[0].length()>1) {
			String result = parts[0].charAt(1)+"";
			for(int i = 0; i<7-parts[1].length(); i++) {
				result +="0";
			}
			
			result += parts[1];
			
			return result;
		} else {
			return seqId;
		}
	}
	
	public void storeViralLoad(Patient p, String val, String date, Test t) {
        String vlVal = ParseConsultDB.parseViralLoad(val);
        if(vlVal!=null) {
        	Date d = parseDate(date);
			if(d!=null) {
	        	TestResult tr = p.createTestResult(t);
	        	tr.setValue(vlVal);
				tr.setTestDate(d);
			} else {
				ConsoleLogger.getInstance().logWarning("Cannot parse old vl date: " + date);
			}
        } else {
        	ConsoleLogger.getInstance().logWarning("Cannot parse old vl val: " + val);
        }
	}
	
	public Date parseDate(String date) {
		Date d = null;
		
		try {
			d = dateFormatter.parse(date);
		} catch (ParseException e) {
		}
		if(d==null) {
			try {
				d = dateFormatter2.parse(date);
			} catch (ParseException e) {
			}
		}
		
		return d;
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
