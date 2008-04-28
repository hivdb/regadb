package net.sf.regadb.io.db.eve;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

public class ImportFromXls {
	private static ArrayList<Cell[]> rowChunk;
	private static int currentRow;
	private static HashMap<String, Integer> headers;
	private static List<DrugGeneric> regaDrugGenerics;
	private static int sampleID = 0;
	private static String basePath_ = "/home/dluypa0/Eve"; // data.xls and previousGT.fasta should be in here
	private static HashMap<String,ViralIsolate> viralisolates = new HashMap<String,ViralIsolate> ();
	
	public static void main( String[]args ) {
		new ImportFromXls(basePath_ + File.separatorChar + "data.xls");
	}
	
	public ImportFromXls(String filename) {
		this( new File(filename) );
	}
	
	public ImportFromXls(File file) {
        System.setProperty("http.proxyHost", "www-proxy");
        System.setProperty("http.proxyPort", "3128");
        regaDrugGenerics = Utils.prepareRegaDrugGenerics();
        
		Workbook wb = null;
		
		try {
        	wb = Workbook.getWorkbook(file);
        } catch (Exception e) {
            System.err.println("file not found: " + file.getAbsolutePath());
            System.exit(1);
        }
        
        readHeader(wb.getSheet(0));
    	
        currentRow = 1;
    	
    	HashMap<String, Patient> patientMap = new HashMap<String, Patient>();
        
    	while ( readyNextTherapy(wb.getSheet(0)) ) {
    		String patientCase = getCell(0, "QuestionN");
    		
    		Patient p = new Patient();
    		p.setPatientId(patientCase);
    		patientMap.put(patientCase, p);
    		
    		Date stopDate = null;
    		
    		for(int i=0; i<rowChunk.size(); i++) {
	    		String drugs = getTherapy(i).replaceAll(",", " ");
				if ( !drugs.equals("") ) {
					processTherapy(p, getDate(i), stopDate, drugs);
					stopDate = getDate(i);
				}
    			
    			processFasta(p, i);
    			processCD4(p, i);
    			processViralLoad(p, i);
    		}
    	}
    	
    	Utils.exportPatientsXML(patientMap, basePath_ + File.separatorChar + "patients_eve.xml");
    	Utils.exportNTXML(viralisolates, basePath_ + File.separatorChar + "sequences_eve.xml");
	}
    
	private void processTherapy(Patient p, Date startDate, Date stopDate, String drugs) {		
		Therapy t = p.createTherapy(startDate);
		t.setStopDate(stopDate);
		
		drugs = drugs.replaceAll("LPV RTVB", "LPV/r");
		drugs = drugs.replaceAll("IDV RTVB", "IDV/r");
		drugs = drugs.replaceAll("ATV RTVB", "ATV/r");
		
		StringTokenizer drugToken = new StringTokenizer(drugs, " ");
		while ( drugToken.hasMoreTokens() ) {
			String drug = drugToken.nextToken();
			//check with avd and kvl
			if( drug.equals("LPV") ) {
				drug = "LPV/r";
			}
			DrugGeneric selectedDg = null;
			for( DrugGeneric dg : regaDrugGenerics ) {
				if( drug.equals( dg.getGenericId() ) ) {
					selectedDg = dg;
					break;
				}
			}
			if ( selectedDg != null ) {
				TherapyGeneric tg = new TherapyGeneric(new TherapyGenericId(t, selectedDg), false, false);
				t.getTherapyGenerics().add(tg);
			} else {
				System.err.println(drugs);
				ConsoleLogger.getInstance().logError("No valid generic drug: " + drug);
			}
		}
	}
	
