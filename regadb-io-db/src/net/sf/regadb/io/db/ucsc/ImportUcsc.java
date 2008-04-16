package net.sf.regadb.io.db.ucsc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.db.util.msaccess.AccessToCsv;
import net.sf.regadb.io.util.StandardObjects;

public class ImportUcsc 
{
	//DB table names
	private String patientTableName = "t pazienti";
	private String analysisTableName = "t_analisi";
	private String hivTherapyTableName = "t terapie anti hiv";
	private String sequencesTableName = "t genotipo hiv";
	
	//DB tables
	private Table patientTable;
	private Table analysisTable;
	private Table hivTherapyTable;
	private Table sequencesTable;
	
	//Translation mapping tables
	private Table countryTable;
	private Table birthPlaceTable;
	private Table riskGroupTable;
	private Table stopTherapieDescTable;
	
	private HashMap<String, String> tableSelections = new HashMap<String, String>();
	
	private HashMap<String, String> stopTherapyTranslation;
	
	private HashMap<String, Patient> patientMap = new HashMap<String, Patient>();
	private HashMap<String, ViralIsolate> viralIsolateHM = new HashMap<String, ViralIsolate>();
	
	private List<DrugCommercial> regaDrugCommercials;
	private List<DrugGeneric> regaDrugGenerics;
    
	private List<Attribute> regadbAttributes;
	
	private Mappings mappings;
	
    private AttributeGroup regadb = new AttributeGroup("RegaDB");
    private AttributeGroup virolab = new AttributeGroup("Virolab");
	
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
    		tableSelections.put(analysisTableName, "SELECT * FROM `"+analysisTableName+"` WHERE t_analisi.[desc_risultato] = 'HIV-RNA' OR t_analisi.[desc_risultato] = 'cutoff' OR t_analisi.[desc_risultato] = 'CD8 (킠)' OR t_analisi.[desc_risultato] = 'CD8 (%)' OR t_analisi.[desc_risultato] = 'CD4 (킠)' OR t_analisi.[desc_risultato] = 'CD4 (%)' ORDER BY t_analisi.[cartella_ucsc], t_analisi.[data_analisi]");
    		tableSelections.put(hivTherapyTableName, "SELECT * FROM `"+hivTherapyTableName+"`");
    		tableSelections.put(sequencesTableName, "SELECT * FROM `"+sequencesTableName+"`");
    		
    		AccessToCsv a2c = new AccessToCsv();
            a2c.createCsv(new File(databaseFile), workingDirectory, tableSelections);
    		
