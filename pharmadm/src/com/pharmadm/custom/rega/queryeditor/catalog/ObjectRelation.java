package com.pharmadm.custom.rega.queryeditor.catalog;

public class ObjectRelation {
	private DbObject inputTable;
	private DbObject foreignTable;
	private DbObject idTable;
	private String inputTableToIdTable;
	private String foreignTableToIdTable;
	private String idTableKey;
	private boolean invertLink;
	private String description;

	public ObjectRelation(DbObject inputTable) {
		this(inputTable, null, inputTable, null, inputTable, false, null);
	}
	
	/**
	 * @param inputTableName table to start from
	 * @param inputTableToIdTable path from the input table to the id table 
	 *                            null if they are the same table
	 * @param foreignTableName name of the table that has the property
	 * @param foreignTableToIdTable path from the foreign table to the id table
	 *                              null if they are the same table
	 * @param idTableName table that acts as key between the input table and the foreign table
	 * @param invertLink false for a relation from input and foreigntable to idtable
	 *                   true for a relation from idtable to input and foreigntable
	 */
	public ObjectRelation(DbObject inputTable, String inputTableToIdTable,
			DbObject foreignTable, String foreignTableToIdTable,
			DbObject idTable, boolean invertLink, String description) {
		this.inputTable = inputTable;
		this.inputTableToIdTable = inputTableToIdTable;
		this.foreignTable = foreignTable;
		this.foreignTableToIdTable = foreignTableToIdTable;
		this.idTable = idTable;
		this.invertLink = invertLink;
		setDescription(description);
	}

	public DbObject getInputTable() {
		return inputTable;
	}

	public String getInputTableToIdTable() {
		return inputTableToIdTable;
	}

	public DbObject getForeignTable() {
		return foreignTable;
	}

	public String getForeignTableToIdTable() {
		return foreignTableToIdTable;
	}

	public DbObject getIdTable() {
		return idTable;
	}

	public String getIdTableKey() {
		return idTableKey;
	}

	public boolean isInvertLink() {
		return invertLink;
	}
	
	public boolean isRelation() {
		return !inputTable.equals(foreignTable);
	}

	private void setDescription(String description) {
		if (description == null) description = "";
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}