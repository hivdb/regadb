/*
 * Created on Aug 24, 2005
 *
 */
package net.sf.regadb.io.db.portugal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
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
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.exportXML.ExportToXML;
import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.FileProvider;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.ac.shef.wit.simmetrics.similaritymetrics.NeedlemanWunch;

/**
 * @author kdforc0
 */
public class ImportPortugalDB {    
    static final String SOURCE = "PT";
    
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
 
    public ImportPortugalDB(String sampleFName, String countryFName,
            				String ethnicityFName, String geographicOriginFName,
            				String institutionFName, String medicinsFName,
            				String therapeuticsFName, String therapeuticMedicinsFname,
            				String transmissionGroupFName, String sequenceDirName)
            throws FileNotFoundException {

        System.err.println("Reading data...");
        this.sampleTable = readTable(sampleFName);
        this.countryTable = readTable(countryFName);
        this.ethnicityTable = readTable(ethnicityFName);
        this.geographicOriginTable = readTable(geographicOriginFName);
        this.institutionTable = readTable(institutionFName);
        this.medicinsTable = readTable(medicinsFName);
        this.therapeuticsTable = readTable(therapeuticsFName);
        this.therapeuticMedicinsTable = readTable(therapeuticMedicinsFname);
        this.transmissionGroupTable = readTable(transmissionGroupFName);
        System.err.println("done.");
        
        System.err.println("Fixing the country of origin list");
        List<Attribute> regadbAttributesList = Utils.prepareRegaDBAttributes();
        Attribute countryOfOrigin = Utils.selectAttribute("Country of origin", regadbAttributesList);
        ArrayList<String> countryIndex = this.countryTable.getColumn(0);
        ArrayList<String> countryName = this.countryTable.getColumn(1);
        String country;
        String bestCountryMatchForNow="";
        float score;
        NeedlemanWunch nmw = new NeedlemanWunch();
        for(int i = 1; i<countryIndex.size(); i++)
        {
            score = Integer.MIN_VALUE;
            country = countryName.get(i);
            if(!country.trim().equals("")) {
                for(AttributeNominalValue anv : countryOfOrigin.getAttributeNominalValues())
                {
                    float oneScore = nmw.getSimilarity(country, anv.getValue());
                    if(oneScore>score)
                    {
                        bestCountryMatchForNow = anv.getValue();
                        score = oneScore;
                    }
                }
                if(!country.trim().equals(bestCountryMatchForNow.trim())) {
                System.err.println("country:"+country+" -> "+bestCountryMatchForNow+" :: "+score);
                countryTable.setValue(1, i, bestCountryMatchForNow);
                }
            }
            
        }
        System.err.println("Done fixing the country of origin list");
        
        this.sequenceDirName = sequenceDirName;
        
        this.CSamplePatientID = findColumn(sampleTable, "PatientID");
        this.CSampleYearCollection = findColumn(sampleTable, "YearCollection");
        this.CSampleMonthCollection = findColumn(sampleTable, "MonthCollection");
        this.CSampleYearBirth = findColumn(sampleTable, "YearBirth");
        this.CSampleGender = findColumn(sampleTable, "Gender");
        this.CSampleViralLoad = findColumn(sampleTable, "ViralLoad");
        this.CSampleCD4Count = findColumn(sampleTable, "CD4Count");
        this.CSampleSampleID = findColumn(sampleTable, "SampleID");
        this.CSampleId_Sample = findColumn(sampleTable, "ID_Sample");
        this.CSampleClinicalFileNumber = findColumn(sampleTable, "ClinicalFileNumber");
        this.CSampleIdInstitution = findColumn(sampleTable, "Id_Institution");
        this.CSampleIdTransmissionGroup = findColumn(sampleTable, "Id_TransmissionGroup");
        this.CSampleIdGeographicOrigin = findColumn(sampleTable, "Id_GeographicOrigin");
        this.CSampleIdEthnicity = findColumn(sampleTable, "Id_Ethnicity");
        this.CSampleIdCountry = findColumn(sampleTable, "Id_Country");

        this.CTherapeuticsIdSample = findColumn(therapeuticsTable, "Id_Sample");

        System.err.println("Merging therapeutics data... (from "+therapeuticsFName+")");
        therapeuticsTable.merge(sampleTable, CTherapeuticsIdSample, CSampleId_Sample, true);
        System.err.println("OK.");

        this.CTherapeuticsTherapy = findColumn(therapeuticsTable, "Therapy");
        this.CTherapeuticsIdSampleStartMonth = findColumn(therapeuticsTable, "StartMonth");
        this.CTherapeuticsIdSampleStartYear = findColumn(therapeuticsTable, "StartYear");
        this.CTherapeuticsId = findColumn(therapeuticsTable, "Id_Therapeutics");
        this.CTherapeuticsIdSampleEndMonth = findColumn(therapeuticsTable, "EndMonth");
        this.CTherapeuticsIdSampleEndYear = findColumn(therapeuticsTable, "EndYear");        
        this.CTherapeuticsPatientId = findColumn(therapeuticsTable, "PatientID");
        this.CTherapeuticsYearCollection = findColumn(therapeuticsTable, "YearCollection");
        this.CTherapeuticsMonthCollection = findColumn(therapeuticsTable, "MonthCollection");

        this.CMedicinsIdMedicins = findColumn(medicinsTable, "Id_Medicins");
        this.CMedicinsMedicinCode = findColumn(medicinsTable, "MedicinCode");

        this.CTherapeuticMedicinsTableIdMedicins = findColumn(therapeuticMedicinsTable, "Id_Medicins");
        this.CTherapeuticMedicinsTableIdTherapeutics = findColumn(therapeuticMedicinsTable, "Id_Therapeutics");
        
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

    private void importTherapy() throws NumberFormatException {
        System.err.println("Importing therapy ...");
        int therapy = 0;
        int datesunknown = 0;

        HashMap<String, DrugGeneric> medicinsMap = new HashMap<String, DrugGeneric>();

        for (int i = 1; i < medicinsTable.numRows(); ++i) {
            String id = medicinsTable.valueAt(CMedicinsIdMedicins, i);
            String code = medicinsTable.valueAt(CMedicinsMedicinCode, i).toUpperCase();

            medicinsMap.put(id, new DrugGeneric(null, code, null)); // FIXME
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
            		          String patientId, Set drugs, String comment) {
        //System.err.print("TH: " + patientId + " " + startDate + " - " + endDate + " (" + comment + "): ");
        
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

        for (Iterator k = drugs.iterator(); k.hasNext();) {
            String drug = (String) k.next();
            //System.err.print(" " + drug);

            TherapyGeneric tg = new TherapyGeneric(new TherapyGenericId(t, medicinsMap.get(drug)));
            t.getTherapyGenerics().add(tg);
        }
        //System.err.println();
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

    public void importSequencesNoAlign(String sequencesFile) throws FileNotFoundException {
        System.err.println("Importing sequences ...");

        ExportToXML l = new ExportToXML();
        Element root = new Element("viralIsolates");
        
        HashMap<String, Integer> sampleMap = new HashMap<String, Integer>();
        for (int i = 1; i < sampleTable.numRows(); ++i) {
            sampleMap.put(sampleTable.valueAt(CSampleSampleID, i), new Integer(i));
        }

        File dir = new File(sequenceDirName);
        File[] files = dir.listFiles();
        
        Map<String, Protein> proteins = new HashMap<String, Protein>();
        proteins.put("PRO", new Protein("PRO", "protease"));
        proteins.put("RT", new Protein("RT", "reverse transcriptase"));

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
                nts.setLabel("Sequence1");
                
                Element viralIsolateE = new Element("viralIsolates-el");
                root.addContent(viralIsolateE);

                l.writeViralIsolate(vi, viralIsolateE);
                viralIsolateHM.put(seqFinalSampleId, vi);
                }
                else
                {
                    System.err.println("Duplicate viral isolate " +  seqFinalSampleId + " -> ignoring");
                }
            }
        }
        
        Document n = new Document(root);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());

