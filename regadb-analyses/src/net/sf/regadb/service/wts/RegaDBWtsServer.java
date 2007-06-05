package net.sf.regadb.service.wts;

import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisType;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;

public class RegaDBWtsServer 
{
    public static final String url_ = "http://zolder:8080/wts/services/";
    
    public static Test getHIV1SubTypeTest(TestObject to, AnalysisType analysisType, ValueType valueType)
    {
        TestType type = new TestType(to, "HIV-1 Subtype Test");
        type.setValueType(valueType);
        Test test = new Test(type, "Rega HIV-1 Subtype Tool");
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
    
    public static Test getHIVTypeTest(TestObject to, AnalysisType analysisType, ValueType valueType)
    {
        TestType type = new TestType(to, "HIV Type Test");
        type.setValueType(valueType);
        Test test = new Test(type, "Rega HIV Type Tool");
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
}
