
/** Java class "AWCPrototypeCatalog.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.util.*;
//import com.pharmadm.custom.rega.chem.search.MoleculeClause;

import sun.management.jmxremote.ConnectorBootstrap.PropertyNames;

/**
 * <p>
 * A catalog containing AtomicWhereClause prototypes ('Prototype' pattern).
 * </p>
 * <p>
 * Typically, a catalog is build from a file describing the different
 * prototypes.
 * </p>
 * <p>
 * The prototypes should not be manipulated directly, but rather be cloned
 * first. The catalog can determine which of its prototype represent
 * AtomicWhereClauses whose clones can be added at a point in a WhereClause
 * composition where a given Collection of OutputVariables is available.
 * </p>
 *
 * Prototype pattern
 */
public class AWCPrototypeCatalog {
    
    private static AWCPrototypeCatalog mainCatalog = null;
    
    public static AWCPrototypeCatalog getInstance() {
        if (mainCatalog == null) {
            initMainCatalog();
        }
        return mainCatalog;
    }

    private static void initMainCatalog() {
        AWCPrototypeCatalog catalog = new AWCPrototypeCatalog();
        catalog.addGoodVariableName("patient", "Patient");
        catalog.addGoodVariableName("therapy", "Therapy");
        catalog.addGoodVariableName("viral_isolate", "ViralIsolate");
        catalog.addGoodVariableName("nt_sequence", "ntSequence");
        catalog.addGoodVariableName("aa_sequence", "aaSequence");
        catalog.addGoodVariableName("aa_mutation", "aaMutation");
        catalog.addGoodVariableName("aa_insertion", "aaInsertion");
        catalog.addGoodVariableName("patient_attribute_value", "Attribute");
        catalog.addGoodVariableName("drug_generic", "GenericDrug");
        catalog.addGoodVariableName("drug_commercial", "CommercialDrug");
        
        catalog.addGoodVariableName("PATIENT_ID", "Id");
        catalog.addGoodVariableName("BIRTH_DATE", "birthDate");
        catalog.addGoodVariableName("DEATH_DATE", "DeathDate");
        catalog.addGoodVariableName("LAST_NAME", "LastName");
        catalog.addGoodVariableName("FIRST_NAME", "FirstName");
        catalog.addGoodVariableName("NAME", "Name");
        catalog.addGoodVariableName("SAMPLE_DATE", "SampleDate");
        catalog.addGoodVariableName("START_DATE", "StartDate");
        catalog.addGoodVariableName("STOP_DATE", "StopDate");
        catalog.addGoodVariableName("SEQUENCE_DATE", "SequenceDate");
        catalog.addGoodVariableName("VALUE", "Value");
        catalog.addGoodVariableName("GENERIC_ID", "Id");
        catalog.addGoodVariableName("GENERIC_NAME", "Name");
        catalog.addGoodVariableName("ATC_CODE", "AtcCode");
        catalog.addGoodVariableName("COMMENT", "Comment");
        
        catalog.addRealValueConstraintClause(true);
        catalog.addRealValueConstraintClause(false);
        catalog.addTimeConstantClause(true);
        catalog.addTimeConstantClause(false);
        catalog.addTimeIntervalClause();
        catalog.addTimeCompareClause();
        catalog.addTimeCalculationClause(true);
        catalog.addTimeCalculationClause(false);
        catalog.addTimeConstantToVariableClause();

        ///////////////////////////////////////
        // patients
        catalog.addBaseClause("patient");
   		catalog.addPropertyCheckClause("patient", "PATIENT_ID", "has id", false, false);
   		catalog.addStringClauses(catalog, "patient", "LAST_NAME", "has last name", false);
   		catalog.addStringClauses(catalog, "patient", "FIRST_NAME", "has first name", false);
        catalog.addDateClauses(catalog, "patient", "BIRTH_DATE", "is born on");
        catalog.addDateClauses(catalog, "patient", "DEATH_DATE", "has died on");

        
        ///////////////////////////////////////
        // therapies
        catalog.addBaseClause("therapy");
        catalog.addDateClauses(catalog, "therapy", "START_DATE", "was started on");
        catalog.addDateClauses(catalog, "therapy", "STOP_DATE", "was stopped on");
        catalog.addStringClauses(catalog, "therapy", "COMMENT", "has a comment", false);

        // link patient - therapy
        catalog.addGetAssociationClause("patient", "PATIENT_II", "therapy", "THERAPY_II", "has received therapy");

        
        ///////////////////////////////////////
        // viral isolates
        catalog.addBaseClause("viral_isolate");
        catalog.addDateClauses(catalog, "viral_isolate", "SAMPLE_DATE", "was taken on");
        
        // link patient - viral isolate
        catalog.addGetAssociationClause("patient", "PATIENT_II", "viral_isolate", "VIRAL_ISOLATE_II", "has a viral isolate");
 
        
        ///////////////////////////////////////
        // nucleotide sequence
        catalog.addBaseClause("nt_sequence");
        catalog.addDateClauses(catalog, "nt_sequence", "SEQUENCE_DATE", "was sequenced on");
        
        // link viral isolate-nt sequence
        catalog.addGetAssociationClause("viral_isolate", "VIRAL_ISOLATE_II", "nt_sequence", "NT_SEQUENCE_II", "has a nucleotide sequence");


        ///////////////////////////////////////
        // amino acid sequence
        catalog.addBaseClause("aa_sequence");
        
        // link nt squence - aa sequence
        catalog.addGetAssociationClause("nt_sequence", "NT_SEQUENCE_II","aa_sequence" ,"AA_SEQUENCE_II" , "has an amino acid sequence");
       
        
        ///////////////////////////////////////
        // amino acid mutation
        catalog.addBaseClause("aa_mutation");

        // link aa sequence - aa mutation
        catalog.addGetAssociationClause("aa_sequence", "AA_SEQUENCE_II", "aa_mutation", "AA_SEQUENCE_II");
        
        

        ///////////////////////////////////////
        // amino acid insertion
        catalog.addBaseClause("aa_insertion");
        
        // link aa sequence - aa insertion
        catalog.addGetAssociationClause("aa_sequence", "AA_SEQUENCE_II", "aa_insertion", "AA_SEQUENCE_II");
        
        
        ///////////////////////////////////////
        // custom attributes

        // link patient - custom attribute
        String[][] assocListPatienttoAttribute = {{"patient", null, "PATIENT_II"}, {"patient_attribute_value", "PATIENT_II",null}};
        catalog.addGetRemoteAssociationClause(assocListPatienttoAttribute, "has the attribute");

        // link custom attribute - attribute name
        catalog.addMandatoryValuesToClause(
        		catalog.addCodedPropertyCheckClause("patient_attribute_value", "ATTRIBUTE_II", "attribute", "ATTRIBUTE_II", "NAME", "has the name", true),
        		new String[] {"attribute"},
        		new String[] {"NAME"});
        		
        // link custom attribute - nominal value
        catalog.addMandatoryValuesToClause(
        		catalog.addCodedPropertyCheckClause("patient_attribute_value", "NOMINAL_VALUE_II", "attribute_nominal_value", "NOMINAL_VALUE_II", "VALUE", "has the nominal value", true),
        		new String[] {"attribute_nominal_value"},
        		new String[] {"VALUE"});
        
        
        ///////////////////////////////////////
        // generic drugs
        catalog.addBaseClause("drug_generic");
   		catalog.addPropertyCheckClause("drug_generic", "GENERIC_ID", "has id", false, false);
   		catalog.addStringClauses(catalog, "drug_generic", "GENERIC_NAME", "has name", false);
   		catalog.addStringClauses(catalog, "drug_generic", "ATC_CODE", "has atc code", false);

        // link therapy - generic drug
        String[][] assocListGenericDrugToTherapy = {{"therapy", null, "THERAPY_II"}, {"therapy_generic", "THERAPY_II","GENERIC_II"},
                {"drug_generic", "GENERIC_II", null}};
        catalog.addGetRemoteAssociationClause(assocListGenericDrugToTherapy, "was treated with the generic drug");
        

        ///////////////////////////////////////
        // commercial drug
        catalog.addBaseClause("drug_commercial");
   		catalog.addStringClauses(catalog, "drug_commercial", "NAME", "has name", false);
   		catalog.addStringClauses(catalog, "drug_commercial", "ATC_CODE", "has atc code", false);
        
        // link therapy - commercial drug
        String[][] assocListCommercialDrugToTherapy = {{"therapy", null, "THERAPY_II"}, {"therapy_commercial", "THERAPY_II","COMMERCIAL_II"},
                {"drug_commercial", "COMMERCIAL_II", null}};
        catalog.addGetRemoteAssociationClause(assocListCommercialDrugToTherapy, "was treated with the commercial drug");
        
        
        
 //       catalog.addSequenceMutationClause("aa_sequence", "AA_SEQUENCE_II", "aa_mutation", "AA_SEQUENCE_II","MUTATION_POSITION", "has a mutation", "in", false);
 //       catalog.addSequenceMutationClause("aa_sequence", "AA_SEQUENCE_II", "aa_mutation", "AA_SEQUENCE_II", "MUTATION_POSITION", "has a real mutation", "in", true);
 //       catalog.addSequenceMutationClause("aa_sequence", "AA_SEQUENCE_II", "aa_insertion", "AA_SEQUENCE_II", "INSERTION_POSITION", "has an insertion", "starting at", false);
 
 //       catalog.addCodedPropertyClauseMandatoryValues("PROTEIN", "PROTEIN_II", "aa_sequence", "PROTEIN_II", "full_name", "codes for protein", true);
        
        
        /*      
        String[][] assocListPatientToResult = {{"Patient", null, "PATIENT_II"}, {"PATIENT_SAMPLE", "PATIENT_II", "PATIENT_SAMPLE_ID"},
        {"Clinical_Isolate", "CLINICAL_ISOLATE_ID", "CLINICAL_ISOLATE_II"}, {"Measurement", "CLINICAL_ISOLATE_II", "MEASUREMENT_II"},
        {"Result", "MEASUREMENT_II", null}};
        String[][] assocListPatientToVLResult = {{"Patient", null, "PATIENT_II"}, {"PATIENT_SAMPLE", "PATIENT_II", "PATIENT_SAMPLE_ID"},
        {"Clinical_Isolate", "CLINICAL_ISOLATE_ID", "CLINICAL_ISOLATE_II"}, {"Measurement", "CLINICAL_ISOLATE_II", "MEASUREMENT_II"},
        {"Result", "MEASUREMENT_II", null, "ELEM_TEST_II", "IN (67, 68, 92)"}};
        String[][] assocListPatientToPositiveVLResult = {{"Patient", null, "PATIENT_II"}, {"PATIENT_SAMPLE", "PATIENT_II", "PATIENT_SAMPLE_ID"},
        {"Clinical_Isolate", "CLINICAL_ISOLATE_ID", "CLINICAL_ISOLATE_II"}, {"Measurement", "CLINICAL_ISOLATE_II", "MEASUREMENT_II"},
        {"Result", "MEASUREMENT_II", null, "ELEM_TEST_II", "IN (67, 68, 92)", "RELATION", "IN ('>', '=', '')"}};
        String[][] assocListPatientToNegativeVLResult = {{"Patient", null, "PATIENT_II"}, {"PATIENT_SAMPLE", "PATIENT_II", "PATIENT_SAMPLE_ID"},
        {"Clinical_Isolate", "CLINICAL_ISOLATE_ID", "CLINICAL_ISOLATE_II"}, {"Measurement", "CLINICAL_ISOLATE_II", "MEASUREMENT_II"},
        {"Result", "MEASUREMENT_II", null, "ELEM_TEST_II", "IN (67, 68, 92)", "RELATION", "= '<'"}};
        String[][] assocListPatientToCD4Result = {{"Patient", null, "PATIENT_II"}, {"PATIENT_SAMPLE", "PATIENT_II", "PATIENT_SAMPLE_ID"},
        {"Clinical_Isolate", "CLINICAL_ISOLATE_ID", "CLINICAL_ISOLATE_II"}, {"Measurement", "CLINICAL_ISOLATE_II", "MEASUREMENT_II"},
        {"Result", "MEASUREMENT_II", null, "ELEM_TEST_II", "IN (71, 72, 88, 90, 93, 75)"}};
        
        String[] resultDateSpec = {"Result", "RESULT_DATE"};
        String[] calcResultDateSpec = {"Calc_Result", "RESULT_DATE"};
        String[] isolateDateSpec = {"Clinical_Isolate", "ESTIMATED_TAKE_DATE"};
        String[] virusDateSpec = {"Viral_Clin_Isolate", "ESTIMATED_TAKE_DATE"};
        catalog.addGetRemoteAssociationClause(assocListPatientToResult);
        catalog.addClosestAssociationToDateClause(assocListPatientToVLResult, isolateDateSpec, "measuring viral load", true);
        catalog.addClosestAssociationToDateClause(assocListPatientToVLResult, isolateDateSpec, "measuring viral load", false);
        catalog.addClosestAssociationToDateClause(assocListPatientToCD4Result, isolateDateSpec, "measuring CD4", true);
        catalog.addClosestAssociationToDateClause(assocListPatientToCD4Result, isolateDateSpec, "measuring CD4", false);
        catalog.addAssociationInPeriodClauses(assocListPatientToPositiveVLResult, isolateDateSpec, "measuring a detectable viral load");
        catalog.addAssociationInPeriodClauses(assocListPatientToNegativeVLResult, isolateDateSpec, "measuring no detectable viral load");
        String[] noDetectionSpec = {"Result", "RELATION", "= '<'"};
        catalog.addChangedAssociationInPeriodClauses(assocListPatientToPositiveVLResult, assocListPatientToVLResult, resultDateSpec, noDetectionSpec, "measuring a detectable viral load after a non-detectable one");
        
        String[][] assocListPatientToCalcResult = {{"Patient", null, "PATIENT_II"}, {"PATIENT_REQUEST", "PATIENT_II", "PATIENT_REQUEST_ID"},
        {"Request", "REQUEST_ID", "REQUEST_II"}, {"Calc_Result", "REQUEST_II", null}};
        String[][] assocListPatientToVLCalcResult = {{"Patient", null, "PATIENT_II"}, {"PATIENT_REQUEST", "PATIENT_II", "PATIENT_REQUEST_ID"},
        {"Request", "REQUEST_ID", "REQUEST_II"}, {"Calc_Result", "REQUEST_II", null, "CALC_TEST_II", "= 7"}};
        String[][] assocListPatientToCD4CalcResult = {{"Patient", null, "PATIENT_II"}, {"PATIENT_REQUEST", "PATIENT_II", "PATIENT_REQUEST_ID"},
        {"Request", "REQUEST_ID", "REQUEST_II"}, {"Calc_Result", "REQUEST_II", null, "CALC_TEST_II", "IN (34, 36, 37)"}};
        catalog.addGetRemoteAssociationClause(assocListPatientToCalcResult);
        catalog.addClosestAssociationToDateClause(assocListPatientToVLCalcResult, calcResultDateSpec, "measuring viral load", true);
        catalog.addClosestAssociationToDateClause(assocListPatientToVLCalcResult, calcResultDateSpec, "measuring viral load", false);
        catalog.addClosestAssociationToDateClause(assocListPatientToCD4CalcResult, calcResultDateSpec, "measuring CD4", true);
        catalog.addClosestAssociationToDateClause(assocListPatientToCD4CalcResult, calcResultDateSpec, "measuring CD4", false);
        
        catalog.addConvertMicrogramsToMillimolarityClause();
        
        catalog.addRealValueWithRelationConstraintClause("Result", "REAL_VALUE", "RELATION", true);
        catalog.addRealValueWithRelationConstraintClause("Result", "REAL_VALUE", "RELATION", false);
        catalog.addRealValueWithRelationConstraintClause("Calc_Result", "REAL_VALUE", "RELATION", true);
        catalog.addRealValueWithRelationConstraintClause("Calc_Result", "REAL_VALUE", "RELATION", false);
        
        catalog.addClinicalTestClause();
        catalog.addClinicalCalcTestClause();
        catalog.addCD4TestClause();
        catalog.addCD4CalcTestClause();
        catalog.addViralLoadTestClause();
        catalog.addViralLoadCalcTestClause();
        
        String[][] assocListChimericVirusAppearanceToWildTypeChimeric = {{"Virus_Appearance", null, "VIRUS_APP_II"},
        {"Virus_App_Chimeric", "VIRUS_APP_II", "VIRUS_APP_II", "BACKGROUND_APP_II", "?1 ="},
        {"Virus_Chimeric_FG", "VIRUS_APP_II", "FOREGROUND_APP_II", "REGION_II", "?2 ="}, {"Virus_App_Selected", "VIRUS_APP_II", "RESISTANCE_NR"},
        {"Resistance", "RESISTANCE_NR", "WILDTYPE_APP_II"}, {"Virus_Appearance", "VIRUS_APP_II", "VIRUS_APP_II"},
        {"Virus_Chimeric_FG", "FOREGROUND_APP_II", "VIRUS_APP_II", "REGION_II", "!2"},
        {"Virus_App_Chimeric", "VIRUS_APP_II", "VIRUS_APP_II", "BACKGROUND_APP_II", "!1"}, {"Virus_Appearance", "VIRUS_APP_II", null}};
        catalog.addGetRemoteAssociationClauseWithForegroundTest(assocListChimericVirusAppearanceToWildTypeChimeric, "is chimeric and has a corresponding wildtype chimeric virus");
        
        String[][] assocListSelectedExperimentToWildTypeExperiment = {{"Experiment", null, "EXPERIMENT_II", "EXPERIMENT_DATE", "?1 ="},
        {"Sample", "EXPERIMENT_II", "VIRUS_STOCK_ID", "DRUG_STOCK_II", "?2 ="}, {"Virus_Stock", "VIRUS_STOCK_ID", "VIRUS_APP_II"},
        {"Virus_Appearance", "VIRUS_APP_II", "VIRUS_APP_II"}, {"Virus_App_Selected", "VIRUS_APP_II", "RESISTANCE_NR"},
        {"Resistance", "RESISTANCE_NR", "WILDTYPE_APP_II"}, {"Virus_Appearance", "VIRUS_APP_II", "VIRUS_APP_II"},
        {"Virus_Stock", "VIRUS_APP_II", "VIRUS_STOCK_ID"}, {"Sample", "VIRUS_STOCK_ID", "EXPERIMENT_II", "DRUG_STOCK_II", "!2"},
        {"Experiment", "EXPERIMENT_II", null, "EXPERIMENT_DATE", "!1"}};
        catalog.addGetRemoteAssociationClause(assocListSelectedExperimentToWildTypeExperiment, "is an experiment on a selected virus and has a corresponding wildtype experiment");
        
        String[][] assocListSelectedResultToWildTypeResult = {{"Result", null, "MEASUREMENT_II", "ELEM_TEST_II", "?1 =", "RESULT_DATE", "?2 ="},
        {"Measurement", "MEASUREMENT_II", "SAMPLE_II"},  {"Sample", "SAMPLE_II", "VIRUS_STOCK_ID", "DRUG_STOCK_II", "?3 ="}, {"Virus_Stock", "VIRUS_STOCK_ID", "VIRUS_APP_II"},
        {"Virus_Appearance", "VIRUS_APP_II", "VIRUS_APP_II"}, {"Virus_App_Selected", "VIRUS_APP_II", "RESISTANCE_NR"},
        {"Resistance", "RESISTANCE_NR", "WILDTYPE_APP_II"}, {"Virus_Appearance", "VIRUS_APP_II", "VIRUS_APP_II"},
        {"Virus_Stock", "VIRUS_APP_II", "VIRUS_STOCK_ID"}, {"Sample", "VIRUS_STOCK_ID", "SAMPLE_II", "DRUG_STOCK_II", "!3"}, {"Measurement", "SAMPLE_II", "MEASUREMENT_II"},
        {"Result", "MEASUREMENT_II", null, "ELEM_TEST_II", "!1", "RESULT_DATE", "!2"}};
        catalog.addGetRemoteAssociationClause(assocListSelectedResultToWildTypeResult, "is a result on a selected virus and has a corresponding wildtype result");
        
        String[][] assocListChimericExperimentToWildTypeChimericExperiment = {{"Experiment", null, "EXPERIMENT_II", "EXPERIMENT_DATE", "?1 ="},
        {"Sample", "EXPERIMENT_II", "VIRUS_STOCK_ID", "DRUG_STOCK_II", "?2 ="}, {"Virus_Stock", "VIRUS_STOCK_ID", "VIRUS_APP_II"},
        {"Virus_App_Chimeric", "VIRUS_APP_II", "VIRUS_APP_II", "BACKGROUND_APP_II", "?3 ="},
        {"Virus_Chimeric_FG", "VIRUS_APP_II", "FOREGROUND_APP_II", "REGION_II", "?4 ="}, {"Virus_App_Selected", "VIRUS_APP_II", "RESISTANCE_NR"},
        {"Resistance", "RESISTANCE_NR", "WILDTYPE_APP_II"}, {"Virus_Appearance", "VIRUS_APP_II", "VIRUS_APP_II"},
        {"Virus_Chimeric_FG", "FOREGROUND_APP_II", "VIRUS_APP_II", "REGION_II", "!4"}, {"Virus_App_Chimeric", "VIRUS_APP_II", "VIRUS_APP_II", "BACKGROUND_APP_II", "!3"},
        {"Virus_Stock", "VIRUS_APP_II", "VIRUS_STOCK_ID"}, {"Sample", "VIRUS_STOCK_ID", "EXPERIMENT_II", "DRUG_STOCK_II", "!2"},
        {"Experiment", "EXPERIMENT_II", null, "EXPERIMENT_DATE", "!1"}};
        catalog.addGetRemoteAssociationClauseWithForegroundTest(assocListChimericExperimentToWildTypeChimericExperiment, "is an experiment on a chimeric virus and has a corresponding wildtype chimeric experiment");
        
        String[][] assocListChimericResultToWildTypeChimericResult = {{"Result", null, "MEASUREMENT_II", "ELEM_TEST_II", "?1 =", "RESULT_DATE", "?2 ="},
        {"Measurement", "MEASUREMENT_II", "SAMPLE_II"},  {"Sample", "SAMPLE_II", "VIRUS_STOCK_ID", "DRUG_STOCK_II", "?3 ="},
        {"Virus_Stock", "VIRUS_STOCK_ID", "VIRUS_APP_II"}, {"Virus_App_Chimeric", "VIRUS_APP_II", "VIRUS_APP_II", "BACKGROUND_APP_II", "?4 ="},
        {"Virus_Chimeric_FG", "VIRUS_APP_II", "FOREGROUND_APP_II", "REGION_II", "?5 ="}, {"Virus_App_Selected", "VIRUS_APP_II", "RESISTANCE_NR"},
        {"Resistance", "RESISTANCE_NR", "WILDTYPE_APP_II"}, {"Virus_Appearance", "VIRUS_APP_II", "VIRUS_APP_II"},
        {"Virus_Chimeric_FG", "FOREGROUND_APP_II", "VIRUS_APP_II", "REGION_II", "!5"}, {"Virus_App_Chimeric", "VIRUS_APP_II", "VIRUS_APP_II", "BACKGROUND_APP_II", "!4"},
        {"Virus_Stock", "VIRUS_APP_II", "VIRUS_STOCK_ID"}, {"Sample", "VIRUS_STOCK_ID", "SAMPLE_II", "DRUG_STOCK_II", "!3"},
        {"Measurement", "SAMPLE_II", "MEASUREMENT_II"}, {"Result", "MEASUREMENT_II", null, "ELEM_TEST_II", "!1", "RESULT_DATE", "!2"}};
        catalog.addGetRemoteAssociationClauseWithForegroundTest(assocListChimericResultToWildTypeChimericResult, "is a result on a chimeric virus and has a corresponding wildtype chimeric result");
        
        catalog.addSequenceToResistanceResultClause();
        
        String[][] virusToRealMutationList = {{"Virus_Appearance", null, "VIRUS_APP_II"}, {"AA_Sequence", "VIRUS_APP_II", "AA_SEQUENCE_ID"}, {"SEQUENCE_AA_MUTATION", "MUTATION_SEQUENCE_ID", null, "AA_WILDTYPE", "?1 <>", "AA_MUTATION", "!1"}};
        String[][] sequenceToRealMutationList = {{"AA_Sequence", null, "AA_SEQUENCE_ID"}, {"SEQUENCE_AA_MUTATION", "MUTATION_SEQUENCE_ID", null, "AA_WILDTYPE", "?1 <>", "AA_MUTATION", "!1"}};
        catalog.addGetRemoteAssociationClause(virusToRealMutationList, "has a sequence with at least one real amino acid mutation");
        catalog.addGetRemoteAssociationClause(sequenceToRealMutationList, "has a real mutation");
        
        catalog.addSequenceMutationClause("AA_Sequence", "AA_SEQUENCE_ID", "SEQUENCE_AA_MUTATION", "MUTATION_SEQUENCE_ID", "AA_POSITION", "has a mutation", "in", false);
        catalog.addSequenceMutationClause("AA_Sequence", "AA_SEQUENCE_ID", "SEQUENCE_AA_MUTATION", "MUTATION_SEQUENCE_ID", "AA_POSITION", "has a real mutation", "in", true);
        catalog.addSequenceMutationClause("AA_Sequence", "AA_SEQUENCE_ID", "SEQUENCE_AA_INSERTION", "MUTATION_SEQUENCE_ID", "AA_PRE_POSITION", "has an insertion", "starting at", false);
        catalog.addSequenceMutationClause("AA_Sequence", "AA_SEQUENCE_ID", "SEQUENCE_AA_DELETION", "MUTATION_SEQUENCE_ID", "AA_FROM_POSITION", "has a deletion", "starting at", false);
        catalog.addSequenceMutationClause("NT_Sequence", "NT_SEQUENCE_ID", "SEQUENCE_NT_MUTATION", "MUTATION_SEQUENCE_ID", "NT_POSITION", "has a mutation", "in", false);
        catalog.addSequenceMutationClause("NT_Sequence", "NT_SEQUENCE_ID", "SEQUENCE_NT_INSERTION", "MUTATION_SEQUENCE_ID", "NT_PRE_POSITION", "has an insertion", "starting at", false);
        catalog.addSequenceMutationClause("NT_Sequence", "NT_SEQUENCE_ID", "SEQUENCE_NT_DELETION", "MUTATION_SEQUENCE_ID", "NT_FROM_POSITION", "has a deletion", "starting at", false);
        
        catalog.addBaseClause("Molecules");
//        catalog.addMoleculeClause();
        
        catalog.addDrugWorkMechanismClause();
        
        // KVB : actually this link should be via MoleculeId
        catalog.addGetAssociationClauses("Drug_Compound", "MOLECULEID", "Molecules", "MOLECULEID", "consists of the molecule", "is used as drug compound");
        catalog.addGetAssociationClauses("Drug_Stock", "DRUG_COMPOUND_II", "Drug_Compound", "DRUG_COMPOUND_II", "is a stock of drug compound", "has a stock");
        catalog.addGetAssociationClauses("Sample", "DRUG_STOCK_II", "Drug_Stock", "DRUG_STOCK_II", "was made using drug stock", "was used for sample");
        
        catalog.addGetDoubleRemoteAssociationClause(assocListMoleculeToSample, assocListVirusAppearanceToSample, "are brought together in sample");
        
        catalog.addGetAssociationClauses("Measurement", "SAMPLE_II", "Sample", "SAMPLE_II", "is performed on sample", "was used for measurement");
        
        catalog.addGetAssociationClauses("Sample", "EXPERIMENT_II", "Experiment", "EXPERIMENT_II", "was used for experiment", "is based on sample");
       */ 
        mainCatalog = catalog;
    }
    
