package net.sf.regadb.analysis.functions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.util.date.DateUtils;

public class GenerateReport 
{
    private StringBuffer rtfBuffer_;
    
    public GenerateReport(byte[] rtfFileContent, ViralIsolate vi, Patient patient, Test algorithm, Transaction t, File chartFile)
    {
        rtfBuffer_ = new StringBuffer(new String(rtfFileContent));
        
        init(vi, patient, algorithm, t, chartFile);
    }
    
    public void init(ViralIsolate vi, Patient patient, Test algorithm, Transaction t, File chartFile)
    {
        replace("$ASI_ALGORITHM", algorithm.getDescription());
        replace("$REPORT_GENERATION_DATE", DateUtils.getEuropeanFormat(new Date()));
        replace("$PATIENT_NAME", patient.getFirstName());
        replace("$PATIENT_LASTNAME", patient.getLastName());
        replace("$PATIENT_ID", patient.getPatientId());
        replace("$PATIENT_CLINICAL_FILE_NR", getClinicalFileNumber(patient));
        replace("$SAMPLE_ID", vi.getSampleId());
        replace("$SAMPLE_DATE", DateUtils.getEuropeanFormat(vi.getSampleDate()));
        
        TestResult viralLoad = getTestResult(vi, patient, StandardObjects.getGenericViralLoadTest());
        if(viralLoad!=null)
            replace("$VIRAL_LOAD RNA", viralLoad.getValue());
        else
            replace("$VIRAL_LOAD RNA", "- ");
        
        TestResult cd4Count = getTestResult(vi, patient, StandardObjects.getGenericCD4Test());
        if(cd4Count!=null)
            replace("$CD4_COUNT", cd4Count.getValue());
        else
            replace("$CD4_COUNT", "- ");
        
        replace("$TYPE", getType(vi));
        replace("$SUBTYPE", getSubtype(vi));
        
        List<TestResult> results = getGssTestResults(vi, algorithm);
        setRITable(results);
        
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
            if(StandardObjects.getClinicalFileNumber().equals(pav.getId().getAttribute().getName()))
            {
                return pav.getValue();
            }
        }
        
