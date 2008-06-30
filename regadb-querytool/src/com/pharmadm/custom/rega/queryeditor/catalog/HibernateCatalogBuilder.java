package com.pharmadm.custom.rega.queryeditor.catalog;

import java.sql.SQLException;
import java.util.*;

import com.pharmadm.custom.rega.awccomposition.*;
import com.pharmadm.custom.rega.queryeditor.port.*;
import com.pharmadm.custom.rega.queryeditor.*;
import com.pharmadm.custom.rega.queryeditor.ComposedAtomicWhereClause.ExportPolicy;
import com.pharmadm.custom.rega.queryeditor.ComposedAtomicWhereClause.VisualisationListPolicy;
import com.pharmadm.custom.rega.queryeditor.catalog.AWCPrototypeCatalog.Status;
import com.pharmadm.custom.rega.queryeditor.catalog.DbObject.ValueType;
import com.pharmadm.custom.rega.queryeditor.constant.*;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.TestType;

public class HibernateCatalogBuilder implements CatalogBuilder{

	private AWCPrototypeCatalog catalog;
	
	public void fillCatalog(AWCPrototypeCatalog catalog) {
		this.catalog = catalog;
		
		try {
			catalog.setTotalSize(320);
			catalog.setStatus(Status.BUSY);
	        addVariableNames();	    
	        addAllTableClauses();
	        catalog.addAll(getNumberClauses());
	        catalog.addAll(getStringClauses());
	        catalog.addAll(getBooleanClauses());
	        catalog.addAll(getDateClauses());
			catalog.setStatus(Status.DONE);
		}
		catch (Exception e) {
			catalog.setStatus(Status.FAILED);
			System.err.println("Error loading catalog:");
			e.printStackTrace();
		}
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
	 * @param nominalValues The table containing all the possible nominal values
	 * @param possibleIdTable table that holds:
	 * 				the references to the custom properties table
	 *              the reference to the nominal values table
	 *              the regular property value if it is not a nominal value
	 * @param idTableToCustomPropertiesTable path from the id table to the custom properties table
	 * @param idTableToNominalValuesTable path from the id table to the nominal values table
	 * @param nominalValuesTableToCustomPropertiesTable path from the nominal values table to the custom properties table
	 * @param inputTable table to start from
	 * @param idTableToInputTable path from the id table to the input table
	 *                            null if they are the same table
	 * @param customPropertiesTableNameProperty The attribute of the custom properties table that points to it's name
	 */
	private List<AtomicWhereClause> getCustomPropertyComparisonClauses(String propertyName, String valueType, DbObject customPropertiesTable, DbObject nominalValues, DbObject possibleIdTable, String idTableToCustomPropertiesTable, String idTableToNominalValuesTable, String nominalValuesTableToCustomPropertiesTable, DbObject inputTable, String idTableToInputTable, String customPropertiesTableNameProperty) {
		List<AtomicWhereClause> result;
		
    	String propertyStr = "value";			// regular value is always found in the value property
    	
    	DbObject idTable = inputTable;	      // start with id table same as input table		
    	String inputTableToIdTable = null;
    	DbObject foreingTable = inputTable; // start with foreign table and id table same as input table
    	String foreignTableToIdTable = null;
    	String suggestedValuesQuery = null;
    	ValueType t = ValueType.String;
    	
    	if (valueType.equals("nominal value")) {
    		foreingTable = nominalValues;			// select from the table of nominal values
    		foreignTableToIdTable = idTableToNominalValuesTable;	
    		idTable = possibleIdTable;							// use the id table as the id 
    		inputTableToIdTable = idTableToInputTable;
    		suggestedValuesQuery = "\nSELECT DISTINCT\n\tnv.value\nFROM\n\t" + nominalValues.getTableName() + " nv,\n\t" + customPropertiesTable.getTableName() + " obj\nWHERE\n\tnv." + nominalValuesTableToCustomPropertiesTable + " = obj AND\n\tobj." + customPropertiesTableNameProperty + "='" + propertyName + "'";
    	}
    	else if (valueType.equals("string") || valueType.equals("number") || valueType.equals("limited number (<,=,>)") || valueType.equals("date")) {
    		foreingTable = possibleIdTable;					// select from the single attribute table
    		foreignTableToIdTable = idTableToInputTable;	
        	if (valueType.equals("number") || valueType.equals("limited number (<,=,>)")) {
        		t = ValueType.Number;
        	}
        	else if (valueType.equals("date")) {
        		t = ValueType.Date;
        	}
        }
    	
    	ObjectRelation relation = new ObjectRelation(inputTable, inputTableToIdTable, foreingTable, foreignTableToIdTable, idTable, true, null);
		DbObject propertyOrig = catalog.getObject(foreingTable.getTableName(), propertyStr);
		DbObject property = new DbObject(propertyOrig.getTableName(), propertyOrig.getPropertyName(), propertyName, propertyOrig.getSqlAlias(), propertyName, propertyOrig.hasDropdown(), t);
		result = addTypeRestrictionToNominalValueClause(getPropertyComparisonClauses(relation, property, suggestedValuesQuery),idTableToCustomPropertiesTable, propertyName, customPropertiesTableNameProperty, valueType);
    	
    	return result;
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
	private List<AtomicWhereClause> addTypeRestrictionToNominalValueClause(List<AtomicWhereClause> clauses, String idTableToCustomPropertiesTable, String propertyName, String customPropertiesTableNameProperty, String valueType) {
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
		return clauses;
	}
	
    /**
     * Adds clauses to check for a relation between the given input table and foreign table
     * and vice-versa
	 * @param relations between the input table and the foreign table 
     * @param description1 A description of the relation between the input table and the foreign table
     * @param description2 A description of the relation between the foreign table and the input table
     * @return All the clause that have been made
     */
    private List<AtomicWhereClause> getRelationClauses(String table1, String table2) {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
        list.add(getRelationClause(catalog.getRelation(table1, table2)));
        list.add(getRelationClause(catalog.getRelation(table2, table1)));
        return list;
    }
	    
    /**
     * get a clause to check for a relation between the given input table and foreign table
	 * @param relations between the input table and the foreign table 
     * @param description A description of the relation between the input table and the foreign table
     * @return The clause that has been made
     */
    private AtomicWhereClause getRelationClause(ObjectRelation relation) {
    	AtomicWhereClause aClause = new SimpleAtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup(relation.getInputTable().getDescription());
		aClause.setCompositionBehaviour(new TableFetchComposition());
        
        InputVariable ivar = new InputVariable(relation.getInputTable());
        FromVariable newFromVar = new FromVariable(relation.getForeignTable());
        OutputVariable ovar =  new OutputVariable(relation.getForeignTable());
        ovar.setRelation(relation.getDescription());
        ovar.getExpression().addFromVariable(newFromVar);
        
        aVisList.addFixedString(new FixedString(relation.getInputTable().getDescription()));
        aVisList.addInputVariable(ivar);
        aVisList.addOutputVariable(ovar);
        
        addRelationClauseToComposer(aComposer, ivar, newFromVar, relation.getInputTableToIdTable(), relation.getForeignTableToIdTable(), new FromVariable(relation.getIdTable()), relation.getIdTableKey(), relation.isInvertLink());
        
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
	 * get a clause to check if instances of the table tableName can be found in the collection foreignTableProperty of foreignTableName
	 * @param inputTable table to start from
	 * @param foreignTableProperty A property of the foreign table that is a collection of input tables
	 * @param description A description of the relation between the input table and the foreign table
	 * @return The clause that has been made
	 */
    private AtomicWhereClause getCollectionRelationClause(DbObject inputTable, DbObject foreignTableProperty, String description) {
        AtomicWhereClause aClause = new SimpleAtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup(inputTable.getDescription());
		aClause.setCompositionBehaviour(new TableFetchComposition());
        
        InputVariable ivar = new InputVariable(inputTable);
        description = description == null ? "have an associated " : description;
        FromVariable newFromVar = new FromVariable(foreignTableProperty);
        OutputVariable ovar = new OutputVariable(foreignTableProperty.getTableObject());
        ovar.setRelation(description);
        ovar.getExpression().addFromVariable(newFromVar);

        aVisList.addFixedString(new FixedString("The "));
        aVisList.addInputVariable(ivar);
        aVisList.addOutputVariable(ovar);
        
        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(" IN ELEMENTS("));
        aComposer.addFromVariable(newFromVar);
        aComposer.addFixedString(new FixedString("." +  foreignTableProperty.getDescription()));
        aComposer.addFixedString(new FixedString(")"));
        
        return aClause;
    }
	
	/**
	 * get all clauses that check a given numeric property of a table
	 * @param relations between the input table and the property table 
	 * @param property the property to check
	 * @param suggestedValuesQuery the query for suggested values if you want a custom filled
	 *                             dropdown. 
	 * @return All the clauses that have been made
	 */
	private List<AtomicWhereClause> getPropertyComparisonClauses(ObjectRelation relation, DbObject property, String suggestedValuesQuery) {
		List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
		list.add(getPropertyComparisonClause(relation, property,  true, suggestedValuesQuery));
		list.add(getPropertyComparisonClause(relation, property,  false, suggestedValuesQuery));
    	return list;
	}
    
    /**
	 * get all clauses that check a given property of a table
	 * @param relations between the input table and the property table 
	 * @param property property to check
	 * @return All the clauses that have been made
	 */
	private List<AtomicWhereClause> getPropertyComparisonClauses(ObjectRelation relation, DbObject property) {
		return getPropertyComparisonClauses(relation, property, null);
	}
	
    /**
	 * get all clauses that check a given property of a table
	 * @param tableName name of the table
	 * @param propertyName property to check
	 *        this must be a property of the foreign table or of the composite id
	 *        of the foreign table. If it is a property of the composite id, include
	 *        the id in the path, like so: id.property
	 * @return All the clauses that have been made
	 */
	private List<AtomicWhereClause> getPropertyComparisonClauses(String tableName, String propertyName) {
		DbObject property = catalog.getObject(tableName, propertyName);
		ObjectRelation relation = new ObjectRelation(catalog.getObject(tableName));
		return getPropertyComparisonClauses(relation, property, null);
	}
	    
	 /**
	 * get a clause to check a property of a given table 
	 * @param relations between the input table and the property table 
	 * @param property the property to check
	 * @param fetchAsVariable true to simply fetch the property without comparison
	 * @param suggestedValuesQuery the query for suggested values if you want a custom filled
	 *                             dropdown. 
	 * @return the clause that have been made
	 */
    private AtomicWhereClause getPropertyComparisonClause(ObjectRelation relations, DbObject property, boolean fetchAsVariable, String suggestedValuesQuery) {
            
    		Constant constant = getConstant(property, suggestedValuesQuery);
            
            AtomicWhereClause aClause = new SimpleAtomicWhereClause();
            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            aClause.addGroup(relations.getInputTable().getDescription());
            
            //// set all needed variables
            //
            
            // if we are returning a variable it has to be in the list of selectable outputvariables
            boolean show = relations.isRelation() || fetchAsVariable;
            
            // if no description is provided use the property name
            String description2 = fetchAsVariable ? "has a " : "'s ";

            // input table needed for input
            // if both input table and foreign table are equal this variable will also be used
            // to refer to the foreign table
            InputVariable ivar = new InputVariable(relations.getInputTable());
            
            // if input table and foreign table are not equal we will
            // also need to select from the foreign table
            FromVariable newFromVar = new FromVariable(relations.getForeignTable());
            
            
	        //// build what gets shown in the query selector dialog
	        //
	        
            aVisList.addFixedString(new FixedString(relations.getInputTable().getDescription()));
            aVisList.addInputVariable(ivar);
            aVisList.addFixedString(new FixedString(description2));
            
            // add the output variable if the result should be selectable
            if (show) {
                // build an outputvariable from the foreign table property and assign it a nice name
    	        OutputVariable ovar = new OutputVariable(property);
    	        // outputvariables are defined as an expression. Without this expression they are useless
    	        if (!relations.isRelation()) {
    	        	ovar.getExpression().addInputVariable(ivar);
    	        }
    	        else {
	    	        ovar.getExpression().addFromVariable(newFromVar);
    	        }
    	        ovar.getExpression().addFixedString(new FixedString("." + property.getPropertyName()));
    	        aVisList.addOutputVariable(ovar);
            }
            else {
            		aVisList.addFixedString(new FixedString(property.getDescription()));
            }

            Constant comparisonOperator = getOperatorConstant(property, suggestedValuesQuery != null);
            // only show the input control for the constant when needed 
            if (!fetchAsVariable) {
                aVisList.addConstant(comparisonOperator);
                aVisList.addConstant(constant);
            	aClause.setCompositionBehaviour(new PropertySetComposition());	                	
            }
            else {
        		aClause.setCompositionBehaviour(new PropertyFetchComposition());
            }
            
            //// build the query
            //

            // only make a link between the foreign table and the input table if they
            // are not the same so we can keep the resulting query simple
            if (relations.isRelation()) {
            	addRelationClauseToComposer(aComposer, ivar, newFromVar, relations.getInputTableToIdTable(), relations.getForeignTableToIdTable(), new FromVariable(relations.getIdTable()), relations.getIdTableKey(), relations.isInvertLink());
            	if (!fetchAsVariable) aComposer.addFixedString(new FixedString(" AND\n\t "));
            }
            if (!fetchAsVariable) {
            	boolean caseSensitive = (isCaseSensitive(property));
            	// create comparison constraint
            	// [foreigntable.property] [operator] [constant]
                if (!caseSensitive) aComposer.addFixedString(new FixedString("UPPER("));
            	if (!relations.isRelation()) {
            		aComposer.addInputVariable(ivar);
            	}
            	else {
            		aComposer.addFromVariable(newFromVar);
            	}
                aComposer.addFixedString(new FixedString("." + property.getPropertyName()));
                if (!caseSensitive) aComposer.addFixedString(new FixedString(")"));
                aComposer.addFixedString(new FixedString(" "));
                aComposer.addConstant(comparisonOperator);
                aComposer.addFixedString(new FixedString(" "));
                if (!caseSensitive) aComposer.addFixedString(new FixedString("UPPER ("));
                aComposer.addConstant(constant);
                if (!caseSensitive) aComposer.addFixedString(new FixedString(")"));
            }
            
            return aClause;

    }
    
    private AtomicWhereClause getGenericDrugResolvedClause() {
    	DbObject therapyTable = catalog.getObject("Therapy");
    	DbObject genericDrugTable = catalog.getObject("DrugGeneric");
    	DbObject genericTherapyTable = catalog.getObject("TherapyGeneric");
    	DbObject commercialTherapyTable = catalog.getObject("TherapyCommercial");
    	String genericTherapyToTherapy = ".id.therapy";
    	String genericTherapyToDrug = ".id.drugGeneric";
    	String commercialTherapyToTherapy = ".id.therapy";
    	String commercialTherapyToDrug = ".id.drugCommercial";
    	String commercialDrugToGenericDrug = ".drugGenerics";
    	
    	SimpleAtomicWhereClause aClause = new SimpleAtomicWhereClause();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        aClause.setCompositionBehaviour(new TableFetchComposition());
        aClause.addGroup(therapyTable.getDescription());
        
        InputVariable therapy1 = new InputVariable(therapyTable);
        FromVariable fromgDrug1 = new FromVariable(genericDrugTable);
        OutputVariable gDrug1 = new OutputVariable(genericDrugTable);
        gDrug1.setRelation("'s medication contains a drug that resolves to a");
        gDrug1.getExpression().addFromVariable(fromgDrug1);
        aClause.addFromVariable(fromgDrug1);
        

        aVisList.addFixedString(new FixedString(therapyTable.getDescription()));
        aVisList.addInputVariable(therapy1);
        aVisList.addOutputVariable(gDrug1);
        
        aComposer.addOutputVariable(gDrug1);
        aComposer.addFixedString(new FixedString(" IN (\n\t\tSELECT\n\t\t\t"));
        aComposer.addFixedString(new FixedString(genericTherapyTable.getSqlAlias() + genericTherapyToDrug));
        aComposer.addFixedString(new FixedString("\n\t\tFROM\n\t\t\t"));
        aComposer.addFixedString(new FixedString(genericTherapyTable.getTableName() + " " + genericTherapyTable.getSqlAlias()));
        aComposer.addFixedString(new FixedString("\n\t\tWHERE\n\t\t\t"));
        aComposer.addFixedString(new FixedString(genericTherapyTable.getSqlAlias() + genericTherapyToTherapy));
        aComposer.addFixedString(new FixedString(" = "));
        aComposer.addInputVariable(therapy1);
        aComposer.addFixedString(new FixedString(")\n\t OR "));
        aComposer.addOutputVariable(gDrug1);
        aComposer.addFixedString(new FixedString(" IN (\n\t\tSELECT\n\t\t\t"));
        aComposer.addFixedString(new FixedString(genericDrugTable.getSqlAlias()));
        aComposer.addFixedString(new FixedString("\n\t\tFROM\n\t\t\t"));
        aComposer.addFixedString(new FixedString(commercialTherapyTable.getTableName() + " " + commercialTherapyTable.getSqlAlias()));
        aComposer.addFixedString(new FixedString(" JOIN\n\t\t\t"));
        aComposer.addFixedString(new FixedString(commercialTherapyTable.getSqlAlias() + commercialTherapyToDrug + commercialDrugToGenericDrug + " " + genericDrugTable.getSqlAlias()));
        aComposer.addFixedString(new FixedString("\n\t\tWHERE\n\t\t\t"));
        aComposer.addFixedString(new FixedString(commercialTherapyTable.getSqlAlias() + commercialTherapyToTherapy));
        aComposer.addFixedString(new FixedString(" = "));
        aComposer.addInputVariable(therapy1);
        aComposer.addFixedString(new FixedString(")"));
        
        return aClause;
    }
    
    private Constant getOperatorConstant(DbObject object, boolean exact) {
    	ValueType type = object.getValueType();
    	if (type == ValueType.String) {
    		return getStringComparisonOperator(exact);
    	}
    	else if (type == ValueType.Boolean) {
    		return getBooleanComparisonOperator();
    	}
    	else if (type == ValueType.Number) {
    		return getNumberComparisonOperator(exact);
    	}
    	else if (type == ValueType.Date) {
    		return getDateComparisonOperator(exact);
    	}
    	else {
    		return getObjectComparisonOperator();
    	}
    }
    
    private boolean isCaseSensitive(DbObject object) {
    	ValueType type = object.getValueType();
    	if (type == ValueType.String) {
    		return false;
    	}
    	else {
    		return true;
    	}
    }
    
    private Constant getConstant(DbObject object, String suggestedValuesQuery) {
    	ValueType type = object.getValueType();
    	Constant constant = null;
    	if (type == ValueType.String) {
    		constant = new StringConstant();
    	}
    	else if (type == ValueType.Boolean) {
    		constant = new BooleanConstant();
    	}
    	else if (type == ValueType.Number) {
    		constant = new DoubleConstant();
    	}
    	else if (type == ValueType.Date) {
    		constant = new DateConstant();
    	}
    	if (constant != null && type != ValueType.Boolean) {
    		if (object.hasDropdown() && suggestedValuesQuery == null) {
    			suggestedValuesQuery = "SELECT DISTINCT obj." + object.getPropertyName() + " FROM " + object.getTableName() + " obj";
    			constant.setSuggestedValuesQuery(suggestedValuesQuery);
        		constant.setSuggestedValuesMandatory(true);
    		}
    		else if (suggestedValuesQuery != null){
    			constant.setSuggestedValuesQuery(suggestedValuesQuery);
        		constant.setSuggestedValuesMandatory(true);
    		}
    	}
    	return constant;
    }

	/**
     * get all clauses related to number variables
     * @return all the clauses that have been made
     */
    private List<AtomicWhereClause> getNumberClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(getNumericVariableDeclarationClause());
    	list.add(getNullClause(catalog.getObject(ValueType.Number.toString())));
    	list.add(getNumericVariableToConstantComparisonClause());
    	list.add(getNumericVariableToVariableComparisonClause());
		list.add(getNumericVariableIntervalClause());
    	list.addAll(getNumericCalculationClauses());
    	return list;
    }
    
    /**
     * get all clauses related to string variables
     * @return all the clauses that have been made
     */
    private List<AtomicWhereClause> getStringClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(getStringVariableDeclarationClause());
    	list.add(getNullClause(catalog.getObject(ValueType.String.toString())));
    	list.add(getStringVariableToConstantComparisonClause());
    	list.add(getStringVariableToVariableComparisonClause());
    	list.add(getStringVariableIntervalClause());
    	list.addAll(getStringCalculationClauses());
    	return list;
    }
    
