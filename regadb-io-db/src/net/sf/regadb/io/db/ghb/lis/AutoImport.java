package net.sf.regadb.io.db.ghb.lis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.db.ghb.GhbUtils;
import net.sf.regadb.io.db.util.mapping.DbObjectStore;
import net.sf.regadb.io.db.util.mapping.ObjectMapper;
import net.sf.regadb.io.db.util.mapping.ObjectStore;
import net.sf.regadb.io.db.util.mapping.ObjectMapper.InvalidValueException;
import net.sf.regadb.io.db.util.mapping.ObjectMapper.MappingDoesNotExistException;
import net.sf.regadb.io.db.util.mapping.ObjectMapper.MappingException;
import net.sf.regadb.io.db.util.mapping.ObjectMapper.ObjectDoesNotExistException;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.args.Arguments.ArgumentException;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.mapper.XmlMapper;
import net.sf.regadb.util.mapper.XmlMapper.MapperParseException;
import net.sf.regadb.util.settings.RegaDBSettings;

public class AutoImport {
    private class FileLogger{
        private PrintStream out;
        
        public FileLogger(File file) throws FileNotFoundException{
            out = new PrintStream(new FileOutputStream(file));
        }
        
        public void println(String msg){
            out.println(msg);
        }
        
        public void close(){
            out.close();
        }
    }
    private class ErrorTypes{
        public boolean mapping = false;
        public boolean object = false;
        public boolean value = false;
        
        public String toString(){
            String s = "";
            if(mapping)
                s = "not mapped, ";
            if(object)
                s += "wrong mapping, ";
            if(value)
                s += "invalid value";
            
            return s.length() == 0 ? "ok":s;
        }
    }
    
    private FileLogger errLog, importLog, infoLog;
    
    public Date firstCd4 = new Date();
    public Date firstCd8 = new Date();
    public Date firstViralLoad = new Date();
    public Date firstSeroStatus = new Date();
    
    Map<String, ErrorTypes> lisTests = new TreeMap<String, ErrorTypes>();
    
    private ObjectMapper objectMapper;
    private XmlMapper xmlMapper;
    private ObjectStore objectStore;
    
    private String datasetDescription = null;
    
    private Set<String> patientsNotFound = new HashSet<String>();
    
