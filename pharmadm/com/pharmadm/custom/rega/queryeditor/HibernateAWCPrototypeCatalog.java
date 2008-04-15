package com.pharmadm.custom.rega.queryeditor;

import java.sql.SQLException;
import java.util.*;

import com.pharmadm.custom.rega.queryeditor.constant.BooleanConstant;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.constant.DateConstant;
import com.pharmadm.custom.rega.queryeditor.constant.DoubleConstant;
import com.pharmadm.custom.rega.queryeditor.constant.OperatorConstant;
import com.pharmadm.custom.rega.queryeditor.constant.StringConstant;
import com.pharmadm.custom.rega.queryeditor.constant.SuggestedValuesOption;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.port.QueryResult;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.TestType;

public class HibernateAWCPrototypeCatalog extends AWCPrototypeCatalog {
	    
    private static AWCPrototypeCatalog mainCatalog = null;
	    
    private Map<String, String> objectNameToVariableName = new HashMap<String, String>();
    private Map<String, String> tableNameToAlias = new HashMap<String, String>();
    private Map<String, String> tableNameToDescription = new HashMap<String, String>();
    private List<AtomicWhereClause> atomicWhereClauses = new ArrayList<AtomicWhereClause>();

    public static AWCPrototypeCatalog getInstance() {
        if (mainCatalog == null) {
            initMainCatalog();
        }
        return mainCatalog;
    }
	    

    private static void initMainCatalog() {
    	HibernateAWCPrototypeCatalog catalog = new HibernateAWCPrototypeCatalog();
        mainCatalog = catalog;

        catalog.addVariableNames();	    
        catalog.addNumberClauses();
        catalog.addStringClauses();
        catalog.addBooleanClauses();
        catalog.addDateClauses();
        catalog.addAllTableClauses();
    }

    public void addAtomicWhereClause(AtomicWhereClause atomicWhereClause) {
        this.atomicWhereClauses.add(atomicWhereClause);
    }
	    
    /**
     * Adds a plain english description for the persistent object with the given name
     * @param objectName name of the persistent object
     * @param description description for the persistent object
     */
    private void addTableDescription(String objectName, String description) {
    	tableNameToDescription.put(objectName.toLowerCase(), description);
    }
	    
    /**
     * gets the plain english description for the persistent object with the given name
     * if no plain english description has been set the simple class name of the object
     * will be used
     * @param objectName name of the persistent object
     * @return a description for the given persistent object
     */
    public String getTableDescription(String objectName) {
        String varName = (String)tableNameToDescription.get(objectName.toLowerCase());
        if (varName == null) {
            return objectName.substring(objectName.lastIndexOf('.')+1);
        } else {
            return varName;
        }
    }
    
    /**
     * adds an variable name to the given object 
     * @param objectName either the class name of a persistent object
     *                   or the property of a persistent object
     * @param variableName name for the variable
     */
    private void addVariableName(String objectName, String variableName) {
        objectNameToVariableName.put(objectName.toLowerCase(), variableName);
    }

    
    /**
     * gets the variable name to the given object 
     * if no variable name is specified, the first letter of the object or property name is used
     * @param objectName either the class name of a persistent object
     *                   or the property of a persistent object
     */
    public String getVariableName(String objectName) {
        String varName = (String)objectNameToVariableName.get(objectName.toLowerCase());
        if (varName == null) {
        	varName = objectName.substring(objectName.lastIndexOf('.')+1);
            return varName.substring(0, 1);
        } else {
            return varName;
        }
    }
	    
    /**
     * adds an hql alias for the given persistent object
     * @param objectName name of the persistent object
     * @param alias alias for the given persistent object
     * @pre	alias must be a valid hql alias
     */
    private void addTableAlias(String objectName, String alias) {
    	tableNameToAlias.put(objectName.toLowerCase(), alias);
    }

    /**
     * gets the hql alias for the persistent object with the given name
     * if no hql alias has been set the simple class name of the object
     * will be used
     * @param objectName name of the persistent object
     * @return an alias for the given persistent object
     */
    public String getTableAlias(String objectName) {
        String varName = (String)tableNameToAlias.get(objectName.toLowerCase());
        if (varName == null) {
            return objectName.substring(objectName.lastIndexOf('.')+1);
        } else {
            return varName;
        }
    }
	    
    /**
     * returns true if a table with the given name exists in the database
     * @param tableName the name of a table
     * @return true if the table exists
     *         false if it doesn't
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
     * returns the sql data type string from the given property of the given table.
     * returns null if the table or property is not found
	 * @param tableName table to check the property of
	 * @param propertyName the property to check
	 *                     this must be a property of the given table or of its composite id
	 *                     If it is a property of the composite id, include
	 *                     the id in the path, like so: id.property
     * @return the data type string of the given property
     */
    private String getDataTypeString(String tableName, String propertyName) {
        DatabaseManager manager = DatabaseManager.getInstance();
        String typeString = null;
        if (manager != null) {
        	typeString = manager.getDatabaseConnector().getColumnType(tableName, propertyName);
        }
        else {
            System.err.println("Unknown column " + propertyName + " for " + tableName);
        }
        return typeString;
    }
	    
	/**
	 * returns true if the given sql data type number belongs to a string
	 * @param dataType an sql data type number
	 * @return true when the data type is a string
	 */
    private boolean isStringType(int dataType) {
    	return dataType == 12;
    }

	/**
	 * returns true if the given sql data type number belongs to a boolean
	 * @param dataType an sql data type number
	 * @return true when the data type is a boolean
	 */
    private boolean isBooleanType(int dataType) {
    	return dataType == -7;
    }
    
	/**
	 * returns true if the given sql data type number belongs to a date
	 * @param dataType an sql data type number
	 * @return true when the data type is a date
	 */
    private boolean isDateType(int dataType) {
    	return (dataType >= 91) && (dataType <= 93);
    }
    
	/**
	 * returns true if the given sql data type number belongs to a numeric value
	 * @param dataType an sql data type number
	 * @return true when the data type is a number
	 */
    private boolean isNumericType(int dataType) {
    	return (((8 >= dataType) && (dataType >=1)) || dataType == 1111 || dataType == -5);
    }
	    
