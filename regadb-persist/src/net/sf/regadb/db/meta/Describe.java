/*
 * Created on May 16, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db.meta;

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
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
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

public class Describe {

    public static String describe(Patient o) {
        return "Patient (in " + describe(o.getSourceDataset()) + "): '" + o.getPatientId() + "'";
    }

    public static String describe(AttributeNominalValue o) {
        return "Attribute nominal value '" + o.getValue() + "'";
    }

    public static String describe(ViralIsolate o) {
        return "Viral isolate '" + o.getSampleId() + "'";
    }

    public static String describe(NtSequence o) {
        return "Nucleotide sequence '" + o.getLabel() + "'";
    }

    public static String describe(AaSequence o) {
        return "Amino Acid sequence for " + describe(o.getProtein()) + "";
    }

    private static String describe(Protein o) {
        return "Protein " + o.getAbbreviation();
    }

    public static String describe(AaMutation o) {
        return "Mutation at position " + o.getId().getPosition();
    }

    public static String describe(AaInsertion o) {
        return "Insertion " + o.getId().getInsertionOrder() + " at position " + o.getId().getPosition();
    }

    public static String describe(Therapy o) {
        return "Therapy starting at date " + o.getStartDate();
    }

    public static String describe(TherapyCommercial o) {
        return describe(o);
    }

    public static String describe(TherapyGeneric o) {
        return describe(o);
    }

    public static String describe(TestResult o) {
        return "Result for " + describe(o.getTest()) + " on date " + o.getTestDate();
    }

    public static String describe(Test o) {
        return "Test '" + o.getDescription() + "'";
    }

    public static String describe(ValueType o) {
        return "Value type '" + o.getDescription() + "'";
    }

    public static String describe(TestObject o) {
        return "Test Category '" + o.getDescription() + "'";
    }

    public static String describe(TestNominalValue o) {
        return "Test nominal value '" + o.getValue() + "'";
    }

    public static String describe(PatientAttributeValue o) {
        return "Value for " + describe(o.getId().getAttribute());
    }

    public static String describe(Attribute o) {
        return "Attribute '" + o.getName() + "'";
    }

    public static String describe(TestType o) {
        return "Test type '" + o.getDescription() + "'";
    }

    public static String describe(DrugGeneric o) {
        return "Generic drug '" + o.getGenericId() + "'";
    }

    public static String describe(DrugCommercial o) {
        return "Commercial drug '" + o.getName() + "'";
    }

    public static String describe(Dataset o) {
        if (o == null)
            return "null Data set";
        else
            return "Data set '" + o.getDescription() + "'";
    }
    
    public static String describe(AttributeGroup o) {
        return "Attribute group '" + o.getGroupName() + "'";
    }

    public static String describe(Analysis o) {
        return "Analysis for '" + describe(o.getTests().iterator().next());
    }

    public static String describe(AnalysisData o) {
        return "Analysis data'" + o.getName() + "'";
    }

}
