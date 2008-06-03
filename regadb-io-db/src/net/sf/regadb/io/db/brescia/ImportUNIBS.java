package net.sf.regadb.io.db.brescia;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Event;
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
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.NominalEvent;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.frequency.Frequency;

public class ImportUNIBS 
{
	//DB tables
	private Table patientTable;
	private Table deathTable;
	private Table cd4Table;
	private Table rnaTable;
	private Table aMarkersTable;
	private Table bMarkersTable;
	private Table adeTable;
	private Table hivTherapyTable;
	
	//Translation mapping tables
	private Table countryMappingTable;
	private Table transmissionGroupmappingTable;
	private Table statusMappingTable;
	private Table seroConverterMappingTable;
	private Table stopTherapyReasonMappingTable;
	private Table deathReasonMappingTable;
	private Table adeMappingTable;

	private HashMap<String, String> stopTherapyTranslation;
	
	private Map<String, Patient> patientMap = new HashMap<String, Patient>();
	
	private List<DrugGeneric> regaDrugGenerics;
	
	private List<Attribute> regadbAttributes;
	private List<Event> regadbEvents;
    
    private AttributeGroup regadb = new AttributeGroup("RegaDB");
    private AttributeGroup virolab = new AttributeGroup("ViroLab");
    
    private Mappings mappings;
    
    private TestNominalValue posSeroStatus;
    
    public static void main(String [] args) 
    {
        
    	try
    	{
    		ImportUNIBS imp = new  ImportUNIBS();
        
    		imp.getData(new File(args[0]), args[1], args[2]);
    	}
    	catch(Exception e)
    	{
    		ConsoleLogger.getInstance().logError("Unknown error: "+e.getMessage());
    	}
    }
    
    public void getData(File workingDirectory, String mappingBasePath, String excelFile)
    {
    	//Just for testing purposes...otherwise remove
		ConsoleLogger.getInstance().setInfoEnabled(true);
    	
    	try
    	{
    		mappings = Mappings.getInstance(mappingBasePath);
    		
    		ConsoleLogger.getInstance().logInfo("Reading input files...");
    		//Filling DB tables
    		patientTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "1_Pazienti.csv");
    		deathTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "7_Decessi.csv");
    		
