package net.sf.regadb.io.db.brescia;

import java.lang.Double;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.DrugCommercial;
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
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.NominalEvent;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

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
	private Table sequencesTable;
	
	
	//Translation mapping tables
	private Table countryMappingTable;
	private Table transmissionGroupmappingTable;
	private Table statusMappingTable;
	private Table seroConverterMappingTable;
	private Table stopTherapyReasonMappingTable;
	private Table deathReasonMappingTable;
	private Table adeMappingTable;

	private HashMap<String, Patient> patientMap = new HashMap<String, Patient>();
	
	private List<DrugCommercial> regaDrugCommercials;
	private List<DrugGeneric> regaDrugGenerics;
	
	private List<Attribute> regadbAttributes;
	private List<Event> regadbEvents;
    
    private AttributeGroup regadb = new AttributeGroup("RegaDB");
    private AttributeGroup virolab = new AttributeGroup("ViroLab");
    
    private Mappings mappings;
    
    private TestNominalValue posSeroStatus;
    
    private HashMap<String, Test> tests = new HashMap<String, Test>();
    private ArrayList<TestType> testTypes = new ArrayList<TestType>();
    
    public static void main(String [] args) 
    {
    	//For internal network usage at Leuven
        //System.setProperty("http.proxyHost", "www-proxy");
        //System.setProperty("http.proxyPort", "3128");
        
    	try
    	{
    		//Just for testing purposes...otherwise remove
    		ConsoleLogger.getInstance().setInfoEnabled(true);
    		
    		ImportUNIBS imp = new  ImportUNIBS();
        
    		imp.getData(new File(args[0]), args[1]);
    	}
    	catch(Exception e)
    	{
    		ConsoleLogger.getInstance().logError("Unknown error: "+e.getMessage());
    	}
    }
    
    private void getData(File workingDirectory, String mappingBasePath)
    {
    	try
    	{
    		mappings = Mappings.getInstance(mappingBasePath);
    		
    		ConsoleLogger.getInstance().logInfo("Reading input files...");
    		//Filling DB tables
    		patientTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "Virolabdata_sample_1_Pazienti.sql");
    		deathTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "Virolabdata_sample_7_Decessi.sql");
    		
    		cd4Table = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "Virolabdata_sample_2_CD4.sql");
    		rnaTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "Virolabdata_sample_3_HIVRNA.sql");
    		
    		aMarkersTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "Virolabdata_sample_4a_Markers.sql");
    		bMarkersTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "Virolabdata_sample_4b_Markers.sql");
    		
    		hivTherapyTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "Virolabdata_sample_5_TARV.sql");
    		
    		adeTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "Virolabdata_sample_6_ADEs.sql");
    		
    		sequencesTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "Virolabdata_sample_8_Sequenze.sql");
    		
    		//Filling translation mapping tables
    		countryMappingTable = Utils.readTable(mappingBasePath + File.separatorChar + "county_of_origin.mapping");   		
    		transmissionGroupmappingTable = Utils.readTable(mappingBasePath + File.separatorChar + "transmission_group.mapping");
    		statusMappingTable = Utils.readTable(mappingBasePath + File.separatorChar + "status.mapping");
    		seroConverterMappingTable = Utils.readTable(mappingBasePath + File.separatorChar + "seroconverter.mapping");
    		stopTherapyReasonMappingTable = Utils.readTable(mappingBasePath + File.separatorChar + "stop_therapy_reason.mapping");  
    		deathReasonMappingTable = Utils.readTable(mappingBasePath + File.separatorChar + "death_reason.mapping");  
    		adeMappingTable = Utils.readTable(mappingBasePath + File.separatorChar + "aids_defining_illness.mapping"); 
    		
    		ConsoleLogger.getInstance().logInfo("Retrieving attributes, drugs, and events...");
    		regadbAttributes = Utils.prepareRegaDBAttributes();
    		regaDrugCommercials = Utils.prepareRegaDrugCommercials();
    		regaDrugGenerics = Utils.prepareRegaDrugGenerics();
    		//TODO: Wait for file
    		//regadbEvents = Utils.prepareRegaDBEvents();
    		
    		posSeroStatus = Utils.getNominalValue(StandardObjects.getHivSeroStatusTestType(), "Positive");
    		
    		ConsoleLogger.getInstance().logInfo("Migrating patient information...");
    		handlePatientData();
    		ConsoleLogger.getInstance().logInfo("Migrating CD data...");
    		handleViralData();
    		ConsoleLogger.getInstance().logInfo("Migrating 4a marker data...");
    		handleAMarkerData();
    		ConsoleLogger.getInstance().logInfo("Migrating 4b marker data...");
    		handleBMarkerData();
    		/*ConsoleLogger.getInstance().logInfo("Migrating ADE data...");
    		handleEvent();*/
    		ConsoleLogger.getInstance().logInfo("Migrating treatments...");
    		handleTherapies();
    		ConsoleLogger.getInstance().logInfo("Migrating viral isolates...");
    		HashMap<String, ViralIsolate> viralisolates = handleSequences();
    		
    		ConsoleLogger.getInstance().logInfo("Generating output xml file...");
    		Utils.exportPatientsXML(patientMap, workingDirectory.getAbsolutePath() + File.separatorChar + "unibs_patients.xml");
    		Utils.exportNTXML(viralisolates, workingDirectory.getAbsolutePath() + File.separatorChar + "unibs_ntseq.xml");
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
    	
    	int CdeathPatientID = Utils.findColumn(this.deathTable, "ID_Coorte");
     	int CdeathDate = Utils.findColumn(this.deathTable, "DataDecesso");
     	int CdeathReason = Utils.findColumn(this.deathTable, "MotivoDecesso");
    	
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
            		p.setBirthDate(Utils.parseEnglishAccessDate(birthDate));
            	}
            	
            	if(Utils.checkColumnValue(nationality, i, patientId))
            	{
            		Utils.handlePatientAttributeValue(originA, nationality, p);
            	}
            	
            	if(Utils.checkColumnValue(firstTest, i, patientId))
            	{
            		TestResult t = p.createTestResult(StandardObjects.getGenericHivSeroStatusTest());
                    t.setTestNominalValue(posSeroStatus);
                    t.setTestDate(Utils.parseEnglishAccessDate(firstTest));
            	}
            	
            	if(Utils.checkColumnValue(riskGroup, i, patientId))
            	{
            		Utils.handlePatientAttributeValue(transmissionGroupA, riskGroup, p);
            	}
            	
            	if(Utils.checkColumnValue(lastTest, i, patientId))
            	{
            		TestResult t = p.createTestResult(hivTest);
                    t.setValue("Last date of follow-up available");
                    t.setTestDate(Utils.parseEnglishAccessDate(lastTest));
            	}
            	
            	if(Utils.checkColumnValue(status, i, patientId))
            	{
            		Utils.handlePatientAttributeValue(statusA, status, p);
            	}
            	
            	if(Utils.checkColumnValue(seroConverter, i, patientId))
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
    			if(Utils.checkColumnValue(deathDate, i, deathPatientID))
            	{
            		p.setDeathDate(Utils.parseEnglishAccessDate(deathDate));
            	}
    			
    			if(Utils.checkColumnValue(deathReason, i, deathPatientID))
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
	    		//CD4
	    		if (Utils.checkColumnValue(cd4Count, i, cd4PatientID) && Utils.checkCDValue(cd4Count, i, cd4PatientID)) 
	    		{
	                TestResult t = p.createTestResult(StandardObjects.getGenericCD4Test());
	                t.setValue(cd4Count);
	                t.setTestDate(Utils.parseEnglishAccessDate(analysisDate));
	    		}
	    		if (Utils.checkColumnValue(cd4Percentage, i, cd4PatientID) && Utils.checkCDValue(cd4Percentage, i, cd4PatientID)) 
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
    		String VLCutOff = this.rnaTable.valueAt(CVLCutOff, i);
    		
    		Patient p = patientMap.get(rnaPatientID);
    		
    		if(p == null)
    		{
    			ConsoleLogger.getInstance().logWarning("No rna patient with id "+rnaPatientID+" found.");
    		}
    		else
    		{
	    		if(Utils.checkColumnValue(VLHIV, i, rnaPatientID) && Utils.checkColumnValue(rnaAnalysisDate, i, rnaPatientID))
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
		    		 
		    		 if(Integer.parseInt(VLCutOff) == 50)
		    			 value = "<";
		    		 else
		    			 value = "=";
		    		 
		    		 value += VLHIV;	
		    		 
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
    			
    			if(Utils.checkColumnValue(startDate, i, patientId))
    			{
    				if(Utils.checkColumnValue(ade, i, patientId))
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
    	createNewTypeAndTest("HCV serology", "HCVAb");
    	createNewTypeAndTest("HBV serology", "HBaAg");
    	createNewTypeAndTest("HBV serology", "HBcAb");
    	createNewTypeAndTest("HBV serology", "HBsAb");
    	
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
    			if(Utils.checkColumnValue(method, i, patientId) && Utils.checkColumnValue(date, i, patientId))
    			{
    				if(Utils.checkColumnValue(result, i, patientId))
    				{
	    				TestResult tr = p.createTestResult(tests.get(method));
		    			tr.setTestDate(Utils.parseEnglishAccessDate(date));
		    			tr.setValue(result);
    				}
    			}
    		}
        }
    }
    
    private void handleBMarkerData()
    {
    	createNewTypeAndTest("HCVRNA", "HCV-RNA");
    	createNewTypeAndTest("HBVDNA", "HBVDNA");
    	
    	int CPatientId = Utils.findColumn(bMarkersTable, "ID_Coorte");
        int Cdate = Utils.findColumn(bMarkersTable, "Data_Esame");
        int CMethod = Utils.findColumn(bMarkersTable, "Test");
        int CResult = Utils.findColumn(bMarkersTable, "Esito");
        int CValue = Utils.findColumn(bMarkersTable, "Valore");
        int CCutOff = Utils.findColumn(bMarkersTable, "CutOff");
        
        for(int i = 1; i < bMarkersTable.numRows(); i++) 
        {
            String patientId = bMarkersTable.valueAt(CPatientId, i);
            String date = bMarkersTable.valueAt(Cdate, i);
            String method = bMarkersTable.valueAt(CMethod, i);
            String result = bMarkersTable.valueAt(CResult, i);
            String value = bMarkersTable.valueAt(CValue, i);
            //TODO: Clarify with Guiseppe
            String cutoff = bMarkersTable.valueAt(CCutOff, i);
            
            Patient p = patientMap.get(patientId);
    		
    		if(p == null)
    		{
    			ConsoleLogger.getInstance().logWarning("No 4b marker patient with id "+patientId+" found.");
    		}
    		else
    		{
    			if(Utils.checkColumnValue(method, i, patientId) && Utils.checkColumnValue(date, i, patientId))
    			{
    				if(Utils.checkColumnValue(result, i, patientId))
    				{
	    				TestResult tr = p.createTestResult(tests.get(method));
		    			tr.setTestDate(Utils.parseEnglishAccessDate(date));
		    			
		    			if(Utils.checkColumnValue(value, i, patientId))
		    				tr.setValue(result+"("+value+")");
		    			else
		    				tr.setValue(result);
    				}
    			}
    		}
        }
    }
    
    private void createNewTypeAndTest(String testtypeDescr, String testDescr){
		TestType tt = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getPatientObject(), testtypeDescr, new TreeSet<TestNominalValue>());
		testTypes.add(tt);
		
		Test tst = new Test(tt,testDescr);
		tests.put(testDescr,tst);
    }
    
    private HashMap<String,ViralIsolate> handleSequences()
    {
    	HashMap<String, ViralIsolate> samvi = new HashMap<String,ViralIsolate>();
    	
    	int CPatientId	= Utils.findColumn(sequencesTable, "ID_Coorte");
    	int CGTDate		= Utils.findColumn(sequencesTable, "DataTest");
    	int CGT			= Utils.findColumn(sequencesTable, "Sequenza");
    	
    	 for(int i = 1; i < sequencesTable.numRows(); i++) 
         {
             String patientId = sequencesTable.valueAt(CPatientId, i);
             String date = sequencesTable.valueAt(CGTDate, i);
             String seq = sequencesTable.valueAt(CGT, i);
    	
	    	Patient p = patientMap.get(patientId);
			
			if(p == null)
			{
				ConsoleLogger.getInstance().logWarning("No sequence patient with id "+patientId+" found.");
			}
			else
			{
				if(Utils.checkColumnValue(seq, i, patientId) && Utils.checkColumnValue(date, i, patientId))
    			{
					Date gtDate = Utils.parseBresciaSeqDate(date);
					
					if(gtDate != null)
					{
		    			Set<NtSequence> seqs;
		    			String sampleid = patientId+gtDate.toString();
		    			ViralIsolate vi = samvi.get(sampleid);
		    			
		    			if(vi == null)
		    			{
		    				vi = p.createViralIsolate();
		    				vi.setSampleDate(gtDate);
		    				vi.setSampleId(sampleid);
		    				seqs = new HashSet<NtSequence>();
		       				vi.setNtSequences(seqs);
		       				
		       				samvi.put(sampleid,vi);
		    			}
		    			else
		    			{
		    				if(gtDate.before(vi.getSampleDate()))
		    					vi.setSampleDate(gtDate);
		    			}
		    				
		    			seqs = vi.getNtSequences();
						NtSequence ntSeq = new NtSequence();
						ntSeq.setNucleotides(parseNucleotides(seq, patientId));
						ntSeq.setLabel("Sequence1");
						ntSeq.setSequenceDate(gtDate);
							
						seqs.add(ntSeq);
					}
		    		else
		    		{
		    			ConsoleLogger.getInstance().logWarning("Invalid date specified in the viral isolate file ("+ i +").");
		    		}
    			}
			}
         }
    	
    	 return samvi;
    }
    
    private String parseNucleotides(String nucleotides, String patientID)
    {
    	int index = nucleotides.indexOf("D0");
    	
    	if(index != -1)
    	{
    		String tempSeq = nucleotides.substring(index+2, nucleotides.length());
    		
    		ConsoleLogger.getInstance().logInfo("TempSeq: "+tempSeq);
    		
    		nucleotides = Utils.clearNucleotides(tempSeq);
    		
    		ConsoleLogger.getInstance().logInfo("Cleared Seq: "+nucleotides.toLowerCase());
    	}
    	else
		{
			ConsoleLogger.getInstance().logWarning("Could not determine sequence for patient "+patientID+" from string "+nucleotides);
		}
    	
    	return nucleotides.toLowerCase();
    }
    
    private void handleTherapies()
    {
    	
    }
    
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
    	
    	for (int i = 0; i < medicinsList.size(); i++) 
    	{
    		TherapyCommercial tc = new TherapyCommercial(new TherapyCommercialId(t, medicinsList.get(i)));
    		t.getTherapyCommercials().add(tc);
    	}
    	
    	if(motivation != null && !motivation.equals(""))
    	{
    		//Needs improvement
    		TherapyMotivation therapyMotivation = new TherapyMotivation("Toxicity");
    	
    		t.setTherapyMotivation(therapyMotivation);
    	}
    }
}
