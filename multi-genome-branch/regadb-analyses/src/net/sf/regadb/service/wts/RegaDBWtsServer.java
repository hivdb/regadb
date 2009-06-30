package net.sf.regadb.service.wts;

import java.io.File;
import java.io.IOException;

import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.util.settings.RegaDBSettings;

public class RegaDBWtsServer 
{
    public static Test getSubtypeTest(TestObject to, AnalysisType analysisType, ValueType valueType)
    {
        TestType type = new TestType(to, getSubtypeTestType());
        type.setValueType(valueType);
        Test test = new Test(type, getSubtypeTest());
        Analysis analysis = new Analysis(analysisType);
        analysis.setUrl(getUrl());
        analysis.setAccount("public");
        analysis.setPassword("public");
        analysis.setServiceName("regadb-subtype");
        analysis.setBaseinputfile("nt_sequence");
        //analysis.setBaseinputfile("species");
        analysis.setBaseoutputfile("subtype");
        test.setAnalysis(analysis);
        
        return test;
    }
    
    public static String getSubtypeTestType()
    {
        return "Subtype Test";
    }
    
    public static String getSubtypeTest()
    {
        return "Rega Subtype Tool";
    }
    
//    public static Test getBlastTest(TestObject to, AnalysisType analysisType, ValueType valueType)
//    {
//        TestType type = new TestType(to, getBlastTestType());
//        type.setValueType(valueType);
//        Test test = new Test(type, getBlastTest());
//        Analysis analysis = new Analysis(analysisType);
//        analysis.setUrl(getUrl());
//        analysis.setAccount("public");
//        analysis.setPassword("public");
//        analysis.setServiceName("regadb-blast");
//        analysis.setBaseinputfile("nt_sequence");
//        analysis.setBaseoutputfile("species");
//        test.setAnalysis(analysis);
//        
//        return test;
//    }
//    
//    public static String getBlastTestType()
//    {
//        return "Blast Test";
//    }
//    public static String getBlastTest(){
//        return "Rega Blast Tool";
//    }
    
    public static String getUrl() {
    	return RegaDBSettings.getInstance().getInstituteConfig().getServiceProviderUrl();
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

}
