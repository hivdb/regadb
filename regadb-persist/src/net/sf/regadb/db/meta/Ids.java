package net.sf.regadb.db.meta;

import java.util.Set;

import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;

public class Ids {

    public static String getUniqueId(EventNominalValue eventNominalValue) {
        return combine(eventNominalValue.getValue(),getUniqueId(eventNominalValue.getEvent()));
    }

    public static String getUniqueId(Event event) {
        return event.getName();
    }

    public static String getUniqueId(ValueType valueType) {
        return valueType.getDescription();
    }

    public static String getUniqueId(Test test) {
        return combine(test.getDescription(),getUniqueId(test.getTestType()));
    }

    public static String getUniqueId(TestNominalValue testNominalValue) {
        return combine(testNominalValue.getValue(),getUniqueId(testNominalValue.getTestType()));
    }

    public static String getUniqueId(Analysis analysis) {
        return combine(analysis.getServiceName(),getUniqueId(analysis.getAnalysisDatas()));
    }

    public static String getUniqueId(Set<AnalysisData> analysisDatas) {
        StringBuilder id = new StringBuilder();
        for(AnalysisData ad : analysisDatas){
            id.append(getUniqueId(ad));
            id.append(';');
        }
        return id.toString();
    }

    public static String getUniqueId(TestType testType) {
        return testType.getDescription();
    }

    public static String getUniqueId(Attribute attribute) {
        return attribute.getName();
    }

    public static String getUniqueId(AttributeNominalValue attributeNominalValue) {
        return combine(attributeNominalValue.getValue(),getUniqueId(attributeNominalValue.getAttribute()));
    }

    public static String getUniqueId(TestObject testObject) {
        return testObject.getDescription();
    }

    public static String getUniqueId(AnalysisData analysisData) {
        StringBuilder id = new StringBuilder();
        id.append(analysisData.getName());
        id.append('=');
        id.append(analysisData.getData());
        return id.toString();
    }

    public static String getUniqueId(AttributeGroup attributeGroup) {
        return attributeGroup.getGroupName();
    }

    public static String getUniqueId(Protein protein) {
        return protein.getAbbreviation();
    }
    
    private static String combine(String ... keys){
        StringBuilder sb = new StringBuilder();
        for(String key : keys){
            sb.append(key);
            sb.append("::");
        }
        return sb.toString();
    }

}
