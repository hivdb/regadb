package com.pharmadm.custom.rega.queryeditor.constant;

import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;

public class EndstringConstant extends StringConstant {
	public EndstringConstant() {}
	
	public EndstringConstant(SuggestedValues suggestedValues) {
		super(suggestedValues);
	}
	
	
    public String acceptWhereClause(QueryVisitor visitor) {
    	return visitor.visitWhereClauseEndstringConstant(this);
    }
}
