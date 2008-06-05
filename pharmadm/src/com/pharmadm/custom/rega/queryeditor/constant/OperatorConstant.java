package com.pharmadm.custom.rega.queryeditor.constant;

import java.io.Serializable;

import com.pharmadm.custom.rega.queryeditor.catalog.DbObject;
import com.pharmadm.custom.rega.queryeditor.catalog.DbObject.ValueType;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;


public class OperatorConstant extends Constant implements Serializable {
	
    public String acceptWhereClause(QueryVisitor visitor) {
        return visitor.visitWhereClauseConstant(this);
    }

	@Override
	public DbObject getDbObject() {
		return DatabaseManager.getInstance().getAWCCatalog().getObject(ValueType.String.toString());
	}

	@Override
	public Object getdefaultValue() {
		if (getSuggestedValuesList().isEmpty()) {
			return "";
		}
		else {
			return getSuggestedValuesList().get(0);
		}
	}

	@Override
	protected String parseObject(Object o) {
    	String humanStringValue = null;
    	for (SuggestedValuesOption option : getSuggestedValuesList()) {
    		if (option.equals(o)) {
        		humanStringValue = ((SuggestedValuesOption) o).getOption().toString();
    		}
    	}
    	return humanStringValue;
	}
}
