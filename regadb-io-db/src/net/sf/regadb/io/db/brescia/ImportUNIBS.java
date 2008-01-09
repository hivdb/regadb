package net.sf.regadb.io.db.brescia;

import java.lang.Double;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

public class ImportUNIBS 
{
	//DB tables
	private Table patientTable;
	private Table cd4Table;
	private Table rnaTable;
	private Table hivTherapyTable;
	private Table sequencesTable;
	
	//Translation mapping tables
	private Table nationalityTable;
	private Table riskfactorTable;
	private Table statusTable;
	private Table seroConverterTable;
	
	//Translation Hashmaps
	private HashMap<String, String> nationalityTranslation;
	private HashMap<String, String> riskfactorTranslation;
	private HashMap<String, String> statusTranslation;
	private HashMap<String, String> seroconverterTranslation;

	private HashMap<String, Patient> patientMap = new HashMap<String, Patient>();
	private HashMap<String, ViralIsolate> viralIsolateHM = new HashMap<String, ViralIsolate>();
	
	private List<DrugCommercial> regaDrugCommercials;
	private List<DrugGeneric> regaDrugGenerics;
    
    private AttributeGroup regadb = new AttributeGroup("RegaDB");
    private AttributeGroup virolab = new AttributeGroup("ViroLab");
    
    public static void main(String [] args) 
    {
    	//For internal network usage at Leuven
        System.setProperty("http.proxyHost", "www-proxy");
        System.setProperty("http.proxyPort", "3128");
        
    	try
    	{
    		//Just for testing purposes...otherwise remove
    		ConsoleLogger.getInstance().setInfoEnabled(true);
    		
    		ImportUNIBS imp = new  ImportUNIBS();
        
    		imp.getData(new File(args[0]));
    	}
    	catch(Exception e)
    	{
    		ConsoleLogger.getInstance().logError("Unknown error: "+e.getMessage());
    	}
    }
    
