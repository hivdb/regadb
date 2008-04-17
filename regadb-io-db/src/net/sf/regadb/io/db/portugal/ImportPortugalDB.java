/*
 * Created on Aug 24, 2005
 *
 */
package net.sf.regadb.io.db.portugal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.analysis.functions.FastaRead;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

/**
 * @author kdforc0
 */
public class ImportPortugalDB {
    static final String SOURCE = "EgazMoniz";
    
    private Table sampleTable;
    private Table countryTable;
    private Table ethnicityTable;
    private Table geographicOriginTable;
    private Table institutionTable;
    private Table medicinsTable;
    private Table therapeuticsTable;
    private Table therapeuticMedicinsTable;
    private Table transmissionGroupTable;
    private String sequenceDirName;

    private Table.Index sampleIndex;
    private Table.Index therapyIndex;

    private int CSamplePatientID;
    private int CSampleYearCollection;
    private int CSampleMonthCollection;
    private int CSampleYearBirth;
    private int CSampleGender;
    private int CSampleViralLoad;
    private int CSampleCD4Count;
    private int CSampleSampleID;
    private int CSampleId_Sample;
    private int CSampleClinicalFileNumber;
    private int CSampleIdInstitution;
    private int CSampleIdTransmissionGroup;
    private int CSampleIdGeographicOrigin;
    private int CSampleIdEthnicity;
    private int CSampleIdCountry;

    private int CTherapeuticsIdSample;
    private int CTherapeuticsIdSampleStartMonth;
    private int CTherapeuticsIdSampleStartYear;
    private int CTherapeuticsId;
    private int CTherapeuticsTherapy;
    private int CTherapeuticsIdSampleEndMonth;
    private int CTherapeuticsIdSampleEndYear;
    private int CTherapeuticsPatientId;

    private int CMedicinsIdMedicins;
    private int CMedicinsMedicinCode;

    private int CTherapeuticMedicinsTableIdTherapeutics;
    private int CTherapeuticMedicinsTableIdMedicins;

    private int CTherapeuticsYearCollection;
    private int CTherapeuticsMonthCollection;

    private Map<String, Patient> patientMap;

    private ValueType stringValueType;
    
    private Map<String, ViralIsolate> viralIsolateHM = new HashMap<String, ViralIsolate>();
    
    private File patientXmlFile;
    private File sequenceXmlFile;
    
    private String mappingBasePath = "/home/plibin0/myWorkspace/regadb-io-db/src/net/sf/regadb/io/db/portugal/mapping/";
 
