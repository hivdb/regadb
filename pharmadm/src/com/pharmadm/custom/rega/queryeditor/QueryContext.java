package com.pharmadm.custom.rega.queryeditor;


public interface QueryContext {
	public QueryEditorComponent getEditorModel();
	public WhereClause getContextClause();
	public void setContextClause(WhereClause clause);
}
