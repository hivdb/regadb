package net.sf.regadb.analysis.functions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class GenerateReport 
{
    private StringBuffer rtfBuffer_;
    private static final long MILLISECS_PER_DAY = 1000*60*60*24;
    
    public GenerateReport(byte[] rtfFileContent, ViralIsolate vi, Patient patient, Test algorithm, Transaction t, File chartFile){
        rtfBuffer_ = new StringBuffer(new String(rtfFileContent));
        
        int dateTolerance;
        try{
            dateTolerance = Integer.parseInt(RegaDBSettings.getInstance().getPropertyValue("regadb.report.dateTolerance"));
        }
        catch(Exception e){
            dateTolerance = 14;
        }
        
        init(vi, patient, algorithm, t, chartFile, dateTolerance); //default tolerance to two weeks
    }
    
    public GenerateReport(byte[] rtfFileContent, ViralIsolate vi, Patient patient, Test algorithm, Transaction t, File chartFile, int dateTolerance)
    {
        rtfBuffer_ = new StringBuffer(new String(rtfFileContent));
        
        init(vi, patient, algorithm, t, chartFile, dateTolerance);
    }
    
    public void init(ViralIsolate vi, Patient patient, Test algorithm, Transaction t, File chartFile, int dateTolerance)
    {
        replace("$ASI_ALGORITHM", algorithm.getDescription());
        replace("$REPORT_GENERATION_DATE", DateUtils.getEuropeanFormat(new Date()));
        replace("$PATIENT_NAME", patient.getFirstName());
        replace("$PATIENT_LASTNAME", patient.getLastName());
        replace("$PATIENT_ID", patient.getPatientId());
        replace("$PATIENT_CLINICAL_FILE_NR", getClinicalFileNumber(patient));
        replace("$SAMPLE_ID", vi.getSampleId());
        replace("$SAMPLE_DATE", DateUtils.getEuropeanFormat(vi.getSampleDate()));
        replace("$ART_EXPERIENCE", getARTExperience(patient));
        
        TestResult viralLoad = getTestResult(vi, patient, StandardObjects.getGenericHiv1ViralLoadTest(), dateTolerance);
        if(viralLoad!=null)
            replace("$VIRAL_LOAD RNA", viralLoad.getValue());
        else
            replace("$VIRAL_LOAD RNA", "- ");
        
        TestResult cd4Count = getTestResult(vi, patient, StandardObjects.getGenericCD4Test(), dateTolerance);
        if(cd4Count!=null)
            replace("$CD4_COUNT", cd4Count.getValue());
        else
            replace("$CD4_COUNT", "- ");
        
        replace("$TYPE", getOrganismName(vi));
        replace("$SUBTYPE", getType(vi, RegaDBWtsServer.getSubtypeTest()));
        
        List<TestResult> results = getGssTestResults(vi, algorithm);
        setRITable(results, t);
        
        setMutations(vi, t);
        
        try {
            writePicture("$PATIENT_HISTORY_CHART", chartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String getClinicalFileNumber(Patient patient)
    {
        for(PatientAttributeValue pav : patient.getPatientAttributeValues())
        {
            if(StandardObjects.getClinicalFileNumberAttribute().getName().equals(pav.getAttribute().getName()))
            {
                return pav.getValue();
            }
        }
        
        return null;
    }
    
    private String getType(ViralIsolate vi, String typeTest)
    {
        for(NtSequence ntSeq : vi.getNtSequences())
        {
            for(TestResult testResult : ntSeq.getTestResults())
            {
                if(testResult.getTest().getDescription().equals(typeTest))
                    return testResult.getValue();
            }
        }
        
        return "";
    }
    
    private String getOrganismName(ViralIsolate vi){
        String organismName="";
        if(vi.getNtSequences().size() > 0){
            NtSequence ntSeq = vi.getNtSequences().iterator().next();
            
            if(ntSeq.getAaSequences().size() > 0){
                AaSequence aaSeq = ntSeq.getAaSequences().iterator().next();
                organismName = aaSeq.getProtein().getOpenReadingFrame().getGenome().getOrganismName();
            }
        }
        return organismName;
    }
    
    private TestResult getTestResult(ViralIsolate vi, Patient patient, Test referenceTest, int dateTolerance)
    {
        TestResult viralLoadS = null;
        TestResult viralLoadD = null;

        long mindiff = (dateTolerance + 1) * MILLISECS_PER_DAY;
        long diff;
        
        for(TestResult testResult : patient.getTestResults())
        {
            if(testResult.getTest().getDescription().equals(referenceTest.getDescription()))
            {
                if(vi.getSampleId().equals(testResult.getSampleId())){
                    viralLoadS = testResult;
                    break;
                }
                else{
                    diff = java.lang.Math.abs(testResult.getTestDate().getTime() - vi.getSampleDate().getTime());
                    if(diff <= mindiff){
                        mindiff = diff;
                        viralLoadD = testResult;
                    }
                }
            }
        }
        
        return viralLoadS==null?viralLoadD:viralLoadS;
    }
    
    private String getARTExperience(Patient p){
        HashSet<String> drugs = new HashSet<String>();
        
        for(Therapy t : p.getTherapies()){
            for(TherapyGeneric tg : t.getTherapyGenerics()){
                drugs.add(tg.getId().getDrugGeneric().getGenericId());
            }
            for(TherapyCommercial tc : t.getTherapyCommercials()){
                for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()){
                    drugs.add(dg.getGenericId());
                }
            }
        }
        
        return drugs.toString();
    }
    
    private List<TestResult> getGssTestResults(ViralIsolate vi, Test algorithm)
    {
        List<TestResult> testResults = new ArrayList<TestResult>();
        try{
            Genome g = vi.getNtSequences().iterator().next().getAaSequences().iterator().next().getProtein().getOpenReadingFrame().getGenome();
            TestType gssTestType = StandardObjects.getGssTestType(g);
            
            for(TestResult tr : vi.getTestResults())
            {
                if(Equals.isSameTestType(tr.getTest().getTestType(), gssTestType) 
                        && tr.getTest().getDescription().equals(algorithm.getDescription())) {
                    testResults.add(tr);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return testResults;
    }
    
    private void setMutations(ViralIsolate vi, Transaction t)
    {
        List<AaSequence> aaSeqs = new ArrayList<AaSequence>();
        for(NtSequence ntSequence : vi.getNtSequences())
        {
            for(AaSequence aaSeq : ntSequence.getAaSequences())
            {
                aaSeqs.add(aaSeq);
            }
        }
        
        String result;
        String textToReplace;
        boolean foundMatchinqSeq;
        for(Protein protein : t.getProteins())
        {   
            foundMatchinqSeq = false;
            textToReplace = "$"+protein.getAbbreviation().toUpperCase()+"_MUTATIONS";
            for(AaSequence aaSeq : aaSeqs)
            {
                if(aaSeq.getProtein().getAbbreviation().equals(protein.getAbbreviation()))
                {
                    result = MutationHelper.getNonSynonymousMutations(aaSeq);
                    if("".equals(result.trim()))
                        result = "-";
                    replace(textToReplace, result);
                    foundMatchinqSeq = true;
                }
            }
            if(!foundMatchinqSeq)
                replace(textToReplace, "undetermined");
        }
    }
    
    public byte[] getReport() {        
        return rtfBuffer_.toString().getBytes();
    }
    
    private void replace(String find, String replace) {
        if(replace==null) {
            replace = "";
        }
        int indexOf = rtfBuffer_.indexOf(find);
        if(indexOf!=-1)
            rtfBuffer_.replace(indexOf, indexOf + find.length(), replace);
    }
    
    private void appendHexdump(File inputFile, StringBuffer toAppend) throws IOException {        
        FileInputStream fis = new FileInputStream(inputFile);
        for (int b = fis.read(); b != -1; b = fis.read()) {
            String hex = Integer.toHexString(b);
            if (hex.length() == 1)
                toAppend.append("0");
            toAppend.append(hex);
        }
    }

    private void writePicture(String find, File input) throws IOException {
        StringBuffer pic = new StringBuffer();
        pic.append(" }{\\*\\shppict{\\pict\\pngblip\n");
        appendHexdump(input, pic);
        pic.append("}}");
        int findStart = rtfBuffer_.indexOf(find);
        if(findStart!=-1)
            rtfBuffer_.replace(findStart, findStart + find.length(), pic.toString());
    }
    
    public void setRITable(List<TestResult> testResults, Transaction t)
    {
        List<DrugGeneric> drugs = new ArrayList<DrugGeneric>();
        List<DrugClass> sortedDrugClasses_  = t.getDrugClassesSortedOnResistanceRanking();
        
        List<DrugGeneric> genericDrugs;
        boolean addedAmprenavir = false;
        for(DrugClass dc : sortedDrugClasses_) {
            genericDrugs = t.getDrugGenericSortedOnResistanceRanking(dc);
            for(DrugGeneric dg : genericDrugs) {
                if(!addedAmprenavir && dg.getGenericId().startsWith("FPV")) {
                    drugs.add(new DrugGeneric(dg.getDrugClass(), "APV", "amprenavir"));
                    drugs.add(new DrugGeneric(dg.getDrugClass(), "APV/r", "amprenavir/r"));
                    addedAmprenavir = true;
                }
                drugs.add(dg);
            }
        }
        
        int interpretation1Pos = rtfBuffer_.indexOf("$INTERPRETATION1");
        int mutation1Pos = rtfBuffer_.indexOf("$MUTATIONS1");
        
        String lastString;
        if (interpretation1Pos < mutation1Pos)
            lastString = "$MUTATIONS";
        else
            lastString = "$INTERPRETATION";
        
        String line
            = rtfBuffer_.substring( rtfBuffer_.indexOf(lastString + "2") + lastString.length() + 1,
                                    rtfBuffer_.indexOf(lastString + "3") + lastString.length() + 1);

        System.err.println(line);
        
        int ii = 1;
        TestResult tr;
        for (DrugGeneric dg : drugs) {
            String drug = dg.getGenericName();
            String drugCode = dg.getGenericId();

            final StringBuffer mutationsLocal = new StringBuffer();
            String interpretation = null;
            Double gss = null;

            tr = null;
            for(TestResult ttr : testResults) {
                if(drugCode.equals(ttr.getDrugGeneric().getGenericId())) {
                    tr = ttr;
                }
            }

                if (tr != null) {
                    if(mutationsLocal.length()!=0)
                    mutationsLocal.delete(0, mutationsLocal.length());
                    interpretation = tr.getValue();
                    ResistanceInterpretationParser inp = new ResistanceInterpretationParser()
                    {
                        @Override
                        public void completeScore(String drug, int level, double gss, String description, char sir, ArrayList<String> mutations, String remarks) 
                        {
                            int size = mutations.size();
                            for(int i = 0; i<size; i++) {
                                mutationsLocal.append(mutations.get(i));
                                if(i!=size-1)
                                    mutationsLocal.append(' ');
                            }
                        }
                    };
                    try 
                    {
                        inp.parse(new InputSource(new ByteArrayInputStream(tr.getData())));
                    } 
                    catch (SAXException e) 
                    {
                        e.printStackTrace();
                    } 
                    catch (IOException e) 
                    {
                        e.printStackTrace();
                    }
                    
                    if (mutationsLocal != null) {
                        if (ii >= 3)
                            replace(line, line + line);

                        String reportMutations = "$MUTATIONS" + (Math.min(ii, 3));
                        String reportInterpretation = "$INTERPRETATION" + (Math.min(ii, 3));

                        replace("$DRUG" + (Math.min(ii, 3)), drug);
                        replace(reportMutations, mutationsLocal.toString());

                        try {
                            gss = Double.parseDouble(interpretation);
                        } catch(NumberFormatException e) {
                            gss = null;                   
                        }
                        replace(reportInterpretation, ResistanceInterpretationHelper.getSIRRepresentation(gss)+ " ("+interpretation+")");
                        ++ii;
                    }
                }
        }
        
        replace(line, "");       
    }
}
