package net.sf.regadb.io.db.irsicaixa;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
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
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Logging;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.NominalEvent;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.io.util.StandardObjects;

public class ImportIrsicaixa {
    private Logging logger_;
    private String basePath_;
    private Mappings mappings_;
    
    private Table generalDataTable_;
    private Table cd4Table_;
    private Table therapyTable_;
    
    private Table eventTable_;
    private Table vlTable_;
    private Table vhbTable_;
    private Table vhcTable_;
    private Table fastaTable_;
    
    private Table countryTable_;
    private Table transmissionGroupTable_;
    private Table aidsDefiningIllnessTable_;
    
    private HashSet<String> unmappedDrugs = new HashSet<String>();
    
    private HashMap<String,TherapyDrug> drugDosageMapping_;
    
    private HashMap<String, Test> tests_ = new HashMap<String, Test>();
    private ArrayList<TestType> testTypes_ = new ArrayList<TestType>();
    
    private AttributeGroup demographics = new AttributeGroup("Demographics");
    private AttributeGroup clinical = new AttributeGroup("Clinical");
    private AttributeGroup personal = new AttributeGroup("Personal");
    
    private List<Attribute> regadbAttributes_;
    private List<Event> regadbEvents_;
    
    private List<DrugGeneric> regaDrugGenerics;
    private List<DrugCommercial> regaDrugCommercials;
    
    private TestNominalValue posSeroStatus_;

    public ImportIrsicaixa(Logging logger, String basePath, String mappingBasePath) {
        logger_ = logger;
        
        basePath_ = basePath;
        mappings_ = Mappings.getInstance(mappingBasePath);
        
        logger_.logInfo("Retrieving standard RegaDB generic drugs");
        regaDrugGenerics = Utils.prepareRegaDrugGenerics();
        logger_.logInfo("Retrieving standard RegaDB commercial drugs");
        regaDrugCommercials = Utils.prepareRegaDrugCommercials();
        drugDosageMapping_ = buildDrugDosageMap(mappingBasePath + File.separatorChar + "generic_drugs.mapping");

        countryTable_ = Utils.readTable(mappingBasePath + File.separatorChar + "country_of_origin.mapping");
        transmissionGroupTable_ = Utils.readTable(mappingBasePath + File.separatorChar + "transmission_group.mapping");
        aidsDefiningIllnessTable_ = Utils.readTable(mappingBasePath + File.separatorChar + "aids_defining_illness.mapping");
        
        posSeroStatus_ = Utils.getNominalValue(StandardObjects.getHiv1SeroStatusTestType(), "Positive");
    }
    
    public void run() {
        generalDataTable_ = Utils.readTable(basePath_ + File.separatorChar + "dbo_dadesgenerals.csv");
        cd4Table_ = Utils.readTable(basePath_ + File.separatorChar + "dbo_dadescd.csv");
        therapyTable_ = Utils.readTable(basePath_ + File.separatorChar + "dbo_dadestractaments.csv","ISO-8859-15");
        
        eventTable_ = Utils.readTable(basePath_ + File.separatorChar + "dbo_dadesmalaltia.csv","ISO-8859-15");
        vlTable_ = Utils.readTable(basePath_ + File.separatorChar + "dbo_dadescv.csv");
        vhbTable_ = Utils.readTable(basePath_ + File.separatorChar + "dbo_dadesvhb.csv");
        vhcTable_ = Utils.readTable(basePath_ + File.separatorChar + "dbo_dadesvhc.csv");
        fastaTable_ = Utils.readTable(basePath_ + File.separatorChar + "dbo_dadesfasta.csv");
        
        tests_ = new HashMap<String, Test>();
        testTypes_ = new ArrayList<TestType>();
        
        logger_.logInfo("Retrieving standard RegaDB attributes");
        regadbAttributes_ = Utils.prepareRegaDBAttributes();
        
        logger_.logInfo("Retrieving standard RegaDB events");
        regadbEvents_ = Utils.prepareRegaDBEvents();
        
        logger_.logInfo("Handling general patient data");
        HashMap<String, Patient> patients = handleGeneralData();
        logger_.logInfo("Handling cd4 data");
        handleCD4(patients);
        logger_.logInfo("Handling therapy data");
        handleTherapy(patients);
        
        logger_.logInfo("Handling event data");
        handleEvent(patients);
        
        logger_.logInfo("Handling viral load data");
        handleViralLoad(patients);
        
        logger_.logInfo("Handling vhb data");
        handleNewTests(patients,vhbTable_);
        
        logger_.logInfo("Handling vhc data");
        handleNewTests(patients,vhcTable_);
        
        logger_.logInfo("Handling fasta data");
        HashMap<String,ViralIsolate> viralisolates = handleFasta(patients);
        
        for(String t: tests_.keySet()){
        	logger_.logInfo("New test: "+ t +":"+ tests_.get(t).getDescription());
        }
        for(TestType t: testTypes_){
        	logger_.logInfo("New testtype: "+ t.getDescription());
        }
        
        IOUtils.exportPatientsXML(patients.values(), getPatientsXmlPath(), ConsoleLogger.getInstance());
        IOUtils.exportNTXML(viralisolates.values(), getViralIsolatesXmlPath(), ConsoleLogger.getInstance());
    }
    