    private void getData(File workingDirectory)
    {
    	try
    	{
    		ConsoleLogger.getInstance().logInfo("Reading input files...");
    		
    		//Filling DB tables
    		patientTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "1_Pazienti.csv");
    		cd4Table = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "2_CD4.csv");
    		rnaTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "3_HIVRNA.csv");
    		hivTherapyTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "5_TARV.csv");
    		sequencesTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "8_Sequenze.csv");
    		
    		//Filling translation mapping tables
    		nationalityTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "nationality.mapping");   		
    		riskfactorTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "riskfactor.mapping");
    		statusTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "status.mapping");
    		seroConverterTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "seroconverter.mapping");
    		
    		ConsoleLogger.getInstance().logInfo("Retrieving all necessary translations...");
    		nationalityTranslation = getNationalityTranslation();
    		riskfactorTranslation = getRiskFactorTranslation();
    		statusTranslation = getStatusTranslation();
    		seroconverterTranslation = getSeroConverterTranslation();
    		
    		/*ConsoleLogger.getInstance().logInfo("Retrieving drugs...");
    		regaDrugCommercials = Utils.prepareRegaDrugCommercials();
    		regaDrugGenerics = Utils.prepareRegaDrugGenerics();*/
    		
    		ConsoleLogger.getInstance().logInfo("Migrating patient information...");
    		handlePatientData();
    		ConsoleLogger.getInstance().logInfo("Migrating CD data...");
    		handleCDData();
    		/*ConsoleLogger.getInstance().logInfo("Migrating treatments...");
    		handleTherapies();
    		ConsoleLogger.getInstance().logInfo("Processing sequences...");
    		handleSequences(workingDirectory);*/
    		ConsoleLogger.getInstance().logInfo("Generating output xml file...");
    		Utils.exportPatientsXML(patientMap, workingDirectory.getAbsolutePath() + File.separatorChar + "unibs_patients.xml");
    		//Utils.exportNTXML(viralIsolateHM, workingDirectory.getAbsolutePath() + File.separatorChar + "unibs_ntseq.xml");
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
    	int CseroConverter = Utils.findColumn(this.patientTable, "sieroconv");
    	
    	 NominalAttribute gender = new NominalAttribute("Gender", Csex, new String[] { "M", "F" },
                 new String[] { "male", "female" } );
         gender.attribute.setAttributeGroup(regadb);
         
         String[] scKeys = Utils.convertKeysToStringArray(seroconverterTranslation.keySet());
         String[] scValues = Utils.convertValuesToStringArray(seroconverterTranslation.values());
         
         NominalAttribute scA = new NominalAttribute("Sero Converter", CseroConverter, scKeys,
                 scValues);
         scA.attribute.setAttributeGroup(virolab);
         
         String[] tgKeys = Utils.convertKeysToStringArray(riskfactorTranslation.keySet());
         String[] tgValues = Utils.convertValuesToStringArray(riskfactorTranslation.values());
         
         NominalAttribute rgA = new NominalAttribute("Transmission group", CriskGroup, tgKeys,
         		tgValues);
         rgA.attribute.setAttributeGroup(regadb);
         
         String[] nKeys = Utils.convertKeysToStringArray(nationalityTranslation.keySet());
         String[] nValues = Utils.convertValuesToStringArray(nationalityTranslation.values());
         
         NominalAttribute nA = new NominalAttribute("Geographic origin", Cnationality, nKeys,
         		nValues);
         nA.attribute.setAttributeGroup(regadb);
         
         String[] sKeys = Utils.convertKeysToStringArray(statusTranslation.keySet());
         String[] sValues = Utils.convertValuesToStringArray(statusTranslation.values());
         
         NominalAttribute sA = new NominalAttribute("Status", Cstatus, sKeys,
                 sValues);
         sA.attribute.setAttributeGroup(virolab);
         
         TestType hivTestType = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), "HIV Test", new TreeSet<TestNominalValue>());
     	 Test hivTest = new Test(hivTestType, "HIV Test");
     	 
     	for(int i = 1; i < this.patientTable.numRows(); i++)
    	{
            String patientId = this.patientTable.valueAt(CpatientID, i);
            String sex = this.patientTable.valueAt(Csex, i);
            String birthDate = this.patientTable.valueAt(CbirthDate, i);
            String nationality = this.patientTable.valueAt(Cnationality, i);
            String firstTest = this.patientTable.valueAt(CfirstTest, i);
            String riskGroup = this.patientTable.valueAt(CriskGroup, i);
            String lastTest = this.patientTable.valueAt(ClastTest, i);
            String status = this.patientTable.valueAt(Cstatus, i);
            String seroConverter = this.patientTable.valueAt(CseroConverter, i);
            
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
                        ConsoleLogger.getInstance().logWarning("Unsupported attribute value (gender): "+sex);
                    }
            	}
            	
            	if(Utils.checkColumnValue(birthDate, i, patientId))
            	{
            		p.setBirthDate(Utils.convertDate(birthDate));
            	}
            	
            	if(Utils.checkColumnValue(nationality, i, patientId))
            	{
            		AttributeNominalValue vv = nA.nominalValueMap.get(nationality.toLowerCase().trim());
                    
                    if (vv != null) 
                    {
                        PatientAttributeValue v = p.createPatientAttributeValue(nA.attribute);
                        v.setAttributeNominalValue(vv);
                    }
                    else 
                    {
                   	 	ConsoleLogger.getInstance().logWarning("Unsupported attribute value (Geographic origin): "+nationality);
                    }	
            	}
            	
            	if(Utils.checkColumnValue(firstTest, i, patientId))
            	{
            		TestResult t = p.createTestResult(hivTest);
                    t.setValue("First HIV-ab positive test");
                    t.setTestDate(Utils.convertDate(firstTest));
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
                    	 ConsoleLogger.getInstance().logWarning("Unsupported attribute value (Transmission group): "+riskGroup);
                     }	
            	}
            	
            	if(Utils.checkColumnValue(lastTest, i, patientId))
            	{
            		TestResult t = p.createTestResult(hivTest);
                    t.setValue("Last negative HIV test");
                    t.setTestDate(Utils.convertDate(lastTest));
            	}
            	
            	if(Utils.checkColumnValue(status, i, patientId))
            	{
            		AttributeNominalValue vv = sA.nominalValueMap.get(status.toLowerCase().trim());
                    
                    if (vv != null) 
                    {
                        PatientAttributeValue v = p.createPatientAttributeValue(sA.attribute);
                        v.setAttributeNominalValue(vv);
                    }
                    else 
                    {
                   	 ConsoleLogger.getInstance().logWarning("Unsupported attribute value (Status): "+status);
                    }
            	}
            	
            	if(Utils.checkColumnValue(seroConverter, i, patientId))
            	{
            		 AttributeNominalValue vv = scA.nominalValueMap.get(seroConverter.toLowerCase().trim());
                     
                     if (vv != null) 
                     {
                         PatientAttributeValue v = p.createPatientAttributeValue(scA.attribute);
                         v.setAttributeNominalValue(vv);
                     }
                     else 
                     {
                    	 ConsoleLogger.getInstance().logWarning("Unsupported attribute value (Seroconverter): "+seroConverter);
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
        
    	int Ccd4PatientID = Utils.findColumn(this.cd4Table, "ID_Coorte");
    	int Ccd4AnalysisDate = Utils.findColumn(this.cd4Table, "Data_Esame");
    	int Ccd4Count = Utils.findColumn(this.cd4Table, "CD4+");
    	int Ccd4Percentage = Utils.findColumn(this.cd4Table, "CD4%");

    	int CrnaPatientID = Utils.findColumn(this.rnaTable, "ID_Coorte");
    	int CrnaAnalysisDate = Utils.findColumn(this.rnaTable, "Data_Esame");
    	int CVLTest = Utils.findColumn(this.rnaTable, "Metodica");
    	int CVLHIV = Utils.findColumn(this.rnaTable, "HIV RNA");
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
    	}
    	
    	for(int i = 1; i < this.rnaTable.numRows(); i++)
    	{
    		String rnaPatientID = this.rnaTable.valueAt(CrnaPatientID, i);
    		String rnaAnalysisDate = this.rnaTable.valueAt(CrnaAnalysisDate, i);;
    		String VLTest = this.rnaTable.valueAt(CVLTest, i);
    		String VLHIV = this.rnaTable.valueAt(CVLHIV, i);
    		String VLCutOff = this.rnaTable.valueAt(CVLCutOff, i);
    		
    		Patient p = patientMap.get(rnaPatientID);
    		
    		if(p == null)
    			ConsoleLogger.getInstance().logWarning("No patient with id "+rnaPatientID+" found.");
    		
    		if(Utils.checkColumnValue(VLHIV, i, rnaPatientID))
   		 	{
	    		 TestResult testResult = null;
	    		 
	    		 if("".equals(VLTest))
	    		 {
	    			 testResult = p.createTestResult(StandardObjects.getGenericViralLoadTest());
	    		 }
	    		 else
	    		 {
	    			 Test vlT = uniqueVLTests.get(VLTest);
                     
                     if(vlT==null) 
                     {
                         vlT = new Test(StandardObjects.getViralLoadTestType(), VLTest);
                         uniqueVLTests.put(VLTest, vlT);
                     }
	    			 
	    			 testResult = p.createTestResult(vlT);
	    		 }
	    		 
	    		 String value = null;
	    		 
	    		 if(Double.parseDouble(VLHIV) <= Integer.parseInt(VLCutOff))
	    			 value = "<";
	    		 else
	    			 value = "=";
	    		 
	    		 value += VLHIV;
	    		 
	    		 testResult.setValue(value);
	    		 testResult.setTestDate(Utils.convertDate(rnaAnalysisDate));
   		 	}
    	}
    }
    
    private HashMap<String, String> getNationalityTranslation()
    {
    	 int italianIndex = Utils.findColumn(this.nationalityTable, "nazionalita");
    	 int englishIndex = Utils.findColumn(this.nationalityTable, "Nationality");
    	 
    	 HashMap<String, String> values = new HashMap<String, String>();
    	 
    	 for(int i = 1; i < this.nationalityTable.numRows(); i++)
     	 {
             String italianvalue = this.nationalityTable.valueAt(italianIndex, i);
             String englishvalue = this.nationalityTable.valueAt(englishIndex, i);
             
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
    
    private HashMap<String, String> getRiskFactorTranslation()
    {
    	 int italianIndex = Utils.findColumn(this.riskfactorTable, "FR");
    	 int englishIndex = Utils.findColumn(this.riskfactorTable, "Risk Factor");
    	 
    	 HashMap<String, String> values = new HashMap<String, String>();
    	 
    	 for(int i = 1; i < this.riskfactorTable.numRows(); i++)
     	 {
             String italianvalue = this.riskfactorTable.valueAt(italianIndex, i);
             String englishvalue = this.riskfactorTable.valueAt(englishIndex, i);
             
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
    
    private HashMap<String, String> getStatusTranslation()
    {
    	 int italianIndex = Utils.findColumn(this.statusTable, "status");
    	 int englishIndex = Utils.findColumn(this.statusTable, "Status");
    	 
    	 HashMap<String, String> values = new HashMap<String, String>();
    	 
    	 for(int i = 1; i < this.statusTable.numRows(); i++)
     	 {
             String italianvalue = this.statusTable.valueAt(italianIndex, i);
             String englishvalue = this.statusTable.valueAt(englishIndex, i);
             
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
    
    private HashMap<String, String> getSeroConverterTranslation()
    {
    	 int italianIndex = Utils.findColumn(this.seroConverterTable, "sieroconv");
    	 int englishIndex = Utils.findColumn(this.seroConverterTable, "Seroconverter");
    	 
    	 HashMap<String, String> values = new HashMap<String, String>();
    	 
    	 for(int i = 1; i < this.seroConverterTable.numRows(); i++)
     	 {
             String italianvalue = this.seroConverterTable.valueAt(italianIndex, i);
             String englishvalue = this.seroConverterTable.valueAt(englishIndex, i);
             
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