        java.io.FileWriter writer;
        try {
            writer = new java.io.FileWriter(sequencesFile);
            outputter.output(n, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.err.println("Sequences: " + seq_found);
    }
    
    public void importPatientAttributes() {
        System.err.println("Importing patient attributes");

        AttributeGroup portugal = new AttributeGroup("PT");
        AttributeGroup regadb = new AttributeGroup("RegaDB");

        Attribute clinicalFileAttribute = new Attribute("Clinical File Number");
        clinicalFileAttribute.setAttributeGroup(portugal);
        clinicalFileAttribute.setValueType(stringValueType);

		ArrayList<NominalAttribute> nominals = new ArrayList<NominalAttribute>();
		nominals.add(new NominalAttribute("Institution", CSampleIdInstitution, institutionTable));
        nominals.get(nominals.size() - 1).attribute.setAttributeGroup(portugal);
		nominals.add(new NominalAttribute("Transmission group", CSampleIdTransmissionGroup, transmissionGroupTable));
        nominals.get(nominals.size() - 1).attribute.setAttributeGroup(regadb);
		nominals.add(new NominalAttribute("Geographic origin", CSampleIdGeographicOrigin, geographicOriginTable));
        nominals.get(nominals.size() - 1).attribute.setAttributeGroup(regadb);
		nominals.add(new NominalAttribute("Ethnicity", CSampleIdEthnicity, ethnicityTable));
        nominals.get(nominals.size() - 1).attribute.setAttributeGroup(regadb);
		nominals.add(new NominalAttribute("Country of origin pt", CSampleIdCountry, countryTable));
        nominals.get(nominals.size() - 1).attribute.setAttributeGroup(portugal);
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
                    args[1]);
                    
        }
        else
        {
            instance = new ImportPortugalDB(    args[0], args[1], args[2], args[3], args[4],
                                                args[5], args[6], args[7], args[8], args[9]);
        }
        
        instance.importPatients();
        instance.importViralLoad_CD4();
        instance.importTherapy();

        instance.importSequencesNoAlign("sequences.xml");
        instance.importPatientAttributes();
        instance.exportXML("result.xml");
        System.err.println("Finished");
    }
}
