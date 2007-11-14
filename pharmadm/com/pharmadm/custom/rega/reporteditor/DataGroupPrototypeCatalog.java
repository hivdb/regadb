/*
 * DataGroupPrototypeCatalog.java
 *
 * Created on November 28, 2003, 12:55 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

import java.util.*;
import com.pharmadm.custom.rega.queryeditor.*;
    
    
/**
 * <p>
 * A catalog containing DataGroup prototypes ('Prototype' pattern).
 * </p>
 * <p>
 * Typically, a catalog is built from a file describing the different
 * prototypes.
 * </p>
 * <p>
 * The prototypes should not be manipulated directly, but rather be cloned
 * first. The catalog can determine which of its prototypes represent
 * DataGroups whose clones can be added at a point in a ReportFormat
 * composition where a given Collection of DataOutputVariables is available.
 * </p>
 *
 * Prototype pattern
 */ 
public class DataGroupPrototypeCatalog {
    
    private static DataGroupPrototypeCatalog mainCatalog = null;
    private List dataGroups = new ArrayList();
    
    
    public static DataGroupPrototypeCatalog getInstance() {
        if (mainCatalog == null) {
            initMainCatalog();
        }
        return mainCatalog;
    }
    
    private static void initMainCatalog() {
        // %$ KVB : under development
        DataGroupPrototypeCatalog catalog = new DataGroupPrototypeCatalog();
        catalog.addBaseGroup("Patient");
        catalog.addBaseGroup("PatientMedicatie");
        
        catalog.addNthDateAssociationGroup("Patient", "PatientMedicatie", "th medication after ", "getNthMedicationAfter", false);
        catalog.addNthDateAssociationGroup("Patient", "PatientMedicatie", "th last medication before ", "getNthMedicationBefore", true);
        catalog.addListAllAssociationsGroup("Patient", "PatientMedicatie", " medications ", "getSortedMedications");
        catalog.addListAllAssociationsGroup("Patient", "PatientMedicatie", " medications in reverse order ", "getReversedSortedMedications");
        catalog.addListAllDoubleAssociationsGroup("Patient", "PatientMedicatie", " simultaneous medications to ", "PatientMedicatie", "getSimultaneousMedications");
        
        catalog.addAssociationGroup("PatientMedicatie", "Patient", " patient ", "getPatient");
        catalog.addPropertyOutputGroup("PatientMedicatie", "Date", " start date ", "getPropertyStartDate");
        catalog.addResultRelativeToMedicationOutputGroup("ViralLoad"); 
        catalog.addResistanceResultRelativeToMedicationOutputGroup(); 
        
        catalog.addListAllAssociationsInPeriodGroup("Patient", "GeneralizedResult", " results between ", "getResultsBetween");
        catalog.addListAllAssociationsInPeriodGroup("Patient", "GeneralizedResult", " results in reverse order between ", "getReversedResultsBetween");
        
        String resultTypesQuery = "(SELECT DISTINCT NAME FROM COD_CALC_TEST) UNION (SELECT DISTINCT NAME FROM COD_ELEM_TEST)";
        catalog.addListAllAssociationsOfConfigurableTypeInPeriodGroup("Patient", "GeneralizedResult", " results ", resultTypesQuery, "getResultsBetween");
        catalog.addListAllAssociationsInPeriodGroup("Patient", "GeneralizedResult", " viral load results ", "getViralLoadResultsBetween");
        catalog.addListAllAssociationsInPeriodGroup("Patient", "GeneralizedResult", " viral load results in reverse order ", "getReversedViralLoadResultsBetween");
        catalog.addListAllAssociationsInPeriodGroup("Patient", "GeneralizedResult", " absolute CD4 results ", "getCD4AbsResultsBetween");
        catalog.addListAllAssociationsInPeriodGroup("Patient", "GeneralizedResult", " absolute CD4 results in reverse order ", "getReversedCD4AbsResultsBetween");
        catalog.addListAllAssociationsInPeriodGroup("Patient", "GeneralizedResult", " relative CD4 results ", "getCD4RelResultsBetween");
        catalog.addListAllAssociationsInPeriodGroup("Patient", "GeneralizedResult", " relative CD4 results in reverse order ", "getReversedCD4RelResultsBetween");
        //catalog.addDateConstantAssociationGroup("Patient", "PatientMedicatie", " first medication after ", "getFirstMedicationAfter");
        catalog.addNthDateAssociationGroup("Patient", "GeneralizedResult", "th viral load result after ", "getNthViralLoadAfter", false);
        catalog.addNthDateAssociationGroup("Patient", "GeneralizedResult", "th absolute CD4 result after ", "getNthCD4AbsAfter", false);
        catalog.addNthDateAssociationGroup("Patient", "GeneralizedResult", "th relative CD4 result after ", "getNthCD4RelAfter", false);
        catalog.addNthDateAssociationGroup("Patient", "GeneralizedResult", "th last viral load result before ", "getNthViralLoadBefore", true);
        catalog.addNthDateAssociationGroup("Patient", "GeneralizedResult", "th last absolute CD4 result before ", "getNthCD4AbsBefore", true);
        catalog.addNthDateAssociationGroup("Patient", "GeneralizedResult", "th last relative CD4 result before ", "getNthCD4RelBefore", true);
        
        catalog.addListAllDoubleAssociationsGroup("Patient", "GeneralizedResult", " viral load results during medication ", "PatientMedicatie", "getViralLoadResultsDuring");
        catalog.addListAllDoubleAssociationsGroup("Patient", "GeneralizedResult", " viral load results in reverse order during medication ", "PatientMedicatie", "getReversedViralLoadResultsDuring");
        catalog.addListAllDoubleAssociationsGroup("Patient", "GeneralizedResult", " absolute CD4 results during medication ", "PatientMedicatie", "getCD4AbsResultsDuring");
        catalog.addListAllDoubleAssociationsGroup("Patient", "GeneralizedResult", " absolute CD4 results in reverse order during medication ", "PatientMedicatie", "getReversedCD4AbsResultsDuring");
        catalog.addListAllDoubleAssociationsGroup("Patient", "GeneralizedResult", " relative CD4 results during medication ", "PatientMedicatie", "getCD4RelResultsDuring");
        catalog.addListAllDoubleAssociationsGroup("Patient", "GeneralizedResult", " relative CD4 results in reverse order during medication ", "PatientMedicatie", "getReversedCD4RelResultsDuring");
        
        catalog.addNthDateAssociationGroup("Patient", "GeneralizedResult", "th viral load result after ", "getNthViralLoadAfter", false);
        catalog.addNthDateAssociationGroup("Patient", "GeneralizedResult", "th absolute CD4 result after ", "getNthCD4AbsAfter", false);
        catalog.addNthDateAssociationGroup("Patient", "GeneralizedResult", "th relative CD4 result after ", "getNthCD4RelAfter", false);
        catalog.addNthDateAssociationGroup("Patient", "GeneralizedResult", "th last viral load result before ", "getNthViralLoadBefore", true);
        catalog.addNthDateAssociationGroup("Patient", "GeneralizedResult", "th last absolute CD4 result before ", "getNthCD4AbsBefore", true);
        catalog.addNthDateAssociationGroup("Patient", "GeneralizedResult", "th last relative CD4 result before ", "getNthCD4RelBefore", true);
        catalog.addListAllAssociationsGroup("Patient", "VirusAppearance", " virus appearances ", "getSortedVirusAppearances");
        
        catalog.addBaseGroup("PATIENT_SAMPLE");
        
        catalog.addAssociationGroup("PATIENT_SAMPLE", "Patient", " patient ", "getPatient");
        catalog.addListAllAssociationsGroup("PATIENT_SAMPLE", "GeneralizedResult", " measured results ", "getResults");
        catalog.addListAllAssociationsGroup("PATIENT_SAMPLE", "GeneralizedResult", " viral load results ", "getViralLoadResults");
        catalog.addListAllAssociationsGroup("PATIENT_SAMPLE", "GeneralizedResult", " absolute CD4 results ", "getCD4AbsResults");
        catalog.addListAllAssociationsGroup("PATIENT_SAMPLE", "GeneralizedResult", " relative CD4 results ", "getCD4RelResults");
        catalog.addListAllAssociationsOfConfigurableTypeGroup("PATIENT_SAMPLE", "GeneralizedResult", " results ", resultTypesQuery, "getSortedResults");
        catalog.addListAllAssociationsGroup("PATIENT_SAMPLE", "VirusAppearance", " virus appearances ", "getSortedVirusAppearances");
        
        catalog.addBaseGroup("VirusAppearance");
        
        String proteinTypesQuery = "(SELECT DISTINCT NAME FROM PROTEIN)";
        catalog.addAssociationOfConfigurableTypeGroup("VirusAppearance", "AASequence", " amino acid sequence for protein ", proteinTypesQuery, "getAASequenceForProtein");
        
        //catalog.addListAllAssociationsGroup("AASequence", "AAMutation", " mutations ", "getMutations");
        //catalog.addListAllAssociationsInRangeGroup("AASequence", "AAMutation", " mutations ", "getMutationsBetween");
        //catalog.addListAllAssociationsGroup("AASequence", "AAMutation", " real mutations ", "getRealMutations");
        //catalog.addListAllAssociationsInRangeGroup("AASequence", "AAMutation", " real mutations ", "getRealMutationsBetween");
        catalog.addListAllAssociationsGroup("AASequence", "AAPointMutation", " point mutations ", "getPointMutations");
        catalog.addListAllAssociationsInRangeGroup("AASequence", "AAPointMutation", " point mutations ", "getPointMutationsBetween");
        catalog.addListAllAssociationsGroup("AASequence", "AAPointMutation", " real point mutations ", "getRealPointMutations");
        catalog.addListAllAssociationsInRangeGroup("AASequence", "AAPointMutation", " real point mutations ", "getRealPointMutationsBetween");
        catalog.addListAllAssociationsGroup("AASequence", "AAInsertion", " insertions ", "getInsertions");
        catalog.addListAllAssociationsInRangeGroup("AASequence", "AAInsertion", " insertions ", "getInsertionsBetween");
        catalog.addListAllAssociationsGroup("AASequence", "AADeletion", " deletions ", "getDeletions");
        catalog.addListAllAssociationsInRangeGroup("AASequence", "AADeletion", " deletions ", "getDeletionsBetween");
        
        catalog.addBaseGroup("Molecules");
        catalog.addListAllAssociationsGroup("Molecules", "DrugStock", " drug stocks ", "getDrugStocks");
        
        catalog.addBaseGroup("Sample");
        catalog.addAssociationGroup("Sample", "Molecules", " molecule ", "getMolecule");
        catalog.addAssociationGroup("Sample", "VirusAppearance", " virus appearance ", "getVirus");
        
        catalog.addListAllAssociationsGroup("Sample", "GeneralizedResult", " results ", "getSortedResults");
        catalog.addListAllAssociationsGroup("Sample", "GeneralizedResult", " EC50 results ", "getSortedIC50Results"); // Sorry, Rega-specific naming EC50/IC50...
        catalog.addListAllAssociationsGroup("Sample", "GeneralizedResult", " CC50 results ", "getSortedCC50Results");
        
        mainCatalog = catalog;
    }
    