    public String getPatientsXmlPath() {
    	return basePath_ + File.separatorChar + "patients.xml";
    }
    
    public String getViralIsolatesXmlPath() {
    	return basePath_ + File.separatorChar + "viralisolates.xml";
    }
    
    public HashMap<String, Patient> handleGeneralData() {
        HashMap<String, Patient> patients = new HashMap<String, Patient>();
        
        int CPatientId = Utils.findColumn(generalDataTable_, "PATIENID");
        int CGender = Utils.findColumn(generalDataTable_, "GENDER");
        int CBirthdate = Utils.findColumn(generalDataTable_, "DATE_OF_BIRTH");
        int CCountry = Utils.findColumn(generalDataTable_, "COUNTRY");
        int CFirstHIVPosTest = Utils.findColumn(generalDataTable_, "Date_of_first_positive_HIV-test");
        int CRouteOfTransmission = Utils.findColumn(generalDataTable_, "ROUTE_OF_TRANSMISION");
        
        NominalAttribute genderNominal = new NominalAttribute("Gender", CGender, new String[] { "M", "F" },
                new String[] { "male", "female" } );
        genderNominal.attribute.setAttributeGroup(personal);
        
        NominalAttribute countryOfOriginA = new NominalAttribute("Country of origin", countryTable_, demographics, Utils.selectAttribute("Country of origin", regadbAttributes_));
        NominalAttribute transmissionGroupA = new NominalAttribute("Transmission group", transmissionGroupTable_, clinical, Utils.selectAttribute("Transmission group", regadbAttributes_));
        
        for(int i = 1; i<generalDataTable_.numRows(); i++) {
            String patientId = generalDataTable_.valueAt(CPatientId, i);
            String gender = generalDataTable_.valueAt(CGender, i);
            String birthdate = generalDataTable_.valueAt(CBirthdate, i);
            String country = generalDataTable_.valueAt(CCountry, i);
            String firstPosHIVTest = generalDataTable_.valueAt(CFirstHIVPosTest, i);
            String routeOfTransmission = generalDataTable_.valueAt(CRouteOfTransmission, i);
            
            //System.err.println(patientId);
            
            Patient p = new Patient();
            p.setPatientId(patientId);
            patients.put(patientId, p);
            if(!birthdate.equals("NULL")) {
                Date birthdateDate = Utils.parseMysqlDate(birthdate);
                if(birthdateDate!=null)
                    Utils.setBirthDate(p, birthdateDate);
                else
                    logger_.logWarning("Unparsable birthdate for patient with patientId " + patientId + " for date " + birthdate);
            }
            
            AttributeNominalValue gnv = genderNominal.nominalValueMap.get(gender.toUpperCase().trim());
            if (gnv != null) {
                PatientAttributeValue v = p.createPatientAttributeValue(genderNominal.attribute);
                v.setAttributeNominalValue(gnv);
            }
            
            if(Utils.checkColumnValueForExistance("country of origin", country, i, patientId))
        	{
                Utils.handlePatientAttributeValue(countryOfOriginA, country, p);
        	}
            
            if(!firstPosHIVTest.equals("NULL")) {
                Date hivPosDate = Utils.parseMysqlDate(firstPosHIVTest);
                if(hivPosDate==null) {
                    logger_.logWarning("Could not parse firstHivPos date " + hivPosDate);
                } else {
                    TestResult t = p.createTestResult(StandardObjects.getGenericHiv1SeroStatusTest());
                    t.setTestNominalValue(posSeroStatus_);
                    t.setTestDate(hivPosDate);
                }
            }

            if(Utils.checkColumnValueForEmptiness("risk group", routeOfTransmission, i, patientId))
        	{
                String[] tgs = routeOfTransmission.split("\\+");
                for(int j=0; j<tgs.length; ++j)
                    Utils.handlePatientAttributeValue(transmissionGroupA, tgs[j].trim(), p);
        	}
        }
        
        return patients;
    }
    
