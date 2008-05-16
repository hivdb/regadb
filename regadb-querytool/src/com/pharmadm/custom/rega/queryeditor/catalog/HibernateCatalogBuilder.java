package com.pharmadm.custom.rega.queryeditor.catalog;

import java.sql.SQLException;
import java.util.*;

import com.pharmadm.custom.rega.awccomposition.*;
import com.pharmadm.custom.rega.queryeditor.port.*;
import com.pharmadm.custom.rega.queryeditor.*;
import com.pharmadm.custom.rega.queryeditor.constant.*;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.TestType;

public class HibernateCatalogBuilder implements CatalogBuilder{

	private AWCPrototypeCatalog catalog;
	
	public void fillCatalog(AWCPrototypeCatalog catalog) {
		this.catalog = catalog;
		
        addVariableNames();	    
        addAllTableClauses();
        addNumberClauses();
        addStringClauses();
        addBooleanClauses();
        addDateClauses();
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
    	String description = null;	        // no description of relation
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
            addTypeRestrictionToNominalValueClause(addStringPropertyComparisonClauses(inputTable, inputTableToIdTable, foreingTableName, foreignTableToIdTable, idTable, null, property, suggestedValuesQuery, caseSensitive, realVariableType, true, description, true),idTableToCustomPropertiesTable, propertyName, customPropertiesTableNameProperty, valueType);
    	}
    	else if (valueType.equals("string")) {
    		foreingTableName = idTableName;					// select from the single attribute table
    		foreignTableToIdTable = idTableToInputTable;	
    		caseSensitive = false;
    		
            addTypeRestrictionToNominalValueClause(addStringPropertyComparisonClauses(inputTable, inputTableToIdTable, foreingTableName, foreignTableToIdTable, idTable, null, property, null, caseSensitive, realVariableType, false, description, true),idTableToCustomPropertiesTable, propertyName, customPropertiesTableNameProperty, valueType);
    	}
    	else if (valueType.equals("number")) {
    		foreingTableName = idTableName;					// select from the single attribute table
    		foreignTableToIdTable = idTableToInputTable;	
    		caseSensitive = true;
    		
            addTypeRestrictionToNominalValueClause(addNumberPropertyComparisonClauses(inputTable, inputTableToIdTable, foreingTableName, foreignTableToIdTable, idTable, null, property, null, caseSensitive, realVariableType, false, description, true),idTableToCustomPropertiesTable, propertyName, customPropertiesTableNameProperty, valueType);
    	}
    	else if (valueType.equals("limited number (<,=,>)")) {
    		foreingTableName = idTableName;					// select from the single attribute table
    		foreignTableToIdTable = idTableToInputTable;	
    		caseSensitive = true;
    		
            addTypeRestrictionToNominalValueClause(addNumberPropertyComparisonClauses(inputTable, inputTableToIdTable, foreingTableName, foreignTableToIdTable, idTable, null, property, null, caseSensitive, realVariableType, false, description, true),idTableToCustomPropertiesTable, propertyName, customPropertiesTableNameProperty, valueType);
    	}
    	else if (valueType.equals("date")) {
    		foreingTableName = idTableName;					// select from the single attribute table
    		foreignTableToIdTable = idTableToInputTable;	
    		caseSensitive = true;
    		
            addTypeRestrictionToNominalValueClause(addDatePropertyComparisonClauses(inputTable, inputTableToIdTable, foreingTableName, foreignTableToIdTable, idTable, null, property, null, caseSensitive, realVariableType, false, description, true),idTableToCustomPropertiesTable, propertyName, customPropertiesTableNameProperty, valueType);
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
			if (clause.getConstants().size() > 0) {
				clause.setCompositionBehaviour(new CustomAttributeComposition());
			}
			
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
    			ovar.setFormalName(propertyName);
    			ovar.setDescription(propertyName);
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
    			else if (valueType.equalsIgnoreCase("date")) {
	    			List<ConfigurableWord> words = ovar.getExpression().getWords();
	    			List<ConfigurableWord> newWords = new ArrayList<ConfigurableWord>();
	    			newWords.add(new FixedString("CASE WHEN "));
	    			newWords.add(words.get(0));
	    			newWords.add(new FixedString("." + idTableToCustomPropertiesTable + "." + customPropertiesTableNameProperty + " = '" + propertyName + "'"));
	    			newWords.add(new FixedString("THEN cast ("));
	    			newWords.addAll(words);
	    			newWords.add(new FixedString(", date) ELSE current_date() END"));
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
    	if (!checkLinkValid(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey)) {
    		return null;
    	}

    	AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup(catalog.getObjectDescription(inputTableName));
		aClause.setCompositionBehaviour(new TableFetchComposition());
        
        InputVariable ivar = new InputVariable(new VariableType(inputTableName));
        description = description == null ? "has an associated " : description;
        FromVariable newFromVar = new FromVariable(foreignTableName);
        OutputVariable ovar =  catalog.createOutputVariable(foreignTableName);
        ovar.setRelation(description);
        ovar.getExpression().addFromVariable(newFromVar);
        
        aVisList.addFixedString(new FixedString(catalog.getTable(inputTableName).getDescription()));
        aVisList.addInputVariable(ivar);
        aVisList.addOutputVariable(ovar);
        
        addRelationClauseToComposer(aComposer, ivar, newFromVar, inputTableToIdTable, foreignTableToIdTable, new FromVariable(idTableName), idTableKey, invertLink);
        
        catalog.addAtomicWhereClause(aClause);
        return aClause;
    }
    
    
    /**
     * adds a to clause to the given where clause composer to link the given input table to the given foreigntable
     * @param aComposer where clause composer to add the relation to
     * @param inputTable table to start from. Either an inputvariable or a fromvariable
     * @param foreignTable name of the table to make a relation with
	 * @param inputTableToIdTable path from the input table to the id table 
	 *                            null if they are the same table
	 * @param foreignTableToIdTable path from the foreign table to the id table
	 *                              null if they are the same table
	 * @param idTable table that acts as key between the input table and the foreign table
	 * @param idTableKey element of idTableName that is the key
	 *                   null if the table should be used as key
	 * @param invertLink false for a relation from input and foreigntable to idtable
	 *                   true for a relation from idtable to input and foreigntable
     */
    private void addRelationClauseToComposer(WhereClauseComposer aComposer, AWCWord inputTable, FromVariable foreignTable, String inputTableToIdTable, String foreignTableToIdTable, FromVariable idTable, String idTableKey, boolean invertLink) {
    	if (!invertLink) {
    		// regular link between input table and foreign table
    		// check if they point to the same id table
            aComposer.addWord(inputTable);
            aComposer.addFixedString(new FixedString((inputTableToIdTable != null ? "." + inputTableToIdTable: "") + (idTableKey != null ?"." + idTableKey:"") + " = "));
            aComposer.addFromVariable(foreignTable);
            aComposer.addFixedString(new FixedString((foreignTableToIdTable != null ? "." + foreignTableToIdTable:"") + (idTableKey != null ?"." + idTableKey:"")));
    	}
    	else {
    		// inverted link
    		// go from the id table to the input table and foreign table
            aComposer.addFromVariable(idTable);
            aComposer.addFixedString(new FixedString((inputTableToIdTable != null ? "." + inputTableToIdTable: "") + " = "));
            aComposer.addWord(inputTable);
            aComposer.addFixedString(new FixedString(" AND\n\t"));
            aComposer.addFromVariable(idTable);
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
    	if (catalog.getTable(inputTableName) == null) {
    		System.err.println("Can't create collection assocation clause. Table " + inputTableName + " does not exist");
    		return null;
    	}
    	if (catalog.getTable(foreignTableName) == null) {
    		System.err.println("Can't create collection assocation clause. Table " + foreignTableName + " does not exist");
    		return null;
    	}
    	
    	
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup(catalog.getObjectDescription(inputTableName));
		aClause.setCompositionBehaviour(new TableFetchComposition());
        
        InputVariable ivar = new InputVariable(new VariableType(inputTableName));
        description = description == null ? "have an associated " : description;
        FromVariable newFromVar = new FromVariable(foreignTableName);
        OutputVariable ovar = catalog.createOutputVariable(foreignTableName);
        ovar.setRelation(description);
        ovar.getExpression().addFromVariable(newFromVar);

        aVisList.addFixedString(new FixedString("The "));
        aVisList.addInputVariable(ivar);
        aVisList.addOutputVariable(ovar);
        
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(" IN ELEMENTS("));
        aComposer.addFromVariable(newFromVar);
        aComposer.addFixedString(new FixedString("." +  foreignTableProperty));
        aComposer.addFixedString(new FixedString(")"));
        
        catalog.addAtomicWhereClause(aClause);
        return aClause;
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
	private List<AtomicWhereClause> addNumberPropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, String suggestedValuesQuery, boolean caseSensitive, String valueType, boolean invertLink, String description, boolean show) {
		List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	
		Constant constant = new DoubleConstant();
    	if (suggestedValuesQuery != null) {
    		constant.setSuggestedValuesQuery(suggestedValuesQuery);
    		constant.setSuggestedValuesMandatory(true);
    	}
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, show, caseSensitive, constant, null, valueType, invertLink, description));
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, show, caseSensitive, constant, getNumberComparisonOperator(), valueType, invertLink, description));
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
	private List<AtomicWhereClause> addNumberPropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, boolean dropdown, boolean invertLink) {
		String suggestedValuesQuery = null;
    	if (dropdown) {
    		suggestedValuesQuery = "SELECT DISTINCT obj." + foreignTableProperty + " FROM " + foreignTableName + " obj";
    	}
		boolean show = foreignTableName != inputTableName || foreignTableProperty.indexOf('.') >= 0;
		return addNumberPropertyComparisonClauses(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, suggestedValuesQuery, true, "Numeric", invertLink, null, show);
	}
	
	private List<AtomicWhereClause> addNumberPropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, boolean dropdown) {
		return addNumberPropertyComparisonClauses(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, dropdown, false);
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
	 * @return All the clauses that have been added
	 */
	private List<AtomicWhereClause> addBooleanPropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, boolean invertLink) {
		List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	boolean show = foreignTableName != inputTableName || foreignTableProperty.indexOf('.') >= 0;
    	boolean caseSensitive = true;
    	String valueType = "Boolean";
    	Constant constant = new BooleanConstant();
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, show, caseSensitive, constant, null, valueType, invertLink, null));
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, show, caseSensitive, constant, getBooleanComparisonOperator(), valueType, invertLink, null));
		return list;
	}
	
	private List<AtomicWhereClause> addBooleanPropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty) {
		return addBooleanPropertyComparisonClauses(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, false);
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
	 * @param suggestedValuesQuery the query for suggested values if you want a dropdown
	 *                             null if you don't want a dropdown
     * @param caseSensitive true for case sensitive comparison
     *                      false for case insensitive comparison
	 * @param valueType value type of the property
	 *                  most likely date, but could be string if you want to force
	 *                  a string value to be interpreted as a date
	 * @param invertLink false for a relation from input and foreigntable to idtable
	 *                   true for a relation from idtable to input and foreigntable
	 * @return All the clauses that have been added
	 */
	private List<AtomicWhereClause> addDatePropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, String suggestedValuesQuery, boolean caseSensitive, String valueType, boolean invertLink, String description, boolean show) {
		List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	
		Constant constant = new DateConstant();
    	if (suggestedValuesQuery != null) {
    		constant.setSuggestedValuesQuery(suggestedValuesQuery);
    		constant.setSuggestedValuesMandatory(true);
    	}
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, show, caseSensitive, constant, null, valueType, invertLink, description));
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, show, caseSensitive, constant, getDateComparisonOperator(), valueType, invertLink, description));
    	
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
	 * @param invertLink false for a relation from input and foreigntable to idtable
	 *                   true for a relation from idtable to input and foreigntable
	 * @return All the clauses that have been added
	 */
	private List<AtomicWhereClause> addDatePropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, boolean dropdown, boolean invertLink) {
		String suggestedValuesQuery = null;
    	if (dropdown) {
    		suggestedValuesQuery = "SELECT DISTINCT obj." + foreignTableProperty + " FROM " + foreignTableName + " obj";
    	}
		boolean show = foreignTableName != inputTableName || foreignTableProperty.indexOf('.') >= 0;
		return addDatePropertyComparisonClauses(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, suggestedValuesQuery, true, "Date", invertLink, null, show);
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
	private List<AtomicWhereClause> addDatePropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, boolean dropdown) {
		return addDatePropertyComparisonClauses(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, dropdown, false);
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
	private List<AtomicWhereClause> addStringPropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, String suggestedValuesQuery, boolean caseSensitive, String valueType, boolean invertLink, String description, boolean show) {
		List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	
		Constant constant = new StringConstant();
    	if (suggestedValuesQuery != null) {
    		constant.setSuggestedValuesQuery(suggestedValuesQuery);
    		constant.setSuggestedValuesMandatory(true);
    	}
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, show, caseSensitive, constant, null, valueType, invertLink, description));
		list.add(addPropertyComparisonClause(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, show, caseSensitive, constant, getStringComparisonOperator(), valueType, invertLink, description));
    	
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
	private List<AtomicWhereClause> addStringPropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, boolean dropdown) {
		return addStringPropertyComparisonClauses(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, dropdown, false);
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
	 */
	private List<AtomicWhereClause> addStringPropertyComparisonClauses(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, boolean dropdown, boolean invertLink) {
		String suggestedValuesQuery = null;
    	if (dropdown) {
    		suggestedValuesQuery = "SELECT DISTINCT obj." + foreignTableProperty + " FROM " + foreignTableName + " obj";
    	}
		boolean show = foreignTableName != inputTableName || foreignTableProperty.indexOf('.') >= 0;
		return addStringPropertyComparisonClauses(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey, foreignTableProperty, suggestedValuesQuery, false, "String", invertLink, null, show);
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
    private AtomicWhereClause addPropertyComparisonClause(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey, String foreignTableProperty, boolean show, boolean caseSensitive, Constant propertyConstant, Constant comparisonOperator, String valueType, boolean invertLink, String description) {
    	if (!checkLinkValid(inputTableName, inputTableToIdTable, foreignTableName, foreignTableToIdTable, idTableName, idTableKey)) {
    		return null;
    	}
    	if (catalog.getField(foreignTableName, foreignTableProperty) == null) {
    		System.err.println("Can't create propert comparison clause. Property " + foreignTableProperty + " of table " + foreignTableName + " does not exist");
    		return null;
    	}
    	
    	// find the data type of the property
    	// this method can resolve properties of ids
    	// returns null if the property is not found
    	String typeString = catalog.getDataTypeOfProperty(foreignTableName, foreignTableProperty);   	
    	if (typeString != null) {
            Constant constant = propertyConstant;
            
            // check if the type reported by the database is the same as specified in the catalog
            if (typeString.equals(valueType)) {
	            AtomicWhereClause aClause = new AtomicWhereClause();
	            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
                VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
                aClause.addGroup(catalog.getObjectDescription(inputTableName));
	            
                //// set all needed variables
                //
                
                // if we aren't doing a comparison we have to return a variable
                boolean fetchAsVariable = (comparisonOperator == null);
                
                // if we are returning a variable it has to be in the list of selectable outputvariables
                show = show || fetchAsVariable;
                
                // if no description is provided use the property name
	            String description2 = fetchAsVariable ? "has a " : "'s ";

	            // input table needed for input
	            // if both input table and foreign table are equal this variable will also be used
	            // to refer to the foreign table
	            InputVariable ivar = new InputVariable(new VariableType(inputTableName));
	            
	            // if input table and foreign table are not equal we will
	            // also need to select from the foreign table
                FromVariable newFromVar = new FromVariable(foreignTableName);
                
                
    	        //// build what gets shown in the query selector dialog
    	        //
    	        
                aVisList.addFixedString(new FixedString(catalog.getObjectDescription(inputTableName)));
                aVisList.addInputVariable(ivar);
                aVisList.addFixedString(new FixedString(description2));
                
                // add the output variable if the result should be selectable
                if (show) {
                    // build an outputvariable from the foreign table property and assign it a nice name
        	        OutputVariable ovar = new OutputVariable(new VariableType(constant.getValueTypeString()), catalog.getVariableName(foreignTableName + "." + foreignTableProperty), catalog.getObjectDescription(foreignTableName + "." + foreignTableProperty));
                    if (description != null) ovar.setRelation(description);
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
                else {
                	if (description != null) {
                		aVisList.addFixedString(new FixedString(description));                	
                	}
                	else {
                		aVisList.addFixedString(new FixedString(catalog.getObjectDescription(foreignTableName + "." + foreignTableProperty)));
                	}
                }
                
                // only show the input control for the constant if when needed 
                if (!fetchAsVariable) {
	                aVisList.addConstant(comparisonOperator);
	                aVisList.addConstant(constant);
                }
                else {
            		aClause.setCompositionBehaviour(new PropertyFetchComposition());
                }
                
                //// build the query
                //

                // only make a link between the foreign table and the input table if they
                // are not the same so we can keep the resulting query simple
                if (!foreignTableName.equals(inputTableName)) {
                	addRelationClauseToComposer(aComposer, ivar, newFromVar, inputTableToIdTable, foreignTableToIdTable, new FromVariable(idTableName), idTableKey, invertLink);
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
                
                catalog.addAtomicWhereClause(aClause);
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
    
    private boolean checkLinkValid(String inputTableName, String inputTableToIdTable, String foreignTableName, String foreignTableToIdTable, String idTableName, String idTableKey) {
    	if (catalog.getTable(inputTableName) == null) {
    		System.err.println("Can't create association clause. Table " + inputTableName + " does not exist");
    		return false;
    	}
    	if (catalog.getTable(foreignTableName) == null) {
    		System.err.println("Can't create association clause. Table " + inputTableName + " does not exist");
    		return false;
    	}
    	if (catalog.getTable(idTableName) == null) {
    		System.err.println("Can't create association clause. Table " + inputTableName + " does not exist");
    		return false;
    	}    	
    	if (idTableKey != null && catalog.getField(idTableName, idTableKey) == null) {
    		System.err.println("Can't create association clause. Id property " + idTableKey + " of table " + idTableName + " does not exist");
    		return false;
    	}
    	return true;
    }
    
    private AtomicWhereClause addGenericDrugResolvedClause() {
    	String therapyTableName = "net.sf.regadb.db.Therapy";
    	String genericDrugTableName = "net.sf.regadb.db.DrugGeneric";
    	String genericTherapyTableName = "net.sf.regadb.db.TherapyGeneric";
    	String commercialTherapyTableName = "net.sf.regadb.db.TherapyCommercial";
    	String genericTherapyToTherapy = ".id.therapy";
    	String genericTherapyToDrug = ".id.drugGeneric";
    	String commercialTherapyToTherapy = ".id.therapy";
    	String commercialTherapyToDrug = ".id.drugCommercial";
    	String commercialDrugToGenericDrug = ".drugGenerics";
    	
        AtomicWhereClause aClause = new AtomicWhereClause();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        aClause.setCompositionBehaviour(new TableFetchComposition());
        aClause.addGroup(catalog.getObjectDescription(therapyTableName));
        
        InputVariable therapy1 = new InputVariable(new VariableType(therapyTableName));
        FromVariable fromgDrug1 = new FromVariable(genericDrugTableName);
        OutputVariable gDrug1 = catalog.createOutputVariable(genericDrugTableName);
        gDrug1.setRelation("'s medication contains a drug that resolves to a");
        gDrug1.getExpression().addFromVariable(fromgDrug1);
        aClause.addFromVariable(fromgDrug1);
        

        aVisList.addFixedString(new FixedString(catalog.getTable(therapyTableName).getDescription()));
        aVisList.addInputVariable(therapy1);
        aVisList.addOutputVariable(gDrug1);
        
        aComposer.addOutputVariable(gDrug1);
        aComposer.addFixedString(new FixedString(" IN (\n\t\tSELECT\n\t\t\t"));
        aComposer.addFixedString(new FixedString(catalog.getTableAlias(genericTherapyTableName) + genericTherapyToDrug));
        aComposer.addFixedString(new FixedString("\n\t\tFROM\n\t\t\t"));
        aComposer.addFixedString(new FixedString(genericTherapyTableName + " " + catalog.getTableAlias(genericTherapyTableName)));
        aComposer.addFixedString(new FixedString("\n\t\tWHERE\n\t\t\t"));
        aComposer.addFixedString(new FixedString(catalog.getTableAlias(genericTherapyTableName) + genericTherapyToTherapy));
        aComposer.addFixedString(new FixedString(" = "));
        aComposer.addInputVariable(therapy1);
        aComposer.addFixedString(new FixedString(")\n\t OR "));
        aComposer.addOutputVariable(gDrug1);
        aComposer.addFixedString(new FixedString(" IN (\n\t\tSELECT\n\t\t\t"));
        aComposer.addFixedString(new FixedString(catalog.getTableAlias(genericDrugTableName)));
        aComposer.addFixedString(new FixedString("\n\t\tFROM\n\t\t\t"));
        aComposer.addFixedString(new FixedString(commercialTherapyTableName + " " + catalog.getTableAlias(commercialTherapyTableName)));
        aComposer.addFixedString(new FixedString(" JOIN\n\t\t\t"));
        aComposer.addFixedString(new FixedString(catalog.getTableAlias(commercialTherapyTableName) + commercialTherapyToDrug + commercialDrugToGenericDrug + " " + catalog.getTableAlias(genericDrugTableName)));
        aComposer.addFixedString(new FixedString("\n\t\tWHERE\n\t\t\t"));
        aComposer.addFixedString(new FixedString(catalog.getTableAlias(commercialTherapyTableName) + commercialTherapyToTherapy));
        aComposer.addFixedString(new FixedString(" = "));
        aComposer.addInputVariable(therapy1);
        aComposer.addFixedString(new FixedString(")"));
        
        catalog.addAtomicWhereClause(aClause);
        return aClause;
    }

	/**
     * add all clauses related to number variables
     * @return all the clauses that have been added
     */
    private List<AtomicWhereClause> addNumberClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(addNumericVariableDeclarationClause());
    	list.add(addNullClause("Number"));
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
    	list.add(addNullClause("String"));
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
    	list.add(addNullClause("Boolean"));
    	list.add(addBooleanVariableToConstantComparisonClause());
    	list.add(addBooleanVariableToVariableComparisonClause());
    	return list;
    }

    /**
     * add all clauses related to date variables
     * @return all the clauses that have been added
     */
    private List<AtomicWhereClause> addDateClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(addDateVariableDeclarationClause());
    	list.add(addNullClause("Date"));
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
    	list.add(addNullClause(objectName));
    	list.add(addObjectVariableToVariableComparisonClause(objectName));
    	return list;
    }
    
    /**
-     * gets a constant with the possible comparison operators for NULL tests (IS NULL, IS NOT NULL)
     * @return a constant with the possible comparison operators for NULL tests
     */
    private OperatorConstant getNullComparisonOperator() {
    	OperatorConstant constant = new OperatorConstant();
    	constant.addSuggestedValue(new SuggestedValuesOption("IS NOT NULL", "is defined"));
    	constant.addSuggestedValue(new SuggestedValuesOption("IS NULL", "is not defined"));
    	constant.setSuggestedValuesMandatory(true);
    	return constant;
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
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup(catalog.getObjectDescription(variableType));
        
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
        
        catalog.addAtomicWhereClause(aClause);
        return aClause;
    }
    
    /**
     * add a clause to turn a constant in a new numeric variable
     * @return the clause that has been added
     */
    private AtomicWhereClause addNumericVariableDeclarationClause() {
    	return addVariableDeclarationClause("Numeric", new DoubleConstant());
    }
    
    /**
     * add a clause to turn a constant in a new string variable
     * @return the clause that has been added
     */
    private AtomicWhereClause addStringVariableDeclarationClause() {
    	return addVariableDeclarationClause("String", new StringConstant());
    }


    /**
     * add a clause to turn a constant in a new date variable 
     * @return the clause that has been added
     */
    private AtomicWhereClause addDateVariableDeclarationClause() {
    	return addVariableDeclarationClause("Date", new DateConstant());
    }
    
    /**
     * add a clause to create a new persistent object variable
     * @param objectName name of the persistent object
     * @return the clause that has been added
     */
    private AtomicWhereClause addObjectVariableDeclarationClause(String objectName) {
    	return addVariableDeclarationClause(objectName, null);
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
    private AtomicWhereClause addVariableDeclarationClause(String variableType, Constant constant) {
    	if (new VariableType(variableType).isTable() && catalog.getTable(variableType) == null) {
    		System.err.println("Can not add table declaration. Table " + variableType + " does not exists");
    		return null;
    	}
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup(catalog.getObjectDescription("New Variable"));
        
        OutputVariable ovar = catalog.createOutputVariable(variableType);

        aVisList.addFixedString(new FixedString("There is a "));
        aVisList.addOutputVariable(ovar);

        if (constant != null) {
        	ovar.getExpression().addConstant(constant);
	        aVisList.addFixedString(new FixedString(" with value "));
	        aVisList.addConstant(constant);
        }
        else {
            FromVariable fromVar = new FromVariable(variableType);
	        ovar.getExpression().addFromVariable(fromVar);
            aClause.addFromVariable(fromVar);
            aClause.setCompositionBehaviour(new VariableDeclarationComposition());
        }
        
        aComposer.addFixedString(new FixedString("1=1"));
        
        catalog.addAtomicWhereClause(aClause);
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
        if (new VariableType(variableType).isTable()) {
        	System.err.println("Invalid variable type: " + variableType + ". Can't create a constant comparison clause with a persistent object.");
        	return null;
        }
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup(catalog.getObjectDescription(variableType));

        InputVariable ivar1 = new InputVariable(new VariableType(variableType));

        aVisList.addFixedString(new FixedString(description));
        aVisList.addInputVariable(ivar1);
        aVisList.addConstant(comparisonOperator);
        aVisList.addConstant(constant);
        
        addComparisonClauseToComposer(aComposer, ivar1, comparisonOperator, constant, caseSensitive);
        
        catalog.addAtomicWhereClause(aClause);
        return aClause;
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
    	return addVariableToVariableComparisonClause(objectName, catalog.getObjectDescription(objectName), getObjectComparisonOperator(), true);
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
        if (new VariableType(variableType).isTable() && catalog.getTable(variableType) == null) {
        	System.err.println("Persistent object not found : " + variableType);
        	return null;
        }
    	
    	AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup(catalog.getObjectDescription(variableType));

        InputVariable ivar1 = new InputVariable(new VariableType(variableType));
        InputVariable ivar2 = new InputVariable(new VariableType(variableType));

        aVisList.addFixedString(new FixedString(description));
        aVisList.addInputVariable(ivar1);
        aVisList.addConstant(comparisonOperator);
        aVisList.addInputVariable(ivar2);
        
        addComparisonClauseToComposer(aComposer, ivar1, comparisonOperator, ivar2, caseSensitive);
        
        catalog.addAtomicWhereClause(aClause);
        return aClause;
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
    	list.add(addVariableCalculationClause("Numeric", new DoubleConstant(), null, getNumberCalculationOperator(), ""));
    	list.add(addVariableCalculationClause("Numeric", null, "Numeric", getNumberCalculationOperator(), ""));
    	return list;
    }
    
    /**
     * add all clause to create a new string variable from a calculation between a two string
     * @return all the clauses that have been added
     */
    private List<AtomicWhereClause> addStringCalculationClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(addVariableCalculationClause("String", new StringConstant(), null, getStringCalculationOperator(), ""));
    	list.add(addVariableCalculationClause("String", null, "String", getStringCalculationOperator(), ""));
    	return list;
    }

    /**
     * add all clause to create a new date variable from a calculation between a string and a number of days
     * @return all the clauses that have been added
     */
    private List<AtomicWhereClause> addDateCalculationClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(addVariableCalculationClause("Date", new DoubleConstant(), null, getDateCalculationOperator(), "days"));
    	list.add(addVariableCalculationClause("Date", null, "Numeric", getDateCalculationOperator(), "days"));
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
     */    
    private AtomicWhereClause addVariableCalculationClause(String variableType, Constant calculationConstant, String calculationVariableType, OperatorConstant calculationOperator, String additionalDescription) {
        if (new VariableType(variableType).isTable()) {
        	System.err.println("Invalid variable type: " + variableType + ". Can't create a calculation clause with a persistent object.");
        	return null;
        }
    	
    	if (calculationConstant != null || calculationVariableType != null) {
            AtomicWhereClause aClause = new AtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
            aClause.addGroup("New Variable");

            InputVariable ivar1 = new InputVariable(new VariableType(variableType));
            InputVariable ivar2 = null;
            
            OutputVariable ovar = catalog.createOutputVariable(variableType);
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
            
            aVisList.addFixedString(new FixedString("There is a"));
            aVisList.addOutputVariable(ovar);
            aVisList.addFixedString(new FixedString(" with value "));
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
            
            catalog.addAtomicWhereClause(aClause);
            return aClause;
        } else {
            return null;
        }
    }
    
    private AtomicWhereClause addNullClause(String variableType) {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup(catalog.getObjectDescription(variableType));
        
        Constant cst = getNullComparisonOperator();
        InputVariable ivar = new InputVariable(new VariableType(variableType));
        
        aVisList.addFixedString(new FixedString(catalog.getObjectDescription(variableType)));
        aVisList.addInputVariable(ivar);
        aVisList.addConstant(cst);

        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(" "));
        aComposer.addConstant(cst);
        
        catalog.addAtomicWhereClause(aClause);
        return aClause;
    }
    
//    private AtomicWhereClause addCountClause(String variableType) {
//        AtomicWhereClause aClause = new AtomicWhereClause();
//        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
//        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
//        aClause.addGroup("Number");
//        
//        InputVariable ivar1 = new InputVariable(new VariableType(variableType));
//        OutputVariable ovar = new OutputVariable(new VariableType("Numeric"), "Numeric");
//        ovar.getExpression().addFixedString(new FixedString("count (distinct "));
//        ovar.getExpression().addInputVariable(ivar1);
//        ovar.getExpression().addFixedString(new FixedString(")"));
//        
//        aVisList.addFixedString(new FixedString("There is a"));
//        aVisList.addOutputVariable(ovar);
//        aVisList.addFixedString(new FixedString(" with value number of different"));
//        aVisList.addInputVariable(ivar1);
//        
//        aComposer.addFixedString(new FixedString("1=1"));
//
//        addAtomicWhereClause(aClause);
//        return aClause;
//    }
    
    private AtomicWhereClause addMutationClause() {
        AtomicWhereClause aClause = new AtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup(catalog.getObjectDescription("net.sf.regadb.db.AaSequence"));
      
        InputVariable ivar1 = new InputVariable(new VariableType("net.sf.regadb.db.AaSequence"));
        Constant constant = new MutationConstant(ivar1);

        aVisList.addInputVariable(ivar1);
        aVisList.addFixedString(new FixedString("has mutation combination"));
        aVisList.addConstant(constant);
      
        aComposer.addConstant(constant);

        catalog. addAtomicWhereClause(aClause);
        return aClause;
    }
	
    /**
     * set all variable names, table descriptions and table aliases
     */
    private void addVariableNames() {
    	// primitive types
    	catalog.addNames("Numeric", "number", "number", "number");
    	catalog.addNames("String", "string", "string", "string");
    	catalog.addNames("Boolean", "boolean", "boolean", "boolean");
    	catalog.addNames("Date", "date", "date", "date");
    	
        // patients 
        catalog.addNames("net.sf.regadb.db.PatientImpl", "patient", "patient", "patient");
        catalog.addNames("net.sf.regadb.db.PatientImpl.patientId", "PatientId", "id", null);
        catalog.addNames("net.sf.regadb.db.PatientImpl.birthDate", "BirthDate", "birth date", null);
        catalog.addNames("net.sf.regadb.db.PatientImpl.deathDate", "DeathDate", "death date", null);
        catalog.addNames("net.sf.regadb.db.PatientImpl.lastName", "LastName", "last name", null);
        catalog.addNames("net.sf.regadb.db.PatientImpl.firstName", "FirstName", "first name", null);
        catalog.addNames("net.sf.regadb.db.AttributeNominalValue.value", "Attribute", "custom attribute", null);
        catalog.addNames("net.sf.regadb.db.PatientAttributeValue.value", "Attribute", "custom attribute", null);
        
        // therapy
        catalog.addNames("net.sf.regadb.db.Therapy", "therapy", "therapy", "therapy");
        catalog.addNames("net.sf.regadb.db.Therapy.startDate", "StartDate", "start date", null);
        catalog.addNames("net.sf.regadb.db.Therapy.stopDate", "StopDate", "stop date", null);
        catalog.addNames("net.sf.regadb.db.Therapy.comment", "Comment", "comment", null);

        // viral isolate
        catalog.addNames("net.sf.regadb.db.ViralIsolate", "viralIsolate","viral isolate", "vi");
        catalog.addNames("net.sf.regadb.db.ViralIsolate.sampleDate", "SampleDate", "sample date", null);
        catalog.addNames("net.sf.regadb.db.ViralIsolate.sampleId", "SampleId", "id", null);
        
        // nt sequence
        catalog.addNames("net.sf.regadb.db.NtSequence", "ntSequence", "nucleotide sequence", "ntSeq");
        catalog.addNames("net.sf.regadb.db.NtSequence.sequenceDate", "SequenceDate", "sequence date", null);
        catalog.addNames("net.sf.regadb.db.NtSequence.label", "Value", "value", null);
        catalog.addNames("net.sf.regadb.db.NtSequence.nucleotides", "Nucleotides", "nucleotides", null);
        
        // protein
        catalog.addNames("net.sf.regadb.db.Protein", "protein", "protein", "protein");
        catalog.addNames("net.sf.regadb.db.Protein.abbreviation", "ProteinAbbreviation", "abbreviation", null);
        catalog.addNames("net.sf.regadb.db.Protein.fullName", "ProteinName", "name", null);

        // aa sequence
        catalog.addNames("net.sf.regadb.db.AaSequence", "aaSequence", "amino acid sequence", "aaSeq");
        catalog.addNames("net.sf.regadb.db.AaSequence.firstAaPos", "AaPosition", "first amino acid position", null);
        catalog.addNames("net.sf.regadb.db.AaSequence.lastAaPos", "AaPosition", "last amino acid position", null);

        // aa mutation
        catalog.addNames("net.sf.regadb.db.AaMutation", "aaMutation", "amino acid mutation", "aaMut");
        catalog.addNames("net.sf.regadb.db.AaMutation.aaReference", "AaStr", "amino acid reference string", null);
        catalog.addNames("net.sf.regadb.db.AaMutation.aaMutation", "AaStr", "amino acid mutation string", null);
        catalog.addNames("net.sf.regadb.db.AaMutation.ntReferenceCodon", "NtStr", "nucleotide reference string", null);
        catalog.addNames("net.sf.regadb.db.AaMutation.ntMutationCodon", "NtStr", "nulceotide mutation string", null);
        catalog.addNames("net.sf.regadb.db.AaMutation.id.mutationPosition", "MutationPosition", "mutation position", null);
        
        // aa insertion
        catalog.addNames("net.sf.regadb.db.AaInsertion", "aaInsertion", "amino acid insertion", "aaIns");
        catalog.addNames("net.sf.regadb.db.AaInsertion.aaInsertion", "AaStr", "amino acid insertion string", null);
        catalog.addNames("net.sf.regadb.db.AaInsertion.ntInsertionCodon", "NtStr", "nucleotide insertion string", null);
        catalog.addNames("net.sf.regadb.db.AaInsertion.id.insertionPosition", "InsertionPosition", "insertion position", null);
        catalog.addNames("net.sf.regadb.db.AaInsertion.id.insertionOrder", "InsertionOrder", "insertion order", null);
        
        // drug class
        catalog.addNames("net.sf.regadb.db.DrugClass", "drugClass", "drug class", "drugClass");
        catalog.addNames("net.sf.regadb.db.DrugClass.classId", "DrugClassId", "id", null);
        catalog.addNames("net.sf.regadb.db.DrugClass.className", "DrugClassName", "name", null);
        catalog.addNames("net.sf.regadb.db.DrugClass.resistanceTableOrder", "ResitanceTableOrder", "resistance table order", null);

        // therapy commercial
        catalog.addNames("net.sf.regadb.db.TherapyCommercial", "commercialDrugTreatment", "treatment with a commercial drug", "commTherapy");
        catalog.addNames("net.sf.regadb.db.TherapyCommercial.dayDosageUnits", "DailyDosage", "daily dosage", null);
        catalog.addNames("net.sf.regadb.db.TherapyCommercial.frequency", "Frequency", "administration frequency", null);
        catalog.addNames("net.sf.regadb.db.TherapyCommercial.placebo", "Placebo", "placebo", null);
        catalog.addNames("net.sf.regadb.db.TherapyCommercial.blind", "Blind", "blind", null);
        
        // therapy generic
        catalog.addNames("net.sf.regadb.db.TherapyGeneric", "genericDrugTreatment", "treatment with a generic drug", "genTherapy");
        catalog.addNames("net.sf.regadb.db.TherapyGeneric.dayDosageMg", "DailyDosage", "daily dosage in mg", null);
        catalog.addNames("net.sf.regadb.db.TherapyGeneric.frequency", "Frequency", "administration frequency", null);
        catalog.addNames("net.sf.regadb.db.TherapyGeneric.placebo", "Placebo", "placebo", null);
        catalog.addNames("net.sf.regadb.db.TherapyGeneric.blind", "Blind", "blind", null);
        
        // events
        catalog.addNames("net.sf.regadb.db.PatientEventValue", "event", "event", "event");
        catalog.addNames("net.sf.regadb.db.PatientEventValue.startDate", "StartDate", "start date", null);
        catalog.addNames("net.sf.regadb.db.PatientEventValue.endDate", "EndDate", "end date", null);
        catalog.addNames("net.sf.regadb.db.PatientEventValue.value", "Event", "event", null);
        catalog.addNames("net.sf.regadb.db.EventNominalValue.value", "Event", "event", null);
        
        // drug generic
        catalog.addNames("net.sf.regadb.db.DrugGeneric", "genericDrug", "generic drug", "genDrug");
        catalog.addNames("net.sf.regadb.db.DrugGeneric.genericId", "GenericDrugId", "id", null);
        catalog.addNames("net.sf.regadb.db.DrugGeneric.atcCode", "AtcCode", "atc code", null);
        catalog.addNames("net.sf.regadb.db.DrugGeneric.resistanceTableOrder", "ResistanceTableOrder", "resistance table order", null);
        catalog.addNames("net.sf.regadb.db.DrugGeneric.genericName", "GenericName", "name", null);
        
        // drug commercial
        catalog.addNames("net.sf.regadb.db.DrugCommercial", "commercialDrug", "commercial drug", "commDrug");
        catalog.addNames("net.sf.regadb.db.DrugCommercial.name", "CommercialName", "name", null);
        catalog.addNames("net.sf.regadb.db.DrugCommercial.atcCode", "AtcCode", "atc code", null);
        
        // dataset
        catalog.addNames("net.sf.regadb.db.Dataset", "dataset", "dataset", "dataset");
        catalog.addNames("net.sf.regadb.db.Dataset.description", "DatasetName", "dataset name", null);
        
        // test result
        catalog.addNames("net.sf.regadb.db.TestResult", "testResult", "test result", "result");
        catalog.addNames("net.sf.regadb.db.TestResult.sampleId", "SampleId", "id", null);
        catalog.addNames("net.sf.regadb.db.TestResult.testDate", "testDate", "test date", null);
        catalog.addNames("net.sf.regadb.db.TestNominalValue.value", "TestResult", "test result", null);
        catalog.addNames("net.sf.regadb.db.TestResult.value", "TestResult", "test result", null);
        
        // test
        catalog.addNames("net.sf.regadb.db.Test", "test", "test", "test");
        catalog.addNames("net.sf.regadb.db.Test.Description", "Name", "name", null);
        catalog.addNames("net.sf.regadb.db.TestType.description", "TestType", "test type", null);
        catalog.addNames("net.sf.regadb.db.TestObject.description", "testObject", "test object", null);

        // therapy motivation
        catalog.addNames("net.sf.regadb.db.TherapyMotivation", "motivation","motivation" , "motivation");
        catalog.addNames("net.sf.regadb.db.TherapyMotivation.value", "Motivation", "motivation", null);
    }
    
    private void addAllTableClauses() {
        addObjectClauses("net.sf.regadb.db.PatientImpl");
        addObjectClauses("net.sf.regadb.db.Therapy");
        addObjectClauses("net.sf.regadb.db.TherapyGeneric");
        addObjectClauses("net.sf.regadb.db.TherapyCommercial");
        addObjectClauses("net.sf.regadb.db.DrugGeneric");
        addObjectClauses("net.sf.regadb.db.DrugCommercial");
        addObjectClauses("net.sf.regadb.db.DrugClass");
        addObjectClauses("net.sf.regadb.db.ViralIsolate");
        addObjectClauses("net.sf.regadb.db.NtSequence");
        addObjectClauses("net.sf.regadb.db.AaSequence");
        addObjectClauses("net.sf.regadb.db.AaMutation");
        addObjectClauses("net.sf.regadb.db.AaInsertion");
        addObjectClauses("net.sf.regadb.db.Protein");
        addObjectClauses("net.sf.regadb.db.TestResult");
        addObjectClauses("net.sf.regadb.db.Test");
        addObjectClauses("net.sf.regadb.db.PatientEventValue");
    	
    	
        ///////////////////////////////////////
        // events
        addDatePropertyComparisonClauses("net.sf.regadb.db.PatientEventValue", null, "net.sf.regadb.db.PatientEventValue", null, "net.sf.regadb.db.PatientEventValue", null, "startDate", false);
        addDatePropertyComparisonClauses("net.sf.regadb.db.PatientEventValue", null, "net.sf.regadb.db.PatientEventValue", null, "net.sf.regadb.db.PatientEventValue", null, "endDate", false);
        
        try {
        	QueryResult result = DatabaseManager.getInstance().getDatabaseConnector().executeQuery("from net.sf.regadb.db.Event");
        	for (int i = 0 ; i < result.size() ; i++) {
        		Event event = (Event) result.get(i, 0);
        		addCustomPropertyComparisonClauses(event.getName(), event.getValueType().getDescription(), "net.sf.regadb.db.Event", "net.sf.regadb.db.EventNominalValue", "net.sf.regadb.db.PatientEventValue", "event", "eventNominalValue", "event", "net.sf.regadb.db.PatientEventValue", null, "name");
        	}
        }
        catch(SQLException e) {}
        
        // link patients - event
        addRelationClauses("net.sf.regadb.db.PatientEventValue", "patient", "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "comes from",  "has an", false);
        
        
        ///////////////////////////////////////
        // patients
        addStringPropertyComparisonClauses("net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null,  "net.sf.regadb.db.PatientImpl", null, "patientId", false);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "lastName", false);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "firstName", false);
   		addDatePropertyComparisonClauses("net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "birthDate", false);
        addDatePropertyComparisonClauses("net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "deathDate", false);

        // data set
        addStringPropertyComparisonClauses("net.sf.regadb.db.PatientImpl", "id.patient", "net.sf.regadb.db.Dataset", "id.dataset",  "net.sf.regadb.db.PatientDataset", null, "description", true, true);
        
        
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
        addRelationClauses("net.sf.regadb.db.Therapy", "patient", "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "was performed on a",  "has received", false);
        
        // link patient - viral isolate
        addRelationClauses("net.sf.regadb.db.ViralIsolate", "patient", "net.sf.regadb.db.PatientImpl",  null, "net.sf.regadb.db.PatientImpl", null, "comes from",  "has a", false);
        
        

        ///////////////////////////////////////
        // therapies
        addDatePropertyComparisonClauses("net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "startDate", false);
        addDatePropertyComparisonClauses("net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "stopDate", false);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "net.sf.regadb.db.Therapy", null, "comment", false);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.Therapy", "therapyMotivation", "net.sf.regadb.db.TherapyMotivation", null, "net.sf.regadb.db.TherapyMotivation", null, "value", true);
   		
   		addGenericDrugResolvedClause();   		
   		
        // link therapy - commercial drug
        addRelationClauses("net.sf.regadb.db.DrugCommercial", "id.drugCommercial", "net.sf.regadb.db.Therapy", "id.therapy" , "net.sf.regadb.db.TherapyCommercial", null, "is one of the drugs used in a",  "'s medication contains a", true);

        // link therapy - generic drug
        addRelationClauses("net.sf.regadb.db.DrugGeneric", "id.drugGeneric", "net.sf.regadb.db.Therapy", "id.therapy" , "net.sf.regadb.db.TherapyGeneric", null, "is one of the drugs used in a",  "'s medication contains a", true);
        

        ///////////////////////////////////////
        // therapyCommercial
        addNumberPropertyComparisonClauses("net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null, "dayDosageUnits", true);
        addNumberPropertyComparisonClauses("net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null, "frequency",  false);
   		addBooleanPropertyComparisonClauses("net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null,  "net.sf.regadb.db.TherapyCommercial", null, "placebo");
   		addBooleanPropertyComparisonClauses("net.sf.regadb.db.TherapyCommercial", null, "net.sf.regadb.db.TherapyCommercial", null,  "net.sf.regadb.db.TherapyCommercial", null, "blind");
        
        // link therapyCommercial - DrugCommercial
        addRelationClauses("net.sf.regadb.db.DrugCommercial", null, "net.sf.regadb.db.TherapyCommercial", "id.drugCommercial", "net.sf.regadb.db.DrugCommercial", null, "is used in a",  "consists of the", false);
        
        // link therapyComercial - therapy
        addRelationClauses("net.sf.regadb.db.TherapyCommercial", "id.therapy", "net.sf.regadb.db.Therapy", null , "net.sf.regadb.db.Therapy", null, "is a part of",  "has a", false);
        
        
        ///////////////////////////////////////
        // therapyGeneric
        addNumberPropertyComparisonClauses("net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null, "dayDosageMg",  true);
        addNumberPropertyComparisonClauses("net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null, "frequency", false);
   		addBooleanPropertyComparisonClauses("net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null,  "net.sf.regadb.db.TherapyGeneric", null, "placebo");
   		addBooleanPropertyComparisonClauses("net.sf.regadb.db.TherapyGeneric", null, "net.sf.regadb.db.TherapyGeneric", null,  "net.sf.regadb.db.TherapyGeneric", null, "blind");
        
        
        // link therapyGeneric - DrugCommercial
        addRelationClauses("net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.TherapyGeneric", "id.drugGeneric", "net.sf.regadb.db.DrugGeneric", null, "is used in a",  "consists of the", false);
        
        // link therapyGeneric - therapy
        addRelationClauses("net.sf.regadb.db.TherapyGeneric", "id.therapy", "net.sf.regadb.db.Therapy", null , "net.sf.regadb.db.Therapy", null, "is a part of",  "has a", false);

        
        ///////////////////////////////////////
        // viral isolates
        addStringPropertyComparisonClauses("net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "sampleId", false);
        addDatePropertyComparisonClauses("net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "sampleDate", false);
        
        // link viral isolate  - nt sequence
        addRelationClauses("net.sf.regadb.db.NtSequence", "viralIsolate", "net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "comes from a",  "has a", false);
 
        
        ///////////////////////////////////////
        // nucleotide sequence
        addDatePropertyComparisonClauses("net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "sequenceDate",false);
        addStringPropertyComparisonClauses("net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "label", false);
        addStringPropertyComparisonClauses("net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "nucleotides", false);
        
        // link nt sequence - patient
        addRelationClauses("net.sf.regadb.db.NtSequence", "viralIsolate.patient", "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "comes from a",  "has a", false);

        // link nt sequence - aa sequence
        addRelationClauses("net.sf.regadb.db.AaSequence", "ntSequence", "net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.NtSequence", null, "comes from a",  "has a", false);

        

        ///////////////////////////////////////
        // amino acid sequence
        addNumberPropertyComparisonClauses("net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "firstAaPos",  false);
        addNumberPropertyComparisonClauses("net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "lastAaPos", false);
        addMutationClause();
        
        // link aa sequence - patient
        addRelationClauses("net.sf.regadb.db.AaSequence", "ntSequence.viralIsolate.patient", "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "comes from a",  "has a", false);
        
        // link aa sequence - viral isolate
        addRelationClauses("net.sf.regadb.db.AaSequence", "ntSequence.viralIsolate", "net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.ViralIsolate", null, "comes from a",  "has a", false);

        // link aa sequence - aa mutation
        addRelationClauses("net.sf.regadb.db.AaMutation", "id.aaSequence", "net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "comes from the",  "has an", false);
        
        // link aa sequence - aa insertion
        addRelationClauses("net.sf.regadb.db.AaInsertion", "id.aaSequence", "net.sf.regadb.db.AaSequence", null, "net.sf.regadb.db.AaSequence", null, "comes from the",  "has an", false);
        
        // link aa sequence - protein
        addRelationClauses("net.sf.regadb.db.Protein", null, "net.sf.regadb.db.AaSequence", "protein", "net.sf.regadb.db.Protein", null, "is present in the",  "has a", false);

        
        
        ///////////////////////////////////////
        // protein
        addStringPropertyComparisonClauses("net.sf.regadb.db.Protein", null, "net.sf.regadb.db.Protein", null, "net.sf.regadb.db.Protein", null,"abbreviation", true);
        addStringPropertyComparisonClauses("net.sf.regadb.db.Protein", null, "net.sf.regadb.db.Protein", null, "net.sf.regadb.db.Protein", null,"fullName", true);

        
        
        ///////////////////////////////////////
        // amino acid mutation
        addStringPropertyComparisonClauses("net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation",  null , "aaReference",  false);
        addStringPropertyComparisonClauses("net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation",  null , "aaMutation", false);
        addStringPropertyComparisonClauses("net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation",  null , "ntReferenceCodon", false);
        addStringPropertyComparisonClauses("net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation",  null , "ntMutationCodon",  false);
        addNumberPropertyComparisonClauses("net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation", null, "net.sf.regadb.db.AaMutation",  null , "id.mutationPosition",  false);
        
        // link aa mutation - patient
        addRelationClauses("net.sf.regadb.db.AaMutation", "id.aaSequence.ntSequence.viralIsolate.patient", "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "comes from a",  "has a", false);
        

        ///////////////////////////////////////
        // amino acid insertion
        addStringPropertyComparisonClauses("net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null, "aaInsertion",  false);
        addStringPropertyComparisonClauses("net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null, "ntInsertionCodon",  false);
        addNumberPropertyComparisonClauses("net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null ,"id.insertionPosition", false);
        addNumberPropertyComparisonClauses("net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null, "net.sf.regadb.db.AaInsertion", null ,"id.insertionOrder",  false);
        
        // link aa insertion - patient
        addRelationClauses("net.sf.regadb.db.AaInsertion", "id.aaSequence.ntSequence.viralIsolate.patient", "net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.PatientImpl", null, "comes from a",  "has a", false);

        
        ///////////////////////////////////////
        // generic drugs
   		addStringPropertyComparisonClauses("net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "genericId",  false);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "genericName", true);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "atcCode", true);
   		addNumberPropertyComparisonClauses("net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.DrugGeneric", null, "resistanceTableOrder", true);

        // link generic drug - drug class
        addRelationClauses("net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugGeneric", "drugClass", "net.sf.regadb.db.DrugClass", null, "has a",  "belongs to the", false);
   		
        
        
        ///////////////////////////////////////
        // commercial drug
   		addStringPropertyComparisonClauses("net.sf.regadb.db.DrugCommercial", null, "net.sf.regadb.db.DrugCommercial", null, "net.sf.regadb.db.DrugCommercial", null, "name", true);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.DrugCommercial", null, "net.sf.regadb.db.DrugCommercial", null, "net.sf.regadb.db.DrugCommercial", null, "atcCode", true);
        
        // link commercial - generic
   		addCollectionRelationClause("net.sf.regadb.db.DrugGeneric", "net.sf.regadb.db.DrugCommercial", "drugGenerics", "is a component of a");
   		addCollectionRelationClause("net.sf.regadb.db.DrugCommercial", "net.sf.regadb.db.DrugGeneric", "drugCommercials", "has a component");
   		
   		
        
   		///////////////////////////////////////
        // drug class
   		addStringPropertyComparisonClauses("net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "className", true);
   		addStringPropertyComparisonClauses("net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "classId", false);
   		addNumberPropertyComparisonClauses("net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "net.sf.regadb.db.DrugClass", null, "resistanceTableOrder", false);
   		
   		
   		
        ///////////////////////////////////////
        // test result
   		addStringPropertyComparisonClauses("net.sf.regadb.db.TestResult", null, "net.sf.regadb.db.TestResult", null, "net.sf.regadb.db.TestResult", null, "sampleId",  false);
   		addDatePropertyComparisonClauses("net.sf.regadb.db.TestResult", null, "net.sf.regadb.db.TestResult", null, "net.sf.regadb.db.TestResult", null, "testDate", false);

        // link test result -  patients
        addRelationClauses("net.sf.regadb.db.PatientImpl", null, "net.sf.regadb.db.TestResult", "patient", "net.sf.regadb.db.PatientImpl", null, "has a",  "comes from a test on", false);
   		
        // link test result -  generic drug
        addRelationClauses("net.sf.regadb.db.DrugGeneric", null, "net.sf.regadb.db.TestResult", "drugGeneric", "net.sf.regadb.db.DrugGeneric", null, "has a",  "comes from a test on", false);

        // link test result -  viral isolate
        addRelationClauses("net.sf.regadb.db.ViralIsolate", null, "net.sf.regadb.db.TestResult", "viralIsolate", "net.sf.regadb.db.ViralIsolate", null, "has a",  "comes from a test on", false);

        // link test result -  nucleotide sequence
        addRelationClauses("net.sf.regadb.db.NtSequence", null, "net.sf.regadb.db.TestResult", "ntSequence", "net.sf.regadb.db.NtSequence", null, "has a",  "comes from a test on", false);
        
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
   		addStringPropertyComparisonClauses("net.sf.regadb.db.Test", null, "net.sf.regadb.db.Test", null, "net.sf.regadb.db.Test", null, "description",  true);
        
        // link test - test type
   		addStringPropertyComparisonClauses("net.sf.regadb.db.Test", "testType", "net.sf.regadb.db.TestType", null, "net.sf.regadb.db.TestType", null, "description", true);

   		// link test - test result
        addRelationClauses("net.sf.regadb.db.Test", null, "net.sf.regadb.db.TestResult", "test", "net.sf.regadb.db.Test", null, "has a",  "comes from the", false);
   		
        // link test - test object
   		addStringPropertyComparisonClauses("net.sf.regadb.db.Test", "testType.testObject", "net.sf.regadb.db.TestObject", null, "net.sf.regadb.db.TestObject", null, "description", true);
    }
}