    		ConsoleLogger.getInstance().logInfo("Reading CSV files...");
    		//Filling DB tables
    		patientTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + patientTableName + ".csv");
    		analysisTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + analysisTableName + ".csv");
    		hivTherapyTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + hivTherapyTableName + ".csv");
    		sequencesTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + sequencesTableName + ".csv");
    		
    		ConsoleLogger.getInstance().logInfo("Initializing mapping tables...");
    		//Filling translation mapping tables
    		countryTable = Utils.readTable(mappingBasePath + File.separatorChar + "nationality.mapping");
    		birthPlaceTable = Utils.readTable(mappingBasePath + File.separatorChar + "birthplace.mapping");
    		riskGroupTable = Utils.readTable(mappingBasePath + File.separatorChar + "riskgroup.mapping");
    		stopTherapieDescTable = Utils.readTable(mappingBasePath + File.separatorChar + "stop_therapy_reason.mapping");
    		
    		ConsoleLogger.getInstance().logInfo("Retrieving all necessary translations...");
    		stopTherapyTranslation = Utils.translationFileToHashMap(stopTherapieDescTable);
    		
    		ConsoleLogger.getInstance().logInfo("Retrieving attributes and drugs...");
    		regadbAttributes = Utils.prepareRegaDBAttributes();
    		regaDrugCommercials = Utils.prepareRegaDrugCommercials();
    		regaDrugGenerics = Utils.prepareRegaDrugGenerics();
    		
    		ConsoleLogger.getInstance().logInfo("Migrating patient information...");
    		handlePatientData();
    		ConsoleLogger.getInstance().logInfo("Migrating CD data...");
    		handleCDData();
    		ConsoleLogger.getInstance().logInfo("Migrating treatments...");
    		handleTherapies();
    		ConsoleLogger.getInstance().logInfo("Processing sequences...");
    		handleSequences();
    		ConsoleLogger.getInstance().logInfo("Processed "+patientMap.size()+" patient(s).");
    		ConsoleLogger.getInstance().logInfo("Generating output xml file...");
    		Utils.exportPatientsXML(patientMap, workingDirectory.getAbsolutePath() + File.separatorChar + "ucsc_patients.xml");
    		Utils.exportNTXML(viralIsolateHM, workingDirectory.getAbsolutePath() + File.separatorChar + "ucsc_ntseq.xml");
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
    	
    	TestType hivTestType = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), "HIV Test", new TreeSet<TestNominalValue>());
    	Test hivTest = new Test(hivTestType, "HIV Test");
	
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
            	
            	if(Utils.checkColumnValueForEmptiness("birthplace", birthPlace, i, patientId))
            	{
                    Utils.handlePatientAttributeValue(birthplaceA, birthPlace, p);
            	}
            	
            	if(Utils.checkColumnValueForEmptiness("nationality", nationality, i, patientId))
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
            	
            	if(Utils.checkColumnValueForExistance("date of first positive HIV test", firstTest, i, patientId))
            	{
            		TestResult t = p.createTestResult(hivTest);
                    t.setValue("First positive HIV test");
                    t.setTestDate(Utils.parseEnglishAccessDate(firstTest));
            	}
            	
            	if(Utils.checkColumnValueForExistance("date of last negative HIV test", lastTest, i, patientId))
            	{
            		TestResult t = p.createTestResult(hivTest);
                    t.setValue("Last negative HIV test");
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
            	
            	if(Utils.checkColumnValueForExistance("acute syndrom", syndrome, i, patientId) && Utils.checkColumnValueForExistance("date of acute syndrom", syndromeDate, i, patientId))
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
        HashMap<String, Test> uniqueVLTests = new HashMap<String, Test>();
        
    	int CCC4PatientID = Utils.findColumn(this.analysisTable, "cartella_ucsc");
    	int CAnalysisDate = Utils.findColumn(this.analysisTable, "data_analisi");
    	int CMethod = Utils.findColumn(this.analysisTable, "desc_risultato");
    	//int CLabor = Utils.findColumn(this.analysisTable, "desc_laboratorio");
    	int CResult= Utils.findColumn(this.analysisTable, "risul_num");
    	
    	TestType cd4PercTestType = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), "CD4 Percentage", new TreeSet<TestNominalValue>());
    	Test cd4PercTest = new Test(cd4PercTestType, "CD4 Percentage (generic)");
    	
    	TestType cd8TestType = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), "CD8 Count", new TreeSet<TestNominalValue>());
    	Test cd8Test = new Test(cd8TestType, "CD8 Count (generic)");
    	
    	TestType cd8PercTestType = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), "CD8 Percentage", new TreeSet<TestNominalValue>());
    	Test cd8PercTest = new Test(cd8PercTestType, "CD8 Percentage (generic)");
    	
    	for(int i = 1; i < this.analysisTable.numRows(); i++)
    	{
    		String patientID = this.analysisTable.valueAt(CCC4PatientID, i);
    		String analysisDate = this.analysisTable.valueAt(CAnalysisDate, i);
    		String method = this.analysisTable.valueAt(CMethod, i);
    		//String labor = this.analysisTable.valueAt(CLabor, i);
    		String result = this.analysisTable.valueAt(CResult, i);
    		
    		Patient p = patientMap.get(patientID);
    		
    		if(p == null)
    			ConsoleLogger.getInstance().logWarning("No patient with id "+patientID+" found.");
    			
    		//CD4
    		if(method.equals("CD4 (킠)"))
    		{
	    		if (Utils.checkColumnValueForEmptiness("CD4 test result (킠)", result, i, patientID) && Utils.checkCDValue(result, i, patientID)) 
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
	                TestResult t = p.createTestResult(cd4PercTest);
	                t.setValue(result.replace(',', '.'));
	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
	    		}
    		}
    		 
    		//CD8
    		if(method.equals("CD8 (킠)"))
    		{
	    		if (Utils.checkColumnValueForEmptiness("CD8 test result (킠)", result, i, patientID) && Utils.checkCDValue(result, i, patientID)) 
	    		{
	                TestResult t = p.createTestResult(cd8Test);
	                t.setValue(result.replace(',', '.'));
	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
	    		}
    		}
    		if(method.equals("CD8 (%)"))
    		{
	    		if (Utils.checkColumnValueForEmptiness("CD8 test result (%)", result, i, patientID) && Utils.checkCDValue(result, i, patientID)) 
	    		{
	                TestResult t = p.createTestResult(cd8PercTest);
	                t.setValue(result.replace(',', '.'));
	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
	    		}
    		}
    		 
    		if(method.equals("HIV-RNA"))
    		{
	    		if(Utils.checkColumnValueForEmptiness("HIV RNA test result", result, i, patientID) && Utils.checkColumnValueForEmptiness("date ofHIV RNA test result", analysisDate, i, patientID))
	    		{
	    			 try
	    			 {
			    		 TestResult t = p.createTestResult(StandardObjects.getGenericViralLoadTest());

			    		 String value = null;
			    		 
			    		 if(Double.parseDouble(result) <= 50)
			    			 value = "<";
			    		 else
			    			 value = "=";
			    		 
			    		 value += result;
			    		 
			    		 t.setValue(value.replace(',', '.'));
			    		 t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
	    			 }
	    			 catch(Exception e)
	    			 {
	    				 
	    			 }
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
    	
        HashMap<Integer, String> drugPositions = new HashMap<Integer, String>();
        
        for(int i = ChivCommercialDrug+1; i < this.hivTherapyTable.numColumns(); i++) 
        {
            String drug = this.hivTherapyTable.valueAt(i, 0);
            
            Utils.checkDrugsWithRepos(drug, regaDrugGenerics, mappings);
            
            drugPositions.put(i, drug);
        }
    	    	
    	for(int i = 1; i < this.hivTherapyTable.numRows(); i++)
    	{
    		String hivPatientID = this.hivTherapyTable.valueAt(ChivPatientID, i);
    		String hivStartTherapy = this.hivTherapyTable.valueAt(ChivStartTherapy, i);
    		String hivStopTherapy = this.hivTherapyTable.valueAt(ChivStopTherapy, i);
    		//String hivLineTherapy = this.hivTherapyTable.valueAt(ChivLineTherapy, i);
    		//String hivSuccessTherapy = this.hivTherapyTable.valueAt(ChivSuccessTherapy, i);
    		String hivStopReasonTherapy = this.hivTherapyTable.valueAt(ChivStopReasonTherapy, i);
    		String hivCommercialDrug = this.hivTherapyTable.valueAt(ChivCommercialDrug, i);
            
        	if(!"".equals(hivPatientID))
            {
        		ArrayList<String> drugs = new ArrayList<String>();
        		Date startDate = null;
        		Date stopDate = null;
        		
        		if(Utils.checkColumnValueForEmptiness("start date of therapy", hivStartTherapy, i, hivPatientID))
        		{
        			startDate = Utils.parseEnglishAccessDate(hivStartTherapy);
        		}
        		
                for(Map.Entry<Integer, String> entry : drugPositions.entrySet()) 
                {
                    String drugValue = this.hivTherapyTable.valueAt(entry.getKey(), i);
                    
            		if(Utils.checkColumnValueForEmptiness("unknown drug value", drugValue, i, hivPatientID) && Utils.checkDrugValue(drugValue, i, hivPatientID))
            		{
            			drugs.add(entry.getValue());
            		} 
                }
        		
        		ArrayList<DrugCommercial> comDrugs = evaluateDrugs(hivCommercialDrug.toLowerCase(), drugs, hivPatientID, i);
        		
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
        			
        				ConsoleLogger.getInstance().logWarning("No applicable HIV motivation found.");
        			}
        			else
        				hivStopReasonTherapy = stopTherapyTranslation.get(hivStopReasonTherapy);
        		}
        		
        		if(hivPatientID != null)
        		{
        			storeTherapy(hivPatientID, startDate, stopDate, comDrugs, hivStopReasonTherapy);
        		}
            }
        	else
        	{
        		ConsoleLogger.getInstance().logWarning("No patient with id "+hivPatientID+" found.");
        	}
    	}
    }
    
    private ArrayList<DrugCommercial> evaluateDrugs(String hivCommercialDrug, ArrayList<String> drugs, String patientID, int row)
    {
    	ArrayList<DrugCommercial> comDrugs = new ArrayList<DrugCommercial>();

    	if("".equals(hivCommercialDrug))
    	{
    		ConsoleLogger.getInstance().logWarning("No commercial drug found for patient "+patientID+" at row "+row+".");
    		return null;
    	}
    	String[] split = null;
    	
    	split = hivCommercialDrug.split("\\+");
    	
    	if(split == null || split.length == 0)
    	{
    		split = new String[1];
    		split[0] = hivCommercialDrug.toLowerCase();
    	}
    	
    	for(int i = 0; i < regaDrugCommercials.size(); i++)
    	{
    		DrugCommercial drug = regaDrugCommercials.get(i);
    		
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
		    								//ConsoleLogger.getInstance().logInfo("Drug "+currentDrug+" found.");
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
    
	@SuppressWarnings("deprecation")
	private void storeTherapy(String patientId, Date startDate, Date endDate, ArrayList<DrugCommercial> medicinsList, String motivation) 
    {
    	Patient p = patientMap.get(patientId);

    	if (p == null)
    	{
    		ConsoleLogger.getInstance().logWarning("No patient with id "+patientId+" found.");
    		
    		return;
    	}
    	
    	if(medicinsList == null)
    	{
    		ConsoleLogger.getInstance().logWarning("Something wrong with therapy mapping for patient '" + patientId + "': No valid drugs found.");
			
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
    	
    	for (int i = 0; i < medicinsList.size(); i++) 
    	{
    		TherapyCommercial tc = new TherapyCommercial(new TherapyCommercialId(t, medicinsList.get(i)),false,false);
    		t.getTherapyCommercials().add(tc);
    	}
    	
    	if(motivation != null && !motivation.equals(""))
    	{
    		//TODO: needs improvement
    		TherapyMotivation therapyMotivation = new TherapyMotivation("Toxicity");
    	
    		t.setTherapyMotivation(therapyMotivation);
    	}
    }
    
    private void addViralIsolateToPatients(String patientID, Date date, String seq)
    {
    	Patient p = patientMap.get(patientID);
    	
    	if(p == null)
    	{
    		ConsoleLogger.getInstance().logError("No patient with id "+patientID+" found.");
    	}
    	
    	ViralIsolate vi = p.createViralIsolate();
    	vi.setSampleId(patientID+date.toString());
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
    	int count = 0;
    	
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
             					addViralIsolateToPatients(patientID, date, clearedSequ);
             					
             					count++;
             				}
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
    	
    	ConsoleLogger.getInstance().logInfo("Processed "+count+" sequence(s).");
    }
}