    public void handleEvent(HashMap<String, Patient> patients){
        int CPatientId = Utils.findColumn(eventTable_, "PATIENTID");
        int CStartDate = Utils.findColumn(eventTable_, "DATE_OF_DIAGNOSIS");
        int CEndDate = Utils.findColumn(eventTable_, "END_DATE");
        int CName = Utils.findColumn(eventTable_, "DIAGNOSIS");
        
        NominalEvent aidsDefiningIllnessA = new NominalEvent("Aids defining illness", aidsDefiningIllnessTable_, Utils.selectEvent("Aids defining illness", regadbEvents_));
        
        for(int i = 1; i<eventTable_.numRows(); i++) {
            String patientId = eventTable_.valueAt(CPatientId, i);
            Patient p = patients.get(patientId);
            if(p!=null) {
                Date startDate = Utils.parseMysqlDate(eventTable_.valueAt(CStartDate, i));
                Date endDate = Utils.parseMysqlDate(eventTable_.valueAt(CEndDate, i));
                
                if(startDate != null){
                    String name = eventTable_.valueAt(CName, i);
                    
                    if(Utils.checkColumnValueForExistance("ade", name, i, patientId))
                    {
                        Utils.handlePatientEventValue(aidsDefiningIllnessA, name, startDate, endDate, p);
                    }
                }
                else{
                    logger_.logWarning("Invalid start date specified in the malaltia file ("+ i +").");
                }
            }
            else{
                logger_.logWarning("Could not find a patient with id " + patientId + " in the malaltia file ("+ i +").");
            }
        }

    }
    
    public void handleCD4(HashMap<String, Patient> patients) {
        int CPatientId = Utils.findColumn(cd4Table_, "PATIENTID");
        int Ccd4Date = Utils.findColumn(cd4Table_, "CD4_DATE");
        int Ccd4Count = Utils.findColumn(cd4Table_, "CD4_COUNT");
        
        for(int i = 1; i<cd4Table_.numRows(); i++) {
            String patientId = cd4Table_.valueAt(CPatientId, i);
            Patient p = patients.get(patientId);
            if(p!=null) {
                Date cd4Date = Utils.parseMysqlDate(cd4Table_.valueAt(Ccd4Date, i));
                if(cd4Date!=null) {
                    try {
                        if(cd4Table_.valueAt(Ccd4Count, i) != null && !cd4Table_.valueAt(Ccd4Count, i).equals("")){
                            double value = Double.parseDouble(cd4Table_.valueAt(Ccd4Count, i));
                            TestResult t = p.createTestResult(StandardObjects.getGenericCD4Test());
                            t.setValue(value+"");
                            t.setTestDate(cd4Date);
                        }
                    }
                    catch(NumberFormatException nfe) {
                        logger_.logWarning("This is not a correct CD4 value (should be floating point or integer number: " + cd4Table_.valueAt(Ccd4Count, i));
                    }
                } else {
                    logger_.logWarning("CD4 tests require a date");
                }
            } else {
                logger_.logWarning("Could not find a patient with id " + patientId + " in the CD4 file");
            }
            
        }
    }
    