    public Collection getDataGroupPrototypes(Collection availableDataOutputVariables) {
        Collection result = new ArrayList();
        Iterator iter = dataGroups.iterator();
        while (iter.hasNext()) {
            DataGroup group = (DataGroup)iter.next();
            boolean groupOk = true;
            Iterator inputIter = group.getDataInputVariables().iterator();
            while (inputIter.hasNext()) {
                DataInputVariable ivar = (DataInputVariable)inputIter.next();
                boolean varOk = false;
                //System.out.println("Checking ..." + ivar.getVariableType());
                Iterator outputIter = availableDataOutputVariables.iterator();
                while (outputIter.hasNext()) {
                    DataOutputVariable ovar = (DataOutputVariable)outputIter.next();
                    if (ivar.isCompatible(ovar)) {
                        //System.out.println(ivar.getVariableType());
                        varOk = true;
                        break;
                    } else {
                        //System.out.println(ivar.getVariableType() + " is not " + ovar.getVariableType());
                    }
                }
                if (! varOk) {
                    groupOk = false;
                    break;
                }
            }
            if (groupOk) {
                result.add(group);
            }
        }
        return result;
    } // end getDataGroupPrototypes
    
    
    public void addDataGroup(DataGroup dataGroup) {
        this.dataGroups.add(dataGroup);
    }
    
