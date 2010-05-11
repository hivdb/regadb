package net.sf.regadb.analysis.functions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;

public class GenerateReport 
{
	private static class RIResult{
		public String gss, mutations, remarks, sir, level, description;
		
		public RIResult(String xml){
			gss = getValue(xml,"gss");
			mutations = getValue(xml,"mutations");
			remarks = getValue(xml,"remarks");
			level = getValue(xml,"level");
			description = getValue(xml,"description");
			sir = getValue(xml,"sir");
		}
		
		private String getValue(String xml, String tag){
			String v = getSubString(xml, '<'+ tag +'>', "</"+ tag +'>').result;
			return v == null || v.equals("null") ? "" : v; 
		}
	}
	
	private Map<String,Map<String,RIResult>> riresults = new HashMap<String,Map<String,RIResult>>();
	
    private StringBuffer rtfBuffer_;
    private static final long MILLISECS_PER_DAY = 1000*60*60*24;
    
    public GenerateReport(byte[] rtfFileContent, ViralIsolate vi, Patient patient, Collection<String> algorithms, Collection<String> drugClasses, Transaction t, File chartFile){
        rtfBuffer_ = new StringBuffer(new String(rtfFileContent));
        
        init(vi, patient, algorithms, drugClasses, t, chartFile, RegaDBSettings.getInstance().getInstituteConfig().getReportDateTolerance()); //default tolerance to two weeks
    }
    
    public GenerateReport(byte[] rtfFileContent, ViralIsolate vi, Patient patient, Collection<String> algorithms, Collection<String> drugClasses, Transaction t, File chartFile, int dateTolerance)
    {
        rtfBuffer_ = new StringBuffer(new String(rtfFileContent));
        
        init(vi, patient, algorithms, drugClasses, t, chartFile, dateTolerance);
    }
    