    		cd4Table = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "2_CD4.csv");
    		rnaTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "3_HIVRNA.csv");
    		
    		aMarkersTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "4a_Markers.csv");
    		bMarkersTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "4b_Markers.csv");
    		
    		hivTherapyTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "5_TARV.csv");
    		
    		adeTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "6_ADEs.csv");
    		
    		//Filling translation mapping tables
    		ConsoleLogger.getInstance().logInfo("Initializing mapping tables...");
    		countryMappingTable = Utils.readTable(mappingBasePath + File.separatorChar + "county_of_origin.mapping");   		
    		transmissionGroupmappingTable = Utils.readTable(mappingBasePath + File.separatorChar + "transmission_group.mapping");
    		statusMappingTable = Utils.readTable(mappingBasePath + File.separatorChar + "status.mapping");
    		seroConverterMappingTable = Utils.readTable(mappingBasePath + File.separatorChar + "seroconverter.mapping");
    		stopTherapyReasonMappingTable = Utils.readTable(mappingBasePath + File.separatorChar + "stop_therapy_reason.mapping");  
    		deathReasonMappingTable = Utils.readTable(mappingBasePath + File.separatorChar + "death_reason.mapping");  
    		adeMappingTable = Utils.readTable(mappingBasePath + File.separatorChar + "aids_defining_illness.mapping"); 
    		
    		ConsoleLogger.getInstance().logInfo("Retrieving all necessary translations...");
    		stopTherapyTranslation = Utils.translationFileToHashMap(stopTherapyReasonMappingTable);
    		
    		ConsoleLogger.getInstance().logInfo("Retrieving attributes, drugs, and events...");
    		regadbAttributes = Utils.prepareRegaDBAttributes();
    		regaDrugGenerics = Utils.prepareRegaDrugGenerics();
    		regadbEvents = Utils.prepareRegaDBEvents();
    		
    		posSeroStatus = Utils.getNominalValue(StandardObjects.getHiv1SeroStatusTestType(), "Positive");
    		
    		ConsoleLogger.getInstance().logInfo("Migrating patient information...");
    		handlePatientData();
    		ConsoleLogger.getInstance().logInfo("Successful");
    		ConsoleLogger.getInstance().logInfo("Migrating CD data...");
    		handleViralData();
    		ConsoleLogger.getInstance().logInfo("Successful");
    		ConsoleLogger.getInstance().logInfo("Migrating 4a marker data...");
    		handleAMarkerData();
    		ConsoleLogger.getInstance().logInfo("Successful");
    		ConsoleLogger.getInstance().logInfo("Migrating 4b marker data...");
    		handleBMarkerData();
    		ConsoleLogger.getInstance().logInfo("Successful");
    		ConsoleLogger.getInstance().logInfo("Migrating ADE data...");
    		handleEvent();
    		ConsoleLogger.getInstance().logInfo("Successful");
    		ConsoleLogger.getInstance().logInfo("Migrating treatments...");
    		handleTherapies();
    		ConsoleLogger.getInstance().logInfo("Successful");
    		ConsoleLogger.getInstance().logInfo("Migrating viral isolates...");
    		ImportSequences sequenceImport = new ImportSequences(patientMap, new File(excelFile));
    		sequenceImport.run();
    		ConsoleLogger.getInstance().logInfo("Successful");
    		
    		ConsoleLogger.getInstance().logInfo("Generating output xml file...");
    		Utils.exportPatientsXML(patientMap, workingDirectory.getAbsolutePath() + File.separatorChar + "unibs_patients.xml");
    		Utils.exportNTXMLFromPatients(patientMap, workingDirectory.getAbsolutePath() + File.separatorChar + "unibs_viralIsolates.xml");
    		ConsoleLogger.getInstance().logInfo("Export finished.");
    	}
    	catch(Exception e)
    	{
    		ConsoleLogger.getInstance().logError("Unknown error: "+e.getMessage());
    	}
    }
    
    private void handlePatientData()
    {
    	int CpatientID = Utils.findColumn(this.patientTable, "ID_Coorte");
    	int Csex = Utils.findColumn(this.patientTable, "Sesso");
    	int CbirthDate = Utils.findColumn(this.patientTable, "DataNascita");
    	int Cnationality = Utils.findColumn(this.patientTable, "Nazionalita");
     
    	int CfirstTest = Utils.findColumn(this.patientTable, "Data_HIV+");
    	int CriskGroup = Utils.findColumn(this.patientTable, "FR");
    
    	int ClastTest = Utils.findColumn(this.patientTable, "Fup");
    
    	int Cstatus = Utils.findColumn(this.patientTable, "Status");
    	int CseroConverter = Utils.findColumn(this.patientTable, "Sieroconv");
    	
    	int CdeathPatientID = Utils.findColumn(this.deathTable, "ID_Coorte");
     	int CdeathDate = Utils.findColumn(this.deathTable, "DataDecesso");
     	int CdeathReason = Utils.findColumn(this.deathTable, "CausaDec");
    	
    	NominalAttribute gender = new NominalAttribute("Gender", Csex, new String[] { "M", "F" },
                 new String[] { "male", "female" } );
        gender.attribute.setAttributeGroup(regadb);
         
        NominalAttribute seroA = new NominalAttribute("Seroconverter", seroConverterMappingTable, virolab, null);
        NominalAttribute transmissionGroupA = new NominalAttribute("Transmission group", transmissionGroupmappingTable, regadb, Utils.selectAttribute("Transmission group", regadbAttributes));  
        NominalAttribute originA = new NominalAttribute("Geographic origin", countryMappingTable, regadb, Utils.selectAttribute("Geographic origin", regadbAttributes));
        NominalAttribute statusA = new NominalAttribute("Status", statusMappingTable, virolab, null);
     	
     	NominalAttribute deathReasonA = new NominalAttribute("Reason of Death", deathReasonMappingTable, virolab, null);
     	 
     	TestType hivTestType = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), "Last date of follow-up available", new TreeSet<TestNominalValue>());
    	Test hivTest = new Test(hivTestType, "Last date of follow-up available");
     	
     	for(int i = 1; i < this.patientTable.numRows(); i++)
    	{
            String patientId = this.patientTable.valueAt(CpatientID, i);
            String sex = this.patientTable.valueAt(Csex, i);
            String birthDate = this.patientTable.valueAt(CbirthDate, i);
            String nationality = this.patientTable.valueAt(Cnationality, i);
            String firstTest = this.patientTable.valueAt(CfirstTest, i);
            String seroConverter = this.patientTable.valueAt(CseroConverter, i);
            String lastTest = this.patientTable.valueAt(ClastTest, i);
            String status = this.patientTable.valueAt(Cstatus, i);
            String riskGroup = this.patientTable.valueAt(CriskGroup, i);
                       
            if(!"".equals(patientId))
            {
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
            	
            	if(Utils.checkColumnValueForExistance("nationality", nationality, i, patientId))
            	{
            		Utils.handlePatientAttributeValue(originA, nationality, p);
            	}
            	
            	if(Utils.checkColumnValueForExistance("date of first positive HIV test", firstTest, i, patientId))
            	{
            		TestResult t = p.createTestResult(StandardObjects.getGenericHiv1SeroStatusTest());
                    t.setTestNominalValue(posSeroStatus);
                    t.setTestDate(Utils.parseEnglishAccessDate(firstTest));
            	}
            	
            	if(Utils.checkColumnValueForEmptiness("risk group", riskGroup, i, patientId))
            	{
            		Utils.handlePatientAttributeValue(transmissionGroupA, riskGroup, p);
            	}
            	
            	if(Utils.checkColumnValueForExistance("date of last negative HIV test", lastTest, i, patientId))
            	{
            	    //TODO
            	    TestResult t = p.createTestResult(hivTest);
                    t.setValue("Contact");
                    t.setTestDate(Utils.parseEnglishAccessDate(lastTest));
            	}
            	
            	if(Utils.checkColumnValueForExistance("status", status, i, patientId))
            	{
            		Utils.handlePatientAttributeValue(statusA, status, p);
            	}
            	
            	//TODO: To check with Giuseppe if this is relevant
            	if(Utils.checkColumnValueForExistance("sero converter", seroConverter, i, patientId))
            	{
            		Utils.handlePatientAttributeValue(seroA, seroConverter, p);
            	}
            	
            	patientMap.put(patientId, p);
            }
            else
            {
           	 	ConsoleLogger.getInstance().logWarning("No patientID in row "+i+" present...Skipping data set");
            }
    	}
     	
     	for(int i = 1; i < this.deathTable.numRows(); i++)
    	{
    		String deathPatientID = this.deathTable.valueAt(CdeathPatientID, i);
    		String deathDate = this.deathTable.valueAt(CdeathDate, i);;
    		String deathReason = this.deathTable.valueAt(CdeathReason, i);
    		
    		Patient p = patientMap.get(deathPatientID);
    		
    		if(p == null)
    		{
    			ConsoleLogger.getInstance().logWarning("No death patient with id "+deathPatientID+" found.");
    		}
    		else
    		{
    			if(Utils.checkColumnValueForExistance("date of death", deathDate, i, deathPatientID))
            	{
            		p.setDeathDate(Utils.parseEnglishAccessDate(deathDate));
            	}
    			
    			if(Utils.checkColumnValueForExistance("death reason", deathReason, i, deathPatientID))
            	{
            		Utils.handlePatientAttributeValue(deathReasonA, deathReason, p);
            	}
    		}
    	}
    }
    
    private void handleViralData()
    {
        HashMap<String, Test> uniqueVLTests = new HashMap<String, Test>();
        
    	int Ccd4PatientID = Utils.findColumn(this.cd4Table, "ID_Coorte");
    	int Ccd4AnalysisDate = Utils.findColumn(this.cd4Table, "Data_Esame");
    	int Ccd4Count = Utils.findColumn(this.cd4Table, "CD4+");
    	int Ccd4Percentage = Utils.findColumn(this.cd4Table, "CD4%");

    	int CrnaPatientID = Utils.findColumn(this.rnaTable, "ID_Coorte");
    	int CrnaAnalysisDate = Utils.findColumn(this.rnaTable, "Data_Esame");
    	int CVLHIV = Utils.findColumn(this.rnaTable, "HIV RNA");
    	int CVLTest = Utils.findColumn(this.rnaTable, "Metodica");
    	int CVLCutOff = Utils.findColumn(this.rnaTable, "CutOff");
    	
    	TestType cd4PercTestType = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), "CD4 Percentage", new TreeSet<TestNominalValue>());
    	Test cd4PercTest = new Test(cd4PercTestType, "CD4 Percentage (generic)");
    	
    	for(int i = 1; i < this.cd4Table.numRows(); i++)
    	{
    		String cd4PatientID = this.cd4Table.valueAt(Ccd4PatientID, i);
    		String analysisDate = this.cd4Table.valueAt(Ccd4AnalysisDate, i);;
    		String cd4Count = this.cd4Table.valueAt(Ccd4Count, i);
    		String cd4Percentage = this.cd4Table.valueAt(Ccd4Percentage, i);
    		
    		Patient p = patientMap.get(cd4PatientID);
    		
    		if(p == null)
    		{
    			ConsoleLogger.getInstance().logWarning("No cd4 patient with id "+cd4PatientID+" found.");
    		}
    		else
    		{
	    		if (Utils.checkColumnValueForEmptiness("CD4 test result (�L)", cd4Count, i, cd4PatientID) && Utils.checkCDValue(cd4Count, i, cd4PatientID)) 
	    		{
	                TestResult t = p.createTestResult(StandardObjects.getGenericCD4Test());
	                t.setValue(cd4Count);
	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
	    		}
	    		if (Utils.checkColumnValueForExistance("CD4 test result (%)", cd4Percentage, i, cd4PatientID)) 
	    		{
	                TestResult t = p.createTestResult(cd4PercTest);
	                t.setValue(cd4Percentage);
	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
	    		}
    		}
    	}
    	
    	for(int i = 1; i < this.rnaTable.numRows(); i++)
    	{
    		String rnaPatientID = this.rnaTable.valueAt(CrnaPatientID, i);
    		String rnaAnalysisDate = this.rnaTable.valueAt(CrnaAnalysisDate, i);;
    		String VLTest = this.rnaTable.valueAt(CVLTest, i);
    		String VLHIV = this.rnaTable.valueAt(CVLHIV, i);
    		//TODO
    		//cutoff can be missing
    		//viral load <50 -> value=1 -> save as <50
    		//no cutoff, val 1 -> <50
    		///val=1, cutoff  is specifified -> use less than cutoff
    		String VLCutOff = this.rnaTable.valueAt(CVLCutOff, i);
    		
    		Patient p = patientMap.get(rnaPatientID);
    		
    		if(p == null)
    		{
    			ConsoleLogger.getInstance().logWarning("No rna patient with id "+rnaPatientID+" found.");
    		}
    		else
    		{
	    		if(Utils.checkColumnValueForEmptiness("HIV RNA test result", VLHIV, i, rnaPatientID) && Utils.checkColumnValueForEmptiness("date ofHIV RNA test result", rnaAnalysisDate, i, rnaPatientID))
	   		 	{
		    		 TestResult testResult = null;
		    		 
		    		 if("".equals(VLTest))
		    		 {
		    			 testResult = p.createTestResult(StandardObjects.getGenericHiv1ViralLoadTest());
		    		 }
		    		 else
		    		 {
		    			 Test vlT = uniqueVLTests.get(VLTest);
	                     
	                     if(vlT==null) 
	                     {
	                         vlT = new Test(StandardObjects.getHiv1ViralLoadTestType(), VLTest);
	                         uniqueVLTests.put(VLTest, vlT);
	                     }
		    			 
		    			 testResult = p.createTestResult(vlT);
		    		 }
		    		 
		    		 String value = null;
		    		 
		    		 if(Double.parseDouble(VLHIV) == 1)
		    			 value = "<"+VLCutOff;
		    		 else if(Integer.parseInt(VLCutOff) == 0)
		    			 value = "="+VLHIV;	
		    		 
		    		 testResult.setValue(value);
		    		 testResult.setTestDate(Utils.parseEnglishAccessDate(rnaAnalysisDate));
	   		 	}
    		}
    	}
    }
    
    private void handleEvent()
    {
        int CPatientId = Utils.findColumn(adeTable, "ID_Coorte");
        int CStartDate = Utils.findColumn(adeTable, "DataDiagnosi");
        int CAde = Utils.findColumn(adeTable, "ADE");
        
        NominalEvent aidsDefiningIllnessA = new NominalEvent("Aids defining illness", adeMappingTable, Utils.selectEvent("Aids defining illness", regadbEvents));
        
        for(int i = 1; i < adeTable.numRows(); i++) 
        {
            String patientId = adeTable.valueAt(CPatientId, i);
            String startDate = adeTable.valueAt(CStartDate, i);
            String ade = adeTable.valueAt(CAde, i);
            
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
    
    private void handleAMarkerData()
    {
        Map<String, Test> coinfection = new HashMap<String, Test>();
        
        //TODO
        //agree on naming
        coinfection.put("HCVAb",createCoinfectionTest("HCVAb", "HCVAb (generic)"));
        coinfection.put("HBeAg",createCoinfectionTest("HBeAg", "HBeAg (generic)"));
        coinfection.put("HBcAb",createCoinfectionTest("HBcAb", "HBcAb (generic)"));
        coinfection.put("HBsAb",createCoinfectionTest("HBsAb", "HBsAb (generic)"));
        coinfection.put("HBsAg",createCoinfectionTest("HBsAg", "HBsAg (generic)"));
    	
    	int CPatientId = Utils.findColumn(aMarkersTable, "ID_Coorte");
        int Cdate = Utils.findColumn(aMarkersTable, "Data_Esame");
        int CMethod = Utils.findColumn(aMarkersTable, "Test");
        int CResult = Utils.findColumn(aMarkersTable, "Esito");
        
        for(int i = 1; i < aMarkersTable.numRows(); i++) 
        {
            String patientId = aMarkersTable.valueAt(CPatientId, i);
            String date = aMarkersTable.valueAt(Cdate, i);
            String method = aMarkersTable.valueAt(CMethod, i);
            String result = aMarkersTable.valueAt(CResult, i);
            
            Patient p = patientMap.get(patientId);
    		
    		if(p == null)
    		{
    			ConsoleLogger.getInstance().logWarning("No 4a marker patient with id "+patientId+" found.");
    		}
    		else
    		{
    			if(Utils.checkColumnValueForEmptiness("marker test method", method, i, patientId) && Utils.checkColumnValueForEmptiness("date of marker test", date, i, patientId))
    			{
    				if(Utils.checkColumnValueForExistance("marker test result", result, i, patientId))
    				{
	    				TestResult tr = p.createTestResult(coinfection.get(method));
		    			tr.setTestDate(Utils.parseEnglishAccessDate(date));
		    			tr.setValue(result);
    				}
    			}
    		}
        }
    }
    
    private void handleBMarkerData()
    {
        Map<String, Test> coinfection = new HashMap<String, Test>();
        
        coinfection.put("HCV-RNA",createCoinfectionTest("HCVRNA", "HCVRNA (generic)"));
        coinfection.put("HBVDNA",createCoinfectionTest("HBVDNA", "HBVDNA (generic)"));
    	
    	int CPatientId = Utils.findColumn(bMarkersTable, "ID_Coorte");
        int Cdate = Utils.findColumn(bMarkersTable, "Data_Esame");
        int CMethod = Utils.findColumn(bMarkersTable, "Test");
        int CResult = Utils.findColumn(bMarkersTable, "Esito");
        int CValue = Utils.findColumn(bMarkersTable, "Valore");
        //int CCutOff = Utils.findColumn(bMarkersTable, "CutOff");
        
        for(int i = 1; i < bMarkersTable.numRows(); i++) 
        {
            String patientId = bMarkersTable.valueAt(CPatientId, i);
            String date = bMarkersTable.valueAt(Cdate, i);
            String method = bMarkersTable.valueAt(CMethod, i);
            String result = bMarkersTable.valueAt(CResult, i);
            String value = bMarkersTable.valueAt(CValue, i);
            //TODO: Clarify with Guiseppe -> Dagstuhl
            //String cutoff = bMarkersTable.valueAt(CCutOff, i);
            
            Patient p = patientMap.get(patientId);
    		
    		if(p == null)
    		{
    			ConsoleLogger.getInstance().logWarning("No 4b marker patient with id "+patientId+" found.");
    		}
    		else
    		{
    			if(Utils.checkColumnValueForEmptiness("marker test method", method, i, patientId) && Utils.checkColumnValueForEmptiness("date of marker test", date, i, patientId))
    			{
    				if(Utils.checkColumnValueForExistance("marker test result", result, i, patientId))
    				{
	    				TestResult tr = p.createTestResult(coinfection.get(method));
		    			tr.setTestDate(Utils.parseEnglishAccessDate(date));
		    			
		    			if(Utils.checkColumnValueForExistance("marker test result value", value, i, patientId))
		    				tr.setValue(result+"("+value+")");
		    			else
		    				tr.setValue(result);
    				}
    			}
    		}
        }
    }
    
    private Test createCoinfectionTest(String testTypeDescription, String testDescription) {
        TestType tt = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), testTypeDescription, new TreeSet<TestNominalValue>());
        Test tst = new Test(tt,testDescription);
        return tst;
    }
    
    private void handleTherapies()
    {
    	int CPatientId = Utils.findColumn(this.hivTherapyTable, "ID_Coorte");
    	int CStartTherapy = Utils.findColumn(this.hivTherapyTable, "Start");
    	int CStopTherapy = Utils.findColumn(this.hivTherapyTable, "End");
    	int CStopReasonTherapy = Utils.findColumn(this.hivTherapyTable, "MotivoSospensione");
  	
    	int CStatus = Utils.findColumn(this.hivTherapyTable, "Status");
    	
        for(int i = CStatus+1; i < this.hivTherapyTable.numColumns()-1; i++) 
        {
            String drug = this.hivTherapyTable.valueAt(i, 0);
            
            Utils.checkDrugsWithRepos(drug, regaDrugGenerics, mappings);
        }
    	    	
    	for(int i = 1; i < this.hivTherapyTable.numRows(); i++)
    	{
	    	String patientID = this.hivTherapyTable.valueAt(CPatientId, i);
			String startTherapy = this.hivTherapyTable.valueAt(CStartTherapy, i);
			String stopTherapy = this.hivTherapyTable.valueAt(CStopTherapy, i);
			String stopReasonTherapy = this.hivTherapyTable.valueAt(CStopReasonTherapy, i);
			
			if(!"".equals(patientID))
            {
        		ArrayList<String> drugs = new ArrayList<String>();
        		Date startDate = null;
        		Date stopDate = null;
        		
        		if(Utils.checkColumnValueForEmptiness("start date of therapy", startTherapy, i, patientID))
        		{
        			startDate = Utils.parseEnglishAccessDate(startTherapy);
        		}
        		
        		for(int j = CStatus+1; j < this.hivTherapyTable.numColumns()-1; j++) 
                {
                    String drugValue = this.hivTherapyTable.valueAt(j, i);
                    
            		if(Utils.checkColumnValueForEmptiness("unknown drug value", drugValue, i, patientID) && Utils.checkDrugValue(drugValue, i, patientID))
            		{
            			String drugName = this.hivTherapyTable.getColumn(j).get(0);
            			
            			//ConsoleLogger.getInstance().logInfo("Found drug value ("+patientID+"): "+drugName);
            			
            			drugs.add(drugName.toUpperCase());
            		} 
                }
        		
        		ArrayList<DrugGeneric> genDrugs = evaluateDrugs(drugs);
        		
        		if(Utils.checkColumnValueForExistance("stop date of therapy", stopTherapy, i, patientID))
        		{
        			stopDate = Utils.parseEnglishAccessDate(stopTherapy);
        		}
        		
        		if(Utils.checkColumnValueForExistance("motivation of stopping therapy", stopReasonTherapy, i, patientID))
        		{
        			if(!stopTherapyTranslation.containsKey(stopReasonTherapy))
        			{
        				stopReasonTherapy = null;
        			
        				//ConsoleLogger.getInstance().logWarning("No applicable HIV motivation found.");
        			}
        			else
        				stopReasonTherapy = stopTherapyTranslation.get(stopReasonTherapy);
        		}
        		
        		if(patientID != null)
        		{
        			storeTherapy(patientID, startDate, stopDate, genDrugs, stopReasonTherapy);
        		}
            }
        	else
        	{
        		ConsoleLogger.getInstance().logWarning("No patient with id "+patientID+" found.");
        	}
    	}
    }
    
    @SuppressWarnings("deprecation")
	private void storeTherapy(String patientId, Date startDate, Date endDate, ArrayList<DrugGeneric> medicinsList, String motivation) 
    {
    	Patient p = patientMap.get(patientId);

    	if (p == null)
    	{
    		ConsoleLogger.getInstance().logWarning("No patient with id "+patientId+" found.");
    		
    		return;
    	}
    	
    	if(medicinsList == null)
    	{
    		ConsoleLogger.getInstance().logWarning("Something wrong with therapy mapping for patient '" + patientId + "': No valid drugs found...Storing anyway!");
    	}

    	if(startDate != null && endDate != null)
    	{
    		if(startDate.equals(endDate))
    		{
    			if(medicinsList != null)
    	    	{
    		    	for (int i = 0; i < medicinsList.size(); i++) 
    		    	{
    		    		DrugGeneric drug = medicinsList.get(i);
    		    		
    		    		if(drug.getGenericId().equals("Unknown"))
    		    		{
    		    		}
    		    		else
    		    		{
    		    			ConsoleLogger.getInstance().logWarning("Something wrong with treatment dates for patient '" + patientId + "': Therapy start " + startDate.toLocaleString() + " -  Therapy end " + endDate.toLocaleString() + ": Dates are equal and drug \"UNK\" == 0...Ignoring!");
    		        		
    		    			return;
    		    		}
    		    	}
    	    	}
    		}
    		
	    	if(startDate.after(endDate))
	    	{
	    		ConsoleLogger.getInstance().logWarning("Something wrong with treatment dates for patient '" + patientId + "': Therapy start " + startDate.toLocaleString() + " -  Therapy end " + endDate.toLocaleString() + ": End date is in the past.");
	    			
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
    	
    	if(medicinsList != null)
    	{
	    	for (int i = 0; i < medicinsList.size(); i++) 
	    	{
	    		TherapyGeneric tg = new TherapyGeneric(new TherapyGenericId(t, (DrugGeneric)medicinsList.get(i)), 
	    		                                        1.0, 
	    		                                        false,
	    		                                        false, 
	    		                                        (long)Frequency.DAYS.getSeconds());
	    		t.getTherapyGenerics().add(tg);
	    	}
    	}
    	
    	if(motivation != null && !motivation.equals(""))
    	{
    		//TODO
    	    //Needs improvement, requires the mapping of motivation
    		TherapyMotivation therapyMotivation = new TherapyMotivation("Toxicity");
    	
    		t.setTherapyMotivation(therapyMotivation);
    	}
    }
    
    private ArrayList<DrugGeneric> evaluateDrugs(ArrayList<String> drugs)
    {
    	ArrayList<DrugGeneric> gDrugs = new ArrayList<DrugGeneric>();
    	
    	for(int i = 0; i < drugs.size(); i++)
    	{
    		String drug = drugs.get(i);
    		
    		if(!"".equals(drug))
    		{
    			DrugGeneric genDrug = getDrugMapping(drug);
    				
    			if(genDrug != null)
    				gDrugs.add(genDrug);
    		}
    	}
    	
    	return gDrugs;
    }
    
    private DrugGeneric getDrugMapping(String drug)
    {
    	boolean foundDrug = false;
    	DrugGeneric genDrug = null;
    	
        for(int j = 0; j < regaDrugGenerics.size(); j++)
    	{
        	genDrug = regaDrugGenerics.get(j);
        	
        	if(genDrug.getGenericId().equals(drug.toUpperCase()))
        	{
        		//ConsoleLogger.getInstance().logInfo("Found drug "+drug.toUpperCase()+" in Rega list");
        		
        		foundDrug = true;
        		
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
            			//ConsoleLogger.getInstance().logInfo("Found drug "+mapping+" after Mapping in Rega list");
            			
            			return genDrug;
                	}
             	}
            }
        }

        return genDrug; 
    }
}