        return null;
    }
    
    private String getType(ViralIsolate vi)
    {
        String type = "";
        for(NtSequence ntSeq : vi.getNtSequences())
        {
            for(TestResult testResult : ntSeq.getTestResults())
            {
                type = testResult.getValue();
                break;
            }
            break;
        }
        
        return type;
    }
    
    private String getSubtype(ViralIsolate vi)
    {
        String subtype = "";
        for(NtSequence ntSequence : vi.getNtSequences())
        {
            for(TestResult tr : ntSequence.getTestResults())
            {
                if(tr.getTest().getDescription().equals(RegaDBWtsServer.getSubTypeTest()) && tr.getTest().getTestType().getDescription().equals(RegaDBWtsServer.getSubTypeTestType()))
                {
                    subtype += tr.getValue() + " (";
                    
                    for(AaSequence aaSequence : ntSequence.getAaSequences())
                    {
                        subtype += aaSequence.getProtein().getAbbreviation() + " + ";
                    }
                    int index = subtype.lastIndexOf("+");
                    if(index!=-1)
                        subtype = subtype.substring(0, index-1);
                    subtype += ") /";
                }
            }
            int index = subtype.lastIndexOf("/");
            if(index!=-1)
                subtype = subtype.substring(0, index-1) + " ";
        }
        return subtype;
    }
    
    private TestResult getTestResult(ViralIsolate vi, Patient patient, Test referenceTest)
    {
        TestResult viralLoadS = null;
        TestResult viralLoadD = null;
        for(TestResult testResult : patient.getTestResults())
        {
            if(testResult.getTest().getDescription().equals(referenceTest.getDescription()))
            {
                if(vi.getSampleId().equals(testResult.getSampleId()))
                    viralLoadS = testResult;
                else if(vi.getSampleDate().equals(testResult.getTestDate()))
                    viralLoadD = testResult;
            }
        }
        
        return viralLoadS==null?viralLoadD:viralLoadS;
    }
    
    private List<TestResult> getGssTestResults(ViralIsolate vi, Test algorithm)
    {
        List<TestResult> testResults = new ArrayList<TestResult>();
        
        for(TestResult tr : vi.getTestResults())
        {
            if(tr.getTest().getTestType().getDescription().equals(StandardObjects.getGssId()) 
                    && tr.getTest().getDescription().equals(algorithm.getDescription())) {
                testResults.add(tr);
            }
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
    
    public void setRITable(/*LinkedHashMap DRUG_NAMES,*/ List<TestResult> testResults)
    {
        //TODO get drugs from resistance interpretation tab
        LinkedHashMap<String,String> DRUG_NAMES = new LinkedHashMap<String,String>();
        // NRTI
        DRUG_NAMES.put("zidovudine", "AZT");
        DRUG_NAMES.put("zalcitabine", "DDC");
        DRUG_NAMES.put("didanosine", "DDI");
        DRUG_NAMES.put("lamivudine", "3TC");
        DRUG_NAMES.put("stavudine", "D4T");
        DRUG_NAMES.put("abacavir", "ABC");
        DRUG_NAMES.put("emtricitabine", "FTC");
        DRUG_NAMES.put("tenofovir", "TDF");
        // NNRTI
        DRUG_NAMES.put("nevirapine", "NVP");
        DRUG_NAMES.put("delavirdine", "DLV");
        DRUG_NAMES.put("efavirenz", "EFV");
        DRUG_NAMES.put("etravirine", "ETV");
        // PI
        DRUG_NAMES.put("saquinavir", "SQV");
        DRUG_NAMES.put("saquinavir/r", "SQV/r");
        DRUG_NAMES.put("ritonavir", "RTV");
        DRUG_NAMES.put("indinavir", "IDV");
        DRUG_NAMES.put("indinavir/r", "IDV/r");
        DRUG_NAMES.put("nelfinavir", "NFV");
        DRUG_NAMES.put("amprenavir", "APV");
        DRUG_NAMES.put("amprenavir/r", "APV/r");
        DRUG_NAMES.put("fosamprenavir", "FPV");
        DRUG_NAMES.put("fosamprenavir/r", "FPV/r");
        DRUG_NAMES.put("lopinavir/r", "LPV/r"); 
        DRUG_NAMES.put("atazanavir", "ATV");
        DRUG_NAMES.put("atazanavir/r", "ATV/r");
        DRUG_NAMES.put("tipranavir/r", "TPV/r");
        DRUG_NAMES.put("darunavir/r", "DRV/r");
        // ENV
        DRUG_NAMES.put("enfuvirtide", "T20");
        
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
        for (Iterator i = DRUG_NAMES.keySet().iterator(); i.hasNext();) {
            String drug = (String) i.next();
            String drugCode = (String) DRUG_NAMES.get(drug);

            String mutations = null;
            String interpretation = null;

            /*if (drugCode.equals("T20")) {
                mutations = lang.equals("NL") ? "niet bepaald" : "not determined";
                interpretation = "-";
            } else {*/
            tr = null;
            for(TestResult ttr : testResults) {
                if(drugCode.equals(ttr.getDrugGeneric().getGenericId())) {
                    tr = ttr;
                }
            }
               // ResistanceResultGeneric resistanceResult = drugForm.getResistanceResult(testII,drugCode);

                if (tr != null) {
                    //TODO mutations
                    //TODO interprete gss
                    mutations = "lala";//resistanceResult.getComment();
                    interpretation = tr.getValue(); //resistanceResult.getInterpretation(lang);
                }
            //}

            if (mutations != null) {
                if (ii >= 3)
                    replace(line, line + line);

                String reportMutations = "$MUTATIONS" + (Math.min(ii, 3));
                String reportInterpretation = "$INTERPRETATION" + (Math.min(ii, 3));

                replace("$DRUG" + (Math.min(ii, 3)), drug);
                replace(reportMutations, mutations);
                replace(reportInterpretation, interpretation);
                ++ii;
            }
        }
        
        replace(line, "");       
    }
}