    public static void main(String [] args) {
    	Arguments as = new Arguments();
    	ValueArgument conf			= as.addValueArgument("conf-dir", "configuration directory", false);
    	PositionalArgument user		= as.addPositionalArgument("regadb user", true);
    	PositionalArgument pass		= as.addPositionalArgument("regadb password", true);
    	PositionalArgument dataset	= as.addPositionalArgument("regadb dataset", true);
    	PositionalArgument mapfile	= as.addPositionalArgument("lis mapping xml file", true);
    	PositionalArgument lisdir	= as.addPositionalArgument("lis export directory", true);
    	
    	try {
			as.parse(args);
		} catch (ArgumentException e1) {
			System.err.println(e1);
		}
		
		if(as.isValid()){
			if(conf.isSet())
				RegaDBSettings.createInstance(conf.getValue());
			else
				RegaDBSettings.createInstance();
			
			as.printValues(System.out);
	        try{
	        	AutoImport ai = new AutoImport(user.getValue(), pass.getValue(), new File(mapfile.getValue()), dataset.getValue());
				ai.run(new File(lisdir.getValue()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (WrongUidException e) {
				e.printStackTrace();
			} catch (WrongPasswordException e) {
				e.printStackTrace();
			} catch (DisabledUserException e) {
				e.printStackTrace();
			} catch (MapperParseException e) {
				e.printStackTrace();
			}
		}
		else
			as.printUsage(System.err);
    }
    
    public AutoImport(){
    	
    }
    
    public AutoImport(File mappingFile, File nationMappingFile, ObjectStore objectStore, String datasetDescription) throws MapperParseException{
    	init(mappingFile, objectStore);
    	this.datasetDescription = datasetDescription;
    }

    public AutoImport(String user, String pass, File mappingFile, String datasetDescription) throws WrongUidException, WrongPasswordException, DisabledUserException, MapperParseException {
        Login login;
		login = Login.authenticate(user, pass);
		init(mappingFile, new DbObjectStore(login));
		this.datasetDescription = datasetDescription;
    }
    
    private void init(File mappingFile, ObjectStore objectStore) throws MapperParseException{
        xmlMapper = new XmlMapper(mappingFile);
        
        objectMapper = new ObjectMapper(objectStore, xmlMapper);
        this.objectStore = objectStore; 
    }
    
    public TestNominalValue getNominalValue(TestType tt, String str){
        for(TestNominalValue tnv : tt.getTestNominalValues()){
            if(tnv.getTestType().equals(tt) && tnv.getValue().equals(str)){
                return tnv;
            }
        }
        return null;
    }
    
    public void run(File path) throws FileNotFoundException{
    	String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    	String logFile = File.separatorChar + date + ".log";
    	
    	if(!path.exists())
    		throw new FileNotFoundException(path.getAbsolutePath());
    	
    	if(path.isFile()){
    		infoLog = new FileLogger(new File(path.getParent() + logFile));
    		process(path);
    	}
    	else if(path.isDirectory()){
    		infoLog = new FileLogger(new File(path.getAbsolutePath() + logFile));
    		batchProcess(path);
    	}
    	
        logInfo("Tests summary ------");
        for(Map.Entry<String, ErrorTypes> me : lisTests.entrySet())
            logInfo(me.getKey() +": \t"+ me.getValue());
        logInfo("------");
        
        infoLog.close();
    }
    
    public void batchProcess(File dir) throws FileNotFoundException {
        File[] files = dir.listFiles();
        for(final File f : files) {
            if(f.getAbsolutePath().toLowerCase().endsWith(".xls") && f.getName().toLowerCase().startsWith("regadb")){
                process(f);
            }
        }
    }
    
    public void process(File file) throws FileNotFoundException{
        File logDir = RegaDBSettings.getInstance().getInstituteConfig().getLogDir();
        errLog = new FileLogger(new File(logDir.getAbsolutePath() + File.separatorChar + file.getName() +".errors.txt"));
        importLog = new FileLogger(new File(logDir.getAbsolutePath() + File.separatorChar + file.getName() +".not-imported.txt"));
    	
        logInfo("\nProcessing file: "+ file.getAbsolutePath());
        try {
            InputStream is = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line = br.readLine();
            if(line != null){
                Map<String, Integer> headers = new HashMap<String, Integer>(); 
                int i=0;
                String[] fields = tokenizeTab(line);
                logNotImported(line);
                
                for(String s : fields)
                    headers.put(s, i++);
            
                int n = 2;
                while((line = br.readLine()) != null) {
                    fields = tokenizeTab(line);
                    
                    if(fields.length > 0 && headers.get(fields[0]) == null){
                        Patient p = handlePatient(headers, fields, n);
                        if(p != null)
                            handleTest(p, headers, fields, n);
                        objectStore.commit();
                    }
                    ++n;
                }
            }
            
            br.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        importLog.close();
        errLog.close();
    }
    
    private Patient handlePatient(Map<String,Integer> headers, String[] line, int lineNumber) {
        String ead = line[headers.get("EADnr")];
        if(ead == null || ead.length() == 0)
            return null;
        String emd = line[headers.get("EMDnr")];
        Date birthDate = null;
        try {
            birthDate = GhbUtils.LISDateFormat.parse(line[headers.get("geboortedatum")]);
        } catch (ParseException e) {
           e.printStackTrace();
        }
        
        Patient p = getPatient(ead);
        if(p==null){
        	if(patientsNotFound.add(ead)){
        		logError(lineNumber, "Patient not found: '"+ ead +"'");
        	}
        	logNotImported(toString(line));
        	return null;
        }
        
        Attribute emdAttribute = objectStore.getAttribute("EMD Number", StandardObjects.getClinicalAttributeGroup().getGroupName());
        if(emdAttribute == null){
        	emdAttribute = objectStore.createAttribute( objectStore.getAttributeGroup(StandardObjects.getClinicalAttributeGroup().getGroupName()),
        												objectStore.getValueType(StandardObjects.getStringValueType().getDescription()),
        												"EMD Number");
        }
        
        Attribute birthDateAttribute = objectStore.getAttribute("Birth date","Personal");

        if(!containsAttribute(emdAttribute, p))
            p.createPatientAttributeValue(emdAttribute).setValue(emd);

        if(!containsAttribute(birthDateAttribute, p))
            p.createPatientAttributeValue(birthDateAttribute).setValue(birthDate.getTime()+"");
        
        return p;
    }
    
    public boolean containsAttribute(Attribute a, Patient p) {
        String name = a.getName();
        for(PatientAttributeValue pav : p.getPatientAttributeValues()) {
            if(pav.getAttribute().getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    private void handleTest(Patient p, Map<String,Integer> headers, String[] line, int lineNumber) {
        Date sampleDate = null;
        String testId = line[headers.get("aanvraagTestNaam")] +", "+ line[headers.get("elementNaam")];
        
        try {
            sampleDate = GhbUtils.LISDateFormat.parse(line[headers.get("afname")]);
            if(!GhbUtils.isValidDate(sampleDate))
            	throw new Exception("invalid test date: "+ sampleDate);
            
            if(!lisTests.containsKey(testId))
                lisTests.put(testId,new ErrorTypes());
            
            String reeel = line[headers.get("reeel")];
            String resultaat = line[headers.get("resultaat")];
            
            //work with a mapping file
            if((reeel != null && reeel.length() > 0)
                    || (resultaat != null && resultaat.length() > 0)) {
            	
                String correctId = getCorrectSampleId(headers, line);
            	
                Map<String,String> variables = new HashMap<String,String>();
                for(Map.Entry<String, Integer> header : headers.entrySet()){
                	variables.put(header.getKey(),line[header.getValue()].trim());
                }
                variables.put("relatie+reeel", line[headers.get("relatie")]+reeel);
                
                long ul = 0;
                long geheel = 0;
                try{
                    double dreeel = Double.parseDouble(reeel);
                    ul = Math.round(dreeel * 1000);
                    geheel = Math.round(dreeel);
                }
                catch(Exception e){
                }
                variables.put("reeel*1000", ""+ul);
                variables.put("geheel", ""+ geheel);
                variables.put("relatie+geheel", line[headers.get("relatie")]+geheel);

                
                TestResult tr = null;
                tr = objectMapper.getTestResult(variables);
                tr.setSampleId(correctId);
                tr.setTestDate(sampleDate);
                
                if(duplicateTestResult(p, tr) != null){
                    logError(lineNumber, "Duplicate test result ignored");
                    return;
                }
                
                setFirstTestDate(tr);
                
                p.addTestResult(tr);
                
            }
            else{
                logError(lineNumber, "No result");
            }
        } 
        catch(MappingDoesNotExistException e){
            logError(lineNumber, e.getMessage());
            lisTests.get(testId).mapping = true;
            logNotImported(toString(line));
        }
        catch(ObjectDoesNotExistException e){
            logError(lineNumber, e.getMessage());
            lisTests.get(testId).object = true;
            logNotImported(toString(line));
        }
        catch(InvalidValueException e){
            logError(lineNumber, e.getMessage());
            lisTests.get(testId).value = true;
            logNotImported(toString(line));
        }
        catch (MappingException e) {
            logError(lineNumber, "MappingException: "+ e.getMessage());
            logNotImported(toString(line));
        }
        catch (ParseException e) {
            logNotImported(toString(line));
            logError("ParseException at line "+ lineNumber +": "+ e.getMessage());
        }
        catch (Exception e){
            logError(lineNumber, "Exception: "+ e.getMessage());
            e.printStackTrace();
            logNotImported(toString(line));
        }
    }
       
//    private void handleHiv1ViralLoad(
//    		Patient p, Date sampleDate, String sampleId, String aanvraagTestNaam, String labotestNaam,
//    		String berekeningNaam, String listestNaam, String elementNaam,String eenheden, String relatie,
//    		String reeel, String resultaat) throws Exception {
//
//    	if(!elementNaam.endsWith("sym") && reeel.length() != 0){
//    		String description;
//    		Test t;
//	    	if(elementNaam.equals("HIV-1 VL")){
//	    		if(labotestNaam.startsWith("Abbott"))
//	    			description = "Abbott Realtime";
//	    		else
//	    			description = StandardObjects.getGenericHiv1ViralLoadTest().getDescription();
//	    		
//	    		t = objectStore.getTest(description,
//	    				StandardObjects.getHiv1ViralLoadTestType().getDescription(),
//	    				StandardObjects.getHiv1Genome().getOrganismName());
//	    	}
//	    	else if(elementNaam.equals("HIV-1 VL log")){
//	    		if(labotestNaam.startsWith("Abbott"))
//	    			description = "Abbott Realtime (log10)";
//	    		else
//	    			description = StandardObjects.getGenericHiv1ViralLoadLog10Test().getDescription();
//	    		
//	    		t = objectStore.getTest(description,
//	    				StandardObjects.getHiv1ViralLoadTestType().getDescription(),
//	    				StandardObjects.getHiv1Genome().getOrganismName());
//	    		
//	    	}
//	    	else{
//	    		throw new Exception("Unknown viral load element name.");
//	    	}
//	    	
//	    	TestResult tr = new TestResult(t);
//    		tr.setSampleId(sampleId);
//    		tr.setTestDate(sampleDate);
//    		tr.setValue(relatie + reeel);
//    		
//    		if(duplicateTestResult(p, tr) == null){
//    			p.addTestResult(tr);
//    		}
//    		else{
//    			throw new Exception("Duplicate viral load.");
//    		}
//    	}
//    	else{
//    		String copies=null,log=null;
//    		if(resultaat.contains("<40") || resultaat.contains("< 40")){
//    			copies="<40";
//    			log="<1.6";
//    		}
//    		else if(resultaat.contains("<50")){
//    			copies="<50";
//    			log="<1.7";
//    		}
//    		else if(resultaat.contains("> 10000000")){
//    			copies=">10000000";
//    			log=">7";
//    		}
//    		
//    		for(TestResult tr : p.getTestResults()){
//    			if(tr.getTestDate().equals(sampleDate)){
//    				if(Equals.isSameTestType(tr.getTest().getTestType(), StandardObjects.getHiv1ViralLoadTestType())){
//    					tr.setValue(copies);
//    				}
//    				else if(Equals.isSameTestType(tr.getTest().getTestType(), StandardObjects.getHiv1ViralLoadLog10TestType())){
//    					tr.setValue(log);
//    				}
//    			}
//    		}
//    	}
//	}

	private void setFirstTestDate(TestResult tr) {
    	String description = tr.getTest().getTestType().getDescription(); 
    	if(description.equals(StandardObjects.getCd4TestType().getDescription()) && tr.getTestDate().before(firstCd4))
    		firstCd4 = tr.getTestDate();
    	else if(description.equals(StandardObjects.getCd8TestType().getDescription()) && tr.getTestDate().before(firstCd8))
    		firstCd8 = tr.getTestDate();
    	else if(description.equals(StandardObjects.getViralLoadDescription()) && tr.getTestDate().before(firstViralLoad))
    		firstViralLoad = tr.getTestDate();
    	else if(description.equals(StandardObjects.getSeroStatusDescription()) && tr.getTestDate().before(firstSeroStatus))
    		firstSeroStatus = tr.getTestDate();
	}

	private String getCorrectSampleId(Map<String,Integer> headers, String[] line) {
        String id = line[headers.get("otheeknr")];
        if(id.equals("")) {
            id = line[headers.get("staalId")];
        }
        if(id.equals("")) {
            id = line[headers.get("metingId")];
        }
        if(id.equals("")) {
           id = line[headers.get("berekeningId")];
        }

        id = id.trim();
        return id.length() == 0 ? null : id;
    }
    
//    private void handleNominalAttributeValue(Patient p, String name, String nominalValue) throws MappingException {
//        try {
//            Map<String,String> variables = new HashMap<String,String>();
//            variables.put("name", name);
//            variables.put("value", nominalValue);
//            PatientAttributeValue pav = objectMapper.getAttributeValue(variables);
//            p.addPatientAttributeValue(pav);
//
//        } catch (ObjectDoesNotExistException e) {
//            ConsoleLogger.getInstance().logWarning("Unsupported attribute value" + name + ": "+nominalValue);
//        } catch (MatcherException e) {
//			e.printStackTrace();
//		}
//    }
    
    private String[] tokenizeTab(String line) {
        return line.split("\t",-1);
    }
    
    private TestResult duplicateTestResult(Patient p, TestResult result) {
        for(TestResult tr : p.getTestResults()) {
            if(Equals.isSameTest(tr.getTest(),result.getTest()) &&
                    DateUtils.equals(tr.getTestDate(),result.getTestDate()) && 
                    equals(tr.getSampleId(),result.getSampleId())) {
            	return tr;
            }
        }
        return null;
    }
    
//    private TestResult dublicateTestTypeResult(Patient p, TestResult result) {
//        for(TestResult tr : p.getTestResults()) {
//            if(Equals.isSameTestType(tr.getTest().getTestType(),result.getTest().getTestType()) &&
//                    DateUtils.equals(tr.getTestDate(),result.getTestDate()) && 
//                    equals(tr.getSampleId(),result.getSampleId())) {
//            	return tr;
//            }
//        }
//        return null;
//    }
    
    private boolean equals(String s1, String s2){
    	if(s1 == s2)
    		return true;
    	if(s1 != null)
    		return s1.equals(s2);
    	return false;
    }
    
    private Patient getPatient(String ead){
        Dataset dataset = objectStore.getDataset(datasetDescription);
        return objectStore.getPatient(dataset, ead);
    }
//    private Patient createPatient(String ead){
//        Dataset dataset = objectStore.getDataset(datasetDescription);
//        return objectStore.createPatient(dataset, ead);
//    }
    
    private void logNotImported(String msg){
        importLog.println(msg);
    }
    private void logError(String msg){
        errLog.println(msg);
    }
    private void logError(int lineNumber, String msg){
        logError(lineNumber +": "+ msg);
    }
    private void logInfo(String msg){
        System.out.println(msg);
        logError(msg);
    }
    
    private String toString(String[] line){
        StringBuilder sb = new StringBuilder();
        for(String s : line){
            sb.append(s);
            sb.append('\t');
        }
        return sb.toString();
    }
    
    public void close(){
    	objectStore.close();
    }
}
