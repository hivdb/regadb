package com.pharmadm.custom.rega.queryeditor;

import java.sql.SQLException;
import java.util.*;
import net.sf.regadb.db.Attribute;

public class HibernateAWCPrototypeCatalog extends AWCPrototypeCatalog {
	    
	    private static AWCPrototypeCatalog mainCatalog = null;
	    
	    public static AWCPrototypeCatalog getInstance() {
	        if (mainCatalog == null) {
	            initMainCatalog();
	        }
	        return mainCatalog;
	    }
	    

	    private static void initMainCatalog() {
	    	HibernateAWCPrototypeCatalog catalog = new HibernateAWCPrototypeCatalog();
	        mainCatalog = catalog;

	    	
	        catalog.addGoodVariableName("net.sf.regadb.db.PatientImpl", "patient");
	        catalog.addGoodVariableName("net.sf.regadb.db.Therapy", "therapy");
	        catalog.addGoodVariableName("net.sf.regadb.db.ViralIsolate", "viralIsolate");
	        catalog.addGoodVariableName("net.sf.regadb.db.NtSequence", "ntSequence");
	        catalog.addGoodVariableName("net.sf.regadb.db.AaSequence", "aaSequence");
	        catalog.addGoodVariableName("net.sf.regadb.db.AaMutation", "aaMutation");
	        catalog.addGoodVariableName("net.sf.regadb.db.AaInsertion", "aaInsertion");
	        catalog.addGoodVariableName("net.sf.regadb.db.PatientAttributeValue", "attribute");
	        catalog.addGoodVariableName("net.sf.regadb.db.DrugGeneric", "genericDrug");
	        catalog.addGoodVariableName("net.sf.regadb.db.DrugCommercial", "commercialDrug");
	        catalog.addGoodVariableName("net.sf.regadb.db.DrugClass", "drugClass");
	        catalog.addGoodVariableName("net.sf.regadb.db.TherapyCommercial", "commercialTherapy");
	        catalog.addGoodVariableName("net.sf.regadb.db.TherapyGeneric", "genericTherapy");
	        catalog.addGoodVariableName("net.sf.regadb.db.Dataset", "dataset");
	        catalog.addGoodVariableName("net.sf.regadb.db.TestResult", "testResult");
	        catalog.addGoodVariableName("net.sf.regadb.db.Test", "test");
	        
	        catalog.addGoodVariableName("net.sf.regadb.db.PatientImpl.patientId", "PatientId");
	        catalog.addGoodVariableName("net.sf.regadb.db.PatientImpl.birthDate", "BirthDate");
	        catalog.addGoodVariableName("net.sf.regadb.db.PatientImpl.deathDate", "DeathDate");
	        catalog.addGoodVariableName("net.sf.regadb.db.PatientImpl.lastName", "LastName");
	        catalog.addGoodVariableName("net.sf.regadb.db.PatientImpl.firstName", "FirstName");
	        
	        catalog.addGoodVariableName("net.sf.regadb.db.Therapy.startDate", "StartDate");
	        catalog.addGoodVariableName("net.sf.regadb.db.Therapy.stopDate", "StopDate");
	        catalog.addGoodVariableName("net.sf.regadb.db.Therapy.comment", "Comment");
	        catalog.addGoodVariableName("net.sf.regadb.db.TherapyMotivation.value", "Motivation");

	        catalog.addGoodVariableName("net.sf.regadb.db.ViralIsolate.sampleDate", "SampleDate");
	        catalog.addGoodVariableName("net.sf.regadb.db.ViralIsolate.sampleId", "SampleId");
	        
	        catalog.addGoodVariableName("net.sf.regadb.db.NtSequence.sequenceDate", "SequenceDate");
	        catalog.addGoodVariableName("net.sf.regadb.db.NtSequence.label", "Value");
	        catalog.addGoodVariableName("net.sf.regadb.db.NtSequence.nucleotides", "Nucleotides");
	        
	        catalog.addGoodVariableName("net.sf.regadb.db.Protein.abbreviation", "ProteinAbbreviation");
	        catalog.addGoodVariableName("net.sf.regadb.db.Protein.fullName", "ProteinName");

	        catalog.addGoodVariableName("net.sf.regadb.db.AaSequence.firstAaPos", "AaPosition");
	        catalog.addGoodVariableName("net.sf.regadb.db.AaSequence.lastAaPos", "AaPosition");

	        catalog.addGoodVariableName("net.sf.regadb.db.AaMutation.aaReference", "AaStr");
	        catalog.addGoodVariableName("net.sf.regadb.db.AaMutation.aaMutation", "AaStr");
	        catalog.addGoodVariableName("net.sf.regadb.db.AaMutation.ntReferenceCodon", "NtStr");
	        catalog.addGoodVariableName("net.sf.regadb.db.AaMutation.ntMutationCodon", "NtStr");
	        catalog.addGoodVariableName("net.sf.regadb.db.AaMutation.id.mutationPosition", "MutationPosition");
	        
	        catalog.addGoodVariableName("net.sf.regadb.db.AaInsertion.aaInsertion", "AaStr");
	        catalog.addGoodVariableName("net.sf.regadb.db.AaInsertion.ntInsertionCodon", "NtStr");
	        catalog.addGoodVariableName("net.sf.regadb.db.AaInsertion.id.insertionPosition", "InsertionPosition");
	        catalog.addGoodVariableName("net.sf.regadb.db.AaInsertion.id.insertionOrder", "InsertionOrder");
	        
	        catalog.addGoodVariableName("net.sf.regadb.db.DrugClass.classId", "DrugClassId");
	        catalog.addGoodVariableName("net.sf.regadb.db.DrugClass.className", "DrugClassName");
	        catalog.addGoodVariableName("net.sf.regadb.db.DrugClass.resistanceTableOrder", "ResitanceTableOrder");

	        catalog.addGoodVariableName("net.sf.regadb.db.DrugClass.classId", "DrugClassId");
	        catalog.addGoodVariableName("net.sf.regadb.db.DrugClass.className", "DrugClassName");
	        catalog.addGoodVariableName("net.sf.regadb.db.DrugClass.resistanceTableOrder", "ResitanceTableOrder");
	        catalog.addGoodVariableName("net.sf.regadb.db.DrugClass.classId", "DrugClassId");
	        
	        catalog.addGoodVariableName("net.sf.regadb.db.DrugClass.className", "DrugClassName");
	        catalog.addGoodVariableName("net.sf.regadb.db.DrugClass.resistanceTableOrder", "ResitanceTableOrder");
	        catalog.addGoodVariableName("net.sf.regadb.db.DrugClass.classId", "DrugClassId");
	        
	        catalog.addGoodVariableName("net.sf.regadb.db.TherapyCommercial.dayDosageUnits", "DailyDosage");
	        catalog.addGoodVariableName("net.sf.regadb.db.TherapyCommercial.frequency", "Frequency");
	        catalog.addGoodVariableName("net.sf.regadb.db.TherapyCommercial.placebo", "Placebo");
	        catalog.addGoodVariableName("net.sf.regadb.db.TherapyCommercial.blind", "Blind");
	        
	        catalog.addGoodVariableName("net.sf.regadb.db.TherapyGeneric.dayDosageUnits", "DailyDosage");
	        catalog.addGoodVariableName("net.sf.regadb.db.TherapyGeneric.frequency", "Frequency");
	        catalog.addGoodVariableName("net.sf.regadb.db.TherapyGeneric.placebo", "Placebo");
	        catalog.addGoodVariableName("net.sf.regadb.db.TherapyGeneric.blind", "Blind");
	        
	        catalog.addGoodTableName("net.sf.regadb.db.PatientEventValue", "event");
	        catalog.addGoodTableName("net.sf.regadb.db.PatientImpl", "patient");
	        catalog.addGoodTableName("net.sf.regadb.db.Therapy", "therapy");
	        catalog.addGoodTableName("net.sf.regadb.db.ViralIsolate", "viral isolate");
	        catalog.addGoodTableName("net.sf.regadb.db.NtSequence", "nucleotide sequence");
	        catalog.addGoodTableName("net.sf.regadb.db.AaSequence", "amino acid sequence");
	        catalog.addGoodTableName("net.sf.regadb.db.AaMutation", "amino acid mutation");
	        catalog.addGoodTableName("net.sf.regadb.db.AaInsertion", "amino acid insertion");
	        catalog.addGoodTableName("net.sf.regadb.db.PatientAttributeValue", "attribute");
	        catalog.addGoodTableName("net.sf.regadb.db.DrugGeneric", "generic drug");
	        catalog.addGoodTableName("net.sf.regadb.db.DrugCommercial", "commercial drug");
	        catalog.addGoodTableName("net.sf.regadb.db.DrugClass", "drug class");
	        catalog.addGoodTableName("net.sf.regadb.db.TherapyCommercial", "treatment with a commercial drug");
	        catalog.addGoodTableName("net.sf.regadb.db.TherapyGeneric", "treatment with a generic drug");
	        catalog.addGoodTableName("net.sf.regadb.db.Dataset", "dataset");
	        catalog.addGoodTableName("net.sf.regadb.db.TestResult", "test result");
	        catalog.addGoodTableName("net.sf.regadb.db.Test", "test");
	        
	        catalog.addGoodDbName("net.sf.regadb.db.PatientImpl", "patient");
	        catalog.addGoodDbName("net.sf.regadb.db.TherapyMotivation", "motivation");
	        catalog.addGoodDbName("net.sf.regadb.db.Therapy", "therapy");
	        catalog.addGoodDbName("net.sf.regadb.db.ViralIsolate", "vi");
	        catalog.addGoodDbName("net.sf.regadb.db.NtSequence", "ntSeq");
	        catalog.addGoodDbName("net.sf.regadb.db.AaSequence", "aaSeq");
	        catalog.addGoodDbName("net.sf.regadb.db.AaMutation", "aaMut");
	        catalog.addGoodDbName("net.sf.regadb.db.AaInsertion", "aaIns");
	        catalog.addGoodDbName("net.sf.regadb.db.PatientAttributeValue", "attribute");
	        catalog.addGoodDbName("net.sf.regadb.db.DrugGeneric", "gDrug");
	        catalog.addGoodDbName("net.sf.regadb.db.DrugCommercial", "cDrug");
	        catalog.addGoodDbName("net.sf.regadb.db.DrugClass", "drug class");
	        catalog.addGoodDbName("net.sf.regadb.db.TherapyCommercial", "cTherapy");
	        catalog.addGoodDbName("net.sf.regadb.db.TherapyGeneric", "gTherapy");
	        catalog.addGoodDbName("net.sf.regadb.db.Dataset", "dataset");
	        catalog.addGoodDbName("net.sf.regadb.db.TestResult", "result");
	        catalog.addGoodDbName("net.sf.regadb.db.Test", "test");

	        
	        
	        
	        /*	        
	        catalog.addGoodVariableName("", "");
	        catalog.addGoodVariableName("", "");
	        catalog.addGoodVariableName("", "");
	        
	        
	        catalog.addGoodVariableName("GENERIC_NAME", "Name");
	        catalog.addGoodVariableName("ATC_CODE", "AtcCode");
*/	        
	        catalog.addRealValueConstraintClause(true);
	        catalog.addRealValueConstraintClause(false);
	        catalog.addRealValueIntervalClause();
	        catalog.addRealValueCompareClause();
	        catalog.addRealConstantToVariableClause();
	        catalog.addRealValueEqualsClause();
	        
	        catalog.addTimeConstantClause(true);
	        catalog.addTimeConstantClause(false);
	        catalog.addTimeIntervalClause();
	        catalog.addTimeCompareClause();
	        catalog.addTimeCalculationClause(true);
	        catalog.addTimeCalculationClause(false);
	        catalog.addTimeConstantToVariableClause();
	        catalog.addTimeEqualsClause();

	        
	        
	        ///////////////////////////////////////
	        // events
	        catalog.addBaseClause("net.sf.regadb.db.ValueType");
	        AtomicWhereClause mvClause = catalog.addBaseClauseWithConstraint("net.sf.regadb.db.PatientEventValue", "event", "net.sf.regadb.db.Event", null, "net.sf.regadb.db.Event", null, "name", "is of type", false);
	        catalog.addMandatoryValuesToClause(mvClause, new String[] {"net.sf.regadb.db.Event"}, new String[] {"name"});
	        catalog.addDateClauses(catalog, "net.sf.regadb.db.PatientEventValue", null, "net.sf.regadb.db.PatientEventValue", null, "net.sf.regadb.db.PatientEventValue", null, "startDate", "started on");
	        catalog.addDateClauses(catalog, "net.sf.regadb.db.PatientEventValue", null, "net.sf.regadb.db.PatientEventValue", null, "net.sf.regadb.db.PatientEventValue", null, "endDate", "ended on");
	        catalog.addMandatoryValuesToClause(catalog.addPropertyCheckClause("net.sf.regadb.db.PatientEventValue", null, "net.sf.regadb.db.PatientEventValue", null, "net.sf.regadb.db.PatientEventValue", null, "value", "has value", false, false),
	        		"net.sf.regadb.db.PatientEventValue", "event", "net.sf.regadb.db.EventNominalValue", mvClause.getFromVariables().iterator().next(), mvClause.getConstants().iterator().next());
	        
	        
	        // link patients - event
	        catalog.addGetAssociationClauses("net.sf.regadb.db.PatientEventValue", "patient", "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "comes from patient",  "has an event");
	        
	        
	        ///////////////////////////////////////
	        // patients
	        catalog.addBaseClause("net.sf.regadb.db.PatientImpl");
	   		catalog.addPropertyCheckClause("net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null,  "net.sf.regadb.db.PatientImpl", null, "patientId",  "has id", false, false);
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "lastName", "has last name", false);
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "firstName", "has first name", false);
	        catalog.addDateClauses(catalog, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "birthDate", "is born on");
	        catalog.addDateClauses(catalog, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "deathDate", "has died on");

	        // link patients - therapy
	        catalog.addGetAssociationClauses("net.sf.regadb.db.Therapy", "patient", "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "was performed on patient",  "has received therapy");
	        
	        // link patient - viral isolate
	        catalog.addGetAssociationClauses("net.sf.regadb.db.ViralIsolate", "patient", "net.sf.regadb.db.PatientImpl",  null, "net.sf.regadb.db.PatientImpl", null, "comes from patient",  "has a viral isolate");
	        
	        // link patient - dataset
//	        catalog.addGetAssociationClauses("net.sf.regadb.db.Dataset", "id.patient", "net.sf.regadb.db.PatientImpl",  null, "net.sf.regadb.db.PatientImpl", "patientIi", "has patient",  "is in dataset");
	        
	        

	        ///////////////////////////////////////
	        // data sets
	        catalog.addBaseClause("net.sf.regadb.db.Dataset");
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.Dataset", null, "net.sf.regadb.db.Dataset", null, "net.sf.regadb.db.Dataset", null, "description", "has name", false, true);

	   		
	   		
	        
	        ///////////////////////////////////////
	        // therapies
	        catalog.addBaseClause("net.sf.regadb.db.Therapy");
	        catalog.addDateClauses(catalog, "net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "startDate", "was started on");
	        catalog.addDateClauses(catalog, "net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "stopDate", "was stopped on");
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "comment", "has a comment", false);
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.Therapy", "therapyMotivation", "net.sf.regadb.db.TherapyMotivation", null, "net.sf.regadb.db.TherapyMotivation", null, "value", "has motivation", false, true);
	   		
	        // link therapy - therapyCommercial
	        catalog.addGetAssociationClauses("net.sf.regadb.db.TherapyCommercial", "id.therapy", "net.sf.regadb.db.Therapy", null , "net.sf.regadb.db.Therapy", null, "is part of the therapy",  "has a commercial drug treatment");

	        // link therapy - therapyGeneric
	        catalog.addGetAssociationClauses("net.sf.regadb.db.TherapyGeneric", "id.therapy", "net.sf.regadb.db.Therapy", null , "net.sf.regadb.db.Therapy", null, "is part of the therapy",  "has a generic drug treatment");
	        


	        ///////////////////////////////////////
	        // therapyCommercial
	        catalog.addNumberClauses(catalog, "net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null, "dayDosageUnits", "has a daily dosage", true);
	        catalog.addNumberClauses(catalog, "net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null, "frequency", "has a frequency", false);
	   		catalog.addBooleanPropertyClause("net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null,  "net.sf.regadb.db.TherapyCommercial", null, "placebo",  "is a placebo");
	   		catalog.addBooleanPropertyClause("net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null,  "net.sf.regadb.db.TherapyCommercial", null, "blind",  "is blind");
	        
	        
	        
	        // link therapyCommercial - DrugCommercial
	        catalog.addGetAssociationClauses("net.sf.regadb.db.DrugCommercial", null, "net.sf.regadb.db.TherapyCommercial", "id.drugCommercial", "net.sf.regadb.db.DrugCommercial", null, "is used in the treatment",  "consist of the commercial drug");
	        
	        
	        
	        ///////////////////////////////////////
	        // therapyGeneric
	        catalog.addNumberClauses(catalog, "net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null, "dayDosageMg", "has a daily dosage in mg", true);
	        catalog.addNumberClauses(catalog, "net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null, "frequency", "has a frequency", false);
	   		catalog.addBooleanPropertyClause("net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null,  "net.sf.regadb.db.TherapyGeneric", null, "placebo",  "is a placebo");
	   		catalog.addBooleanPropertyClause("net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null,  "net.sf.regadb.db.TherapyGeneric", null, "blind",  "is blind");
	        
	        
	        // link therapyGeneric - DrugCommercial
	        catalog.addGetAssociationClauses("net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.TherapyGeneric", "id.drugGeneric", "net.sf.regadb.db.DrugGeneric", null, "is used in the treatment",  "consist of the generic drug");
	        
	        
	        
	        ///////////////////////////////////////
	        // viral isolates
	        catalog.addBaseClause("net.sf.regadb.db.ViralIsolate");
	        catalog.addPropertyCheckClause("net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "sampleId", "has Id", false, false);
	        catalog.addDateClauses(catalog, "net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "sampleDate", "was taken on");
	        
	        // link viral isolate  - nt sequence
	        catalog.addGetAssociationClauses("net.sf.regadb.db.NtSequence", "viralIsolate", "net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "comes from the viral isolate",  "has a nucleotide sequence");
	 
	        
	        ///////////////////////////////////////
	        // nucleotide sequence
	        catalog.addBaseClause("net.sf.regadb.db.NtSequence");
	        catalog.addDateClauses(catalog, "net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "sequenceDate", "was sequenced on");
	        catalog.addStringClauses(catalog, "net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "label", "has label", false);
	        catalog.addStringClauses(catalog, "net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "nucleotides", "has nucleotides", false);
	        
	        // link nt sequence - aa sequence
	        catalog.addGetAssociationClauses("net.sf.regadb.db.AaSequence", "ntSequence", "net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "comes from the nucleotide sequence",  "has a amino acid sequence");

	        

	        ///////////////////////////////////////
	        // amino acid sequence
	        catalog.addBaseClause("net.sf.regadb.db.AaSequence");
	        catalog.addNumberClauses(catalog, "net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "firstAaPos", "has first amino acid position", false);
	        catalog.addNumberClauses(catalog, "net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "lastAaPos", "has last amino acid position", false);
	        
	        // link aa sequence - aa mutation
	        catalog.addGetAssociationClauses("net.sf.regadb.db.AaMutation", "id.aaSequence", "net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "comes from the amino acid sequence",  "has an amino acid mutation");
	        
	        // link aa sequence - aa insertion
	        catalog.addGetAssociationClauses("net.sf.regadb.db.AaInsertion", "id.aaSequence", "net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "comes from the amino acid sequence",  "has an amino acid insertion");
	        
	        // link aa sequence - protein
	        catalog.addGetAssociationClauses("net.sf.regadb.db.Protein", null, "net.sf.regadb.db.AaSequence", "protein", "net.sf.regadb.db.Protein", null, "is present in the amino acid sequence",  "has a protein");

	        
	        
	        ///////////////////////////////////////
	        // protein
	        catalog.addStringClauses(catalog, "net.sf.regadb.db.Protein", null, "net.sf.regadb.db.Protein", null, "net.sf.regadb.db.Protein", null,"abbreviation", "has abbreviation", false, true);
	        catalog.addStringClauses(catalog, "net.sf.regadb.db.Protein", null, "net.sf.regadb.db.Protein", null, "net.sf.regadb.db.Protein", null,"fullName", "has name", false, true);

	        
	        
	        ///////////////////////////////////////
	        // amino acid mutation
	        catalog.addBaseClause("net.sf.regadb.db.AaMutation");
	        catalog.addStringClauses(catalog, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation",  null , "aaReference", "has amino acid reference", false);
	        catalog.addStringClauses(catalog, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation",  null , "aaMutation", "has amino acid mutation", false);
	        catalog.addStringClauses(catalog, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation",  null , "ntReferenceCodon", "has nucleotide reference", false);
	        catalog.addStringClauses(catalog, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation",  null , "ntMutationCodon", "has nucleotide mutation", false);
	        catalog.addNumberClauses(catalog, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation",  null , "id.mutationPosition", "is at position", false);
	        
	        

	        ///////////////////////////////////////
	        // amino acid insertion
	        catalog.addBaseClause("net.sf.regadb.db.AaInsertion");
	        catalog.addStringClauses(catalog, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", "aaInsertionId", "aaInsertion", "has amino acid insertion", false);
	        catalog.addStringClauses(catalog, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", "aaInsertionId", "ntInsertionCodon", "has nucleotide insertion", false);
	        catalog.addNumberClauses(catalog, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", "id" ,"id.insertionPosition", "is at position", false);
	        catalog.addNumberClauses(catalog, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", "id" ,"id.insertionOrder", "has insertion order", false);
	        

	        
	        ///////////////////////////////////////
	        // custom Attributes
	        try {
	        	QueryResult result = DatabaseManager.getInstance().executeQuery("from net.sf.regadb.db.Attribute");
	        	for (int i = 0 ; i < result.size() ; i++) {
	        		Attribute a = (Attribute) result.get(i, 0);
//	    	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.PatientImpl", new String[] {"genericId"}, "net.sf.regadb.db.DrugGeneric",  "has " + a.getName(), false, true);
	        		
//	    	   		select distinct nv.value
//	    	   		from AttributeNominalValue nv
//	    	   		where nv.attribute.attributeIi = ?	    	   		
	    	   		
	        	}
	        }
	        catch(SQLException e) {
	        	
	        }
	        
	        // link net.sf.regadb.db.PatientImpl - custom Attribute
//	        String[][] assocListPatienttoAttribute = {{"net.sf.regadb.db.PatientImpl", null, "patientIi"}, {"net.sf.regadb.db.PatientAttributeValue", "patientIi",null}};
//	        catalog.addGetRemoteAssociationClause(assocListPatienttoAttribute, "has the Attribute");

	        // link custom Attribute - Attribute name
//	        catalog.addMandatoryValuesToClause(
//	        		catalog.addCodedPropertyCheckClause("net.sf.regadb.db.PatientAttributeValue", "attributeIi", "net.sf.regadb.db.Attribute", "attributeIi", "name", "has the name", true),
//	        		new String[] {"Attribute"},
//	        		new String[] {"name"});
	        		
	        // link custom Attribute - nominal value
//	        catalog.addMandatoryValuesToClause(
//	        		catalog.addCodedPropertyCheckClause("net.sf.regadb.db.PatientAttributeValue", "nominalValueIi", "net.sf.regadb.db.AttributeNominalValue", "nominalValueIi", "value", "has the nominal value", true),
//	        		new String[] {"AttributeNominalValue"},
//	        		new String[] {"value"});
	        
	        
	        ///////////////////////////////////////
	        // generic drugs
	        catalog.addBaseClause("net.sf.regadb.db.DrugGeneric");
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "genericId", "has id", false, false);
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "genericName", "has name", false, true);
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "atcCode", "has atc code", false, true);
	   		catalog.addNumberClauses(catalog, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "resistanceTableOrder", "has resistance table order", true);

	        // link generic drug - drug class
	        catalog.addGetAssociationClauses("net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugGeneric", "drugClass", "net.sf.regadb.db.DrugClass", null, "has a drug",  "belongs to the drug class");
	   		
	        
	        ///////////////////////////////////////
	        // commercial drug
	        catalog.addBaseClause("net.sf.regadb.db.DrugCommercial");
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.DrugCommercial", null, "net.sf.regadb.db.DrugCommercial", null, "net.sf.regadb.db.DrugCommercial", null, "name", "has name", false, true);
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.DrugCommercial", null, "net.sf.regadb.db.DrugCommercial", null, "net.sf.regadb.db.DrugCommercial", null, "atcCode", "has atc code", false, true);
	        
	        // link comercial - generic
	   		catalog.addGetCollectionAssociation("net.sf.regadb.db.DrugGeneric", "net.sf.regadb.db.DrugCommercial", "drugGenerics", "has a commercial component");
	   		catalog.addGetCollectionAssociation("net.sf.regadb.db.DrugCommercial", "net.sf.regadb.db.DrugGeneric", "drugCommercials", "is used in the generic drug");
	   		
	   		
	        ///////////////////////////////////////
	        // drug class
	        catalog.addBaseClause("net.sf.regadb.db.DrugClass");
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "className", "has name", false, true);
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "classId", "has id", false);
	   		catalog.addNumberClauses(catalog, "net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "resistanceTableOrder", "has resistance table order", false);
	   		
	   		
	   		
	        ///////////////////////////////////////
	        // test result
	        catalog.addBaseClause("net.sf.regadb.db.TestResult");
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.TestResult", null, "net.sf.regadb.db.TestResult", null, "net.sf.regadb.db.TestResult", null, "value", "has value", false);
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.TestResult", null, "net.sf.regadb.db.TestResult", null, "net.sf.regadb.db.TestResult", null, "sampleId", "has sample id", false);
	   		catalog.addDateClauses(catalog, "net.sf.regadb.db.TestResult", null, "net.sf.regadb.db.TestResult", null, "net.sf.regadb.db.TestResult", null, "testDate", "comes from a test on");

	   		
	        // link test result -  patients
	        catalog.addGetAssociationClauses("net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.TestResult", "patient", "net.sf.regadb.db.PatientImpl", null, "has a test result",  "comes from a test on patient");
	   		
	        // link test result -  generic drug
	        catalog.addGetAssociationClauses("net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.TestResult", "drugGeneric", "net.sf.regadb.db.DrugGeneric", null, "has a test result",  "comes from a test on generic drug");

	        // link test result -  viral isolate
	        catalog.addGetAssociationClauses("net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.TestResult", "viralIsolate", "net.sf.regadb.db.ViralIsolate", null, "has a test result",  "comes from a test on viral isolate");

	        // link test result -  nucleotide sequence
	        catalog.addGetAssociationClauses("net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.TestResult", "ntSequence", "net.sf.regadb.db.NtSequence", null, "has a test result",  "comes from a test on nucleotide sequence");
	        
	        
	        ///////////////////////////////////////
	        // test
	        catalog.addBaseClause("net.sf.regadb.db.Test");
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.Test", null, "net.sf.regadb.db.Test", null, "net.sf.regadb.db.Test", null, "description", "has name", false, true);
	        
	        // link test - test type
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.Test", "testType", "net.sf.regadb.db.TestType", null, "net.sf.regadb.db.TestType", null, "description", "is a test of type", false, true);

	   		// link test - test result
	        catalog.addGetAssociationClauses("net.sf.regadb.db.Test", null, "net.sf.regadb.db.TestResult", "test", "net.sf.regadb.db.Test", null, "has a test result",  "comes from the test");
	   		
	        // link test - test object
	   		catalog.addStringClauses(catalog, "net.sf.regadb.db.Test", "testType.testObject", "net.sf.regadb.db.TestObject", null, "net.sf.regadb.db.TestObject", null, "description", "is a", false, true);

	        
	        
	        /*      
	        catalog.addConvertMicrogramsToMillimolarityClause();
	        catalog.addRealValueWithRelationConstraintClause("Result", "REAL_VALUE", "RELATION", true);
	        catalog.addRealValueWithRelationConstraintClause("Result", "REAL_VALUE", "RELATION", false);
	        catalog.addRealValueWithRelationConstraintClause("Calc_Result", "REAL_VALUE", "RELATION", true);
	        catalog.addRealValueWithRelationConstraintClause("Calc_Result", "REAL_VALUE", "RELATION", false);
	       */ 
	    }
	    
	    public void addNumberClauses(HibernateAWCPrototypeCatalog catalog, String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean dropdown) {
	    	boolean show = foreignTableName != tableName;
	        catalog.addGetPropertyClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description);
	    	if (dropdown) {
	            catalog.addMandatoryValuesToClause(
	            		catalog.addPropertyCheckClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description + " of", show, false),
	            		new String[] {foreignTableName},
	            		new String[] {foreignTableProperty});
	    	}
	    	else {
	    		catalog.addPropertyCheckClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description + " of", show, false);
	    	}
	    	catalog.addPropertyValueClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description + " above", show, false);
	    	catalog.addPropertyValueClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description + " below", show, true);
	        
	    }
	    
	    private AtomicWhereClause addPropertyValueClause(String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean show, boolean below) {
	    	String foreignTablePropertySimple = (foreignTableProperty.indexOf('.') >= 0 ? foreignTableProperty.substring(foreignTableProperty.lastIndexOf('.')+1) : foreignTableProperty );
	    	Properties p = getDataTypeDependantProperties(foreignTableName, foreignTableProperty);   	
	        if (p != null) {
	            String dataTypeString = (String) p.get("dataTypeString");
	            String typeString = (String) p.get("typeString");
	            Constant constant = (Constant) p.get("constant");
	            
	            if (isNumericType(dataTypeString)) {
		            AtomicWhereClause aClause = new AtomicWhereClause();
		            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	                VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
		            
		            description	= description == null ? "has a " + foreignTablePropertySimple : description;
	                InputVariable ivar = new InputVariable(new VariableType(tableName));
                	InputVariable ivar2 = foreignTableName.equals(tableName) ? ivar : new InputVariable(new VariableType(foreignTableName));
	                FromVariable newFromVar = new FromVariable(foreignTableName);
                	OutputVariable ovar = new OutputVariable(new VariableType(typeString), getGoodVariableName(foreignTableName + "." + foreignTableProperty));
	    	        ovar.setUniqueName(ovar.getFormalName());
                	if (foreignTableName.equals(tableName)) {
    	    	        ovar.getExpression().addInputVariable(ivar2);
                	}
                	else {
    	    	        ovar.getExpression().addFromVariable(newFromVar);
                	}
	    	        
	    	        ovar.getExpression().addFixedString(new FixedString("." + foreignTableProperty));
	                
	                aVisList.addFixedString(new FixedString("The " + getTable(tableName).getSingularName()));
	                aVisList.addInputVariable(ivar);
	                aVisList.addFixedString(new FixedString(description));
	                if (show) aVisList.addOutputVariable(ovar);
	                aVisList.addConstant(constant);
		                
	                if (!foreignTableName.equals(tableName)) {
		                aComposer.addFixedString(new FixedString("("));
		                aComposer.addInputVariable(ivar);
		                aComposer.addFixedString(new FixedString("." + (tableRelations != null ? tableRelations + "." : "") + idTableKey + " = "));
		                aComposer.addFromVariable(newFromVar);
		                aComposer.addFixedString(new FixedString("." + (foreignTableRelations != null ? foreignTableRelations + ".":"") + idTableKey));
		                aComposer.addFixedString(new FixedString(") AND\n\t"));
	                }
	                aComposer.addFixedString(new FixedString("("));
                	if (foreignTableName.equals(tableName)) {
    	                aComposer.addInputVariable(ivar2);
                	}
                	else {
    	                aComposer.addFromVariable(newFromVar);
                	}
                    aComposer.addFixedString(new FixedString("." + foreignTableProperty));
    	            aComposer.addFixedString(new FixedString(below ? " < " : " > "));
	                aComposer.addConstant(constant);
	                aComposer.addFixedString(new FixedString(")"));
		            
		            addAtomicWhereClause(aClause);
		            return aClause;
	            }
	            else {
	                System.err.println("Incompatible datatype, number expected: " + foreignTableName + "." + foreignTableProperty);
	                return null;
	            }
	        } else {
	            return null;
	        }
		}

		public void addDateClauses (HibernateAWCPrototypeCatalog catalog, String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String foreignTableProperty, String description) {
	        catalog.addGetPropertyClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description + " date");
	        catalog.addPropertyTimeIntervalClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description + " date", false);
	        catalog.addPropertyCheckClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description + " date", false, false);
	    }

	    public void addStringClauses(HibernateAWCPrototypeCatalog catalog, String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean caseSensitive, boolean dropdown) {
	    	boolean show = foreignTableName != tableName || foreignTableProperty.indexOf('.') >= 0;
	    	
	    	if (dropdown) {
	            catalog.addMandatoryValuesToClause(
	            		catalog.addPropertyCheckClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive),
	            		new String[] {foreignTableName},
	            		new String[] {foreignTableProperty});
	    	}
	    	else {
	    		catalog.addPropertyCheckClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive);
				catalog.addPropertyLikeClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive);
		        catalog.addPropertyStartsLikeClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive);
		        catalog.addPropertyEndsLikeClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive);
	    	}
	    }
	    
	    public void addStringClauses(HibernateAWCPrototypeCatalog catalog, String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean caseSensitive) {
	    	addStringClauses(catalog, tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description, caseSensitive, false);
	    }
	    
	    ///////////////////////////////////////
	    // associations
	    
	    private Map<String, String> typeNameToGoodVariableName = new HashMap<String, String>();
	    private Map<String, String> tableNameToDbName = new HashMap<String, String>();
	    private Map<String, String> tableNameToGoodName = new HashMap<String, String>();
	    private List<AtomicWhereClause> atomicWhereClauses = new ArrayList<AtomicWhereClause>();
	    
	    ///////////////////////////////////////
	    // access methods for associations
	    
	    public void addAtomicWhereClause(AtomicWhereClause atomicWhereClause) {
	        this.atomicWhereClauses.add(atomicWhereClause);
	    }
	    
	    private void addGoodVariableName(String typeName, String varName) {
	        typeNameToGoodVariableName.put(typeName.toLowerCase(), varName);
	    }
	    
	    private void addGoodTableName(String tableName, String name) {
	    	tableNameToGoodName.put(tableName.toLowerCase(), name);
	    }
	    
	    private void addGoodDbName(String tableName, String name) {
	    	tableNameToDbName.put(tableName.toLowerCase(), name);
	    }

	    
	    public String getGoodVariableName(String tableName) {
	        String varName = (String)typeNameToGoodVariableName.get(tableName.toLowerCase());
	        if (varName == null) {
	            return tableName.substring(0, 1);
	        } else {
	            return varName;
	        }
	    }
	    
	    public String getGoodTableName(String tableName) {
	        String varName = (String)tableNameToGoodName.get(tableName.toLowerCase());
	        if (varName == null) {
	            return tableName.substring(tableName.lastIndexOf('.')+1);
	        } else {
	            return varName;
	        }
	    }

	    public String getGoodDbName(String tableName) {
	        String varName = (String)tableNameToDbName.get(tableName.toLowerCase());
	        if (varName == null) {
	            return tableName.substring(tableName.lastIndexOf('.')+1);
	        } else {
	            return varName;
	        }
	    }
	    
	    
	    /**
	     * returns true if a table with the given name exists in the database
	     * @param tableName the name of a table
	     * @return
	     */
	    private boolean tableExists(String tableName) {
	        DatabaseManager manager = DatabaseManager.getInstance();
	        if (manager != null) {
	        	return manager.getTableNames().contains(tableName);
	        }
	        else {
	        	return false;
	        }
	    }
	    
	    /**
	     * returns the data type string from the given property of the given table. Returns null if the table
	     * or property is not found
	     * @param tableName
	     * @param propertyName
	     * @return
	     */
	    private String getDataTypeString(String tableName, String propertyName) {
	        DatabaseManager manager = DatabaseManager.getInstance();
	        String typeString = null;
	        if (manager != null) {
	        	typeString = manager.getColumnType(tableName, propertyName);
	        	if (typeString == null) {
	        		
	        	}
	        }
	        else {
	            System.err.println("Unknown column " + propertyName + " for " + tableName);
	        }
	        return typeString;
	    }
	    
	    private OutputVariable getOutputVariable(String typeString, String propertyName, FromVariable fromVar) {
	        OutputVariable ovar = new OutputVariable(new VariableType(typeString), getGoodVariableName(typeString));
	        ovar.setUniqueName(ovar.getFormalName());
	        ovar.getExpression().addFromVariable(fromVar);
	        return ovar;
	    }
	    
	    private boolean isStringType(String dataTypeString) {
	    	return isStringType(Integer.parseInt(dataTypeString));
	    }
	    
	    private boolean isStringType(int dataType) {
	    	return dataType == 12;
	    }

	    private boolean isBooleanType(int dataType) {
	    	return dataType == -7;
	    }
	    
	    
	    private boolean isDateType(String dataTypeString) {
	    	return isDateType(Integer.parseInt(dataTypeString));
	    }
	    
	    private boolean isDateType(int dataType) {
	    	return (dataType >= 91) && (dataType <= 93);
	    }
	    
	    private boolean isNumericType(String dataTypeString) {
	    	return isNumericType(Integer.parseInt(dataTypeString));
	    }
	    
	    private boolean isBooleanType(String dataTypeString) {
	    	return isBooleanType(Integer.parseInt(dataTypeString));
	    }
	    
	    private boolean isNumericType(int dataType) {
	    	return (((8 >= dataType) && (dataType >=1)) || dataType == 1111 || dataType == -5);
	    }
	    
	    private Properties getDataTypeDependantProperties(String tableName, String propertyName) {
	    	String dataTypeString = getDataTypeString(tableName, propertyName);
	    	if (dataTypeString != null) {
	    			int dataType = Integer.parseInt(dataTypeString);
		    	
		        String variableType;
		        Constant valueConstant = null;
		        if (isStringType(dataType)) {
		            valueConstant = new StringConstant();
		            variableType = "String";
		        }
		        else if (isDateType(dataType)) {
		            valueConstant = new DateConstant();
		            variableType = "Date";
		        }
		        else if (isNumericType(dataType)) {
		            valueConstant = new DoubleConstant();
		            variableType = "Numeric";
		        }
		        else if (isBooleanType(dataType)) {
		            //TODO check in booleanconstant!!!!!
		            //valueConstant = new BooleanConstant();
		            variableType = "Boolean";
		        }
		        
		        else {
	                System.err.println("Unknown data type found for " + tableName + "." + propertyName + ": " + dataType);
		            return null;
		        }
		        
		    	Properties p = new Properties();
		    	p.put("typeString", variableType);
		    	p.put("constant", valueConstant);
		    	p.put("dataTypeString", dataTypeString);
		    	return p;
	    	}
	    	return null;
	    }
	    
	    private AtomicWhereClause addMandatoryValuesToClause(AtomicWhereClause clause, String[] tables, String[] properties) {
		    if (clause != null) {
		    	int i = 0;
		    	Iterator<Constant> it = clause.getConstants().iterator();
		    	while (it.hasNext() && i < tables.length && i < properties.length) {
		    		Constant constant = it.next();
		    		AtomicWhereClause awc = new AtomicWhereClause();
		    		
		    		FromVariable fromVar = new FromVariable(tables[i]);
		    		String typeString = getDataTypeDependantProperties(tables[i], properties[i]).getProperty("typeString");
	    	        OutputVariable ovar = new OutputVariable(new VariableType(typeString), getGoodVariableName(tables[i] + "." + properties[i]));
	    	        ovar.setUniqueName(ovar.getFormalName());
    	        	ovar.getExpression().addFromVariable(fromVar);
    	        	ovar.getExpression().addFixedString(new FixedString("." + properties[i]));
		    		
		    		awc.addFromVariable(fromVar);
		    		Query query = new Query(awc);
	    	        
		    		Selection sel = new OutputSelection(ovar);
		    		sel.setSelected(true);
		    		query.getSelectList().getSelections().add(sel);

		    		constant.setSuggestedValuesQuery(query);
		    		constant.setSuggestedValuesMandatory(true);
		    		i++;
		    	}
		    }
		    return clause;
		}
	    
	    
	    
	    
	    ///////////////////////////////////////
	    // operations
	    
	    
	    /**
	     * <p>
	     * Returns a collection with all AtomicWhereClause prototypes that are
	     * compatible with the given list of OutputVariables. Compatible means that
	     * for all types of the InputVariables of an AtomicWhereClause prototype,
	     * there is at least one OutputVariable present in the given Collection.
	     * Note that the presence of one OutputVariable may satisfy many
	     * InputVariables.
	     * </p>
	     * <p>
	     *
	     * @param availableOutputVariables the Collection of OutputVariables that
	     * are available to bind InputVariables to.
	     * </p>
	     * <p>
	     * @return a Collection with all AtomicWhereClause prototypes that are
	     * compatible with the given list of OutputVariables
	     * </p>
	     */
	    public Collection<AtomicWhereClause> getAWCPrototypes(Collection availableOutputVariables) {
	    	// your code here
	        Collection<AtomicWhereClause> result = new ArrayList<AtomicWhereClause>();
	        Iterator<AtomicWhereClause> iter = atomicWhereClauses.iterator();
	        while (iter.hasNext()) {
	            AtomicWhereClause clause = iter.next();
	            boolean clauseOk = true;
	            Iterator<InputVariable> inputIter = clause.getInputVariables().iterator();
	            while (inputIter.hasNext()) {
	                InputVariable ivar = inputIter.next();
	                boolean varOk = false;
	                Iterator<OutputVariable> outputIter = availableOutputVariables.iterator();
	                while (outputIter.hasNext()) {
	                    OutputVariable ovar = outputIter.next();
	                    if (ivar.isCompatible(ovar)) {
	                        varOk = true;
	                        break;
	                    } 
	                }
	                if (! varOk) {
	                    clauseOk = false;
	                    break;
	                }
	            }
	            if (clauseOk) {
	            	if (!result.contains(clause)) {
	            		result.add(clause);
	            	}
	            }
	        }
	        return result;
	        
	    } // end getAWCPrototypes
	    
	    /*
	     * JDBC version
	     */

	    /**
	     * add the table with the given name to the list of available clauses
	     * @param tableName
	     * @return
	     */
	    public AtomicWhereClause addBaseClause(String tableName) {
	        if (tableExists(tableName)) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
	            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
	            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();

	            aVisList.addFixedString(new FixedString("There is a " + getTable(tableName).getSingularName()));
	            
	            FromVariable tableFromVariable = new FromVariable(tableName);
	            aClause.addFromVariable(tableFromVariable);
	            aVisList.addOutputVariable(getOutputVariable(tableName, tableName, tableFromVariable));
	            
	            aComposer.addFixedString(new FixedString("1=1"));
	            addAtomicWhereClause(aClause);
	            return aClause;
	        } else {
	            System.err.println("No table " + tableName + " found.");
	            return null;
	        }
	    }
	    
	    private AtomicWhereClause addMandatoryValuesToClause(AtomicWhereClause clause, String joinTable, String joinPath, String nominalValTable, FromVariable fromVar, Constant nominalName) {
	    	Constant constant = clause.getConstants().iterator().next();
    		AtomicWhereClause awc = new AtomicWhereClause();
            WhereClauseComposer aComposer = awc.getWhereClauseComposer();
    		Query query = new Query(awc);
    		
    		FromVariable fromVarJoin = new FromVariable(joinTable);
    		FromVariable fromVarNominal = new FromVariable(nominalValTable);
    		String typeString = getDataTypeDependantProperties(nominalValTable, "value").getProperty("typeString");
    		OutputVariable ovar = new OutputVariable(new VariableType(typeString), getGoodVariableName(nominalValTable + "." + "value"));
	        ovar.setUniqueName(ovar.getFormalName());
        	ovar.getExpression().addFromVariable(fromVarNominal);
        	ovar.getExpression().addFixedString(new FixedString(".value"));
    		
    		Selection sel = new OutputSelection(ovar);
    		sel.setSelected(true);
    		query.getSelectList().getSelections().add(sel);

    		aComposer.addFromVariable(fromVar);
    		aComposer.addFixedString(new FixedString(" = "));
    		aComposer.addFromVariable(fromVarJoin);
    		aComposer.addFixedString(new FixedString("." + joinPath + " AND\n\t"));
    		aComposer.addFromVariable(fromVar);
    		aComposer.addFixedString(new FixedString(" = "));
    		aComposer.addFromVariable(fromVarNominal);
    		aComposer.addFixedString(new FixedString("." + joinPath + " AND\n\t"));
    		aComposer.addFromVariable(fromVar);
    		aComposer.addFixedString(new FixedString(".name = "));
    		aComposer.addConstant(nominalName);
        	
    		constant.setSuggestedValuesQuery(query);
    		constant.setSuggestedValuesMandatory(true);
	    	
	    	constant.setSuggestedValuesQuery(query);
	    	constant.setSuggestedValuesMandatory(false);
	    	return clause;
	    }
	    
//	    private AtomicWhereClause add
	    
	    public AtomicWhereClause addBaseClauseWithConstraint(String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean caseSensitive) {
	    	Properties p = getDataTypeDependantProperties(foreignTableName, foreignTableProperty);   	
	        if (p != null) {
                AtomicWhereClause aClause = new AtomicWhereClause();
                VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
                WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	            Constant constant = (Constant) p.get("constant");
	            String dataTypeString = (String) p.get("dataTypeString");
	            String typeString = (String) p.get("typeString");
	            
                InputVariable ivar = new InputVariable(new VariableType(tableName));
            	InputVariable ivar2 = foreignTableName.equals(tableName) ? ivar : new InputVariable(new VariableType(foreignTableName));
                FromVariable fromVar = new FromVariable(tableName);
                FromVariable newFromVar = new FromVariable(foreignTableName);
    	        OutputVariable ovar = new OutputVariable(new VariableType(tableName), getGoodVariableName(tableName));
    	        ovar.setUniqueName(ovar.getFormalName());
    	        ovar.getExpression().addFromVariable(fromVar);
                
                aVisList.addFixedString(new FixedString("there is a " + getTable(tableName).getSingularName()));
                aVisList.addOutputVariable(ovar);
                aVisList.addFixedString(new FixedString(description));
                aVisList.addConstant(constant);
                
	            aClause.addFromVariable(fromVar);

                
                if(!foreignTableName.equals(tableName)) {
	                aComposer.addFixedString(new FixedString("("));
	                aComposer.addOutputVariable(ovar);
	                aComposer.addFixedString(new FixedString((tableRelations != null ?"." +  tableRelations: "") + (idTableKey != null ?"." + idTableKey:"") + " = "));
	                aComposer.addFromVariable(newFromVar);
	                aComposer.addFixedString(new FixedString((foreignTableRelations != null ? "." + foreignTableRelations:"") + (idTableKey != null ?"." + idTableKey:"")));
	                aComposer.addFixedString(new FixedString(") AND\n\t"));
                }
                
                aComposer.addFixedString(new FixedString("("));
                if (!isStringType(dataTypeString) || caseSensitive) {
                	if (foreignTableName.equals(tableName)) {
                		aComposer.addOutputVariable(ovar);
                	}
                	else {
                		aComposer.addFromVariable(newFromVar);
                	}
                    aComposer.addFixedString(new FixedString("." + foreignTableProperty));
	                aComposer.addFixedString(new FixedString(" = "));
	                aComposer.addConstant(constant);
	            }
	            else {
	                aComposer.addFixedString(new FixedString("UPPER("));
                	if (foreignTableName.equals(tableName)) {
                		aComposer.addOutputVariable(ovar);
                	}
                	else {
                		aComposer.addFromVariable(newFromVar);
                	}
                    aComposer.addFixedString(new FixedString("." + foreignTableProperty));
	                aComposer.addFixedString(new FixedString(") = UPPER("));
	                aComposer.addConstant(constant);
	                aComposer.addFixedString(new FixedString(")"));
	            }
                aComposer.addFixedString(new FixedString(")"));
	        
                
                
                addAtomicWhereClause(aClause);
                return aClause;
	        } else {
	            return null;
	        }
	    }
	    
	    /**
	     * allow users to search for an identical property match
	     * @param tableName table we start in
	     * @param propertyName list of properties to follow 
	     * @param resultTable name of table of the lat property in the list
	     * @param description
	     * @param show true if the last property should be selectable
	     * @param caseSensitive is check case sensitive
	     * @return
	     */
	    public AtomicWhereClause addPropertyCheckClause(String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean show, boolean caseSensitive) {
	    	String foreignTablePropertySimple = (foreignTableProperty.indexOf('.') >= 0 ? foreignTableProperty.substring(foreignTableProperty.lastIndexOf('.')+1) : foreignTableProperty );
	    	Properties p = getDataTypeDependantProperties(foreignTableName, foreignTableProperty);   	
	        if (p != null) {
                AtomicWhereClause aClause = new AtomicWhereClause();
                VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
                WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	            Constant constant = (Constant) p.get("constant");
	            String dataTypeString = (String) p.get("dataTypeString");
	            String typeString = (String) p.get("typeString");
                
	            description	= description == null ? "has a " + foreignTablePropertySimple : description;
                InputVariable ivar = new InputVariable(new VariableType(tableName));
            	InputVariable ivar2 = foreignTableName.equals(tableName) ? ivar : new InputVariable(new VariableType(foreignTableName));
                FromVariable newFromVar = new FromVariable(foreignTableName);
    	        OutputVariable ovar = new OutputVariable(new VariableType(typeString), getGoodVariableName(foreignTableName + "." + foreignTableProperty));
    	        ovar.setUniqueName(ovar.getFormalName());
    	        if (!foreignTableName.equals(tableName)) {
    	        	ovar.getExpression().addFromVariable(newFromVar);
    	        }
    	        else {
    	        	ovar.getExpression().addInputVariable(ivar2);
    	        }
                
                aVisList.addFixedString(new FixedString("The " + getTable(tableName).getSingularName()));
                aVisList.addInputVariable(ivar);
                aVisList.addFixedString(new FixedString(description));
                if (show) aVisList.addOutputVariable(ovar);
                aVisList.addConstant(constant);
                
                if(!foreignTableName.equals(tableName)) {
	                aComposer.addFixedString(new FixedString("("));
	                aComposer.addInputVariable(ivar);
	                aComposer.addFixedString(new FixedString((tableRelations != null ?"." +  tableRelations: "") + (idTableKey != null ?"." + idTableKey:"") + " = "));
	                aComposer.addFromVariable(newFromVar);
	                aComposer.addFixedString(new FixedString((foreignTableRelations != null ? "." + foreignTableRelations:"") + (idTableKey != null ?"." + idTableKey:"")));
	                aComposer.addFixedString(new FixedString(") AND\n\t"));
                }
                aComposer.addFixedString(new FixedString("("));
                if (!isStringType(dataTypeString) || caseSensitive) {
                	if (foreignTableName.equals(tableName)) {
                		aComposer.addInputVariable(ivar2);
                	}
                	else {
                		aComposer.addFromVariable(newFromVar);
                	}
                    aComposer.addFixedString(new FixedString("." + foreignTableProperty));
	                aComposer.addFixedString(new FixedString(" = "));
	                aComposer.addConstant(constant);
	            }
	            else {
	                aComposer.addFixedString(new FixedString("UPPER("));
                	if (foreignTableName.equals(tableName)) {
                		aComposer.addInputVariable(ivar2);
                	}
                	else {
                		aComposer.addFromVariable(newFromVar);
                	}
                    aComposer.addFixedString(new FixedString("." + foreignTableProperty));
	                aComposer.addFixedString(new FixedString(") = UPPER("));
	                aComposer.addConstant(constant);
	                aComposer.addFixedString(new FixedString(")"));
	            }
                aComposer.addFixedString(new FixedString(")"));
	                
                addAtomicWhereClause(aClause);
                return aClause;
	        } else {
	            return null;
	        }
	    }
	    
	    public AtomicWhereClause addPropertyLikeClause(String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean show, boolean caseSensitive, Constant likeConstant, String constantDescription) {
	    	String foreignTablePropertySimple = (foreignTableProperty.indexOf('.') >= 0 ? foreignTableProperty.substring(foreignTableProperty.lastIndexOf('.')+1) : foreignTableProperty );
	    	Properties p = getDataTypeDependantProperties(foreignTableName, foreignTableProperty);   	
	    	if (p != null) {
	            String dataTypeString = (String) p.get("dataTypeString");
	            String typeString = (String) p.get("typeString");
	            Constant constant = likeConstant;
	            
	            if (isStringType(dataTypeString)) {
		            AtomicWhereClause aClause = new AtomicWhereClause();
		            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	                VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
		            
		            description	= description == null ? "has a " + foreignTablePropertySimple : description;
	                InputVariable ivar = new InputVariable(new VariableType(tableName));
                	InputVariable ivar2 = foreignTableName.equals(tableName) ? ivar : new InputVariable(new VariableType(foreignTableName));
	                FromVariable newFromVar = new FromVariable(foreignTableName);
	    	        OutputVariable ovar = new OutputVariable(new VariableType(typeString), getGoodVariableName(foreignTableName + "." + foreignTableProperty));
	    	        ovar.setUniqueName(ovar.getFormalName());
	    	        if (!foreignTableName.equals(tableName)) {
		    	        ovar.getExpression().addFromVariable(newFromVar);
	    	        }
	    	        else {
	    	        	ovar.getExpression().addInputVariable(ivar2);
	    	        }
	    	        ovar.getExpression().addFixedString(new FixedString("." + foreignTableProperty));
	                
	                aVisList.addFixedString(new FixedString("The " + getTable(tableName).getSingularName()));
	                aVisList.addInputVariable(ivar);
	                aVisList.addFixedString(new FixedString(description));
	                if (show) aVisList.addOutputVariable(ovar);
	                aVisList.addFixedString(new FixedString(constantDescription));
	                aVisList.addConstant(constant);
		                
	                if (!foreignTableName.equals(tableName)) {
		                aComposer.addFixedString(new FixedString("("));
		                aComposer.addInputVariable(ivar);
		                aComposer.addFixedString(new FixedString((tableRelations != null ? "." + tableRelations: "") + (idTableKey != null ?"." + idTableKey:"") + " = "));
		                aComposer.addFromVariable(newFromVar);
		                aComposer.addFixedString(new FixedString((foreignTableRelations != null ? "." + foreignTableRelations:"") + (idTableKey != null ?"." + idTableKey:"")));
		                aComposer.addFixedString(new FixedString(") AND\n\t ("));
	                }
		            if (caseSensitive) {
	                	if (foreignTableName.equals(tableName)) {
	                		aComposer.addInputVariable(ivar2);
	                	}
	                	else {
	                		aComposer.addFromVariable(newFromVar);
	                	}
	                    aComposer.addFixedString(new FixedString("." + foreignTableProperty));
		                aComposer.addFixedString(new FixedString(" LIKE "));
		                aComposer.addConstant(constant);
		            }
		            else {
		                aComposer.addFixedString(new FixedString("UPPER("));
	                	if (foreignTableName.equals(tableName)) {
	                		aComposer.addInputVariable(ivar2);
	                	}
	                	else {
	                		aComposer.addFromVariable(newFromVar);
	                	}
	                    aComposer.addFixedString(new FixedString("." + foreignTableProperty));
		                aComposer.addFixedString(new FixedString(") LIKE UPPER("));
		                aComposer.addConstant(constant);
		                aComposer.addFixedString(new FixedString(")"));
		            }
		            if (!foreignTableName.equals(tableName)) {
		            	aComposer.addFixedString(new FixedString(")"));
		            }
		            addAtomicWhereClause(aClause);
		            return aClause;
	            }
	            else {
	                System.err.println("Incompatible datatype, string expected: " + foreignTableName + "." + foreignTableProperty);
	                return null;
	            }
	        } else {
	            return null;
	        }
	    }
	    
	    /**
	     * allows users to search for a property containing a specified string
	     * @param tableName
	     * @param propertyName
	     * @param description
	     * @param show
	     * @param caseSensitive
	     * @return
	     */
	    public AtomicWhereClause addPropertyLikeClause(String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean show, boolean caseSensitive) {
	    	return addPropertyLikeClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive, new SubstringConstant(), "containing");
	    }
	    
	    /**
	     * allows users to search for a property ending on a specified string
	     * @param tableName
	     * @param propertyName
	     * @param description
	     * @param show
	     * @param caseSensitive
	     * @return
	     */
	    public AtomicWhereClause addPropertyEndsLikeClause(String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean show, boolean caseSensitive) {
	    	return addPropertyLikeClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive, new EndstringConstant(), "that ends with");
	    }
	    
	    /**
	     * allows users to search for a property ending with a specified string
	     * @param tableName
	     * @param propertyName
	     * @param description
	     * @param show
	     * @param caseSensitive
	     * @return
	     */
	    public AtomicWhereClause addPropertyStartsLikeClause(String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean show, boolean caseSensitive) {
	    	return addPropertyLikeClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive, new StartstringConstant(), "that starts with");
	    }
	    
	    
	    /**
	     * gets the property from the given table as a variable
	     * @param tableName
	     * @param propertyName
	     * @param description
	     * @return
	     */
	    public AtomicWhereClause addBooleanPropertyClause(String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String foreignTableProperty, String description) {
	    	String foreignTablePropertySimple = (foreignTableProperty.indexOf('.') >= 0 ? foreignTableProperty.substring(foreignTableProperty.lastIndexOf('.')+1) : foreignTableProperty );
	    	Properties p = getDataTypeDependantProperties(foreignTableName, foreignTableProperty);   	
	    	if (p != null) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
                VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
                WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	            
	            String dataTypeString = (String) p.get("dataTypeString");
	            String typeString = (String) p.get("typeString");
	            if (isBooleanType(dataTypeString)) {
		            description			= description == null ? "has a " + foreignTablePropertySimple : description;
		            InputVariable ivar  = new InputVariable(new VariableType(tableName));
	                FromVariable newFromVar = new FromVariable(foreignTableName);
	    	        OutputVariable ovar = new OutputVariable(new VariableType(typeString), getGoodVariableName(foreignTableName + "." + foreignTableProperty));
	    	        ovar.setUniqueName(ovar.getFormalName());
	    	        if (!foreignTableName.equals(tableName)) {
	    	        	ovar.getExpression().addFromVariable(newFromVar);
	    	        }
	    	        else {
	    	        	ovar.getExpression().addInputVariable(ivar);
	    	        }
			        ovar.getExpression().addFixedString(new FixedString("." + foreignTableProperty));
	
	                aVisList.addFixedString(new FixedString("The " + getTable(tableName).getSingularName()));
	                aVisList.addInputVariable(ivar);
	                aVisList.addFixedString(new FixedString(description));
	                aVisList.addOutputVariable(ovar);
		                
	                if (!foreignTableName.equals(tableName)) {
		                aComposer.addFixedString(new FixedString("("));
		                aComposer.addInputVariable(ivar);
		                aComposer.addFixedString(new FixedString("." + (tableRelations != null ? tableRelations + "." : "") + idTableKey + " = "));
		                aComposer.addFromVariable(newFromVar);
		                aComposer.addFixedString(new FixedString("." + (foreignTableRelations != null ? foreignTableRelations + ".":"") + idTableKey));
		                aComposer.addFixedString(new FixedString(")"));
	                }
	                else {
		    	        if (!foreignTableName.equals(tableName)) {
		    	        	aComposer.addFromVariable(newFromVar);
		    	        }
		    	        else {
		    	        	aComposer.addInputVariable(ivar);
		    	        }
	                    aComposer.addFixedString(new FixedString("." + foreignTableProperty + " = true"));
	                }
		            addAtomicWhereClause(aClause);
		            return aClause;
	            }
	            else {
	                System.err.println("Incompatible datatype, bolean expected: " + foreignTableName + "." + foreignTableProperty);
	                return null;
	            }
		            
	    	}
	    	return null;
	    }
	    
	    
	    /**
	     * gets the property from the given table as a variable
	     * @param tableName
	     * @param propertyName
	     * @param description
	     * @return
	     */
	    public AtomicWhereClause addGetPropertyClause(String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String foreignTableProperty, String description) {
	    	String foreignTablePropertySimple = (foreignTableProperty.indexOf('.') >= 0 ? foreignTableProperty.substring(foreignTableProperty.lastIndexOf('.')+1) : foreignTableProperty );
	    	Properties p = getDataTypeDependantProperties(foreignTableName, foreignTableProperty);   	
	    	if (p != null) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
                VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
                WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	            
	            String typeString = (String) p.get("typeString");

	            description			= description == null ? "has a " + foreignTablePropertySimple : description;
	            InputVariable ivar  = new InputVariable(new VariableType(tableName));
                FromVariable newFromVar = new FromVariable(foreignTableName);
    	        OutputVariable ovar = new OutputVariable(new VariableType(typeString), getGoodVariableName(foreignTableName + "." + foreignTableProperty));
    	        ovar.setUniqueName(ovar.getFormalName());
    	        if (!foreignTableName.equals(tableName)) {
    	        	ovar.getExpression().addFromVariable(newFromVar);
    	        }
    	        else {
    	        	ovar.getExpression().addInputVariable(ivar);
    	        }
		        ovar.getExpression().addFixedString(new FixedString("." + foreignTableProperty));

                aVisList.addFixedString(new FixedString("The " + getTable(tableName).getSingularName()));
                aVisList.addInputVariable(ivar);
                aVisList.addFixedString(new FixedString(description));
                aVisList.addOutputVariable(ovar);
	                
                if (!foreignTableName.equals(tableName)) {
	                aComposer.addFixedString(new FixedString("("));
	                aComposer.addInputVariable(ivar);
	                aComposer.addFixedString(new FixedString("." + (tableRelations != null ? tableRelations + "." : "") + idTableKey + " = "));
	                aComposer.addFromVariable(newFromVar);
	                aComposer.addFixedString(new FixedString("." + (foreignTableRelations != null ? foreignTableRelations + ".":"") + idTableKey));
	                aComposer.addFixedString(new FixedString(")"));
                }
                else {
                	aComposer.addFixedString(new FixedString("1=1"));
                }
	            addAtomicWhereClause(aClause);
	            return aClause;
	    	}
	    	return null;
	    }


	    
	    public AtomicWhereClause addGetAssociationClauses(String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String description1, String description2) {
	        AtomicWhereClause aClause1 = addGetAssociationClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, description1);
	        AtomicWhereClause aClause2 = addGetReverseAssociationClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, description2);
	        if (aClause1 == null) {
	            return aClause2;
	        } else {
	            return aClause1;
	        }
	    }
	    
	    public AtomicWhereClause addGetAssociationClause(String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey) {
	        return addGetAssociationClause(tableName, tableRelations, foreignTableName, foreignTableRelations, idTableName, idTableKey, null);
	    }
	    
	    public AtomicWhereClause addGetAssociationClause(String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String description) {
	        DatabaseManager manager = DatabaseManager.getInstance();
	        if (manager != null) {
                AtomicWhereClause aClause = new AtomicWhereClause();
                VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
                WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
                
                InputVariable ivar = new InputVariable(new VariableType(tableName));
                description = description == null?"has an associated " + getTable(foreignTableName).getSingularName():description;
                FromVariable newFromVar = new FromVariable(foreignTableName);
                OutputVariable ovar = new OutputVariable(new VariableType(foreignTableName), getGoodVariableName(foreignTableName));
                ovar.setUniqueName(ovar.getFormalName());
                ovar.getExpression().addFromVariable(newFromVar);
                
                aVisList.addFixedString(new FixedString("The " + getTable(tableName).getSingularName()));
                aVisList.addInputVariable(ivar);
                aVisList.addFixedString(new FixedString(description));
                aVisList.addOutputVariable(ovar);
                
                aComposer.addInputVariable(ivar);
                aComposer.addFixedString(new FixedString((tableRelations != null ? "." + tableRelations: "") + (idTableKey!= null ? "." + idTableKey: "") + " = "));
                aComposer.addFromVariable(newFromVar);
                aComposer.addFixedString(new FixedString((foreignTableRelations != null ? "." + foreignTableRelations:"") + (idTableKey!= null ? "." + idTableKey: "") ));
                
                addAtomicWhereClause(aClause);
                return aClause;
	        } else {
	            return null;
	        }
	    }

	    public AtomicWhereClause addGetReverseAssociationClause(String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String description) {
	        DatabaseManager manager = DatabaseManager.getInstance();
	        if (manager != null) {
                AtomicWhereClause aClause = new AtomicWhereClause();
                VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
                WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
                String singularName = getTable(foreignTableName).getSingularName();
                aVisList.addFixedString(new FixedString("The " + singularName));
                InputVariable ivar = new InputVariable(new VariableType(foreignTableName));
                aVisList.addInputVariable(ivar);
                String foreignSingularName = getTable(tableName).getSingularName();
                if (description == null) {
                    aVisList.addFixedString(new FixedString("has an associated " + foreignSingularName));
                } else {
                    aVisList.addFixedString(new FixedString(description));
                }
                FromVariable newFromVar = new FromVariable(tableName);
                OutputVariable ovar = new OutputVariable(new VariableType(tableName), getGoodVariableName(tableName));
                ovar.setUniqueName(ovar.getFormalName());
                aVisList.addOutputVariable(ovar);
                ovar.getExpression().addFromVariable(newFromVar);
                
                aComposer.addFromVariable(newFromVar);
                aComposer.addFixedString(new FixedString((tableRelations != null ? "." + tableRelations: "") + (idTableKey!= null ? "." + idTableKey: "")  + " = "));
                aComposer.addInputVariable(ivar);
                aComposer.addFixedString(new FixedString((foreignTableRelations != null ? "." + foreignTableRelations : "") + (idTableKey!= null ? "." + idTableKey: "") ));
                
                addAtomicWhereClause(aClause);
                return aClause;
	        } else {
	            return null;
	        }
	    }
	    
	    public AtomicWhereClause addGetCollectionAssociation(String tableName, String foreignTableName, String foreignTableProperty, String description) {
	        DatabaseManager manager = DatabaseManager.getInstance();
	        if (manager != null) {
                AtomicWhereClause aClause = new AtomicWhereClause();
                VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
                WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
                
                InputVariable ivar = new InputVariable(new VariableType(tableName));
                description = description == null ? "has an associated " + getTable(foreignTableName).getSingularName() : description;
                FromVariable newFromVar = new FromVariable(foreignTableName);

                aVisList.addFixedString(new FixedString("The " + getTable(tableName).getSingularName()));
                aVisList.addInputVariable(ivar);
                aVisList.addFixedString(new FixedString(description));
                OutputVariable ovar = new OutputVariable(new VariableType(tableName), getGoodVariableName(foreignTableName));
                ovar.setUniqueName(ovar.getFormalName());
                ovar.getExpression().addFromVariable(newFromVar);
                aVisList.addOutputVariable(ovar);
                
                aComposer.addInputVariable(ivar);
                aComposer.addFixedString(new FixedString(" in elements("));
                aComposer.addFromVariable(newFromVar);
                aComposer.addFixedString(new FixedString("." +  foreignTableProperty));
                aComposer.addFixedString(new FixedString(")"));
                
                addAtomicWhereClause(aClause);
                return aClause;
	        } else {
	            return null;
	        }
	    }
	    
	    
	    public AtomicWhereClause addPropertyTimeIntervalClause(String tableName, String tableRelations, String foreignTableName, String foreignTableRelations, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean show) {
	    	String foreignTablePropertySimple = (foreignTableProperty.indexOf('.') >= 0 ? foreignTableProperty.substring(foreignTableProperty.lastIndexOf('.')+1) : foreignTableProperty );
	    	Properties p = getDataTypeDependantProperties(foreignTableName, foreignTableProperty);   	
	        if (p != null) {
	            String dataTypeString = (String) p.get("dataTypeString");
	            String typeString = (String) p.get("typeString");
	            
	            if (isDateType(dataTypeString)) {
		            AtomicWhereClause aClause = new AtomicWhereClause();
		            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	                VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
		            
		            description	= description == null ? "has a " + foreignTablePropertySimple : description;
	                InputVariable ivar = new InputVariable(new VariableType(tableName));
                	InputVariable ivar2 = foreignTableName.equals(tableName) ? ivar : new InputVariable(new VariableType(foreignTableName));
	                FromVariable newFromVar = new FromVariable(foreignTableName);
                	OutputVariable ovar = new OutputVariable(new VariableType(typeString), getGoodVariableName(foreignTableName + "." + foreignTableProperty));
	    	        ovar.setUniqueName(ovar.getFormalName());
	    	        if (!foreignTableName.equals(tableName)) {
	    	        	ovar.getExpression().addFromVariable(newFromVar);
	    	        }
	    	        else {
	    	        	ovar.getExpression().addInputVariable(ivar2);
	    	        }
	    	        ovar.getExpression().addFixedString(new FixedString("." + foreignTableProperty));
	                
	                aVisList.addFixedString(new FixedString("The " + getTable(tableName).getSingularName()));
	                aVisList.addInputVariable(ivar);
	                aVisList.addFixedString(new FixedString(description));
	                if (show) aVisList.addOutputVariable(ovar);
                    aVisList.addFixedString(new FixedString("between"));
                    Constant valueConstant1 = new DateConstant("1900-01-01");
                    aVisList.addConstant(valueConstant1);
                    aVisList.addFixedString(new FixedString("and"));
                    Constant valueConstant2 = new DateConstant();
                    aVisList.addConstant(valueConstant2);
		                
                    if (!foreignTableName.equals(tableName)) {
		                aComposer.addFixedString(new FixedString("("));
		                aComposer.addInputVariable(ivar);
		                aComposer.addFixedString(new FixedString("." + (tableRelations != null ? tableRelations + "." : "") + idTableKey + " = "));
		                aComposer.addFromVariable(newFromVar);
		                aComposer.addFixedString(new FixedString("." + (foreignTableRelations != null ? foreignTableRelations + ".":"") + idTableKey));
		                aComposer.addFixedString(new FixedString(") AND\n\t"));
	                }
	                aComposer.addFixedString(new FixedString("("));
                    if (show) {
                    	aComposer.addOutputVariable(ovar);
                    }
                    else {
                        aComposer.addInputVariable(ivar2);
                    	aComposer.addFixedString(new FixedString("." + foreignTableProperty));
                    }
                    aComposer.addFixedString(new FixedString(" > "));
                    aComposer.addConstant(valueConstant1);
                    aComposer.addFixedString(new FixedString(") AND\n\t ("));
                    if (show) {
                    	aComposer.addOutputVariable(ovar);
                    }
                    else {
                        aComposer.addInputVariable(ivar2);
                    	aComposer.addFixedString(new FixedString("." + foreignTableProperty));
                    }
                    aComposer.addFixedString(new FixedString(" < "));
                    aComposer.addConstant(valueConstant2);
	                aComposer.addFixedString(new FixedString(")"));
		            
		            addAtomicWhereClause(aClause);
		            return aClause;
	            }
	            else {
	                System.err.println("Incompatible datatype, number expected: " + foreignTableName + "." + foreignTableProperty);
	                return null;
	            }
	        } else {
	            return null;
	        }
	    }
	    
	    public AtomicWhereClause addTimeConstantClause(boolean before) {
	        DatabaseManager manager = DatabaseManager.getInstance();
	        if (manager != null) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
	            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
	            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();

	            InputVariable ivar = new InputVariable(new VariableType("Date"));
	            Constant valueConstant = new DateConstant();
	            
	            aVisList.addFixedString(new FixedString("Date"));
	            aVisList.addInputVariable(ivar);
	            aVisList.addFixedString(new FixedString(before ? "is before" : "is after"));
	            aVisList.addConstant(valueConstant);
	            
	            aComposer.addInputVariable(ivar);
	            aComposer.addFixedString(new FixedString(before ? " < " : " > "));
	            aComposer.addConstant(valueConstant);
	            
	            addAtomicWhereClause(aClause);
	            return aClause;
	        } else {
	            return null;
	        }
	    }
	    
	    public AtomicWhereClause addTimeIntervalClause() {
	        DatabaseManager manager = DatabaseManager.getInstance();
	        if (manager != null) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
	            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
	            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	            aVisList.addFixedString(new FixedString("Date"));
	            InputVariable ivar = new InputVariable(new VariableType("Date"));
	            aVisList.addInputVariable(ivar);
	            aVisList.addFixedString(new FixedString("is between"));
	            Constant valueConstant1 = new DateConstant("1900-01-01");
	            aVisList.addConstant(valueConstant1);
	            aVisList.addFixedString(new FixedString("and"));
	            Constant valueConstant2 = new DateConstant();
	            aVisList.addConstant(valueConstant2);
	            
	            aComposer.addFixedString(new FixedString("("));
	            aComposer.addInputVariable(ivar);
	            aComposer.addFixedString(new FixedString(" > "));
	            aComposer.addConstant(valueConstant1);
	            aComposer.addFixedString(new FixedString(") AND\n\t("));
	            aComposer.addInputVariable(ivar);
	            aComposer.addFixedString(new FixedString(" < "));
	            aComposer.addConstant(valueConstant2);
	            aComposer.addFixedString(new FixedString(")"));
	            
	            addAtomicWhereClause(aClause);
	            return aClause;
	        } else {
	            return null;
	        }
	    }
	    
	    public AtomicWhereClause addTimeConstantToVariableClause() {
	        AtomicWhereClause aClause = new AtomicWhereClause();
	        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
	        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	        aVisList.addFixedString(new FixedString("Date"));
	        Constant dateConstant = new DateConstant();
	        OutputVariable ovar = new OutputVariable(new VariableType("Date"), getGoodVariableName("Date"));
	        ovar.setUniqueName(ovar.getFormalName());
	        ovar.getExpression().addConstant(dateConstant);
	        aVisList.addOutputVariable(ovar);
	        aVisList.addFixedString(new FixedString("is"));
	        aVisList.addConstant(dateConstant);
	        
	        aComposer.addFixedString(new FixedString("1=1"));
	        
	        addAtomicWhereClause(aClause);
	        return aClause;
	    }
	    
	    
	    public AtomicWhereClause addTimeCompareClause() {
	        DatabaseManager manager = DatabaseManager.getInstance();
	        if (manager != null) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
	            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
	            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	            aVisList.addFixedString(new FixedString("Date"));
	            InputVariable ivar1 = new InputVariable(new VariableType("Date"));
	            InputVariable ivar2 = new InputVariable(new VariableType("Date"));
	            aVisList.addInputVariable(ivar1);
	            aVisList.addFixedString(new FixedString("is before"));
	            aVisList.addInputVariable(ivar2);
	            aComposer.addInputVariable(ivar1);
	            aComposer.addFixedString(new FixedString(" < "));
	            aComposer.addInputVariable(ivar2);
	            
	            addAtomicWhereClause(aClause);
	            return aClause;
	        } else {
	            return null;
	        }
	    }
	    
	    public AtomicWhereClause addTimeEqualsClause() {
	        DatabaseManager manager = DatabaseManager.getInstance();
	        if (manager != null) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
	            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
	            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();

	            InputVariable ivar1 = new InputVariable(new VariableType("Date"));
	            Constant timeConstant = new DateConstant();

	            aVisList.addFixedString(new FixedString("Date"));
	            aVisList.addInputVariable(ivar1);
	            aVisList.addFixedString(new FixedString(" is "));
	            aVisList.addConstant(timeConstant);
	            
	            aComposer.addInputVariable(ivar1);
	            aComposer.addFixedString(new FixedString(" = "));
	            aComposer.addConstant(timeConstant);
	            
	            addAtomicWhereClause(aClause);
	            return aClause;
	        } else {
	            return null;
	        }
	    }
	    
	    
	    public AtomicWhereClause addTimeCalculationClause(boolean plus) {
	        DatabaseManager manager = DatabaseManager.getInstance();
	        if (manager != null) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
	            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
	            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	            aVisList.addFixedString(new FixedString("Date "));
	            InputVariable ivar = new InputVariable(new VariableType("Date"));
	            
	            OutputVariable ovar = new OutputVariable(new VariableType("Date"), getGoodVariableName("Date"));
	            ovar.setUniqueName(ovar.getFormalName());
	            ovar.getExpression().addInputVariable(ivar);
	            ovar.getExpression().addFixedString(new FixedString(plus ? " + " : " - "));
	            DateConstant timeConstant = new DateConstant();
	            ovar.getExpression().addConstant(timeConstant);
	            
	            aVisList.addOutputVariable(ovar);
	            aVisList.addFixedString(new FixedString("is"));
	            aVisList.addInputVariable(ivar);
	            aVisList.addFixedString(new FixedString(plus ? " + " : " - "));
	            aVisList.addConstant(timeConstant);
	            aVisList.addFixedString(new FixedString("days"));
	            
	            aComposer.addFixedString(new FixedString("1=1"));
	            
	            addAtomicWhereClause(aClause);
	            return aClause;
	        } else {
	            return null;
	        }
	    }
	    