    public ImportPortugalDB(String sampleFName, String countryFName,
            				String ethnicityFName, String geographicOriginFName,
            				String institutionFName, String medicinsFName,
            				String therapeuticsFName, String therapeuticMedicinsFname,
            				String transmissionGroupFName, String sequenceDirName,
            				String patientXmlFile, String sequenceXmlFile)
            throws FileNotFoundException {

        System.err.println("Reading data...");
        this.sampleTable = Utils.readTable(sampleFName);
        this.countryTable = Utils.readTable(countryFName);
        this.ethnicityTable = Utils.readTable(ethnicityFName);
        this.geographicOriginTable = Utils.readTable(geographicOriginFName);
        this.institutionTable = Utils.readTable(institutionFName);
        this.medicinsTable = Utils.readTable(medicinsFName);
        this.therapeuticsTable = Utils.readTable(therapeuticsFName);
        this.therapeuticMedicinsTable = Utils.readTable(therapeuticMedicinsFname);
        this.transmissionGroupTable = Utils.readTable(transmissionGroupFName);
        
        this.patientXmlFile = new File(patientXmlFile);
        this.sequenceXmlFile = new File(sequenceXmlFile);
        
        System.err.println("done.");
        
        List<Attribute> regadbAttributesList = Utils.prepareRegaDBAttributes();
        
        Attribute countryOfOrigin = Utils.selectAttribute("Country of origin", regadbAttributesList);
        fixAttributeTable(countryTable, countryOfOrigin, "countryOfOrigin.mapping");
        Attribute transmissionGroup = Utils.selectAttribute("Transmission group", regadbAttributesList);
        fixAttributeTable(transmissionGroupTable, transmissionGroup, "transmissionGroup.mapping");
        Attribute geographicOrigin = Utils.selectAttribute("Geographic origin", regadbAttributesList);
        fixAttributeTable(geographicOriginTable, geographicOrigin, "geographicOrigin.mapping");
        Attribute ethnicity = Utils.selectAttribute("Ethnicity", regadbAttributesList);
        fixAttributeTable(ethnicityTable, ethnicity, "ethnicity.mapping");
        
        this.sequenceDirName = sequenceDirName;
        
        this.CSamplePatientID = Utils.findColumn(sampleTable, "PatientID");
        this.CSampleYearCollection = Utils.findColumn(sampleTable, "YearCollection");
        this.CSampleMonthCollection = Utils.findColumn(sampleTable, "MonthCollection");
        this.CSampleYearBirth = Utils.findColumn(sampleTable, "YearBirth");
        this.CSampleGender = Utils.findColumn(sampleTable, "Gender");
        this.CSampleViralLoad = Utils.findColumn(sampleTable, "ViralLoad");
        this.CSampleCD4Count = Utils.findColumn(sampleTable, "CD4Count");
        this.CSampleSampleID = Utils.findColumn(sampleTable, "SampleID");
        this.CSampleId_Sample = Utils.findColumn(sampleTable, "ID_Sample");
        this.CSampleClinicalFileNumber = Utils.findColumn(sampleTable, "ClinicalFileNumber");
        this.CSampleIdInstitution = Utils.findColumn(sampleTable, "Id_Institution");
        this.CSampleIdTransmissionGroup = Utils.findColumn(sampleTable, "Id_TransmissionGroup");
        this.CSampleIdGeographicOrigin = Utils.findColumn(sampleTable, "Id_GeographicOrigin");
        this.CSampleIdEthnicity = Utils.findColumn(sampleTable, "Id_Ethnicity");
        this.CSampleIdCountry = Utils.findColumn(sampleTable, "Id_Country");

        this.CTherapeuticsIdSample = Utils.findColumn(therapeuticsTable, "Id_Sample");

        System.err.println("Merging therapeutics data... (from "+therapeuticsFName+")");
        therapeuticsTable.merge(sampleTable, CTherapeuticsIdSample, CSampleId_Sample, true);
        System.err.println("OK.");

        this.CTherapeuticsTherapy = Utils.findColumn(therapeuticsTable, "Therapy");
        this.CTherapeuticsIdSampleStartMonth = Utils.findColumn(therapeuticsTable, "StartMonth");
        this.CTherapeuticsIdSampleStartYear = Utils.findColumn(therapeuticsTable, "StartYear");
        this.CTherapeuticsId = Utils.findColumn(therapeuticsTable, "Id_Therapeutics");
        this.CTherapeuticsIdSampleEndMonth = Utils.findColumn(therapeuticsTable, "EndMonth");
        this.CTherapeuticsIdSampleEndYear = Utils.findColumn(therapeuticsTable, "EndYear");        
        this.CTherapeuticsPatientId = Utils.findColumn(therapeuticsTable, "PatientID");
        this.CTherapeuticsYearCollection = Utils.findColumn(therapeuticsTable, "YearCollection");
        this.CTherapeuticsMonthCollection = Utils.findColumn(therapeuticsTable, "MonthCollection");

        this.CMedicinsIdMedicins = Utils.findColumn(medicinsTable, "Id_Medicins");
        this.CMedicinsMedicinCode = Utils.findColumn(medicinsTable, "MedicinCode");

        this.CTherapeuticMedicinsTableIdMedicins = Utils.findColumn(therapeuticMedicinsTable, "Id_Medicins");
        this.CTherapeuticMedicinsTableIdTherapeutics = Utils.findColumn(therapeuticMedicinsTable, "Id_Therapeutics");
        
        System.err.print("Creating indexes... ");
        this.sampleIndex = sampleTable.addIndex("Patient_Sample",
                new int[] { CSamplePatientID, CSampleYearCollection, CSampleMonthCollection });
        this.therapyIndex = therapeuticsTable.addIndex("Patient_Sample_Therapy",
                new int[] { CTherapeuticsPatientId, CTherapeuticsYearCollection,
                CTherapeuticsMonthCollection, CTherapeuticsTherapy},
                new boolean[] { false, true, true, false });
        System.err.println(" done");
        
        this.stringValueType = new ValueType("string");
    }
    
