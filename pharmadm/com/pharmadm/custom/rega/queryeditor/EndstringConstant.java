package com.pharmadm.custom.rega.queryeditor;

public class EndstringConstant extends StringConstant {

    public String getSQLWhereClauseStringValue() {
        return "\'%" + getValue().toString() + "\'";
    }
    
    public String getHibernateWhereClauseStringValue() {
        return "\"%" + getValue().toString() + "\"";
    }    
	
}
