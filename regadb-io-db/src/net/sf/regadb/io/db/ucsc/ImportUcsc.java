package net.sf.regadb.io.db.ucsc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.NominalEvent;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.db.util.db2csv.AccessConnectionProvider;
import net.sf.regadb.io.db.util.db2csv.DBToCsv;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.frequency.Frequency;

public class ImportUcsc 
{
	//DB table names
	private String patientTableName = "t pazienti";
	private String analysisTableName = "t_analisi";
	private String hivTherapyTableName = "t terapie anti hiv";
	private String sequencesTableName = "t genotipo hiv";
	private String adeTableName = "t eventi aids";
	
	//DB tables
	private Table patientTable;
	private Table analysisTable;
	private Table hivTherapyTable;
	private Table sequencesTable;
	private Table adeTable;
	
	//Translation mapping tables
	private Table countryTable;
	private Table birthPlaceTable;
	private Table riskGroupTable;
	private Table stopTherapieDescTable;
	private Table adeMappingTable;
	
	private HashMap<String, String> tableSelections = new HashMap<String, String>();
	
	private HashMap<String, String> stopTherapyTranslation;
	
	private HashMap<String, Patient> patientMap = new HashMap<String, Patient>();
	private HashMap<String, ViralIsolate> viralIsolateHM = new HashMap<String, ViralIsolate>();
	
	private List<DrugGeneric> regaDrugGenerics;
    
	private List<Attribute> regadbAttributes;
	private List<Event> regadbEvents;
	
	private Mappings mappings;
	
    private AttributeGroup regadb = new AttributeGroup("RegaDB");
    private AttributeGroup virolab = new AttributeGroup("Virolab");
    
    private TestNominalValue posSeroStatus;
	
    public static void main(String [] args) 
    {
    	try
    	{
    		 if(args.length != 3) 
    		 {
    			 System.err.println("Usage: ImportUcsc workingDirectory database.mdb mappingBasePath");
    	         System.exit(0);
    	    }
    		
    		ImportUcsc imp = new  ImportUcsc();
        
    		imp.getData(new File(args[0]), args[1], args[2]);
    	}
    	catch(Exception e)
    	{
    		ConsoleLogger.getInstance().logError("Unknown error: "+e.getMessage());
    	}
    }
    