	    public AtomicWhereClause addRealValueConstraintClause(boolean below) {
	        DatabaseManager manager = DatabaseManager.getInstance();
	        if (manager != null) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
	            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
	            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	            aVisList.addFixedString(new FixedString("Value"));
	            InputVariable ivar = new InputVariable(new VariableType("Numeric"));
	            aVisList.addInputVariable(ivar);
	            aVisList.addFixedString(new FixedString(below ? " < " : " > "));
	            Constant valueConstant = new DoubleConstant();
	            aVisList.addConstant(valueConstant);
	            
	            aComposer.addInputVariable(ivar);
	            aComposer.addFixedString(new FixedString(below ? " < " : " > "));
	            aComposer.addConstant(valueConstant);
	            
	            addAtomicWhereClause(aClause);
	            return aClause;
	        } else {
	            return null;
	        }
	    }
	    
	    public AtomicWhereClause addRealValueCompareClause() {
	        DatabaseManager manager = DatabaseManager.getInstance();
	        if (manager != null) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
	            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
	            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	            
	            InputVariable ivar1 = new InputVariable(new VariableType("Numeric"));
	            InputVariable ivar2 = new InputVariable(new VariableType("Numeric"));

	            aVisList.addFixedString(new FixedString("Value"));
	            aVisList.addInputVariable(ivar1);
	            aVisList.addFixedString(new FixedString("is lower than"));
	            aVisList.addInputVariable(ivar2);

	            aComposer.addInputVariable(ivar1);
	            aComposer.addFixedString(new FixedString(" < "));
	            aComposer.addInputVariable(ivar2);
	            
	            addAtomicWhereClause(aClause);
	            return aClause;
	        } else {
	            return null;
	        }
	    }
	    
	    public AtomicWhereClause addRealValueIntervalClause() {
	        DatabaseManager manager = DatabaseManager.getInstance();
	        if (manager != null) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
	            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
	            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	            aVisList.addFixedString(new FixedString("Value"));
	            InputVariable ivar = new InputVariable(new VariableType("Numeric"));
	            aVisList.addInputVariable(ivar);
	            aVisList.addFixedString(new FixedString("is between"));
	            Constant valueConstant1 = new DoubleConstant();
	            aVisList.addConstant(valueConstant1);
	            aVisList.addFixedString(new FixedString("and"));
	            Constant valueConstant2 = new DoubleConstant();
	            aVisList.addConstant(valueConstant2);
	            
	            aComposer.addFixedString(new FixedString("("));
	            aComposer.addInputVariable(ivar);
	            aComposer.addFixedString(new FixedString(" > "));
	            aComposer.addConstant(valueConstant1);
	            aComposer.addFixedString(new FixedString(") AND\n\t("));
	            aComposer.addInputVariable(ivar);
	            aComposer.addFixedString(new FixedString(" < "));
	            aComposer.addConstant(valueConstant2);
	            aComposer.addFixedString(new FixedString(")"));
	            
	            addAtomicWhereClause(aClause);
	            return aClause;
	        } else {
	            return null;
	        }
	    }
	    
	    public AtomicWhereClause addRealConstantToVariableClause() {
	        AtomicWhereClause aClause = new AtomicWhereClause();
	        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
	        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	        aVisList.addFixedString(new FixedString("Value"));
	        Constant dateConstant = new DoubleConstant();
	        OutputVariable ovar = new OutputVariable(new VariableType("Numeric"), getGoodVariableName("Number"));
	        ovar.setUniqueName(ovar.getFormalName());
	        ovar.getExpression().addConstant(dateConstant);
	        aVisList.addOutputVariable(ovar);
	        aVisList.addFixedString(new FixedString("is"));
	        aVisList.addConstant(dateConstant);
	        
	        aComposer.addFixedString(new FixedString("1=1"));
	        
	        addAtomicWhereClause(aClause);
	        return aClause;
	    }
	    
	    public AtomicWhereClause addRealValueEqualsClause() {
	        DatabaseManager manager = DatabaseManager.getInstance();
	        if (manager != null) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
	            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
	            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();

	            InputVariable ivar1 = new InputVariable(new VariableType("Numeric"));
	            Constant numberConstant = new DoubleConstant();

	            aVisList.addFixedString(new FixedString("Value"));
	            aVisList.addInputVariable(ivar1);
	            aVisList.addFixedString(new FixedString(" is "));
	            aVisList.addConstant(numberConstant);
	            
	            aComposer.addInputVariable(ivar1);
	            aComposer.addFixedString(new FixedString(" = "));
	            aComposer.addConstant(numberConstant);
	            
	            addAtomicWhereClause(aClause);
	            return aClause;
	        } else {
	            return null;
	        }
	    }
}