	public static String processFasta(Patient p, int row) {
		String fasta = null;
		if ( ( getGenotypeRT(row) != null && !getGenotypeRT(row).equals("") ) || ( getGenotypePR(row) != null && !getGenotypePR(row).equals("") ) ) {
			DateFormat fastaFormat = new SimpleDateFormat("yyyy-MM-dd");
			String localString = ">case_" + getQuestionN(row) + "_date_" + fastaFormat.format(getDate(row));
			String fastaFile = basePath_ + File.separatorChar + "previousGT.fasta";
			
			try {
				BufferedReader br = new BufferedReader( new FileReader(fastaFile) );
				while ( br.ready() && fasta == null ) {
					String key = br.readLine().trim();
					if ( key.indexOf(">") == 0 ) {
						String line = br.readLine();
						if ( key.equals(localString) ) {
							fasta = line;
						}
					}
				}
			} catch ( FileNotFoundException fnfe ) {
				System.err.println("File not found: " + fastaFile);
				System.exit(1);
			} catch ( IOException ioe ) {
				System.err.println(ioe.getMessage());
				System.exit(1);
			}
			
			if ( fasta == null ) {
				System.err.println("Fasta not found for " + localString);
			} else {
				fasta = Utils.clearNucleotides(fasta);
				NtSequence nts = new NtSequence();
				nts.setNucleotides(fasta);
				
				ViralIsolate vi = new ViralIsolate();
				vi.setSampleDate(getDate(row));
				vi.getNtSequences().add(nts);
				vi.setSampleId(new Integer(++sampleID).toString());
				p.getViralIsolates().add(vi);
				
				viralisolates.put(sampleID + "", vi);
			}
		}
		return fasta;
	}
	
	private static void processCD4(Patient p, int row) {
		processTestResult(p, getDate(row), StandardObjects.getGenericCD4Test(), getCD4(row));
	}
	private static  void processViralLoad(Patient p, int row) {
		processTestResult(p, getDate(row), StandardObjects.getGenericViralLoadTest(), getRNA(row));
	}
	private static  void processTestResult(Patient p, Date d, Test t, String result) {
		if ( result != null && !result.equals("") ) {
			TestResult tr = p.createTestResult(t);
			tr.setTestDate(d);
			tr.setTest(t);
			tr.setValue(result);
		}
	}
	
	private static boolean readyNextTherapy(Sheet s) {
		rowChunk = new ArrayList<Cell[]>();
		
		if ( currentRow >= s.getRows() ) {
			return false;
		} else {
			Cell[] row = s.getRow(currentRow);
			String question = row[0].getContents();
			rowChunk.add(row);
			
			try {
				while ( s.getRow(++currentRow)[0].getContents().equals(question) ) {
					rowChunk.add( s.getRow(currentRow) );
				}
			} catch ( ArrayIndexOutOfBoundsException aioobe ) {}
			
			if ( rowChunk.size() > 0 )
				return true;
			else
				return false;
		}
	}
	
	private void readHeader(Sheet s) {
		headers = new HashMap<String, Integer>();
		Cell[] row = s.getRow(0);
		for(int i=0; i<row.length; i++) {
			Cell c = row[i];
			headers.put(c.getContents(), i);
		}
	}
	
	private static String getQuestionN(int row) { return getCell(row, "QuestionN"); }
	
	private static String getTherapy(int row) { return getCell(row, "Therapy"); }
	
	private static Date getDate(int row) {
		DateFormat xlsFormat = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
		try {
			return (Date)xlsFormat.parse(getCell(row, "Date"));
		} catch ( ParseException pe ) {
			System.err.println(pe.getMessage());
			System.exit(1);
			return null;
		}
	}
	
	private static String getCD4(int row) { return getCell(row, "CD4"); }
	private static String getRNA(int row) { return getCell(row, "RNA"); }
	private static String getGenotypeRT(int row) { return getCell(row, "Genotype RT"); }
	private static String getGenotypePR(int row) { return getCell(row, "Genotype PR"); }
	
	private static String getCell(int rowNr, String headerName) {
		Cell[] row = rowChunk.get(rowNr);
		int col = headers.get(headerName);
		return ( col < row.length ) ? row[col].getContents() : "";
	}
}
