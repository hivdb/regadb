package net.sf.regadb.io.db.ucsc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

import org.apache.commons.io.FileUtils;

public class ImportUcsc 
{
	private Table patientTable;
	private Table cd4Table;
	private Table hivTherapyTable;
	private Table countryTable;
	private Table birthPlaceTable;
	private Table riskGroupTable;
	private Table stopTherapieDescTable;
	private HashMap<String, Patient> patientMap = new HashMap<String, Patient>();
	
	private HashMap<String, String> countryTranslation;
	private HashMap<String, String> birthplaceTranslation;
	private HashMap<String, String> riskGroupTranslation;
	//private HashMap<String, String> stopTherapyTranslation;
	
	private List<DrugCommercial> dcs;
    
    private AttributeGroup regadb = new AttributeGroup("RegaDB");
    private AttributeGroup virolab = new AttributeGroup("ViroLab");
	
    public static void main(String [] args) 
    {
    	try
    	{
    		//Just for testing purposes...otherwise remove
    		ConsoleLogger.getInstance().setInfoEnabled(true);
    		
    		ImportUcsc imp = new  ImportUcsc();
        
    		imp.getData(new File(args[0]));
    	}
    	catch(Exception e)
    	{
    		ConsoleLogger.getInstance().logError("Unknown error: "+e.getMessage());
    	}
    }
    
    public void run(File workingDirectory) 
    {
        try 
        {
            handleSequences(workingDirectory);
        } 
        catch(IOException e) 
        {
        	ConsoleLogger.getInstance().logError("Cannot find/access directory "+workingDirectory);
        }
    }
    