    private DataGroup addBaseGroup(String typeString) {
        DataGroup aGroup = new DataGroup();
        VariableType type = new VariableType(typeString);
        OrderedDGWordList aVisList = aGroup.getVisualizationList();
        aVisList.addFixedString(new FixedString("Show data for " + typeString + " "));
        ObjectListVariable olvar = new ObjectListVariable(type);
        aVisList.addObjectListVariable(olvar);
        DataOutputVariable ovar = new DataOutputVariable(type, typeString.substring(0,1));
        ovar.setUniqueName(ovar.getFormalName());
        ovar.setSpecifier(olvar);
        aGroup.addDataOutputVariable(ovar);
        addDataGroup(aGroup);
        return aGroup;
    }
    
    private DataGroup addPropertyOutputGroup(String inTypeString, String outTypeString, String description, String methodString) {
        // conceptually different but in practice same implementation
        return addAssociationGroup(inTypeString, outTypeString, description, methodString);
    }
    
    private DataGroup addAssociationGroup(String inTypeString, String outTypeString, String description, String methodString) {
        DataGroup aGroup = new DataGroup();
        VariableType inType = new VariableType(inTypeString);
        DataInputVariable inputVar = new DataInputVariable(inType);
        VariableType outType = new VariableType(outTypeString);
        DataOutputVariable ovar = new DataOutputVariable(outType, outTypeString.substring(0,1));
        ovar.setUniqueName(ovar.getFormalName());
        OrderedDGWordList aVisList = aGroup.getVisualizationList();
        aVisList.addFixedString(new FixedString("Show "));
        aVisList.addDataInputVariable(inputVar);
        aVisList.addFixedString(new FixedString("'s " + description));
        aVisList.addDataOutputVariable(ovar);
        ValueSpecifier[] params = new ValueSpecifier[0];
        ovar.setSpecifier(new MethodSpecifier(inputVar, methodString, params));
        addDataGroup(aGroup);
        return aGroup; 
    }
    