    /**
     * get all clauses related to boolean variables
     * @return all the clauses that have been made
     */
    private List<AtomicWhereClause> getBooleanClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(getNullClause(catalog.getObject(ValueType.Boolean.toString())));
    	list.add(getBooleanVariableToConstantComparisonClause());
    	list.add(getBooleanVariableToVariableComparisonClause());
    	return list;
    }

    /**
     * get all clauses related to date variables
     * @return all the clauses that have been made
     */
    private List<AtomicWhereClause> getDateClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(getDateVariableDeclarationClause());
    	list.add(getNullClause(catalog.getObject(ValueType.Date.toString())));
    	list.add(getDateVariableToConstantComparisonClause());
    	list.add(getDateVariableToVariableComparisonClause());
    	list.add(getDateVariableIntervalClause());
    	list.addAll(getDateCalculationClauses());
    	return list;
    }
    
    /**
     * get all clauses related to the given persistent objects 
     * @return all the clauses that have been made
     */
    private List<AtomicWhereClause> getObjectClauses(DbObject object) {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(getObjectVariableDeclarationClause(object.getTableObject()));
    	if (object.getPropertyName() != null) {
    		list.add(getNamedObjectDeclarationClause(object));
    	}
    	list.add(getNullClause(object.getTableObject()));
    	list.add(getObjectVariableToVariableComparisonClause(object.getTableObject()));
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
     * @param property the property to fetch the date comparator of
     * @param exact true when comparison may only be exact
     * @return a constant with the possible comparison operations for date values
     */
    private OperatorConstant getDateComparisonOperator(boolean exact) {
    	OperatorConstant constant = new OperatorConstant();
       	constant.addSuggestedValue(new SuggestedValuesOption("="," is on"));
    	constant.addSuggestedValue(new SuggestedValuesOption("<>", " is not on"));
    	if (!exact) {
	       	constant.addSuggestedValue(new SuggestedValuesOption("<", "is before"));
	    	constant.addSuggestedValue(new SuggestedValuesOption(">", "is after"));
    	}
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
    private OperatorConstant getStringComparisonOperator(boolean exact) {
    	OperatorConstant constant = new OperatorConstant();
       	constant.addSuggestedValue(new SuggestedValuesOption("LIKE", "is"));
    	constant.addSuggestedValue(new SuggestedValuesOption("NOT LIKE", "is not"));
    	if (!exact) {
	    	constant.addSuggestedValue(new SuggestedValuesOption("<", "comes before"));
	    	constant.addSuggestedValue(new SuggestedValuesOption(">", "comes after"));
    	}
    	constant.setSuggestedValuesMandatory(true);
    	return constant;
    }
    
    /**
     * Get a constant with the possible comparison operations for numeric values: (=, <>, <, >)
     * @return a constant with the possible comparison operations for numeric values
     */
    private OperatorConstant getNumberComparisonOperator(boolean exact) {
    	OperatorConstant constant = new OperatorConstant();
       	constant.addSuggestedValue(new SuggestedValuesOption("=", "is"));
    	constant.addSuggestedValue(new SuggestedValuesOption("<>", "is not"));
    	if (!exact) {
	       	constant.addSuggestedValue(new SuggestedValuesOption("<", "is less than"));
	    	constant.addSuggestedValue(new SuggestedValuesOption(">", "is more than"));
    	}
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
     * get a clause to check if a numeric variable is between two constants
     * @return the clause that has been made
     */
    private AtomicWhereClause getNumericVariableIntervalClause() {
    	return getVariableIntervalClause(catalog.getObject(ValueType.Number.toString()));
    }
    
    /**
     * get a clause to check if a date variable is between two constants
     * @return the clause that has been made
     */
    private AtomicWhereClause getDateVariableIntervalClause() {
    	return getVariableIntervalClause(catalog.getObject(ValueType.Date.toString()));
    }
    
    /**
     * get a clause to check if a string variable is between two constants
     * @return the clause that has been made
     */
    private AtomicWhereClause getStringVariableIntervalClause() {
    	return getVariableIntervalClause(catalog.getObject(ValueType.String.toString()));
    }

    /**
     * get a clause to check if a variable of the given type is between two constants
     * @param the object type
     * @return the clause that has been made
     */
    private AtomicWhereClause getVariableIntervalClause(DbObject object) {
        AtomicWhereClause aClause = new SimpleAtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup(object.getDescription());
        
        InputVariable ivar = new InputVariable(object);
        Constant comparisonOperator = getIntervalComparisonOperator();
        boolean caseSensitive = isCaseSensitive(object);
        Constant startConstant = getConstant(object, null);
        Constant endConstant = getConstant(object, null);
        
        aVisList.addFixedString(new FixedString(object.getDescription()));
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
        
        return aClause;
    }
    
    /**
     * get a clause to turn a constant in a new numeric variable
     * @return the clause that has been made
     */
    private AtomicWhereClause getNumericVariableDeclarationClause() {
    	return getVariableDeclarationClause(catalog.getObject(ValueType.Number.toString()));
    }
    
    /**
     * get a clause to turn a constant in a new string variable
     * @return the clause that has been made
     */
    private AtomicWhereClause getStringVariableDeclarationClause() {
    	return getVariableDeclarationClause(catalog.getObject(ValueType.String.toString()));
    }


    /**
     * get a clause to turn a constant in a new date variable 
     * @return the clause that has been made
     */
    private AtomicWhereClause getDateVariableDeclarationClause() {
    	return getVariableDeclarationClause(catalog.getObject(ValueType.Date.toString()));
    }
    
    /**
     * get a clause to create a new persistent object variable
     * @param objectName name of the persistent object
     * @return the clause that has been made
     */
    private AtomicWhereClause getObjectVariableDeclarationClause(DbObject object) {
    	return getVariableDeclarationClause(object);
    }
    
    /**
     * get a clause to create a variable of the given type from the given constant
     * @param object a table type object
     * @return the clause that has been made
     */
    private AtomicWhereClause getVariableDeclarationClause(DbObject object) {
    	SimpleAtomicWhereClause aClause = new SimpleAtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup("New Variable");
        
        OutputVariable ovar = new OutputVariable(object);

        aVisList.addFixedString(new FixedString("There is a"));
        aVisList.addOutputVariable(ovar);
        
        Constant constant = getConstant(object, null);
        
        if (constant != null) {
        	ovar.getExpression().addConstant(constant);
	        aVisList.addFixedString(new FixedString("with value"));
	        aVisList.addConstant(constant);
            aClause.setCompositionBehaviour(new PrimitiveDeclarationComposition());
	    }
        else {
            FromVariable fromVar = new FromVariable(object);
	        ovar.getExpression().addFromVariable(fromVar);
            aClause.addFromVariable(fromVar);
            aClause.setCompositionBehaviour(new NewTableComposition());
        }
        
        aComposer.addFixedString(new FixedString("1=1"));
        
        return aClause;
    }
    
    /**
     * get a clause to compare between a numeric variable and a numeric constant
     * @return the clause that has been made
     */
    private AtomicWhereClause getNumericVariableToConstantComparisonClause() {
    	return getVariableToConstantComparisonClause(catalog.getObject(ValueType.Number.toString()));
    }
    
    /**
     * get a clause to compare between a string variable and a string constant
     * @return the clause that has been made
     */
    private AtomicWhereClause getStringVariableToConstantComparisonClause() {
    	return getVariableToConstantComparisonClause(catalog.getObject(ValueType.String.toString()));
    }
    
    /**
     * get a clause to compare between a boolean variable and a boolean constant
     * @return the clause that has been made
     */
    private AtomicWhereClause getBooleanVariableToConstantComparisonClause() {
    	return getVariableToConstantComparisonClause(catalog.getObject(ValueType.Boolean.toString()));
    }
    
    /**
     * get a clause to compare between a date variable and a date constant
     * @return the clause that has been made
     */
    private AtomicWhereClause getDateVariableToConstantComparisonClause() {
    	return getVariableToConstantComparisonClause(catalog.getObject(ValueType.Date.toString()));
    }
    
    /**
     * get a clause to compare between a variable and an object of the given object type
     * @param object the object
     * @return the clause that has been made
     */
    private AtomicWhereClause getVariableToConstantComparisonClause(DbObject object) {

        AtomicWhereClause aClause = new SimpleAtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup(object.getDescription());

        InputVariable ivar1 = new InputVariable(object);
        Constant operatorConstant = getOperatorConstant(object, false);
        Constant constant = getConstant(object, null);
        
        aVisList.addFixedString(new FixedString(object.getDescription()));
        aVisList.addInputVariable(ivar1);
        aVisList.addConstant(operatorConstant);
        aVisList.addConstant(constant);
        
        addComparisonClauseToComposer(aComposer, ivar1, operatorConstant, constant, isCaseSensitive(object));
        
        return aClause;
    }
    
    /**
     * get a clause to compare between two numbers
     * @return the clause that has been made
     */
    private AtomicWhereClause getNumericVariableToVariableComparisonClause() {
    	return getVariableToVariableComparisonClause(catalog.getObject(ValueType.Number.toString()));
    }
    
    /**
     * get a clause to compare between two strings
     * @return the clause that has been made
     */
    private AtomicWhereClause getStringVariableToVariableComparisonClause() {
    	return getVariableToVariableComparisonClause(catalog.getObject(ValueType.String.toString()));
    }

    /**
     * get a clause to compare between two booleans
     * @return the clause that has been made
     */
    private AtomicWhereClause getBooleanVariableToVariableComparisonClause() {
    	return getVariableToVariableComparisonClause(catalog.getObject(ValueType.Boolean.toString()));
    }

    /**
     * get a clause to compare between two dates
     * @return the clause that has been made
     */
    private AtomicWhereClause getDateVariableToVariableComparisonClause() {
    	return getVariableToVariableComparisonClause(catalog.getObject(ValueType.Date.toString()));
    }

    /**
     * get a clause to compare between two instances of the given persistent object
     * @param object the persistent object
     * @return the clause that has been made
     */
    private AtomicWhereClause getObjectVariableToVariableComparisonClause(DbObject object) {
    	return getVariableToVariableComparisonClause(object);
    }
    
    /**
     * get a clause to compare between two variables of the given object type
     * @param object the object
     * @return the clause that has been made
     */
    private AtomicWhereClause getVariableToVariableComparisonClause(DbObject object) {
    	AtomicWhereClause aClause = new SimpleAtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup(object.getTableObject().getDescription());

        InputVariable ivar1 = new InputVariable(object);
        InputVariable ivar2 = new InputVariable(object);

        Constant constant = getOperatorConstant(object, false);
        aVisList.addFixedString(new FixedString(object.getDescription()));
        aVisList.addInputVariable(ivar1);
        aVisList.addConstant(constant);
        aVisList.addInputVariable(ivar2);
        
        addComparisonClauseToComposer(aComposer, ivar1, constant, ivar2, isCaseSensitive(object));
        
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
     * @return all the clauses that have been made
     */
    private List<AtomicWhereClause> getNumericCalculationClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(getVariableCalculationClause(catalog.getObject(ValueType.Number.toString()), new DoubleConstant(), null, getNumberCalculationOperator(), ""));
    	list.add(getVariableCalculationClause(catalog.getObject(ValueType.Number.toString()), null, catalog.getObject(ValueType.Number.toString()), getNumberCalculationOperator(), ""));
    	return list;
    }
    
    /**
     * add all clause to create a new string variable from a calculation between a two string
     * @return all the clauses that have been made
     */
    private List<AtomicWhereClause> getStringCalculationClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(getVariableCalculationClause(catalog.getObject(ValueType.String.toString()), new StringConstant(), null, getStringCalculationOperator(), ""));
    	list.add(getVariableCalculationClause(catalog.getObject(ValueType.String.toString()), null, catalog.getObject(ValueType.String.toString()), getStringCalculationOperator(), ""));
    	return list;
    }

    /**
     * add all clause to create a new date variable from a calculation between a string and a number of days
     * @return all the clauses that have been made
     */
    private List<AtomicWhereClause> getDateCalculationClauses() {
    	List<AtomicWhereClause> list = new ArrayList<AtomicWhereClause>();
    	list.add(getVariableCalculationClause(catalog.getObject(ValueType.Date.toString()), new DoubleConstant(), null, getDateCalculationOperator(), "days"));
    	list.add(getVariableCalculationClause(catalog.getObject(ValueType.Date.toString()), null, catalog.getObject(ValueType.Number.toString()), getDateCalculationOperator(), "days"));
    	return list;
    }
    
    /**
     * get a clause to create a new variable of the given type from a calculation between a variable of the given type
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
     * @return the clause that has been made
     */    
    private AtomicWhereClause getVariableCalculationClause(DbObject object, Constant calculationConstant, DbObject calculationObject, OperatorConstant calculationOperator, String additionalDescription) {
    	if (calculationConstant != null || calculationObject != null) {
            AtomicWhereClause aClause = new SimpleAtomicWhereClause();
            VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
            WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
            aClause.addGroup("New Variable");

            InputVariable ivar1 = new InputVariable(object);
            InputVariable ivar2 = null;
            
            OutputVariable ovar = new OutputVariable(object.getTableObject());
            ovar.getExpression().addInputVariable(ivar1);
            ovar.getExpression().addFixedString(new FixedString(" "));
            ovar.getExpression().addConstant(calculationOperator);
            ovar.getExpression().addFixedString(new FixedString(" "));
            if (calculationConstant != null) {
                aClause.setCompositionBehaviour(new PrimitiveConstantAdditionComposition());
	            ovar.getExpression().addConstant(calculationConstant);
            }
            else if (calculationObject != null) {
                aClause.setCompositionBehaviour(new PrimitiveInputAdditionComposition());
            	ivar2 =  new InputVariable(calculationObject);
	            ovar.getExpression().addInputVariable(ivar2);
            }
            
            aVisList.addFixedString(new FixedString("There is a"));
            aVisList.addOutputVariable(ovar);
            aVisList.addFixedString(new FixedString("with value"));
            aVisList.addInputVariable(ivar1);
            aVisList.addConstant(calculationOperator);
            if (calculationConstant != null) {
            	aVisList.addConstant(calculationConstant);
            }
            else if (calculationObject != null) {
                aVisList.addInputVariable(ivar2);
            }
            aVisList.addFixedString(new FixedString(additionalDescription));
            
            aComposer.addFixedString(new FixedString("1=1"));
            
            return aClause;
        } else {
            return null;
        }
    }
    
    private AtomicWhereClause getNullClause(DbObject object) {
        AtomicWhereClause aClause = new SimpleAtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup(object.getTableObject().getDescription());
        
        Constant cst = getNullComparisonOperator();
        InputVariable ivar = new InputVariable(object);
        
        aVisList.addFixedString(new FixedString(object.getDescription()));
        aVisList.addInputVariable(ivar);
        aVisList.addConstant(cst);

        aComposer.addInputVariable(ivar);
        aComposer.addFixedString(new FixedString(" "));
        aComposer.addConstant(cst);
        
        return aClause;
    }
    
    private List<AtomicWhereClause> getCollectionSizeClauses(DbObject object, String relationDescription) {
    	List<AtomicWhereClause> result = new ArrayList<AtomicWhereClause>();
    	result.add(getCollectionSizeClause(object, relationDescription, true));
    	result.add(getCollectionSizeClause(object, relationDescription, false));
    	return result;
    }
    
    private AtomicWhereClause getCollectionSizeClause(DbObject object, String relationDescription, boolean fetchAsVariable) {
    	AtomicWhereClause aClause = new SimpleAtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.addGroup(object.getTableObject().getDescription());

        InputVariable ivar1 = new InputVariable(object);
        OutputVariable ovar =  new OutputVariable(object);
        
        ovar.getExpression().addInputVariable(ivar1);
        ovar.getExpression().addFixedString(new FixedString("." + object.getPropertyName() + ".size"));
        ovar.setRelation(relationDescription);
        
        aVisList.addFixedString(new FixedString(object.getTableObject().getDescription()));
        aVisList.addInputVariable(ivar1);
        aVisList.addFixedString(new FixedString(fetchAsVariable ? "has a" : "'s"));
        aVisList.addOutputVariable(ovar);
        if (!fetchAsVariable) {
            aClause.setCompositionBehaviour(new PropertySetComposition());
            Constant constant = getOperatorConstant(object, object.hasDropdown());
            Constant amount = getConstant(object, null);

            aVisList.addConstant(constant);
	        aVisList.addConstant(amount);

	        aComposer.addOutputVariable(ovar);
	        aComposer.addFixedString(new FixedString(" "));
	        aComposer.addConstant(constant);
	        aComposer.addFixedString(new FixedString(" "));
	        aComposer.addConstant(amount);
        }
        else {
        	aClause.setCompositionBehaviour(new PropertyFetchComposition());
        }
        
        return aClause;
    	
    }
    
//    private AtomicWhereClause addCountClause(String variableType) {
//        AtomicWhereClause aClause = new AtomicWhereClause();
//        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
//        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
//        aClause.addGroup("Number");
//        
//        InputVariable ivar1 = new InputVariable(new VariableType(variableType));
//        OutputVariable ovar = new OutputVariable(new VariableType(ValueType.Numeric.toString()), ValueType.Numeric.toString());
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
    
    private AtomicWhereClause getMutationClause(String referencePath, String mutationPath, String description) {
        AtomicWhereClause aClause = new SimpleAtomicWhereClause();
        VisualizationClauseList aVisList = aClause.getVisualizationClauseList();
        WhereClauseComposer aComposer = aClause.getWhereClauseComposer();
        aClause.setCompositionBehaviour(new MutationComposition());

        DbObject input = catalog.getObject("AaSequence");
        aClause.addGroup(input.getDescription());
      
        InputVariable ivar1 = new InputVariable(input);
        Constant constant = new MutationConstant(ivar1, referencePath, mutationPath);

        aVisList.addFixedString(new FixedString(input.getDescription()));
        aVisList.addInputVariable(ivar1);
        aVisList.addFixedString(new FixedString(description));
        aVisList.addConstant(constant);
      
        aComposer.addConstant(constant);

        return aClause;
    }
    
    private AtomicWhereClause getNamedObjectDeclarationClause(DbObject property) {
    	ComposedAtomicWhereClause clause = new ComposedAtomicWhereClause(ExportPolicy.FIRST);
    	VisualizationClauseList aVisList = clause.getVisualizationClauseList();
        clause.setCompositionBehaviour(new NamedTableFetchComposition());
    	
    	// add declaration
    	clause.addChild(getObjectVariableDeclarationClause(property.getTableObject()), VisualisationListPolicy.ALL);
		
    	aVisList.addFixedString(new FixedString("whose " + property.getDescription()));
    	
    	// add property comparison
    	clause.addChild(getPropertyComparisonClause(new ObjectRelation(property.getTableObject()), property, false, null), VisualisationListPolicy.CONSTANTS);
    	
    	return clause;
    }
    
    private AtomicWhereClause getNamedObjectFetchClause(ObjectRelation relation, DbObject property) {
    	ComposedAtomicWhereClause clause = new ComposedAtomicWhereClause(ExportPolicy.FIRST);
    	VisualizationClauseList aVisList = clause.getVisualizationClauseList();
        clause.setCompositionBehaviour(new NamedTablePropertyComposition());
    	
    	// add declaration
    	clause.addChild(getRelationClause(relation), VisualisationListPolicy.ALL);
		
    	aVisList.addFixedString(new FixedString("whose " + property.getDescription()));
    	
    	// add property comparison
    	ObjectRelation r = null;
    	if (!property.getTableName().equals(relation.getForeignTable().getTableName())) {
    		r = catalog.getRelation(relation.getForeignTable().getTableName(), property.getTableName());
    	}
    	else {
    		r = new ObjectRelation(property.getTableObject());
    	}
    	clause.addChild(getPropertyComparisonClause(r, property, false, null), VisualisationListPolicy.CONSTANTS);
    	
    	return clause;
    }
    
    private AtomicWhereClause getNamedDrugClassClause() {
    	ComposedAtomicWhereClause clause = new ComposedAtomicWhereClause(ExportPolicy.LAST);
    	VisualizationClauseList aVisList = clause.getVisualizationClauseList();
        clause.setCompositionBehaviour(new NamedTablePropertyComposition());
        
        DbObject obj = catalog.getObject("DrugClass", "className");
        
        clause.addChild(getGenericDrugResolvedClause(), VisualisationListPolicy.INPUT);
        
        ObjectRelation rel = catalog.getRelation("DrugGeneric", "DrugClass");
        AtomicWhereClause aClause = getRelationClause(rel);
        clause.addChild(aClause, VisualisationListPolicy.NONE);
        OutputVariable ovar = aClause.getOutputVariables().get(0);
        ovar.setRelation("was treated with a drug in a");
        aVisList.addOutputVariable(ovar);
        aVisList.addFixedString(new FixedString("whose " + obj.getDescription()));
        
        clause.addChild(getPropertyComparisonClause(new ObjectRelation(obj.getTableObject()), obj, false, null), VisualisationListPolicy.CONSTANTS);
    	
        return clause;
    }
    
    private AtomicWhereClause getRelationClause(AtomicWhereClause... relations) {
    	ComposedAtomicWhereClause clause = new ComposedAtomicWhereClause(ExportPolicy.LAST);
    	VisualizationClauseList aVisList = clause.getVisualizationClauseList();
        clause.setCompositionBehaviour(new NamedTablePropertyComposition());
        
        boolean first = true;
        for (AtomicWhereClause aClause : relations) {
        	clause.addChild(aClause, (first ? VisualisationListPolicy.INPUT : VisualisationListPolicy.NONE));
        	first = false;
        }
        
        return clause;
    }
    
    /**
     * set all variable names, table descriptions and table aliases
     */
    private void addVariableNames() {
    	// primitive types
    	catalog.addObject(new DbObject(ValueType.Number.toString()));
    	catalog.addObject(new DbObject(ValueType.Boolean.toString()));
    	catalog.addObject(new DbObject(ValueType.Date.toString()));
    	catalog.addObject(new DbObject(ValueType.String.toString()));
    	
    	// attributes
        catalog.addObject(new DbObject("Attribute"));
        catalog.addObject(new DbObject("PatientAttributeValue"));
        catalog.addObject(new DbObject("PatientAttributeValue", "value"));
        catalog.addObject(new DbObject("AttributeNominalValue"));
        catalog.addObject(new DbObject("AttributeNominalValue", "value"));
    	
    	// patients 
        catalog.addObject(new DbObject("PatientImpl", null, "patient", "patient"));
        catalog.addObject(new DbObject("PatientImpl", "patientIi", "index", "index"));
        catalog.addObject(new DbObject("PatientImpl", "patientId", "id", "id"));
        catalog.addObject(new DbObject("PatientImpl", "lastName", "name", "name"));
        catalog.addObject(new DbObject("PatientImpl", "firstName", "surname", "surname"));
        catalog.addObject(new DbObject("PatientImpl", "birthDate", "birth_date", "birth date"));
        catalog.addObject(new DbObject("PatientImpl", "deathDate", "death_date", "death date"));
        catalog.addObject(new DbObject("PatientImpl", "patientDatasets", "dataset_count", "datasets").setValueType(ValueType.Number));
        catalog.addObject(new DbObject("PatientImpl", "testResults", "test_result_count", "test results").setValueType(ValueType.Number));
        catalog.addObject(new DbObject("PatientImpl", "viralIsolates", "viral_isolate_count", "viral isolates").setValueType(ValueType.Number));
        catalog.addObject(new DbObject("PatientImpl", "therapies", "therapy_count", "therapies").setValueType(ValueType.Number));
        
        // therapy
        catalog.addObject(new DbObject("Therapy", null, "therapy", "therapy"));
        catalog.addObject(new DbObject("Therapy", "therapyIi", "index", "index"));
        catalog.addObject(new DbObject("Therapy", "startDate", "start_date", "start date"));
        catalog.addObject(new DbObject("Therapy", "stopDate", "stop_date", "stop date"));
        catalog.addObject(new DbObject("Therapy", "comment", "comment", "comment", true));
        catalog.addObject(new DbObject("Therapy", "therapyCommercials", "commercial_treatment_count", "commercial drugs").setValueType(ValueType.Number));
        catalog.addObject(new DbObject("Therapy", "therapyGenerics", "generic_treatment_count", "generic drugs").setValueType(ValueType.Number));

        // viral isolate
        catalog.addObject(new DbObject("ViralIsolate", null, "viral_isolate", "viral isolate"));
        catalog.addObject(new DbObject("ViralIsolate", "viralIsolateIi", "index", "index"));
        catalog.addObject(new DbObject("ViralIsolate", "sampleDate", "sample_date", "sample date"));
        catalog.addObject(new DbObject("ViralIsolate", "sampleId", "id", "id"));
        catalog.addObject(new DbObject("ViralIsolate", "testResults", "test_result_count", "test results").setValueType(ValueType.Number));
        catalog.addObject(new DbObject("ViralIsolate", "ntSequences", "nt_sequence_count", "nucleotide sequences").setValueType(ValueType.Number));
        
        // nt sequence
        catalog.addObject(new DbObject("NtSequence", null, "nt_sequence", "nucleotide sequence"));
        catalog.addObject(new DbObject("NtSequence", "ntSequenceIi", "index", "index"));
        catalog.addObject(new DbObject("NtSequence", "sequenceDate", "sequence_date", "sequence date"));
        catalog.addObject(new DbObject("NtSequence", "label", "sequence_label", "sequence label"));
        catalog.addObject(new DbObject("NtSequence", "nucleotides", "nucleotides", "nucleotides"));
        catalog.addObject(new DbObject("NtSequence", "testResults", "test_result_count",  "test results").setValueType(ValueType.Number));
        catalog.addObject(new DbObject("NtSequence", "aaSequences", "aa_sequence_count",  "amino acid sequences").setValueType(ValueType.Number));
        
        // protein
        catalog.addObject(new DbObject("Protein", null, "protein", "protein"));
        catalog.addObject(new DbObject("Protein", "proteinIi", "index", "index"));
        catalog.addObject(new DbObject("Protein", "abbreviation", "abbreviation", "abbreviation", true));
        catalog.addObject(new DbObject("Protein", "fullName", "name", "name", true));

        // aa sequence
        catalog.addObject(new DbObject("AaSequence", null, "aa_sequence", "amino acid sequence"));
        catalog.addObject(new DbObject("AaSequence", "aaSequenceIi", "index", "index"));
        catalog.addObject(new DbObject("AaSequence", "firstAaPos", "aa_position", "first amino acid position"));
        catalog.addObject(new DbObject("AaSequence", "lastAaPos", "aa_position", "last amino acid position"));
        catalog.addObject(new DbObject("AaSequence", "aaMutations", "aa_mutation_count", "amino acid mutations").setValueType(ValueType.Number));
        catalog.addObject(new DbObject("AaSequence", "aaInsertions", "aa_insertion_count", "amino acid insertions").setValueType(ValueType.Number));

        // aa mutation
        catalog.addObject(new DbObject("AaMutation", null, "aa_mutation", "amino acid mutation"));
        catalog.addObject(new DbObject("AaMutation", "aaReference", "reference_str", "non-synonymous mutation reference string"));
        catalog.addObject(new DbObject("AaMutation", "aaMutation", "mutation_str", "non-synonymous mutation mutation string"));
        catalog.addObject(new DbObject("AaMutation", "ntReferenceCodon", "reference_str",  "synonymous mutation reference string"));
        catalog.addObject(new DbObject("AaMutation", "ntMutationCodon", "mutation_str",  "synonymous mutation mutation string"));
        catalog.addObject(new DbObject("AaMutation", "id.mutationPosition", "mutation_position",  "mutation position"));
        
        // aa insertion
        catalog.addObject(new DbObject("AaInsertion", null, "aa_insertion", "amino acid insertion"));
        catalog.addObject(new DbObject("AaInsertion", "aaInsertion", "insertion_str", "non-synonymous insertion string"));
        catalog.addObject(new DbObject("AaInsertion", "ntInsertionCodon", "insertion_str", "synonymous insertion string"));
        catalog.addObject(new DbObject("AaInsertion", "id.insertionPosition", "insertion_position", "insertion position"));
        catalog.addObject(new DbObject("AaInsertion", "id.insertionOrder", "insertion_order", "insertion order"));
        
        // drug class
        catalog.addObject(new DbObject("DrugClass", null, "drug_class", "drug class"));
        catalog.addObject(new DbObject("DrugClass", "drugClassIi", "index", "index"));
        catalog.addObject(new DbObject("DrugClass", "classId", "id", "id"));
        catalog.addObject(new DbObject("DrugClass", "className", "class_name", "name", true));
        catalog.addObject(new DbObject("DrugClass", "resistanceTableOrder", "resitance_table_order", "resistance table order"));
        catalog.addObject(new DbObject("DrugClass", "drugGenerics", "generic_drug_count", "generic drugs").setValueType(ValueType.Number));

        // therapy commercial
        catalog.addObject(new DbObject("TherapyCommercial", null, "commercial_treatment", "treatment with a commercial drug"));
        catalog.addObject(new DbObject("TherapyCommercial", "dayDosageUnits", "dosage", "daily dosage", true));
        catalog.addObject(new DbObject("TherapyCommercial", "frequency", "frequency", "administration frequency"));
        catalog.addObject(new DbObject("TherapyCommercial", "placebo", "placebo", "placebo"));
        catalog.addObject(new DbObject("TherapyCommercial", "blind", "plind", "blind"));
        
        // therapy generic
        catalog.addObject(new DbObject("TherapyGeneric", null, "generic_treatment", "treatment with a generic drug"));
        catalog.addObject(new DbObject("TherapyGeneric", "dayDosageMg", "dosage", "daily dosage in mg", true));
        catalog.addObject(new DbObject("TherapyGeneric", "frequency", "frequency", "administration frequency"));
        catalog.addObject(new DbObject("TherapyGeneric", "placebo", "placebo", "placebo"));
        catalog.addObject(new DbObject("TherapyGeneric", "blind", "blind", "blind"));
        
        // events
        catalog.addObject(new DbObject("Event"));
        catalog.addObject(new DbObject("PatientEventValue", null, "event", "event"));
        catalog.addObject(new DbObject("PatientEventValue", "patientEventValueIi", "index", "index"));
        catalog.addObject(new DbObject("PatientEventValue", "startDate", "start_date", "start date"));
        catalog.addObject(new DbObject("PatientEventValue", "endDate", "end_date", "end date"));
        catalog.addObject(new DbObject("PatientEventValue", "value", "event", "event"));
        catalog.addObject(new DbObject("EventNominalValue"));
        catalog.addObject(new DbObject("EventNominalValue", "value", "event", "event"));
        
        // drug generic
        catalog.addObject(new DbObject("DrugGeneric", null, "generic_drug", "generic drug"));
        catalog.addObject(new DbObject("DrugGeneric", "genericIi", "index", "index"));
        catalog.addObject(new DbObject("DrugGeneric", "genericId", "id", "id"));
        catalog.addObject(new DbObject("DrugGeneric", "atcCode", "atc_code", "atc code", true));
        catalog.addObject(new DbObject("DrugGeneric", "resistanceTableOrder", "resistance_table_order", "resistance table order", true));
        catalog.addObject(new DbObject("DrugGeneric", "genericName", "drug_name", "name", true));
        catalog.addObject(new DbObject("DrugGeneric", "drugCommercials", "commercial_drug_count", "commercial drugs").setValueType(ValueType.Number));
        
        // drug commercial
        catalog.addObject(new DbObject("DrugCommercial", null, "commercial_drug", "commercial drug"));
        catalog.addObject(new DbObject("DrugCommercial", "commercialIi", "index", "index"));
        catalog.addObject(new DbObject("DrugCommercial", "name", "drug_name", "name", true));
        catalog.addObject(new DbObject("DrugCommercial", "atcCode", "atc_code", "atc code", true));
        catalog.addObject(new DbObject("DrugCommercial", "drugGenerics", "generic_drug_count", "generic drugs").setValueType(ValueType.Number));
        
        // dataset
        catalog.addObject(new DbObject("PatientDataset"));
        catalog.addObject(new DbObject("Dataset", null, "dataset", "dataset"));
        catalog.addObject(new DbObject("Dataset", "description", "dataset_Name", "dataset name", true));
        
        // test result
        catalog.addObject(new DbObject("TestResult", null, "testResult", "test result"));
        catalog.addObject(new DbObject("TestResult", "testResultIi", "index", "index"));
        catalog.addObject(new DbObject("TestResult", "data", "data", "data"));
        catalog.addObject(new DbObject("TestResult", "sampleId", "sample_id", "id"));
        catalog.addObject(new DbObject("TestResult", "testDate", "test_date", "test date"));
        catalog.addObject(new DbObject("TestResult", "value", "test_result", "test result"));
        catalog.addObject(new DbObject("TestNominalValue"));
        catalog.addObject(new DbObject("TestNominalValue", "value", "test_result", "test result"));
        
        // test
        catalog.addObject(new DbObject("Test", null, "test", "test"));
        catalog.addObject(new DbObject("Test", "testIi", "index", "index"));
        catalog.addObject(new DbObject("Test", "description", "test_name", "test name", true));
        
        // test type
        catalog.addObject(new DbObject("TestType", null, "test_type", "test type"));
        catalog.addObject(new DbObject("TestType", "testTypeIi", "index", "index"));
        catalog.addObject(new DbObject("TestType", "description", "type_name", "name", true));
        
        // test object
        catalog.addObject(new DbObject("TestObject", null, "test_object", "test object"));
        catalog.addObject(new DbObject("TestObject", "testObjectIi", "index", "index"));
        catalog.addObject(new DbObject("TestObject", "testObjectId", "id", "id"));
        catalog.addObject(new DbObject("TestObject", "description", "object_name", "test object", true));

        // therapy motivation
        catalog.addObject(new DbObject("TherapyMotivation", null,"motivation" , "motivation"));
        catalog.addObject(new DbObject("TherapyMotivation", "therapyMotivationIi", "index", "index"));
        catalog.addObject(new DbObject("TherapyMotivation", "value", "Motivation", "motivation", true));
        
        addRelations("PatientEventValue", "patient", "PatientImpl", null, false, "comes from",  "has an");
        addRelations("Therapy", "patient", "PatientImpl", null, false, "was performed on a",  "has received");        
        addRelations("ViralIsolate", "patient", "PatientImpl",  null, false, "comes from a ",  "has a");
        addRelation("PatientImpl", "id.patient", "Dataset", "id.dataset",  "PatientDataset", true, null);        
        
        addRelation("Therapy", "therapyMotivation", "TherapyMotivation", null, "TherapyMotivation", false, null);
        addRelations("DrugGeneric", "id.drugGeneric", "Therapy", "id.therapy" , "TherapyGeneric", true, "is one of the drugs used in a", "'s medication contains a");       
        addRelations("DrugCommercial", "id.drugCommercial", "Therapy", "id.therapy" , "TherapyCommercial", true, "is one of the drugs used in a",  "'s medication contains a");      
        
        addRelations("DrugCommercial", null, "TherapyCommercial", "id.drugCommercial", false, "is used in a",  "consists of the");
        addRelations("DrugGeneric", null, "TherapyGeneric", "id.drugGeneric", false, "is used in a",  "consists of the");      
        addRelations("TherapyCommercial", "id.therapy", "Therapy", null , false, "is a part of",  "has a");       
        addRelations("TherapyGeneric", "id.therapy", "Therapy", null , false, "is a part of",  "has a");

        addRelations("NtSequence", "viralIsolate", "ViralIsolate", null, false, "comes from a",  "has a" );       
        addRelations("NtSequence", "viralIsolate.patient", "PatientImpl", null, false, "comes from a",  "has a");
        addRelations("AaSequence", "ntSequence", "NtSequence", null, false, "comes from a",  "has an"   );     
        addRelations("AaSequence", "ntSequence.viralIsolate.patient", "PatientImpl", null, false, "comes from a",  "has an");
        addRelations("AaSequence", "ntSequence.viralIsolate", "ViralIsolate", null, false, "comes from a",  "has an");
        addRelations("AaMutation", "id.aaSequence", "AaSequence", null, false, "comes from an",  "has an");
        addRelations("AaInsertion", "id.aaSequence", "AaSequence", null, false, "comes from an",  "has an");
        addRelations("Protein", null, "AaSequence", "protein", false, "has an encoding",  "encodes for a");   

        addRelations("TestResult", "test", "Test", null, false, null, null);
        addRelations("TestResult", "test.testType.testObject", "TestObject", null, false, null, null);
        addRelations("PatientImpl", null, "TestResult", "patient",  false, "has a",  "is a result from a");
        addRelations("DrugGeneric", null, "TestResult", "drugGeneric",  false, "has a",  "is a result from a");
        addRelations("ViralIsolate", null, "TestResult", "viralIsolate",  false, "has a",  "is a result from a");
        addRelations("NtSequence", null, "TestResult", "ntSequence",  false, "has a",  "is a result from a");
//        addRelations("AaMutation", "id.aaSequence.ntSequence.viralIsolate.patient", "PatientImpl", null,  false, "comes from a",  "has a");
//        addRelations("AaInsertion", "id.aaSequence.ntSequence.viralIsolate.patient", "PatientImpl", null, false, "comes from a",  "has a");
        addRelations("DrugClass", null, "DrugGeneric", "drugClass", false, "has a",  "belongs to the");
        
        
    }
    
    private ObjectRelation getRelation(String inputTable, String iTo, String foreignTable, String fTo, String idTable, boolean invert, String description) {
    	return new ObjectRelation(catalog.getObject(inputTable), iTo, catalog.getObject(foreignTable), fTo, catalog.getObject(idTable, null), invert, description);
    }
    
    private ObjectRelation getRelation(String inputTable, String iTo, String foreignTable, String fTo, boolean invert, String description) {
    	return getRelation(inputTable, iTo, foreignTable, fTo, (iTo == null ? foreignTable: inputTable), invert, description);
    }
    
    private void addRelation(String inputTable, String iTo, String foreignTable, String fTo, String idTable, boolean invert, String description) {
    	catalog.addRelation(getRelation(inputTable, iTo, foreignTable, fTo, idTable, invert, description));
    }
    
    private void addRelations(String inputTable, String iTo, String foreignTable, String fTo, String idTable, boolean invert, String description1, String description2) {
    	addRelation(inputTable, iTo, foreignTable, fTo, idTable, invert, description1);
    	addRelation(foreignTable, fTo, inputTable, iTo, idTable, invert, description2);
    }
    
    private void addRelations(String inputTable, String iTo, String foreignTable, String fTo, boolean invert, String description1, String description2) {
    	catalog.addRelation(getRelation(inputTable, iTo, foreignTable, fTo, invert, description1));
    	catalog.addRelation(getRelation(foreignTable, fTo, inputTable, iTo, invert, description2));
    }
    
    private void addAllTableClauses() throws SQLException {
    	catalog.addAll(getObjectClauses(catalog.getObject("PatientImpl", "lastName")));
    	catalog.addAll(getObjectClauses(catalog.getObject("Therapy")));
    	catalog.addAll(getObjectClauses(catalog.getObject("TherapyGeneric")));
    	catalog.addAll(getObjectClauses(catalog.getObject("TherapyCommercial")));
    	catalog.addAll(getObjectClauses(catalog.getObject("DrugGeneric", "genericName")));
    	catalog.addAll(getObjectClauses(catalog.getObject("DrugCommercial", "name")));
    	catalog.addAll(getObjectClauses(catalog.getObject("DrugClass", "className")));
    	catalog.addAll(getObjectClauses(catalog.getObject("ViralIsolate")));
    	catalog.addAll(getObjectClauses(catalog.getObject("NtSequence")));
    	catalog.addAll(getObjectClauses(catalog.getObject("AaSequence")));
    	catalog.addAll(getObjectClauses(catalog.getObject("AaMutation")));
    	catalog.addAll(getObjectClauses(catalog.getObject("AaInsertion")));
    	catalog.addAll(getObjectClauses(catalog.getObject("Protein", "fullName")));
    	catalog.addAll(getObjectClauses(catalog.getObject("TestResult")));
    	catalog.addAll(getObjectClauses(catalog.getObject("PatientEventValue")));
    	
        ///////////////////////////////////////
        // events
    	catalog.addAll(getPropertyComparisonClauses("PatientEventValue", "startDate"));
        catalog.addAll(getPropertyComparisonClauses("PatientEventValue", "endDate"));
        
    	QueryResult result = DatabaseManager.getInstance().getDatabaseConnector().executeQuery("from net.sf.regadb.db.Event");
    	for (int i = 0 ; i < result.size() ; i++) {
    		Event event = (Event) result.get(i, 0);
    		catalog.addAll(getCustomPropertyComparisonClauses(event.getName(), event.getValueType().getDescription(), catalog.getObject("Event"), catalog.getObject("EventNominalValue"), catalog.getObject("PatientEventValue"), "event", "eventNominalValue", "event", catalog.getObject("PatientEventValue"), null, "name"));
    	}
        
        catalog.addAll(getRelationClauses("PatientEventValue", "PatientImpl"));
        catalog.addAll(getRelationClauses("Therapy", "PatientImpl"));
        catalog.addAll(getRelationClauses("ViralIsolate", "PatientImpl"));
        
        
        ///////////////////////////////////////
        // patients
        catalog.addAll(getPropertyComparisonClauses("PatientImpl", "patientId"));
        catalog.addAll(getPropertyComparisonClauses("PatientImpl", "lastName"));
   		catalog.addAll(getPropertyComparisonClauses("PatientImpl", "firstName"));
   		catalog.addAll(getPropertyComparisonClauses("PatientImpl", "birthDate"));
   		catalog.addAll(getPropertyComparisonClauses("PatientImpl", "deathDate"));
        catalog.addAll(getPropertyComparisonClauses(catalog.getRelation("PatientImpl", "Dataset"), catalog.getObject("Dataset", "description")));

        // patient custom attributes
    	result = DatabaseManager.getInstance().getDatabaseConnector().executeQuery("from Attribute");
    	for (int i = 0 ; i < result.size() ; i++) {
    		Attribute attribute = (Attribute) result.get(i, 0);
    		catalog.addAll(getCustomPropertyComparisonClauses(attribute.getName(), attribute.getValueType().getDescription(), catalog.getObject("Attribute"), catalog.getObject("AttributeNominalValue"), catalog.getObject("PatientAttributeValue"), "attribute", "attributeNominalValue", "attribute", catalog.getObject("PatientImpl"), "patient", "name"));
    	}

        catalog.addAll(getCollectionSizeClauses(catalog.getObject("PatientImpl", "patientDatasets"), "number of"));
        catalog.addAll(getCollectionSizeClauses(catalog.getObject("PatientImpl", "therapies"), "number of"));
        catalog.addAll(getCollectionSizeClauses(catalog.getObject("PatientImpl", "testResults"), "number of"));
        catalog.addAll(getCollectionSizeClauses(catalog.getObject("PatientImpl", "viralIsolates"), "number of"));
        
        

        ///////////////////////////////////////
        // therapies
        catalog.add(getNamedDrugClassClause());
        catalog.add(getNamedObjectFetchClause(catalog.getRelation("Therapy", "DrugGeneric"), catalog.getObject("DrugGeneric", "genericName")) );       
        catalog.add(getNamedObjectFetchClause(catalog.getRelation("Therapy", "DrugCommercial"), catalog.getObject("DrugCommercial", "name")) );       
        catalog.add(getNamedObjectFetchClause(catalog.getRelation("Therapy", "TherapyGeneric"), catalog.getObject("DrugGeneric", "genericName")) );       
        catalog.add(getNamedObjectFetchClause(catalog.getRelation("Therapy", "TherapyCommercial"), catalog.getObject("DrugCommercial", "name")) );       

        catalog.addAll(getPropertyComparisonClauses("Therapy", "startDate"));
        catalog.addAll(getPropertyComparisonClauses("Therapy", "stopDate"));
        catalog.addAll(getPropertyComparisonClauses("Therapy", "comment"));
   		catalog.addAll(getPropertyComparisonClauses(catalog.getRelation("Therapy", "TherapyMotivation"), catalog.getObject("TherapyMotivation","value")));

   		catalog.add(getGenericDrugResolvedClause());   		
        catalog.addAll(getRelationClauses("DrugGeneric", "Therapy"));
        catalog.addAll(getRelationClauses("DrugCommercial", "Therapy"));
        catalog.addAll(getRelationClauses("TherapyCommercial", "Therapy"));
        catalog.addAll(getRelationClauses("TherapyGeneric", "Therapy"));

   		catalog.addAll(getCollectionSizeClauses(catalog.getObject("Therapy", "therapyGenerics"), "number of"));
   		catalog.addAll(getCollectionSizeClauses(catalog.getObject("Therapy", "therapyCommercials"), "number of"));
        
        
        ///////////////////////////////////////
        // therapyCommercial
        catalog.addAll(getPropertyComparisonClauses("TherapyCommercial", "dayDosageUnits"));
        catalog.addAll(getPropertyComparisonClauses("TherapyCommercial", "frequency"));
        catalog.addAll(getPropertyComparisonClauses("TherapyCommercial", "placebo"));
   		catalog.addAll(getPropertyComparisonClauses("TherapyCommercial", "blind"));
   		catalog.addAll(getPropertyComparisonClauses(catalog.getRelation("TherapyCommercial", "DrugCommercial"), catalog.getObject("DrugCommercial", "name")));
   		catalog.addAll(getPropertyComparisonClauses(catalog.getRelation("TherapyCommercial", "DrugCommercial"), catalog.getObject("DrugCommercial", "atcCode")));
        
   		catalog.addAll(getRelationClauses("DrugCommercial", "TherapyCommercial"));
        
        
        ///////////////////////////////////////
        // therapyGeneric
        catalog.addAll(getPropertyComparisonClauses("TherapyGeneric", "dayDosageMg"));
        catalog.addAll(getPropertyComparisonClauses("TherapyGeneric", "frequency"));
        catalog.addAll(getPropertyComparisonClauses("TherapyGeneric", "placebo"));
   		catalog.addAll(getPropertyComparisonClauses("TherapyGeneric", "blind"));
   		catalog.addAll(getPropertyComparisonClauses(catalog.getRelation("TherapyGeneric", "DrugGeneric"), catalog.getObject("DrugGeneric", "genericId")));
   		catalog.addAll(getPropertyComparisonClauses(catalog.getRelation("TherapyGeneric", "DrugGeneric"), catalog.getObject("DrugGeneric", "genericName")));
   		catalog.addAll(getPropertyComparisonClauses(catalog.getRelation("TherapyGeneric", "DrugGeneric"), catalog.getObject("DrugGeneric", "resistanceTableOrder")));
   		catalog.addAll(getPropertyComparisonClauses(catalog.getRelation("TherapyGeneric", "DrugGeneric"), catalog.getObject("DrugGeneric", "atcCode")));
   		
        
   		catalog.addAll(getRelationClauses("DrugGeneric", "TherapyGeneric"));

        
        ///////////////////////////////////////
        // viral isolates
        catalog.addAll(getPropertyComparisonClauses("ViralIsolate", "sampleId"));
        catalog.addAll(getPropertyComparisonClauses("ViralIsolate", "sampleDate"));
        catalog.addAll(getCollectionSizeClauses(catalog.getObject("ViralIsolate", "ntSequences"), "number of"));
        catalog.addAll(getCollectionSizeClauses(catalog.getObject("ViralIsolate", "testResults"), "number of"));
        
        catalog.addAll(getRelationClauses("NtSequence", "ViralIsolate"));
 
        
        ///////////////////////////////////////
        // nucleotide sequence
        catalog.addAll(getPropertyComparisonClauses("NtSequence", "sequenceDate"));
        catalog.addAll(getPropertyComparisonClauses("NtSequence", "label"));
        catalog.addAll(getPropertyComparisonClauses("NtSequence", "nucleotides"));
        catalog.addAll(getCollectionSizeClauses(catalog.getObject("NtSequence", "aaSequences"), "number of"));
        catalog.addAll(getCollectionSizeClauses(catalog.getObject("NtSequence", "testResults"), "number of"));
        
        catalog.addAll(getRelationClauses("PatientImpl", "NtSequence"));
        catalog.addAll(getRelationClauses("AaSequence", "NtSequence"));

        
        ///////////////////////////////////////
        // amino acid sequence
        catalog.add(getNamedObjectFetchClause(catalog.getRelation("AaSequence", "Protein"), catalog.getObject("Protein", "fullName")));
        catalog.add(getNamedObjectFetchClause(catalog.getRelation("AaSequence", "PatientImpl"), catalog.getObject("PatientImpl", "lastName")));
        catalog.addAll(getPropertyComparisonClauses("AaSequence", "firstAaPos"));
        catalog.addAll(getPropertyComparisonClauses("AaSequence", "lastAaPos"));
        catalog.add(getMutationClause("ntReferenceCodon", "ntMutationCodon", "has synonymous mutations"));
        catalog.add(getMutationClause("aaReference", "aaMutation", "has non-synonymous mutations"));
        catalog.addAll(getCollectionSizeClauses(catalog.getObject("AaSequence", "aaMutations"), "number of"));
        catalog.addAll(getCollectionSizeClauses(catalog.getObject("AaSequence", "aaInsertions"), "number of"));
        
        catalog.addAll(getRelationClauses("AaSequence", "PatientImpl"));
        catalog.addAll(getRelationClauses("AaSequence", "ViralIsolate"));
        catalog.addAll(getRelationClauses("AaMutation", "AaSequence"));
        catalog.addAll(getRelationClauses("AaInsertion", "AaSequence"));
        catalog.addAll(getRelationClauses("AaSequence", "Protein"));


        ///////////////////////////////////////
        // protein
        catalog.addAll(getPropertyComparisonClauses("Protein", "abbreviation"));
        catalog.addAll(getPropertyComparisonClauses("Protein", "fullName"));

        
        
        ///////////////////////////////////////
        // amino acid mutation
        catalog.addAll(getPropertyComparisonClauses("AaMutation", "aaReference"));
        catalog.addAll(getPropertyComparisonClauses("AaMutation", "aaMutation"));
        catalog.addAll(getPropertyComparisonClauses("AaMutation", "ntReferenceCodon"));
        catalog.addAll(getPropertyComparisonClauses("AaMutation", "ntMutationCodon"));
        catalog.addAll(getPropertyComparisonClauses("AaMutation", "id.mutationPosition"));
        
        
        ///////////////////////////////////////
        // amino acid insertion
        catalog.addAll(getPropertyComparisonClauses("AaInsertion", "aaInsertion"));
        catalog.addAll(getPropertyComparisonClauses("AaInsertion", "ntInsertionCodon"));
        catalog.addAll(getPropertyComparisonClauses("AaInsertion", "id.insertionPosition"));
        catalog.addAll(getPropertyComparisonClauses("AaInsertion", "id.insertionOrder"));

        
        ///////////////////////////////////////
        // generic drugs
        catalog.addAll(getPropertyComparisonClauses("DrugGeneric", "genericId"));
   		catalog.addAll(getPropertyComparisonClauses("DrugGeneric", "genericName"));
   		catalog.addAll(getPropertyComparisonClauses("DrugGeneric", "atcCode"));
   		catalog.addAll(getPropertyComparisonClauses("DrugGeneric", "resistanceTableOrder"));
   		catalog.addAll(getCollectionSizeClauses(catalog.getObject("DrugGeneric", "drugCommercials"), "number of"));
   		catalog.addAll(getRelationClauses("DrugClass", "DrugGeneric"));
   		
        
        ///////////////////////////////////////
        // commercial drug
        catalog.addAll(getPropertyComparisonClauses("DrugCommercial", "name"));
   		catalog.addAll(getPropertyComparisonClauses("DrugCommercial", "atcCode"));
   		catalog.addAll(getCollectionSizeClauses(catalog.getObject("DrugCommercial", "drugGenerics"), "number of"));
   		catalog.add(getCollectionRelationClause(catalog.getObject("DrugGeneric"), catalog.getObject("DrugCommercial", "drugGenerics"), "is a component of a"));
   		catalog.add(getCollectionRelationClause(catalog.getObject("DrugCommercial"), catalog.getObject("DrugGeneric", "drugCommercials"), "has a component"));
   		
   		
        
   		///////////////////////////////////////
        // drug class
   		catalog.addAll(getPropertyComparisonClauses("DrugClass", "className"));
   		catalog.addAll(getPropertyComparisonClauses("DrugClass", "classId"));
   		catalog.addAll(getPropertyComparisonClauses("DrugClass", "resistanceTableOrder"));
   		catalog.addAll(getCollectionSizeClauses(catalog.getObject("DrugClass", "drugGenerics"), "number of"));
   		
   		
   		
        ///////////////////////////////////////
        // test result
   		catalog.addAll(getPropertyComparisonClauses("TestResult", "sampleId"));
   		catalog.addAll(getPropertyComparisonClauses("TestResult", "testDate"));
   		catalog.addAll(getPropertyComparisonClauses(catalog.getRelation("TestResult", "Test"), catalog.getObject("Test","description")));
   		catalog.addAll(getPropertyComparisonClauses(catalog.getRelation("TestResult", "TestObject"), catalog.getObject("TestObject","description")));
   		catalog.addAll(getRelationClauses("PatientImpl", "TestResult"));
        catalog.addAll(getRelationClauses("DrugGeneric","TestResult"));
        catalog.addAll(getRelationClauses("ViralIsolate", "TestResult"));
        catalog.addAll(getRelationClauses("NtSequence", "TestResult"));
        
        // test result value
    	result = DatabaseManager.getInstance().getDatabaseConnector().executeQuery("from TestType");
    	for (int i = 0 ; i < result.size() ; i++) {
    		TestType type = (TestType) result.get(i, 0);
    		catalog.addAll(getCustomPropertyComparisonClauses(type.getDescription(), type.getValueType().getDescription(), catalog.getObject("TestType"), catalog.getObject("TestNominalValue"), catalog.getObject("TestResult"), "test.testType", "testNominalValue", "testType", catalog.getObject("TestResult"), null, "description"));
    	}
    	
    }
}