    private void getData(File workingDirectory)
    {
    	try
    	{
    		ConsoleLogger.getInstance().logInfo("Reading input files...");
    		patientTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "dbo_T_pazienti.csv");
    		cd4Table = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "dbo_T analisi HIV RNA CD4_CD8.csv");
    		hivTherapyTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "dbo_T terapie anti HIV.csv");
    		riskGroupTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "dbo_T elenco fattori di rischio.csv");
    		stopTherapieDescTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "dbo_T elenco motivo stop terapia anti HIV.csv");
    		
    		ConsoleLogger.getInstance().logInfo("Retrieving Rega attributes and drugs...");
    		List<Attribute> regadbAttributesList = Utils.prepareRegaDBAttributes();
    		dcs = Utils.prepareRegaDBDrugs();
    		
    		ConsoleLogger.getInstance().logInfo("Retrieving all necessary translations...");
    		countryTranslation = getCountryTranslation();
    		birthplaceTranslation = getBirthPlaceTranslation();
    		riskGroupTranslation = getRiskGroupTranslation();
    		//stopTherapyTranslation = getStopTherapyTranslation();
    		
    		ConsoleLogger.getInstance().logInfo("Migrating patient information...");
    		handlePatientData(regadbAttributesList);
    		ConsoleLogger.getInstance().logInfo("Migrating CD data...");
    		handleCDData();
    		ConsoleLogger.getInstance().logInfo("Migrating treatments...");
    		handleTherapies();
    		ConsoleLogger.getInstance().logInfo("Processing sequences...");
    		handleSequences(workingDirectory);
    		ConsoleLogger.getInstance().logInfo("Generating output xml file...");
    		Utils.exportXML(patientMap, workingDirectory.getAbsolutePath() + File.separatorChar + "ucsc_patients.xml");
    	}
    	catch(Exception e)
    	{
    		ConsoleLogger.getInstance().logError("Unknown error: "+e.getMessage());
    	}
    }
    
    private void handlePatientData(List<Attribute> regadbAttributesList)
    {
    	int CpatientID = Utils.findColumn(this.patientTable, "cartella UCSC");
    	int Csex = Utils.findColumn(this.patientTable, "sesso");
    	int CbirthDate = Utils.findColumn(this.patientTable, "data di nascita");
    	int CbirthPlace = Utils.findColumn(this.patientTable, "luogo di nascita");
    	int Cnationality = Utils.findColumn(this.patientTable, "nazionalità");
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
        
        NominalAttribute scA = new NominalAttribute("Sero Converter", CseroConverter, new String[] { "-1", "0" },
                new String[] { "-1", "0" } );
        scA.attribute.setAttributeGroup(virolab);
        
        NominalAttribute rgA = new NominalAttribute("Transmission group", CriskGroup, (String[])riskGroupTranslation.keySet().toArray(),
        		(String[])riskGroupTranslation.values().toArray());
        rgA.attribute.setAttributeGroup(regadb);
        
        NominalAttribute couA = new NominalAttribute("Country of origin", Cnationality, (String[])countryTranslation.keySet().toArray(),
        		(String[])countryTranslation.values().toArray());
        couA.attribute.setAttributeGroup(regadb);
        
        NominalAttribute bpA = new NominalAttribute("Birthplace", CbirthPlace, (String[])birthplaceTranslation.keySet().toArray(),
        		(String[])birthplaceTranslation.values().toArray());
        bpA.attribute.setAttributeGroup(virolab);
        
        TestType acuteSyndromTestType = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), "Acute Syndrome", new TreeSet<TestNominalValue>());
    	Test acuteSyndromTest = new Test(acuteSyndromTestType, "Acute Syndrome");
    	
    	TestType hivTestType = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), "HIV Test", new TreeSet<TestNominalValue>());
    	Test hivTest = new Test(hivTestType, "HIV Test");
	
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
            	Patient p = new Patient();
            	p.setPatientId(patientId);
            	
            	if(Utils.checkColumnValue(sex, i, patientId))
            	{
                    AttributeNominalValue vv = gender.nominalValueMap.get(sex.toUpperCase().trim());
                    
                    if (vv != null) 
                    {
                        PatientAttributeValue v = p.createPatientAttributeValue(gender.attribute);
                        v.setAttributeNominalValue(vv);
                    }
                    else 
                    {
                        ConsoleLogger.getInstance().logWarning("Unsupported attribute value (CDC): "+cdc);
                    }
            	}
            	
            	if(Utils.checkColumnValue(birthDate, i, patientId))
            	{
            		p.setBirthDate(Utils.convertDate(birthDate));
            	}
            	
            	if(Utils.checkColumnValue(birthPlace, i, patientId))
            	{
            		AttributeNominalValue vv = bpA.nominalValueMap.get(birthPlace.trim());
                    
                    if (vv != null) 
                    {
                        PatientAttributeValue v = p.createPatientAttributeValue(bpA.attribute);
                        v.setAttributeNominalValue(vv);
                    }
                    else 
                    {
                        ConsoleLogger.getInstance().logWarning("Unsupported attribute value (Birthplace): "+birthPlace);
                    }
            	}
            	
            	if(Utils.checkColumnValue(nationality, i, patientId))
            	{
            		AttributeNominalValue vv = couA.nominalValueMap.get(nationality.trim());
                    
                    if (vv != null) 
                    {
                        PatientAttributeValue v = p.createPatientAttributeValue(couA.attribute);
                        v.setAttributeNominalValue(vv);
                    }
                    else 
                    {
                   	 	ConsoleLogger.getInstance().logError("Unsupported attribute value (Country of Origin): "+nationality);
                    }	
            	}
            	
            	if(Utils.checkColumnValue(riskGroup, i, patientId))
            	{
            		AttributeNominalValue vv = rgA.nominalValueMap.get(riskGroup.toLowerCase().trim());
                     
                     if (vv != null) 
                     {
                         PatientAttributeValue v = p.createPatientAttributeValue(rgA.attribute);
                         v.setAttributeNominalValue(vv);
                     }
                     else 
                     {
                    	 ConsoleLogger.getInstance().logError("Unsupported attribute value (Transmission group): "+riskGroup);
                     }	
            	}
            	
            	if(Utils.checkColumnValue(seroConverter, i, patientId))
            	{
            		 AttributeNominalValue vv = scA.nominalValueMap.get(seroConverter.trim());
                     
                     if (vv != null) 
                     {
                         PatientAttributeValue v = p.createPatientAttributeValue(scA.attribute);
                         v.setAttributeNominalValue(vv);
                     }
                     else 
                     {
                    	 ConsoleLogger.getInstance().logError("Unsupported attribute value (Seroconverter): "+seroConverter);
                     }
            	}
            	
            	if(Utils.checkColumnValue(firstTest, i, patientId))
            	{
            		TestResult t = p.createTestResult(hivTest);
                    t.setValue("First positive HIV test");
                    t.setTestDate(Utils.convertDate(firstTest));
            	}
            	
            	if(Utils.checkColumnValue(lastTest, i, patientId))
            	{
            		TestResult t = p.createTestResult(hivTest);
                    t.setValue("Last negative HIV test");
                    t.setTestDate(Utils.convertDate(lastTest));
            	}
            	
            	if(Utils.checkColumnValue(deathDate, i, patientId))
            	{
            		p.setDeathDate(Utils.convertDate(deathDate));
            	}
            	
            	if(Utils.checkColumnValue(deathReason, i, patientId))
            	{
            		//Ask if necessary, to be translated first (Wait for Mattia)
            	}
            	
            	if(Utils.checkColumnValue(syndrome, i, patientId) && Utils.checkColumnValue(syndromeDate, i, patientId))
            	{
            		TestResult t = p.createTestResult(acuteSyndromTest);
                    t.setValue(syndrome);
                    t.setTestDate(Utils.convertDate(syndromeDate));
            	}
            	
            	if(Utils.checkColumnValue(cdc, i, patientId))
            	{
                    AttributeNominalValue vv = cdcA.nominalValueMap.get(cdc.toUpperCase().trim());
                    
                    if(vv != null) 
                    {
                        PatientAttributeValue v = p.createPatientAttributeValue(cdcA.attribute);
                        v.setAttributeNominalValue(vv);
                    } 
                    else 
                    {
                    	ConsoleLogger.getInstance().logWarning("Unsupported attribute value (CDC): "+cdc);
                    }
            	}
            	
            	patientMap.put(patientId, p);
            }
            else
            {
           	 	ConsoleLogger.getInstance().logWarning("No patientID in row "+i+" present...Skipping data set");
            }
    	}
    }
    
    private void handleCDData()
    {
        HashMap<String, Test> uniqueVLTests = new HashMap<String, Test>();
        
    	int Ccd4PatientID = Utils.findColumn(this.cd4Table, "cartella UCSC");
    	int Ccd4AnalysisDate = Utils.findColumn(this.cd4Table, "data analisi HIV RNA CD4/CD8");
    	int CVLTest = Utils.findColumn(this.cd4Table, "metodo");
    	int CHIV = Utils.findColumn(this.cd4Table, "copie HIV RNA");
    	int CVLCutOff = Utils.findColumn(this.cd4Table, "cutoff");
    	int Ccd4Count = Utils.findColumn(this.cd4Table, "CD4 assoluti");
    	int Ccd4Percentage = Utils.findColumn(this.cd4Table, "CD4 %");
    	int Ccd8Count = Utils.findColumn(this.cd4Table, "CD8 assoluti");
    	int Ccd8Percentage = Utils.findColumn(this.cd4Table, "CD8 %");
    	
    	TestType cd4PercTestType = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), "CD4 Percentage", new TreeSet<TestNominalValue>());
    	Test cd4PercTest = new Test(cd4PercTestType, "CD4 Percentage (generic)");
    	
    	TestType cd8TestType = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), "CD8 Count", new TreeSet<TestNominalValue>());
    	Test cd8Test = new Test(cd8TestType, "CD8 Count (generic)");
    	
    	TestType cd8PercTestType = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), "CD8 Percentage", new TreeSet<TestNominalValue>());
    	Test cd8PercTest = new Test(cd8PercTestType, "CD8 Percentage (generic)");
    	
    	for(int i = 1; i < this.cd4Table.numRows(); i++)
    	{
    		String cd4PatientID = this.cd4Table.valueAt(Ccd4PatientID, i);
    		String analysisDate = this.cd4Table.valueAt(Ccd4AnalysisDate, i);
    		String vlTest = this.cd4Table.valueAt(CVLTest, i);
    		String vlHIV = this.cd4Table.valueAt(CHIV, i);
    		String vlco = this.cd4Table.valueAt(CVLCutOff, i);
    		String cd4Count = this.cd4Table.valueAt(Ccd4Count, i);
    		String cd4Percentage = this.cd4Table.valueAt(Ccd4Percentage, i);
    		String cd8Count = this.cd4Table.valueAt(Ccd8Count, i);
    		String cd8Percentage = this.cd4Table.valueAt(Ccd8Percentage, i);
    		
    		Patient p = patientMap.get(cd4PatientID);
    		
    		if(p == null)
    			ConsoleLogger.getInstance().logWarning("No patient with id "+cd4PatientID+" found.");
    			
    		//CD4
    		if (Utils.checkColumnValue(cd4Count, i, cd4PatientID) && Utils.checkCDValue(cd4Count, i, cd4PatientID)) 
    		{
                TestResult t = p.createTestResult(StandardObjects.getGenericCD4Test());
                t.setValue(cd4Count);
                t.setTestDate(Utils.convertDate(analysisDate));
    		}
    		if (Utils.checkColumnValue(cd4Percentage, i, cd4PatientID) && Utils.checkCDValue(cd4Percentage, i, cd4PatientID)) 
    		{
                TestResult t = p.createTestResult(cd4PercTest);
                t.setValue(cd4Percentage);
                t.setTestDate(Utils.convertDate(analysisDate));
    		}
    		 
    		//CD8
    		if (Utils.checkColumnValue(cd8Count, i, cd4PatientID) && Utils.checkCDValue(cd8Count, i, cd4PatientID)) 
    		{
                TestResult t = p.createTestResult(cd8Test);
                t.setValue(cd8Count);
                t.setTestDate(Utils.convertDate(analysisDate));
    		}
    		if (Utils.checkColumnValue(cd8Percentage, i, cd4PatientID) && Utils.checkCDValue(cd8Percentage, i, cd4PatientID)) 
    		{
                TestResult t = p.createTestResult(cd8PercTest);
                t.setValue(cd8Percentage);
                t.setTestDate(Utils.convertDate(analysisDate));
    		}
    		 
    		 if(Utils.checkColumnValue(vlHIV, i, cd4PatientID))
    		 {
	    		 TestResult testResult = null;
	    		 
	    		 if("".equals(vlTest))
	    		 {
	    			 testResult = p.createTestResult(StandardObjects.getGenericViralLoadTest());
	    		 }
	    		 else
	    		 {
                     Test vlT = uniqueVLTests.get(vlTest);
                     
                     if(vlT==null) 
                     {
                         vlT = new Test(StandardObjects.getViralLoadTestType(), vlTest);
                         uniqueVLTests.put(vlTest, vlT);
                     }
	    			 
	    			 testResult = p.createTestResult(vlT);
	    		 }
	    		 
	    		 String value = null;
	    		 
	    		 if(Integer.parseInt(vlco) == -1)
	    			 value = "<";
	    		 else
	    			 value = "=";
	    		 
	    		 value += vlHIV;
	    		 
	    		 testResult.setValue(value);
	    		 testResult.setTestDate(Utils.convertDate(analysisDate));
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
    	//int ChivStopReasonTherapy = Utils.findColumn(this.hivTherapyTable, "motivo stop terapia anti HIV");
    	int ChivCommercialDrug = Utils.findColumn(this.hivTherapyTable, "terapia ARV");
    	int ChivAZTDrug = Utils.findColumn(this.hivTherapyTable, "AZT");
    	int ChivDDIDrug = Utils.findColumn(this.hivTherapyTable, "DDI");
    	int ChivDDCDrug = Utils.findColumn(this.hivTherapyTable, "DDC");
    	int ChivHUDrug = Utils.findColumn(this.hivTherapyTable, "HU");
    	int Chiv3TCDrug = Utils.findColumn(this.hivTherapyTable, "3TC");
    	int ChivD4TDrug = Utils.findColumn(this.hivTherapyTable, "D4T");
    	int ChivABCDrug = Utils.findColumn(this.hivTherapyTable, "ABC");
    	int ChivFTCDrug = Utils.findColumn(this.hivTherapyTable, "FTC");
    	int ChivTDFDrug = Utils.findColumn(this.hivTherapyTable, "TDF");
    	int ChivNVPDrug = Utils.findColumn(this.hivTherapyTable, "NVP");
    	int ChivEFVDrug = Utils.findColumn(this.hivTherapyTable, "EFV");
    	int ChivDLVDrug = Utils.findColumn(this.hivTherapyTable, "DLV");
    	int ChivCPVDrug = Utils.findColumn(this.hivTherapyTable, "CPV");
    	int ChivIDVDrug = Utils.findColumn(this.hivTherapyTable, "IDV");
    	int ChivRTVDrug = Utils.findColumn(this.hivTherapyTable, "RTV");
    	int ChivRTVBoostDrug = Utils.findColumn(this.hivTherapyTable, "RTV_booster");
    	int ChivSQVDrug = Utils.findColumn(this.hivTherapyTable, "SQV");
    	int ChivNFVDrug = Utils.findColumn(this.hivTherapyTable, "NFV");
    	int ChivAPVDrug = Utils.findColumn(this.hivTherapyTable, "APV");
    	int ChivFPVDrug = Utils.findColumn(this.hivTherapyTable, "FPV");
    	int ChivLPVDrug = Utils.findColumn(this.hivTherapyTable, "LPV");
    	int ChivATVDrug = Utils.findColumn(this.hivTherapyTable, "ATV");
    	int ChivTPVDrug = Utils.findColumn(this.hivTherapyTable, "TPV");
    	int ChivT20Drug = Utils.findColumn(this.hivTherapyTable, "T20");
    	
    	    	
    	for(int i = 1; i < this.hivTherapyTable.numRows(); i++)
    	{
    		String hivPatientID = this.hivTherapyTable.valueAt(ChivPatientID, i);
    		String hivStartTherapy = this.hivTherapyTable.valueAt(ChivStartTherapy, i);
    		String hivStopTherapy = this.hivTherapyTable.valueAt(ChivStopTherapy, i);
    		//String hivLineTherapy = this.hivTherapyTable.valueAt(ChivLineTherapy, i);
    		//String hivSuccessTherapy = this.hivTherapyTable.valueAt(ChivSuccessTherapy, i);
    		//String hivStopReasonTherapy = this.hivTherapyTable.valueAt(ChivStopReasonTherapy, i);
    		String hivCommercialDrug = this.hivTherapyTable.valueAt(ChivCommercialDrug, i);
    		String hivAZTDrug = this.hivTherapyTable.valueAt(ChivAZTDrug, i);
    		String hivDDIDrug = this.hivTherapyTable.valueAt(ChivDDIDrug, i);
    		String hivDDCDrug = this.hivTherapyTable.valueAt(ChivDDCDrug, i);
    		String hivHUDrug = this.hivTherapyTable.valueAt(ChivHUDrug, i);
    		String hiv3TCDrug = this.hivTherapyTable.valueAt(Chiv3TCDrug, i);
    		String hivD4TDrug = this.hivTherapyTable.valueAt(ChivD4TDrug, i);
    		String hivABCDrug = this.hivTherapyTable.valueAt(ChivABCDrug, i);
    		String hivFTCDrug = this.hivTherapyTable.valueAt(ChivFTCDrug, i);
    		String hivTDFDrug = this.hivTherapyTable.valueAt(ChivTDFDrug, i);
    		String hivNVPDrug = this.hivTherapyTable.valueAt(ChivNVPDrug, i);
    		String hivEFVDrug = this.hivTherapyTable.valueAt(ChivEFVDrug, i);
    		String hivDLVDrug = this.hivTherapyTable.valueAt(ChivDLVDrug, i);;
        	String hivCPVDrug = this.hivTherapyTable.valueAt(ChivCPVDrug, i);
        	String hivIDVDrug = this.hivTherapyTable.valueAt(ChivIDVDrug, i);
        	String hivRTVDrug = this.hivTherapyTable.valueAt(ChivRTVDrug, i);
        	String hivRTVBoostDrug = this.hivTherapyTable.valueAt(ChivRTVBoostDrug, i);
        	String hivSQVDrug = this.hivTherapyTable.valueAt(ChivSQVDrug, i);
        	String hivNFVDrug = this.hivTherapyTable.valueAt(ChivNFVDrug, i);
        	String hivAPVDrug = this.hivTherapyTable.valueAt(ChivAPVDrug, i);
        	String hivFPVDrug = this.hivTherapyTable.valueAt(ChivFPVDrug, i);
        	String hivLPVDrug = this.hivTherapyTable.valueAt(ChivLPVDrug, i);
        	String hivATVDrug = this.hivTherapyTable.valueAt(ChivATVDrug, i);
        	String hivTPVDrug = this.hivTherapyTable.valueAt(ChivTPVDrug, i);
        	String hivT20Drug = this.hivTherapyTable.valueAt(ChivT20Drug, i);
        	
        	if(!"".equals(hivPatientID))
            {
        		ArrayList<String> drugs = new ArrayList<String>();
        		Date startDate = null;
        		Date stopDate = null;
        		
        		if(Utils.checkColumnValue(hivStartTherapy, i, hivPatientID))
        		{
        			startDate = Utils.convertDate(hivStartTherapy);
        		}
        		
        		if(Utils.checkColumnValue(hivAZTDrug, i, hivPatientID) && Utils.checkDrugValue(hivAZTDrug, i, hivPatientID))
        		{
        			drugs.add("AZT");
        		}
        		
        		if(Utils.checkColumnValue(hivDDIDrug, i, hivPatientID) && Utils.checkDrugValue(hivDDIDrug, i, hivPatientID))
        		{
        			drugs.add("DDI");
        		}
        		
        		if(Utils.checkColumnValue(hivDDCDrug, i, hivPatientID) && Utils.checkDrugValue(hivDDCDrug, i, hivPatientID))
        		{
        			drugs.add("DDC");
        		}
        		
        		if(Utils.checkColumnValue(hivHUDrug, i, hivPatientID) && Utils.checkDrugValue(hivHUDrug, i, hivPatientID))
        		{
        			drugs.add("HU");
        		}
        		
        		if(Utils.checkColumnValue(hiv3TCDrug, i, hivPatientID) && Utils.checkDrugValue(hiv3TCDrug, i, hivPatientID))
        		{
        			drugs.add("3TC");
        		}
        		
        		if(Utils.checkColumnValue(hivD4TDrug, i, hivPatientID)&& Utils.checkDrugValue(hivD4TDrug, i, hivPatientID))
        		{
        			drugs.add("D4T");
        		}
        		
        		if(Utils.checkColumnValue(hivABCDrug, i, hivPatientID) && Utils.checkDrugValue(hivABCDrug, i, hivPatientID))
        		{
        			drugs.add("ABC");
        		}
        		
        		if(Utils.checkColumnValue(hivFTCDrug, i, hivPatientID) && Utils.checkDrugValue(hivFTCDrug, i, hivPatientID))
        		{
        			drugs.add("FTC");
        		}
        		
        		if(Utils.checkColumnValue(hivTDFDrug, i, hivPatientID) && Utils.checkDrugValue(hivTDFDrug, i, hivPatientID))
        		{
        			drugs.add("TDF");
        		}
        		
        		if(Utils.checkColumnValue(hivNVPDrug, i, hivPatientID) && Utils.checkDrugValue(hivNVPDrug, i, hivPatientID))
        		{
        			drugs.add("NVP");
        		}
        		
        		if(Utils.checkColumnValue(hivEFVDrug, i, hivPatientID) && Utils.checkDrugValue(hivEFVDrug, i, hivPatientID))
        		{
        			drugs.add("EFV");
        		}
        		
        		if(Utils.checkColumnValue(hivDLVDrug, i, hivPatientID) && Utils.checkDrugValue(hivDLVDrug, i, hivPatientID))
        		{
        			drugs.add("DLV");
        		}
        		
        		if(Utils.checkColumnValue(hivCPVDrug, i, hivPatientID) && Utils.checkDrugValue(hivCPVDrug, i, hivPatientID))
        		{
        			drugs.add("CPV");
        		}
        		
        		if(Utils.checkColumnValue(hivIDVDrug, i, hivPatientID) && Utils.checkDrugValue(hivIDVDrug, i, hivPatientID))
        		{
        			drugs.add("IDV");
        		}
        		
        		if(Utils.checkColumnValue(hivRTVDrug, i, hivPatientID) && Utils.checkDrugValue(hivRTVDrug, i, hivPatientID))
        		{
        			drugs.add("RTV");
        		}
        		
        		if(Utils.checkColumnValue(hivRTVBoostDrug, i, hivPatientID) && Utils.checkDrugValue(hivRTVBoostDrug, i, hivPatientID))
        		{
        			drugs.add("RTV");
        		}
        		
        		if(Utils.checkColumnValue(hivSQVDrug, i, hivPatientID) && Utils.checkDrugValue(hivSQVDrug, i, hivPatientID))
        		{
        			drugs.add("SQV");
        		}
        		
        		if(Utils.checkColumnValue(hivNFVDrug, i, hivPatientID) && Utils.checkDrugValue(hivNFVDrug, i, hivPatientID))
        		{
        			drugs.add("NFV");
        		}
        		
        		if(Utils.checkColumnValue(hivAPVDrug, i, hivPatientID) && Utils.checkDrugValue(hivAPVDrug, i, hivPatientID))
        		{
        			drugs.add("APV");
        		}
        		
        		if(Utils.checkColumnValue(hivFPVDrug, i, hivPatientID) && Utils.checkDrugValue(hivFPVDrug, i, hivPatientID))
        		{
        			drugs.add("FPV");
        		}
        		
        		if(Utils.checkColumnValue(hivLPVDrug, i, hivPatientID) && Utils.checkDrugValue(hivLPVDrug, i, hivPatientID))
        		{
        			drugs.add("LPV");
        		}
        		
        		if(Utils.checkColumnValue(hivATVDrug, i, hivPatientID) && Utils.checkDrugValue(hivATVDrug, i, hivPatientID))
        		{
        			drugs.add("ATV");
        		}
        		
        		if(Utils.checkColumnValue(hivTPVDrug, i, hivPatientID) && Utils.checkDrugValue(hivTPVDrug, i, hivPatientID))
        		{
        			drugs.add("TPV");
        		}
        		
        		if(Utils.checkColumnValue(hivT20Drug, i, hivPatientID) && Utils.checkDrugValue(hivT20Drug, i, hivPatientID))
        		{
        			drugs.add("T20");
        		}
        		
        		ArrayList<DrugCommercial> comDrugs = evaluateDrugs(hivCommercialDrug.toLowerCase(), drugs);
        		
        		if(Utils.checkColumnValue(hivStopTherapy, i, hivPatientID))
        		{
        			stopDate = Utils.convertDate(hivStopTherapy);
        		}
        		
        		if(hivPatientID != null)
        		{
        			storeTherapy(hivPatientID, startDate, stopDate, comDrugs, null);
        		}
            }
        	else
        	{
        		ConsoleLogger.getInstance().logWarning("No patient with id "+hivPatientID+" found.");
        	}
    	}
    }
    
    private ArrayList<DrugCommercial> evaluateDrugs(String hivCommercialDrug, ArrayList<String> drugs)
    {
    	ArrayList<DrugCommercial> comDrugs = new ArrayList<DrugCommercial>();

    	if("".equals(hivCommercialDrug))
    		return null;
    	
    	String[] split = null;
    	
    	split = hivCommercialDrug.split("\\+");
    	
    	if(split == null || split.length == 0)
    	{
    		split = new String[1];
    		split[0] = hivCommercialDrug.toLowerCase();
    	}
    	
    	for(int i = 0; i < dcs.size(); i++)
    	{
    		DrugCommercial drug = dcs.get(i);
    		
    		if(drug != null)
    		{
    			for(int j = 0; j < split.length; j++)
    			{
    				if(drug.getName().toLowerCase().startsWith(split[j]))
    				{
    					String currentDrug = split[j];
    					
    					String tempDrug = "";
    					
		    			for(Iterator it = drug.getDrugGenerics().iterator(); it.hasNext();) 
		    			{
		    				DrugGeneric dg = (DrugGeneric)it.next();
		    				
		    				if(dg != null)
		    				{
		    					String id = dg.getGenericId();
		    					
		    					for(int k = 0; k < drugs.size(); k++)
		    					{
		    						if(id.startsWith((String)drugs.get(k)))
		    						{
		    							if(!tempDrug.equals(currentDrug))
		    							{
		    								comDrugs.add(drug);
		    							}
		    							
		    							drugs.remove(k);
		    							
		    							tempDrug = currentDrug; 
		    						}
		    					}
		    				}
		    			}
    				}
    			}
    		}
    	}
    	
    	if(comDrugs.size() != split.length)
    	{
    		return null;
    	}
    	
    	return comDrugs;
    }
    
	private void storeTherapy(String patientId, Date startDate, Date endDate, ArrayList<DrugCommercial> medicinsList, String comment) 
    {
    	Patient p = patientMap.get(patientId);

    	if (p == null)
    	{
    		ConsoleLogger.getInstance().logWarning("No patient with id "+patientId+" found.");
    		
    		return;
    	}
    	
    	if(medicinsList == null)
    	{
    		ConsoleLogger.getInstance().logWarning("Something wrong with therapy mapping for patient '" + patientId + "'");
			
    		return;
    	}

    	if(startDate != null && endDate != null)
    	{
	    	if(startDate.equals(endDate) || startDate.after(endDate))
	    	{
	    		ConsoleLogger.getInstance().logWarning("Something wrong with treatment dates for patient '" + patientId + "': " + startDate.toLocaleString() + " - " + endDate.toLocaleString() + ": End date is in the past...ignoring");
	    			
	    		return;
	    	}
    	}
    	else if(startDate == null)
    	{
    		ConsoleLogger.getInstance().logError(patientId, "No corresponding start date available.");
    	}
    	
    	//TODO: Additional error handling...
    	/*else if(startDate != null && endDate == null)
    	{
    		ConsoleLogger.getInstance().logWarning("Something wrong with treatment dates for patient '" + patientId + "': No suitable end date available...ignoring");
			
    		return;
    	}
    	/*else
    	{
    		ConsoleLogger.getInstance().logWarning("Something wrong with treatment dates for patient '" + patientId + "': No suitable start and end dates available...ignoring");
			
    		return;
    	}*/

    	Therapy t = p.createTherapy(startDate);
    	t.setStopDate(endDate);
    	t.setComment(comment);

    	for (int i = 0; i < medicinsList.size(); i++) 
    	{
    		TherapyCommercial tc = new TherapyCommercial(new TherapyCommercialId(t, medicinsList.get(i)));
    		t.getTherapyCommercials().add(tc);
    	}
    }
    
    private void addViralIsolateToPatients(String patientID, Date date, String seq)
    {
    	Patient p = patientMap.get(patientID);
    	
    	if(p == null)
    	{
    		ConsoleLogger.getInstance().logWarning("No patient with id "+patientID+" found.");
    		
    		return;
    	}
    	
    	ViralIsolate vi = p.createViralIsolate();
    	vi.setSampleId(patientID+date.toString());
    	vi.setSampleDate(date);
    	
    	NtSequence ntseq = new NtSequence();
    	ntseq.setLabel("Sequence1");
    	ntseq.setSequenceDate(date);
    	ntseq.setNucleotides(seq);
    	
    	vi.getNtSequences().add(ntseq);
    }
    
    //to obtain this, run the following query in microsoft access
    //SELECT T_genotipo_HIV.[cartella UCSC], T_genotipo_HIV.[data genotipo], T_genotipo_HIV.[sequenza basi azotate (fasta)], ""
    //FROM T_genotipo_HIV;
    //export with (; and no text separation sign)
    public void handleSequences(File workingDirectory) throws IOException 
    {
        File onlySequences = new File(workingDirectory.getAbsolutePath()+File.separatorChar+"nt_sequences.txt");
        String fileContent = new String(FileUtils.readFileToByteArray(onlySequences));
        StringTokenizer st = new StringTokenizer(fileContent, ";");
        String token;
        int linePositionCounter = 0;
        
   	 	String patientID = null;
   	 	Date date = null;
     
        for(int i = 0; i<st.countTokens(); i++) 
        {
            token = st.nextToken();
             
            if(linePositionCounter == 0)
            {
            	patientID = token.trim();
            	linePositionCounter++;
            }
            else if(linePositionCounter == 1)
            {
            	date = Utils.convertDate(token.trim());
            	linePositionCounter++;
            }
            else if(linePositionCounter==2) 
            {
            	String seq = Utils.clearNucleotides(token);
            	
            	ConsoleLogger.getInstance().logInfo("Patient: "+patientID+" Seq: "+seq);
            	
                addViralIsolateToPatients(patientID, date, seq);
                
                linePositionCounter = 0;
            }
        }
    }
     
     private HashMap<String, String> getCountryTranslation()
     {
    	 int italianIndex = Utils.findColumn(this.countryTable, "nazionalità");
    	 int englishIndex = Utils.findColumn(this.countryTable, "Nationality");
    	 
    	 HashMap<String, String> values = new HashMap<String, String>();
    	 
    	 for(int i = 1; i < this.countryTable.numRows(); i++)
     	 {
             String italianvalue = this.countryTable.valueAt(italianIndex, i);
             String englishvalue = this.countryTable.valueAt(englishIndex, i);
             
             if(!"".equals(italianvalue) && !"".equals(englishvalue))
             {
            	 values.put(italianvalue, englishvalue);
             }
             else
             {
            	 ConsoleLogger.getInstance().logWarning("Values in row "+i+" not present.");
             }
     	 }
    	 
    	 return values;
     }
    
     private HashMap<String, String> getBirthPlaceTranslation()
     {
    	 int italianIndex = Utils.findColumn(this.birthPlaceTable, "luogo di nascita");
    	 int englishIndex = Utils.findColumn(this.birthPlaceTable, "Birthplace");
    	 
    	 HashMap<String, String> values = new HashMap<String, String>();
    	 
    	 for(int i = 1; i < this.birthPlaceTable.numRows(); i++)
     	 {
             String italianvalue = this.birthPlaceTable.valueAt(italianIndex, i);
             String englishvalue = this.birthPlaceTable.valueAt(englishIndex, i);
             
             if(!"".equals(italianvalue) && !"".equals(englishvalue))
             {
            	 values.put(italianvalue, englishvalue);
             }
             else
             {
            	 ConsoleLogger.getInstance().logWarning("Values in row "+i+" not present.");
             }
     	 }
    	 
    	 return values;
     }
    
    private HashMap<String, String> getRiskGroupTranslation()
    {
    	 int italianIndex = Utils.findColumn(this.riskGroupTable, "fattore di rischio");
    	 int englishIndex = Utils.findColumn(this.riskGroupTable, "Transmission Group");
    	 
    	 HashMap<String, String> values = new HashMap<String, String>();
    	 
    	 for(int i = 1; i < this.riskGroupTable.numRows(); i++)
     	 {
             String italianvalue = this.riskGroupTable.valueAt(italianIndex, i);
             String englishvalue = this.riskGroupTable.valueAt(englishIndex, i);
             
             if(!"".equals(italianvalue) && !"".equals(englishvalue))
             {
            	 values.put(italianvalue, englishvalue);
             }
             else
             {
            	 ConsoleLogger.getInstance().logWarning("Values in row "+i+" not present.");
             }
     	 }
    	 
    	 return values;
     }
    
     private HashMap<String, String> getStopTherapyTranslation()
     {
    	 int italianIndex = Utils.findColumn(this.stopTherapieDescTable, "motivo stop terapia anti HIV");
    	 int englishIndex = Utils.findColumn(this.stopTherapieDescTable, "Reason for stopping HIV Therapy");
    	 
    	 HashMap<String, String> values = new HashMap<String, String>();
    	 
    	 for(int i = 1; i < this.stopTherapieDescTable.numRows(); i++)
     	 {
             String italianvalue = this.stopTherapieDescTable.valueAt(italianIndex, i);
             String englishvalue = this.stopTherapieDescTable.valueAt(englishIndex, i);
             
             if(!"".equals(italianvalue) && !"".equals(englishvalue))
             {
            	 values.put(italianvalue, englishvalue);
             }
             else
             {
            	 ConsoleLogger.getInstance().logWarning("Values in row "+i+" not present.");
             }
     	 }
    	 
    	 return values;
     }
}
