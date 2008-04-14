
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

import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;

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
            mainCatalog = HibernateAWCPrototypeCatalog.getInstance();
        }
        return mainCatalog;
    }
    
    public static AWCPrototypeCatalog getInstance(boolean hibernate) {
        if (mainCatalog == null) {
           mainCatalog = HibernateAWCPrototypeCatalog.getInstance();
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
        catalog.addTimeLowerThanClause();
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
        String[][] assocPatientToTherapy = {{"patient", null, "PATIENT_II"}, {"therapy", "PATIENT_II",null}};
        catalog.addGetRemoteAssociationClause(assocPatientToTherapy, "has received therapy");

        
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
        
        // link viral isolate - nt sequence
        catalog.addGetAssociationClause("viral_isolate", "VIRAL_ISOLATE_II", "nt_sequence", "NT_SEQUENCE_II", "has a nucleotide sequence");

        // link patient - nt sequence
        String[][] assocPatientToNtSequence = {{"patient", null, "PATIENT_II"}, {"viral_isolate", "PATIENT_II","VIRAL_ISOLATE_II"},
                {"nt_sequence", "VIRAL_ISOLATE_II", null}};
        catalog.addGetRemoteAssociationClause(assocPatientToNtSequence, "has a nucleotide sequence");

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
   		catalog.addStringClauses(catalog, "drug_generic", "GENERIC_NAME", "has name", false, true);
   		catalog.addStringClauses(catalog, "drug_generic", "ATC_CODE", "has atc code", false, true);

        // link therapy - generic drug
        String[][] assocListGenericDrugToTherapy = {{"therapy", null, "THERAPY_II"}, {"therapy_generic", "THERAPY_II","GENERIC_II"},
                {"drug_generic", "GENERIC_II", null}};
        catalog.addGetRemoteAssociationClause(assocListGenericDrugToTherapy, "was treated with the generic drug");
        

        ///////////////////////////////////////
        // commercial drug
        catalog.addBaseClause("drug_commercial");
   		catalog.addStringClauses(catalog, "drug_commercial", "NAME", "has name", false, true);
   		catalog.addStringClauses(catalog, "drug_commercial", "ATC_CODE", "has atc code", false, true);
        
        // link therapy - commercial drug
        String[][] assocListCommercialDrugToTherapy = {{"therapy", null, "THERAPY_II"}, {"therapy_commercial", "THERAPY_II","COMMERCIAL_II"},
                {"drug_commercial", "COMMERCIAL_II", null}};
        catalog.addGetRemoteAssociationClause(assocListCommercialDrugToTherapy, "was treated with the commercial drug");
        
        // link comercial - generic
        String[][] assocListCommercialDrugToGenericDrug = {{"drug_commercial", null, "COMMERCIAL_II"}, {"commercial_generic", "COMMERCIAL_II","GENERIC_II"},
                {"drug_generic", "GENERIC_II", null}};
        catalog.addGetRemoteAssociationClause(assocListCommercialDrugToGenericDrug, "has a generic equivalent");
        
        mainCatalog = catalog;
    }
    
    private void addDateClauses (AWCPrototypeCatalog catalog, String tableName, String propertyName, String description) {
        catalog.addGetPropertyClause(tableName, propertyName, description + " date");
        catalog.addPropertyTimeIntervalClause(tableName, propertyName, description + " date", false);
        catalog.addPropertyCheckClause(tableName, propertyName, description + " date", false, false);
    }

    private void addStringClauses(AWCPrototypeCatalog catalog, String tableName, String propertyName, String description, boolean caseSensitive, boolean dropdown) {
    	if (dropdown) {
            catalog.addMandatoryValuesToClause(
            		catalog.addPropertyCheckClause(tableName, propertyName, description, false, caseSensitive),
            		new String[] {tableName},
            		new String[] {propertyName});
    	}
    	else {
    		catalog.addPropertyCheckClause(tableName, propertyName, description, false, caseSensitive);
    	}
		
		catalog.addPropertyLikeClause(tableName, propertyName, description, false, caseSensitive);
        catalog.addPropertyStartsLikeClause(tableName, propertyName, description, false, caseSensitive);
        catalog.addPropertyEndsLikeClause(tableName, propertyName, description, false, caseSensitive);
    }
    
    private void addStringClauses(AWCPrototypeCatalog catalog, String tableName, String propertyName, String description, boolean caseSensitive) {
    	addStringClauses(catalog, tableName, propertyName, description, caseSensitive, false);
    }
    
    ///////////////////////////////////////
    // associations
    
    private Map<String, String> typeNameToGoodVariableName = new HashMap<String, String>();
    private List<AtomicWhereClause> atomicWhereClauses = new ArrayList<AtomicWhereClause>();
    
    ///////////////////////////////////////
    // access methods for associations
    
    public final Table getTable(String name) {
        return DatabaseManager.getInstance().getTableCatalog().doGetTable(name);
    }
    
    public void addAtomicWhereClause(AtomicWhereClause atomicWhereClause) {
        this.atomicWhereClauses.add(atomicWhereClause);
    }
    
    private void addGoodVariableName(String typeName, String varName) {
        typeNameToGoodVariableName.put(typeName.toLowerCase(), varName);
    }
    
    public String getVariableName(String tableName) {
        String varName = (String)typeNameToGoodVariableName.get(tableName.toLowerCase());
        if (varName == null) {
            return tableName.substring(0, 1);
        } else {
            return varName;
        }
    }
    
    public String getTableDescription(String tableName) {
    	return tableName.toLowerCase();
    }
    
    public String getTableAlias(String tableName) {
    	return tableName.toLowerCase();
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
        if (manager != null) {
        	return manager.getDatabaseConnector().getColumnType(tableName, propertyName);
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
        OutputVariable ovar = new OutputVariable(new VariableType(typeString), getVariableName(propertyName));
        ovar.setUniqueName(ovar.getFormalName());
    	return ovar;
    }
    
    private boolean isStringType(String dataTypeString) {
    	return isStringType(Integer.parseInt(dataTypeString));
    }
    
    private boolean isStringType(int dataType) {
    	return dataType == 12;
    }
    
    private boolean isDateType(int dataType) {
    	return (dataType >= 91) && (dataType <= 93);
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
	    	Iterator<Constant> it = clause.getConstants().iterator();
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
    public Collection<AtomicWhereClause> getAWCPrototypes(Collection availableOutputVariables) {
        // your code here
        Collection<AtomicWhereClause> result = new ArrayList<AtomicWhereClause>();
        Iterator<AtomicWhereClause> iter = atomicWhereClauses.iterator();
        while (iter.hasNext()) {
            AtomicWhereClause clause = (AtomicWhereClause)iter.next();
            boolean clauseOk = true;
            Iterator<InputVariable> inputIter = clause.getInputVariables().iterator();
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
    private AtomicWhereClause addBaseClause(String tableName) {
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
    
    /**
     * allow users to search for an identical property match
     * @param tableName
     * @param propertyName
     * @param description
     * @param show  
     * @param caseSensitive
     * @return
     */
    private AtomicWhereClause addPropertyCheckClause(String tableName, String propertyName, String description, boolean show, boolean caseSensitive) {
    	Properties p = getDataTypeDependantProperties(tableName, propertyName);   	
    	if (p != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
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
    
    private AtomicWhereClause addPropertyLikeClause(String tableName, String propertyName, String description, boolean show, boolean caseSensitive, Constant likeConstant, String constantDescription) {
    	Properties p = getDataTypeDependantProperties(tableName, propertyName);   	
    	if (p != null) {
            String dataTypeString = (String) p.get("dataTypeString");
            String typeString = (String) p.get("typeString");
            Constant constant = likeConstant;
            
            if (isStringType(dataTypeString)) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
	            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
	
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
    private AtomicWhereClause addPropertyLikeClause(String tableName, String propertyName, String description, boolean show, boolean caseSensitive) {
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
    private AtomicWhereClause addPropertyEndsLikeClause(String tableName, String propertyName, String description, boolean show, boolean caseSensitive) {
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
    private AtomicWhereClause addPropertyStartsLikeClause(String tableName, String propertyName, String description, boolean show, boolean caseSensitive) {
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
    private AtomicWhereClause addGetPropertyClause(String tableName, String propertyName, String description) {
    	Properties p = getDataTypeDependantProperties(tableName, propertyName);   	
    	if (p != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            String typeString = (String) p.get("typeString");

            description			= description == null ? "has a " + propertyName : description;
            InputVariable ivar  = new InputVariable(new VariableType(tableName));
            OutputVariable ovar = getOutputVariable(typeString, propertyName, ivar);
            composeHumanReadableQuery(aClause.getVisualizationClauseList(), tableName, ivar, propertyName, description, null, ovar, null);
            
            aClause.getWhereClauseComposer().addFixedString(new FixedString("1=1"));
            addAtomicWhereClause(aClause);
            return aClause;
    	}
    	return null;
    }

    
    private AtomicWhereClause addGetAssociationClause(String tableName, String foreignKeyName, String foreignTableName, String foreignTableKey) {
        return addGetAssociationClause(tableName, foreignKeyName, foreignTableName, foreignTableKey, null);
    }
    
    private AtomicWhereClause addGetAssociationClause(String tableName, String foreignKeyName, String foreignTableName, String foreignTableKey, String description) {
        DatabaseManager manager = DatabaseManager.getInstance();
        if (manager != null) {
            String dataTypeString = manager.getDatabaseConnector().getColumnType(tableName, foreignKeyName);
            if (dataTypeString != null) {
                AtomicWhereClause aClause = new AtomicWhereClause();
                VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
                WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
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
                OutputVariable ovar = new OutputVariable(new VariableType(foreignTableName), getVariableName(foreignTableName));
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
    
    private AtomicWhereClause addGetRemoteAssociationClause(String[][] args, String description) {
        DatabaseManager manager = DatabaseManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
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
                    OutputVariable ovar = new OutputVariable(new VariableType(newTableName), getVariableName(newTableName));
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
    
        
        private AtomicWhereClause addCodedPropertyCheckClause(String tableName, String codeName, String codeTableName, String codeKeyName, String propertyName, String description, boolean show) {
            DatabaseManager manager = DatabaseManager.getInstance();
            if (manager != null) {
                String dataTypeString = manager.getDatabaseConnector().getColumnType(codeTableName, propertyName);
                if (dataTypeString != null) {
                    int dataType = Integer.parseInt(dataTypeString);
                    VariableType varType;
                    AtomicWhereClause aClause = new AtomicWhereClause();
                    VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
                    WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
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
                        OutputVariable ovar = new OutputVariable(varType, getVariableName(codeName));
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
                    return aClause;
                } else {
                    System.err.println("Unknown column " + propertyName + " for " + tableName);
                    return null;
                }
            } else {
                return null;
            }
        }
        
    
    
    
    
    private AtomicWhereClause addPropertyTimeIntervalClause(String tableName, String propertyName, String description, boolean show) {
    	DatabaseManager manager = DatabaseManager.getInstance();
        if (manager != null) {
            String dataTypeString = manager.getDatabaseConnector().getColumnType(tableName, propertyName);
            if (dataTypeString != null) {
                int dataType = Integer.parseInt(dataTypeString);
                if ((dataType >= 91) && (dataType <= 93)) {
                    AtomicWhereClause aClause = new AtomicWhereClause();
                    VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
                    WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
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
	                    ovar = new OutputVariable(new VariableType("Date"), getVariableName(propertyName));
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
    
    private AtomicWhereClause addTimeConstantClause(boolean before) {
        DatabaseManager manager = DatabaseManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
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
    
    private AtomicWhereClause addTimeIntervalClause() {
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
    
    private AtomicWhereClause addTimeConstantToVariableClause() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aVisList.addFixedString(new FixedString("Date"));
        Constant dateConstant = new DateConstant();
        OutputVariable ovar = new OutputVariable(new VariableType("Date"), getVariableName("Date"));
        ovar.setUniqueName(ovar.getFormalName());
        ovar.getExpression().addConstant(dateConstant);
        aVisList.addOutputVariable(ovar);
        aVisList.addFixedString(new FixedString("is"));
        aVisList.addConstant(dateConstant);
        
        aComposer.addFixedString(new FixedString("1=1"));
        
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    
    private AtomicWhereClause addTimeLowerThanClause() {
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
    
    private AtomicWhereClause addTimeCalculationClause(boolean plus) {
        DatabaseManager manager = DatabaseManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
            aVisList.addFixedString(new FixedString("Date "));
            InputVariable ivar = new InputVariable(new VariableType("Date"));
            
            OutputVariable ovar = new OutputVariable(new VariableType("Date"), getVariableName("Date"));
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
    
    private AtomicWhereClause addRealValueConstraintClause(boolean below) {
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
} // end AWCPrototypeCatalog