    public void init(ViralIsolate vi, Patient patient, Collection<String> algorithms, Collection<String> drugClasses, Transaction t, File chartFile, int dateTolerance)
    {
        replace("$REPORT_GENERATION_DATE", DateUtils.format(new Date()));
        replace("$PATIENT_NAME", patient.getFirstName());
        replace("$PATIENT_LASTNAME", patient.getLastName());
        replace("$PATIENT_ID", patient.getPatientId());
        replace("$PATIENT_CLINICAL_FILE_NR", getClinicalFileNumber(patient));
        replace("$SAMPLE_ID", vi.getSampleId());
        replace("$SAMPLE_DATE", DateUtils.format(vi.getSampleDate()));
        replace("$ART_EXPERIENCE", getARTExperience(patient));
        
        TestResult viralLoad = getTestResult(vi, patient, StandardObjects.getGenericHiv1ViralLoadTest(), dateTolerance);
        String viralLoadValue = viralLoad==null?"- ":viralLoad.getValue();
        replace("$VIRAL_LOAD_RNA", viralLoadValue);
        
        TestResult cd4Count = getTestResult(vi, patient, StandardObjects.getGenericCD4Test(), dateTolerance);
        if(cd4Count!=null)
            replace("$CD4_COUNT", cd4Count.getValue());
        else
            replace("$CD4_COUNT", "- ");
        
        replace("$ORGANISM", getOrganismName(vi));
        replace("$SUBTYPE", getType(vi, StandardObjects.getSubtypeTestDescription()));
        
        replace("$ASI_ALGORITHMS", algorithmsToString(algorithms));
        loadGssTestResults(vi);
        setRITable(algorithms, drugClasses, t);
        
        setMutations(vi, t);
        
        try {
            writePicture("$PATIENT_HISTORY_CHART", chartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String algorithmsToString(Collection<String> algorithms) {
    	if(algorithms.size() == 0)
    		return "";
    	
    	StringBuilder sb = new StringBuilder();
    	for(String algorithm : algorithms)
    		sb.append(", "+ algorithm);
    	
    	return sb.toString().substring(2);
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
        if(vi.getGenome() != null){
        	return vi.getGenome().getOrganismName();
        }
        return "";
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
    
    private void loadGssTestResults(ViralIsolate vi)
    {
        try{
            Genome g = vi.getGenome();
            TestType gssTestType = StandardObjects.getGssTestType(g);
            
            for(TestResult tr : vi.getTestResults())
            {
                if(Equals.isSameTestType(tr.getTest().getTestType(), gssTestType)) {
                	String genericName = tr.getDrugGeneric().getGenericName();
                	String algorithm = tr.getTest().getDescription();
                	
                    Map<String,RIResult> ariresults = riresults.get(algorithm);
                    if(ariresults == null){
                    	ariresults = new HashMap<String,RIResult>();
                    	riresults.put(algorithm, ariresults);
                    }
                    ariresults.put(genericName,new RIResult(new String(tr.getData())));
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
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
        
        
        Genome g = vi.getGenome();
        
        for(Protein protein : t.getProteins(g))
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
    
    private static class SubString{
    	public String result;
    	public int bpos, epos;
    	
    	public SubString(String result, int bpos, int epos){
    		this.result = result;
    		this.bpos = bpos;
    		this.epos = epos;
    	}
    }
    
    private static SubString getSubString(String seed, String begin, String end){
    	return getSubString(seed, begin, end, 0);
    }
    private static SubString getSubString(String seed, String begin, String end, int bpos){
    	int epos;
    	if((bpos = seed.indexOf(begin,bpos)) != -1 && (epos = seed.indexOf(end,bpos+begin.length())) != -1)
    		return new SubString(seed.substring(bpos+begin.length(),epos),bpos,epos+end.length());
    	else
    		return null;
    }
    private static SubString getSubString(StringBuffer seed, String begin, String end, int bpos){
    	int epos;
    	if((bpos = seed.indexOf(begin,bpos)) != -1 && (epos = seed.indexOf(end,bpos+begin.length())) != -1)
    		return new SubString(seed.substring(bpos+begin.length(),epos),bpos,epos+end.length());
    	else
    		return null;
    }
    
    private String getRITable(String algorithm, Collection<String> drugs, String asiString){
    	asiString = asiString.replace("$ASI_ALGORITHM", algorithm);
    	
    	SubString tableString = getSubString(asiString, "$BEGIN_TABLE", "$END_TABLE");
    	
    	StringBuilder result = new StringBuilder(asiString.substring(0,tableString.bpos));
    	
    	Map<String,RIResult> ariresults = riresults.get(algorithm);
    	for(String drug : drugs){
    		RIResult riresult = ariresults.get(drug);
    		if(riresult == null)
        		result.append(tableString.result
        				.replace("$ASI_DRUG", drug)
        				.replace("$ASI_MUTATIONS", "")
        				.replace("$ASI_GSS", "")
        				.replace("$ASI_LEVEL", "")
        				.replace("$ASI_SIR", "")
        				.replace("$ASI_REMARKS", "")
        				.replace("$ASI_DESCRIPTION", "")
        				);
    		else
	    		result.append(tableString.result
	    				.replace("$ASI_DRUG", drug)
	    				.replace("$ASI_MUTATIONS", riresult.mutations)
	    				.replace("$ASI_GSS", riresult.gss)
	    				.replace("$ASI_LEVEL", riresult.level)
	    				.replace("$ASI_SIR", riresult.sir)
	    				.replace("$ASI_REMARKS", riresult.remarks)
	    				.replace("$ASI_DESCRIPTION", riresult.description)
	    				);
    	}
    	
    	result.append(asiString.substring(tableString.epos));
    	return result.toString();
    }
    
    public void setRITable(Collection<String> algorithms, Collection<String> drugClasses, Transaction t)
    {
        List<String> drugs = new ArrayList<String>();
        List<DrugClass> sortedDrugClasses_  = t.getDrugClassesSortedOnResistanceRanking();
        
        List<DrugGeneric> genericDrugs;
//        boolean addedAmprenavir = false;
        for(DrugClass dc : sortedDrugClasses_) {
        	if(!drugClasses.contains(dc.getClassId()))
        		continue;
        	
            genericDrugs = t.getDrugGenericSortedOnResistanceRanking(dc);
            for(DrugGeneric dg : genericDrugs) {
//                if(!addedAmprenavir && dg.getGenericId().startsWith("FPV")) {
//                    new DrugGeneric(dg.getDrugClass(), "APV", "amprenavir"));
//                    new DrugGeneric(dg.getDrugClass(), "APV/r", "amprenavir/r"));
//                    addedAmprenavir = true;
//                }
                drugs.add(dg.getGenericName());
            }
        }
        
        int bpos = 0;
        SubString asiString;
        while((asiString = getSubString(rtfBuffer_, "$BEGIN_ASI", "$END_ASI", bpos)) != null){
        	String result;
        	SubString paramString = getSubString(asiString.result,"(",")");
        	asiString.result = asiString.result.substring(paramString.epos);

        	if(paramString.result.length() == 0){
        		StringBuilder sb = new StringBuilder();
        		for(String algorithm : algorithms)
        			sb.append(getRITable(algorithm, drugs, asiString.result));
        		result = sb.toString();
        	}
        	else{
        		String [] params = paramString.result.split(",");
        		result = getRITable(params[1], drugs, asiString.result);
        	}
        	
        	rtfBuffer_.replace(asiString.bpos, asiString.epos,result);
        	bpos = asiString.bpos + asiString.result.length();
        }
    }
}