    public void getData(File workingDirectory, String databaseFile, String mappingBasePath)
    {
    	//Just for testing purposes...otherwise remove
		ConsoleLogger.getInstance().setInfoEnabled(true);
    	
    	try
    	{
    		mappings = Mappings.getInstance(mappingBasePath);
    		
    		ConsoleLogger.getInstance().logInfo("Creating CSV files...");
    		tableSelections.put(patientTableName, "SELECT * FROM `"+patientTableName+"`");
    		tableSelections.put(analysisTableName, "SELECT * FROM `"+analysisTableName+"` WHERE " +
    				"t_analisi.[desc_risultato] = 'HIV-RNA' OR t_analisi.[desc_risultato] = 'cutoff' OR " +
    				"t_analisi.[desc_risultato] LIKE 'CD8%' OR t_analisi.[desc_risultato] LIKE 'CD4%' OR " +
    				"t_analisi.[desc_risultato] LIKE 'CD3%' OR t_analisi.[desc_risultato] LIKE 'Toxo%' OR " +
    				"t_analisi.[desc_risultato] LIKE 'HAV%' OR t_analisi.[desc_risultato] LIKE 'HB%' OR " +
    				"t_analisi.[desc_laboratorio] = 'FARMACOLOGIA' "+
    				"ORDER BY t_analisi.[cartella_ucsc], t_analisi.[data_analisi]");
    		tableSelections.put(hivTherapyTableName, "SELECT * FROM `"+hivTherapyTableName+"`");
    		tableSelections.put(sequencesTableName, "SELECT * FROM `"+sequencesTableName+"`");
    		tableSelections.put(adeTableName, "SELECT * FROM `"+adeTableName+"`");
    		
    		DBToCsv a2c = new DBToCsv(new AccessConnectionProvider(new File(databaseFile)));
            a2c.createCsv(workingDirectory, tableSelections);
    		
    		ConsoleLogger.getInstance().logInfo("Reading CSV files...");
    		//Filling DB tables
    		patientTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + patientTableName + ".csv");
    		analysisTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + analysisTableName + ".csv");
    		hivTherapyTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + hivTherapyTableName + ".csv");
    		sequencesTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + sequencesTableName + ".csv");
    		adeTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + adeTableName + ".csv");
    		
    		ConsoleLogger.getInstance().logInfo("Initializing mapping tables...");
    		//Filling translation mapping tables
    		countryTable = Utils.readTable(mappingBasePath + File.separatorChar + "nationality.mapping");
    		birthPlaceTable = Utils.readTable(mappingBasePath + File.separatorChar + "birthplace.mapping");
    		riskGroupTable = Utils.readTable(mappingBasePath + File.separatorChar + "riskgroup.mapping");
    		stopTherapieDescTable = Utils.readTable(mappingBasePath + File.separatorChar + "stop_therapy_reason.mapping");
    		adeMappingTable = Utils.readTable(mappingBasePath + File.separatorChar + "aids_defining_illness.mapping");
    		
    		ConsoleLogger.getInstance().logInfo("Retrieving all necessary translations...");
    		stopTherapyTranslation = Utils.translationFileToHashMap(stopTherapieDescTable);
    		
    		ConsoleLogger.getInstance().logInfo("Retrieving attributes, drugs, and events...");
    		regadbAttributes = Utils.prepareRegaDBAttributes();
    		regaDrugGenerics = Utils.prepareRegaDrugGenerics();
    		regadbEvents = Utils.prepareRegaDBEvents();
    		
    		posSeroStatus = Utils.getNominalValue(StandardObjects.getHiv1SeroStatusTestType(), "Positive");
    		
    		ConsoleLogger.getInstance().logInfo("Migrating patient information...");
    		handlePatientData();
    		ConsoleLogger.getInstance().logInfo("Successful");
    		ConsoleLogger.getInstance().logInfo("Migrating CD data...");
    		handleCDData();
    		ConsoleLogger.getInstance().logInfo("Successful");
    		ConsoleLogger.getInstance().logInfo("Migrating ADE data...");
    		handleEvent();
    		ConsoleLogger.getInstance().logInfo("Successful");
    		ConsoleLogger.getInstance().logInfo("Migrating treatments...");
    		handleTherapies();
    		ConsoleLogger.getInstance().logInfo("Successful");
    		ConsoleLogger.getInstance().logInfo("Processing sequences...");
    		handleSequences();
    		ConsoleLogger.getInstance().logInfo("Processed "+patientMap.size()+" patient(s).");
    		ConsoleLogger.getInstance().logInfo("Successful");
    		
    		ConsoleLogger.getInstance().logInfo("Generating output xml file...");
    		IOUtils.exportPatientsXML(patientMap, workingDirectory.getAbsolutePath() + File.separatorChar + "ucsc_patients.xml", ConsoleLogger.getInstance());
    		IOUtils.exportNTXMLFromPatients(patientMap, workingDirectory.getAbsolutePath() + File.separatorChar + "ucsc_viralIsolates.xml", ConsoleLogger.getInstance());
    		ConsoleLogger.getInstance().logInfo("Export finished.");
    	}
    	catch(Exception e)
    	{
    		ConsoleLogger.getInstance().logError("Unknown error: "+e.getMessage());
    	}
    }
    
    private void handlePatientData()
    {
    	int CpatientID = Utils.findColumn(this.patientTable, "cartella UCSC");
    	int Csex = Utils.findColumn(this.patientTable, "sesso");
    	int CbirthDate = Utils.findColumn(this.patientTable, "data di nascita");
    	int CbirthPlace = Utils.findColumn(this.patientTable, "luogo di nascita");
    	int Cnationality = Utils.findColumn(this.patientTable, "nazionalita");
    	int CriskGroup = Utils.findColumn(this.patientTable, "fattore di rischio");
    	int CseroConverter = Utils.findColumn(this.patientTable, "seroconverter");
    	int CfirstTest = Utils.findColumn(this.patientTable, "data primo test HIV positivo");
    	int ClastTest = Utils.findColumn(this.patientTable, "data ultimo test HIV negativo");
    	int CdeathDate = Utils.findColumn(this.patientTable, "data decesso");
    	int CdeathReason = Utils.findColumn(this.patientTable, "descrizione decesso");
    	int Csyndrome = Utils.findColumn(this.patientTable, "sindrome acuta"); 
    	int CsyndromeDate = Utils.findColumn(this.patientTable, "data sindrome acuta");
    	int Ccdc = Utils.findColumn(this.patientTable, "CDC");
        
        NominalAttribute gender = new NominalAttribute("Gender", Csex, new String[] { "M", "F" },
                new String[] { "male", "female" } );
        gender.attribute.setAttributeGroup(regadb);
        
        NominalAttribute cdcA = new NominalAttribute("CDC", Ccdc, new String[] { "A", "B", "C" },
                new String[] { "A", "B", "C" } );
        cdcA.attribute.setAttributeGroup(virolab);
        
        NominalAttribute scA = new NominalAttribute("Sero Converter", CseroConverter, new String[] { "1", "0" },
                new String[] { "1", "0" } );
        scA.attribute.setAttributeGroup(virolab);
        
        TestType acuteSyndromTestType = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), "Acute Syndrome", new TreeSet<TestNominalValue>());
    	Test acuteSyndromTest = new Test(acuteSyndromTestType, "Acute Syndrome");
	
    	NominalAttribute countryOfOriginA = new NominalAttribute("Country of origin", countryTable, regadb, Utils.selectAttribute("Country of origin", regadbAttributes));
    	NominalAttribute birthplaceA = new NominalAttribute("Birthplace", birthPlaceTable, virolab, null);
    	NominalAttribute transmissionGroupA = new NominalAttribute("Transmission group", riskGroupTable, regadb, Utils.selectAttribute("Transmission group", regadbAttributes));    	
    	
    	for(int i = 1; i < this.patientTable.numRows(); i++)
    	{
            String patientId = this.patientTable.valueAt(CpatientID, i);
            String sex = this.patientTable.valueAt(Csex, i);
            String birthDate = this.patientTable.valueAt(CbirthDate, i);
            String birthPlace = this.patientTable.valueAt(CbirthPlace, i);
            String nationality = this.patientTable.valueAt(Cnationality, i);
            String riskGroup = this.patientTable.valueAt(CriskGroup, i);
            String seroConverter = this.patientTable.valueAt(CseroConverter, i);
            String firstTest = this.patientTable.valueAt(CfirstTest, i);
            String lastTest = this.patientTable.valueAt(ClastTest, i);
            String deathDate = this.patientTable.valueAt(CdeathDate, i);
            String deathReason = this.patientTable.valueAt(CdeathReason, i);
            String syndrome = this.patientTable.valueAt(Csyndrome, i);
            String syndromeDate = this.patientTable.valueAt(CsyndromeDate, i);
            String cdc = this.patientTable.valueAt(Ccdc, i);
            
            if(!"".equals(patientId))
            {
            	patientId = patientId.toUpperCase();
            	
            	Patient p = new Patient();
            	p.setPatientId(patientId);
            	
            	if(Utils.checkColumnValueForEmptiness("gender", sex, i, patientId))
            	{
                    AttributeNominalValue vv = gender.nominalValueMap.get(sex.toUpperCase().trim());
                    
                    if (vv != null) 
                    {
                        PatientAttributeValue v = p.createPatientAttributeValue(gender.attribute);
                        v.setAttributeNominalValue(vv);
                    }
                    else 
                    {
                        ConsoleLogger.getInstance().logWarning("Unsupported attribute value (gender): "+sex);
                    }
            	}
            	
            	if(Utils.checkColumnValueForEmptiness("date of Birth", birthDate, i, patientId))
            	{
            		p.setBirthDate(Utils.parseEnglishAccessDate(birthDate));
            	}
            	
            	if(Utils.checkColumnValueForExistance("birthplace", birthPlace, i, patientId))
            	{
                    Utils.handlePatientAttributeValue(birthplaceA, birthPlace, p);
            	}
            	
            	if(Utils.checkColumnValueForExistance("nationality", nationality, i, patientId))
            	{
                    Utils.handlePatientAttributeValue(countryOfOriginA, nationality, p);
            	}
            	
            	if(Utils.checkColumnValueForEmptiness("risk group", riskGroup, i, patientId))
            	{
                    Utils.handlePatientAttributeValue(transmissionGroupA, riskGroup, p);
            	}
            	
            	if(Utils.checkColumnValueForEmptiness("sero converter", seroConverter, i, patientId))
            	{
            		//Due to a bug in their frontend. 
            		if(seroConverter.equals("-1"))
            			seroConverter = "1";
            		
                    Utils.handlePatientAttributeValue(scA, seroConverter, p);
            	}
            	
            	if(Utils.checkDate("date of first positive HIV test", firstTest, i, patientId))
            	{
            		TestResult t = p.createTestResult(StandardObjects.getGenericHiv1SeroStatusTest());
                    t.setTestNominalValue(posSeroStatus);
                    t.setTestDate(Utils.parseEnglishAccessDate(firstTest));
            	}
            	
            	if(Utils.checkDate("date of last negative HIV test", lastTest, i, patientId))
            	{
            		TestResult t = p.createTestResult(StandardObjects.getContactTest());
                    t.setValue("Contact");
                    t.setTestDate(Utils.parseEnglishAccessDate(lastTest));
            	}
            	
            	if(Utils.checkColumnValueForExistance("date of death", deathDate, i, patientId))
            	{
            		p.setDeathDate(Utils.parseEnglishAccessDate(deathDate));
            	}
            	
            	if(Utils.checkColumnValueForExistance("death reason", deathReason, i, patientId))
            	{
            		//Ask if necessary, to be translated first (Wait for Mattia)
            	}
            	
            	if(Utils.checkColumnValueForSyndrome("acute syndrom", syndrome, i, patientId) && 
            	   Utils.checkDate("date of acute syndrom", syndromeDate, i, patientId))
            	{
            		TestResult t = p.createTestResult(acuteSyndromTest);
                    t.setValue(syndrome);
                    t.setTestDate(Utils.parseEnglishAccessDate(syndromeDate));
            	}
            	
            	if(Utils.checkColumnValueForExistance("CDC value", cdc, i, patientId))
            	{
                    AttributeNominalValue vv = cdcA.nominalValueMap.get(cdc.toUpperCase().trim());
                    
                    if(vv != null) 
                    {
                        PatientAttributeValue v = p.createPatientAttributeValue(cdcA.attribute);
                        v.setAttributeNominalValue(vv);
                    } 
                    else 
                    {
                    	if(!"".equals(cdc))
                    		ConsoleLogger.getInstance().logWarning("Unsupported attribute value (CDC): "+cdc);
                    }
            	}
            	
            	patientMap.put(patientId, p);
            }
            else
            {
           	 	ConsoleLogger.getInstance().logWarning("No patientID in row "+i+" present...Skipping data set.");
            }
    	}
    }
    
    private void handleCDData()
    {
    	int CCC4PatientID = Utils.findColumn(this.analysisTable, "cartella_ucsc");
    	int CAnalysisDate = Utils.findColumn(this.analysisTable, "data_analisi");
    	int CMethod = Utils.findColumn(this.analysisTable, "desc_risultato");
    	int CLabor = Utils.findColumn(this.analysisTable, "desc_laboratorio");
    	int CResult= Utils.findColumn(this.analysisTable, "risul_num");
    	
    	for(int i = 1; i < this.analysisTable.numRows(); i++)
    	{
    		String patientID = this.analysisTable.valueAt(CCC4PatientID, i);
    		String analysisDate = this.analysisTable.valueAt(CAnalysisDate, i);
    		String method = this.analysisTable.valueAt(CMethod, i);
    		String labor = this.analysisTable.valueAt(CLabor, i);
    		String result = this.analysisTable.valueAt(CResult, i);
    		
    		patientID = patientID.toUpperCase();
    		
    		Patient p = patientMap.get(patientID);
    		
    		if(p == null)
    		{
    			ConsoleLogger.getInstance().logWarning("No patient with id "+patientID+" found.");
    		}
    		
    		if(p != null)
    		{
    			//CD3
        		if(method.equals("CD3 (?L)"))
        		{
    	    		if (Utils.checkColumnValueForEmptiness("CD3 test result (�L)", result, i, patientID) && Utils.checkCDValue(result, i, patientID)) 
    	    		{
    	                TestResult t = p.createTestResult(StandardObjects.getGenericCD3Test());
    	                t.setValue(result.replace(',', '.'));
    	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
    	    		}
        		}
        		if(method.equals("CD3 (%)"))
        		{
    	    		if (Utils.checkColumnValueForEmptiness("CD3 test result (%)", result, i, patientID) && Utils.checkCDValue(result, i, patientID)) 
    	    		{
    	                TestResult t = p.createTestResult(StandardObjects.getGenericCD3PercentTest());
    	                t.setValue(result.replace(',', '.'));
    	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
    	    		}
        		}
        			
        		//CD4
        		if(method.equals("CD4 (?L)"))
        		{
    	    		if (Utils.checkColumnValueForEmptiness("CD4 test result (�L)", result, i, patientID) && Utils.checkCDValue(result, i, patientID)) 
    	    		{
    	                TestResult t = p.createTestResult(StandardObjects.getGenericCD4Test());
    	                t.setValue(result.replace(',', '.'));
    	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
    	    		}
        		}
        		if(method.equals("CD4 (%)"))
        		{
    	    		if (Utils.checkColumnValueForEmptiness("CD4 test result (%)", result, i, patientID) && Utils.checkCDValue(result, i, patientID)) 
    	    		{
    	                TestResult t = p.createTestResult(StandardObjects.getGenericCD4PercentageTest());
    	                t.setValue(result.replace(',', '.'));
    	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
    	    		}
        		}
        		 
        		//CD8
        		if(method.equals("CD8 (?L)"))
        		{
    	    		if (Utils.checkColumnValueForEmptiness("CD8 test result (�L)", result, i, patientID) && Utils.checkCDValue(result, i, patientID)) 
    	    		{
    	                TestResult t = p.createTestResult(StandardObjects.getGenericCD8Test());
    	                t.setValue(result.replace(',', '.'));
    	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
    	    		}
        		}
        		if(method.equals("CD8 (%)"))
        		{
    	    		if (Utils.checkColumnValueForEmptiness("CD8 test result (%)", result, i, patientID) && Utils.checkCDValue(result, i, patientID)) 
    	    		{
    	                TestResult t = p.createTestResult(StandardObjects.getGenericCD8PercentageTest());
    	                t.setValue(result.replace(',', '.'));
    	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
    	    		}
        		}
        		 
        		//Viral Load
        		if(method.equals("HIV-RNA"))
        		{
    	    		if(Utils.checkColumnValueForEmptiness("HIV RNA test result", result, i, patientID) && Utils.checkColumnValueForEmptiness("date ofHIV RNA test result", analysisDate, i, patientID))
    	    		{
    	    			 try
    	    			 {
    			    		 TestResult t = p.createTestResult(StandardObjects.getGenericHiv1ViralLoadTest());

    			    		 String value = null;
    			    		 
    			    		 double limit = 50.0;
    			    		 
    			    		 if(Double.parseDouble(result) <= limit)
    			    			 value = "<"+ limit;
    			    		 else
    			    		 {
    			    			 value = "="+ result;
    			    		 }
    			    		 
    			    		 t.setValue(value.replace(',', '.'));
    			    		 t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
    	    			 }
    	    			 catch(Exception e)
    	    			 {
    	    				 
    	    			 }
    	    		}
        		}
        		
        		//Toxo
        		if(method.equals("Toxo IgG"))
        		{
    	    		if (Utils.checkColumnValueForEmptiness("Toxo IgG", result, i, patientID)) 
    	    		{
    	                TestResult t = p.createTestResult(StandardObjects.getGenericToxoIgGTest());
    	                t.setValue(result.replace(',', '.'));
    	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
    	    		}
        		}
        		if(method.equals("Toxo IgM"))
        		{
    	    		if (Utils.checkColumnValueForEmptiness("Toxo IgM", result, i, patientID)) 
    	    		{
    	                TestResult t = p.createTestResult(StandardObjects.getGenericToxoIgMTest());
    	                t.setValue(result.replace(',', '.'));
    	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
    	    		}
        		}
        		
        		//HAV
        		if(method.equals("HAV IgG"))
        		{
    	    		if (Utils.checkColumnValueForEmptiness("HAV IgG", result, i, patientID)) 
    	    		{
    	                TestResult t = p.createTestResult(StandardObjects.getGenericHAVIgGTest());
    	                t.setValue(result.replace(',', '.'));
    	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
    	    		}
        		}
        		if(method.equals("HAV IgM"))
        		{
    	    		if (Utils.checkColumnValueForEmptiness("HAV IgM", result, i, patientID)) 
    	    		{
    	                TestResult t = p.createTestResult(StandardObjects.getGenericHAVIgMTest());
    	                t.setValue(result.replace(',', '.'));
    	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
    	    		}
        		}
        		
        		//HAV
        		if(method.equals("HBeAg"))
        		{
    	    		if (Utils.checkColumnValueForEmptiness("HBeAg", result, i, patientID)) 
    	    		{
    	                TestResult t = p.createTestResult(StandardObjects.getGenericHBeAgTest());
    	                t.setValue(result.replace(',', '.'));
    	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
    	    		}
        		}
        		if(method.equals("HBsAg"))
        		{
    	    		if (Utils.checkColumnValueForEmptiness("HBsAg", result, i, patientID)) 
    	    		{
    	                TestResult t = p.createTestResult(StandardObjects.getGenericHBsAgTest());
    	                t.setValue(result.replace(',', '.'));
    	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
    	    		}
        		}
        		
        		//Drug tests not used yet
        		/*if(labor.equals("FARMACOLOGIA"))
        		{
        			for(int j = 0; j < regaDrugGenerics.size(); j++)
        			{
        				String name = regaDrugGenerics.get(j).getGenericName();
        				
        				if(name.equals(method.toLowerCase()))
        				{	
        					if (Utils.checkColumnValueForEmptiness("Drug testing for "+name+"", result, i, patientID)) 
        		    		{
        						//TODO: Wait for Pieter to fix it within the repository
        		                TestResult t = p.createTestResult(StandardObjects.getGenericDrugTesting());
        		                t.setValue(result.replace(',', '.'));
        		                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
        		    		}
        					
        					break;
        				}
        			}
        		}*/
        	}	
    	}		
    }
    
    private void handleEvent()
    {
        int CPatientId = Utils.findColumn(adeTable, "cartella UCSC");
        int CStartDate = Utils.findColumn(adeTable, "data evento AIDS");
        int CAde = Utils.findColumn(adeTable, "descrizione evento AIDS");
        
        NominalEvent aidsDefiningIllnessA = new NominalEvent("Aids defining illness", adeMappingTable, Utils.selectEvent("Aids defining illness", regadbEvents));
        
        for(int i = 1; i < adeTable.numRows(); i++) 
        {
            String patientId = adeTable.valueAt(CPatientId, i);
            String startDate = adeTable.valueAt(CStartDate, i);
            String ade = adeTable.valueAt(CAde, i);
            
            patientId = patientId.toUpperCase();
            
            Patient p = patientMap.get(patientId);
    		
    		if(p == null)
    		{
    			ConsoleLogger.getInstance().logWarning("No ade patient with id "+patientId+" found.");
    		}
    		else
    		{
    			//No end dates in table available
    			Date endDate = null;
    			
    			if(Utils.checkColumnValueForEmptiness("date of ade", startDate, i, patientId))
    			{
    				if(Utils.checkColumnValueForExistance("ade", ade, i, patientId))
    				{
    					Utils.handlePatientEventValue(aidsDefiningIllnessA, ade, Utils.parseEnglishAccessDate(startDate), endDate, p);
    				}
    			}
                else
                {
                	ConsoleLogger.getInstance().logWarning("Invalid start date specified ("+ i +").");
                }
            }
        }
    }
    
    private void handleTherapies()
    {
    	int ChivPatientID = Utils.findColumn(this.hivTherapyTable, "cartella UCSC");
    	int ChivStartTherapy = Utils.findColumn(this.hivTherapyTable, "data start terapia anti HIV");
    	int ChivStopTherapy = Utils.findColumn(this.hivTherapyTable, "data stop terapia anti HIV");
    	//int ChivLineTherapy = Utils.findColumn(this.hivTherapyTable, "linea terapia anti HIV");
    	//int ChivSuccessTherapy = Utils.findColumn(this.hivTherapyTable, "terapia anti HIV conclusa");
    	int ChivStopReasonTherapy = Utils.findColumn(this.hivTherapyTable, "motivo stop terapia anti HIV");
    	int ChivCommercialDrug = Utils.findColumn(this.hivTherapyTable, "terapia ARV");
    	
        for(int i = ChivCommercialDrug+1; i < this.hivTherapyTable.numColumns(); i++) 
        {
            String drug = this.hivTherapyTable.valueAt(i, 0);
            
            Utils.checkDrugsWithRepos(drug, regaDrugGenerics, mappings);
        }
    	    	
    	for(int i = 1; i < this.hivTherapyTable.numRows(); i++)
    	{
    		String hivPatientID = this.hivTherapyTable.valueAt(ChivPatientID, i);
    		String hivStartTherapy = this.hivTherapyTable.valueAt(ChivStartTherapy, i);
    		String hivStopTherapy = this.hivTherapyTable.valueAt(ChivStopTherapy, i);
    		//String hivLineTherapy = this.hivTherapyTable.valueAt(ChivLineTherapy, i);
    		//String hivSuccessTherapy = this.hivTherapyTable.valueAt(ChivSuccessTherapy, i);
    		String hivStopReasonTherapy = this.hivTherapyTable.valueAt(ChivStopReasonTherapy, i);
            
        	if(!"".equals(hivPatientID))
            {
        		hivPatientID = hivPatientID.toUpperCase();
        		
        		HashMap<String,String> drugs = new HashMap<String,String>();
        		Date startDate = null;
        		Date stopDate = null;
        		
        		if(Utils.checkColumnValueForEmptiness("start date of therapy", hivStartTherapy, i, hivPatientID))
        		{
        			startDate = Utils.parseEnglishAccessDate(hivStartTherapy);
        		}
        		
        		for(int j = ChivCommercialDrug+1; j < this.hivTherapyTable.numColumns()-1; j++) 
                {
                    String drugValue = this.hivTherapyTable.valueAt(j, i);
                    
            		if(Utils.checkColumnValueForEmptiness("unknown drug value", drugValue, i, hivPatientID) && Utils.checkDrugValue(drugValue, i, hivPatientID))
            		{
            			String drugName = this.hivTherapyTable.getColumn(j).get(0);
            			
            			drugs.put(drugName.toUpperCase(), drugValue);
            		} 
                }
        		
        		ArrayList<DrugGeneric> genDrugs = evaluateDrugs(drugs);
        		
        		if(Utils.checkColumnValueForExistance("stop date of therapy", hivStopTherapy, i, hivPatientID))
        		{
        			stopDate = Utils.parseEnglishAccessDate(hivStopTherapy);
        		}
        		
        		//can be empty
        		if(Utils.checkColumnValueForExistance("motivation of stopping therapy", hivStopReasonTherapy, i, hivPatientID))
        		{
        			if(!stopTherapyTranslation.containsKey(hivStopReasonTherapy))
        			{
        				hivStopReasonTherapy = null;
        			
        				//ConsoleLogger.getInstance().logWarning("No applicable HIV motivation found.");
        			}
        			else
        				hivStopReasonTherapy = stopTherapyTranslation.get(hivStopReasonTherapy);
        		}
        		
        		if(hivPatientID != null)
        		{
        			storeTherapy(hivPatientID, startDate, stopDate, genDrugs, hivStopReasonTherapy);
        		}
            }
        	else
        	{
        		ConsoleLogger.getInstance().logWarning("No patient with id "+hivPatientID+" found.");
        	}
    	}
    }
    
    private ArrayList<DrugGeneric> evaluateDrugs(HashMap<String,String> drugs)
    {
    	ArrayList<DrugGeneric> gDrugs = new ArrayList<DrugGeneric>();
    	
    	for(String drug : drugs.keySet())
    	{
    		if(!"".equals(drug))
    		{
    			getDrugMapping(gDrugs, drug, drugs.get(drug));
    		}
    	}
    	
    	return gDrugs;
    }
    
    private void getDrugMapping(ArrayList<DrugGeneric> gDrugs, String drug, String value)
    {
    	//ConsoleLogger.getInstance().logInfo("Found drug "+drug+" with value "+value);
    	
    	boolean foundDrug = false;
    	DrugGeneric genDrug = null;
    	
        for(int j = 0; j < regaDrugGenerics.size(); j++)
    	{
        	genDrug = regaDrugGenerics.get(j);
        	
        	if(genDrug.getGenericId().equals(drug.toUpperCase()))
        	{
        		foundDrug = true;
        		
        		gDrugs.add(genDrug);
        		
        		break;
        	}
    	}
        
        if(!foundDrug)
        {
        	String mapping = mappings.getMapping("generic_drugs.mapping", drug);
        		
            if(mapping != null) 
            {
            	for(int i = 0; i < regaDrugGenerics.size(); i++)
             	{
            		genDrug = regaDrugGenerics.get(i);
            		
            		if(genDrug.getGenericId().toUpperCase().equals(mapping))
                	{
            			gDrugs.add(genDrug);
                	}
             	}
            }
        }
    }
    
    private ArrayList<DrugGeneric> validateDrugs(ArrayList<DrugGeneric> foundDrugs, String patientID)
    {
    	ArrayList<DrugGeneric> vDrugs = new ArrayList<DrugGeneric>();
    	
    	for(int i = 0; i < foundDrugs.size(); i++)
    	{
    		DrugGeneric gDrug = (DrugGeneric)foundDrugs.get(i);
    		
    		List<DrugGeneric> subList = foundDrugs.subList(i+1, foundDrugs.size());
    		
    		if(subList.contains(gDrug))
    		{
    			ConsoleLogger.getInstance().logWarning("Found double drug entry "+(String)foundDrugs.get(i).getGenericId()+" for patient "+patientID+"");
    		}
    		else
    		{
    			vDrugs.add(gDrug);
    		}
    	}
    	
    	return vDrugs;
    }
    
    @SuppressWarnings("deprecation")
	private void storeTherapy(String patientId, Date startDate, Date endDate, ArrayList<DrugGeneric> foundDrugs, String motivation) 
    {
    	Patient p = patientMap.get(patientId);

    	if (p == null)
    	{
    		ConsoleLogger.getInstance().logWarning("No patient with id "+patientId+" found.");
    		
    		return;
    	}
    	
    	if(foundDrugs == null)
    	{
    		ConsoleLogger.getInstance().logWarning("Something wrong with therapy mapping for patient '" + patientId + "': No valid drugs found...Storing anyway!");
    	}

    	if(startDate != null && endDate != null)
    	{
    		if(startDate.equals(endDate))
    		{
    			ConsoleLogger.getInstance().logWarning("Something wrong with treatment dates for patient '" + patientId + "': Therapy start " + startDate.toLocaleString() + " - Therapy end " + endDate.toLocaleString() + ": Dates are equal.");
    		        
    			//Do not store here...
    		    return;
    		}
    		
	    	if(startDate.after(endDate))
	    	{
	    		ConsoleLogger.getInstance().logWarning("Something wrong with treatment dates for patient '" + patientId + "': Therapy start " + startDate.toLocaleString() + " - Therapy end " + endDate.toLocaleString() + ": End date is in the past.");
	    			
	    		//Do not store here...
	    		return;
	    	}
    	}
    	else if(startDate == null)
    	{
    		ConsoleLogger.getInstance().logError(patientId, "No corresponding start date available.");
    	}

    	Therapy t = p.createTherapy(startDate);
    	t.setStopDate(endDate);
    	
    	String drugs = ""; 
    	
    	if(foundDrugs != null)
    	{
    		ArrayList<DrugGeneric> medicinsList = validateDrugs(foundDrugs, p.getPatientId());
    		
	    	for (int i = 0; i < medicinsList.size(); i++) 
	    	{
	    		TherapyGeneric tg = new TherapyGeneric(new TherapyGenericId(t, (DrugGeneric)medicinsList.get(i)), 
			                                1.0, 
			                                false,
			                                false, 
			                                (long)Frequency.DAYS.getSeconds());
				    		
				t.getTherapyGenerics().add(tg);
							
				drugs += (String)medicinsList.get(i).getGenericId() + " ";
	    	}
	    	
	    	//ConsoleLogger.getInstance().logInfo(""+p.getPatientId()+" "+startDate.toLocaleString()+" "+drugs);
    	}
    	
    	if(motivation != null && !motivation.equals(""))
    	{
    	    //Still needs improvement, requires the mapping of motivation
    		TherapyMotivation therapyMotivation = null;
    		
    		if(motivation.equals("toxicity"))
    			therapyMotivation = new TherapyMotivation("Toxicity");
    		else if(motivation.equals("unknown"))
    			therapyMotivation = new TherapyMotivation("Unknown");
    		else if(motivation.equals("patient's choice"))
    			therapyMotivation = new TherapyMotivation("Patient's choice");
    		else
    			therapyMotivation = new TherapyMotivation("Other");
    	
    		if(therapyMotivation != null)
    			t.setTherapyMotivation(therapyMotivation);
    	}
    }
    
    private void addViralIsolateToPatients(int counter, String patientID, Date date, String seq)
    {
    	Patient p = patientMap.get(patientID);
    	
    	if(p == null)
    	{
    		ConsoleLogger.getInstance().logError("No patient with id "+patientID+" found.");
    	}
    	
    	ViralIsolate vi = p.createViralIsolate();
    	vi.setSampleId(counter+"");
    	vi.setSampleDate(date);
    	
    	NtSequence ntseq = new NtSequence();
    	ntseq.setLabel("Sequence1");
    	ntseq.setSequenceDate(date);
    	ntseq.setNucleotides(seq);
    	
    	vi.getNtSequences().add(ntseq);
    	
    	viralIsolateHM.put(vi.getSampleId(), vi);
    }
    
    
    private void handleSequences() throws IOException
    {
    	int counter = 0;
    	int emptyCounter = 0;
    	
    	int CpatientID = Utils.findColumn(this.sequencesTable, "cartella UCSC");
    	int CsequenceDate = Utils.findColumn(this.sequencesTable, "data genotipo");
    	int Csequence = Utils.findColumn(this.sequencesTable, "sequenza basi azotate (fasta)");
    	
    	for(int i = 1; i < this.sequencesTable.numRows(); i++)
    	{
    		String patientID = this.sequencesTable.valueAt(CpatientID, i);
    		String sequenceDate = this.sequencesTable.valueAt(CsequenceDate, i);
    		String sequence = this.sequencesTable.valueAt(Csequence, i);
    	
    		 if(!"".equals(patientID))
             {
    			 patientID = patientID.toUpperCase();
    			 
    			 if(Utils.checkColumnValueForEmptiness("date of sequence analysis", sequenceDate, i, patientID))
    			 {
             		Date date = Utils.parseEnglishAccessDate(sequenceDate);
             		
             		if(date != null)
             		{
             			if(Utils.checkSequence(sequence, i, patientID))
             			{
             				String clearedSequ = Utils.clearNucleotides(sequence);
             				
             				if(!"".equals(clearedSequ))
             				{
             					addViralIsolateToPatients(counter, patientID, date, clearedSequ);
             					
             					counter++;
             				}
             			}
             			else
             			{
             				ConsoleLogger.getInstance().logWarning("Empty seq for patient "+patientID+"");
                            
                            emptyCounter++;
             			}
             		}
             		else
                    {
                    	ConsoleLogger.getInstance().logWarning("No sequence date found for patient "+patientID+".");
                    }
             	}
    			else
                {
    				ConsoleLogger.getInstance().logWarning("No sequence date found for patient "+patientID+".");
                }
             }
    		 else
             {
            	 ConsoleLogger.getInstance().logWarning("No patientID in row "+i+" present...Skipping data set.");
             }
    	}
    	
    	ConsoleLogger.getInstance().logInfo(""+counter+" sequence(s) added");
        ConsoleLogger.getInstance().logInfo(""+emptyCounter+" blank sequence(s) found");
    }
}