package com.pharmadm.custom.rega.queryeditor;

public class EndstringConstant extends StringConstant {
	public EndstringConstant() {}
	
	public EndstringConstant(SuggestedValues suggestedValues) {
		super(suggestedValues);
	}
	
	
    public String acceptWhereClause(QueryVisitor visitor) {
    	return visitor.visitWhereClauseEndstringConstant(this);
    }
}