    private void fixAttributeTable(Table t, Attribute a, String mappingFile) {
        System.err.println("Fixing attribute table " + a.getName());
        ArrayList<String> tableIndex = t.getColumn(0);
        ArrayList<String> tableName = t.getColumn(1);
        Mappings mappings = Mappings.getInstance(mappingBasePath);
        for(int i = 1; i<tableIndex.size(); i++)
        {
            String value = tableName.get(i);
            boolean foundMatch = false;
            for(AttributeNominalValue anv : a.getAttributeNominalValues()) {
                if(value.equals(anv.getValue().trim())) {
                    foundMatch = true;
                    break;
                }
            }
            if(!foundMatch && !value.trim().equals("")) {
                String mapping = mappings.getMapping(mappingFile, value);
                if(mapping == null) {
                    System.err.println("Cannot map " + a.getName() + " :" + value);
                } else {
                    t.setValue(1, i, mapping);
                }
            }
        }
        System.err.println("Done fixing attribute table " + a.getName());
    }

    private void importTherapy() throws NumberFormatException {
        System.err.println("Importing therapy ...");
        int therapy = 0;
        int datesunknown = 0;

        HashMap<String, DrugGeneric> medicinsMap = new HashMap<String, DrugGeneric>();

        for (int i = 1; i < medicinsTable.numRows(); ++i) {
            String id = medicinsTable.valueAt(CMedicinsIdMedicins, i);
            String code = medicinsTable.valueAt(CMedicinsMedicinCode, i).toUpperCase();

            medicinsMap.put(id, new DrugGeneric(null, code, null));
        }

        /*
         * Therapy = 1 corresponds to the latest therapy, Therapy = 2 the previous, etc...
         * Strategy for messy therapy in portugal db:
         *   - Generally, therapy annotated with the last sample seems most accurate and includes
         *     therapy start/stop dates. Thus, we start with the latest sample.
         *   - as soon as we encounter an older therapy without stop date we simply collect previous therapies
         *     in one 'previous therapy experience therapy'
         *   - therapy number 4 is 'older therapies': they usually have no dates. Thus, we can only
         *     deduce an upperbound for the stop date, as
         *        min (sample date, last therapy start date)
         *     and we cannot know a start date.
         *     but we take the start date before the oldest sample collection time and we include any
         *     other treatment that is indicated in the older treatments.
         */
        String lastPatientId = null;
        String lastSampleId = null;
        Date lastStartDate = null;
        Date lastCollectionDate = null;

        Set<String> previousDrugs = null;
        Date previousEndDate = null;
        Date previousStartDate = null;
        
        for (int j = 1; j < therapeuticsTable.numRows(); ++j) {
            int row = therapyIndex.row(j);

            String Id_Therapeutics = therapeuticsTable.valueAt(CTherapeuticsId, row);
            String sampleId = therapeuticsTable.valueAt(CTherapeuticsIdSample, row);
            String startMonth = therapeuticsTable.valueAt(CTherapeuticsIdSampleStartMonth, row);
            String startYear = therapeuticsTable.valueAt(CTherapeuticsIdSampleStartYear, row);
            String endMonth = therapeuticsTable.valueAt(CTherapeuticsIdSampleEndMonth, row);
            String endYear = therapeuticsTable.valueAt(CTherapeuticsIdSampleEndYear, row);
            String patientId = therapeuticsTable.valueAt(CTherapeuticsPatientId, row);
            String collectionYear = therapeuticsTable.valueAt(CTherapeuticsYearCollection, row);
            String collectionMonth = therapeuticsTable.valueAt(CTherapeuticsMonthCollection, row);
            
            Date collectionDate = Utils.createDate(collectionYear, collectionMonth, null);

            if (collectionDate == null) {
                System.err.println("Sample.csv: sampleId='" + sampleId + "': "
                        + "Collection date is null: '" + collectionYear + "' '" + collectionMonth + "'");
            }
            
            //System.err.print(patientId + " " + collectionMonth + "/" + collectionYear
            //        + " (" + therapy + ") " + startMonth + "/" + startYear + " - "
            //        + endMonth + "/" + endYear + ":");

            int d = 0;
            
            Set<String> drugs = new TreeSet<String>();
            
            for (; (d = therapeuticMedicinsTable.findInColumn(CTherapeuticMedicinsTableIdTherapeutics, Id_Therapeutics, d + 1)) != -1; ) {
                String drug = therapeuticMedicinsTable.valueAt(CTherapeuticMedicinsTableIdMedicins, d);
                //System.err.print(" " + drug);
                drugs.add(drug);
            }
            //System.err.println();
            
            if (!patientId.equals(lastPatientId)) {
                /* new patient */
                if (lastPatientId != null && previousDrugs != null
                    && !previousDrugs.isEmpty()) {

                    /*
                     * Make it start one day earlier
                     */
                    Calendar c = new GregorianCalendar();
                    c.setTime(previousStartDate);
                    c.add(Calendar.DAY_OF_YEAR, -1);
                    
                    storeTherapy(medicinsMap, previousEndDate, c.getTime(), lastPatientId, previousDrugs,
                                 "previous therapies (dates unknown)");
                    ++therapy;
                    ++datesunknown;
                }

                previousStartDate = null;
                previousEndDate = null;
                previousDrugs = null;
                lastSampleId = null;
                lastCollectionDate = null;
                lastStartDate = null;
            } else {
                /* not new patient but new sample: force to collect everything in previous regimens */
                if (!sampleId.equals(lastSampleId)) {
                    if (previousEndDate == null) {
                        previousEndDate = lastStartDate;
                        previousStartDate = previousEndDate;
                    }
                }
            }

            Date startDate = null;
            if (!startYear.equals("") && !startYear.equals("0") && Integer.parseInt(startYear) > 1970) {
                startDate = Utils.createDate(startYear, startMonth, null);
            }
            Date endDate = null;
            if (!endYear.equals("") && !endYear.equals("0") && Integer.parseInt(endYear) > 1970) {
                endDate = Utils.createDate(endYear, endMonth, null);
            }
            
            if (previousEndDate == null) {
                // we were still collecting accurate regimens
                if (startDate == null) {
                    // this is the first non-accurate entry or a previous sample: determine previousEndDate
                    if (lastStartDate != null)
                        // previous stop date must be before last start date
                        previousEndDate = lastStartDate;
                    else
                        // no accurate therapy information at all: everything is previous
                        if (lastCollectionDate != null)
                            previousEndDate = lastCollectionDate;
                        else
                            previousEndDate = collectionDate;
                    previousStartDate = previousEndDate;
                } else {
                    if (endDate == null)
                        if (lastStartDate != null)
                            // must be before last start date
                            endDate = lastStartDate;
                    
                    storeTherapy(medicinsMap, endDate, startDate, patientId, drugs, null);
                    ++therapy;
                }
            }
            
            if (previousEndDate != null) {
                if (previousDrugs == null)
                    previousDrugs = new TreeSet<String>();

                // include in regimen if the regimen is not for certain after this 'previous regimen'
                if (startDate == null || startDate.before(previousEndDate)) {
                    previousDrugs.addAll(drugs);
                }

                // lower previousStartDate to include collected sequence or even better a known start date
                if (startDate != null && previousStartDate.after(startDate))
                    previousStartDate = startDate;

                if (collectionDate.before(previousStartDate))
                    previousStartDate = collectionDate;
            }
            
            lastSampleId = sampleId;
            lastPatientId = patientId;
            lastStartDate = startDate;
            lastCollectionDate = collectionDate;
        }
        
        System.err.println( " n = " + therapy + " (with unknown dates: " + datesunknown + ")");
    }