    private DataGroup addAssociationOfConfigurableTypeGroup(String inTypeString, String outTypeString, String description, String valuesQuery, String methodString) {
        DataGroup aGroup = new DataGroup();
        VariableType inType = new VariableType(inTypeString);
        DataInputVariable inputVar = new DataInputVariable(inType);
        VariableType outType = new VariableType(outTypeString);
        DataOutputVariable ovar = new DataOutputVariable(outType, outTypeString.substring(0,1));
        ovar.setUniqueName(ovar.getFormalName());
        OrderedDGWordList aVisList = aGroup.getVisualizationList();
        aVisList.addFixedString(new FixedString("Show "));
        aVisList.addDataInputVariable(inputVar);
        aVisList.addFixedString(new FixedString("'s " + description));
        StringConstant typeCst = new StringConstant();
        typeCst.setSuggestedValuesQuery(valuesQuery);
        typeCst.setSuggestedValuesMandatory(true);
        aVisList.addConstant(typeCst);
        aVisList.addFixedString(new FixedString(" as "));
        aVisList.addDataOutputVariable(ovar);
        ValueSpecifier[] params = new ValueSpecifier[1];
        params[0] = typeCst;
        ovar.setSpecifier(new MethodSpecifier(inputVar, methodString, params));
        addDataGroup(aGroup);
        return aGroup; 
    }
    
