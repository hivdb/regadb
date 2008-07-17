package net.sf.regadb.service.wts;

import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;

public class RegaDBWtsServer 
{
    private static final String url_ = "http://regadb.med.kuleuven.be/wts/services/";
    
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
        return url_;
    }
}