    public void addDateClauses (AWCPrototypeCatalog catalog, String tableName, String propertyName, String description) {
        catalog.addGetPropertyClause(tableName, propertyName, description + " date");
        catalog.addPropertyTimeIntervalClause(tableName, propertyName, description + " date", false);
        catalog.addPropertyCheckClause(tableName, propertyName, description + " date", false, false);
    }
    
    public void addStringClauses(AWCPrototypeCatalog catalog, String tableName, String propertyName, String description, boolean caseSensitive) {
		catalog.addPropertyCheckClause(tableName, propertyName, description, false, caseSensitive);
        catalog.addPropertyLikeClause(tableName, propertyName, description, false, caseSensitive);
        catalog.addPropertyStartsLikeClause(tableName, propertyName, description, false, caseSensitive);
        catalog.addPropertyEndsLikeClause(tableName, propertyName, description, false, caseSensitive);
    }
    
    ///////////////////////////////////////
    // associations
    
    private Map typeNameToGoodVariableName = new HashMap();
    private List atomicWhereClauses = new ArrayList();
    
    ///////////////////////////////////////
    // access methods for associations
    
    public final Table getTable(String name) {
        return JDBCManager.getInstance().getTableCatalog().doGetTable(name);
    }
    
    public void addAtomicWhereClause(AtomicWhereClause atomicWhereClause) {
        this.atomicWhereClauses.add(atomicWhereClause);
    }
    
    private void addGoodVariableName(String typeName, String varName) {
        typeNameToGoodVariableName.put(typeName.toLowerCase(), varName);
    }
    
    public String getGoodVariableName(VariableType varType) {
        return getGoodVariableName(varType.getName());
    }
    
    public String getGoodVariableName(String tableName) {
        String varName = (String)typeNameToGoodVariableName.get(tableName.toLowerCase());
        if (varName == null) {
            return tableName.substring(0, 1);
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
        JDBCManager manager = JDBCManager.getInstance();
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
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
        	return manager.getColumnType(tableName, propertyName);
        }
        else {
            System.err.println("Unknown column " + propertyName + " for " + tableName);
        	return null;
        }
    }
    
    private OutputVariable getOutputVariable(String typeString, String propertyName, FromVariable fromVar) {
        OutputVariable ovar = getBasicOutputVariable(typeString, propertyName);
        ovar.getExpression().addFromVariable(fromVar);
        return ovar;
    }
    
    private OutputVariable getOutputVariable(String typeString, String propertyName, Constant constant) {
        OutputVariable ovar = getBasicOutputVariable(typeString, propertyName);
        ovar.getExpression().addConstant(constant);
        return ovar;
    }
    
    private OutputVariable getOutputVariable(String typeString, String propertyName, InputVariable ivar) {
        OutputVariable ovar = getBasicOutputVariable(typeString, propertyName);
        ovar.getExpression().addInputVariable(ivar);
        ovar.getExpression().addFixedString(new FixedString("." + propertyName));
        return ovar;
    }
    
    private OutputVariable getBasicOutputVariable(String typeString, String propertyName) {
        OutputVariable ovar = new OutputVariable(new VariableType(typeString), getGoodVariableName(propertyName));
        ovar.setUniqueName(ovar.getFormalName());
    	return ovar;
    }
    
