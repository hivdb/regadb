
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
package com.pharmadm.custom.rega.queryeditor.catalog;

import java.util.*;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.Field;
import com.pharmadm.custom.rega.queryeditor.InputVariable;
import com.pharmadm.custom.rega.queryeditor.NullComposition;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.Table;
import com.pharmadm.custom.rega.queryeditor.VariableType;
import com.pharmadm.custom.rega.queryeditor.VariableType.ValueType;
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
    
    private DatabaseManager manager;
    private Map<String, DbObject> objectNames = new HashMap<String, DbObject>();
//    private Map<String, String> objectNameToVariableName = new HashMap<String, String>();
//    private Map<String, String> tableNameToAlias = new HashMap<String, String>();
//    private Map<String, String> objectNameToDescription = new HashMap<String, String>();
    private List<AtomicWhereClause> atomicWhereClauses = new ArrayList<AtomicWhereClause>();
    
    public AWCPrototypeCatalog(DatabaseManager manager) {
    	this.manager = manager;
    }
    
    /**
     * adds the given atomic where clause to the catalog
     * @param atomicWhereClause
     */
    public void addAtomicWhereClause(AtomicWhereClause atomicWhereClause) {
        this.atomicWhereClauses.add(atomicWhereClause);
    }

    public void addObject(DbObject object) {
    	objectNames.put(object.getObjectName(), object);
    }
    
    @Deprecated
    public void addVariableName(String objectName, String variableName) {
    	DbObject object = new DbObject(objectName, variableName, null, null);
    	addObject(object);
    }
    
    public DbObject getObject(String objectName) {
    	return objectNames.get(objectName);
    }
    
    /**
     * gets the plain english description for the persistent object with the given name
     * if no plain english description has been set the simple class name of the object
     * will be used
     * @param objectName name of the persistent object
     * @return a description for the given persistent object
     */
    public String getObjectDescription(String objectName) {
    	return getObject(objectName).getDescription();
    }
    
    /**
     * adds a variable name, plain emglish description and hql alias for the given object
     * @param objectName either the class name of a persistent object
     *                   or the property of a persistent object
     * @param variableName name for the variable
     * @param alias alias for the given persistent object
     *              null if this property does nut need an alias
     * @param description description for the persistent object
     */
    public void addNames(String objectName, String variableName, String alias, String description) {
    	DbObject object = new DbObject(objectName, variableName, alias, description);
    	addObject(object);
    }

    
    /**
     * gets the variable name to the given object 
     * if no variable name is specified, the first letter of the object or property name is used
     * @param objectName either the class name of a persistent object
     *                   or the property of a persistent object
     */
    public String getVariableName(String objectName) {
    	return getObject(objectName).getVariableName();
    }
	    
    /**
     * gets the hql alias for the persistent object with the given name
     * if no hql alias has been set the simple class name of the object
     * will be used
     * @param objectName name of the persistent object
     * @return an alias for the given persistent object
     */
    public String getTableAlias(String objectName) {
    	return getObject(objectName).getSqlAlias();
    }
    
    /**
     * returns the table with the given name
     * @param tableName
     * @return
     */
    public Table getTable(String tableName) {
    	return manager.getTableCatalog().getTable(tableName);
    }
    
    /**
     * returns the field with the given name from the given table
     * @param tableName
     * @param fieldName
     * @return
     */
    public Field getField(String tableName, String fieldName) {
    	Table table = getTable(tableName);
    	if (table != null) {
    		return table.getField(fieldName);
    	}
    	return null;
    }
	    
	/**
	 * returns true if the given sql data type number belongs to a string
	 * @param dataType an sql data type number
	 * @return true when the data type is a string
	 */
    public static boolean isStringType(int dataType) {
    	return dataType == 12;
    }

	/**
	 * returns true if the given sql data type number belongs to a boolean
	 * @param dataType an sql data type number
	 * @return true when the data type is a boolean
	 */
    public static boolean isBooleanType(int dataType) {
    	return dataType == -7;
    }
    
	/**
	 * returns true if the given sql data type number belongs to a date
	 * @param dataType an sql data type number
	 * @return true when the data type is a date
	 */
    public static boolean isDateType(int dataType) {
    	return (dataType >= 91) && (dataType <= 93);
    }
    
	/**
	 * returns true if the given sql data type number belongs to a numeric value
	 * @param dataType an sql data type number
	 * @return true when the data type is a number
	 */
    public static boolean isNumericType(int dataType) {
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
    public ValueType getDataTypeOfProperty(String tableName, String propertyName) {
    	ValueType valueType = null;
    	
    	int dataType = manager.getDatabaseConnector().getColumnType(tableName, propertyName);
	    	
        if (isStringType(dataType)) {
        	valueType = ValueType.String;
        }
        else if (isDateType(dataType)) {
        	valueType = ValueType.Date;
        }
        else if (isNumericType(dataType)) {
        	valueType = ValueType.Numeric;
        }
        else if (isBooleanType(dataType)) {
        	valueType = ValueType.Boolean;
        }
        else {
            System.err.println("Unknown data type found for " + tableName + "." + propertyName + ": " + dataType);
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
    public Collection<AtomicWhereClause> getAWCPrototypes(Collection<OutputVariable> availableOutputVariables) {
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
     * returns a collection of all clauses that are in the same group and have the same
     * composition behavior as the given clause
     * clauses that have a NullComposition only have 
     */
    public List<AtomicWhereClause> getSimilarClauses(AtomicWhereClause clause) {
        List<AtomicWhereClause> result = new ArrayList<AtomicWhereClause>();
        if (clause.getCompositionBehaviour() instanceof NullComposition) {
        	result.add(clause);
        }
        else {
			try {
		    	for (AtomicWhereClause aClause : atomicWhereClauses) {
		    		if (aClause.getGroups().containsAll(clause.getGroups()) &&
		    			clause.getCompositionBehaviour().getClass().equals(aClause.getCompositionBehaviour().getClass())) {
							result.add((AtomicWhereClause) aClause.clone());
		    		}
		    	}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
        }
    	
    	return result;
    } 
    
    public OutputVariable createOutputVariable(String objectName) {
    	return new OutputVariable(new VariableType(objectName), getVariableName(objectName), getObjectDescription(objectName));
    }
}