    private void storeTherapy(Map<String, DrugGeneric> medicinsMap, Date endDate, Date startDate,
            		          String patientId, Set<String> drugs, String comment) {        
        Patient p = patientMap.get(patientId);
        if (p == null)
            return;

        if (endDate != null) {
            if (startDate.equals(endDate) || startDate.after(endDate)) {
                System.err.println("Something wrong with treatment for patient '" + patientId + "': " + startDate + " - " + endDate + ": ignoring.");
                return;
            }   
        }
        
        Therapy t = p.createTherapy(startDate);
        t.setStopDate(endDate);
        t.setComment(comment);

        for (String drug : drugs) {
           TherapyGeneric tg = new TherapyGeneric(new TherapyGenericId(t, medicinsMap.get(drug)),false,false);
            t.getTherapyGenerics().add(tg);
        }
    }

    private void importViralLoad_CD4() {
        System.err.print("Importing viral loads and CD4 measurements ...");

        int vl = 0;
        int cd4 = 0;
        
        for (int i = 1; i < sampleTable.numRows(); ++i) {
          int row = sampleIndex.row(i);

          String patientId = sampleTable.valueAt(CSamplePatientID, row);
          String viralLoad = sampleTable.valueAt(CSampleViralLoad, row);
          String cd4Count = sampleTable.valueAt(CSampleCD4Count, row);
          String collectionYear = sampleTable.valueAt(CSampleYearCollection, row);
          String collectionMonth = sampleTable.valueAt(CSampleMonthCollection, row);
          String sampleId = sampleTable.valueAt(CSampleSampleID, row);
          //System.err.println(sampleId + " " + collectionYear + " " + collectionMonth);

          Patient p = patientMap.get(patientId);
          if (p == null)
              continue;
          
          if (!viralLoad.equals("") && !viralLoad.equals("0")) {
              TestResult t = p.createTestResult(StandardObjects.getGenericViralLoadTest());
              t.setValue("=" + viralLoad);
              t.setTestDate(Utils.createDate(collectionYear, collectionMonth, null));
              t.setSampleId(sampleId);
              ++vl;
          }
          
          if (!cd4Count.equals("") && !cd4Count.equals("0")) {
              TestResult t = p.createTestResult(StandardObjects.getGenericCD4Test());
              t.setValue(cd4Count);
              t.setTestDate(Utils.createDate(collectionYear, collectionMonth, null));
              t.setSampleId(sampleId);
              ++cd4;
          }
      }

        System.err.println(" CD4 (n = " + cd4 + "), VL (n = " + vl + ")");
    }