    public void handleTherapy(HashMap<String, Patient> patients) {
        int CPatientId = Utils.findColumn(therapyTable_, "PATIENTID");
        int CStartDate = Utils.findColumn(therapyTable_, "INITIATION_DATE");
        int CEndDate = Utils.findColumn(therapyTable_, "END_DATE");
        int CDrugs = Utils.findColumn(therapyTable_, "DRUGS");
        int CMotivation = Utils.findColumn(therapyTable_, "INTERRUPT");
        
        for(int i = 1; i<therapyTable_.numRows(); i++) {
            String patientId = therapyTable_.valueAt(CPatientId, i);
            Patient p = patients.get(patientId);
            if(p!=null) {
                Date startDate = Utils.parseMysqlDate(therapyTable_.valueAt(CStartDate, i));
                if(startDate!=null) {
                    Date endDate = Utils.parseMysqlDate(therapyTable_.valueAt(CEndDate, i));
                    
                    String drugs = therapyTable_.valueAt(CDrugs, i);
                    HashMap<TherapyDrug,Double> drugsList = processDrugs(drugs);
                    
                    Therapy t = p.createTherapy(startDate);
                    t.setStopDate(endDate);

                    for (TherapyDrug td : drugsList.keySet()){
                    	Double dose = drugsList.get(td);
                    	
                    	if(td != null){
                    		if(td.generic != null){
                    			TherapyGeneric tg = new TherapyGeneric(new TherapyGenericId(t, td.generic),false,false);
                    			tg.setDayDosageMg(dose);
                    			t.getTherapyGenerics().add(tg);
                    		}
                    		else{
                    			TherapyCommercial tc = new TherapyCommercial(new TherapyCommercialId(t, td.commercial),false,false);
                    			tc.setDayDosageUnits(dose);
                    			t.getTherapyCommercials().add(tc);
                    		}
                    	}
                    }
                    
                    String smotiv = therapyTable_.valueAt(CMotivation, i);
                    if(!smotiv.trim().equals("")) {
	                    String mapping = mappings_.getMapping("motivation.mapping",smotiv);
	                    if(mapping != null && !mapping.equals("")){
	                    	TherapyMotivation tmotiv = new TherapyMotivation(mapping);
	                    	t.setTherapyMotivation(tmotiv);
	                    }
	                    else{
	                    	logger_.logWarning("Could not find mapping for motivation "+ smotiv);
	                    }
                    }
                    
                } else {
                    logger_.logWarning("Therapy without startdate for patient " + patientId);
                }
            } else {
                logger_.logWarning("Could not find a patient with id " + patientId + " in the therapy file");
            }
        }
    }
    
    public void handleViralLoad(HashMap<String, Patient> patients){
    	int CPatientId	= Utils.findColumn(vlTable_, "PATIENTID");
    	int CVLDate 	= Utils.findColumn(vlTable_, "VL_DATE");
    	int CVL			= Utils.findColumn(vlTable_, "VIRAL_LOAD");
    	int CMethod		= Utils.findColumn(vlTable_, "METHOD");
    	int CLimit		= Utils.findColumn(vlTable_, "LIMIT");
    	
    	for(int i=1; i<vlTable_.numRows(); ++i){
    		String pid = vlTable_.valueAt(CPatientId, i);
    		
    		Patient p = patients.get(pid);
    		if(p != null){
        		String method = vlTable_.valueAt(CMethod, i);
        		
        		if(method.length() > 0){
	        		Test t = tests_.get(method);
	        		if(t == null){
	        			t = new Test(StandardObjects.getHiv1ViralLoadTestType(),method);
	        			tests_.put(method, t);
	        		}
	
	    			Date vldate = Utils.parseMysqlDate(vlTable_.valueAt(CVLDate, i));
	    			if(vldate != null){
	    			    if(vlTable_.valueAt(CVL,i) != null && !vlTable_.valueAt(CVL,i).equals("")){
    		    			String slimit = vlTable_.valueAt(CLimit,i);
    	    				String value;
    	    				
    	    				try{
    	    					double vl = Double.parseDouble(vlTable_.valueAt(CVL,i));
    	    					
    	    					try{
    		    					int limit = 0;
    		    					if((slimit.length() > 0) && (!slimit.equals("NULL")))
    		    						limit = Integer.parseInt(vlTable_.valueAt(CLimit,i));
    			    				
    			    				if(vl <= limit)
    			    					value = "<"+ limit;
    			    				else
    			    					value = "="+ vl;
    			    				
    				    			TestResult tr = p.createTestResult(t);
    				    			tr.setTestDate(vldate);
    				    			tr.setValue(value);
    	    					}
    	    					catch(Exception e){
    	    						logger_.logWarning("Invalid limit specified in the viralload file ("+ i +").");
    	    					}
    	    				}
    	    				catch(Exception e){
    	    					logger_.logWarning("Invalid viral load specified in the viralload file ("+ i +").");
    	    				}
	    			    }
	    			}
	    			else{
	        			logger_.logWarning("Invalid date specified in the viralload file ("+ i +").");
	        		}
        		}
        		else{
        			logger_.logWarning("Invalid method specified in the viralload file ("+ i +").");
        		}
    		}
    		else{
    			logger_.logWarning("Could not find a patient with id " + pid + " in the viralload file ("+ i +").");
    		}
    	}
    }
    