    private DataGroup addResultRelativeToMedicationOutputGroup(String type) {
        String showType = (type.equals("ViralLoad") ? "viral load" : (type.equals("CD4Abs") ? "CD4 count" : (type.equals("CD4Rel") ? "CD4 percentage" : type)));
        DataGroup aGroup = new DataGroup();
        VariableType inType = new VariableType("Patient");
        DataInputVariable inputVar = new DataInputVariable(inType);
        VariableType inType2 = new VariableType("PatientMedicatie");
        DataInputVariable inputVar2 = new DataInputVariable(inType2);
        VariableType outType = new VariableType("GeneralizedResult");
        DataOutputVariable ovar = new DataOutputVariable(outType, "GeneralizedResult".substring(0,1));
        ovar.setUniqueName(ovar.getFormalName());
        DoubleConstant days = new DoubleConstant();
        DoubleConstant devDays = new DoubleConstant();
        devDays.setValue(new Double(30));
        OrderedDGWordList aVisList = aGroup.getVisualizationList();
        aVisList.addFixedString(new FixedString("Show "));
        aVisList.addDataInputVariable(inputVar);
        aVisList.addFixedString(new FixedString("'s " + showType + " result "));
        aVisList.addDataOutputVariable(ovar);
        aVisList.addFixedString(new FixedString(" which is closest to "));
        aVisList.addConstant(days);
        aVisList.addFixedString(new FixedString(" days after the start of medication ")); 
        aVisList.addDataInputVariable(inputVar2);
        aVisList.addFixedString(new FixedString(" (maximum deviation "));
        aVisList.addConstant(devDays);
        aVisList.addFixedString(new FixedString(" days)"));
        
        ValueSpecifier[] params = new ValueSpecifier[4];
        params[0] = new MethodSpecifier(inputVar2, "getPropertyStartDate", new ValueSpecifier[0]);
        params[1] = new FixedString(type);
        params[2] = days;
        params[3] = devDays;
        ovar.setSpecifier(new MethodSpecifier(inputVar, "getResultClosestToDate", params));
        addDataGroup(aGroup);
        return aGroup; 
    }
    
    private DataGroup addResistanceResultRelativeToMedicationOutputGroup() {
        DataGroup aGroup = new DataGroup();
        VariableType inType = new VariableType("Patient");
        DataInputVariable inputVar = new DataInputVariable(inType);
        VariableType inType2 = new VariableType("PatientMedicatie");
        DataInputVariable inputVar2 = new DataInputVariable(inType2);
        VariableType outType = new VariableType("AlgorithmResult");
        DataOutputVariable ovar = new DataOutputVariable(outType, "Res");
        ovar.setUniqueName(ovar.getFormalName());
        DoubleConstant days = new DoubleConstant();
        DoubleConstant devDays = new DoubleConstant();
        devDays.setValue(new Double(30));
        Constant algoCst = new StringConstant();
        // warning : hard-coded format assumed to separate name from version in domain method getResistanceResultClosestToMedicationStart()
        algoCst.setSuggestedValuesQuery("SELECT DISTINCT NAME || ' Version ' || VERSION FROM COD_ALGORITHM");
        algoCst.setSuggestedValuesMandatory(true);
        OrderedDGWordList aVisList = aGroup.getVisualizationList();
        aVisList.addFixedString(new FixedString("Show "));
        aVisList.addDataInputVariable(inputVar);
        aVisList.addFixedString(new FixedString("'s resistance result "));
        aVisList.addDataOutputVariable(ovar);
        aVisList.addFixedString(new FixedString(" for the medication "));
        aVisList.addDataInputVariable(inputVar2);
        aVisList.addFixedString(new FixedString(" according to algorithm "));
        aVisList.addConstant(algoCst);
        aVisList.addFixedString(new FixedString(" which is closest to "));
        aVisList.addConstant(days);
        aVisList.addFixedString(new FixedString(" days after the medication start (maximum deviation "));
        aVisList.addConstant(devDays);
        aVisList.addFixedString(new FixedString(" days)"));
        
        ValueSpecifier[] params = new ValueSpecifier[4];
        params[0] = inputVar2;
        params[1] = algoCst;
        params[2] = days;
        params[3] = devDays;
        ovar.setSpecifier(new MethodSpecifier(inputVar, "getResistanceResultClosestToMedicationStart", params));
        addDataGroup(aGroup);
        return aGroup; 
    }
    
