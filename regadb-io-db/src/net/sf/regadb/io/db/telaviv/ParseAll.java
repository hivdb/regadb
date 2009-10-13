package net.sf.regadb.io.db.telaviv;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.Patient;
import net.sf.regadb.io.db.telaviv.ParseDrugs.Drug;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Logging;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ParseAll {
    
    public static void main(String[] args){
    	Arguments as = new Arguments();
    	ValueArgument confDir = as.addValueArgument("c", "conf-dir", false);
    	PositionalArgument importDir = as.addPositionalArgument("import-dir", true);
    	PositionalArgument mappingDir = as.addPositionalArgument("mapping-dir", true);
    	PositionalArgument outputDir = as.addPositionalArgument("output-dir", true);
    	
    	if(!as.handle(args))
    		return;
    	
    	if(confDir.isSet())
    		RegaDBSettings.createInstance(confDir.getValue());
    	else
    		RegaDBSettings.createInstance();
    	
        ParseAll pa = new ParseAll();
        pa.run(importDir.getValue(),
        		mappingDir.getValue(),
        		outputDir.getValue());
    }
    
    public ParseAll(){
        
    }
    
    public void run(String importDir, String mappingDir, String outputDir){
        Logging logger = ConsoleLogger.getInstance();
        logger.logInfo("Parsing...");
        
        List<DateFormat> df = new ArrayList<DateFormat>();
        df.add(new SimpleDateFormat("MM/dd/yy HH:mm:ss"));
        df.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        
        ParsePatients pPatients = new ParsePatients(logger,df);
        ParseDrugs pDrugs = new ParseDrugs(logger);
        ParseTherapies pTherapies = new ParseTherapies(logger,df);
        ParseTests pTests = new ParseTests(logger,df);
        ParseSequences pSeqs = new ParseSequences(logger,df);
        
        Map<String,Drug> drugs = pDrugs.run(getFile(mappingDir,"drug.mapping"));
        
        Map<String,Patient> patients = pPatients.run(   getFile(importDir,"ID.csv"),
                                                        getFile(mappingDir,"gender.mapping"),
                                                        getFile(mappingDir,"country.mapping"),
                                                        getFile(mappingDir,"transmission_group.mapping"));
        
        pTherapies.run( patients,
                        drugs,
                        getFile(importDir,"TreatHistorySon.csv"),
                        getFile(mappingDir,"therapy_motivation.mapping"));
        
        pTests.run(     patients,
                        getFile(importDir,"Samples.csv"),
                        getFile(importDir,"ClinDataSon.csv"),
                        getFile(importDir,"VLSysNames.csv"),
                        getFile(importDir,"VLunits.csv"));
        
        pSeqs.run(getFile(importDir,"Sequences.csv"), getFile(importDir,"Samples.csv"), patients);
        
        //pSeqs.run(      getFile(importDir,""));
        
        IOUtils.exportPatientsXML(patients.values(), outputDir + File.separatorChar + "patients.xml", ConsoleLogger.getInstance());
        //Utils.exportNTXML(viralisolates, outputDir + File.separatorChar + "viralisolates.xml");
        
        logger.logInfo("Done.");
    }
    
    private File getFile(String dir, String name){
        return new File(new File(dir).getAbsolutePath() + File.separatorChar + name);
    }

}