    private boolean isStringType(String dataTypeString) {
    	return isStringType(Integer.parseInt(dataTypeString));
    }
    
    private boolean isStringType(int dataType) {
    	return dataType == 12;
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
    
    private boolean isNumericType(int dataType) {
    	return (((8 >= dataType) && (dataType >=1)) || dataType == 1111);
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
	    	Iterator it = clause.getConstants().iterator();
	    	while (it.hasNext() && i < tables.length && i < properties.length) {
	    		Constant constant = (Constant) it.next();
	    		constant.setSuggestedValuesQuery("SELECT DISTINCT "+ properties[i] + " FROM " + tables[i]);
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
    public Collection getAWCPrototypes(Collection availableOutputVariables) {
        // your code here
        Collection result = new ArrayList();
        Iterator iter = atomicWhereClauses.iterator();
        while (iter.hasNext()) {
            AtomicWhereClause clause = (AtomicWhereClause)iter.next();
            boolean clauseOk = true;
            Iterator inputIter = clause.getInputVariables().iterator();
            while (inputIter.hasNext()) {
                InputVariable ivar = (InputVariable)inputIter.next();
                boolean varOk = false;
                //System.out.println("Checking ..." + ivar.getVariableType());
                Iterator outputIter = availableOutputVariables.iterator();
                while (outputIter.hasNext()) {
                    OutputVariable ovar = (OutputVariable)outputIter.next();
                    if (ivar.isCompatible(ovar)) {
                        //System.out.println(ivar.getVariableType());
                        varOk = true;
                        break;
                    } else {
                        //System.out.println(ivar.getVariableType() + " is not " + ovar.getVariableType());
                    }
                }
                if (! varOk) {
                    clauseOk = false;
                    break;
                }
            }
            if (clauseOk) {
                result.add(clause);
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
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();

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
    
    /**
     * allow users to search for an identical property match
     * @param tableName
     * @param propertyName
     * @param description
     * @param show  
     * @param caseSensitive
     * @return
     */
    public AtomicWhereClause addPropertyCheckClause(String tableName, String propertyName, String description, boolean show, boolean caseSensitive) {
    	Properties p = getDataTypeDependantProperties(tableName, propertyName);   	
    	if (p != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
            Constant constant = (Constant) p.get("constant");
            String dataTypeString = (String) p.get("dataTypeString");
            String typeString = (String) p.get("typeString");
            
            description	= description == null ? "has a " + propertyName : description;
            InputVariable qTableInstanceName = composeHumanReadableQuery(aClause.getVisualizationClauseList(), tableName, null, propertyName, description, constant, (show?getOutputVariable(typeString, propertyName, constant):null), null);
            
            if (!isStringType(dataTypeString) || caseSensitive) {
                aComposer.addInputVariable(qTableInstanceName);
                aComposer.addFixedString(new FixedString("." + propertyName + " = "));
                aComposer.addConstant(constant);
            } 
            else {  // only for case insensitive string comparison
                aComposer.addFixedString(new FixedString("UPPER("));
                aComposer.addInputVariable(qTableInstanceName);
                aComposer.addFixedString(new FixedString("." + propertyName + ") = UPPER("));
                aComposer.addConstant(constant);
                aComposer.addFixedString(new FixedString(")"));
            }
            addAtomicWhereClause(aClause);
            return aClause;
    	}
    	else {
    		return null;
    	}
    }
    
    public AtomicWhereClause addPropertyLikeClause(String tableName, String propertyName, String description, boolean show, boolean caseSensitive, Constant likeConstant, String constantDescription) {
    	Properties p = getDataTypeDependantProperties(tableName, propertyName);   	
    	if (p != null) {
            String dataTypeString = (String) p.get("dataTypeString");
            String typeString = (String) p.get("typeString");
            Constant constant = likeConstant;
            
            if (isStringType(dataTypeString)) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
	            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
	
	            description	= description == null ? "has a " + propertyName : description;
	            InputVariable 	qTableInstanceName = composeHumanReadableQuery(aClause.getVisualizationClauseList(), tableName, null, propertyName, description, constant, (show?getOutputVariable(typeString, propertyName, constant):null), constantDescription);
	            
	            if (caseSensitive) {
	                aComposer.addInputVariable(qTableInstanceName);
	                aComposer.addFixedString(new FixedString("." + propertyName + " LIKE "));
	                aComposer.addConstant(constant);
	            } 
	            else {
	                aComposer.addFixedString(new FixedString("UPPER("));
	                aComposer.addInputVariable(qTableInstanceName);
	                aComposer.addFixedString(new FixedString("." + propertyName + ") LIKE UPPER("));
	                aComposer.addConstant(constant);
	                aComposer.addFixedString(new FixedString(")"));
	            }
	            addAtomicWhereClause(aClause);
	            return aClause;
            }
            else {
                System.err.println("Incompatible datatype, string expected: " + tableName + "." + propertyName);
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
    public AtomicWhereClause addPropertyLikeClause(String tableName, String propertyName, String description, boolean show, boolean caseSensitive) {
    	return addPropertyLikeClause(tableName, propertyName, description, show, caseSensitive, new SubstringConstant(), "containing");
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
    public AtomicWhereClause addPropertyEndsLikeClause(String tableName, String propertyName, String description, boolean show, boolean caseSensitive) {
    	return addPropertyLikeClause(tableName, propertyName, description, show, caseSensitive, new EndstringConstant(), "that ends with");
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
    public AtomicWhereClause addPropertyStartsLikeClause(String tableName, String propertyName, String description, boolean show, boolean caseSensitive) {
    	return addPropertyLikeClause(tableName, propertyName, description, show, caseSensitive, new StartstringConstant(), "that starts with");
    }

    /**
     * compose a human readable query for the given VisualizationClauseList
     * @param aVisList the {@link VisualizationClauseList} to fill
     * @param tableName the name of the table to query
     * @param ivar inputvariable derived from the table. If null a new inputvariable will be derived from the given table  
     * @param propertyName the name of the property of the table to query
     * @param relDdescription description of the relationship
     * @param constant constant chosen by the user. Can be null if the resulting query should have a new variable instead.
     * @param typeString type of the constant chosen by the user. Can be null if you do not
     * want a name assigned to this constant
     * @param constantDescription extra description for the constant. Can be null if no extra description is needed
     * @return the input variable derived from the table (instance name of the table)
     */
    private InputVariable composeHumanReadableQuery(VisualizationClauseList aVisList, String tableName, InputVariable ivar, String propertyName, String relDescription, Constant constant, OutputVariable ovar, String constantdescription) {
        String qTableName = getTable(tableName).getSingularName();

        aVisList.addFixedString(new FixedString("The " + qTableName));
        if (ivar == null) ivar = new InputVariable(new VariableType(tableName));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString(relDescription));
        if (ovar != null) aVisList.addOutputVariable(ovar);
        if (constantdescription != null) aVisList.addFixedString(new FixedString(constantdescription));
        if (constant != null) aVisList.addConstant(constant);
        
        return ivar;
    }
    
    
    /**
     * gets the property from the given table as a variable
     * @param tableName
     * @param propertyName
     * @param description
     * @return
     */
    public AtomicWhereClause addGetPropertyClause(String tableName, String propertyName, String description) {
    	Properties p = getDataTypeDependantProperties(tableName, propertyName);   	
    	if (p != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            String typeString = (String) p.get("typeString");

            description			= description == null ? "has a " + propertyName : description;
            InputVariable ivar  = new InputVariable(new VariableType(tableName));
            OutputVariable ovar = getOutputVariable(typeString, propertyName, ivar);
            composeHumanReadableQuery(aClause.getVisualizationClauseList(), tableName, ivar, propertyName, description, null, ovar, null);
            
            aClause.getHibernateClauseComposer().addFixedString(new FixedString("1=1"));
            addAtomicWhereClause(aClause);
            return aClause;
    	}
    	return null;
    }

    /**
     * select the property propertyName from the table codeTableName associated with the table tableName as a variable 
     * @param tableName
     * @param codeName
     * @param codeTableName
     * @param codeKeyName
     * @param propertyName
     * @param description
     * @return
     */
    public AtomicWhereClause addGetPropertyClause(String tableName, String codeName, String codeTableName, String codeKeyName, String propertyName, String description) {
    	Properties p = getDataTypeDependantProperties(tableName, propertyName);   	
    	if (p != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
            Constant constant = (Constant) p.get("constant");
            String typeString = (String) p.get("typeString");

            description				= description == null ? "has a " + propertyName : description;
            InputVariable ivar  	= new InputVariable(new VariableType(tableName));
            FromVariable codeVar	= new FromVariable(codeTableName);
            OutputVariable ovar 	= getOutputVariable(typeString, propertyName, codeVar);
            ovar.getExpression().addFixedString(new FixedString("." + propertyName));
            
            composeHumanReadableQuery(aClause.getVisualizationClauseList(), tableName, null, propertyName, description, constant, ovar, null);
            
            aComposer.addInputVariable(ivar);
            aComposer.addFixedString(new FixedString("." + codeName + " = "));
            aComposer.addFromVariable(codeVar);
            aComposer.addFixedString(new FixedString("." + codeKeyName));
            
            addAtomicWhereClause(aClause);
            return aClause;
    	}
    	return null;
    }
    
    public AtomicWhereClause addGetAssociationClauses(String tableName, String foreignKeyName, String foreignTableName, String foreignTableKey) {
        AtomicWhereClause aClause1 = addGetAssociationClause(tableName, foreignKeyName, foreignTableName, foreignTableKey);
        AtomicWhereClause aClause2 = addGetAssociationClause(foreignTableName, foreignTableKey, tableName, foreignKeyName);
        if (aClause1 == null) {
            return aClause2;
        } else {
            return aClause1;
        }
    }
    
    public AtomicWhereClause addGetAssociationClauses(String tableName, String foreignKeyName, String foreignTableName, String foreignTableKey, String description1, String description2) {
        AtomicWhereClause aClause1 = addGetAssociationClause(tableName, foreignKeyName, foreignTableName, foreignTableKey, description1);
        AtomicWhereClause aClause2 = addGetAssociationClause(foreignTableName, foreignTableKey, tableName, foreignKeyName, description2);
        if (aClause1 == null) {
            return aClause2;
        } else {
            return aClause1;
        }
    }
    
    public AtomicWhereClause addGetAssociationClause(String tableName, String foreignKeyName, String foreignTableName, String foreignTableKey) {
        return addGetAssociationClause(tableName, foreignKeyName, foreignTableName, foreignTableKey, null);
    }
    
    public AtomicWhereClause addGetAssociationClause(String tableName, String foreignKeyName, String foreignTableName, String foreignTableKey, String description) {
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            String dataTypeString = manager.getColumnType(tableName, foreignKeyName);
            if (dataTypeString != null) {
                AtomicWhereClause aClause = new AtomicWhereClause();
                VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
                HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
                String singularName = getTable(tableName).getSingularName();
                aVisList.addFixedString(new FixedString("The " + singularName));
                InputVariable ivar = new InputVariable(new VariableType(tableName));
                aVisList.addInputVariable(ivar);
                String foreignSingularName = getTable(foreignTableName).getSingularName();
                if (description == null) {
                    aVisList.addFixedString(new FixedString("has an associated " + foreignSingularName));
                } else {
                    aVisList.addFixedString(new FixedString(description));
                }
                FromVariable newFromVar = new FromVariable(foreignTableName);
                OutputVariable ovar = new OutputVariable(new VariableType(foreignTableName), getGoodVariableName(foreignTableName));
                ovar.setUniqueName(ovar.getFormalName());
                aVisList.addOutputVariable(ovar);
                ovar.getExpression().addFromVariable(newFromVar);
                
                aComposer.addInputVariable(ivar);
                aComposer.addFixedString(new FixedString("." + foreignKeyName + " = "));
                aComposer.addFromVariable(newFromVar);
                aComposer.addFixedString(new FixedString("." + foreignTableKey));
                
                addAtomicWhereClause(aClause);
                return aClause;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    public AtomicWhereClause addGetRemoteAssociationClause(String[][] args) {
        return addGetRemoteAssociationClause(args, null);
    }
    
    public AtomicWhereClause addGetRemoteAssociationClause(String[][] args, String description) {
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
            HashMap tmpMap = new HashMap(); // for storing parts of expressions that come from different parts of the query
            String tableName = args[0][0];
            String keyName = args[0][1];
            String foreignKeyName = args[0][2];
            String singularName = getTable(tableName).getSingularName();
            aVisList.addFixedString(new FixedString("The " + singularName));
            InputVariable ivar = new InputVariable(new VariableType(tableName));
            aVisList.addInputVariable(ivar);
            for (int j = 3; j < args[0].length; j = j + 2) { // checks on this table
                if (args[0][j+1].startsWith("?")) {
                    String keyString = args[0][j+1].substring(1, args[0][j+1].indexOf(' '));
                    //System.err.println(keyString);
                    ArrayList expressionList = new ArrayList();
                    expressionList.add(ivar);
                    expressionList.add(new FixedString("." + args[0][j] + args[0][j+1].substring(args[0][j+1].indexOf(' ') + 1)));
                    tmpMap.put(keyString, expressionList);
                }
            }
            aComposer.addFixedString(new FixedString("("));
            aComposer.addInputVariable(ivar);
            aComposer.addFixedString(new FixedString("." + foreignKeyName + " = "));
            for (int i = 1; i < args.length; i++) {
                String newTableName = args[i][0];
                String newKeyName = args[i][1];
                String newForeignKeyName = args[i][2];
                FromVariable newFromVar = new FromVariable(newTableName);
                aComposer.addFromVariable(newFromVar);
                aComposer.addFixedString(new FixedString("." + newKeyName + ")"));
                for (int j = 3; j < args[i].length; j = j + 2) { // checks on this table
                    if (args[i][j+1].startsWith("?")) {
                        String keyString = args[i][j+1].substring(1, args[i][j+1].indexOf(' '));
                        //System.err.println(keyString);
                        ArrayList expressionList = new ArrayList();
                        expressionList.add(newFromVar);
                        expressionList.add(new FixedString("." + args[i][j] + args[i][j+1].substring(args[i][j+1].indexOf(' ') + 1)));
                        tmpMap.put(keyString, expressionList);
                    } else if (args[i][j+1].startsWith("!")) {
                        String keyString = args[i][j+1].substring(1);
                        //System.err.println(keyString);
                        ArrayList expressionList = (ArrayList)tmpMap.get(keyString);
                        aComposer.addFixedString(new FixedString(" and ("));
                        Iterator iter = expressionList.iterator();
                        while (iter.hasNext()) {
                            AWCWord word = (AWCWord)iter.next();
                            if (word instanceof FromVariable) {
                                aComposer.addFromVariable((FromVariable)word);
                            } else if (word instanceof FixedString) {
                                aComposer.addFixedString((FixedString)word);
                            } else if (word instanceof InputVariable) {
                                aComposer.addInputVariable((InputVariable)word);
                            } else {
                                System.err.println("Unsupported operation in AWCPrototypeCatalog.addGetRemoteAssociationClause()");
                            }
                        }
                        aComposer.addFromVariable(newFromVar);
                        aComposer.addFixedString(new FixedString("." + args[i][j] + ")"));
                    } else {
                        aComposer.addFixedString(new FixedString(" and ("));
                        aComposer.addFromVariable(newFromVar);
                        aComposer.addFixedString(new FixedString("." + args[i][j] + " " + args[i][j+1] + ")"));
                    }
                }
                if (i < args.length - 1) {
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + newForeignKeyName + " = "));
                } else {
                    String newSingularName = getTable(newTableName).getSingularName();
                    if (description == null) {
                        aVisList.addFixedString(new FixedString("has an associated " + newSingularName));
                    } else {
                        aVisList.addFixedString(new FixedString(description));
                    }
                    OutputVariable ovar = new OutputVariable(new VariableType(newTableName), getGoodVariableName(newTableName));
                    ovar.setUniqueName(ovar.getFormalName());
                    aVisList.addOutputVariable(ovar);
                    ovar.getExpression().addFromVariable(newFromVar);
                }
            }
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
    
    // this is actually quite ad-hoc, but used in all the variants where we look for a "wildtype chimeric equivalent" for something
    public AtomicWhereClause addGetRemoteAssociationClauseWithForegroundTest(String[][] args, String description) {
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
            HashMap tmpMap = new HashMap(); // for storing parts of expressions that come from different parts of the query
            FromVariable chimFromVar1 = null;
            FromVariable chimFromVar2 = null;
            FromVariable chimFGFromVar1 = null;
            FromVariable chimFGFromVar2 = null;
            String tableName = args[0][0];
            String keyName = args[0][1];
            String foreignKeyName = args[0][2];
            String singularName = getTable(tableName).getSingularName();
            aVisList.addFixedString(new FixedString("The " + singularName));
            InputVariable ivar = new InputVariable(new VariableType(tableName));
            aVisList.addInputVariable(ivar);
            for (int j = 3; j < args[0].length; j = j + 2) { // checks on this table
                if (args[0][j+1].startsWith("?")) {
                    String keyString = args[0][j+1].substring(1, args[0][j+1].indexOf(' '));
                    //System.err.println(keyString);
                    ArrayList expressionList = new ArrayList();
                    expressionList.add(ivar);
                    expressionList.add(new FixedString("." + args[0][j] + args[0][j+1].substring(args[0][j+1].indexOf(' ') + 1)));
                    tmpMap.put(keyString, expressionList);
                }
            }
            aComposer.addFixedString(new FixedString("("));
            aComposer.addInputVariable(ivar);
            aComposer.addFixedString(new FixedString("." + foreignKeyName + " = "));
            for (int i = 1; i < args.length; i++) {
                String newTableName = args[i][0];
                String newKeyName = args[i][1];
                String newForeignKeyName = args[i][2];
                FromVariable newFromVar = new FromVariable(newTableName);
                if (newTableName.equalsIgnoreCase("Virus_App_Chimeric")) {
                    if (chimFromVar1 == null) {
                        chimFromVar1 = newFromVar;
                    } else if (chimFromVar2 == null) {
                        chimFromVar2 = newFromVar;
                    }
                } else if (newTableName.equalsIgnoreCase("Virus_Chimeric_FG")) {
                    if (chimFGFromVar1 == null) {
                        chimFGFromVar1 = newFromVar;
                    } else if (chimFGFromVar2 == null) {
                        chimFGFromVar2 = newFromVar;
                    }
                }
                aComposer.addFromVariable(newFromVar);
                aComposer.addFixedString(new FixedString("." + newKeyName + ")"));
                for (int j = 3; j < args[i].length; j = j + 2) { // checks on this table
                    if (args[i][j+1].startsWith("?")) {
                        String keyString = args[i][j+1].substring(1, args[i][j+1].indexOf(' '));
                        //System.err.println(keyString);
                        ArrayList expressionList = new ArrayList();
                        expressionList.add(newFromVar);
                        expressionList.add(new FixedString("." + args[i][j] + args[i][j+1].substring(args[i][j+1].indexOf(' ') + 1)));
                        tmpMap.put(keyString, expressionList);
                    } else if (args[i][j+1].startsWith("!")) {
                        String keyString = args[i][j+1].substring(1);
                        //System.err.println(keyString);
                        ArrayList expressionList = (ArrayList)tmpMap.get(keyString);
                        aComposer.addFixedString(new FixedString(" and ("));
                        Iterator iter = expressionList.iterator();
                        while (iter.hasNext()) {
                            AWCWord word = (AWCWord)iter.next();
                            if (word instanceof FromVariable) {
                                aComposer.addFromVariable((FromVariable)word);
                            } else if (word instanceof FixedString) {
                                aComposer.addFixedString((FixedString)word);
                            } else if (word instanceof InputVariable) {
                                aComposer.addInputVariable((InputVariable)word);
                            } else {
                                System.err.println("Unsupported operation in AWCPrototypeCatalog.addGetRemoteAssociationClause()");
                            }
                        }
                        aComposer.addFromVariable(newFromVar);
                        aComposer.addFixedString(new FixedString("." + args[i][j] + ")"));
                    } else {
                        aComposer.addFixedString(new FixedString(" and ("));
                        aComposer.addFromVariable(newFromVar);
                        aComposer.addFixedString(new FixedString("." + args[i][j] + " " + args[i][j+1] + ")"));
                    }
                }
                if (i < args.length - 1) {
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + newForeignKeyName + " = "));
                } else {
                    // do the chim test
                    if (chimFromVar1 != null && chimFromVar2 != null) {
                        aComposer.addFixedString(new FixedString(" and (not exists (((SELECT region_ii, foreground_app_ii FROM Virus_Chimeric_FG tmpFG WHERE tmpFG.region_ii <> "));
                        aComposer.addFromVariable(chimFGFromVar1);
                        aComposer.addFixedString(new FixedString(".region_ii AND tmpFG.VIRUS_APP_II = "));
                        aComposer.addFromVariable(chimFromVar1);
                        aComposer.addFixedString(new FixedString(".VIRUS_APP_II) MINUS (SELECT region_ii, foreground_app_ii FROM Virus_Chimeric_FG tmpFG WHERE tmpFG.region_ii <> "));
                        aComposer.addFromVariable(chimFGFromVar2);
                        aComposer.addFixedString(new FixedString(".region_ii AND tmpFG.VIRUS_APP_II = "));
                        aComposer.addFromVariable(chimFromVar2);
                        aComposer.addFixedString(new FixedString(".VIRUS_APP_II)) UNION ALL ((SELECT region_ii, foreground_app_ii FROM Virus_Chimeric_FG tmpFG WHERE tmpFG.region_ii <> "));
                        aComposer.addFromVariable(chimFGFromVar2);
                        aComposer.addFixedString(new FixedString(".region_ii AND tmpFG.VIRUS_APP_II = "));
                        aComposer.addFromVariable(chimFromVar2);
                        aComposer.addFixedString(new FixedString(".VIRUS_APP_II) MINUS (SELECT region_ii, foreground_app_ii FROM Virus_Chimeric_FG tmpFG WHERE tmpFG.region_ii <> "));
                        aComposer.addFromVariable(chimFGFromVar1);
                        aComposer.addFixedString(new FixedString(".region_ii AND tmpFG.VIRUS_APP_II = "));
                        aComposer.addFromVariable(chimFromVar1);
                        aComposer.addFixedString(new FixedString(".VIRUS_APP_II))))"));
                    }
                    String newSingularName = getTable(newTableName).getSingularName();
                    if (description == null) {
                        aVisList.addFixedString(new FixedString("has an associated " + newSingularName));
                    } else {
                        aVisList.addFixedString(new FixedString(description));
                    }
                    OutputVariable ovar = new OutputVariable(new VariableType(newTableName), getGoodVariableName(newTableName));
                    ovar.setUniqueName(ovar.getFormalName());
                    aVisList.addOutputVariable(ovar);
                    ovar.getExpression().addFromVariable(newFromVar);
                }
            }
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
    
    public AtomicWhereClause addGetDoubleRemoteAssociationClause(String[][] assocs1, String[][] assocs2, String description) {
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
            HashMap tmpMap = new HashMap(); // for storing parts of expressions that come from different parts of the query
            String tableName1 = assocs1[0][0];
            String keyName1 = assocs1[0][1];
            String foreignKeyName1 = assocs1[0][2];
            String singularName1 = getTable(tableName1).getSingularName();
            FromVariable lastFromVar1 = null;
            String lastKeyName1 = null;
            aVisList.addFixedString(new FixedString("The " + singularName1));
            InputVariable ivar1 = new InputVariable(new VariableType(tableName1));
            aVisList.addInputVariable(ivar1);
            for (int j = 3; j < assocs1[0].length; j = j + 2) { // checks on this table
                if (assocs1[0][j+1].startsWith("?")) {
                    String keyString = assocs1[0][j+1].substring(1, assocs1[0][j+1].indexOf(' '));
                    //System.err.println(keyString);
                    ArrayList expressionList = new ArrayList();
                    expressionList.add(ivar1);
                    expressionList.add(new FixedString("." + assocs1[0][j] + assocs1[0][j+1].substring(assocs1[0][j+1].indexOf(' ') + 1)));
                    tmpMap.put(keyString, expressionList);
                }
            }
            aComposer.addFixedString(new FixedString("("));
            aComposer.addInputVariable(ivar1);
            aComposer.addFixedString(new FixedString("." + foreignKeyName1 + " = "));
            for (int i = 1; i < assocs1.length; i++) {
                String newTableName = assocs1[i][0];
                String newKeyName = assocs1[i][1];
                String newForeignKeyName = assocs1[i][2];
                FromVariable newFromVar = new FromVariable(newTableName);
                lastFromVar1 = newFromVar;
                lastKeyName1 = newForeignKeyName;
                aComposer.addFromVariable(newFromVar);
                aComposer.addFixedString(new FixedString("." + newKeyName + ")"));
                for (int j = 3; j < assocs1[i].length; j = j + 2) { // checks on this table
                    if (assocs1[i][j+1].startsWith("?")) {
                        String keyString = assocs1[i][j+1].substring(1, assocs1[i][j+1].indexOf(' '));
                        //System.err.println(keyString);
                        ArrayList expressionList = new ArrayList();
                        expressionList.add(newFromVar);
                        expressionList.add(new FixedString("." + assocs1[i][j] + assocs1[i][j+1].substring(assocs1[i][j+1].indexOf(' ') + 1)));
                        tmpMap.put(keyString, expressionList);
                    } else if (assocs1[i][j+1].startsWith("!")) {
                        String keyString = assocs1[i][j+1].substring(1);
                        //System.err.println(keyString);
                        ArrayList expressionList = (ArrayList)tmpMap.get(keyString);
                        aComposer.addFixedString(new FixedString(" and ("));
                        Iterator iter = expressionList.iterator();
                        while (iter.hasNext()) {
                            AWCWord word = (AWCWord)iter.next();
                            if (word instanceof FromVariable) {
                                aComposer.addFromVariable((FromVariable)word);
                            } else if (word instanceof FixedString) {
                                aComposer.addFixedString((FixedString)word);
                            } else if (word instanceof InputVariable) {
                                aComposer.addInputVariable((InputVariable)word);
                            } else {
                                System.err.println("Unsupported operation in AWCPrototypeCatalog.addGetRemoteAssociationClause()");
                            }
                        }
                        aComposer.addFromVariable(newFromVar);
                        aComposer.addFixedString(new FixedString("." + assocs1[i][j] + ")"));
                    } else {
                        aComposer.addFixedString(new FixedString(" and ("));
                        aComposer.addFromVariable(newFromVar);
                        aComposer.addFixedString(new FixedString("." + assocs1[i][j] + " " + assocs1[i][j+1] + ")"));
                    }
                }
                if (i < assocs1.length - 1) {
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + newForeignKeyName + " = "));
                }
            }
            String tableName2 = assocs2[0][0];
            String keyName2 = assocs2[0][1];
            String foreignKeyName2 = assocs2[0][2];
            String singularName2 = getTable(tableName2).getSingularName();
            aVisList.addFixedString(new FixedString("and the " + singularName2));
            InputVariable ivar2 = new InputVariable(new VariableType(tableName2));
            aVisList.addInputVariable(ivar2);
            for (int j = 3; j < assocs2[0].length; j = j + 2) { // checks on this table
                if (assocs2[0][j+1].startsWith("?")) {
                    String keyString = assocs2[0][j+1].substring(1, assocs2[0][j+1].indexOf(' '));
                    //System.err.println(keyString);
                    ArrayList expressionList = new ArrayList();
                    expressionList.add(ivar2);
                    expressionList.add(new FixedString("." + assocs2[0][j] + assocs2[0][j+1].substring(assocs2[0][j+1].indexOf(' ') + 1)));
                    tmpMap.put(keyString, expressionList);
                }
            }
            aComposer.addFixedString(new FixedString(" and ("));
            aComposer.addInputVariable(ivar2);
            aComposer.addFixedString(new FixedString("." + foreignKeyName2 + " = "));
            for (int i = 1; i < assocs2.length; i++) {
                String newTableName = assocs2[i][0];
                String newKeyName = assocs2[i][1];
                String newForeignKeyName = assocs2[i][2];
                FromVariable newFromVar = new FromVariable(newTableName);
                aComposer.addFromVariable(newFromVar);
                aComposer.addFixedString(new FixedString("." + newKeyName + ")"));
                for (int j = 3; j < assocs2[i].length; j = j + 2) { // checks on this table
                    if (assocs2[i][j+1].startsWith("?")) {
                        String keyString = assocs2[i][j+1].substring(1, assocs2[i][j+1].indexOf(' '));
                        //System.err.println(keyString);
                        ArrayList expressionList = new ArrayList();
                        expressionList.add(newFromVar);
                        expressionList.add(new FixedString("." + assocs2[i][j] + assocs2[i][j+1].substring(assocs2[i][j+1].indexOf(' ') + 1)));
                        tmpMap.put(keyString, expressionList);
                    } else if (assocs2[i][j+1].startsWith("!")) {
                        String keyString = assocs2[i][j+1].substring(1);
                        //System.err.println(keyString);
                        ArrayList expressionList = (ArrayList)tmpMap.get(keyString);
                        aComposer.addFixedString(new FixedString(" and ("));
                        Iterator iter = expressionList.iterator();
                        while (iter.hasNext()) {
                            AWCWord word = (AWCWord)iter.next();
                            if (word instanceof FromVariable) {
                                aComposer.addFromVariable((FromVariable)word);
                            } else if (word instanceof FixedString) {
                                aComposer.addFixedString((FixedString)word);
                            } else if (word instanceof InputVariable) {
                                aComposer.addInputVariable((InputVariable)word);
                            } else {
                                System.err.println("Unsupported operation in AWCPrototypeCatalog.addGetRemoteAssociationClause()");
                            }
                        }
                        aComposer.addFromVariable(newFromVar);
                        aComposer.addFixedString(new FixedString("." + assocs2[i][j] + ")"));
                    } else {
                        aComposer.addFixedString(new FixedString(" and ("));
                        aComposer.addFromVariable(newFromVar);
                        aComposer.addFixedString(new FixedString("." + assocs2[i][j] + " " + assocs2[i][j+1] + ")"));
                    }
                }
                aComposer.addFixedString(new FixedString(" and ("));
                aComposer.addFromVariable(newFromVar);
                aComposer.addFixedString(new FixedString("." + newForeignKeyName + " = "));
                if (i >= assocs2.length - 1) {
                    aComposer.addFromVariable(lastFromVar1);
                    aComposer.addFixedString(new FixedString("." + lastKeyName1 + ")"));
                    
                    aVisList.addFixedString(new FixedString(description));
                    OutputVariable ovar = new OutputVariable(new VariableType(newTableName), getGoodVariableName(newTableName));
                    ovar.setUniqueName(ovar.getFormalName());
                    aVisList.addOutputVariable(ovar);
                    ovar.getExpression().addFromVariable(newFromVar);
                }
            }
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
    
    
        
        public AtomicWhereClause addCodedPropertyCheckClause(String tableName, String codeName, String codeTableName, String codeKeyName, String propertyName, String description, boolean show) {
            JDBCManager manager = JDBCManager.getInstance();
            if (manager != null) {
                String dataTypeString = manager.getColumnType(codeTableName, propertyName);
                if (dataTypeString != null) {
                    int dataType = Integer.parseInt(dataTypeString);
                    VariableType varType;
                    AtomicWhereClause aClause = new AtomicWhereClause();
                    VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
                    HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
                    String singularName = getTable(tableName).getSingularName();
                    aVisList.addFixedString(new FixedString("The " + singularName));
                    InputVariable ivar = new InputVariable(new VariableType(tableName));
                    aVisList.addInputVariable(ivar);
                    if (description == null) {
                        aVisList.addFixedString(new FixedString("has " + codeName));
                    } else {
                        aVisList.addFixedString(new FixedString(description));
                    }
                    
                    Constant valueConstant = null;
                    if (dataType == 12) {
                        valueConstant = new StringConstant();
                        varType = new VariableType("String");
                    } else if ((dataType >= 91) && (dataType <= 93)) {
                        valueConstant = new DateConstant();
                        varType = new VariableType("Date");
                    } else if (((8 >= dataType) && (dataType >=1)) || dataType == 1111) {
                        valueConstant = new DoubleConstant();
                        varType = new VariableType("Numeric");
                    } else {
                        System.err.println("Unknown data type found for " + codeTableName + "." + propertyName + ": " + dataType);
                        return null;
                    }
                    if (show) {
                        OutputVariable ovar = new OutputVariable(varType, getGoodVariableName(codeName));
                        ovar.setUniqueName(ovar.getFormalName());
                        ovar.getExpression().addConstant(valueConstant);
                        aVisList.addOutputVariable(ovar);
                    }
                    if (description == null) {
                        aVisList.addFixedString(new FixedString("which decodes to"));
                    } else if (show) {
                        aVisList.addFixedString(new FixedString("="));
                    }
                    aVisList.addConstant(valueConstant);
                    
                    aComposer.addFixedString(new FixedString("("));
                    aComposer.addInputVariable(ivar);
                    aComposer.addFixedString(new FixedString("." + codeName + " = "));
                    FromVariable codeVar = new FromVariable(codeTableName);
                    aComposer.addFromVariable(codeVar);
                    aComposer.addFixedString(new FixedString("." + codeKeyName + ") and ("));
                    aComposer.addFromVariable(codeVar);
                    aComposer.addFixedString(new FixedString("." + propertyName + " = "));
                    aComposer.addConstant(valueConstant);
                    aComposer.addFixedString(new FixedString(")"));
                    
                    addAtomicWhereClause(aClause);
                    System.out.println(aVisList.getHumanStringValue());
                    System.out.println(aComposer.getHumanStringValue());
                    return aClause;
                } else {
                    System.err.println("Unknown column " + propertyName + " for " + tableName);
                    return null;
                }
            } else {
                return null;
            }
        }
        
    
    
/*    
    public AtomicWhereClause addTherapyNaiveVirusClause() {
        String[][] assocs = {{"Patient", null, "PATIENT_II"}, {"PATIENT_SAMPLE", "PATIENT_II", "PATIENT_SAMPLE_ID"},
        {"Viral_Clin_Isolate", "CLINICAL_ISOLATE_ID", "VIRAL_CLIN_ISOLATE_II"}, {"Virus_app_clinical", "VIRAL_CLIN_ISOLATE_II", "VIRUS_APP_II"},
        {"Virus_Appearance", "VIRUS_APP_II", null}};
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
            HashMap tmpMap = new HashMap(); // for storing parts of expressions that come from different parts of the query
            FromVariable dateFromVar = null;
            String tableName = assocs[0][0];
            String keyName = assocs[0][1];
            String foreignKeyName = assocs[0][2];
            String singularName = getTable(tableName).getSingularName();
            aVisList.addFixedString(new FixedString("The " + singularName));
            InputVariable ivar = new InputVariable(new VariableType(tableName));
            aVisList.addInputVariable(ivar);
            
            aComposer.addFixedString(new FixedString("("));
            aComposer.addInputVariable(ivar);
            aComposer.addFixedString(new FixedString("." + foreignKeyName + " = "));
            for (int i = 1; i < assocs.length; i++) {
                String newTableName = assocs[i][0];
                String newKeyName = assocs[i][1];
                String newForeignKeyName = assocs[i][2];
                FromVariable newFromVar = new FromVariable(newTableName);
                aComposer.addFromVariable(newFromVar);
                aComposer.addFixedString(new FixedString("." + newKeyName + ")"));
                for (int j = 3; j < assocs[i].length; j = j + 2) { // checks on this table
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + assocs[i][j] + " " + assocs[i][j+1] + ")"));
                }
                if (newTableName.equalsIgnoreCase("Viral_Clin_Isolate")) {
                    dateFromVar = newFromVar;
                }
                if (i < assocs.length - 1) {
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + newForeignKeyName + " = "));
                } else {
                    aComposer.addFixedString(new FixedString(" and (not exists (SELECT med.* FROM PATIENT_MEDICATION med WHERE med.PATIENT_II = "));
                    aComposer.addInputVariable(ivar);
                    aComposer.addFixedString(new FixedString(".PATIENT_II AND med.SYMPTOM_II = 70 AND med.BEGIN_DATE < "));
                    aComposer.addFromVariable(dateFromVar);
                    aComposer.addFixedString(new FixedString(".ESTIMATED_TAKE_DATE))"));
                    
                    aVisList.addFixedString(new FixedString("has provided a therapy-naive virus appearance"));
                    OutputVariable ovar = new OutputVariable(new VariableType(newTableName), getGoodVariableName(newTableName));
                    ovar.setUniqueName(ovar.getFormalName());
                    aVisList.addOutputVariable(ovar);
                    ovar.getExpression().addFromVariable(newFromVar);
                }
            }
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
    */
    
    public AtomicWhereClause addPropertyEqualsClause(String tableName1, String propertyName1) {
        return addPropertyEqualsClause(tableName1, propertyName1, tableName1, propertyName1);
    }
    
    public AtomicWhereClause addPropertyEqualsClause(String tableName1, String propertyName1, String tableName2, String propertyName2) {
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            String dataTypeString1 = manager.getColumnType(tableName1, propertyName1);
            String dataTypeString2 = manager.getColumnType(tableName2, propertyName2);
            if ((dataTypeString1 != null) && (dataTypeString1 != null)) {
                AtomicWhereClause aClause = new AtomicWhereClause();
                VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
                HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
                InputVariable ivar1 = new InputVariable(new VariableType(tableName1));
                InputVariable ivar2 = new InputVariable(new VariableType(tableName2));
                aVisList.addInputVariable(ivar1);
                aVisList.addFixedString(new FixedString("'s " + propertyName1 + " ="));
                aVisList.addInputVariable(ivar2);
                aVisList.addFixedString(new FixedString("'s " + propertyName2));
                
                aComposer.addInputVariable(ivar1);
                aComposer.addFixedString(new FixedString("." + propertyName1 + " = "));
                aComposer.addInputVariable(ivar2);
                aComposer.addFixedString(new FixedString("." + propertyName2));
                
                addAtomicWhereClause(aClause);
                return aClause;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    public AtomicWhereClause addPropertyTimeIntervalClause(String tableName, String propertyName, String description, boolean show) {
    	JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            String dataTypeString = manager.getColumnType(tableName, propertyName);
            if (dataTypeString != null) {
                int dataType = Integer.parseInt(dataTypeString);
                if ((dataType >= 91) && (dataType <= 93)) {
                    AtomicWhereClause aClause = new AtomicWhereClause();
                    VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
                    HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
                    String singularName = getTable(tableName).getSingularName();
                    aVisList.addFixedString(new FixedString("The " + singularName));
                    InputVariable ivar = new InputVariable(new VariableType(tableName));
                    aVisList.addInputVariable(ivar);

                    if (description == null) {
                        aVisList.addFixedString(new FixedString("has " + propertyName));
                    } else {
                        aVisList.addFixedString(new FixedString(description));
                    }
                    OutputVariable ovar = null;
                    if (show) {
	                    ovar = new OutputVariable(new VariableType("Date"), getGoodVariableName(propertyName));
	                    ovar.setUniqueName(ovar.getFormalName());
	                    ovar.getExpression().addInputVariable(ivar);
	                    ovar.getExpression().addFixedString(new FixedString("." + propertyName));
	                    aVisList.addOutputVariable(ovar);
                    }
                    aVisList.addFixedString(new FixedString("between"));
                    Constant valueConstant1 = new DateConstant("1900-01-01");
                    aVisList.addConstant(valueConstant1);
                    aVisList.addFixedString(new FixedString("and"));
                    Constant valueConstant2 = new DateConstant();
                    aVisList.addConstant(valueConstant2);
                    
                    
                    aComposer.addFixedString(new FixedString("("));
                    if (show) {
                    	aComposer.addOutputVariable(ovar);
                    }
                    else {
                        aComposer.addInputVariable(ivar);
                        aComposer.addFixedString(new FixedString("." + propertyName));
                    }
                    aComposer.addFixedString(new FixedString(" > "));
                    aComposer.addConstant(valueConstant1);
                    aComposer.addFixedString(new FixedString(") AND ("));
                    
                    if (show) {
                    	aComposer.addOutputVariable(ovar);
                    }
                    else {
                        aComposer.addInputVariable(ivar);
                        aComposer.addFixedString(new FixedString("." + propertyName));
                    }
                    aComposer.addFixedString(new FixedString(" < "));
                    aComposer.addConstant(valueConstant2);
                    aComposer.addFixedString(new FixedString(")"));
                    
                    addAtomicWhereClause(aClause);
                    return aClause;
                } else {
                    System.err.println("Wrong data type found for " + tableName + "." + propertyName + ": " + dataType);
                    return null;
                }
            } else {
                System.err.println("Unknown column " + propertyName + " for " + tableName);
                return null;
            }
        } else {
            return null;
        }
    }
    
    public AtomicWhereClause addPropertyTimeIntervalClause(String tableName, String propertyName) {
    	return addPropertyTimeIntervalClause(tableName, propertyName, null, true);
    }
    
    public AtomicWhereClause addTimeConstantClause(boolean before) {
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
            aVisList.addFixedString(new FixedString("Date"));
            InputVariable ivar = new InputVariable(new VariableType("Date"));
            aVisList.addInputVariable(ivar);
            aVisList.addFixedString(new FixedString(before ? "is before" : "is after"));
            Constant valueConstant = new DateConstant();
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
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
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
            aComposer.addFixedString(new FixedString(") and ("));
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
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
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
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
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
    
    public AtomicWhereClause addTimeCalculationClause(boolean plus) {
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
            aVisList.addFixedString(new FixedString("Date "));
            InputVariable ivar = new InputVariable(new VariableType("Date"));
            
            OutputVariable ovar = new OutputVariable(new VariableType("Date"), getGoodVariableName("Date"));
            ovar.setUniqueName(ovar.getFormalName());
            ovar.getExpression().addInputVariable(ivar);
            ovar.getExpression().addFixedString(new FixedString(plus ? " + " : " - "));
            DoubleConstant timeConstant = new DoubleConstant();
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
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
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
    
/*    
    public AtomicWhereClause addRealValueWithRelationConstraintClause(String tableName, String valueFieldName, String relationFieldName, boolean below) {
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
            aVisList.addFixedString(new FixedString("The " + tableName));
            InputVariable ivar = new InputVariable(new VariableType(tableName));
            aVisList.addInputVariable(ivar);
            aVisList.addFixedString(new FixedString("has a real value which is certainly " + (below ? "at most" : "at least")));
            Constant valueConstant = new DoubleConstant();
            aVisList.addConstant(valueConstant);
            
            aComposer.addFixedString(new FixedString("("));
            aComposer.addInputVariable(ivar);
            aComposer.addFixedString(new FixedString("." + valueFieldName + (below ? " <= " : " >= ")));
            aComposer.addConstant(valueConstant);
            aComposer.addFixedString(new FixedString(") and ("));
            aComposer.addInputVariable(ivar);
            aComposer.addFixedString(new FixedString("." + relationFieldName + " IN " + (below ? "('<', '=', '')" : "('>', '=', '')") + ")"));
            
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
    
    public AtomicWhereClause addConvertMicrogramsToMillimolarityClause() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
        
        aVisList.addFixedString(new FixedString("The ug/ml value of result"));
        InputVariable ivar = new InputVariable(new VariableType("Result"));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString("can be approximated in mM as "));
        OutputVariable ovar = new OutputVariable(new VariableType("Numeric"), "Concentr");
        ovar.setUniqueName(ovar.getFormalName());
        aVisList.addOutputVariable(ovar);
        
        aComposer.addFixedString(new FixedString("("));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".ELEM_TEST_II = "));
        FromVariable elemTestVar = new FromVariable("COD_ELEM_TEST");
        aComposer.addFromVariable(elemTestVar);
        aComposer.addFixedString(new FixedString(".ELEM_TEST_II) and ("));
        aComposer.addFromVariable(elemTestVar);
        aComposer.addFixedString(new FixedString(".UNIT_II = "));
        FromVariable unitVar = new FromVariable("COD_UNIT");
        aComposer.addFromVariable(unitVar);
        aComposer.addFixedString(new FixedString(".UNIT_II) and ("));
        aComposer.addFromVariable(unitVar);
        aComposer.addFixedString(new FixedString(".UNIT_II = 1) and ("));
        aComposer.addFromVariable(unitVar);
        aComposer.addFixedString(new FixedString(".NAME = 'ug/mL') and ("));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".MEASUREMENT_II = "));
        FromVariable measurementVar = new FromVariable("Measurement");
        aComposer.addFromVariable(measurementVar);
        aComposer.addFixedString(new FixedString(".MEASUREMENT_II) and ("));
        aComposer.addFromVariable(measurementVar);
        aComposer.addFixedString(new FixedString(".SAMPLE_II = "));
        FromVariable sampleVar = new FromVariable("Sample");
        aComposer.addFromVariable(sampleVar);
        aComposer.addFixedString(new FixedString(".SAMPLE_II) and ("));
        aComposer.addFromVariable(sampleVar);
        aComposer.addFixedString(new FixedString(".DRUG_STOCK_II = "));
        FromVariable drugStockVar = new FromVariable("Drug_Stock");
        aComposer.addFromVariable(drugStockVar);
        aComposer.addFixedString(new FixedString(".DRUG_STOCK_II) and ("));
        aComposer.addFromVariable(drugStockVar);
        aComposer.addFixedString(new FixedString(".DRUG_COMPOUND_II = "));
        FromVariable drugCompoundVar = new FromVariable("Drug_Compound");
        aComposer.addFromVariable(drugCompoundVar);
        aComposer.addFixedString(new FixedString(".DRUG_COMPOUND_II) and ("));
        aComposer.addFromVariable(drugCompoundVar);
        // Actually, this should better be done via MOLECULEID instead of the NAMEs, but the database fields are not okay for that now.
        aComposer.addFixedString(new FixedString(".MOLECULEID = "));
        FromVariable moleculeVar = new FromVariable("Molecules");
        aComposer.addFromVariable(moleculeVar);
        aComposer.addFixedString(new FixedString(".MOLECULEID) and ("));
        aComposer.addFromVariable(moleculeVar);
        aComposer.addFixedString(new FixedString(".RELATIVEMASS IS NOT NULL)"));
        
        ovar.getExpression().addFixedString(new FixedString("("));
        ovar.getExpression().addInputVariable(ivar);
        ovar.getExpression().addFixedString(new FixedString(".REAL_VALUE / "));
        ovar.getExpression().addFromVariable(moleculeVar);
        ovar.getExpression().addFixedString(new FixedString(".RELATIVEMASS)"));
        
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
//    public MoleculeClause addMoleculeClause() {
//        MoleculeClause aClause = new MoleculeClause(MoleculeClause.MATCH_SUBSTRUCTURE_OCCURRENCE);
//        addAtomicWhereClause(aClause);
//        return aClause;
//    }
    
    public AtomicWhereClause addSequenceMutationClause(String sequence, String seqid1, String mutatype, String seqid2, String posname, String description, String prep, boolean real) {
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
            aVisList.addFixedString(new FixedString("The sequence"));
            InputVariable ivar = new InputVariable(new VariableType(sequence));
            FromVariable mutavar = new FromVariable(mutatype);
            aVisList.addInputVariable(ivar);
            if (description == null) {
                aVisList.addFixedString(new FixedString("has a " + mutatype));
            } else {
                aVisList.addFixedString(new FixedString(description));
            }
            OutputVariable ovar = new OutputVariable(new VariableType(mutatype), getGoodVariableName(mutatype));
            ovar.setUniqueName(ovar.getFormalName());
            ovar.getExpression().addFromVariable(mutavar);
            aVisList.addOutputVariable(ovar);
            aVisList.addFixedString(new FixedString(((prep == null) ? "in" : prep) + " position"));
            Constant pos = new DoubleConstant();
            aVisList.addConstant(pos);
            aComposer.addFixedString(new FixedString("("));
            aComposer.addInputVariable(ivar);
            aComposer.addFixedString(new FixedString("." + seqid1 + " = "));
            aComposer.addFromVariable(mutavar);
            aComposer.addFixedString(new FixedString("." + seqid2 + ") AND ("));
            aComposer.addFromVariable(mutavar);
            aComposer.addFixedString(new FixedString("." + posname + " = "));
            aComposer.addConstant(pos);
            aComposer.addFixedString(new FixedString(")"));
            if (real && mutatype.equals("SEQUENCE_AA_MUTATION")) { // real switch only useful for amino acids, and only for mutations
                aComposer.addFixedString(new FixedString(" AND ("));
                aComposer.addFromVariable(mutavar);
                aComposer.addFixedString(new FixedString(".AA_WILDTYPE != "));
                aComposer.addFromVariable(mutavar);
                aComposer.addFixedString(new FixedString(".AA_MUTATION)"));
            }
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
    
    // specific named clauses
    
    public AtomicWhereClause addPatientPosTestClause() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
        aVisList.addFixedString(new FixedString("The patient"));
        InputVariable ivar = new InputVariable(new VariableType("Patient"));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString("has a first positive HIV test on date"));
        OutputVariable ovar = new OutputVariable(new VariableType("Date"), getGoodVariableName("Date"));
        ovar.setUniqueName(ovar.getFormalName());
        ovar.getExpression().addInputVariable(ivar);
        ovar.getExpression().addFixedString(new FixedString(".DATE_1ST_POS_TEST"));
        aVisList.addOutputVariable(ovar);
        aComposer.addFixedString(new FixedString("1=1"));
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    public AtomicWhereClause addHIVMedicationClause() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
        aVisList.addFixedString(new FixedString("The medication"));
        InputVariable ivar = new InputVariable(new VariableType("PATIENT_MEDICATION"));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString("is HIV medication."));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".SYMPTOM_II = 70"));
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    public AtomicWhereClause addClinicalTestClause() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
        aVisList.addFixedString(new FixedString("The test"));
        InputVariable ivar = new InputVariable(new VariableType("COD_ELEM_TEST"));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString("is an elementary clinical test"));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".ELEM_TEST_II IN (67, 68, 92, 71, 72, 88, 90, 93, 75)"));
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    public AtomicWhereClause addViralLoadTestClause() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
        aVisList.addFixedString(new FixedString("The test"));
        InputVariable ivar = new InputVariable(new VariableType("COD_ELEM_TEST"));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString("is an elementary viral load test"));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".ELEM_TEST_II IN (67, 68, 92)"));
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    public AtomicWhereClause addCD4TestClause() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
        aVisList.addFixedString(new FixedString("The test"));
        InputVariable ivar = new InputVariable(new VariableType("COD_ELEM_TEST"));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString("is an elementary CD4 count test"));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".ELEM_TEST_II IN (71, 72, 88, 90, 93, 75)"));
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    public AtomicWhereClause addClinicalCalcTestClause() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
        aVisList.addFixedString(new FixedString("The test"));
        InputVariable ivar = new InputVariable(new VariableType("COD_CALC_TEST"));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString("is a calculated clinical test"));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".CALC_TEST_II IN (7, 36, 37, 34)"));
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    public AtomicWhereClause addViralLoadCalcTestClause() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
        aVisList.addFixedString(new FixedString("The test"));
        InputVariable ivar = new InputVariable(new VariableType("COD_CALC_TEST"));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString("is a calculated viral load test"));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".CALC_TEST_II = 7"));
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    public AtomicWhereClause addCD4CalcTestClause() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
        aVisList.addFixedString(new FixedString("The test"));
        InputVariable ivar = new InputVariable(new VariableType("COD_CALC_TEST"));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString("is a calculated CD4 test"));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".CALC_TEST_II = (36, 37, 34)"));
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    public AtomicWhereClause addCombinationTherapyCheckClause() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
        aVisList.addFixedString(new FixedString("The patient"));
        InputVariable ivar = new InputVariable(new VariableType("Patient"));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString("has received a combination therapy consisting of"));
        
        StringConstant constant1 = new StringConstant();
        constant1.setSuggestedValuesQuery("SELECT DISTINCT NAME FROM COD_MEDICATION");
        constant1.setSuggestedValuesMandatory(true);
        aVisList.addConstant(constant1);
        FromVariable medicVar1 = new FromVariable("PATIENT_MEDICATION");
        FromVariable codVar1 = new FromVariable("COD_MEDICATION");
        OutputVariable ovar1 = new OutputVariable(new VariableType("PATIENT_MEDICATION"), getGoodVariableName("PATIENT_MEDICATION"));
        ovar1.setUniqueName(ovar1.getFormalName());
        ovar1.getExpression().addFromVariable(medicVar1);
        aClause.addFromVariable(medicVar1);
        aVisList.addFixedString(new FixedString("("));
        aVisList.addOutputVariable(ovar1);
        aVisList.addFixedString(new FixedString("),"));
        
        StringConstant constant2 = new StringConstant();
        constant2.setSuggestedValuesQuery("SELECT DISTINCT NAME FROM COD_MEDICATION");
        constant2.setSuggestedValuesMandatory(true);
        aVisList.addConstant(constant2);
        FromVariable medicVar2 = new FromVariable("PATIENT_MEDICATION");
        FromVariable codVar2 = new FromVariable("COD_MEDICATION");
        OutputVariable ovar2 = new OutputVariable(new VariableType("PATIENT_MEDICATION"), getGoodVariableName("PATIENT_MEDICATION"));
        ovar2.setUniqueName(ovar2.getFormalName());
        ovar2.getExpression().addFromVariable(medicVar2);
        aClause.addFromVariable(medicVar2);
        aVisList.addFixedString(new FixedString("("));
        aVisList.addOutputVariable(ovar2);
        aVisList.addFixedString(new FixedString(") and"));
        
        StringConstant constant3 = new StringConstant();
        constant3.setSuggestedValuesQuery("SELECT DISTINCT NAME FROM COD_MEDICATION");
        constant3.setSuggestedValuesMandatory(true);
        aVisList.addConstant(constant3);
        FromVariable medicVar3 = new FromVariable("PATIENT_MEDICATION");
        FromVariable codVar3 = new FromVariable("COD_MEDICATION");
        OutputVariable ovar3 = new OutputVariable(new VariableType("PATIENT_MEDICATION"), getGoodVariableName("PATIENT_MEDICATION"));
        ovar3.setUniqueName(ovar3.getFormalName());
        ovar3.getExpression().addFromVariable(medicVar3);
        aClause.addFromVariable(medicVar3);
        aVisList.addFixedString(new FixedString("("));
        aVisList.addOutputVariable(ovar3);
        aVisList.addFixedString(new FixedString(")"));
        
        aComposer.addFixedString(new FixedString("("));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".PATIENT_II = "));
        aComposer.addOutputVariable(ovar1);
        aComposer.addFixedString(new FixedString(".PATIENT_II) AND ("));
        aComposer.addOutputVariable(ovar1);
        aComposer.addFixedString(new FixedString(".MEDICATION_II = "));
        aComposer.addFromVariable(codVar1);
        aComposer.addFixedString(new FixedString(".MEDICATION_II) AND ("));
        aComposer.addFromVariable(codVar1);
        aComposer.addFixedString(new FixedString(".NAME = "));
        aComposer.addConstant(constant1);
        aComposer.addFixedString(new FixedString(") AND ("));
        
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".PATIENT_II = "));
        aComposer.addOutputVariable(ovar2);
        aComposer.addFixedString(new FixedString(".PATIENT_II) AND ("));
        aComposer.addOutputVariable(ovar2);
        aComposer.addFixedString(new FixedString(".MEDICATION_II = "));
        aComposer.addFromVariable(codVar2);
        aComposer.addFixedString(new FixedString(".MEDICATION_II) AND ("));
        aComposer.addFromVariable(codVar2);
        aComposer.addFixedString(new FixedString(".NAME = "));
        aComposer.addConstant(constant2);
        aComposer.addFixedString(new FixedString(") AND ("));
        
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".PATIENT_II = "));
        aComposer.addOutputVariable(ovar3);
        aComposer.addFixedString(new FixedString(".PATIENT_II) AND ("));
        aComposer.addOutputVariable(ovar3);
        aComposer.addFixedString(new FixedString(".MEDICATION_II = "));
        aComposer.addFromVariable(codVar3);
        aComposer.addFixedString(new FixedString(".MEDICATION_II) AND ("));
        aComposer.addFromVariable(codVar3);
        aComposer.addFixedString(new FixedString(".NAME = "));
        aComposer.addConstant(constant3);
        aComposer.addFixedString(new FixedString(") AND ("));
        
        aComposer.addOutputVariable(ovar1);
        aComposer.addFixedString(new FixedString(".BEGIN_DATE = "));
        aComposer.addOutputVariable(ovar2);
        aComposer.addFixedString(new FixedString(".BEGIN_DATE) AND ("));
        aComposer.addOutputVariable(ovar1);
        aComposer.addFixedString(new FixedString(".BEGIN_DATE = "));
        aComposer.addOutputVariable(ovar3);
        aComposer.addFixedString(new FixedString(".BEGIN_DATE)"));
        
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    public AtomicWhereClause addMedicationChangeClauseInputDates() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
        aVisList.addFixedString(new FixedString("The patient"));
        InputVariable ivar = new InputVariable(new VariableType("Patient"));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString("has a medication"));
        
        aComposer.addFixedString((new FixedString("(")));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString((new FixedString(".PATIENT_II = ")));
        FromVariable medicVar = new FromVariable("PATIENT_MEDICATION");
        OutputVariable ovar = new OutputVariable(new VariableType("PATIENT_MEDICATION"), getGoodVariableName("PATIENT_MEDICATION"));
        ovar.setUniqueName(ovar.getFormalName());
        ovar.getExpression().addFromVariable(medicVar);
        aVisList.addOutputVariable(ovar);
        aVisList.addFixedString(new FixedString("which changes between"));
        
        InputVariable ivarDate1 = new InputVariable(new VariableType("Date"));
        aVisList.addInputVariable(ivarDate1);
        aVisList.addFixedString(new FixedString("and"));
        InputVariable ivarDate2 = new InputVariable(new VariableType("Date"));
        aVisList.addInputVariable(ivarDate2);
        
        aComposer.addFixedString((new FixedString("(")));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString((new FixedString(".PATIENT_II = ")));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString((new FixedString(".PATIENT_II ) AND (((")));
        aComposer.addInputVariable(ivarDate1);
        aComposer.addFixedString((new FixedString(" < ")));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString((new FixedString(".BEGIN_DATE ) AND (")));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString((new FixedString(".BEGIN_DATE < ")));
        aComposer.addInputVariable(ivarDate2);
        aComposer.addFixedString((new FixedString(" )) OR (( ")));
        aComposer.addInputVariable(ivarDate1);
        aComposer.addFixedString((new FixedString(" < ")));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString((new FixedString(".END_DATE ) AND (")));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString((new FixedString(".END_DATE < ")));
        aComposer.addInputVariable(ivarDate2);
        aComposer.addFixedString((new FixedString(" )))")));
        
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    public AtomicWhereClause addMedicationChangeClauseConstantDates() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
        aVisList.addFixedString(new FixedString("The patient"));
        InputVariable ivar = new InputVariable(new VariableType("Patient"));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString("has a medication"));
        
        aComposer.addFixedString((new FixedString("(")));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString((new FixedString(".PATIENT_II = ")));
        FromVariable medicVar = new FromVariable("PATIENT_MEDICATION");
        OutputVariable ovar = new OutputVariable(new VariableType("PATIENT_MEDICATION"), getGoodVariableName("PATIENT_MEDICATION"));
        ovar.setUniqueName(ovar.getFormalName());
        ovar.getExpression().addFromVariable(medicVar);
        aVisList.addOutputVariable(ovar);
        aVisList.addFixedString(new FixedString("which changes between"));
        
        Constant valueConstant1 = new DateConstant("1900-01-01");
        aVisList.addConstant(valueConstant1);
        
        aVisList.addFixedString(new FixedString("and"));
        Constant valueConstant2 = new DateConstant();
        aVisList.addConstant(valueConstant2);
        
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString((new FixedString(".PATIENT_II ) AND (((")));
        aComposer.addConstant(valueConstant1);
        aComposer.addFixedString((new FixedString(" < ")));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString((new FixedString(".BEGIN_DATE ) AND (")));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString((new FixedString(".BEGIN_DATE < ")));
        aComposer.addConstant(valueConstant2);
        aComposer.addFixedString((new FixedString(" )) OR (( ")));
        aComposer.addConstant(valueConstant1);
        aComposer.addFixedString((new FixedString(" < ")));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString((new FixedString(".END_DATE ) AND (")));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString((new FixedString(".END_DATE < ")));
        aComposer.addConstant(valueConstant2);
        aComposer.addFixedString((new FixedString(" )))")));
        
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    public AtomicWhereClause addPatientFirstHIVMedicationClause() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
        aVisList.addFixedString(new FixedString("The patient"));
        InputVariable ivar = new InputVariable(new VariableType("Patient"));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString("'s first HIV medication is"));
        FromVariable medicVar = new FromVariable("PATIENT_MEDICATION");
        OutputVariable ovar = new OutputVariable(new VariableType("PATIENT_MEDICATION"), getGoodVariableName("PATIENT_MEDICATION"));
        ovar.setUniqueName(ovar.getFormalName());
        ovar.getExpression().addFromVariable(medicVar);
        aVisList.addOutputVariable(ovar);
        
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".PATIENT_II = "));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString(new FixedString(".PATIENT_II ) and ("));
        
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString(new FixedString(".BEGIN_DATE = (SELECT MIN ("));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString(new FixedString("11.BEGIN_DATE) FROM PATIENT_MEDICATION "));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString(new FixedString("11 WHERE (("));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString(new FixedString("11.SYMPTOM_II = 70) and ("));
        
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".PATIENT_II = "));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString(new FixedString("11.PATIENT_II )) GROUP BY "));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".PATIENT_II )"));
        
        // this last bit leads to terrible "pessimization" of the query :-(
        aComposer.addFixedString(new FixedString(" and ("));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString(new FixedString(".SYMPTOM_II = 70) "));
        
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    public AtomicWhereClause addSequenceToResistanceResultClause() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
        
        aVisList.addFixedString(new FixedString("The amino acid sequence"));
        InputVariable ivar = new InputVariable(new VariableType("AA_Sequence"));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString("has a resistance result"));
        OutputVariable ovar = new OutputVariable(new VariableType("Algo_Result"), getGoodVariableName("Algo_Result"));
        ovar.setUniqueName(ovar.getFormalName());
        FromVariable resultVar = new FromVariable("Algo_Result");
        ovar.getExpression().addFromVariable(resultVar);
        aVisList.addOutputVariable(ovar);
        aVisList.addFixedString(new FixedString("for drug"));
        StringConstant drugConstant = new StringConstant();
        drugConstant.setSuggestedValuesQuery("SELECT DISTINCT NAME FROM COD_MEDICATION");
        drugConstant.setSuggestedValuesMandatory(true);
        aVisList.addConstant(drugConstant);
        aVisList.addFixedString(new FixedString("according to algorithm"));
        StringConstant algoConstant = new StringConstant();
        algoConstant.setSuggestedValuesQuery("SELECT DISTINCT NAME FROM COD_ALGORITHM");
        algoConstant.setSuggestedValuesMandatory(true);
        aVisList.addConstant(algoConstant);
        
        aComposer.addFixedString(new FixedString("("));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".AA_SEQUENCE_ID = "));
        aComposer.addFromVariable(resultVar);
        aComposer.addFixedString(new FixedString(".AA_SEQUENCE_II) and ("));
        aComposer.addFromVariable(resultVar);
        aComposer.addFixedString(new FixedString(".MEDICATION_II = "));
        FromVariable medicVar = new FromVariable("COD_MEDICATION");
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString(new FixedString(".MEDICATION_II) and ("));
        aComposer.addFromVariable(medicVar);
        aComposer.addFixedString(new FixedString(".NAME = "));
        aComposer.addConstant(drugConstant);
        aComposer.addFixedString(new FixedString(") and ("));
        FromVariable algorVar = new FromVariable("COD_ALGORITHM");
        aComposer.addFromVariable(resultVar);
        aComposer.addFixedString(new FixedString(".ALGORITHM_II ="));
        aComposer.addFromVariable(algorVar);
        aComposer.addFixedString(new FixedString(".ALGORITHM_II) and ("));
        aComposer.addFromVariable(algorVar);
        aComposer.addFixedString(new FixedString(".NAME = "));
        aComposer.addConstant(algoConstant);
        aComposer.addFixedString(new FixedString(")"));
        
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    public AtomicWhereClause addDrugWorkMechanismClause() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
        
        aVisList.addFixedString(new FixedString("The drug compound"));
        InputVariable ivar = new InputVariable(new VariableType("Drug_Compound"));
        aVisList.addInputVariable(ivar);
        aVisList.addFixedString(new FixedString("has work mechanism"));
        StringConstant workConstant = new StringConstant();
        workConstant.setSuggestedValuesQuery("SELECT DISTINCT WORK_MECHANISM_NAME FROM DRUG_WORK_MECHANISM");
        workConstant.setSuggestedValuesMandatory(true);
        aVisList.addConstant(workConstant);
        
        FromVariable drugWorkVar = new FromVariable("Drug_Work_Mechanism");
        aComposer.addFixedString(new FixedString("("));
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(".DRUG_COMPOUND_II = "));
        aComposer.addFromVariable(drugWorkVar);
        aComposer.addFixedString(new FixedString(".DRUG_COMPOUND_II) and ("));
        aComposer.addFromVariable(drugWorkVar);
        aComposer.addFixedString(new FixedString(".WORK_MECHANISM_ID = "));
        FromVariable codeWorkVar = new FromVariable("Cod_Work_Mechanism");
        aComposer.addFromVariable(codeWorkVar);
        aComposer.addFixedString(new FixedString(".WORK_MECHANISM_ID) and ("));
        aComposer.addFromVariable(codeWorkVar);
        aComposer.addFixedString(new FixedString(".WORK_MECHANISM_NAME = "));
        aComposer.addConstant(workConstant);
        aComposer.addFixedString(new FixedString(")"));
        
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    public AtomicWhereClause addClosestAssociationToDateClause(String[][] assocs, String[] dateFieldSpec, String condition, boolean before) {
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
            FromVariable dateFromVar = null;
            String tmpDateFromVarName = null;
            String tableName = assocs[0][0];
            String keyName = assocs[0][1];
            String foreignKeyName = assocs[0][2];
            String singularName = getTable(tableName).getSingularName();
            aVisList.addFixedString(new FixedString("The " + singularName));
            InputVariable ivar = new InputVariable(new VariableType(tableName));
            InputVariable dateIvar = new InputVariable(new VariableType("Date"));
            aVisList.addInputVariable(ivar);
            
            aComposer.addFixedString(new FixedString("("));
            aComposer.addInputVariable(ivar);
            aComposer.addFixedString(new FixedString("." + foreignKeyName + " = "));
            for (int i = 1; i < assocs.length; i++) {
                String newTableName = assocs[i][0];
                String newKeyName = assocs[i][1];
                String newForeignKeyName = assocs[i][2];
                FromVariable newFromVar = new FromVariable(newTableName);
                aComposer.addFromVariable(newFromVar);
                aComposer.addFixedString(new FixedString("." + newKeyName + ")"));
                for (int j = 3; j < assocs[i].length; j = j + 2) { // checks on this table
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + assocs[i][j] + " " + assocs[i][j+1] + ")"));
                }
                if (dateFieldSpec[0].equals(newTableName)) { // this is where the date field must be checked
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1]));
                    aComposer.addFixedString(new FixedString(before ? " < " : " > "));
                    aComposer.addInputVariable(dateIvar);
                    aComposer.addFixedString(new FixedString(")"));
                    dateFromVar = newFromVar;
                }
                
                if (i < assocs.length - 1) { // intermediate steps
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + newForeignKeyName + " = "));
                } else { // final step - test dateFromVar against subquery
                    // build temporary FromVariable names
                    String[] tmpFromVarNames = new String[assocs.length];
                    for (int k = 1; k < assocs.length; k++) {
                        String tmpTableName = assocs[k][0];
                        String tmpFromVarName = new FromVariable(tmpTableName).getWhereClauseStringValue();
                        tmpFromVarNames[k] = tmpFromVarName;
                        if (dateFieldSpec[0].equals(tmpTableName)) {
                            tmpDateFromVarName = tmpFromVarName;
                        }
                    }
                    
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(dateFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + " = (SELECT "));
                    aComposer.addFixedString(new FixedString(before ? "MAX (" : "MIN ("));
                    aComposer.addFixedString(new FixedString(tmpDateFromVarName));
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + ") FROM "));
                    for (int k = 1; k < assocs.length; k++) {
                        aComposer.addFixedString(new FixedString(assocs[k][0] + " "));
                        aComposer.addFixedString(new FixedString(tmpFromVarNames[k]));
                        aComposer.addFixedString(new FixedString((k < assocs.length - 1) ? ", " : " WHERE ("));
                    }
                    
                    aComposer.addInputVariable(ivar);
                    aComposer.addFixedString(new FixedString("." + foreignKeyName + " = "));
                    for (int k = 1; k < assocs.length; k++) {
                        String tmpTableName = assocs[k][0];
                        String tmpKeyName = assocs[k][1];
                        String tmpForeignKeyName = assocs[k][2];
                        aComposer.addFixedString(new FixedString(tmpFromVarNames[k] + "." + tmpKeyName + ")"));
                        for (int l = 3; l < assocs[k].length; l = l + 2) { // checks on this table
                            aComposer.addFixedString(new FixedString(" and ("));
                            aComposer.addFixedString(new FixedString(tmpFromVarNames[k] + "." + assocs[k][l] + " " + assocs[k][l+1] + ")"));
                        }
                        if (dateFieldSpec[0].equals(tmpTableName)) { // check on the date field
                            aComposer.addFixedString(new FixedString(" and ("));
                            aComposer.addFixedString(new FixedString(tmpFromVarNames[k] + "." + dateFieldSpec[1]));
                            aComposer.addFixedString(new FixedString(before ? " < " : " > "));
                            aComposer.addInputVariable(dateIvar);
                            aComposer.addFixedString(new FixedString(")"));
                        }
                        
                        if (k < assocs.length - 1) { // intermediate steps
                            aComposer.addFixedString(new FixedString(" and ("));
                            aComposer.addFixedString(new FixedString(tmpFromVarNames[k] + "." + tmpForeignKeyName + " = "));
                        } else {      // finish subquery
                            aComposer.addFixedString(new FixedString(" GROUP BY "));
                            aComposer.addInputVariable(ivar);
                            aComposer.addFixedString(new FixedString("." + foreignKeyName + "))"));
                        }
                    }
                    
                    
                    String newSingularName = getTable(newTableName).getSingularName();
                    aVisList.addFixedString(new FixedString("'s closest associated " + newSingularName + " " + condition + (before ? " before" : " after")));
                    aVisList.addInputVariable(dateIvar);
                    aVisList.addFixedString(new FixedString("is"));
                    OutputVariable ovar = new OutputVariable(new VariableType(newTableName), getGoodVariableName(newTableName));
                    ovar.setUniqueName(ovar.getFormalName());
                    ovar.getExpression().addFromVariable(newFromVar);
                    aVisList.addOutputVariable(ovar);
                }
            }
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
    
    public AtomicWhereClause addAssociationInPeriodClauses(String[][] assocs, String[] dateFieldSpec, String condition) {
        addAssociationInInputPeriodClause(assocs, dateFieldSpec, condition);
        return addAssociationInConstantPeriodClause(assocs, dateFieldSpec, condition);
    }
    
    public AtomicWhereClause addAssociationInInputPeriodClause(String[][] assocs, String[] dateFieldSpec, String condition) {
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
            FromVariable dateFromVar = null;
            String tmpDateFromVarName = null;
            String tableName = assocs[0][0];
            String keyName = assocs[0][1];
            String foreignKeyName = assocs[0][2];
            String singularName = getTable(tableName).getSingularName();
            aVisList.addFixedString(new FixedString("The " + singularName));
            InputVariable ivar = new InputVariable(new VariableType(tableName));
            InputVariable startDateIvar = new InputVariable(new VariableType("Date"));
            InputVariable endDateIvar = new InputVariable(new VariableType("Date"));
            aVisList.addInputVariable(ivar);
            
            aComposer.addFixedString(new FixedString("("));
            aComposer.addInputVariable(ivar);
            aComposer.addFixedString(new FixedString("." + foreignKeyName + " = "));
            for (int i = 1; i < assocs.length; i++) {
                String newTableName = assocs[i][0];
                String newKeyName = assocs[i][1];
                String newForeignKeyName = assocs[i][2];
                FromVariable newFromVar = new FromVariable(newTableName);
                aComposer.addFromVariable(newFromVar);
                aComposer.addFixedString(new FixedString("." + newKeyName + ")"));
                for (int j = 3; j < assocs[i].length; j = j + 2) { // checks on this table
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + assocs[i][j] + " " + assocs[i][j+1] + ")"));
                }
                if (dateFieldSpec[0].equals(newTableName)) { // this is where the date field must be checked
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + " > "));
                    aComposer.addInputVariable(startDateIvar);
                    aComposer.addFixedString(new FixedString(") and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + " < "));
                    aComposer.addInputVariable(endDateIvar);
                    aComposer.addFixedString(new FixedString(")"));
                    dateFromVar = newFromVar;
                }
                if (i < assocs.length - 1) { // intermediate steps
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + newForeignKeyName + " = "));
                } else { // final step - test dateFromVar against period
                    
                    String newSingularName = getTable(newTableName).getSingularName();
                    OutputVariable ovar = new OutputVariable(new VariableType(newTableName), getGoodVariableName(newTableName));
                    ovar.setUniqueName(ovar.getFormalName());
                    ovar.getExpression().addFromVariable(newFromVar);
                    aVisList.addFixedString(new FixedString("has a " + newSingularName));
                    aVisList.addOutputVariable(ovar);
                    aVisList.addFixedString(new FixedString(condition + " between"));
                    aVisList.addInputVariable(startDateIvar);
                    aVisList.addFixedString(new FixedString("and"));
                    aVisList.addInputVariable(endDateIvar);
                    
                }
            }
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
    
    public AtomicWhereClause addAssociationInConstantPeriodClause(String[][] assocs, String[] dateFieldSpec, String condition) {
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
            FromVariable dateFromVar = null;
            String tmpDateFromVarName = null;
            String tableName = assocs[0][0];
            String keyName = assocs[0][1];
            String foreignKeyName = assocs[0][2];
            String singularName = getTable(tableName).getSingularName();
            aVisList.addFixedString(new FixedString("The " + singularName));
            InputVariable ivar = new InputVariable(new VariableType(tableName));
            DateConstant startDateCst = new DateConstant("1900-01-01");
            DateConstant endDateCst = new DateConstant();
            aVisList.addInputVariable(ivar);
            
            aComposer.addFixedString(new FixedString("("));
            aComposer.addInputVariable(ivar);
            aComposer.addFixedString(new FixedString("." + foreignKeyName + " = "));
            for (int i = 1; i < assocs.length; i++) {
                String newTableName = assocs[i][0];
                String newKeyName = assocs[i][1];
                String newForeignKeyName = assocs[i][2];
                FromVariable newFromVar = new FromVariable(newTableName);
                aComposer.addFromVariable(newFromVar);
                aComposer.addFixedString(new FixedString("." + newKeyName + ")"));
                for (int j = 3; j < assocs[i].length; j = j + 2) { // checks on this table
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + assocs[i][j] + " " + assocs[i][j+1] + ")"));
                }
                if (dateFieldSpec[0].equals(newTableName)) { // this is where the date field must be checked
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + " > "));
                    aComposer.addConstant(startDateCst);
                    aComposer.addFixedString(new FixedString(") and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + " < "));
                    aComposer.addConstant(endDateCst);
                    aComposer.addFixedString(new FixedString(")"));
                    dateFromVar = newFromVar;
                }
                if (i < assocs.length - 1) { // intermediate steps
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + newForeignKeyName + " = "));
                } else { // final step - test dateFromVar against period
                    
                    String newSingularName = getTable(newTableName).getSingularName();
                    OutputVariable ovar = new OutputVariable(new VariableType(newTableName), getGoodVariableName(newTableName));
                    ovar.setUniqueName(ovar.getFormalName());
                    ovar.getExpression().addFromVariable(newFromVar);
                    aVisList.addFixedString(new FixedString("has a " + newSingularName));
                    aVisList.addOutputVariable(ovar);
                    aVisList.addFixedString(new FixedString(condition + " between"));
                    aVisList.addConstant(startDateCst);
                    aVisList.addFixedString(new FixedString("and"));
                    aVisList.addConstant(endDateCst);
                    
                }
            }
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
        
    }
    
    public AtomicWhereClause addChangedAssociationInPeriodClauses(String[][] assocs1, String[][] assocs2, String[] dateFieldSpec, String[] changeSpec, String condition) {
        addChangedAssociationInInputPeriodClause(assocs1, assocs2, dateFieldSpec, changeSpec, condition);
        return addChangedAssociationInConstantPeriodClause(assocs1, assocs2, dateFieldSpec, changeSpec, condition);
    }
    
    public AtomicWhereClause addChangedAssociationInInputPeriodClause(String[][] assocs1, String[][] assocs2, String[] dateFieldSpec, String[] changeSpec, String condition) {
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
            
            // 1. get the candidate association
            
            FromVariable dateFromVar = null;
            String tableName = assocs1[0][0];
            String keyName = assocs1[0][1];
            String foreignKeyName = assocs1[0][2];
            String singularName = getTable(tableName).getSingularName();
            aVisList.addFixedString(new FixedString("The " + singularName));
            InputVariable ivar = new InputVariable(new VariableType(tableName));
            InputVariable startDateIvar = new InputVariable(new VariableType("Date"));
            InputVariable endDateIvar = new InputVariable(new VariableType("Date"));
            aVisList.addInputVariable(ivar);
            
            aComposer.addFixedString(new FixedString("("));
            aComposer.addInputVariable(ivar);
            aComposer.addFixedString(new FixedString("." + foreignKeyName + " = "));
            for (int i = 1; i < assocs1.length; i++) {
                String newTableName = assocs1[i][0];
                String newKeyName = assocs1[i][1];
                String newForeignKeyName = assocs1[i][2];
                FromVariable newFromVar = new FromVariable(newTableName);
                aComposer.addFromVariable(newFromVar);
                aComposer.addFixedString(new FixedString("." + newKeyName + ")"));
                for (int j = 3; j < assocs1[i].length; j = j + 2) { // checks on this table
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + assocs1[i][j] + " " + assocs1[i][j+1] + ")"));
                }
                if (dateFieldSpec[0].equals(newTableName)) { // this is where the date field must be checked
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + " > "));
                    aComposer.addInputVariable(startDateIvar);
                    aComposer.addFixedString(new FixedString(") and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + " < "));
                    aComposer.addInputVariable(endDateIvar);
                    aComposer.addFixedString(new FixedString(")"));
                    dateFromVar = newFromVar;
                }
                if (i < assocs1.length - 1) { // intermediate steps
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + newForeignKeyName + " = "));
                } else { // final step
                    
                    String newSingularName = getTable(newTableName).getSingularName();
                    OutputVariable ovar = new OutputVariable(new VariableType(newTableName), getGoodVariableName(newTableName));
                    ovar.setUniqueName(ovar.getFormalName());
                    ovar.getExpression().addFromVariable(newFromVar);
                    aVisList.addFixedString(new FixedString("has a " + newSingularName));
                    aVisList.addOutputVariable(ovar);
                    aVisList.addFixedString(new FixedString(condition + " between"));
                    aVisList.addInputVariable(startDateIvar);
                    aVisList.addFixedString(new FixedString("and"));
                    aVisList.addInputVariable(endDateIvar);
                    
                }
            }
            
            // 2. Get the closest association to the candidate, and check it
            
            FromVariable prevDateFromVar = null;
            String tmpDateFromVarName = null;
            String prevTableName = assocs2[0][0];
            String prevKeyName = assocs2[0][1];
            String prevForeignKeyName = assocs2[0][2];
            
            
            aComposer.addFixedString(new FixedString(" and ("));
            aComposer.addInputVariable(ivar);
            aComposer.addFixedString(new FixedString("." + prevForeignKeyName + " = "));
            for (int i = 1; i < assocs2.length; i++) {
                String prevNewTableName = assocs2[i][0];
                String prevNewKeyName = assocs2[i][1];
                String prevNewForeignKeyName = assocs2[i][2];
                FromVariable prevNewFromVar = new FromVariable(prevNewTableName);
                aComposer.addFromVariable(prevNewFromVar);
                aComposer.addFixedString(new FixedString("." + prevNewKeyName + ")"));
                for (int j = 3; j < assocs2[i].length; j = j + 2) { // checks on this table
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(prevNewFromVar);
                    aComposer.addFixedString(new FixedString("." + assocs2[i][j] + " " + assocs2[i][j+1] + ")"));
                }
                if (dateFieldSpec[0].equals(prevNewTableName)) { // this is where the date field must be checked
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(prevNewFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + "<"));
                    aComposer.addFromVariable(dateFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + ")"));
                    prevDateFromVar = prevNewFromVar;
                }
                if (changeSpec[0].equals(prevNewTableName)) { // this is where the change condition must be checked
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(prevNewFromVar);
                    aComposer.addFixedString(new FixedString("." + changeSpec[1] + " " + changeSpec[2] + ")"));
                }
                
                
                if (i < assocs2.length - 1) { // intermediate steps
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(prevNewFromVar);
                    aComposer.addFixedString(new FixedString("." + prevNewForeignKeyName + " = "));
                } else { // final step - test dateFromVar against subquery
                    // build temporary FromVariable names
                    String[] tmpFromVarNames = new String[assocs2.length];
                    for (int k = 1; k < assocs2.length; k++) {
                        String tmpTableName = assocs2[k][0];
                        String tmpFromVarName = new FromVariable(tmpTableName).getWhereClauseStringValue();
                        tmpFromVarNames[k] = tmpFromVarName;
                        if (dateFieldSpec[0].equals(tmpTableName)) {
                            tmpDateFromVarName = tmpFromVarName;
                        }
                    }
                    
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(prevDateFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + " = (SELECT MAX ("));
                    aComposer.addFixedString(new FixedString(tmpDateFromVarName));
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + ") FROM "));
                    for (int k = 1; k < assocs2.length; k++) {
                        aComposer.addFixedString(new FixedString(assocs2[k][0] + " "));
                        aComposer.addFixedString(new FixedString(tmpFromVarNames[k]));
                        aComposer.addFixedString(new FixedString((k < assocs2.length - 1) ? ", " : " WHERE ("));
                    }
                    
                    aComposer.addInputVariable(ivar);
                    aComposer.addFixedString(new FixedString("." + prevForeignKeyName + " = "));
                    for (int k = 1; k < assocs2.length; k++) {
                        String tmpTableName = assocs2[k][0];
                        String tmpKeyName = assocs2[k][1];
                        String tmpForeignKeyName = assocs2[k][2];
                        aComposer.addFixedString(new FixedString(tmpFromVarNames[k] + "." + tmpKeyName + ")"));
                        for (int l = 3; l < assocs2[k].length; l = l + 2) { // checks on this table
                            aComposer.addFixedString(new FixedString(" and ("));
                            aComposer.addFixedString(new FixedString(tmpFromVarNames[k] + "." + assocs2[k][l] + " " + assocs2[k][l+1] + ")"));
                        }
                        if (dateFieldSpec[0].equals(tmpTableName)) { // check on the date field
                            aComposer.addFixedString(new FixedString(" and ("));
                            aComposer.addFixedString(new FixedString(tmpFromVarNames[k] + "." + dateFieldSpec[1] + "<"));
                            aComposer.addFromVariable(dateFromVar);
                            aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + ")"));
                        }
                        
                        if (k < assocs2.length - 1) { // intermediate steps
                            aComposer.addFixedString(new FixedString(" and ("));
                            aComposer.addFixedString(new FixedString(tmpFromVarNames[k] + "." + tmpForeignKeyName + " = "));
                        } else {      // finish subquery
                            aComposer.addFixedString(new FixedString(" GROUP BY "));
                            aComposer.addInputVariable(ivar);
                            aComposer.addFixedString(new FixedString("." + prevForeignKeyName + "))"));
                        }
                    }
                }
            }
            
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
    
    public AtomicWhereClause addChangedAssociationInConstantPeriodClause(String[][] assocs1, String[][] assocs2, String[] dateFieldSpec, String[] changeSpec, String condition) {
        JDBCManager manager = JDBCManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            HibernateClauseComposer aComposer = aClause.getHibernateClauseComposer();
            
            // 1. get the candidate association
            
            FromVariable dateFromVar = null;
            String tableName = assocs1[0][0];
            String keyName = assocs1[0][1];
            String foreignKeyName = assocs1[0][2];
            String singularName = getTable(tableName).getSingularName();
            aVisList.addFixedString(new FixedString("The " + singularName));
            InputVariable ivar = new InputVariable(new VariableType(tableName));
            DateConstant startDateConstant = new DateConstant("1900-01-01");
            DateConstant endDateConstant = new DateConstant();
            aVisList.addInputVariable(ivar);
            
            aComposer.addFixedString(new FixedString("("));
            aComposer.addInputVariable(ivar);
            aComposer.addFixedString(new FixedString("." + foreignKeyName + " = "));
            for (int i = 1; i < assocs1.length; i++) {
                String newTableName = assocs1[i][0];
                String newKeyName = assocs1[i][1];
                String newForeignKeyName = assocs1[i][2];
                FromVariable newFromVar = new FromVariable(newTableName);
                aComposer.addFromVariable(newFromVar);
                aComposer.addFixedString(new FixedString("." + newKeyName + ")"));
                for (int j = 3; j < assocs1[i].length; j = j + 2) { // checks on this table
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + assocs1[i][j] + " " + assocs1[i][j+1] + ")"));
                }
                if (dateFieldSpec[0].equals(newTableName)) { // this is where the date field must be checked
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + " > "));
                    aComposer.addConstant(startDateConstant);
                    aComposer.addFixedString(new FixedString(") and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + " < "));
                    aComposer.addConstant(endDateConstant);
                    aComposer.addFixedString(new FixedString(")"));
                    dateFromVar = newFromVar;
                }
                if (i < assocs1.length - 1) { // intermediate steps
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(newFromVar);
                    aComposer.addFixedString(new FixedString("." + newForeignKeyName + " = "));
                } else { // final step
                    
                    String newSingularName = getTable(newTableName).getSingularName();
                    OutputVariable ovar = new OutputVariable(new VariableType(newTableName), getGoodVariableName(newTableName));
                    ovar.setUniqueName(ovar.getFormalName());
                    ovar.getExpression().addFromVariable(newFromVar);
                    aVisList.addFixedString(new FixedString("has a " + newSingularName));
                    aVisList.addOutputVariable(ovar);
                    aVisList.addFixedString(new FixedString(condition + " between"));
                    aVisList.addConstant(startDateConstant);
                    aVisList.addFixedString(new FixedString("and"));
                    aVisList.addConstant(endDateConstant);
                    
                }
            }
            
            // 2. Get the closest association to the candidate, and check it
            
            FromVariable prevDateFromVar = null;
            String tmpDateFromVarName = null;
            String prevTableName = assocs2[0][0];
            String prevKeyName = assocs2[0][1];
            String prevForeignKeyName = assocs2[0][2];
            
            
            aComposer.addFixedString(new FixedString(" and ("));
            aComposer.addInputVariable(ivar);
            aComposer.addFixedString(new FixedString("." + prevForeignKeyName + " = "));
            for (int i = 1; i < assocs2.length; i++) {
                String prevNewTableName = assocs2[i][0];
                String prevNewKeyName = assocs2[i][1];
                String prevNewForeignKeyName = assocs2[i][2];
                FromVariable prevNewFromVar = new FromVariable(prevNewTableName);
                aComposer.addFromVariable(prevNewFromVar);
                aComposer.addFixedString(new FixedString("." + prevNewKeyName + ")"));
                for (int j = 3; j < assocs2[i].length; j = j + 2) { // checks on this table
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(prevNewFromVar);
                    aComposer.addFixedString(new FixedString("." + assocs2[i][j] + " " + assocs2[i][j+1] + ")"));
                }
                if (dateFieldSpec[0].equals(prevNewTableName)) { // this is where the date field must be checked
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(prevNewFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + "<"));
                    aComposer.addFromVariable(dateFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + ")"));
                    prevDateFromVar = prevNewFromVar;
                }
                if (changeSpec[0].equals(prevNewTableName)) { // this is where the change condition must be checked
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(prevNewFromVar);
                    aComposer.addFixedString(new FixedString("." + changeSpec[1] + " " + changeSpec[2] + ")"));
                }
                
                
                if (i < assocs2.length - 1) { // intermediate steps
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(prevNewFromVar);
                    aComposer.addFixedString(new FixedString("." + prevNewForeignKeyName + " = "));
                } else { // final step - test dateFromVar against subquery
                    // build temporary FromVariable names
                    String[] tmpFromVarNames = new String[assocs2.length];
                    for (int k = 1; k < assocs2.length; k++) {
                        String tmpTableName = assocs2[k][0];
                        String tmpFromVarName = new FromVariable(tmpTableName).getWhereClauseStringValue();
                        tmpFromVarNames[k] = tmpFromVarName;
                        if (dateFieldSpec[0].equals(tmpTableName)) {
                            tmpDateFromVarName = tmpFromVarName;
                        }
                    }
                    
                    aComposer.addFixedString(new FixedString(" and ("));
                    aComposer.addFromVariable(prevDateFromVar);
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + " = (SELECT MAX ("));
                    aComposer.addFixedString(new FixedString(tmpDateFromVarName));
                    aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + ") FROM "));
                    for (int k = 1; k < assocs2.length; k++) {
                        aComposer.addFixedString(new FixedString(assocs2[k][0] + " "));
                        aComposer.addFixedString(new FixedString(tmpFromVarNames[k]));
                        aComposer.addFixedString(new FixedString((k < assocs2.length - 1) ? ", " : " WHERE ("));
                    }
                    
                    aComposer.addInputVariable(ivar);
                    aComposer.addFixedString(new FixedString("." + prevForeignKeyName + " = "));
                    for (int k = 1; k < assocs2.length; k++) {
                        String tmpTableName = assocs2[k][0];
                        String tmpKeyName = assocs2[k][1];
                        String tmpForeignKeyName = assocs2[k][2];
                        aComposer.addFixedString(new FixedString(tmpFromVarNames[k] + "." + tmpKeyName + ")"));
                        for (int l = 3; l < assocs2[k].length; l = l + 2) { // checks on this table
                            aComposer.addFixedString(new FixedString(" and ("));
                            aComposer.addFixedString(new FixedString(tmpFromVarNames[k] + "." + assocs2[k][l] + " " + assocs2[k][l+1] + ")"));
                        }
                        if (dateFieldSpec[0].equals(tmpTableName)) { // check on the date field
                            aComposer.addFixedString(new FixedString(" and ("));
                            aComposer.addFixedString(new FixedString(tmpFromVarNames[k] + "." + dateFieldSpec[1] + "<"));
                            aComposer.addFromVariable(dateFromVar);
                            aComposer.addFixedString(new FixedString("." + dateFieldSpec[1] + ")"));
                        }
                        
                        if (k < assocs2.length - 1) { // intermediate steps
                            aComposer.addFixedString(new FixedString(" and ("));
                            aComposer.addFixedString(new FixedString(tmpFromVarNames[k] + "." + tmpForeignKeyName + " = "));
                        } else {      // finish subquery
                            aComposer.addFixedString(new FixedString(" GROUP BY "));
                            aComposer.addInputVariable(ivar);
                            aComposer.addFixedString(new FixedString("." + prevForeignKeyName + "))"));
                        }
                    }
                }
            }
            
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
*/    
} // end AWCPrototypeCatalog
