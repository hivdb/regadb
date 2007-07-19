package net.sf.regadb.io.db.portugal.hiv2;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.io.exportXML.ExportToXML;
import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.FileProvider;
import net.sf.regadb.util.pair.Pair;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.regadb.util.string.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.ac.shef.wit.simmetrics.similaritymetrics.NeedlemanWunch;

public class ImportPortugalDBHIV2 
{
    private String workingDir;
    
    private Table sampleTable;
    private Table patientTable;
    private Table therapyTable;
    
    private Table instituteTable;
    
    private List<Patient> patientList = new ArrayList<Patient>();
    
    public static void main(String [] args)
    {
        ImportPortugalDBHIV2 imp = new ImportPortugalDBHIV2();
        
        imp.workingDir = args[0];
        
        imp.generateCsvFiles();
        
        imp.loadCsvFiles();
        
        //imp.listGeographicOrigins();
        //imp.listInstitutes();
        List<Attribute> regadbAttributesList = imp.prepareRegaDBAttributes();
        Attribute countryOfOrigin = imp.selectAttribute("Country of origin", regadbAttributesList);
        Attribute gender = imp.selectAttribute("Gender", regadbAttributesList);
        imp.parsePatient(gender, countryOfOrigin);
        imp.parseSampleData();
        
        imp.exportXML(imp.workingDir + File.separatorChar + "export.xml");
        
        for(Patient p : imp.patientList)
        {
            //imp.printPatient(p);
        }
    }
    
    private void printPatient(Patient p)
    {
        System.err.println("Patient: " + p.getPatientId());
        System.err.println("Name f+l: " + p.getFirstName() + " + " + p.getLastName());
        System.err.println("Birthdate: " + p.getBirthDate());
        for(PatientAttributeValue pav : p.getPatientAttributeValues())
        {
            String value =  pav.getValue()!=null?pav.getValue():pav.getAttributeNominalValue().getValue();
            System.err.println("Attribute: " + pav.getId().getAttribute().getName() + " - " + value);
        }
        for(TestResult tr : p.getTestResults())
        {
            System.err.println("Test:" + tr.getTest().getDescription() + ":" + tr.getValue());
        }
        System.err.println("--------------");
    }
    
