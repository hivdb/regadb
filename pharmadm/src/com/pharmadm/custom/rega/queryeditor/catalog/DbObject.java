package com.pharmadm.custom.rega.queryeditor.catalog;

import com.pharmadm.custom.rega.queryeditor.Field;
import com.pharmadm.custom.rega.queryeditor.Table;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;



public class DbObject {

	private String propertyName;
	private String tableName;
	private String variableName;
	private String sqlAlias;
	private String description;
	private boolean dropdown;
	private ValueType type;
	
	/**
	 * table with default variable names and description
	 * @param tableName
	 */
	public DbObject(String tableName) {
		this(tableName, null, null, null, null, false, null);
	}
	
	/**
	 * property with default variable names and description
	 * @param tableName
	 * @param propertyName
	 */
	public DbObject(String tableName, String propertyName) {
		this(tableName, propertyName, null, null, null, false, null);
	}

	/**
	 * property with default variable names and description
	 * and a possible dropdown
	 * @param tableName
	 * @param propertyName
	 * @param dropdown
	 */
	public DbObject(String tableName, String propertyName, boolean dropdown) {
		this(tableName, propertyName, null, null, null, dropdown, null);
	}	
	
	/**
	 * property with given variable names and description
	 * @param tableName
	 * @param propertyName
	 */
	public DbObject(String tableName, String propertyName, String variableName, String description) {
		this(tableName, propertyName, variableName, null, description, false, null);
	}	
	 
	/**
	 * property with given variable names and description
	 * and a possible dropdown
	 * @param tableName
	 * @param propertyName
	 * @param dropdown
	 */	public DbObject(String tableName, String propertyName, String variableName, String description, boolean dropdown) {
		this(tableName, propertyName, variableName, null, description, dropdown, null);
	}
	
	public DbObject(String tableName, String propertyName, String variableName, String sqlAlias, String description, boolean dropdown, ValueType type) {
		setTableName(tableName);
		setPropertyName(propertyName);
		setVariableName(variableName);
		setSqlAlias(sqlAlias);
		setDescription(description);
		setDropdown(dropdown);
		setValueType(type);
	}
	
    /**
     * sets whether or not this object requires a dropdown
     * @param dropdown
     */
	private void setDropdown(boolean dropdown) {
		this.dropdown = dropdown;
	}
	
	public boolean hasDropdown() {
		return dropdown;
	}
	
	/**
	 * sets the description of this object
	 * if no description is provided the description will be set to
	 * the table name for tables and primitives, and the property name for fields
	 * @param description
	 */
	public void setDescription(String description) {
		if (description == null) {
			description = (getPropertyName() == null ? getTableName() : getPropertyName());
		}
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * sets the sql alias of this object
	 * if no sql alias is provided the sql alias will be set to the
	 * variable name 
	 * @pre sqlAlias must be a valid sql alias or null
	 * @param sqlAlias
	 */
	public void setSqlAlias(String sqlAlias) {
		if (sqlAlias == null) {
			sqlAlias = getVariableName().toLowerCase();
		}
		this.sqlAlias = sqlAlias;
	}

	public String getSqlAlias() {
		return sqlAlias;
	}

	/**
	 * sets the variable name of this object
	 * if no variable name is provided the variable name will be set to
	 * the table name for tables and primitives, and the property name for fields
	 * variable names will have spaces converted to underscore
	 * @param variableName
	 */
	public void setVariableName(String variableName) {
		if (variableName == null) {
			variableName = (getPropertyName() == null ? getTableName() : getPropertyName());
		}
		variableName.replace(' ', '_');
		this.variableName = variableName;
	}

	public String getVariableName() {
		return variableName;
	}

	/**
	 * return only the table name of this object
	 * @return
	 */
	public String getTableName() {
		 return tableName;
	}

	/**
	 * sets a new table name
	 * @param tableName
	 */
	private void setTableName(String tableName) {
		this.tableName = tableName;
		if (!isvalidTableName() && !isPrimitive()) {
			System.err.println("Error: Table " + getTableName() + " does not exist!");
		}
	}
	
	/**
	 * return true if this object has a valid table name
	 * @return
	 */
	private boolean isvalidTableName() {
		return (!isPrimitive() &&
				DatabaseManager.getInstance().getTableCatalog().hasTable(getTableName()));
	}
	
	/**
	 * return only the property name of this object
	 * @return
	 */
	public String getPropertyName() {
		return propertyName;
	}	

	/**
	 * sets the property name of this object
	 * @param propertyName
	 */
	private void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
		if (propertyName != null && !isField()) {
			System.err.println("Error: property " + getPropertyName() + " of table " + getTableName() + " does not exist!");
		}
	}
	