    public void handleNewTests(HashMap<String, Patient> patients, Table t){
    	int CPatientId	= Utils.findColumn(t, "PATIENTID");
    	int CTestDate	= Utils.findColumn(t, "TEST_DATE");
    	
    	//create the new testtypes and test first, based on the column names
    	if(t.numRows()>0){
    		for(int i = 2; i<t.numColumns(); ++i){
    			String s = t.valueAt(i, 0);
    			createNewTypeAndTest(s, s);
    		}
    	}
    	
    	//create the test results
    	for(int i=1; i<t.numRows(); ++i){
    		String pid = t.valueAt(CPatientId, i);
    		Patient p = patients.get(pid);
    		if(p != null){
        		Date testdate = Utils.parseMysqlDate(t.valueAt(CTestDate, i));
        		
        		if(testdate != null){
		    		for(int j=2; j<t.numColumns(); ++j){
		    			String value = t.valueAt(j, i);
		    			
		    			if(value != null && !value.equals("NULL") && !value.equals("")){
			    			//find test using column name
			    			TestResult tr = p.createTestResult(tests_.get(t.valueAt(j,0)));
			    			tr.setTestDate(testdate);
			    			tr.setValue(value);
		    			}
		    		}
        		}
        		else{
        			logger_.logWarning("Invalid date specified in the vhb/vhc file ("+ i +").");
        		}
    		}
    		else{
    			logger_.logWarning("Could not find a patient with id " + pid + " in the vhb/vhc file ("+ i +").");
    		}
    	}
    }
    
    private void createNewTypeAndTest(String testtypeDescr, String testDescr){
    	if(testDescr.equals("HBsAg"))
    		tests_.put(testDescr,StandardObjects.getGenericHBsAgTest());
    	else if(testDescr.equals("HBeAg"))
    		tests_.put(testDescr,StandardObjects.getGenericHBeAgTest());
    	else if(testDescr.equals("anti HBc"))
    		tests_.put(testDescr,StandardObjects.getGenericHBcAbTest());
    	else if(testDescr.equals("anti HBs"))
    		tests_.put(testDescr,StandardObjects.getGenericHBsAbTest());
    	else if(testDescr.equals("anti HBe"))
    		tests_.put(testDescr,StandardObjects.getGenericHBeAbTest());
    	else{
			TestType tt = new TestType(StandardObjects.getNumberValueType(), StandardObjects.getHiv1Genome(), StandardObjects.getPatientTestObject(),testtypeDescr, new TreeSet<TestNominalValue>());
			testTypes_.add(tt);
			
			Test tst = new Test(tt,testDescr);
			tests_.put(testDescr,tst);
    	}
    }
    
    public HashMap<String,ViralIsolate> handleFasta(HashMap<String, Patient> patients){
    	int CIdSample	= Utils.findColumn(fastaTable_, "IDSAMPLE");
    	int CIdSeq		= Utils.findColumn(fastaTable_, "IDSEQ");
    	int CPatientId	= Utils.findColumn(fastaTable_, "PATIENTID");
    	int CGTDate		= Utils.findColumn(fastaTable_, "GENOTYPE_DATE");
    	int CGT			= Utils.findColumn(fastaTable_, "GENOTYPE(FASTA)");
    	
    	HashMap<String,ViralIsolate> samvi = new HashMap<String,ViralIsolate>();
    	
    	for(int i=1; i<fastaTable_.numRows(); ++i){

    		String pid = fastaTable_.valueAt(CPatientId, i);
    		Patient p = patients.get(pid);

    		if(p != null){
    			Date gtdate = Utils.parseMysqlDate(fastaTable_.valueAt(CGTDate, i));
    			
    			if(gtdate != null){
	    			Set<NtSequence> seqs;
	    			String sampleid = fastaTable_.valueAt(CIdSample,i);
	    			ViralIsolate vi = samvi.get(sampleid);
	
	    			if(vi == null){
	    				vi = p.createViralIsolate();
	    				vi.setSampleDate(gtdate);
	    				vi.setSampleId(sampleid);
	    				seqs = new HashSet<NtSequence>();
	       				vi.setNtSequences(seqs);
	       				
	       				samvi.put(sampleid,vi);
	    			}
	    			else{
	    				if(gtdate.before(vi.getSampleDate())){
	    					vi.setSampleDate(gtdate);
	    				}
	    			}
	    			
	    			seqs = vi.getNtSequences();
					NtSequence seq = new NtSequence();
					seq.setNucleotides(parseNucleotides(fastaTable_.valueAt(CGT,i)));
					seq.setLabel(fastaTable_.valueAt(CIdSeq, i));
					
					seqs.add(seq);
    			}
    			else{
    				logger_.logWarning("Invalid date specified in the viral isolate file ("+ i +").");
    			}
    		}
    		else{
    			logger_.logWarning("Could not find a patient with id " + pid + " in the viral isolate file ("+ i +").");
    		}
    	}
    	
    	return samvi;
    }
    