    private void importPatients()  {
        System.err.println("Importing patients... ");
		patientMap = new TreeMap<String, Patient>();
		
		String lastPatientId = null;
        for (int i = 1; i < sampleTable.numRows(); ++i) {
            int row = sampleIndex.row(i);
            
            String patientId = sampleTable.valueAt(CSamplePatientID, row);
            
            if (patientId.length() == 0) {
                System.err.println("Row: " + row + ": patient Id = '' ?");
                continue;
            }
            if (!patientId.equals(lastPatientId)) {
                lastPatientId = patientId;

                Patient patient = patientMap.get(patientId);

                if (patient == null) {
                    String yearBirth = sampleTable.valueAt(CSampleYearBirth, row);

                    patient = new Patient();
                    patient.setPatientId(patientId);
                    patient.setBirthDate(Utils.createDate(yearBirth, "", null));
                    
                    patientMap.put(patientId, patient);
                }
            }
        }

        System.err.println(" n = " + patientMap.size());
    }

    public void importSequences() throws FileNotFoundException {
        System.err.println("Importing sequences ...");
        
        HashMap<String, Integer> sampleMap = new HashMap<String, Integer>();
        for (int i = 1; i < sampleTable.numRows(); ++i) {
            sampleMap.put(sampleTable.valueAt(CSampleSampleID, i), new Integer(i));
        }

        File dir = new File(sequenceDirName);
        File[] files = dir.listFiles();
        
        int seq_found = 0;
        for (int i = 0; i < files.length; ++i) {            
            FastaRead fr = FastaHelper.readFastaFile(files[i], true);

            switch (fr.status_) {
            case Valid:
            case ValidButFixed:

                break;
            case MultipleSequences:
            case FileNotFound:
            case Invalid:
                System.err.println("invalid fasta " + files[i]);
                continue;
            }

            String seqSampleId = fr.seq_.getName();
            String seqFileSampleId = files[i].getName();
            seqFileSampleId = seqFileSampleId.substring(0, seqFileSampleId.indexOf('.'));
            String seqAltName = seqSampleId;
            if (seqSampleId.charAt(seqSampleId.length() - 1) == 's')
                seqAltName = seqAltName.substring(0, seqAltName.length() - 1);
                
            String seqFinalSampleId = null;
            if (sampleMap.containsKey(seqSampleId))
                seqFinalSampleId = seqSampleId;
            else
                if (sampleMap.containsKey(seqFileSampleId))
                    seqFinalSampleId = seqFileSampleId;
                else if (sampleMap.containsKey(seqAltName))
                    seqFinalSampleId = seqAltName;

            if (seqFinalSampleId == null)
                System.err.println("? " + seqSampleId + " " + seqFileSampleId);
            else {
                int row = ((Integer) sampleMap.get(seqFinalSampleId)).intValue();
                ++seq_found;

                String patientId = sampleTable.valueAt(CSamplePatientID, row);

                Date sampleDate = Utils.createDate(sampleTable.valueAt(CSampleYearCollection, row),
                        sampleTable.valueAt(CSampleMonthCollection, row) , null);

                Patient p = patientMap.get(patientId);
                if (p == null)
                    continue;

                if(viralIsolateHM.get(seqFinalSampleId)==null)
                {
                ViralIsolate vi = p.createViralIsolate();
                vi.setSampleDate(sampleDate);
                vi.setSampleId(seqFinalSampleId);
                
                NtSequence nts = new NtSequence(vi);
                vi.getNtSequences().add(nts);
                nts.setNucleotides(fr.xna_);
                nts.setLabel("Sequence 1");

                viralIsolateHM.put(seqFinalSampleId, vi);
                }
                else
                {
                    System.err.println("Duplicate viral isolate " +  seqFinalSampleId + " -> ignoring");
                }
            }
        }
        
        System.err.println("Sequences: " + seq_found);
    }
    
