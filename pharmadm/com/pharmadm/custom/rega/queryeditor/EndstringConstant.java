package com.pharmadm.custom.rega.queryeditor;

public class EndstringConstant extends StringConstant {

    public String acceptWhereClause(QueryVisitor visitor) {
    	return visitor.visitWhereClauseEndstringConstant(this);
    }
}
