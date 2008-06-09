package net.sf.regadb.service.wts;

import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;

public class RegaDBWtsServer 
{
    public static final String url_ = "http://regadb.med.kuleuven.be/wts/services/";
    
    public static Test getHIV1SubTypeTest(TestObject to, AnalysisType analysisType, ValueType valueType)
    {
        TestType type = new TestType(to, getSubTypeTestType());
        type.setValueType(valueType);
        Test test = new Test(type, getSubTypeTest());
        Analysis analysis = new Analysis(analysisType);
        analysis.setUrl(url_);
        analysis.setAccount("public");
        analysis.setPassword("public");
        analysis.setServiceName("regadb-hiv-subtype");
        analysis.setBaseinputfile("nt_sequence");
        analysis.setBaseoutputfile("subtype");
        test.setAnalysis(analysis);
        
        return test;
    }
    
    public static String getSubTypeTestType()
    {
        return "HIV-1 Subtype Test";
    }
    
    public static String getSubTypeTest()
    {
        return "Rega HIV-1 Subtype Tool";
    }
    
    public static Test getHIVTypeTest(TestObject to, AnalysisType analysisType, ValueType valueType)
    {
        TestType type = new TestType(to, getTypeTestType());
        type.setValueType(valueType);
        Test test = new Test(type, getTypeTest());
        Analysis analysis = new Analysis(analysisType);
        analysis.setUrl(url_);
        analysis.setAccount("public");
        analysis.setPassword("public");
        analysis.setServiceName("regadb-hiv-type");
        analysis.setBaseinputfile("nt_sequence");
        analysis.setBaseoutputfile("type");
        test.setAnalysis(analysis);
        
        return test;
    }
    
    public static String getTypeTestType()
    {
        return "HIV Type Test";
    }
    
    public static String getTypeTest()
    {
        return "Rega HIV Type Tool";
    }
}