	/**
	 * returns the object of the table of this object if this object is a field
	 * returns this object otherwise\
	 * @pre isTable() || isField() || isPrimitive must be true
	 * @pre the table object must be in the catalog
	 * @return
	 */
	public DbObject getTableObject() {
		if (propertyName == null) {
			return this;
		}
		else {
			return DatabaseManager.getInstance().getAWCCatalog().getObject(getTableName());
		}
	}
	
	/**
	 * return the table of this object
	 * @pre isField() || isTable() must be true
	 * @return
	 */
	public Table getTable() {
		return DatabaseManager.getInstance().getTableCatalog().getTable(getTableName());
	}
	
	/**
	 * returns the field of this object
	 * @pre isField() must be true
	 * @return
	 */
	public Field getField() {
		return getTable().getField(propertyName);
	}	
	
	/**
	 * return true if this object is a valid field of a table
	 * @return
	 */
	public boolean isField() {
		return (getPropertyName() != null &&
				isvalidTableName() &&
				DatabaseManager.getInstance().getTableCatalog().getTable(getTableName()).hasField(propertyName) );
	}
	
	/**
	 * return true if this object is a valid table
	 * @return
	 */
	public boolean isTable() {
		return (getPropertyName() == null &&
				isvalidTableName());
	}	
	
	/**
	 * return true if this object is a primitive value
	 * @return
	 */
	public boolean isPrimitive() {
		boolean primitive;
		try {
			ValueType.valueOf(tableName);
			primitive = true;
		}
    	catch (IllegalArgumentException e) {
    		primitive = false;
    	}
    	return primitive;
	}	
	
	/**
	 * returns true if the variable type of
	 * this object and the given object are
	 * compatible
	 * @param object
	 * @return
	 */
	public boolean isCompatible(DbObject object) {
		return object.getVariableTypeString().equals(getVariableTypeString());
	}	
	
	/**
	 * returns the variable type of this object
	 * @pre isTable() || isField() || isPrimitive
	 * @return
	 */	private String getVariableTypeString() {
		if (isTable()) {
			return getTableName();
		}
		else {
			return getValueType().toString();
		}
		
	}
	
	/**
	 * sets a custom valueType for this object
	 */
	public DbObject setValueType(ValueType t) {
		this.type = t;
		return this;
	}

	/**
	 * returns the value type of this object
	 * @pre isField() || isPrimitive() || this object has a custom set value type
	 * @return
	 */
    public ValueType getValueType() {
    	ValueType valueType = null;
    	if (type != null) {
    		valueType = type;
    	}
    	else if (isField()) {
	    	int dataType= getField().getDataType();
		    	
	        if (AWCPrototypeCatalog.isStringType(dataType)) {
	        	valueType = ValueType.String;
	        }
	        else if (AWCPrototypeCatalog.isDateType(dataType)) {
	        	valueType = ValueType.Date;
	        }
	        else if (AWCPrototypeCatalog.isNumericType(dataType)) {
	        	valueType = ValueType.Number;
	        }
	        else if (AWCPrototypeCatalog.isBooleanType(dataType)) {
	        	valueType = ValueType.Boolean;
	        }
	        else {
	            System.err.println("Unknown data type found for " + getTableName() + "." + getPropertyName() + ": " + dataType);
	        }
    	}
    	else if (isPrimitive()) {
    		valueType = ValueType.valueOf(getTableName());
    	}
    	return valueType;
    }
    
    
    public enum ValueType  {
    	String,
    	Number,
    	Date,
    	Boolean
    }
    
    /**
     * return the Class associated with this VariableType
     */
    public Class getValueTypeClass() {
    	String name = getVariableTypeString();

        if (name.equalsIgnoreCase(ValueType.Date.toString())) {
            return java.util.Date.class;
        } 
        else if (name.equalsIgnoreCase(ValueType.String.toString())) {
            return java.lang.String.class;
        }
        else if (name.equalsIgnoreCase(ValueType.Boolean.toString())) {
            return java.lang.Boolean.class;
        } 
        else if (name.equalsIgnoreCase(ValueType.Number.toString())) {
            return java.lang.Number.class;
        }
        else {
            try {
                return Class.forName(name);
            } catch (Exception e) {
                return null;
            }
        }
    }    
}