    private DataGroup addDateConstantAssociationGroup(String inTypeString, String outTypeString, String description, String methodString) {
        DataGroup aGroup = new DataGroup();
        VariableType inType = new VariableType(inTypeString);
        DataInputVariable inputVar = new DataInputVariable(inType);
        VariableType outType = new VariableType(outTypeString);
        DataOutputVariable ovar = new DataOutputVariable(outType, outTypeString.substring(0,1));
        ovar.setUniqueName(ovar.getFormalName());
        OrderedDGWordList aVisList = aGroup.getVisualizationList();
        aVisList.addFixedString(new FixedString("Show "));
        aVisList.addDataInputVariable(inputVar);
        aVisList.addFixedString(new FixedString("'s " + description));
        DateConstant cst = new DateConstant();
        aVisList.addConstant(cst);
        aVisList.addFixedString(new FixedString(" as "));
        aVisList.addDataOutputVariable(ovar);
        ValueSpecifier[] params = new ValueSpecifier[1];
        params[0] = cst;
        ovar.setSpecifier(new MethodSpecifier(inputVar, methodString, params));
        addDataGroup(aGroup);
        return aGroup;
    }
    
    private DataGroup addListAllAssociationsGroup(String inTypeString, String outTypeString, String description, String methodString) {
        DataGroup aGroup = new DataGroup();
        VariableType inType = new VariableType(inTypeString);
        DataInputVariable inputVar = new DataInputVariable(inType);
        VariableType outType = new VariableType(outTypeString);
        DoubleConstant sizeCst = new DoubleConstant();
        sizeCst.setValue(new Double(5));
        DataOutputVariable ovar = new ListDataOutputVariable(outType, sizeCst, outTypeString.substring(0,1));
        ovar.setUniqueName(ovar.getFormalName());
        OrderedDGWordList aVisList = aGroup.getVisualizationList();
        aVisList.addFixedString(new FixedString("Show "));
        aVisList.addDataInputVariable(inputVar);
        aVisList.addFixedString(new FixedString("'s " + description + "(max "));
        aVisList.addConstant(sizeCst);
        aVisList.addFixedString(new FixedString(") as "));
        aVisList.addDataOutputVariable(ovar);
        ValueSpecifier[] params = new ValueSpecifier[0];
        ovar.setSpecifier(new MethodSpecifier(inputVar, methodString, params));
        addDataGroup(aGroup);
        return aGroup;
    }
    
    private DataGroup addListAllAssociationsOfConfigurableTypeGroup(String inTypeString, String outTypeString, String description, String valuesQuery, String methodString) {
        DataGroup aGroup = new DataGroup();
        VariableType inType = new VariableType(inTypeString);
        DataInputVariable inputVar = new DataInputVariable(inType);
        VariableType outType = new VariableType(outTypeString);
        DoubleConstant sizeCst = new DoubleConstant();
        sizeCst.setValue(new Double(5));
        DataOutputVariable ovar = new ListDataOutputVariable(outType, sizeCst, outTypeString.substring(0,1));
        ovar.setUniqueName(ovar.getFormalName());
        OrderedDGWordList aVisList = aGroup.getVisualizationList();
        aVisList.addFixedString(new FixedString("Show "));
        aVisList.addDataInputVariable(inputVar);
        aVisList.addFixedString(new FixedString("'s " + description + " of type "));
        StringConstant typeCst = new StringConstant();
        typeCst.setSuggestedValuesQuery(valuesQuery);
        typeCst.setSuggestedValuesMandatory(true);
        aVisList.addConstant(typeCst);
        aVisList.addFixedString(new FixedString("(max "));
        aVisList.addConstant(sizeCst);
        aVisList.addFixedString(new FixedString(") as "));
        aVisList.addDataOutputVariable(ovar);
        ValueSpecifier[] params = new ValueSpecifier[1];
        params[0] = typeCst; 
        ovar.setSpecifier(new MethodSpecifier(inputVar, methodString, params));
        addDataGroup(aGroup);
        return aGroup;
    }
    
