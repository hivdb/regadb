package net.sf.regadb.service.wts;

import java.io.File;
import java.io.IOException;

import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestType;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.settings.InstituteConfig;

public class RegaDBWtsServer 
{
	private static Test subtypeTest = null;
	
    public static String getUrl() {
    	return InstituteConfig.getDefaultWtsUrl();
    }
    
    private static File getFile(String localFileName, String provider, String remoteFileName) throws IOException{
    	FileProvider fp = new FileProvider();
        File f = File.createTempFile(localFileName, ".xml");
        fp.getFile(provider, remoteFileName, f);
        return f;
    }
    
    public static File getDrugClasses() throws IOException{
    	return getFile("drug-classes","regadb-drugs","DrugClasses-genomes.xml");
    }
    public static File getDrugGenerics() throws IOException{
    	return getFile("drug-enerics","regadb-drugs","DrugGenerics-genomes.xml");
    }
    public static File getDrugCommercials() throws IOException{
    	return getFile("drug-commercials","regadb-drugs","DrugCommercials-genomes.xml");
    }
    public static File getAttributes() throws IOException{
    	return getFile("attributes","regadb-attributes","attributes.xml");
    }
    public static File getEvents() throws IOException{
    	return getFile("events","regadb-events","events.xml");
    }
    public static File getGenomes() throws IOException{
    	return getFile("genomes","regadb-genomes","genomes.xml");
    }
    public static File getTests() throws IOException{
    	return getFile("tests","regadb-tests","tests-genomes.xml");
    }

    public static synchronized Test getSubtypeTest(){
    	if(subtypeTest == null)
    		subtypeTest = createSubtypeTest();
    	return subtypeTest;
    }
    private static Test createSubtypeTest()
    {
        TestType type = new TestType(StandardObjects.getSequenceAnalysisTestObject(), StandardObjects.getSubtypeTestTypeDescription());
        type.setValueType(StandardObjects.getStringValueType());
        Test test = new Test(type, StandardObjects.getSubtypeTestDescription());
        AnalysisType wts = new AnalysisType("wts");
        Analysis analysis = new Analysis(wts);
        analysis.setUrl(RegaDBWtsServer.getUrl());
        analysis.setAccount("public");
        analysis.setPassword("public");
        analysis.setServiceName("regadb-subtype");
        analysis.setBaseinputfile("nt_sequence");
        analysis.setBaseoutputfile("subtype");
        test.setAnalysis(analysis);
        
        return test;
    }
}