	/**
     * find the data type of the property with the given name of the given table
     * this method can resolve properties of composite ids
     * returns null if the property is not found
	 * @param tableName table to check the property of
	 * @param propertyName the property to check
	 *                     this must be a property of the given table or of its composite id
	 *                     If it is a property of the composite id, include
	 *                     the id in the path, like so: id.property
	 * @return the data type of the property
	 * 		  String for strings
	 *        Numeric for numbers
	 *        Boolean for booleans
	 *        Date for dates
	 */
    private String getDataTypeOfProperty(String tableName, String propertyName) {
    	String valueType = null;
    	
    	String dataTypeString = getDataTypeString(tableName, propertyName);
    	if (dataTypeString != null) {
    		int dataType = Integer.parseInt(dataTypeString);
	    	
	        if (isStringType(dataType)) {
	        	valueType = "String";
	        }
	        else if (isDateType(dataType)) {
	        	valueType = "Date";
	        }
	        else if (isNumericType(dataType)) {
	        	valueType = "Numeric";
	        }
	        else if (isBooleanType(dataType)) {
	        	valueType = "Boolean";
	        }
	        
	        else {
                System.err.println("Unknown data type found for " + tableName + "." + propertyName + ": " + dataType);
	        }
    	}
    	return valueType;
    }
	    
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
    }
	    
	/**
	 * add the clauses for a given custom property
	 * @param propertyName name of the custom property
	 * @param valueType type of the custom properties. One of the types from the ValueType table.
	 *                  currently supported: nominal value
	 *                                       string
	 *                                       number
	 *                                       limited number (<,=,>)
	 * @param customPropertiesTable the table containing the names of all the custom properties
	 * @param nominalValuesTable The table containing all the possible nominal values
	 * @param idTableName table that holds:
	 * 				the references to the custom properties table
	 *              the reference to the nominal values table
	 *              the regular property value if it is not a nominal value
	 * @param idTableToCustomPropertiesTable path from the id table to the custom properties table
	 * @param idTableToNominalValuesTable path from the id table to the nominal values table
	 * @param nominalValuesTableToCustomPropertiesTable path from the nominal values table to the custom properties table
	 * @param inputTableName table to start from
	 * @param idTableToInputTable path from the id table to the input table
	 *                            null if they are the same table
	 * @param customPropertiesTableNameProperty The attribute of the custom properties table that points to it's name
	 */
	private void addCustomPropertyComparisonClauses(String propertyName, String valueType, String customPropertiesTable, String nominalValuesTable, String idTableName, String idTableToCustomPropertiesTable, String idTableToNominalValuesTable, String nominalValuesTableToCustomPropertiesTable, String inputTableName, String idTableToInputTable, String customPropertiesTableNameProperty) {
    	boolean caseSensitive = true;		// default comparison is case sensitive
    	String property = "value";			// regular value is always found in the value property
    	String description = propertyName;	// use name of the property as description
    	String realVariableType = "String"; // all values are stored as strings
    	
    	String inputTable = inputTableName;		  // use input table as starting point
    	String foreingTableName = inputTableName; // start with foreign table and id table same as input table
    	String inputTableToIdTable = null;
    	String idTable = inputTableName;	      // start with id table same as input table		
    	String foreignTableToIdTable = null;
    	
    	if (valueType.equals("nominal value")) {
    		foreingTableName = nominalValuesTable;			// select from the table of nominal values
    		foreignTableToIdTable = idTableToNominalValuesTable;	
    		idTable = idTableName;							// use the id table as the id 
    		inputTableToIdTable = idTableToInputTable;

    		String suggestedValuesQuery = "\nSELECT DISTINCT\n\tnv.value\nFROM\n\t" + nominalValuesTable + " nv,\n\t" + customPropertiesTable + " obj\nWHERE\n\tnv." + nominalValuesTableToCustomPropertiesTable + " = obj AND\n\tobj." + customPropertiesTableNameProperty + "='" + propertyName + "'";
            addTypeRestrictionToNominalValueClause(addStringPropertyComparisonClauses(inputTable, inputTableToIdTable, foreingTableName, foreignTableToIdTable, idTable, null, property, description, suggestedValuesQuery, caseSensitive, realVariableType, true),idTableToCustomPropertiesTable, propertyName, customPropertiesTableNameProperty, valueType);
    	}
    	else if (valueType.equals("string")) {
    		foreingTableName = idTableName;					// select from the single attribute table
    		foreignTableToIdTable = idTableToInputTable;	
    		caseSensitive = false;
    		
            addTypeRestrictionToNominalValueClause(addStringPropertyComparisonClauses(inputTable, inputTableToIdTable, foreingTableName, foreignTableToIdTable, idTable, null, property, description, null, caseSensitive, realVariableType, false),idTableToCustomPropertiesTable, propertyName, customPropertiesTableNameProperty, valueType);
    	}
    	else if (valueType.equals("number")) {
    		foreingTableName = idTableName;					// select from the single attribute table
    		foreignTableToIdTable = idTableToInputTable;	
    		caseSensitive = true;
    		
            addTypeRestrictionToNominalValueClause(addNumberPropertyComparisonClauses(inputTable, inputTableToIdTable, foreingTableName, foreignTableToIdTable, idTable, null, property, description, null, caseSensitive, realVariableType, false),idTableToCustomPropertiesTable, propertyName, customPropertiesTableNameProperty, valueType);
    	}
    	else if (valueType.equals("limited number (<,=,>)")) {
    		foreingTableName = idTableName;					// select from the single attribute table
    		foreignTableToIdTable = idTableToInputTable;	
    		caseSensitive = true;
    		
            addTypeRestrictionToNominalValueClause(addNumberPropertyComparisonClauses(inputTable, inputTableToIdTable, foreingTableName, foreignTableToIdTable, idTable, null, property, description, null, caseSensitive, realVariableType, false),idTableToCustomPropertiesTable, propertyName, customPropertiesTableNameProperty, valueType);
    	}
    	else {
    		System.err.println("Unknown value type " + valueType + ":" + customPropertiesTable + "." + propertyName);
    	}
	}
		
	/**
	 * Adds an additional part to the outputvariables of the clauses in the given list to translates the types
	 * of their outputvariables from string to the given valueType
	 * Adds an additional part to the where clause of the clauses in the given list to restrict the results to
	 * properties of the given propertyName
	 * @param clauses list of clauses
	 * @param idTableToCustomPropertiesTable path from the id table (the first table (from or inputvariable) in the clauses)
	 *                                       to the custom properties table
	 * @param propertyName name of the custom property
	 * @param customPropertiesTableNameProperty The attribute of the custom properties table that points to it's name
	 * @param valueType type of the custom properties. One of the types from the ValueType table.
	 *                  currently supported: nominal value
	 *                                       string
	 *                                       number
	 *                                       limited number (<,=,>)
	 */
	private void addTypeRestrictionToNominalValueClause(List<AtomicWhereClause> clauses, String idTableToCustomPropertiesTable, String propertyName, String customPropertiesTableNameProperty, String valueType) {
		for (AtomicWhereClause clause : clauses) {

			//// add extra condition to where clause
			//
			
			WhereClauseComposer aComposer = clause.getWhereClauseComposer();
    		// if inputtable != foreignTable => fromvariable
    		// else => inputvariable
    		aComposer.addFixedString(new FixedString(" "));
	    	if (clause.getFromVariables().isEmpty()) {
	    		// empty constants list means no previous statements and thus no AND needed
	    		if (!clause.getConstants().isEmpty()) {
		    		aComposer.addFixedString(new FixedString(" AND\n\t"));
	    		}
	    		aComposer.addInputVariable(clause.getInputVariables().iterator().next());
	    	}
	    	else {
	    		aComposer.addFixedString(new FixedString(" AND\n\t"));
	    		aComposer.addFromVariable(clause.getFromVariables().iterator().next());
	    	}
    		aComposer.addFixedString(new FixedString("." + idTableToCustomPropertiesTable + "." + customPropertiesTableNameProperty + " = '" + propertyName + "'"));
    		
    		//// wrap outputvariable when it should be interpreted as a number
    		//
    		
    		if (!clause.getOutputVariables().isEmpty() ) {
    			OutputVariable ovar = clause.getOutputVariables().iterator().next();
    			if (valueType.equals("number")) {
	    			List<ConfigurableWord> words = ovar.getExpression().getWords();
	    			List<ConfigurableWord> newWords = new ArrayList<ConfigurableWord>();
	    			newWords.add(new FixedString("CASE WHEN "));
	    			newWords.add(words.get(0));
	    			newWords.add(new FixedString("." + idTableToCustomPropertiesTable + "." + customPropertiesTableNameProperty + " = '" + propertyName + "'"));
	    			newWords.add(new FixedString("THEN cast ("));
	    			newWords.addAll(words);
	    			newWords.add(new FixedString(", big_decimal) ELSE 0 END"));
	    			ovar.getExpression().setWords(newWords);
    			}
    			else if (valueType.equals("limited number (<,=,>)")) {
	    			List<ConfigurableWord> words = ovar.getExpression().getWords();
	    			List<ConfigurableWord> newWords = new ArrayList<ConfigurableWord>();
	    			newWords.add(new FixedString("CASE WHEN "));
	    			newWords.add(words.get(0));
	    			newWords.add(new FixedString("." + idTableToCustomPropertiesTable + "." + customPropertiesTableNameProperty + " = '" + propertyName + "'"));
	    			newWords.add(new FixedString(" THEN ("));
	    			newWords.add(new FixedString("CASE WHEN substring("));
	    			newWords.addAll(words);
	    			newWords.add(new FixedString(", 1 , 1) in ('<', '>', '=') THEN cast(substring("));
	    			newWords.addAll(words);
	    			newWords.add(new FixedString(", 2, length("));
	    			newWords.addAll(words);
	    			newWords.add(new FixedString(") ), big_decimal) ELSE cast("));
	    			newWords.addAll(words);
	    			newWords.add(new FixedString(", big_decimal) END"));
	    			newWords.add(new FixedString(") ELSE 0 END"));
	    			ovar.getExpression().setWords(newWords);
    			}
    		}
		}
	}

    /**
     * Adds clauses to check for a relation between the given input table and foreign table
     * and vice-versa
	 * @param inputTableName table to start from
	 * @param inputTableToIdTable path from the input table to the id table 
	 *                            null if they are the same table
	 * @param foreignTableName name of the table to check the relationship with
	 * @param foreignTableToIdTable path from the foreign table to the id table
	 *                              null if they are the same table
	 * @param idTableName table that acts as key between the input table and the foreign table
	 * @param idTableKey element of idTableName that is the key
	 *                   null if the table should be used as key
     * @param description1 A description of the relation between the input table and the foreign table
     * @param description2 A description of the relation between the foreign table and the input table
	 * @param invertLink false for a relation from input and foreigntable to idtable
	 *                   true for a relation from idtable to input and foreigntable
     * @return All the clause that have been added
     */
    private List<AtomicWhereClause> addRelationClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String description1, String description2, boolean invertLink) {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
        list.add(addRelationClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, description1, invertLink));
        list.add(addRelationClause(foreignTableName, foreignTableToIdTable, inputTableName, inputTableToIdTable, idTableName, idTableKey, description2, invertLink));
        return list;
    }
	    
    /**
     * Add a clause to check for a relation between the given input table and foreign table
	 * @param inputTableName table to start from
	 * @param inputTableToIdTable path from the input table to the id table 
	 *                            null if they are the same table
	 * @param foreignTableName name of the table to check the relationship with
	 * @param foreignTableToIdTable path from the foreign table to the id table
	 *                              null if they are the same table
	 * @param idTableName table that acts as key between the input table and the foreign table
	 * @param idTableKey element of idTableName that is the key
	 *                   null if the table should be used as key
     * @param description A description of the relation between the input table and the foreign table
	 * @param invertLink false for a relation from input and foreigntable to idtable
	 *                   true for a relation from idtable to input and foreigntable
     * @return The clause that has been added
     */
    private AtomicWhereClause addRelationClause(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String description, boolean invertLink) {
        DatabaseManager manager = DatabaseManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
            
            InputVariable ivar = new InputVariable(new VariableType(inputTableName));
            description = description == null?"have an associated " + getTable(foreignTableName).getSingularName():description;
            FromVariable newFromVar = new FromVariable(foreignTableName);
            OutputVariable ovar = new OutputVariable(new VariableType(foreignTableName), getVariableName(foreignTableName));
            ovar.setUniqueName(ovar.getFormalName());
            ovar.getExpression().addFromVariable(newFromVar);
            
            aVisList.addFixedString(new FixedString("The " + getTable(inputTableName).getSingularName()));
            aVisList.addInputVariable(ivar);
            aVisList.addFixedString(new FixedString(description));
            aVisList.addOutputVariable(ovar);
            
            addRelationClauseToComposer(aComposer, ivar, newFromVar, inputTableToIdTable, foreignTableToIdTable, idTableName, idTableKey, invertLink);
            
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
    
    /**
     * adds a to clause to the given where clause composer to link the given input table to the given foreigntable
     * @param aComposer where clause composer to add the relation to
     * @param inputTable table to start from
     * @param foreignTable name of the table to make a relation with
	 * @param inputTableToIdTable path from the input table to the id table 
	 *                            null if they are the same table
	 * @param foreignTableToIdTable path from the foreign table to the id table
	 *                              null if they are the same table
	 * @param idTableName table that acts as key between the input table and the foreign table
	 * @param idTableKey element of idTableName that is the key
	 *                   null if the table should be used as key
	 * @param invertLink false for a relation from input and foreigntable to idtable
	 *                   true for a relation from idtable to input and foreigntable
     */
    private void addRelationClauseToComposer(WhereClauseComposer aComposer, InputVariable inputTable, FromVariable foreignTable, String inputTableToIdTable, String foreignTableToIdTable, String idTableName, String idTableKey, boolean invertLink) {
    	if (!invertLink) {
    		// regular link between input table and foreign table
    		// check if they point to the same id table
            aComposer.addInputVariable(inputTable);
            aComposer.addFixedString(new FixedString((inputTableToIdTable != null ? "." + inputTableToIdTable: "") + (idTableKey != null ?"." + idTableKey:"") + " = "));
            aComposer.addFromVariable(foreignTable);
            aComposer.addFixedString(new FixedString((foreignTableToIdTable != null ? "." + foreignTableToIdTable:"") + (idTableKey != null ?"." + idTableKey:"")));
    	}
    	else {
    		// inverted link
    		// go from the id table to the input table and foreign table
            FromVariable fromVarId = new FromVariable(idTableName);
            aComposer.addFromVariable(fromVarId);
            aComposer.addFixedString(new FixedString((inputTableToIdTable != null ? "." + inputTableToIdTable: "") + " = "));
            aComposer.addInputVariable(inputTable);
            aComposer.addFixedString(new FixedString(" AND "));
            aComposer.addFromVariable(fromVarId);
            aComposer.addFixedString(new FixedString((foreignTableToIdTable != null ? "." + foreignTableToIdTable: "") + " = "));
            aComposer.addFromVariable(foreignTable);
    	}
    }

	/**
	 * add a clause to check if instances of the table tableName can be found in the collection foreignTableProperty of foreignTableName
	 * @param inputTableName table to start from
	 * @param foreignTableName name of the table that has the property
	 * @param foreignTableProperty A property of the foreign table that is a collection of input tables
	 * @param description A description of the relation between the input table and the foreign table
	 * @return The clause that has been added
	 */
    private AtomicWhereClause addCollectionRelationClause(String inputTableName, String foreignTableName, String foreignTableProperty, String description) {
        DatabaseManager manager = DatabaseManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
            
            InputVariable ivar = new InputVariable(new VariableType(inputTableName));
            description = description == null ? "have an associated " + getTable(foreignTableName).getSingularName() : description;
            FromVariable newFromVar = new FromVariable(foreignTableName);
            OutputVariable ovar = new OutputVariable(new VariableType(foreignTableName), getVariableName(foreignTableName));
            ovar.setUniqueName(ovar.getFormalName());
            ovar.getExpression().addFromVariable(newFromVar);

            aVisList.addFixedString(new FixedString("The " + getTable(inputTableName).getSingularName()));
            aVisList.addInputVariable(ivar);
            aVisList.addFixedString(new FixedString(description));
            aVisList.addOutputVariable(ovar);
            
            aComposer.addInputVariable(ivar);
            aComposer.addFixedString(new FixedString(" IN ELEMENTS("));
            aComposer.addFromVariable(newFromVar);
            aComposer.addFixedString(new FixedString("." +  foreignTableProperty));
            aComposer.addFixedString(new FixedString(")"));
            
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
	
	/**
	 * add all clauses that check a given numeric property of a table
	 * @param inputTableName table to start from
	 * @param inputTableToIdTable path from the input table to the id table 
	 *                            null if they are the same table
	 * @param foreignTableName name of the table that has the property
	 * @param foreignTableToIdTable path from the foreign table to the id table
	 *                              null if they are the same table
	 * @param idTableName table that acts as key between the input table and the foreign table
	 * @param idTableKey element of idTableName that is the key
	 *                   null if the table should be used as key
	 * @param foreignTableProperty the property to check
	 * @param description
	 * @param suggestedValuesQuery the query for suggested values if you want a dropdown
	 *                             null if you don't want a dropdown
     * @param caseSensitive true for case sensitive comparison
     *                      false for case insensitive comparison
	 * @param valueType value type of the property
	 *                  most likely Numeric, but could be String if you want to force
	 *                  a string value to be interpreted as a number
	 * @param invertLink false for a relation from input and foreigntable to idtable
	 *                   true for a relation from idtable to input and foreigntable
	 * @return All the clauses that have been added
	 */
	private List<AtomicWhereClause> addNumberPropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, String description, String suggestedValuesQuery, boolean caseSensitive, String valueType, boolean invertLink) {
		List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
		boolean show = foreignTableName != inputTableName || foreignTableProperty.indexOf('.') >= 0;
    	
		Constant constant = new DoubleConstant();
    	if (suggestedValuesQuery != null) {
    		constant.setSuggestedValuesQuery(suggestedValuesQuery);
    		constant.setSuggestedValuesMandatory(true);
    	}
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive, constant, null, valueType, invertLink));
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive, constant, getNumberComparisonOperator(), valueType, invertLink));
    	return list;
	}
    
    /**
	 * add all clauses that check a given numeric property of a table
	 * @param inputTableName table to start from
	 * @param inputTableToIdTable path from the input table to the id table 
	 *                            null if they are the same table
	 * @param foreignTableName name of the table that has the property
	 * @param foreignTableToIdTable path from the foreign table to the id table
	 *                              null if they are the same table
	 * @param idTableName table that acts as key between the input table and the foreign table
	 * @param idTableKey element of idTableName that is the key
	 *                   null if the table should be used as key
	 * @param foreignTableProperty the property to check
	 * @param description
	 * @param dropdown true if the possible values should be selected from a dropdown
	 *                 the most likely suggested values will be set to
	 *                 select distinct foreignTableProperty from foreignTableName
	 * @return All the clauses that have been added
	 */
	private List<AtomicWhereClause> addNumberPropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean dropdown) {
		String suggestedValuesQuery = null;
    	if (dropdown) {
    		suggestedValuesQuery = "SELECT DISTINCT obj." + foreignTableProperty + " FROM " + foreignTableName + " obj";
    	}
		return addNumberPropertyComparisonClauses(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, description, suggestedValuesQuery, true, "Numeric", false);
	}
	    
	/**
	 * add all clauses that check a given boolean property of a table
	 * @param inputTableName table to start from
	 * @param inputTableToIdTable path from the input table to the id table 
	 *                            null if they are the same table
	 * @param foreignTableName name of the table that has the property
	 * @param foreignTableToIdTable path from the foreign table to the id table
	 *                              null if they are the same table
	 * @param idTableName table that acts as key between the input table and the foreign table
	 * @param idTableKey element of idTableName that is the key
	 *                   null if the table should be used as key
	 * @param foreignTableProperty the property to check
	 * @param description
	 * @return All the clauses that have been added
	 */
	private List<AtomicWhereClause> addBooleanPropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, String description) {
		List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	boolean show = foreignTableName != inputTableName || foreignTableProperty.indexOf('.') >= 0;
    	boolean caseSensitive = true;
    	String valueType = "Boolean";
    	Constant constant = new BooleanConstant();
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive, constant, null, valueType, false));
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive, constant, getBooleanComparisonOperator(), valueType, false));
		return list;
	}
	    
	/**
	 * add all clauses that check a given date property of a table
	 * @param inputTableName table to start from
	 * @param inputTableToIdTable path from the input table to the id table 
	 *                            null if they are the same table
	 * @param foreignTableName name of the table that has the property
	 * @param foreignTableToIdTable path from the foreign table to the id table
	 *                              null if they are the same table
	 * @param idTableName table that acts as key between the input table and the foreign table
	 * @param idTableKey element of idTableName that is the key
	 *                   null if the table should be used as key
	 * @param foreignTableProperty the property to check
	 * @param description
	 * @param dropdown true if the possible values should be selected from a dropdown
	 *                 the most likely suggested values will be set to
	 *                 select distinct foreignTableProperty from foreignTableName
	 * @return All the clauses that have been added
	 */
	private List<AtomicWhereClause> addDatePropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean dropdown) {
		List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	boolean show = foreignTableName != inputTableName || foreignTableProperty.indexOf('.') >= 0;
    	boolean caseSensitive = true;
    	String valueType = "Date";
    	
		Constant constant = new DateConstant();
    	if (dropdown) {
    		constant.setSuggestedValuesQuery("SELECT DISTINCT obj." + foreignTableProperty + " FROM " + foreignTableName + " obj");
    		constant.setSuggestedValuesMandatory(true);
    	}
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive, constant, null, valueType, false));
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive, constant, getDateComparisonOperator(), valueType, false));
    	
    	return list;
	}
	   
	
	/**
	 * add all clauses that check a given string property of a table
	 * @param inputTableName table to start from
	 * @param inputTableToIdTable path from the input table to the id table 
	 *                            null if they are the same table
	 * @param foreignTableName name of the table that has the property
	 * @param foreignTableToIdTable path from the foreign table to the id table
	 *                              null if they are the same table
	 * @param idTableName table that acts as key between the input table and the foreign table
	 * @param idTableKey element of idTableName that is the key
	 *                   null if the table should be used as key
	 * @param foreignTableProperty the property to check
	 * @param description
	 * @param suggestedValuesQuery the query for suggested values if you want a dropdown
	 *                             null if you don't want a dropdown
     * @param caseSensitive true for case sensitive comparison
     *                      false for case insensitive comparison
	 * @param valueType value type of the property
	 *                  most likely String, but could be Numeric if you want to force
	 *                  a numeric value to be interpreted as a string
	 * @param invertLink false for a relation from input and foreigntable to idtable
	 *                   true for a relation from idtable to input and foreigntable
	 * @return All the clauses that have been added
	 */
	private List<AtomicWhereClause> addStringPropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, String description, String suggestedValuesQuery, boolean caseSensitive, String valueType, boolean invertLink) {
		List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
		boolean show = foreignTableName != inputTableName || foreignTableProperty.indexOf('.') >= 0;
    	
		Constant constant = new StringConstant();
    	if (suggestedValuesQuery != null) {
    		constant.setSuggestedValuesQuery(suggestedValuesQuery);
    		constant.setSuggestedValuesMandatory(true);
    	}
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive, constant, null, valueType, invertLink));
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, description, show, caseSensitive, constant, getStringComparisonOperator(), valueType, invertLink));
    	
    	return list;
	}
	
	/**
	 * add all clauses that check a given string property of a table
	 * @param inputTableName table to start from
	 * @param inputTableToIdTable path from the input table to the id table 
	 *                            null if they are the same table
	 * @param foreignTableName name of the table that has the property
	 * @param foreignTableToIdTable path from the foreign table to the id table
	 *                              null if they are the same table
	 * @param idTableName table that acts as key between the input table and the foreign table
	 * @param idTableKey element of idTableName that is the key
	 *                   null if the table should be used as key
	 * @param foreignTableProperty the property to check
	 * @param description
	 * @param dropdown true if the possible values should be selected from a dropdown
	 *                 the most likely suggested values will be set to
	 *                 select distinct foreignTableProperty from foreignTableName
	 * @return All the clauses that have been added
	 */
	private List<AtomicWhereClause> addStringPropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean dropdown) {
		return addStringPropertyComparisonClauses(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, description, dropdown, false);
	}
	
	/**
	 * add all clauses that check a given string property of a table
	 * @param inputTableName table to start from
	 * @param inputTableToIdTable path from the input table to the id table 
	 *                            null if they are the same table
	 * @param foreignTableName name of the table that has the property
	 * @param foreignTableToIdTable path from the foreign table to the id table
	 *                              null if they are the same table
	 * @param idTableName table that acts as key between the input table and the foreign table
	 * @param idTableKey element of idTableName that is the key
	 *                   null if the table should be used as key
	 * @param foreignTableProperty the property to check
	 * @param description
	 * @param dropdown true if the possible values should be selected from a dropdown
	 *                 the most likely suggested values will be set to
	 *                 select distinct foreignTableProperty from foreignTableName
	 * @param invertLink false for a relation from input and foreigntable to idtable
	 *                   true for a relation from idtable to input and foreigntable
	 * @return All the clauses that have been added
	 */	private List<AtomicWhereClause> addStringPropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean dropdown, boolean invertLink) {
		String suggestedValuesQuery = null;
    	if (dropdown) {
    		suggestedValuesQuery = "SELECT DISTINCT obj." + foreignTableProperty + " FROM " + foreignTableName + " obj";
    	}
		return addStringPropertyComparisonClauses(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, description, suggestedValuesQuery, false, "String", invertLink);
	}
	    
	 /**
	 * add a clause to check a property of a given table 
	 * @param inputTableName table to start from
	 * @param inputTableToIdTable path from the input table to the id table 
	 *                            null if they are the same table
	 * @param foreignTableName name of the table that has the property
	 * @param foreignTableToIdTable path from the foreign table to the id table
	 *                              null if they are the same table
	 * @param idTableName table that acts as key between the input table and the foreign table
	 * @param idTableKey element of idTableName that is the key
	 *                   null if the table should be used as key
	 *                   this will be ignored if invertLink is true
	 * @param foreignTableProperty the property to check
	 *                             this must be a property of the foreign table or of the composite id
	 *                             of the foreign table. If it is a property of the composite id, include
	 *                             the id in the path, like so: id.property
	 * @param description
	 * @param show true if the property should be shown in the list of selectable columns
     * @param caseSensitive true for case sensitive comparison
     *                      false for case insensitive comparison
	 * @param propertyConstant constant to use for the property
     * @param comparisonOperator constant with the possible comparison operations
	 *        null to simply fetch the property without comparison
	 * @param comparisonDescription description of the comparison operation
	 *        null to simply fetch the property without comparison
	 * @param valueType type of the property
	 * 		  String for strings
	 *        Numeric for numbers
	 *        Boolean for booleans
	 *        Date for dates
	 * @param invertLink false for a relation from input and foreigntable to idtable
	 *                   true for a relation from idtable to input and foreigntable
	 * @return All the clauses that have been added
	 */
    private AtomicWhereClause addPropertyComparisonClause(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, String description, boolean show, boolean caseSensitive, Constant propertyConstant, Constant comparisonOperator, String valueType, boolean invertLink) {
    	// the foreign table property can be a property of the id
    	// get only the property names
    	String foreignTablePropertySimple = (foreignTableProperty.indexOf('.') >= 0 ? foreignTableProperty.substring(foreignTableProperty.lastIndexOf('.')+1) : foreignTableProperty );
    	
    	// find the data type of the property
    	// this method can resolve properties of ids
    	// returns null if the property is not found
    	String typeString = getDataTypeOfProperty(foreignTableName, foreignTableProperty);   	
    	if (typeString != null) {
            Constant constant = propertyConstant;
            
            // check if the type reported by the database is the same as specified in the catalog
            if (typeString.equals(valueType)) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
	            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
                VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
	            
                //// set all needed variables
                //
                
                // if we aren't doing a comparison we have to return a variable
                boolean fetchAsVariable = (comparisonOperator == null);
                
                // if we are returning a variable it has to be in the list of selectable outputvariables
                show = show || fetchAsVariable;
                
                // if no description is provided use the property name
	            description	= description == null ? foreignTablePropertySimple : description;
	            description = fetchAsVariable ? "has a " + description : "'s " + description;

	            // input table needed for input
	            // if both input table and foreign table are equal this variable will also be used
	            // to refer to the foreign table
	            InputVariable ivar = new InputVariable(new VariableType(inputTableName));
	            
	            // if input table and foreign table are not equal we will
	            // also need to select from the foreign table
                FromVariable newFromVar = new FromVariable(foreignTableName);
                
                
    	        //// build what gets shown in the query selector dialog
    	        //
    	        
                aVisList.addFixedString(new FixedString("The " + getTable(inputTableName).getSingularName()));
                aVisList.addInputVariable(ivar);
                aVisList.addFixedString(new FixedString(description));
                
                // add the output variable if the result should be selectable
                if (show) {
                    // build an outputvariable from the foreign table property and assign it a nice name
        	        OutputVariable ovar = new OutputVariable(new VariableType(constant.getValueTypeString()), getVariableName(foreignTableName + "." + foreignTableProperty));
        	        ovar.setUniqueName(ovar.getFormalName());
        	        // outputvariables are defined as an expression. Without this expression they are useless
        	        if (foreignTableName.equals(inputTableName)) {
        	        	ovar.getExpression().addInputVariable(ivar);
        	        }
        	        else {
    	    	        ovar.getExpression().addFromVariable(newFromVar);
        	        }
        	        ovar.getExpression().addFixedString(new FixedString("." + foreignTableProperty));
        	        aVisList.addOutputVariable(ovar);
                }
                
                // only show the input control for the constant if when needed 
                if (!fetchAsVariable) {
	                aVisList.addConstant(comparisonOperator);
	                aVisList.addConstant(constant);
                }
                
                //// build the query
                //

                // only make a link between the foreign table and the input table if they
                // are not the same so we can keep the resulting query simple
                if (!foreignTableName.equals(inputTableName)) {
                	addRelationClauseToComposer(aComposer, ivar, newFromVar, inputTableToIdTable, foreignTableToIdTable, idTableName, idTableKey, invertLink);
                	if (!fetchAsVariable) aComposer.addFixedString(new FixedString(" AND\n\t "));
                }
                if (!fetchAsVariable) {
                	// create comparison constraint
                	// [foreigntable.property] [operator] [constant]
	                if (!caseSensitive) aComposer.addFixedString(new FixedString("UPPER("));
	            	if (foreignTableName.equals(inputTableName)) {
	            		aComposer.addInputVariable(ivar);
	            	}
	            	else {
	            		aComposer.addFromVariable(newFromVar);
	            	}
	                aComposer.addFixedString(new FixedString("." + foreignTableProperty));
	                if (!caseSensitive) aComposer.addFixedString(new FixedString(")"));
	                aComposer.addFixedString(new FixedString(" "));
	                aComposer.addConstant(comparisonOperator);
	                aComposer.addFixedString(new FixedString(" "));
	                if (!caseSensitive) aComposer.addFixedString(new FixedString("UPPER ("));
	                aComposer.addConstant(constant);
	                if (!caseSensitive) aComposer.addFixedString(new FixedString(")"));
                }
                
	            addAtomicWhereClause(aClause);
	            return aClause;
            }
            else {
                System.err.println("Incompatible datatype, " + valueType + " expected: " + foreignTableName + "." + foreignTableProperty);
                return null;
            }
        } else {
            System.err.println("property not found: " + foreignTableName + "." + foreignTableProperty);
            return null;
        }
    }	    

	/**
     * add all clauses related to number variables
     * @return all the clauses that have been added
     */
    private List<AtomicWhereClause> addNumberClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(addNumericVariableDeclarationClause());
    	list.add(addNumericVariableToConstantComparisonClause());
    	list.add(addNumericVariableToVariableComparisonClause());
		list.add(addNumericVariableIntervalClause());
    	list.addAll(addNumericCalculationClauses());
    	return list;
    }
    
    /**
     * add all clauses related to string variables
     * @return all the clauses that have been added
     */
    private List<AtomicWhereClause> addStringClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(addStringVariableDeclarationClause());
    	list.add(addStringVariableToConstantComparisonClause());
    	list.add(addStringVariableToVariableComparisonClause());
    	list.add(addStringVariableIntervalClause());
    	list.addAll(addStringCalculationClauses());
    	return list;
    }
    
    /**
     * add all clauses related to boolean variables
     * @return all the clauses that have been added
     */
    private List<AtomicWhereClause> addBooleanClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(addBooleanVariableDeclarationClause());
    	list.add(addBooleanVariableToConstantComparisonClause());
    	list.add(addBooleanVariableToVariableComparisonClause());
    	list.addAll(addBooleanCalculationClauses());
    	return list;
    }

    /**
     * add all clauses related to date variables
     * @return all the clauses that have been added
     */
    private List<AtomicWhereClause> addDateClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(addDateVariableDeclarationClause());
    	list.add(addDateVariableToConstantComparisonClause());
    	list.add(addDateVariableToVariableComparisonClause());
    	list.add(addDateVariableIntervalClause());
    	list.addAll(addDateCalculationClauses());
    	return list;
    }
    
    /**
     * add all clauses related to persistent object variables of the given type
     * @param objectName name of the persistent object
     * @return all the clauses that have been added
     */
    private List<AtomicWhereClause> addObjectClauses(String objectName) {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(addObjectVariableDeclarationClause(objectName));
    	list.add(addObjectVariableToVariableComparisonClause(objectName));
    	return list;
    }
    
    /**
     * Get a constant with the possible comparison operations for persistent objects: (=, <>)
     * @return a constant with the possible comparison operations for persistent objects
     */
    private OperatorConstant getObjectComparisonOperator() {
    	OperatorConstant constant = new OperatorConstant();
    	constant.addSuggestedValue(new SuggestedValuesOption("=", "is"));
    	constant.addSuggestedValue(new SuggestedValuesOption("<>", "is not"));
    	constant.setSuggestedValuesMandatory(true);
    	return constant;
    }
    
    /**
     * Get a constant with the possible comparison operations for date values: (=, <>, <, >)
     * @return a constant with the possible comparison operations for date values
     */
    private OperatorConstant getDateComparisonOperator() {
    	OperatorConstant constant = new OperatorConstant();
       	constant.addSuggestedValue(new SuggestedValuesOption("=", "is"));
    	constant.addSuggestedValue(new SuggestedValuesOption("<>", "is not"));
       	constant.addSuggestedValue(new SuggestedValuesOption("<", "is before"));
    	constant.addSuggestedValue(new SuggestedValuesOption(">", "is after"));
    	constant.setSuggestedValuesMandatory(true);
    	return constant;
    }
    
    /**
     * Get a constant with the possible comparison operations for boolean values: (=, <>)
     * @return a constant with the possible comparison operations for boolean values
     */
    private OperatorConstant getBooleanComparisonOperator() {
    	OperatorConstant constant = new OperatorConstant();
       	constant.addSuggestedValue(new SuggestedValuesOption("=", "is"));
    	constant.addSuggestedValue(new SuggestedValuesOption("<>", "is not"));
    	constant.setSuggestedValuesMandatory(true);
    	return constant;
    }
    
    /**
     * Get a constant with the possible comparison operations for string values: (like, not like)
     * @return a constant with the possible comparison operations for string values
     */
    private OperatorConstant getStringComparisonOperator() {
    	OperatorConstant constant = new OperatorConstant();
       	constant.addSuggestedValue(new SuggestedValuesOption("LIKE", "is"));
    	constant.addSuggestedValue(new SuggestedValuesOption("NOT LIKE", "is not"));
    	constant.addSuggestedValue(new SuggestedValuesOption("<", "comes before"));
    	constant.addSuggestedValue(new SuggestedValuesOption(">", "comes after"));
    	constant.setSuggestedValuesMandatory(true);
    	return constant;
    }
    
    /**
     * Get a constant with the possible comparison operations for numeric values: (=, <>, <, >)
     * @return a constant with the possible comparison operations for numeric values
     */
    private OperatorConstant getNumberComparisonOperator() {
    	OperatorConstant constant = new OperatorConstant();
       	constant.addSuggestedValue(new SuggestedValuesOption("=", "is"));
    	constant.addSuggestedValue(new SuggestedValuesOption("<>", "is not"));
       	constant.addSuggestedValue(new SuggestedValuesOption("<", "is smaller than"));
    	constant.addSuggestedValue(new SuggestedValuesOption(">", "is greater than"));
    	constant.setSuggestedValuesMandatory(true);
    	return constant;
    }
    
    /**
     * Get a constant with the possible comparison operations for checking an interval: (between, not between)
     * @return a constant with the possible comparison operations for checking an interval
     */
    private OperatorConstant getIntervalComparisonOperator() {
    	OperatorConstant constant = new OperatorConstant();
       	constant.addSuggestedValue(new SuggestedValuesOption("BETWEEN", "is between"));
    	constant.addSuggestedValue(new SuggestedValuesOption("NOT BETWEEN", "is not between"));
    	constant.setSuggestedValuesMandatory(true);
    	return constant;
    }


    /**
     * Get a constant with the possible calculation operations for numbers: (+, -, *, /)
     * @return a constant with the possible calculation operations for numbers
     */
    private OperatorConstant getNumberCalculationOperator() {
    	OperatorConstant constant = new OperatorConstant();
       	constant.addSuggestedValue(new SuggestedValuesOption("+", "+"));
    	constant.addSuggestedValue(new SuggestedValuesOption("-", "-"));
    	constant.addSuggestedValue(new SuggestedValuesOption("*", "*"));
    	constant.addSuggestedValue(new SuggestedValuesOption("/", "/"));
    	constant.setSuggestedValuesMandatory(true);
    	return constant;
    }

    /**
     * Get a constant with the possible calculation operations for strings: (||)
     * @return a constant with the possible calculation operations for strings
     */
    private OperatorConstant getStringCalculationOperator() {
    	OperatorConstant constant = new OperatorConstant();
       	constant.addSuggestedValue(new SuggestedValuesOption("||", "+"));
    	constant.setSuggestedValuesMandatory(true);
    	return constant;
    }

    /**
     * Get a constant with the possible calculation operations for booleans: (or, and)
     * @return a constant with the possible calculation operations for booleans
     */
    private OperatorConstant getBooleanCalculationOperator() {
    	OperatorConstant constant = new OperatorConstant();
       	constant.addSuggestedValue(new SuggestedValuesOption("or", "is true or"));
    	constant.addSuggestedValue(new SuggestedValuesOption("and", "is true and"));
    	constant.addSuggestedValue(new SuggestedValuesOption("and not", "is true but not"));
    	constant.setSuggestedValuesMandatory(true);
    	return constant;
    }

    /**
     * Get a constant with the possible calculation operations for dates: (+, -)
     * @return a constant with the possible calculation operations for dates
     */
    private OperatorConstant getDateCalculationOperator() {
    	OperatorConstant constant = new OperatorConstant();
       	constant.addSuggestedValue(new SuggestedValuesOption("+", "+"));
    	constant.addSuggestedValue(new SuggestedValuesOption("-", "-"));
    	constant.setSuggestedValuesMandatory(true);
    	return constant;
    }
    
    /**
     * add a clause to check if a numeric variable is between two constants
     * @return the clause that has been added
     */
    private AtomicWhereClause addNumericVariableIntervalClause() {
    	return addVariableIntervalClause("Numeric", new DoubleConstant(), new DoubleConstant(), getIntervalComparisonOperator(), "Number", true);
    }
    
    /**
     * add a clause to check if a date variable is between two constants
     * @return the clause that has been added
     */
    private AtomicWhereClause addDateVariableIntervalClause() {
    	return addVariableIntervalClause("Date", new DateConstant("1900-01-01"), new DateConstant(), getIntervalComparisonOperator(), "Date", true);
    }
    
    /**
     * add a clause to check if a string variable is between two constants
     * @return the clause that has been added
     */
    private AtomicWhereClause addStringVariableIntervalClause() {
    	return addVariableIntervalClause("Date", new StringConstant(), new StringConstant(), getIntervalComparisonOperator(), "Date", false);
    }

    /**
     * add a clause to check if a variable of the given type is between two constants
     * @param variableType Numeric for numbers
     * 	                   Date for dates
     * @param startConstant the variable should be bigger than the value of this constant
     *                      preferably of the same type as variableType
     * @param endConstant the variable should be smaller than the value of this constant
     *                    preferably of the same type as variableType
     * @param comparisonOperator constant with the possible comparison operations
     * @param caseSensitive true for case sensitive comparison
     *                      false for case insensitive comparison
     * @return the clause that has been added
     */
    private AtomicWhereClause addVariableIntervalClause(String variableType, Constant startConstant, Constant endConstant, Constant comparisonOperator, String description, boolean caseSensitive) {
        DatabaseManager manager = DatabaseManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
            
            InputVariable ivar = new InputVariable(new VariableType(variableType));

            aVisList.addFixedString(new FixedString(description));
            aVisList.addInputVariable(ivar);
            aVisList.addConstant(comparisonOperator);
            aVisList.addConstant(startConstant);
            aVisList.addFixedString(new FixedString("and"));
            aVisList.addConstant(endConstant);
            
            if (!caseSensitive) aComposer.addFixedString(new FixedString("UPPER ("));
            aComposer.addInputVariable(ivar);
            if (!caseSensitive) aComposer.addFixedString(new FixedString(")"));
            aComposer.addFixedString(new FixedString(" "));
            aComposer.addConstant(comparisonOperator);
            aComposer.addFixedString(new FixedString(" "));
            if (!caseSensitive) aComposer.addFixedString(new FixedString("UPPER ("));
            aComposer.addConstant(startConstant);
            if (!caseSensitive) aComposer.addFixedString(new FixedString(")"));
            aComposer.addFixedString(new FixedString(" AND "));
            if (!caseSensitive) aComposer.addFixedString(new FixedString("UPPER ("));
            aComposer.addConstant(endConstant);
            if (!caseSensitive) aComposer.addFixedString(new FixedString(")"));
            
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
    
    /**
     * add a clause to turn a constant in a new numeric variable
     * @return the clause that has been added
     */
    private AtomicWhereClause addNumericVariableDeclarationClause() {
    	return addVariableDeclarationClause("Numeric", new DoubleConstant(), "Number");
    }
    
    /**
     * add a clause to turn a constant in a new string variable
     * @return the clause that has been added
     */
    private AtomicWhereClause addStringVariableDeclarationClause() {
    	return addVariableDeclarationClause("String", new StringConstant(), "String");
    }

    /**
     * add a clause to turn a constant in a new boolean variable
     * @return the clause that has been added
     */
    private AtomicWhereClause addBooleanVariableDeclarationClause() {
    	Constant constant = new BooleanConstant();
    	return addVariableDeclarationClause("Boolean", constant, "Boolean");
    }

    /**
     * add a clause to turn a constant in a new date variable 
     * @return the clause that has been added
     */
    private AtomicWhereClause addDateVariableDeclarationClause() {
    	return addVariableDeclarationClause("Date", new DateConstant(), "Date");
    }
    
    /**
     * add a clause to create a new persistent object variable
     * @param objectName name of the persistent object
     * @return the clause that has been added
     */
    private AtomicWhereClause addObjectVariableDeclarationClause(String objectName) {
    	return addVariableDeclarationClause(objectName, null, getTable(objectName).getSingularName());
    }
    
    /**
     * add a clause to create a variable of the given type from the given constant
     * @param variableType Numeric for numbers
     * 	                   Date for dates
     *                     String for strings
     *                     Boolean for booleans
     *                     Name of a persistent object for persistent objects
     * @param constant     The constant to turn into a variable
     *                     preferably of the same type as variableType
     *                     null if a persistent object
     * @return the clause that has been added
     */
    private AtomicWhereClause addVariableDeclarationClause(String variableType, Constant constant, String description) {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        
        OutputVariable ovar = new OutputVariable(new VariableType(variableType), getVariableName(variableType));
        ovar.setUniqueName(ovar.getFormalName());

        aVisList.addFixedString(new FixedString("There is a "));
        aVisList.addOutputVariable(ovar);

        if (constant != null) {
        	ovar.getExpression().addConstant(constant);
	        aVisList.addFixedString(new FixedString(" = "));
	        aVisList.addConstant(constant);
        }
        else if (tableExists(variableType)){
            FromVariable fromVar = new FromVariable(variableType);
	        ovar.getExpression().addFromVariable(fromVar);
            aClause.addFromVariable(fromVar);
        }
        
        aComposer.addFixedString(new FixedString("1=1"));
        
        addAtomicWhereClause(aClause);
        return aClause;
    }
    
    /**
     * add a clause to compare between a numeric variable and a numeric constant
     * @return the clause that has been added
     */
    private AtomicWhereClause addNumericVariableToConstantComparisonClause() {
    	return addVariableToConstantComparisonClause("Numeric", new DoubleConstant(), "Number", getNumberComparisonOperator(), true);
    }
    
    /**
     * add a clause to compare between a string variable and a string constant
     * @return the clause that has been added
     */
    private AtomicWhereClause addStringVariableToConstantComparisonClause() {
    	return addVariableToConstantComparisonClause("String", new StringConstant(), "String", getStringComparisonOperator(), false);
    }
    
    /**
     * add a clause to compare between a boolean variable and a boolean constant
     * @return the clause that has been added
     */
    private AtomicWhereClause addBooleanVariableToConstantComparisonClause() {
    	return addVariableToConstantComparisonClause("Boolean", new BooleanConstant(), "Boolean", getBooleanComparisonOperator(), true);
    }
    
    /**
     * add a clause to compare between a date variable and a date constant
     * @return the clause that has been added
     */
    private AtomicWhereClause addDateVariableToConstantComparisonClause() {
    	return addVariableToConstantComparisonClause("Date", new DateConstant(), "Date", getDateComparisonOperator(), true);
    }
    
    /**
     * add a clause to compare between a variable of the given type and a given constant
     * @param variableType Numeric for numbers
     * 	                   Date for dates
     *                     String for strings
     *                     Boolean for booleans
     * @param constant     The constant to compare to
     *                     preferably of the same type as variableType
     * @param comparisonOperator constant with the possible comparison operations
     * @param caseSensitive true for case sensitive comparison
     *                      false for case insensitive comparison
     * @return the clause that has been added
     */
    private AtomicWhereClause addVariableToConstantComparisonClause(String variableType, Constant constant, String description, OperatorConstant comparisonOperator, boolean caseSensitive) {
        DatabaseManager manager = DatabaseManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();

            InputVariable ivar1 = new InputVariable(new VariableType(variableType));

            aVisList.addFixedString(new FixedString(description));
            aVisList.addInputVariable(ivar1);
            aVisList.addConstant(comparisonOperator);
            aVisList.addConstant(constant);
            
            addComparisonClauseToComposer(aComposer, ivar1, comparisonOperator, constant, caseSensitive);
            
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
    
    /**
     * add a clause to compare between two numbers
     * @return the clause that has been added
     */
    private AtomicWhereClause addNumericVariableToVariableComparisonClause() {
    	return addVariableToVariableComparisonClause("Numeric", "Number", getNumberComparisonOperator(), true);
    }
    
    /**
     * add a clause to compare between two strings
     * @return the clause that has been added
     */
    private AtomicWhereClause addStringVariableToVariableComparisonClause() {
    	return addVariableToVariableComparisonClause("String", "String", getStringComparisonOperator(), false);
    }

    /**
     * add a clause to compare between two booleans
     * @return the clause that has been added
     */
    private AtomicWhereClause addBooleanVariableToVariableComparisonClause() {
    	return addVariableToVariableComparisonClause("Boolean", "Boolean", getBooleanComparisonOperator(), true);
    }

    /**
     * add a clause to compare between two dates
     * @return the clause that has been added
     */
    private AtomicWhereClause addDateVariableToVariableComparisonClause() {
    	return addVariableToVariableComparisonClause("Date", "Date", getDateComparisonOperator(), true);
    }

    /**
     * add a clause to compare between two instances of the given persistent object
     * @param objectName name of the persistent object
     * @return the clause that has been added
     */
    private AtomicWhereClause addObjectVariableToVariableComparisonClause(String objectName) {
    	return addVariableToVariableComparisonClause(objectName, getTable(objectName).getSingularName(), getObjectComparisonOperator(), true);
    }
    
    /**
     * add a clause to compare between two variables of the given type
     * @param variableType Numeric for numbers
     * 	                   Date for dates
     *                     String for strings
     *                     Boolean for booleans
     *                     Name of a persistent object for a persistent object
     * @param comparisonOperator constant with the possible comparison operations
     * @param caseSensitive true for case sensitive comparison
     *                      false for case insensitive comparison
     * @return the clause that has been added
     */
    private AtomicWhereClause addVariableToVariableComparisonClause(String variableType, String description, OperatorConstant comparisonOperator, boolean caseSensitive) {
        DatabaseManager manager = DatabaseManager.getInstance();
        if (manager != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();

            InputVariable ivar1 = new InputVariable(new VariableType(variableType));
            InputVariable ivar2 = new InputVariable(new VariableType(variableType));

            aVisList.addFixedString(new FixedString(description));
            aVisList.addInputVariable(ivar1);
            aVisList.addConstant(comparisonOperator);
            aVisList.addInputVariable(ivar2);
            
            addComparisonClauseToComposer(aComposer, ivar1, comparisonOperator, ivar2, caseSensitive);
            
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
    
    /**
     * adds a to clause to the given where clause composer to link the given input table to the given foreigntable
     * @param aComposer where clause composer to add the comparison to
     * @param word1 left part of the comparison
     * @param comparisonOperator comparison operator
     * @param word2 right part of the comparison
     * @param caseSensitive true for case sensitive comparison
     *                      false for case insensitive comparison
     */
    private void addComparisonClauseToComposer(WhereClauseComposer aComposer, AWCWord word1, Constant comparisonOperator, AWCWord word2, boolean caseSensitive) {
        if (!caseSensitive) aComposer.addFixedString(new FixedString("UPPER ("));
        aComposer.addWord(word1);
        if (!caseSensitive) aComposer.addFixedString(new FixedString(")"));
        aComposer.addFixedString(new FixedString(" "));
        aComposer.addConstant(comparisonOperator);
        aComposer.addFixedString(new FixedString(" "));
        if (!caseSensitive) aComposer.addFixedString(new FixedString("UPPER ("));
        aComposer.addWord(word2);
        if (!caseSensitive) aComposer.addFixedString(new FixedString(")"));
    }
    
    /**
     * add all clause to create a new number variable from a calculation between a two numbers
     * @return all the clauses that have been added
     */
    private List<AtomicWhereClause> addNumericCalculationClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(addVariableCalculationClause("Numeric", "number", new DoubleConstant(), null, getNumberCalculationOperator(), ""));
    	list.add(addVariableCalculationClause("Numeric", "number", null, "Numeric", getNumberCalculationOperator(), ""));
    	return list;
    }
    
    /**
     * add all clause to create a new boolean variable from a calculation between a two booleans
     * @return all the clauses that have been added
     */
    private List<AtomicWhereClause> addBooleanCalculationClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(addVariableCalculationClause("Boolean", "boolean", null, "Boolean",  getBooleanCalculationOperator(), "is true"));
    	return list;
    }
    
    /**
     * add all clause to create a new strng variable from a calculation between a two string
     * @return all the clauses that have been added
     */
    private List<AtomicWhereClause> addStringCalculationClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(addVariableCalculationClause("String", "string", new StringConstant(), null, getStringCalculationOperator(), ""));
    	list.add(addVariableCalculationClause("String", "string", null, "String", getStringCalculationOperator(), ""));
    	return list;
    }

    /**
     * add all clause to create a new date variable from a calculation between a string and a number of days
     * @return all the clauses that have been added
     */
    private List<AtomicWhereClause> addDateCalculationClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(addVariableCalculationClause("Date", "date", new DoubleConstant(), null, getDateCalculationOperator(), "days"));
    	list.add(addVariableCalculationClause("Date", "date", null, "Numeric", getDateCalculationOperator(), "days"));
    	return list;
    }
    
    /**
     * add a clause to create a new variable of the given type from a calculation between a variable of the given type
     * and either a given constant or another variable
     * @param variableType Numeric for numbers
     * 	                   Date for dates
     *                     String for strings
     *                     Boolean for booleans
     * @param comparisonOperator constant with the possible calculation operations
     * @param calculationConstant The constant to do the calculation with
     *                           null if you do not want to do a calculation with a constant
     * @param calculationVariableType The type of variable you want to do the calculation with
     *                               null if you do not want to do a calculation with a variable
     * @param calculationOperator constant with the possible comparison operations
     * @param description text at the start of the english representation of the clause
     * @param additionalDescription text at the end of the english representation of the clause
     * @return the clause that has been added
     */    private AtomicWhereClause addVariableCalculationClause(String variableType, String description, Constant calculationConstant, String calculationVariableType, OperatorConstant calculationOperator, String additionalDescription) {
        DatabaseManager manager = DatabaseManager.getInstance();
        if (manager != null && (calculationConstant != null || calculationVariableType != null)) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();

            InputVariable ivar1 = new InputVariable(new VariableType(variableType));
            InputVariable ivar2 = null;
            
            OutputVariable ovar = new OutputVariable(new VariableType(variableType), getVariableName(variableType));
            ovar.setUniqueName(ovar.getFormalName());
            ovar.getExpression().addInputVariable(ivar1);
            ovar.getExpression().addFixedString(new FixedString(" "));
            ovar.getExpression().addConstant(calculationOperator);
            ovar.getExpression().addFixedString(new FixedString(" "));
            if (calculationConstant != null) {
	            ovar.getExpression().addConstant(calculationConstant);
            }
            else if (calculationVariableType != null) {
            	ivar2 =  new InputVariable(new VariableType(calculationVariableType));
	            ovar.getExpression().addInputVariable(ivar2);
            }
            
            aVisList.addFixedString(new FixedString(description));
            aVisList.addOutputVariable(ovar);
            aVisList.addFixedString(new FixedString(" = "));
            aVisList.addInputVariable(ivar1);
            aVisList.addConstant(calculationOperator);
            if (calculationConstant != null) {
            	aVisList.addConstant(calculationConstant);
            }
            else if (calculationVariableType != null) {
                aVisList.addInputVariable(ivar2);
            }
            aVisList.addFixedString(new FixedString(additionalDescription));
            
            aComposer.addFixedString(new FixedString("1=1"));
            
            addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
	    
    /**
     * set all variable names, table descriptions and table aliases
     */
    public void addVariableNames() {
    	// primitive types
        addVariableName("Numeric", "number");
        addVariableName("String", "string");
        addVariableName("Boolean", "boolean");
        addVariableName("Date", "date");
    	
        // patients 
        addTableDescription("net.sf.regadb.db.PatientImpl", "patient");
        addTableAlias("net.sf.regadb.db.PatientImpl", "patient");
        addVariableName("net.sf.regadb.db.PatientImpl", "patient");
        addVariableName("net.sf.regadb.db.PatientImpl.patientId", "PatientId");
        addVariableName("net.sf.regadb.db.PatientImpl.birthDate", "BirthDate");
        addVariableName("net.sf.regadb.db.PatientImpl.deathDate", "DeathDate");
        addVariableName("net.sf.regadb.db.PatientImpl.lastName", "LastName");
        addVariableName("net.sf.regadb.db.PatientImpl.firstName", "FirstName");
        addVariableName("net.sf.regadb.db.AttributeNominalValue.value", "Attribute");
        addVariableName("net.sf.regadb.db.PatientAttributeValue.value", "Attribute");
        
        // therapy
        addTableDescription("net.sf.regadb.db.Therapy", "therapy");
        addTableAlias("net.sf.regadb.db.Therapy", "therapy");
        addVariableName("net.sf.regadb.db.Therapy", "therapy");
        addVariableName("net.sf.regadb.db.Therapy.startDate", "StartDate");
        addVariableName("net.sf.regadb.db.Therapy.stopDate", "StopDate");
        addVariableName("net.sf.regadb.db.Therapy.comment", "Comment");

        // viral isolate
        addTableDescription("net.sf.regadb.db.ViralIsolate", "viral isolate");
        addTableAlias("net.sf.regadb.db.ViralIsolate", "vi");
        addVariableName("net.sf.regadb.db.ViralIsolate", "viralIsolate");
        addVariableName("net.sf.regadb.db.ViralIsolate.sampleDate", "SampleDate");
        addVariableName("net.sf.regadb.db.ViralIsolate.sampleId", "SampleId");
        
        // nt sequence
        addTableDescription("net.sf.regadb.db.NtSequence", "nucleotide sequence");
        addTableAlias("net.sf.regadb.db.NtSequence", "ntSeq");
        addVariableName("net.sf.regadb.db.NtSequence", "ntSequence");
        addVariableName("net.sf.regadb.db.NtSequence.sequenceDate", "SequenceDate");
        addVariableName("net.sf.regadb.db.NtSequence.label", "Value");
        addVariableName("net.sf.regadb.db.NtSequence.nucleotides", "Nucleotides");
        
        // protein
        addVariableName("net.sf.regadb.db.Protein", "protein");
        addVariableName("net.sf.regadb.db.Protein.abbreviation", "ProteinAbbreviation");
        addVariableName("net.sf.regadb.db.Protein.fullName", "ProteinName");

        // aa sequence
        addTableDescription("net.sf.regadb.db.AaSequence", "amino acid sequence");
        addTableAlias("net.sf.regadb.db.AaSequence", "aaSeq");
        addVariableName("net.sf.regadb.db.AaSequence", "aaSequence");
        addVariableName("net.sf.regadb.db.AaSequence.firstAaPos", "AaPosition");
        addVariableName("net.sf.regadb.db.AaSequence.lastAaPos", "AaPosition");

        // aa mutation
        addTableDescription("net.sf.regadb.db.AaMutation", "amino acid mutation");
        addTableAlias("net.sf.regadb.db.AaMutation", "aaMut");
        addVariableName("net.sf.regadb.db.AaMutation", "aaMutation");
        addVariableName("net.sf.regadb.db.AaMutation.aaReference", "AaStr");
        addVariableName("net.sf.regadb.db.AaMutation.aaMutation", "AaStr");
        addVariableName("net.sf.regadb.db.AaMutation.ntReferenceCodon", "NtStr");
        addVariableName("net.sf.regadb.db.AaMutation.ntMutationCodon", "NtStr");
        addVariableName("net.sf.regadb.db.AaMutation.id.mutationPosition", "MutationPosition");
        
        // aa insertion
        addTableDescription("net.sf.regadb.db.AaInsertion", "amino acid insertion");
        addTableAlias("net.sf.regadb.db.AaInsertion", "aaIns");
        addVariableName("net.sf.regadb.db.AaInsertion", "aaInsertion");
        addVariableName("net.sf.regadb.db.AaInsertion.aaInsertion", "AaStr");
        addVariableName("net.sf.regadb.db.AaInsertion.ntInsertionCodon", "NtStr");
        addVariableName("net.sf.regadb.db.AaInsertion.id.insertionPosition", "InsertionPosition");
        addVariableName("net.sf.regadb.db.AaInsertion.id.insertionOrder", "InsertionOrder");
        
        // drug class
        addTableDescription("net.sf.regadb.db.DrugClass", "drug class");
        addTableAlias("net.sf.regadb.db.DrugClass", "drugClass");
        addVariableName("net.sf.regadb.db.DrugClass", "drugClass");
        addVariableName("net.sf.regadb.db.DrugClass.classId", "DrugClassId");
        addVariableName("net.sf.regadb.db.DrugClass.className", "DrugClassName");
        addVariableName("net.sf.regadb.db.DrugClass.resistanceTableOrder", "ResitanceTableOrder");

        // therapy commercial
        addTableDescription("net.sf.regadb.db.TherapyCommercial", "treatment with a commercial drug");
        addTableAlias("net.sf.regadb.db.TherapyCommercial", "cTherapy");
        addVariableName("net.sf.regadb.db.TherapyCommercial", "commercialTherapy");
        addVariableName("net.sf.regadb.db.TherapyCommercial.dayDosageUnits", "DailyDosage");
        addVariableName("net.sf.regadb.db.TherapyCommercial.frequency", "Frequency");
        addVariableName("net.sf.regadb.db.TherapyCommercial.placebo", "Placebo");
        addVariableName("net.sf.regadb.db.TherapyCommercial.blind", "Blind");
        
        // therapy generic
        addTableDescription("net.sf.regadb.db.TherapyGeneric", "treatment with a generic drug");
        addTableAlias("net.sf.regadb.db.TherapyGeneric", "gTherapy");
        addVariableName("net.sf.regadb.db.TherapyGeneric", "genericTherapy");
        addVariableName("net.sf.regadb.db.TherapyGeneric.dayDosageMg", "DailyDosage");
        addVariableName("net.sf.regadb.db.TherapyGeneric.frequency", "Frequency");
        addVariableName("net.sf.regadb.db.TherapyGeneric.placebo", "Placebo");
        addVariableName("net.sf.regadb.db.TherapyGeneric.blind", "Blind");
        
        // events
        addTableDescription("net.sf.regadb.db.PatientEventValue", "event");
        addTableAlias("net.sf.regadb.db.PatientEventValue", "event");
        addVariableName("net.sf.regadb.db.PatientEventValue", "event");
        addVariableName("net.sf.regadb.db.PatientEventValue.startDate", "StartDate");
        addVariableName("net.sf.regadb.db.PatientEventValue.endDate", "EndDate");
        addVariableName("net.sf.regadb.db.PatientEventValue.value", "Event");
        addVariableName("net.sf.regadb.db.EventNominalValue.value", "Event");
        
        // drug generic
        addTableDescription("net.sf.regadb.db.DrugGeneric", "generic drug");
        addTableAlias("net.sf.regadb.db.DrugGeneric", "gDrug");
        addVariableName("net.sf.regadb.db.DrugGeneric", "genericDrug");
        
        // drug commercial
        addTableDescription("net.sf.regadb.db.DrugCommercial", "commercial drug");
        addTableAlias("net.sf.regadb.db.DrugCommercial", "cDrug");
        addVariableName("net.sf.regadb.db.DrugCommercial", "commercialDrug");
        
        // dataset
        addTableDescription("net.sf.regadb.db.Dataset", "dataset");
        addTableAlias("net.sf.regadb.db.Dataset", "dataset");
        addVariableName("net.sf.regadb.db.Dataset", "dataset");
        addVariableName("net.sf.regadb.db.Dataset.description", "DatasetName");
        
        // test result
        addTableDescription("net.sf.regadb.db.TestResult", "test result");
        addTableAlias("net.sf.regadb.db.TestResult", "result");
        addVariableName("net.sf.regadb.db.TestResult", "testResult");
        addVariableName("net.sf.regadb.db.TestResult.sampleId", "SampleId");
        addVariableName("net.sf.regadb.db.TestResult.testDate", "testDate");
        addVariableName("net.sf.regadb.db.TestNominalValue.value", "TestResult");
        addVariableName("net.sf.regadb.db.TestResult.value", "TestResult");
        
        // test
        addTableDescription("net.sf.regadb.db.Test", "test");
        addTableAlias("net.sf.regadb.db.Test", "test");
        addVariableName("net.sf.regadb.db.Test", "test");
        addVariableName("net.sf.regadb.db.Test.Description", "Name");
        addVariableName("net.sf.regadb.db.TestType.description", "TestType");
        addVariableName("net.sf.regadb.db.TestObject.description", "testObject");

        // therapy motivation
        addTableAlias("net.sf.regadb.db.TherapyMotivation", "motivation");
        addVariableName("net.sf.regadb.db.TherapyMotivation.value", "Motivation");
    }
    
    public void addAllTableClauses() {
        ///////////////////////////////////////
        // events
        addObjectClauses("net.sf.regadb.db.PatientEventValue");
        addDatePropertyComparisonClauses("net.sf.regadb.db.PatientEventValue", null, "net.sf.regadb.db.PatientEventValue", null, "net.sf.regadb.db.PatientEventValue", null, "startDate", "start date", false);
        addDatePropertyComparisonClauses("net.sf.regadb.db.PatientEventValue", null, "net.sf.regadb.db.PatientEventValue", null, "net.sf.regadb.db.PatientEventValue", null, "endDate", "stop date", false);
        
        try {
        	QueryResult result = DatabaseManager.getInstance().getDatabaseConnector().executeQuery("from net.sf.regadb.db.Event");
        	for (int i = 0 ; i < result.size() ; i++) {
        		Event event = (Event) result.get(i, 0);
        		addCustomPropertyComparisonClauses(event.getName(), event.getValueType().getDescription(), "net.sf.regadb.db.Event", "net.sf.regadb.db.EventNominalValue", "net.sf.regadb.db.PatientEventValue", "event", "eventNominalValue", "event", "net.sf.regadb.db.PatientEventValue", null, "name");
        	}
        }
        catch(SQLException e) {}
        
        // link patients - event
        addRelationClauses("net.sf.regadb.db.PatientEventValue", "patient", "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "comes from patient",  "has an event", false);
        
        
        ///////////////////////////////////////
        // patients
        addObjectClauses("net.sf.regadb.db.PatientImpl");
        addStringPropertyComparisonClauses("net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null,  "net.sf.regadb.db.PatientImpl", null, "patientId", "id", false);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "lastName", "last name", false);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "firstName", "first name", false);
   		addDatePropertyComparisonClauses("net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "birthDate", "birth date", false);
        addDatePropertyComparisonClauses("net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "deathDate", "death date", false);

        // data set
        addStringPropertyComparisonClauses("net.sf.regadb.db.PatientImpl", "id.patient", "net.sf.regadb.db.Dataset", "id.dataset",  "net.sf.regadb.db.PatientDataset", null, "description", "dataset", true, true);
        
        
        // patient custom attributes
        try {
        	QueryResult result = DatabaseManager.getInstance().getDatabaseConnector().executeQuery("from net.sf.regadb.db.Attribute");
        	for (int i = 0 ; i < result.size() ; i++) {
        		Attribute attribute = (Attribute) result.get(i, 0);
        		addCustomPropertyComparisonClauses(attribute.getName(), attribute.getValueType().getDescription(), "net.sf.regadb.db.Attribute", "net.sf.regadb.db.AttributeNominalValue", "net.sf.regadb.db.PatientAttributeValue", "attribute", "attributeNominalValue", "attribute", "net.sf.regadb.db.PatientImpl", "patient", "name");
        	}
        }
        catch(SQLException e) {}

        // link patients - therapy
        addRelationClauses("net.sf.regadb.db.Therapy", "patient", "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "was performed on patient",  "has received therapy", false);
        
        // link patient - viral isolate
        addRelationClauses("net.sf.regadb.db.ViralIsolate", "patient", "net.sf.regadb.db.PatientImpl",  null, "net.sf.regadb.db.PatientImpl", null, "comes from patient",  "has a viral isolate", false);
        
        

        ///////////////////////////////////////
        // therapies
        addObjectClauses("net.sf.regadb.db.Therapy");
        addDatePropertyComparisonClauses("net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "startDate", "start date", false);
        addDatePropertyComparisonClauses("net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "stopDate", "stop date", false);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "comment", "has a comment", false);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.Therapy", "therapyMotivation", "net.sf.regadb.db.TherapyMotivation", null, "net.sf.regadb.db.TherapyMotivation", null, "value", "motivation", true);
   		
        // link therapy - therapyCommercial
        addRelationClauses("net.sf.regadb.db.TherapyCommercial", "id.therapy", "net.sf.regadb.db.Therapy", null , "net.sf.regadb.db.Therapy", null, "is part of the therapy",  "has a commercial drug treatment", false);
        addRelationClauses("net.sf.regadb.db.DrugCommercial", "id.drugCommercial", "net.sf.regadb.db.Therapy", "id.therapy" , "net.sf.regadb.db.TherapyCommercial", null, "is used in the therapy",  "consisted of a treatment with the commercial drug", true);

        // link therapy - therapyGeneric
        addRelationClauses("net.sf.regadb.db.TherapyGeneric", "id.therapy", "net.sf.regadb.db.Therapy", null , "net.sf.regadb.db.Therapy", null, "is part of the therapy",  "has a generic drug treatment", false);
        addRelationClauses("net.sf.regadb.db.DrugGeneric", "id.drugGeneric", "net.sf.regadb.db.Therapy", "id.therapy" , "net.sf.regadb.db.TherapyGeneric", null, "is used in the therapy",  "consisted of a treatment with the generic drug", true);
        


        ///////////////////////////////////////
        // therapyCommercial
        addNumberPropertyComparisonClauses("net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null, "dayDosageUnits", "daily dosage", true);
        addNumberPropertyComparisonClauses("net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null, "frequency", "frequency", false);
   		addBooleanPropertyComparisonClauses("net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null,  "net.sf.regadb.db.TherapyCommercial", null, "placebo",  "placebo");
   		addBooleanPropertyComparisonClauses("net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null,  "net.sf.regadb.db.TherapyCommercial", null, "blind",  "blind");
        
        // link therapyCommercial - DrugCommercial
        addRelationClauses("net.sf.regadb.db.DrugCommercial", null, "net.sf.regadb.db.TherapyCommercial", "id.drugCommercial", "net.sf.regadb.db.DrugCommercial", null, "is used in the treatment",  "consist of the commercial drug", false);
        
        
        
        ///////////////////////////////////////
        // therapyGeneric
        addNumberPropertyComparisonClauses("net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null, "dayDosageMg", "daily dosage in mg", true);
        addNumberPropertyComparisonClauses("net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null, "frequency", "frequency", false);
   		addBooleanPropertyComparisonClauses("net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null,  "net.sf.regadb.db.TherapyGeneric", null, "placebo",  "placebo");
   		addBooleanPropertyComparisonClauses("net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null,  "net.sf.regadb.db.TherapyGeneric", null, "blind",  "blind");
        
        
        // link therapyGeneric - DrugCommercial
        addRelationClauses("net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.TherapyGeneric", "id.drugGeneric", "net.sf.regadb.db.DrugGeneric", null, "is used in the treatment",  "consist of the generic drug", false);
        
        
        
        ///////////////////////////////////////
        // viral isolates
        addObjectClauses("net.sf.regadb.db.ViralIsolate");
        addStringPropertyComparisonClauses("net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "sampleId", "has Id", false);
        addDatePropertyComparisonClauses("net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "sampleDate", "sample date", false);
        
        // link viral isolate  - nt sequence
        addRelationClauses("net.sf.regadb.db.NtSequence", "viralIsolate", "net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "comes from the viral isolate",  "has a nucleotide sequence", false);
 
        
        ///////////////////////////////////////
        // nucleotide sequence
        addObjectClauses("net.sf.regadb.db.NtSequence");
        addDatePropertyComparisonClauses("net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "sequenceDate", "sequenced date", false);
        addStringPropertyComparisonClauses("net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "label", "label", false);
        addStringPropertyComparisonClauses("net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "nucleotides", "nucleotides", false);
        
        // link nt sequence - aa sequence
        addRelationClauses("net.sf.regadb.db.AaSequence", "ntSequence", "net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "comes from the nucleotide sequence",  "has a amino acid sequence", false);

        

        ///////////////////////////////////////
        // amino acid sequence
        addObjectClauses("net.sf.regadb.db.AaSequence");
        addNumberPropertyComparisonClauses("net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "firstAaPos", "first amino acid position", false);
        addNumberPropertyComparisonClauses("net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "lastAaPos", "last amino acid position", false);
        
        // link aa sequence - aa mutation
        addRelationClauses("net.sf.regadb.db.AaMutation", "id.aaSequence", "net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "comes from the amino acid sequence",  "has an amino acid mutation", false);
        
        // link aa sequence - aa insertion
        addRelationClauses("net.sf.regadb.db.AaInsertion", "id.aaSequence", "net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "comes from the amino acid sequence",  "has an amino acid insertion", false);
        
        // link aa sequence - protein
        addRelationClauses("net.sf.regadb.db.Protein", null, "net.sf.regadb.db.AaSequence", "protein", "net.sf.regadb.db.Protein", null, "is present in the amino acid sequence",  "has a protein", false);

        
        
        ///////////////////////////////////////
        // protein
        addStringPropertyComparisonClauses("net.sf.regadb.db.Protein", null, "net.sf.regadb.db.Protein", null, "net.sf.regadb.db.Protein", null,"abbreviation", "abbreviation", true);
        addStringPropertyComparisonClauses("net.sf.regadb.db.Protein", null, "net.sf.regadb.db.Protein", null, "net.sf.regadb.db.Protein", null,"fullName", "name", true);

        
        
        ///////////////////////////////////////
        // amino acid mutation
        addObjectClauses("net.sf.regadb.db.AaMutation");
        addStringPropertyComparisonClauses("net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation",  null , "aaReference", "amino acid reference", false);
        addStringPropertyComparisonClauses("net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation",  null , "aaMutation", "amino acid mutation", false);
        addStringPropertyComparisonClauses("net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation",  null , "ntReferenceCodon", "nucleotide reference codon", false);
        addStringPropertyComparisonClauses("net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation",  null , "ntMutationCodon", "nucleotide mutation codon", false);
        addNumberPropertyComparisonClauses("net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation",  null , "id.mutationPosition", "position", false);
        
        

        ///////////////////////////////////////
        // amino acid insertion
        addObjectClauses("net.sf.regadb.db.AaInsertion");
        addStringPropertyComparisonClauses("net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", "aaInsertionId", "aaInsertion", "amino acid insertion", false);
        addStringPropertyComparisonClauses("net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", "aaInsertionId", "ntInsertionCodon", "nucleotide insertion codon", false);
        addNumberPropertyComparisonClauses("net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", "id" ,"id.insertionPosition", "position", false);
        addNumberPropertyComparisonClauses("net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", "id" ,"id.insertionOrder", "insertion order", false);
        

        
        ///////////////////////////////////////
        // generic drugs
        addObjectClauses("net.sf.regadb.db.DrugGeneric");
   		addStringPropertyComparisonClauses("net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "genericId", "id", false);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "genericName", "name", true);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "atcCode", "atc code", true);
   		addNumberPropertyComparisonClauses("net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "resistanceTableOrder", "resistance table order", true);

        // link generic drug - drug class
        addRelationClauses("net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugGeneric", "drugClass", "net.sf.regadb.db.DrugClass", null, "has a drug",  "belongs to the drug class", false);
   		
        
        
        ///////////////////////////////////////
        // commercial drug
        addObjectClauses("net.sf.regadb.db.DrugCommercial");
   		addStringPropertyComparisonClauses("net.sf.regadb.db.DrugCommercial", null, "net.sf.regadb.db.DrugCommercial", null, "net.sf.regadb.db.DrugCommercial", null, "name", "name", true);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.DrugCommercial", null, "net.sf.regadb.db.DrugCommercial", null, "net.sf.regadb.db.DrugCommercial", null, "atcCode", "atc code", true);
        
        // link commercial - generic
   		addCollectionRelationClause("net.sf.regadb.db.DrugGeneric", "net.sf.regadb.db.DrugCommercial", "drugGenerics", "has a commercial component");
   		addCollectionRelationClause("net.sf.regadb.db.DrugCommercial", "net.sf.regadb.db.DrugGeneric", "drugCommercials", "has a use in the generic drug");
   		
   		
        
   		///////////////////////////////////////
        // drug class
        addObjectClauses("net.sf.regadb.db.DrugClass");
   		addStringPropertyComparisonClauses("net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "className", "name", true);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "classId", "id", false);
   		addNumberPropertyComparisonClauses("net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "resistanceTableOrder", "resistance table order", false);
   		
   		
   		
        ///////////////////////////////////////
        // test result
        addObjectClauses("net.sf.regadb.db.TestResult");
   		addStringPropertyComparisonClauses("net.sf.regadb.db.TestResult", null, "net.sf.regadb.db.TestResult", null, "net.sf.regadb.db.TestResult", null, "sampleId", "sample id", false);
   		addDatePropertyComparisonClauses("net.sf.regadb.db.TestResult", null, "net.sf.regadb.db.TestResult", null, "net.sf.regadb.db.TestResult", null, "testDate", "test date", false);

        // link test result -  patients
        addRelationClauses("net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.TestResult", "patient", "net.sf.regadb.db.PatientImpl", null, "has a test result",  "comes from a test on patient", false);
   		
        // link test result -  generic drug
        addRelationClauses("net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.TestResult", "drugGeneric", "net.sf.regadb.db.DrugGeneric", null, "has a test result",  "comes from a test on generic drug", false);

        // link test result -  viral isolate
        addRelationClauses("net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.TestResult", "viralIsolate", "net.sf.regadb.db.ViralIsolate", null, "has a test result",  "comes from a test on viral isolate", false);

        // link test result -  nucleotide sequence
        addRelationClauses("net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.TestResult", "ntSequence", "net.sf.regadb.db.NtSequence", null, "has a test result",  "comes from a test on nucleotide sequence", false);
        
        // test result value
        try {
        	QueryResult result = DatabaseManager.getInstance().getDatabaseConnector().executeQuery("from net.sf.regadb.db.TestType");
        	for (int i = 0 ; i < result.size() ; i++) {
        		TestType type = (TestType) result.get(i, 0);
        		addCustomPropertyComparisonClauses(type.getDescription(), type.getValueType().getDescription(), "net.sf.regadb.db.TestType", "net.sf.regadb.db.TestNominalValue", "net.sf.regadb.db.TestResult", "test.testType", "testNominalValue", "testType", "net.sf.regadb.db.TestResult", null, "description");
        	}
        }
        catch(SQLException e) {}
        
        
        ///////////////////////////////////////
        // test
        addObjectClauses("net.sf.regadb.db.Test");
   		addStringPropertyComparisonClauses("net.sf.regadb.db.Test", null, "net.sf.regadb.db.Test", null, "net.sf.regadb.db.Test", null, "description", "name", true);
        
        // link test - test type
   		addStringPropertyComparisonClauses("net.sf.regadb.db.Test", "testType", "net.sf.regadb.db.TestType", null, "net.sf.regadb.db.TestType", null, "description", "test type", true);

   		// link test - test result
        addRelationClauses("net.sf.regadb.db.Test", null, "net.sf.regadb.db.TestResult", "test", "net.sf.regadb.db.Test", null, "has a test result",  "comes from the test", false);
   		
        // link test - test object
   		addStringPropertyComparisonClauses("net.sf.regadb.db.Test", "testType.testObject", "net.sf.regadb.db.TestObject", null, "net.sf.regadb.db.TestObject", null, "description", "test object", true);
    }
}