    private DataGroup addListAllAssociationsInRangeGroup(String inTypeString, String outTypeString, String description, String methodString) {
        DataGroup aGroup = new DataGroup();
        VariableType inType = new VariableType(inTypeString);
        DataInputVariable inputVar = new DataInputVariable(inType);
        VariableType outType = new VariableType(outTypeString);
        DoubleConstant sizeCst = new DoubleConstant();
        sizeCst.setValue(new Double(5));
        DataOutputVariable ovar = new ListDataOutputVariable(outType, sizeCst, outTypeString.substring(0,1));
        ovar.setUniqueName(ovar.getFormalName());
        OrderedDGWordList aVisList = aGroup.getVisualizationList();
        aVisList.addFixedString(new FixedString("Show "));
        aVisList.addDataInputVariable(inputVar);
        aVisList.addFixedString(new FixedString("'s " + description + "(max "));
        aVisList.addConstant(sizeCst);
        aVisList.addFixedString(new FixedString(") between "));
        DoubleConstant startCst = new DoubleConstant();
        startCst.setValue(new Double(0));
        aVisList.addConstant(startCst);
        aVisList.addFixedString(new FixedString(" and "));
        DoubleConstant endCst = new DoubleConstant();
        endCst.setValue(new Double(1000));
        aVisList.addConstant(endCst);
        aVisList.addFixedString(new FixedString(" as "));
        aVisList.addDataOutputVariable(ovar);
        ValueSpecifier[] params = new ValueSpecifier[2];
        params[0] = startCst;
        params[1] = endCst;
        ovar.setSpecifier(new MethodSpecifier(inputVar, methodString, params));
        addDataGroup(aGroup);
        return aGroup;
    }
    
    private DataGroup addListAllAssociationsInPeriodGroup(String inTypeString, String outTypeString, String description, String methodString) {
        DataGroup aGroup = new DataGroup();
        VariableType inType = new VariableType(inTypeString);
        DataInputVariable inputVar = new DataInputVariable(inType);
        VariableType outType = new VariableType(outTypeString);
        DoubleConstant sizeCst = new DoubleConstant();
        sizeCst.setValue(new Double(5));
        DataOutputVariable ovar = new ListDataOutputVariable(outType, sizeCst, outTypeString.substring(0,1));
        ovar.setUniqueName(ovar.getFormalName());
        OrderedDGWordList aVisList = aGroup.getVisualizationList();
        aVisList.addFixedString(new FixedString("Show "));
        aVisList.addDataInputVariable(inputVar);
        aVisList.addFixedString(new FixedString("'s " + description + "(max "));
        aVisList.addConstant(sizeCst);
        aVisList.addFixedString(new FixedString(") between "));
        DateConstant startCst = new DateConstant("1900-01-01");
        aVisList.addConstant(startCst);
        aVisList.addFixedString(new FixedString(" and "));
        DateConstant endCst = new DateConstant();
        aVisList.addConstant(endCst);
        aVisList.addFixedString(new FixedString(" as "));
        aVisList.addDataOutputVariable(ovar);
        ValueSpecifier[] params = new ValueSpecifier[2];
        params[0] = startCst;
        params[1] = endCst;
        ovar.setSpecifier(new MethodSpecifier(inputVar, methodString, params));
        addDataGroup(aGroup);
        return aGroup;
    }
    
