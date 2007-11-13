package net.sf.regadb.io.db.ucsc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import net.sf.regadb.align.Aligner;
import net.sf.regadb.align.local.LocalAlignmentService;
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
import net.sf.regadb.io.db.drugs.ImportDrugsFromCentralRepos;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.exportXML.ExportToXML;
import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.FileProvider;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.regadb.util.date.DateUtils;
//import net.spy.translate.remote.rpcstyle.TranslateServiceLocator;
//import net.spy.translate.remote.types.TranslateRequestBean;

import org.apache.commons.io.FileUtils;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ImportUcsc 
{
	private Table patientTable;
	private Table cd4Table;
	private Table hivTherapyTable;
	private Table riskGroupTable;
	private Table stopTherapieDescTable;
	private HashMap<String, Patient> patientMap = new HashMap<String, Patient>();
	
	private HashMap<String, String> riskGroupTranslation;
	private HashMap<String, String> stopTherapyTranslation;
	
	private List<DrugCommercial> dcs;
    
    private AttributeGroup regadb = new AttributeGroup("RegaDB");
    private AttributeGroup virolab = new AttributeGroup("ViroLab");
	
    public static void main(String [] args) {
        ImportUcsc imp = new  ImportUcsc();
        
        imp.getData(new File(args[0]));
    }
    
    public void run(File workingDirectory) {
        try {
            getSequences(workingDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void getData(File workingDirectory)
    {
    	try
    	{
    		List<Attribute> regadbAttributesList = prepareRegaDBAttributes(workingDirectory);
    		prepareRegaDBDrugs();
    		
    		patientTable = readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "dbo_T_pazienti.csv");
    		cd4Table = readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "dbo_T analisi HIV RNA CD4_CD8.csv");
    		hivTherapyTable = readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "dbo_T terapie anti HIV.csv");
    		riskGroupTable = readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "dbo_T elenco fattori di rischio.csv");
    		stopTherapieDescTable = readTable(workingDirectory.getAbsolutePath() + File.separatorChar + "dbo_T elenco motivo stop terapia anti HIV.csv");
    		
    		//Get the translations
    		riskGroupTranslation = getRiskGroupTranslation();
    		stopTherapyTranslation = getStopTherapyTranslation();
    		
    		handlePatientData(regadbAttributesList);
    		handleCD4Data();
    		handleTherapies();
    		
    		getSequences(workingDirectory);
    		
    		exportXML(workingDirectory.getAbsolutePath() + File.separatorChar + "ucsc_patients.xml");
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    private void handlePatientData(List<Attribute> regadbAttributesList)
    {
    	int CpatientID = findColumn(this.patientTable, "cartella UCSC");
    	int Csex = findColumn(this.patientTable, "sesso");
    	int CbirthDate = findColumn(this.patientTable, "data di nascita");
    	int CbirthPlace = findColumn(this.patientTable, "luogo di nascita");
    	int Cnationality = findColumn(this.patientTable, "nazionalit�");
    	int CriskGroup = findColumn(this.patientTable, "fattore di rischio");
    	int CseroConverter = findColumn(this.patientTable, "seroconverter");
    	int CfirstTest = findColumn(this.patientTable, "data primo test HIV positivo");
    	int ClastTest = findColumn(this.patientTable, "data ultimo test HIV negativo");
    	int Cdeath = findColumn(this.patientTable, "decesso");
    	int CdeathDate = findColumn(this.patientTable, "data decesso");
    	int CdeathReason = findColumn(this.patientTable, "descrizione decesso");
    	int Csyndrome = findColumn(this.patientTable, "sindrome acuta"); 
    	int CsyndromeDate = findColumn(this.patientTable, "data sindrome acuta");
    	int Ccdc = findColumn(this.patientTable, "CDC");
    	
    	/*Attribute seroAttr = new Attribute("Sero Converter");
		Attribute deathAttr = new Attribute("Death");
		Attribute synAttr = new Attribute("Acute Syndrome");
		Attribute firstVisitAttr = new Attribute("Date of first HIV-positive test");
		Attribute lastVisitAttr = new Attribute("Date of last HIV-negative test");
		Attribute synDateAttr = new Attribute("Date of acute syndrome");
		Attribute bPlaceAttr = new Attribute("Place of Birth");*/
	
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
            String death = this.patientTable.valueAt(Cdeath, i);
            String deathDate = this.patientTable.valueAt(CdeathDate, i);
            String deathReason = this.patientTable.valueAt(CdeathReason, i);
            String syndrome = this.patientTable.valueAt(Csyndrome, i);
            String syndromeDate = this.patientTable.valueAt(CsyndromeDate, i);
            String cdc = this.patientTable.valueAt(Ccdc, i);
            
            if(!"".equals(patientId))
            {
            	Patient p = new Patient();
            	p.setPatientId(patientId);
            	
            	if(!"".equals(sex))
            	{
                    NominalAttribute gender = new NominalAttribute("Gender", Csex, new String[] { "M", "F" },
                            new String[] { "male", "female" } );
                    gender.attribute.setAttributeGroup(regadb);
                    
                    AttributeNominalValue vv = gender.nominalValueMap.get(sex.toUpperCase().trim());
                    if (vv != null) {
                        PatientAttributeValue v = p.createPatientAttributeValue(gender.attribute);
                        v.setAttributeNominalValue(vv);
                    }
            	}
            	
            	if(!"".equals(birthDate))
            	{
            		p.setBirthDate(convertDate(birthDate));
            	}
            	
            	//TODO: Comment the two statements out to include the place of birth and the nationality as well...
            	/*if(!"".equals(birthPlace))
            	{
            		//The WS is not very stable - this may cause problems during processing
            		String translatedValue = translate(birthPlace);
            		
            		if(!"".equals(translatedValue))
            		{
            			PatientAttributeValue bPlaceValue = p.createPatientAttributeValue(bPlaceAttr);
            			bPlaceValue.setValue(translatedValue);
            		}
            	}*/
            	
            	/*if(!"".equals(nationality))
            	{
            		Attribute countryOfOrigin = selectAttribute("Country of origin", regadbAttributesList);
            		
            		PatientAttributeValue nationValue = p.createPatientAttributeValue(countryOfOrigin);
            		
            		//The WS is not very stable - this may cause problems during processing
            		String translatedValue = translate(nationality);
            		
            		if(!"".equals(translatedValue))
            		{
            			String bestCountryMatchForNow="";
            	        
            			double score = Double.MIN_VALUE;
            	        
            	        for(AttributeNominalValue anv : countryOfOrigin.getAttributeNominalValues())
            	        {
            	           score = findCountryMatch(translatedValue, anv.getValue());
            	        	
            	           if(score >= 0.70)
            	           {
            	        	   bestCountryMatchForNow = anv.getValue();
            	        	   
            	        	   break;
            	           }
            	        }
            	       
            	        System.out.println("Found match between "+translatedValue+" and "+bestCountryMatchForNow+"");
            	        
            			nationValue.setAttributeNominalValue(new AttributeNominalValue(countryOfOrigin, bestCountryMatchForNow));
            		}
            	}*/
            	
            	/*if(!"".equals(riskGroup))
            	{
            		Attribute tGroup = selectAttribute("Transmission group", regadbAttributesList);
            		
            		PatientAttributeValue tGroupValue = p.createPatientAttributeValue(tGroup);
            		
            		if(riskGroupTranslation.containsKey(riskGroup))
            			tGroupValue.setAttributeNominalValue(new AttributeNominalValue(tGroup, riskGroupTranslation.get(riskGroup)));
            		else
            			System.err.println("Unsupported attribute value (Transmission group): "+riskGroup);
            	}*/
            	
            	/*if(!"".equals(seroConverter))
            	{
            		PatientAttributeValue seroValue = p.createPatientAttributeValue(seroAttr);
            		
            		if("1".equals(seroConverter))
            		{
            			seroValue.setAttributeNominalValue(new AttributeNominalValue(seroAttr, "1"));
            		}
            		else if("0".equals(seroConverter))
            		{
            			seroValue.setAttributeNominalValue(new AttributeNominalValue(seroAttr, "0"));
            		}
            		else
            			System.err.println("Unsupported attribute value (Seroconverter): "+seroConverter);
            	}*/
            	
            	/*if(!"".equals(firstTest))
            	{
            		PatientAttributeValue firstTestValue = p.createPatientAttributeValue(firstVisitAttr);
            		firstTestValue.setValue(DateUtils.getEuropeanFormat(convertDate(firstTest)));
            	}
            	
            	if(!"".equals(lastTest))
            	{
            		PatientAttributeValue lastTestValue = p.createPatientAttributeValue(lastVisitAttr);
            		lastTestValue.setValue(DateUtils.getEuropeanFormat(convertDate(lastTest)));
            	}*/
            	
            	/*if(!"".equals(death))
            	{
            		PatientAttributeValue deathValue = p.createPatientAttributeValue(deathAttr);
            		
            		if("1".equals(death))
            		{
            			deathValue.setAttributeNominalValue(new AttributeNominalValue(deathAttr, "1"));
            		}
            		else if("0".equals(death))
            		{
            			deathValue.setAttributeNominalValue(new AttributeNominalValue(deathAttr, "0"));
            		}
            		else
            			System.err.println("Unsupported attribute value (Death): "+death);
            	}*/
            	
            	if(!"".equals(deathDate))
            	{
            		p.setDeathDate(convertDate(deathDate));
            	}
            	
            	if(!"".equals(deathReason))
            	{
            		//Ask if necessary, to be translated first (Wait for Mattia)
            	}
            	
            	/*if(!"".equals(syndrome))
            	{
            		PatientAttributeValue synValue = p.createPatientAttributeValue(synAttr);
            		
            		if("1".equals(syndrome))
            		{
            			synValue.setAttributeNominalValue(new AttributeNominalValue(synAttr, "1"));
            		}
            		else if("0".equals(syndrome))
            		{
            			synValue.setAttributeNominalValue(new AttributeNominalValue(synAttr, "0"));
            		}
            		else
            			System.err.println("Unsupported attribute value (Syndrome): "+syndrome);
            	}
            	
            	if(!"".equals(syndromeDate))
            	{
            		PatientAttributeValue synDateValue = p.createPatientAttributeValue(synDateAttr);
            		synDateValue.setValue(DateUtils.getEuropeanFormat(convertDate(syndromeDate)));
            	}
            	*/
            	if(!"".equals(cdc))
            	{
                    NominalAttribute cdcA = new NominalAttribute("CDC", Ccdc, new String[] { "A", "B", "C" },
                            new String[] { "A", "B", "C" } );
                    cdcA.attribute.setAttributeGroup(virolab);
                    
                    AttributeNominalValue vv = cdcA.nominalValueMap.get(cdc.toUpperCase().trim());
                    if (vv != null) {
                        PatientAttributeValue v = p.createPatientAttributeValue(cdcA.attribute);
                        v.setAttributeNominalValue(vv);
                    } else {
                        System.err.println("Unsupported attribute value (CDC): "+cdc);
                    }
            	}
            	
            	patientMap.put(patientId, p);
            }
    	}
    }
    
    private void handleCD4Data()
    {
        HashMap<String, Test> uniqueVLTests = new HashMap<String, Test>();
        
    	int Ccd4PatientID = findColumn(this.cd4Table, "cartella UCSC");
    	int Ccd4AnalysisDate = findColumn(this.cd4Table, "data analisi HIV RNA CD4/CD8");
    	int CVLTest = findColumn(this.cd4Table, "metodo");
    	int CHIV = findColumn(this.cd4Table, "copie HIV RNA");
    	int CVLCutOff = findColumn(this.cd4Table, "cutoff");
    	int Ccd4Count = findColumn(this.cd4Table, "CD4 assoluti");
    	int Ccd4Percentage = findColumn(this.cd4Table, "CD4 %");
    	int Ccd8Count = findColumn(this.cd4Table, "CD8 assoluti");
    	int Ccd8Percentage = findColumn(this.cd4Table, "CD8 %");
    	
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
    		
    		//CD4
    		if (!cd4Count.equals("") && !cd4Count.equals("0")) 
    		{
                TestResult t = p.createTestResult(StandardObjects.getGenericCD4Test());
                t.setValue(cd4Count);
                t.setTestDate(convertDate(analysisDate));
    		}
    		if (!cd4Percentage.equals("") && !cd4Percentage.equals("0")) 
    		{
                TestResult t = p.createTestResult(cd4PercTest);
                t.setValue(cd4Percentage);
                t.setTestDate(convertDate(analysisDate));
    		}
    		 
    		//CD8
    		if (!cd8Count.equals("") && !cd8Count.equals("0")) 
    		{
                TestResult t = p.createTestResult(cd8Test);
                t.setValue(cd8Count);
                t.setTestDate(convertDate(analysisDate));
    		}
    		if (!cd8Percentage.equals("") && !cd8Percentage.equals("0")) 
    		{
                TestResult t = p.createTestResult(cd8PercTest);
                t.setValue(cd8Percentage);
                t.setTestDate(convertDate(analysisDate));
    		}
    		 
    		 if(!"".equals(vlHIV))
    		 {
	    		 TestResult testResult = null;
	    		 
	    		 if("".equals(vlTest))
	    		 {
	    			 testResult = p.createTestResult(StandardObjects.getGenericViralLoadTest());
	    		 }
	    		 else
	    		 {
                     Test vlT = uniqueVLTests.get(vlTest);
                     if(vlT==null) {
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
	    		 testResult.setTestDate(convertDate(analysisDate));
    		 }
    	}
    }
    
    private void handleTherapies()
    {
    	int ChivPatientID = findColumn(this.hivTherapyTable, "cartella UCSC");
    	int ChivStartTherapy = findColumn(this.hivTherapyTable, "data start terapia anti HIV");
    	int ChivStopTherapy = findColumn(this.hivTherapyTable, "data stop terapia anti HIV");
    	int ChivLineTherapy = findColumn(this.hivTherapyTable, "linea terapia anti HIV");
    	int ChivSuccessTherapy = findColumn(this.hivTherapyTable, "terapia anti HIV conclusa");
    	int ChivStopReasonTherapy = findColumn(this.hivTherapyTable, "motivo stop terapia anti HIV");
    	int ChivCommercialDrug = findColumn(this.hivTherapyTable, "terapia ARV");
    	int ChivAZTDrug = findColumn(this.hivTherapyTable, "AZT");
    	int ChivDDIDrug = findColumn(this.hivTherapyTable, "DDI");
    	int ChivDDCDrug = findColumn(this.hivTherapyTable, "DDC");
    	int ChivHUDrug = findColumn(this.hivTherapyTable, "HU");
    	int Chiv3TCDrug = findColumn(this.hivTherapyTable, "3TC");
    	int ChivD4TDrug = findColumn(this.hivTherapyTable, "D4T");
    	int ChivABCDrug = findColumn(this.hivTherapyTable, "ABC");
    	int ChivFTCDrug = findColumn(this.hivTherapyTable, "FTC");
    	int ChivTDFDrug = findColumn(this.hivTherapyTable, "TDF");
    	int ChivNVPDrug = findColumn(this.hivTherapyTable, "NVP");
    	int ChivEFVDrug = findColumn(this.hivTherapyTable, "EFV");
    	int ChivDLVDrug = findColumn(this.hivTherapyTable, "DLV");
    	int ChivCPVDrug = findColumn(this.hivTherapyTable, "CPV");
    	int ChivIDVDrug = findColumn(this.hivTherapyTable, "IDV");
    	int ChivRTVDrug = findColumn(this.hivTherapyTable, "RTV");
    	int ChivRTVBoostDrug = findColumn(this.hivTherapyTable, "RTV_booster");
    	int ChivSQVDrug = findColumn(this.hivTherapyTable, "SQV");
    	int ChivNFVDrug = findColumn(this.hivTherapyTable, "NFV");
    	int ChivAPVDrug = findColumn(this.hivTherapyTable, "APV");
    	int ChivFPVDrug = findColumn(this.hivTherapyTable, "FPV");
    	int ChivLPVDrug = findColumn(this.hivTherapyTable, "LPV");
    	int ChivATVDrug = findColumn(this.hivTherapyTable, "ATV");
    	int ChivTPVDrug = findColumn(this.hivTherapyTable, "TPV");
    	int ChivT20Drug = findColumn(this.hivTherapyTable, "T20");
    	
    	    	
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
        		
        		if(!"".equals(hivStartTherapy))
        		{
        			startDate = convertDate(hivStartTherapy);
        		}
        		
        		if(!"".equals(hivAZTDrug) && hivAZTDrug.equals("1"))
        		{
        			drugs.add("AZT");
        		}
        		
        		if(!"".equals(hivDDIDrug) && hivDDIDrug.equals("1"))
        		{
        			drugs.add("DDI");
        		}
        		
        		if(!"".equals(hivDDCDrug) && hivDDCDrug.equals("1"))
        		{
        			drugs.add("DDC");
        		}
        		
        		if(!"".equals(hivHUDrug) && hivHUDrug.equals("1"))
        		{
        			drugs.add("HU");
        		}
        		
        		if(!"".equals(hiv3TCDrug) && hiv3TCDrug.equals("1"))
        		{
        			drugs.add("3TC");
        		}
        		
        		if(!"".equals(hivD4TDrug) && hivD4TDrug.equals("1"))
        		{
        			drugs.add("D4T");
        		}
        		
        		if(!"".equals(hivABCDrug) && hivABCDrug.equals("1"))
        		{
        			drugs.add("ABC");
        		}
        		
        		if(!"".equals(hivFTCDrug) && hivFTCDrug.equals("1"))
        		{
        			drugs.add("FTC");
        		}
        		
        		if(!"".equals(hivTDFDrug) && hivTDFDrug.equals("1"))
        		{
        			drugs.add("TDF");
        		}
        		
        		if(!"".equals(hivNVPDrug) && hivNVPDrug.equals("1"))
        		{
        			drugs.add("NVP");
        		}
        		
        		if(!"".equals(hivEFVDrug) && hivEFVDrug.equals("1"))
        		{
        			drugs.add("EFV");
        		}
        		
        		if(!"".equals(hivDLVDrug) && hivDLVDrug.equals("1"))
        		{
        			drugs.add("DLV");
        		}
        		
        		if(!"".equals(hivCPVDrug) && hivCPVDrug.equals("1"))
        		{
        			drugs.add("CPV");
        		}
        		
        		if(!"".equals(hivIDVDrug) && hivIDVDrug.equals("1"))
        		{
        			drugs.add("IDV");
        		}
        		
        		if(!"".equals(hivRTVDrug) && hivRTVDrug.equals("1"))
        		{
        			drugs.add("RTV");
        		}
        		
        		if(!"".equals(hivRTVBoostDrug) && hivRTVBoostDrug.equals("1"))
        		{
        			drugs.add("RTV");
        		}
        		
        		if(!"".equals(hivSQVDrug) && hivSQVDrug.equals("1"))
        		{
        			drugs.add("SQV");
        		}
        		
        		if(!"".equals(hivNFVDrug) && hivNFVDrug.equals("1"))
        		{
        			drugs.add("NFV");
        		}
        		
        		if(!"".equals(hivAPVDrug) && hivAPVDrug.equals("1"))
        		{
        			drugs.add("APV");
        		}
        		
        		if(!"".equals(hivFPVDrug) && hivFPVDrug.equals("1"))
        		{
        			drugs.add("FPV");
        		}
        		
        		if(!"".equals(hivLPVDrug) && hivLPVDrug.equals("1"))
        		{
        			drugs.add("LPV");
        		}
        		
        		if(!"".equals(hivATVDrug) && hivATVDrug.equals("1"))
        		{
        			drugs.add("ATV");
        		}
        		
        		if(!"".equals(hivTPVDrug) && hivTPVDrug.equals("1"))
        		{
        			drugs.add("TPV");
        		}
        		
        		if(!"".equals(hivT20Drug) && hivT20Drug.equals("1"))
        		{
        			drugs.add("T20");
        		}
        		
        		ArrayList<DrugCommercial> comDrugs = evaluateDrugs(hivCommercialDrug.toLowerCase(), drugs);
        		
        		if(!"".equals(hivStopTherapy))
        		{
        			stopDate = convertDate(hivStopTherapy);
        		}
        		
        		if(hivPatientID != null)
        		{
        			storeTherapy(hivPatientID, startDate, stopDate, comDrugs, null);
        		}
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
    		return;
    	
    	if(medicinsList == null)
    	{
    		System.err.println("Something wrong with therapy mapping for patient '" + patientId + "'");
			
    		return;
    	}

    	if(startDate != null && endDate != null)
    	{
	    	if(startDate.equals(endDate) || startDate.after(endDate))
	    	{
	    		System.err.println("Something wrong with treatment dates for patient '" + patientId + "': " + startDate.toLocaleString() + " - " + endDate.toLocaleString() + ": End date is in the past...ignoring");
	    			
	    		return;
	    	}
    	}
    	//TODO: Additional error handling...
    	/*else if(startDate != null && endDate == null)
    	{
    		System.err.println("Something wrong with treatment dates for patient '" + patientId + "': No suitable end date available...ignoring");
			
    		return;
    	}
    	else if(startDate == null && endDate != null)
    	{
    		System.err.println("Something wrong with treatment dates for patient '" + patientId + "': No suitable start date available...ignoring");
			
    		return;
    	}
    	else
    	{
    		System.err.println("Something wrong with treatment dates for patient '" + patientId + "': No suitable start and end dates available...ignoring");
			
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
    		System.err.println("Cannot find patient with ID "+patientID);
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
    public void getSequences(File workingDirectory) throws IOException {
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
            	date = convertDate(token.trim());
            	linePositionCounter++;
            }
            else if(linePositionCounter==2) 
            {
            	String seq = clearNucleotides(token);
            	
            	System.out.println("Patient: "+patientID+" Seq: "+seq);
            	
                /*Aligner aligner = new Aligner(new LocalAlignmentService(), StandardObjects.getProteinMap());
                int size = 0;
                try 
                {
                    NtSequence ntseq = new NtSequence();
                    ntseq.setNucleotides(seq);
                    size = aligner.alignHiv(ntseq).size();
                } 
                catch (IllegalSymbolException e) 
                {
                    e.printStackTrace();
                }
                
                System.out.println("This.size: "+size);
                
                if(size != 0)
                	addViralIsolateToPatients(patientID, date, seq);
                else
                	System.err.println("Cannot align sequence "+seq);
                 */
                //addViralIsolateToPatients(patientID, date, seq);
                
                linePositionCounter = 0;
            }
        }
    }
    
    public String clearNucleotides(String nucleotides) {
        StringBuffer toReturn = new StringBuffer();
        for(char c : nucleotides.toCharArray()) {
            if(Character.isLetter(c)) {
                toReturn.append(c);
            }
        }
        return toReturn.toString();
    }
    
    int findColumn(Table t, String name) {
		int column = t.findInRow(0, name);
		
		if (column == -1)
			throw new RuntimeException("Could not find column " + name);

		return column;
	}
    
     private Table readTable(String filename) throws FileNotFoundException {
        System.err.println(filename);
        return new Table(new BufferedInputStream(new FileInputStream(filename)), false);
    }
     
     private List<Attribute> prepareRegaDBAttributes(File workingDirectory)
     {
         RegaDBSettings.getInstance().initProxySettings();
         
         FileProvider fp = new FileProvider();
         List<Attribute> list = null;
         File attributesFile = null;
         try {
             attributesFile = File.createTempFile("attributes", ".xml");
         } catch (IOException e1) {
             e1.printStackTrace();
         }
         try 
         {
             fp.getFile("regadb-attributes", "attributes.xml", attributesFile);
         }
         catch (RemoteException e) 
         {
             e.printStackTrace();
         }
         final ImportFromXML imp = new ImportFromXML();
         try 
         {
             imp.loadDatabaseObjects(null);
             list = imp.readAttributes(new InputSource(new FileReader(attributesFile)), null);
         }
         catch(SAXException saxe)
         {
             saxe.printStackTrace();
         }
         catch(IOException ioex)
         {
             ioex.printStackTrace();
         }
         
         return list;
     } 
     
     private void prepareRegaDBDrugs()
     {
    	 ImportDrugsFromCentralRepos imDrug = new ImportDrugsFromCentralRepos();
    	 
    	 dcs = imDrug.getCommercialDrugs();
     } 
     
     private Attribute selectAttribute(String attributeName, List<Attribute> list)
     {
         Attribute toReturn = null;
         
         for(Attribute a : list)
         {
             if(a.getName().equals(attributeName))
             {
                 toReturn = a;
             }
         }
         
         return toReturn;
     }
     
    private Date convertDate(String germanDate)
    {
    	try
    	{
	    	String[] split = germanDate.split(" ");
			
			String date = null;
			
			if(split != null &&
			   split.length == 2)
			{
				date = split[0];
			
				if(date != null)
				{
					String[] parts = date.split("\\.");
					
					if(parts != null &&
					   parts.length == 3)
					{
						String day = parts[0];
						String month = parts[1];
						String year = parts[2];
						
						return Utils.createDate(year, month, day);
					}
				}
			}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		
		return null;
    }
    
    private void exportXML(String fileName) {
        ExportToXML l = new ExportToXML();
        Element root = new Element("patients");
        
        for (String patientId:patientMap.keySet()) {
            Element patient = new Element("patients-el");
            root.addContent(patient);

            Patient p = patientMap.get(patientId);
            l.writePatient(p, patient);            
        }
        
        Document n = new Document(root);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        
        /*
        try {
            outputter.output(n, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        java.io.FileWriter writer;
        try {
            writer = new java.io.FileWriter(fileName);
            outputter.output(n, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     
    //TODO: Find a more sophisticated solution for translating text on the fly...
    /*private String translate(String word)
    {
    	try
    	{
    		Thread.sleep(500);
    		 
    		TranslateServiceLocator loc = new TranslateServiceLocator();
    		loc.setEndpointAddress("TranslatorInterfacePort", "http://incantations.net:8080/GoogleTranslateRemoteJAXRPC/Translate");
    		 
    		String result = loc.getTranslatorInterfacePort().translate(new TranslateRequestBean("it|en", word));
    		 
    		System.out.println("Translated value "+word+" into " +result);
    		 
    		return result.toLowerCase();
    	} 
    	catch (Exception e) 
    	{
    		System.err.println(e.toString());
    	}
    	 
    	return null;
    }*/
    
    //TODO: Directly parse from test file...
    private HashMap<String, String> getRiskGroupTranslation()
    {
    	 int cIndex = findColumn(this.riskGroupTable, "fattore di rischio");
    	 
    	 HashMap<String, String> values = new HashMap<String, String>();
    	 
    	 for(int i = 1; i < this.riskGroupTable.numRows(); i++)
     	 {
             String value = this.riskGroupTable.valueAt(cIndex, i);
             
             if(!"".equals(value))
             {
            	 if(value.equals("altro"))
            	 {
            		 values.put("altro", "other");
            	 }
            	 else if(value.equals("etero"))
            	 {
            		 values.put("etero", "heterosexual");
            	 }
            	 else if(value.equals("trasfusioni"))
            	 {
            		 values.put("trasfusioni", "transfusion");
            	 }
            	 else if(value.equals("FD"))
            	 {
            		 values.put("FD", "IVDU");
            	 }
            	 else if(value.equals("omo/bi"))
            	 {
            		 values.put("omo/bi", "homosexual");
            	 }
            	 else if(value.equals("ignoto"))
            	 {
            		 //ignore...
            	 }
             }
     	 }
    	 
    	 return values;
     }
     
     //TODO: Directly parse from test file...
     private HashMap<String, String> getStopTherapyTranslation()
     {
    	 int cIndex = findColumn(this.stopTherapieDescTable, "motivo stop terapia anti HIV");
    	 
    	 HashMap<String, String> values = new HashMap<String, String>();
    	 
    	 for(int i = 1; i < this.stopTherapieDescTable.numRows(); i++)
     	 {
             String value = this.stopTherapieDescTable.valueAt(cIndex, i);
             
             if(!"".equals(value))
             {
            	 if(value.equals("cambiamento genotipo-guidato"))
            	 {
            		 values.put("cambiamento genotipo-guidato", "genotype guided change");
            	 }
            	 else if(value.equals("coinfezione HIV-HBV"))
            	 {
            		 values.put("coinfezione HIV-HBV", "HBV coinfection");
            	 }
            	 else if(value.equals("epatite viral"))
            	 {
            		 values.put("epatite viral", "hepatitis");
            	 }
            	 else if(value.equals("fallimento"))
            	 {
            		 values.put("fallimento", "failure");
            	 }
            	 else if(value.equals("fine gravidanza"))
            	 {
            		 values.put("fine gravidanza", "end of pregnancy");
            	 }
            	 else if(value.equals("fine protocollo studio"))
            	 {
            		 values.put("fine protocollo studio", "end of perspective study");
            	 }
            	 else if(value.equals("gravidanza"))
            	 {
            		 values.put("gravidanza", "pregnancy");
            	 }
            	 else if(value.equals("interazione farmaci per TBC"))
            	 {
            		 values.put("interazione farmaci per TBC", "interaction with tuberculosis drugs");
            	 }
            	 else if(value.equals("interazione psicofarmac"))
            	 {
            		 values.put("interazione psicofarmac", "interactions with antipsychotic drugs");
            	 }
            	 else if(value.equals("interazione terapia anti HCV"))
            	 {
            		 values.put("interazione terapia anti HCV", "interactions with anti HCV therapy");
            	 }
            	 else if(value.equals("Interferenza farmaci antiblastic"))
            	 {
            		 values.put("Interferenza farmaci antiblastic", "interactions with anti-tumoral drugs");
            	 }
            	 else if(value.equals("interruzione strutturat"))
            	 {
            		 values.put("interruzione strutturat", "structured interruption");
            	 }
            	 else if(value.equals("Miglioramento dati viro-immunologici"))
            	 {
            		 values.put("Miglioramento dati viro-immunologici", "improvement of virologic and immune markers");
            	 }
            	 else if(value.equals("non desumibile"))
            	 {
            		 values.put("non desumibile", "unknown");
            	 }
            	 else if(value.equals("nuovo studio"))
            	 {
            		 values.put("nuovo studio", "new perspective study");
            	 }
            	 else if(value.equals("potenziamento"))
            	 {
            		 values.put("potenziamento", "more powerful therapy chosen");
            	 }
            	 else if(value.equals("Ricovero"))
            	 {
            		 values.put("Ricovero", "patient was in hospital");
            	 }
            	 else if(value.equals("Scarsa compliance"))
            	 {
            		 values.put("Scarsa compliance", "non adherence");
            	 }
            	 else if(value.equals("Scelta del paziente"))
            	 {
            		 values.put("Scelta del paziente", "patient's choice");
            	 }
            	 else if(value.equals("semplificazione"))
            	 {
            		 values.put("semplificazione", "simplification");
            	 }
            	 else if(value.equals("tossicit�/allergia"))
            	 {
            		 values.put("tossicit�/allergia", "toxicity");
            	 }
             }
     	 }
    	 
    	 return values;
     }
     
     //TODO: Use more sophisticated algorithms to find a string match...
     private double findCountryMatch(String value, String compareWithValue)
     {
    	 double match = 0.000d;
    	 
    	 double inc = 0.000d;
    		 
    	 inc = (100d / compareWithValue.length()) / 100d;
    	 
    	 value = value.toLowerCase();
    	 compareWithValue = compareWithValue.toLowerCase();
    	 
    	 for(int i = 0; i < value.length(); i++)
    	 {
    		 if(match >= 0.70)
    		 {
    			 return match;
    		 }
    		 
    		 if(value.charAt(i) == compareWithValue.charAt(i))
    		 {
    			 match += inc;
    		 }
    		 else
    		 {
    			 return match;
    		 }
    	 }
    	 
    	 return -1;
     }
}