    public void importPatientAttributes() {
        System.err.println("Importing patient attributes");

        AttributeGroup portugal = new AttributeGroup("PT");
        AttributeGroup regadb = new AttributeGroup("RegaDB");
        
        List<Attribute> regadbAttributes = Utils.prepareRegaDBAttributes();

        Attribute clinicalFileAttribute = new Attribute("Clinical File Number");
        clinicalFileAttribute.setAttributeGroup(portugal);
        clinicalFileAttribute.setValueType(stringValueType);

        ArrayList<NominalAttribute> nominals = new ArrayList<NominalAttribute>();
        nominals.add(new NominalAttribute("Institution", CSampleIdInstitution, institutionTable));
        nominals.get(nominals.size() - 1).attribute.setAttributeGroup(portugal);
        
        //change tables see country of origin
        nominals.add(new NominalAttribute(Utils.selectAttribute("Transmission group", regadbAttributes), CSampleIdTransmissionGroup, transmissionGroupTable));
        nominals.add(new NominalAttribute(Utils.selectAttribute("Geographic origin", regadbAttributes), CSampleIdGeographicOrigin, geographicOriginTable));
        nominals.add(new NominalAttribute(Utils.selectAttribute("Ethnicity", regadbAttributes), CSampleIdEthnicity, ethnicityTable));
        nominals.add(new NominalAttribute(Utils.selectAttribute("Country of origin", regadbAttributes), CSampleIdCountry, countryTable));
        
        nominals.add(new NominalAttribute("Gender", CSampleGender, new String[] { "M", "F" },
                                          new String[] { "male", "female" } ));
        nominals.get(nominals.size() - 1).attribute.setAttributeGroup(regadb);
        
        String lastPatientId = null;
        for (int i = 1; i < sampleTable.numRows(); ++i) {
            int row = sampleIndex.row(i);
            
            String patientId = sampleTable.valueAt(CSamplePatientID, row);
            if (!patientId.equals(lastPatientId)) {
                lastPatientId = patientId;
                
                Patient p = patientMap.get(patientId);
                if (p == null)
                    continue;

                String clinicalFileNumber = sampleTable.valueAt(CSampleClinicalFileNumber, row);
                if (!clinicalFileNumber.equals("")) {
                    PatientAttributeValue v = p.createPatientAttributeValue(clinicalFileAttribute);
                    v.setValue(clinicalFileNumber);
                }

                for (int j = 0; j < nominals.size(); ++j) {
                    NominalAttribute attr = (NominalAttribute) nominals.get(j);
                    String value = sampleTable.valueAt(attr.column, row);
                    AttributeNominalValue vv = attr.nominalValueMap.get(value);
                    if (vv != null) {
                        PatientAttributeValue v = p.createPatientAttributeValue(attr.attribute);
                        v.setAttributeNominalValue(vv);
                        //System.err.println(value + " " + v.getAttributeNominalValue().getValue());
                    }
                }
            }
        }
    }
    