    private void exportXML(String fileName) {
        ExportToXML l = new ExportToXML();
        Element root = new Element("patients");
        
        for (Patient p :patientList) {
            Element patient = new Element("patients-el");
            root.addContent(patient);

            //Patient p = patientMap.get(patientId);
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
    
    private void listInstitutes()
    {
        Set<String> institutes = new HashSet<String>();
        String institute;
        for(int i = 1; i < patientTable.numRows(); i++)
        {
            institute = patientTable.valueAt(7, i);
            if(!"".equals(institute))
            {
                institutes.add(institute);
            }
        }
        
        List<Pair<String, String>> hiv1Institutes = new ArrayList<Pair<String, String>>();
        for(int i = 1; i<instituteTable.numRows(); i++)
        {
            hiv1Institutes.add(new Pair<String, String>(instituteTable.valueAt(1, i), instituteTable.valueAt(2, i)));
        }
        for(String i : institutes)
        {
            System.err.println(i + "-" + getBestInstituteMatch(hiv1Institutes, i));
        }
    }
    
    private void listGeographicOrigins()
    {
        Set<String> countries = new HashSet<String>();
        String country;
        for(int i = 1; i < patientTable.numRows(); i++)
        {
            country = patientTable.valueAt(6, i);
            if(!"".equals(country))
            {
                countries.add(country);
            }
        }
        
        System.err.println("Fixing the country of origin list");
        List<Attribute> regadbAttributesList = prepareRegaDBAttributes();
        Attribute countryOfOrigin = selectAttribute("Country of origin", regadbAttributesList);
        
        for(String c : countries)
        {
            System.err.println(clearCountry(c) + " - " +getBestCountryMatch(countryOfOrigin.getAttributeNominalValues(), clearCountry(c)));
        }
        System.err.println("Done fixing the country of origin list");
    }
    
    private String clearCountry(String country)
    {
        String toReturn = country.toLowerCase().replace('\ufffd', 'a');
        if(toReturn.contains("tome"))
        {
            toReturn += " e principe";
        }
        return toReturn;
    }
    
    private String getBestInstituteMatch(List<Pair<String, String>> nominalValues, String institute)
    {
        for(Pair<String, String> nv : nominalValues)
        {
            if(institute.equals(nv.getKey()))
            {
                return nv.getKey();
            }
        }
        float score = Integer.MIN_VALUE;
        NeedlemanWunch nmw = new NeedlemanWunch();
        String bestInstituteMatchForNow="";
        institute = institute.toLowerCase();
        for(Pair<String, String> nv : nominalValues)
        {
            float oneScore = nmw.getSimilarity(institute, nv.getValue());
            if(oneScore>score)
            {
                bestInstituteMatchForNow = nv.getValue();
                score = oneScore;
            }
        }
        
        return bestInstituteMatchForNow;
    }
    
    private String getBestCountryMatch(Set<AttributeNominalValue> nominalValues, String country)
    {
        float score = Integer.MIN_VALUE;
        NeedlemanWunch nmw = new NeedlemanWunch();
        String bestCountryMatchForNow="";
        for(AttributeNominalValue anv : nominalValues)
        {
            float oneScore = nmw.getSimilarity(country, anv.getValue());
            if(oneScore>score)
            {
                bestCountryMatchForNow = anv.getValue();
                score = oneScore;
            }
        }
        
        return bestCountryMatchForNow;
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
    
    private List<Attribute> prepareRegaDBAttributes()
    {
        RegaDBSettings.getInstance().initProxySettings();
        
        FileProvider fp = new FileProvider();
        List<Attribute> list = null;
        File attributesFile = null;
        try {
            attributesFile = File.createTempFile("attributes", "xml");
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
    
    private void parsePatient(Attribute genderA, Attribute countryOfOriginA)
    {
        String patientId;
        String patientIdHospital;
        String name;
        String initials;
        String gender;
        String birth_date;
        String geographic_origin;
        String institute;
        String date_first_time_in_lab;
        String hiv1_coinfection;
        String comments;
        
        Attribute initialsA = new Attribute("Initials");
        initialsA.setValueType(new ValueType("string"));
        initialsA.setAttributeGroup(new AttributeGroup("PT"));
        
        Attribute clinicalFileNumber = new Attribute("Clinical file number");
        clinicalFileNumber.setValueType(new ValueType("string"));
        clinicalFileNumber.setAttributeGroup(new AttributeGroup("PT"));
        
        Patient p;
        for(int i = 1; i < patientTable.numRows(); i++)
        {
            patientId = patientTable.valueAt(0, i);
            patientIdHospital = patientTable.valueAt(1, i);
            if("0".equals(patientIdHospital))
            {
                System.err.println("No clinical file number for pt " + patientId);
            }
                p = new Patient();

                //check for duplicate patient id's
                for(Patient pt : patientList)
                {
                    if(pt.getPatientId().equals(patientId))
                    {
                        throw new RuntimeException("duplicate patient id's: " + patientId);
                    }
                }
                //check for duplicate patient id's
                
                patientList.add(p);
                p.setPatientId(patientId);
                
                //Clinical file number
                p.createPatientAttributeValue(clinicalFileNumber).setValue(patientIdHospital);
                //Clinical file number

                //Firstname+lastname
                name = patientTable.valueAt(2, i);
                String [] nameTokens = name.split(" ");
                String firstName = "";
                String lastName = "";
                if(nameTokens.length>=3)
                {
                    for(int j = 0; j<nameTokens.length-2; j++)
                    {
                        firstName += nameTokens[j] + " ";
                    }
                    lastName = nameTokens[nameTokens.length-2] + " " + nameTokens[nameTokens.length-1];
                }
                else if(nameTokens.length == 2)
                {
                    firstName = nameTokens[0];
                    lastName = nameTokens[1];
                }
                else
                {
                    lastName = nameTokens[0];
                }
                p.setFirstName(firstName);
                p.setLastName(lastName);
                //Firstname+lastname

                //Initials
                initials = patientTable.valueAt(3, i);
                PatientAttributeValue pav = p.createPatientAttributeValue(initialsA);
                pav.setValue(initials);
                //Initials
                
                //Gender
                gender = patientTable.valueAt(4, i);
                if(!"".equals(gender))
                {
                    if(gender.toLowerCase().equals("f"))
                    {
                        p.createPatientAttributeValue(genderA).setAttributeNominalValue(new AttributeNominalValue(genderA, "female"));
                    }
                    else if(gender.toLowerCase().equals("m"))
                    {
                        p.createPatientAttributeValue(genderA).setAttributeNominalValue(new AttributeNominalValue(genderA, "male"));
                    }
                    else
                    {
                        throw new RuntimeException(gender + " is not a gender");
                    }
                }
                else
                {
                    System.err.println("Patient" + patientId + "has no gender");
                }
                //Gender
                
                //Birth date
                birth_date = patientTable.valueAt(5, i);
                p.setBirthDate(parseDate(birth_date));
                if(p.getBirthDate()==null)
                {
                    System.err.println("No birthdate avialable for patientId:" + patientId);
                }
                //Birth date
                
                //Geographic origin
                geographic_origin = patientTable.valueAt(6, i);
                if(!clearCountry(geographic_origin).toLowerCase().equals("africa"))
                {
                    p.createPatientAttributeValue(countryOfOriginA).setAttributeNominalValue(new AttributeNominalValue(genderA, this.getBestCountryMatch(countryOfOriginA.getAttributeNominalValues(), geographic_origin)));
                }
                else
                {
                    System.err.println("If country of origin is *africa*, put a continent attribute!");
                }
                //Geographic origin
                
                institute = patientTable.valueAt(7, i);
                
                date_first_time_in_lab = patientTable.valueAt(8, i);
                hiv1_coinfection = patientTable.valueAt(9, i);
                comments = patientTable.valueAt(10, i);
            //}
        }
    }
    
    private void parseSampleData()
    {
        String patientId;
        Patient pt = null;
        String sampleId;
        String sampleDate;
        Date sampleDateD;
        String cd4;
        String rt_pcr;
        String viralLoad;
        String log10;
        String localisation_ot_sample;
        String comment;
        for(int i = 1; i < sampleTable.numRows(); i++)
        {
            patientId = sampleTable.valueAt(0, i);
            sampleId = sampleTable.valueAt(1, i);
            sampleDate = sampleTable.valueAt(2, i);
            cd4 = sampleTable.valueAt(3, i);
            rt_pcr = sampleTable.valueAt(4, i);
            viralLoad = sampleTable.valueAt(5, i);
            log10 = sampleTable.valueAt(6, i);
            localisation_ot_sample = sampleTable.valueAt(7, i);
            comment = sampleTable.valueAt(8, i);
            
            if(!patientId.equals("0"))
            {
                pt = null;
                for(Patient p : patientList)
                {
                    if(p.getPatientId().equals(patientId))
                    {
                        pt = p;
                        break;
                    }
                }
                
                if(pt==null)
                {
                    System.err.println("Patient referred to in this sample data does not exist yet - create new " + patientId);
                    pt = new Patient();
                    pt.setPatientId(patientId);
                    patientList.add(pt);
                }

                
                if(!sampleId.equals(""))
                {
                    sampleDateD = parseDate(sampleDate);
                    if(sampleDateD==null)
                    {
                        System.err.println("No sampledate available for patientId+sampleId:" + patientId + "+" + sampleId);
                    }
                    
                    if(!"".equals(cd4))
                    {
                        putCD4(pt, cd4, sampleDateD, sampleId);
                    }
                    
                    if(!"".equals(viralLoad))
                    {
                        putViralLoad(pt, viralLoad, log10, sampleDateD, sampleId);
                    }
                    
                    if(!"".equals(localisation_ot_sample))
                    {
                        System.err.println("There is a localisation of the sample for patient+sample:" + patientId + "+" + sampleId);
                    }
                    
                    if(!"".equals(comment))
                    {
                        if("RT".toLowerCase().equals(comment.toLowerCase()))
                        {
                            //there is said a resistance test has been done
                            //look for it in the fasta directory
                            //if it can be found, it is ok, the sequence will be retrieved in a next step
                            File fasta = findFastaFile(sampleId);
                            if(fasta==null)
                            {
                                System.err.println("Could not find fasta file for sample " + sampleId);
                            }
                        }
                    }
                }
                else
                {
                    System.err.println("Wrong sampleid at line " + (i+1));
                }
            }
        }
    }
    
    public File findFastaFile(String sampleId)
    {
        File seqDir = new File(workingDir + File.separatorChar + "sequences");
        for(File f : seqDir.listFiles())
        {
            if(f.getName().startsWith(sampleId))
            {
                return f;
            }
        }
        return null;
    }
    
    public String findTherapyData(String sampleId, Date sampleDate)
    {
        String sampleIdT;
        String toReturn;
        for(int i = 1; i < therapyTable.numRows(); i++)
        {
            sampleIdT = therapyTable.valueAt(1, i);
            if(sampleIdT.equals(sampleId))
            {
                if(parseDate(therapyTable.valueAt(2, i)).equals(sampleDate))
                {
                    toReturn = therapyTable.valueAt(3, i);
                    toReturn = toReturn.toLowerCase().replace('\ufffd', 'a');
                    if(toReturn.equals("nao"))
                        return "NO";
                    else if(toReturn.equals("sim"))
                        return "YES";
                    else if(toReturn.equals("desconhecido"))
                        return "UNKNOWN";
                    else throw new RuntimeException("unknown string in therapy data: " + toReturn);
                }
                else
                {
                    System.err.println("Sample info in therapy file 's date does not match");
                }
            }
        }
        
        return null;
    }
    
    private void putCD4(Patient p, String cd4Count, Date date, String sampleId)
    {
        TestResult t = p.createTestResult(StandardObjects.getGenericCD4Test());
        try
        {
            int cd4 = Integer.parseInt(cd4Count);
        }
        catch(NumberFormatException nfe)
        {
            throw new RuntimeException("Illegal CD4:" + cd4Count);
        }
        
        t.setValue(cd4Count);
        t.setTestDate(date);
        t.setSampleId(sampleId);
    }
    
    private void putViralLoad(Patient p, String viralLoad, String log, Date date, String sampleId)
    {
        TestResult t = p.createTestResult(StandardObjects.getGenericViralLoadTest());
        try
        {
            double vl = Double.parseDouble(viralLoad);
        }
        catch (NumberFormatException e)
        {
            throw new RuntimeException("Illegal Viral Load:" + viralLoad);
        }
        if(log!=null && !"".equals(log))
        {
            try
            {
                int vlLog = Integer.parseInt(log);
            }
            catch(NumberFormatException nfe)
            {
                throw new RuntimeException("Illegal Viral Load Log:" + log);
            }
        }
        t.setValue("=" + viralLoad);
        t.setTestDate(date);
        t.setSampleId(sampleId);
        p.getTestResults().add(t);
    }
    
    private Date parseDate(String date)
    {
        if("".equals(date))
            return null;
        
        String dateNoTime = date.split(" ")[0];
        String [] dateTokens = dateNoTime.split("-");
       
        return createDate(dateTokens[2], dateTokens[1], dateTokens[0]);
    }
    
    private void loadCsvFiles()
    {
        patientTable = readTable(workingDir + File.separatorChar + "Ficha_de_doente.csv");
        sampleTable = readTable(workingDir + File.separatorChar + "Análises_dos_Doentes.csv");
        therapyTable = readTable(workingDir + File.separatorChar + "Terapêutica.csv");
        instituteTable = readTable(workingDir + File.separatorChar + "Institution.csv");
    }
    
    private Table readTable(String filename)
    {
        System.err.println("Reading: " + filename);
        try {
            return new Table(new BufferedInputStream(new FileInputStream(filename)), false);
        } catch (FileNotFoundException e) {
            return null;
        }
    }
    
    private static Date createDate(String yearStr, String monthStr, String dayString) {
        Calendar cal = Calendar.getInstance();

        if (!yearStr.equals("")) {
            int year, month;

            year = Integer.parseInt(yearStr);
            if (year < 1900)
                return null;

            if (!monthStr.equals("")) {
                month = Integer.parseInt(monthStr);
            } else
                month = 0;

            int day = 1;
            if(dayString!=null)
                day = Integer.parseInt(dayString);
            cal.set(year, month, day);
            return new Date(cal.getTimeInMillis());
        } else {
            return null;
        }
    }
    
    private void generateCsvFiles()
    {
        File work = new File(workingDir);
        String thisLine;
        StringBuffer outputFile = new StringBuffer();
        String token;
        for(File txtFile : work.listFiles())
        {
            if(txtFile.getAbsolutePath().endsWith(".txt"))
            {
                try
                {
                    InputStream is = new FileInputStream(txtFile);
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    while ((thisLine = br.readLine()) != null)
                    {
                        StringTokenizer st = new StringTokenizer(thisLine, ";");
                        st.setReturnEmptyTokens(true);
                        while(st.hasMoreTokens())
                        {
                            token = st.nextToken();
                            if(token.startsWith("\""))
                            {
                                outputFile.append(token + ",");
                            }
                            else
                            {
                                outputFile.append("\"" + token + "\"" + ",");
                            }
                        }
                        outputFile.deleteCharAt(outputFile.length()-1);
                        outputFile.append('\n');
                    }
                    File fileToWriteTo = new File(txtFile.getAbsolutePath().replaceAll(".txt", ".csv"));
                    FileUtils.writeStringToFile(fileToWriteTo, outputFile.toString(), null);
                    System.err.println("Processed file: " + txtFile.getAbsolutePath());
                    outputFile.delete(0, outputFile.length());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
