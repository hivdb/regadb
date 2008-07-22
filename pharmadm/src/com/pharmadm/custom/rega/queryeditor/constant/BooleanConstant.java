package com.pharmadm.custom.rega.queryeditor.constant;

import java.io.Serializable;

import com.pharmadm.custom.rega.queryeditor.catalog.DbObject;
import com.pharmadm.custom.rega.queryeditor.catalog.DbObject.ValueType;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;



public class BooleanConstant extends Constant implements Serializable {

	private SuggestedValuesOption trueOption = new SuggestedValuesOption("true");
	private SuggestedValuesOption falseOption = new SuggestedValuesOption("false");
	
	public BooleanConstant() {
		super();
    	addSuggestedValue(trueOption);
    	addSuggestedValue(falseOption);
    	setSuggestedValuesMandatory(true);
	}
    
	@Override
	public DbObject getDbObject() {
		return DatabaseManager.getInstance().getAWCCatalog().getObject(ValueType.Boolean.toString());
	}

	@Override
	public Object getdefaultValue() {
		return trueOption;
	}
	
    public String acceptWhereClause(QueryVisitor visitor) {
        return visitor.visitWhereClauseConstant(this);
    } 	

	@Override
	protected String parseObject(Object o) {
		if (o.equals(trueOption)) {
			return "true";
		}
		else if (o.equals(falseOption)){
			return "false";
		}
		else {
			return null;
		}
	}
}