    public String parseNucleotides(String nucleotides){
    	nucleotides = nucleotides.replaceAll("\\W", "");
    	return nucleotides.toLowerCase();
    }
    
    public HashMap<TherapyDrug,Double> processDrugs(String drugs) {
        StringTokenizer st = new StringTokenizer(drugs, ",");
        HashMap<TherapyDrug,Double> drugsList = new HashMap<TherapyDrug,Double>();
        if(drugs.equals("NULL"))
            return drugsList;
        
        while(st.hasMoreTokens())  {
            String drug = st.nextToken().trim();
            TherapyDrug drugdos = drugDosageMapping_.get(drug);

            if(drugdos == null){
                //search unmapped but identically named drug
                DrugGeneric dg = getDrugGeneric(drug);
                if(dg != null){
                    drugdos = new TherapyDrug(drug,0,dg,null);
                }
                else{
                    DrugCommercial dc = getDrugCommercial(drug);
                    if(dc != null){
                        drugdos = new TherapyDrug(drug,0,dg,null);
                    }
                    else{
                        logUnmappedDrug(drug);
                        continue;
                    }
                }
                drugDosageMapping_.put(drug, drugdos);
            }
            
        	Double dose = drugsList.get(drugdos);
        	if(dose != null){
       			drugsList.put(drugdos, new Double(drugdos.dosage + dose));
        	}
        	else{
        		drugsList.put(drugdos, drugdos.dosage);
        	}
        }
        
        return drugsList;
    }
    
    public HashMap<String,TherapyDrug> buildDrugDosageMap(String drugMappingFile){
    	Table t = Utils.readTable(drugMappingFile);
    	
    	HashMap<String,TherapyDrug> ddmap = new HashMap<String,TherapyDrug>();
    	
    	for(int i=1; i<t.numRows();++i){
    		TherapyDrug td = new TherapyDrug();
    		
    		String name = t.valueAt(1, i);
    		String sdosage = t.valueAt(2,i); 
    		
    		
    		td.generic = getDrugGeneric(name);
    		if(td.generic == null)
    			td.commercial = getDrugCommercial(name);
    		
    		if(td.generic == null && td.commercial == null){
    			logUnmappedDrug(name);
    			continue;
    		}
    		
    		if(sdosage != null && sdosage.length() > 0){
	    		try{
	    			Double dosage = new Double(Double.parseDouble(sdosage));
	    			td.dosage = dosage;
	    		}
	    		catch(Exception e){
	    			logger_.logWarning("Invalid dosage specified in generic_drugs.mapping file ("+ i +").");
	    		}
    		}
    		ddmap.put(t.valueAt(0,i), td);
    	}
    	
    	return ddmap;
    }
    
    public DrugGeneric getDrugGeneric(String generic_id){
        for(int j = 0; j < regaDrugGenerics.size(); j++)
        {
            DrugGeneric d = regaDrugGenerics.get(j);
            
            if(d.getGenericId().equals(generic_id))
                return d;
        }
        return null;
    }
    public DrugCommercial getDrugCommercial(String name){
        for(int j = 0; j < regaDrugCommercials.size(); j++)
        {
            DrugCommercial d = regaDrugCommercials.get(j);
            
            if(d.getName().equals(name))
                return d;
        }
        return null;
    }
    
    public void logUnmappedDrug(String drug){
        if(unmappedDrugs.add(drug))
            logger_.logWarning("No mapping for drug: "+ drug);
    }

    
    public static void main(String [] args) {
        if(args.length < 2)
            System.out.println("Usage: ImportIrsicaixa <csv path> <mappings path>");
        System.setProperty("http.proxyHost", "www-proxy");
        System.setProperty("http.proxyPort", "3128");//*/
        ImportIrsicaixa imp = new ImportIrsicaixa(ConsoleLogger.getInstance(), args[0], args[1]);
        imp.run();
    }
    
    public class TherapyDrug{
        public TherapyDrug(){}
        public TherapyDrug(String name, double dosage, DrugGeneric generic, DrugCommercial commercial){
            this.name = name;
            this.dosage = dosage;
            this.generic = generic;
            this.commercial = commercial;
        }
        
    	public String name;
    	public double dosage=0;
    	public DrugGeneric generic=null;
    	public DrugCommercial commercial=null;
    }
}