    public void exportToXml() {
        Utils.exportPatientsXML(patientMap, patientXmlFile.getAbsolutePath());
        Utils.exportNTXMLFromPatients(patientMap, sequenceXmlFile.getAbsolutePath());
    }
    
 	public static void main(String[] args)
        throws FileNotFoundException, ClassNotFoundException {
        
        ImportPortugalDB instance; 
        
        File test = new File(args[0]);
        if(test.isDirectory())
        {
            String dir = args[0];
            instance = new ImportPortugalDB(
                    dir+File.separatorChar+"Sample.csv",
                    dir+File.separatorChar+"Country.csv",
                    dir+File.separatorChar+"Ethnicity.csv",
                    dir+File.separatorChar+"GeographicOrigin.csv",
                    dir+File.separatorChar+"Institution.csv",
                    dir+File.separatorChar+"Medicins.csv",
                    dir+File.separatorChar+"Therapeutics.csv",
                    dir+File.separatorChar+"TherapeuticMedicins.csv",
                    dir+File.separatorChar+"TransmissionGroup.csv",
                    args[1],
                    dir+File.separatorChar+"patients.xml",
                    dir+File.separatorChar+"sequences.xml");
                    
        }
        else
        {
            instance = new ImportPortugalDB(    args[0], args[1], args[2], args[3], args[4],
                                                args[5], args[6], args[7], args[8], args[9],
                                                args[10], args[11]);
        }
        
        instance.importPatients();
        instance.importViralLoad_CD4();
        instance.importTherapy();

        instance.importSequences();
        instance.importPatientAttributes();
        instance.exportToXml();
        System.err.println("Finished");
    }
}
