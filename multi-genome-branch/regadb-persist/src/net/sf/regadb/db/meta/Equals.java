/*
 * Created on May 16, 2007
 *
 * To change the template o2or this generated o2ile go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db.meta;

import java.util.Date;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.ViralIsolate;

public class Equals {

    public static boolean isSameTest(Test o1, Test o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getDescription().equals(o2.getDescription()));
    }

    public static boolean isSameTestNominalValue(TestNominalValue o1, TestNominalValue o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getValue().equals(o2.getValue()));
    }
    
    public static boolean isSameEventNominalValue(EventNominalValue o1, EventNominalValue o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getValue().equals(o2.getValue()));
    }

    public static boolean isSameTestType(TestType o1, TestType o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getDescription().equals(o2.getDescription())
                && isSameGenome(o1.getGenome(), o2.getGenome()));
    }
    
    public static boolean isSameGenome(Genome o1, Genome o2){
        return o1 == o2
        || (o1 != null && o2 != null && o1.getOrganismName().equals(o2.getOrganismName()));
    }

    public static boolean isSameDataset(Dataset o1, Dataset o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getDescription().equals(o2.getDescription()));
    }

    public static boolean isSameTestResult(TestResult o1, TestResult o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getTestDate().equals(o2.getTestDate())
                && isSameTest(o1.getTest(), o2.getTest())
                && isSameDrugGeneric(o1.getDrugGeneric(), o2.getDrugGeneric())
                && isSameViralIsolate(o1.getViralIsolate(), o2.getViralIsolate())
                && isSameNtSequence(o1.getNtSequence(), o2.getNtSequence()));
    }

    public static boolean isSameDrugGeneric(DrugGeneric o1, DrugGeneric o2) {
        return o1 == o2;
    }

    public static boolean isSamePatientAttributeValue(PatientAttributeValue o1, PatientAttributeValue o2) {
        if(o1 == o2) return true;
        
        String v1=null, v2=null;
        Attribute a1=null, a2=null;
        AttributeNominalValue nv1=null, nv2=null;

        if(o1 != null){
            v1 = o1.getValue();
            a1 = o1.getAttribute();
            nv1 = o1.getAttributeNominalValue();
        }
        if(o2 != null){
            v2 = o2.getValue();
            a2 = o2.getAttribute();
            nv2 = o2.getAttributeNominalValue();
        }

        return (   isSameAttributeNominalValue(nv1,nv2)
                && isSameAttribute(a1,a2)
                && isSameString(v1,v2));
    }
    
    public static boolean isSamePatientEventValue(PatientEventValue o1, PatientEventValue o2) {
        Date end1 = o1.getEndDate();
        Date end2 = o2.getEndDate();

        return o1 == o2
        || (o1 != null && o2 != null
                && o1.getStartDate().equals(o2.getStartDate())
                && (end1 == end2 || (end1 != null && end2 != null && end1.equals(end2)))
                && isSameEventNominalValue(o1.getEventNominalValue(),o2.getEventNominalValue())
                && isSameEvent(o1.getEvent(), o2.getEvent()));
    }

    public static boolean isSameViralIsolate(ViralIsolate o1, ViralIsolate o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getSampleId().equals(o2.getSampleId()));
    }

    public static boolean isSameTherapy(Therapy o1, Therapy o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getStartDate().equals(o2.getStartDate()));
    }

    public static boolean isSameNtSequence(NtSequence o1, NtSequence o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getLabel().equals(o2.getLabel()));
    }

    public static boolean isSameAaSequence(AaSequence o1, AaSequence o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getProtein() == o2.getProtein());
    }

    public static boolean isSameAaInsertion(AaInsertion o1, AaInsertion o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getId().getInsertionPosition() == o2.getId().getInsertionPosition()
                && o1.getId().getInsertionOrder() == o2.getId().getInsertionOrder());
    }

    public static boolean isSameTherapyCommercial(TherapyCommercial o1, TherapyCommercial o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getId().getDrugCommercial() == o2.getId().getDrugCommercial());
    }

    public static boolean isSameTherapyGeneric(TherapyGeneric o1, TherapyGeneric o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getId().getDrugGeneric() == o2.getId().getDrugGeneric());
    }

    public static boolean isSameAaMutation(AaMutation o1, AaMutation o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getId().getMutationPosition() == o2.getId().getMutationPosition());
    }

    public static boolean isSameAttribute(Attribute o1, Attribute o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getName().equals(o2.getName()));
    }
    
    public static boolean isSameEvent(Event o1, Event o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getName().equals(o2.getName()));
    }

    public static boolean isSameAttributeNominalValue(AttributeNominalValue o1, AttributeNominalValue o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getValue().equals(o2.getValue()));
    }

    public static boolean isSameValueType(ValueType o1, ValueType o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getDescription().equals(o2.getDescription()));
    }

    public static boolean isSameTestObject(TestObject o1, TestObject o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getDescription().equals(o2.getDescription()));
    }
    
    public static boolean isSameAttributeGroup(AttributeGroup o1, AttributeGroup o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getGroupName().equals(o2.getGroupName()));
    }

    public static boolean isSameAnalysis(Analysis o1, Analysis o2) {
        return true; // only analysis in an object
    }

    public static boolean isSameAnalysisData(AnalysisData o1, AnalysisData o2) {
        return o1 == o2
        || (o1 != null && o2 !=null && o1.getName().equals(o2.getName()));
    }
    
    public static boolean isSameString(String s1, String s2){
        return s1 == s2
            || (s1 != null && s1.equals(s2));
    }

	public static boolean isSameProtein(Protein protein, Protein protein2) {
		return protein == protein2 
		|| (protein != null && protein2 != null && protein.getProteinIi().equals(protein2.getProteinIi()));
	}

	public static boolean isSameDrugCommercial(DrugCommercial drugCommercial,
			DrugCommercial drugCommercial2) {
		return drugCommercial == drugCommercial2 
		|| (drugCommercial != null && drugCommercial2 != null && drugCommercial.getCommercialIi().equals(drugCommercial2.getCommercialIi()));
	}

}
