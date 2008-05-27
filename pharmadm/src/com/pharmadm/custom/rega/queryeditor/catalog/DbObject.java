package com.pharmadm.custom.rega.queryeditor.catalog;

public class DbObject {

	private String objectName;
	private String variableName;
	private String sqlAlias;
	private String description;
	private String relation;
	private String verb;
	
	public DbObject(String objectName) {
		this(objectName, null, null, null);
	}
	
	public DbObject(String objectName, String variableName, String sqlAlias, String description) {
		setObjectName(objectName);
		setVariableName(variableName);
		setSqlAlias(sqlAlias);
		setDescription(description);
		setRelation(relation);
		setVerb(verb);
	}
	
	public void setRelation(String relation) {
		if (relation == null) {
			relation = "";
		}
		this.relation = relation;
	}

	public String getRelation() {
		return relation;
	}

	public void setVerb(String verb) {
		if (verb == null) {
			verb = "";
		}
		this.verb = verb;
	}

	public String getVerb() {
		return verb;
	}

	public void setDescription(String description) {
		if (description == null) {
			description = getObjectName().substring(getObjectName().lastIndexOf('.')+1);	
		}
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setSqlAlias(String sqlAlias) {
		if (sqlAlias == null) {
			sqlAlias = getVariableName().toLowerCase();
		}
		this.sqlAlias = sqlAlias;
	}

	public String getSqlAlias() {
		return sqlAlias;
	}

	public void setVariableName(String variableName) {
		if (variableName == null) {
			variableName = getObjectName().substring(getObjectName().lastIndexOf('.')+1);			
		}
		this.variableName = variableName;
	}

	public String getVariableName() {
		return variableName;
	}

	private void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectName() {
		return objectName;
	}
	
	
}
