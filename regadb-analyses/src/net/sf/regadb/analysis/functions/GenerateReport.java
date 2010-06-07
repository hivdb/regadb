package net.sf.regadb.analysis.functions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Protein;
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
		public String gss="N/A",
			mutations="N/A",
			remarks="N/A",
			sir="N/A",
			level="N/A",
			description="N/A";
		
		public RIResult(){
			
		}
		
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
	
	private static final RIResult emptyRIResult = new RIResult();
	
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

        replaceTestResult(patient, vi, StandardObjects.getHiv1ViralLoadTestType(), dateTolerance, "VIRAL_LOAD_RNA");
        replaceTestResult(patient, vi, StandardObjects.getCd4TestType(), dateTolerance, "CD4_COUNT");
        
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
    
    private void replaceTestResult(Patient p, ViralIsolate vi, TestType tt, int dateTolerance, String pattern){
        String value, date;
        TestResult tr = getTestResult(vi, p, tt, dateTolerance);
        if(tr != null){
        	value = tr.getValue();
        	date = DateUtils.format(tr.getTestDate());
        }
        else
        	value = date = "-";
        
        replace("$"+ pattern +"_DATE", date);
        replace("$"+ pattern, value);
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
    	TreeSet<String> subtypes = new TreeSet<String>();
        for(NtSequence ntSeq : vi.getNtSequences())
        {
            for(TestResult testResult : ntSeq.getTestResults())
            {
                if(testResult.getTest().getDescription().equals(typeTest))
                    subtypes.add(testResult.getValue());
            }
        }
        
        return subtypes.size() == 0 ? "":subtypes.toString().replace("[", "").replace("]", "");
    }
    
    private String getOrganismName(ViralIsolate vi){
        if(vi.getGenome() != null){
        	return vi.getGenome().getOrganismName();
        }
        return "";
    }
    
    private TestResult getTestResult(ViralIsolate vi, Patient patient, TestType referenceTestType, int dateTolerance)
    {
        TestResult resultSample = null;
        TestResult resultDate = null;

        long mindiff = (dateTolerance + 1) * MILLISECS_PER_DAY;
        long diff;
        
        for(TestResult testResult : patient.getTestResults())
        {
            if(Equals.isSameTestType(testResult.getTest().getTestType(),referenceTestType))
            {
                if(vi.getSampleId().equals(testResult.getSampleId())){
                    resultSample = testResult;
                    break;
                }
                else{
                    diff = java.lang.Math.abs(testResult.getTestDate().getTime() - vi.getSampleDate().getTime());
                    if(diff <= mindiff){
                        mindiff = diff;
                        resultDate = testResult;
                    }
                }
            }
        }
        
        return resultSample==null?resultDate:resultSample;
    }
    
    private String getARTExperience(Patient p){
        StringBuilder result = new StringBuilder();
        
        TreeSet<Therapy> therapies = new TreeSet<Therapy>(new Comparator<Therapy>() {
			public int compare(Therapy o1, Therapy o2) {
				return o1.getStartDate().compareTo(o2.getStartDate());
			}
		});
        
        for(Therapy t : p.getTherapies())
        	therapies.add(t);

        if(therapies.size() == 0)
        	return "";
        
        String prev = "";
        for(Therapy t : therapies){
        	TreeSet<String> combination = new TreeSet<String>();
            for(TherapyGeneric tg : t.getTherapyGenerics()){
                combination.add(tg.getId().getDrugGeneric().getGenericId());
            }
            for(TherapyCommercial tc : t.getTherapyCommercials()){
                for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()){
                    combination.add(dg.getGenericId());
                }
            }
            String curr = combination.toString().replace(", ", "+");
            if(!curr.equals(prev)){
            	result.append(", "+ curr);
            	prev = curr;
            }
        }
        
        return result.substring(2);
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
    	
    	final Pattern pattern = Pattern.compile("\\$ASI_[A-Z_]+1");
    	Matcher matcher = pattern.matcher(asiString);
    	String first;
    	int bpos,epos;
    	if(!matcher.find(0))
    		return asiString;

    	first = matcher.group();
    	first = first.substring(0,first.length()-1);
    	
    	bpos = matcher.start();
    	epos = asiString.indexOf(first + "2");
    	
    	String line = asiString.substring(bpos,epos);
    	StringBuilder result = new StringBuilder(asiString.substring(0, bpos));
    	
    	Map<String,RIResult> ariresults = riresults.get(algorithm);
    	int i = 0;
    	int n = 1;
    	for(String drug : drugs){
    		++i;

    		RIResult riresult = ariresults.get(drug);
    		if(riresult == null)
    			riresult = emptyRIResult;
    		
    		if(i == drugs.size()){
    			line = asiString.substring(epos);
    			n = 2;
    		}
    			
    		result.append(line
    				.replace("$ASI_DRUG"+n, drug)
    				.replace("$ASI_MUTATIONS"+n, riresult.mutations)
    				.replace("$ASI_GSS"+n, riresult.gss)
    				.replace("$ASI_LEVEL"+n, riresult.level)
    				.replace("$ASI_SIR"+n, riresult.sir)
    				.replace("$ASI_REMARKS"+n, riresult.remarks)
    				.replace("$ASI_DESCRIPTION"+n, riresult.description)
    				);
    	}
    	
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
        
        //multi asi tables
        bpos = 0;
        while((asiString = getSubString(rtfBuffer_, "$BEGIN_MULTIASI", "$END_MULTIASI", bpos)) != null){
        	String result = getMultiAsiTable(algorithms, drugs, asiString.result);
        	rtfBuffer_.replace(asiString.bpos, asiString.epos,result);
        	bpos = asiString.bpos + asiString.result.length();
        }
        
        //legacy rtf templates
        if(!algorithms.isEmpty()){
        	String algorithm = algorithms.iterator().next();
        	setRITableOld(algorithm, drugs);
        	replace("$ASI_ALGORITHM", algorithm);
        }
    }
    
	public void setRITableOld(String algorithm, Collection<String> drugs) {
		int interpretation1Pos = rtfBuffer_.indexOf("$INTERPRETATION1");
		int mutation1Pos = rtfBuffer_.indexOf("$MUTATIONS1");

		String lastString;
		if (interpretation1Pos < mutation1Pos)
			lastString = "$MUTATIONS";
		else
			lastString = "$INTERPRETATION";

		String line = rtfBuffer_.substring(rtfBuffer_.indexOf(lastString + "2")
				+ lastString.length() + 1, rtfBuffer_.indexOf(lastString + "3")
				+ lastString.length() + 1);

		System.err.println(line);

		int ii = 1;
		Map<String, RIResult> ariresults = riresults.get(algorithm);

		for (String drug : drugs) {
			RIResult riresult = ariresults.get(drug);

			if (riresult != null) {
				if (ii >= 3)
                    replace(line, line + line);
				
				String reportMutations = "$MUTATIONS" + (Math.min(ii, 3));
				String reportInterpretation = "$INTERPRETATION"
						+ (Math.min(ii, 3));

				replace("$DRUG" + (Math.min(ii, 3)), drug);
				replace(reportMutations, riresult.mutations);

				replace(reportInterpretation,riresult.sir +" ("+ riresult.gss +")");
				++ii;
			}
		}

		replace(line, "");
	}
	
	public String getMultiAsiTable(Collection<String> algorithms, Collection<String> drugs, String tableString){
    	int i;
    	int bpos1,epos1,bpos2,epos2;
    	StringBuilder result;

    	//expand u
    	
    	//expand algorithm names
    	bpos1 = tableString.indexOf("$ASI_ALGORITHM1");
    	if(bpos1 > -1 && (epos1 = tableString.indexOf("$ASI_ALGORITHM2")) > -1){
	    	String algtpl = tableString.substring(bpos1+"$ASI_ALGORITHM1".length(), epos1);
	    	
	    	result = new StringBuilder(tableString.substring(0,bpos1));
	    	i = 0;
	    	for(String algorithm : algorithms){
	    		++i;
	    		
	    		if(i == algorithms.size()){
	    			result.append(algorithm);
	    		}
	    		else{
	    			result.append(algorithm).append(algtpl);
	    		}
	    	}
	    	tableString = result.toString() + tableString.substring(epos1 + "$ASI_ALGORITHM2".length());
    	}
    	
		//expand template columns
    	Pattern pattern = Pattern.compile("\\$ASI_[A-Z_]+1\\(1\\)");
    	Matcher matcher = pattern.matcher(tableString);
    	String first;

    	if(matcher.find(0)){

	    	first = matcher.group();
	    	first = first.substring(0,first.length()-4);
	    	
	    	bpos1 = matcher.start();
	    	epos1 = tableString.indexOf(first + "1(2)");
	    	bpos2 = tableString.indexOf(first +"2(1)");
	    	epos2 = tableString.indexOf(first+"2(2)");
	    	
	    	String coltpl1 = tableString.substring(bpos1,epos1);
	    	String coltpl2 = tableString.substring(bpos2,epos2);
	    	
	    	StringBuilder rowtpl1 = new StringBuilder();
	    	StringBuilder rowtpl2 = new StringBuilder();
	    	
	    	i = 0;
	    	for(String algorithm : algorithms){
	    		++i;
	    		
	    		if(i == algorithms.size()){
	    			//end of row
	    			coltpl1 = tableString.substring(epos1,bpos2);
	    			coltpl2 = tableString.substring(epos2);
	    		}
	    		
	    		rowtpl1.append(coltpl1.replaceAll("\\$ASI_([A-Z]+)1\\([12]\\)", "\\$ASI_$1\\1("+ algorithm +")"));
	    		rowtpl2.append(coltpl2.replaceAll("\\$ASI_([A-Z]+)2\\([12]\\)", "\\$ASI_$1\\2("+ algorithm +")"));
	    	}
	    	
	    	tableString = tableString.substring(0,bpos1) + rowtpl1.toString() + rowtpl2.toString();
    	}

    	//expand rows
    	pattern = Pattern.compile("(\\$ASI_[A-Z_]+)1");
    	matcher = pattern.matcher(tableString);
    	if(matcher.find(0)){
    		bpos1 = matcher.start();
    		epos1 = tableString.indexOf(matcher.group(1) +"2");
    		String rowtpl = tableString.substring(bpos1, epos1);

    		//get a list of algorithms actually being used
    		List<String> usedalgorithms = new ArrayList<String>();
    		pattern = Pattern.compile("(\\$ASI_[A-Z_]+)1\\(([^)]+)\\)");
    		matcher = pattern.matcher(rowtpl);
    		bpos2 = 0;
    		while(matcher.find(bpos2)){
    			if(riresults.get(matcher.group(2)) != null)
    				usedalgorithms.add(matcher.group(2));
    			bpos2 = matcher.end();
    		}
    		
    		result = new StringBuilder(tableString.substring(0,bpos1));
    		i = 0;
    		for(String drug : drugs){
    			++i;
    			
    			String row;
    			if(i == drugs.size())
    				row = tableString.substring(epos1);
    			else
    				row = rowtpl;
    			
    			row = row.replaceAll("\\$ASI_DRUG[12]", drug);
    			
    			for(String algorithm : usedalgorithms){
    				RIResult rir = riresults.get(algorithm).get(drug);
    				if(rir == null)
    					rir = emptyRIResult;
    				
    				row = row
    					.replaceAll("\\$ASI_DESRIPTION[12]\\("+ algorithm +"\\)", rir.description)
    					.replaceAll("\\$ASI_GSS[12]\\("+ algorithm +"\\)", rir.gss)
    					.replaceAll("\\$ASI_LEVEL[12]\\("+ algorithm +"\\)", rir.level)
    					.replaceAll("\\$ASI_MUTATIONS[12]\\("+ algorithm +"\\)", rir.mutations)
    					.replaceAll("\\$ASI_REMARKS[12]\\("+ algorithm +"\\)", rir.remarks)
    					.replaceAll("\\$ASI_SIR[12]\\("+ algorithm +"\\)", rir.sir);
    			}
    			
    			result.append(row);
    		}
    		
    		tableString = result.toString();
    	}
    	
    	return tableString;
	}
}