    private DataGroup addListAllAssociationsOfConfigurableTypeInPeriodGroup(String inTypeString, String outTypeString, String description, String valuesQuery, String methodString) {
        DataGroup aGroup = new DataGroup();
        VariableType inType = new VariableType(inTypeString);
        DataInputVariable inputVar = new DataInputVariable(inType);
        VariableType outType = new VariableType(outTypeString);
        DoubleConstant sizeCst = new DoubleConstant();
        sizeCst.setValue(new Double(5));
        DataOutputVariable ovar = new ListDataOutputVariable(outType, sizeCst, outTypeString.substring(0,1));
        ovar.setUniqueName(ovar.getFormalName());
        OrderedDGWordList aVisList = aGroup.getVisualizationList();
        aVisList.addFixedString(new FixedString("Show "));
        aVisList.addDataInputVariable(inputVar);
        aVisList.addFixedString(new FixedString("'s " + description + " of type "));
        StringConstant typeCst = new StringConstant();
        aVisList.addConstant(typeCst);
        typeCst.setSuggestedValuesQuery(valuesQuery);
        typeCst.setSuggestedValuesMandatory(true);
        aVisList.addFixedString(new FixedString("(max "));
        aVisList.addConstant(sizeCst);
        aVisList.addFixedString(new FixedString(") between "));
        DateConstant startCst = new DateConstant("1900-01-01");
        aVisList.addConstant(startCst);
        aVisList.addFixedString(new FixedString(" and "));
        DateConstant endCst = new DateConstant();
        aVisList.addConstant(endCst);
        aVisList.addFixedString(new FixedString(" as "));
        aVisList.addDataOutputVariable(ovar);
        ValueSpecifier[] params = new ValueSpecifier[3];
        params[0] = startCst;
        params[1] = endCst;
        params[2] = typeCst;
        ovar.setSpecifier(new MethodSpecifier(inputVar, methodString, params));
        addDataGroup(aGroup);
        return aGroup;
    }
    
    private DataGroup addListAllDoubleAssociationsGroup(String inTypeString, String outTypeString, String description, String in2TypeString, String methodString) {
        DataGroup aGroup = new DataGroup();
        VariableType inType = new VariableType(inTypeString);
        DataInputVariable inputVar = new DataInputVariable(inType);
        VariableType in2Type = new VariableType(in2TypeString);
        DataInputVariable inputVar2 = new DataInputVariable(in2Type);
        VariableType outType = new VariableType(outTypeString);
        DoubleConstant sizeCst = new DoubleConstant();
        sizeCst.setValue(new Double(3));
        DataOutputVariable ovar = new ListDataOutputVariable(outType, sizeCst, outTypeString.substring(0,1));
        ovar.setUniqueName(ovar.getFormalName());
        OrderedDGWordList aVisList = aGroup.getVisualizationList();
        aVisList.addFixedString(new FixedString("Show "));
        aVisList.addDataInputVariable(inputVar);
        aVisList.addFixedString(new FixedString("'s " + description));
        aVisList.addDataInputVariable(inputVar2);
        aVisList.addFixedString(new FixedString("(max "));
        aVisList.addConstant(sizeCst);
        aVisList.addFixedString(new FixedString(") as "));
        aVisList.addDataOutputVariable(ovar);
        ValueSpecifier[] params = new ValueSpecifier[1];
        params[0] = inputVar2;
        ovar.setSpecifier(new MethodSpecifier(inputVar, methodString, params));
        addDataGroup(aGroup);
        return aGroup;
    }
    
    private DataGroup addNthDateAssociationGroup(String inTypeString, String outTypeString, String description, String methodString, boolean before) {
        DataGroup aGroup = new DataGroup();
        VariableType inType = new VariableType(inTypeString);
        DataInputVariable inputVar = new DataInputVariable(inType);
        VariableType outType = new VariableType(outTypeString);
        DataOutputVariable ovar = new DataOutputVariable(outType, outTypeString.substring(0,1));
        ovar.setUniqueName(ovar.getFormalName());
        OrderedDGWordList aVisList = aGroup.getVisualizationList();
        aVisList.addFixedString(new FixedString("Show "));
        aVisList.addDataInputVariable(inputVar);
        aVisList.addFixedString(new FixedString("'s "));
        DoubleConstant dcst = new DoubleConstant();
        dcst.setValue(new Double(1));
        aVisList.addConstant(dcst);
        aVisList.addFixedString(new FixedString(description));
        DateConstant cst = (before ? new DateConstant() : new DateConstant("1900-01-01"));
        aVisList.addConstant(cst);
        aVisList.addFixedString(new FixedString(" as "));
        aVisList.addDataOutputVariable(ovar);
        ValueSpecifier[] params = new ValueSpecifier[2];
        params[0] = cst;
        params[1] = dcst;
        ovar.setSpecifier(new MethodSpecifier(inputVar, methodString, params));
        addDataGroup(aGroup);
        return aGroup;
    }
        
}
