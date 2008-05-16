package com.pharmadm.custom.rega.queryeditor.constant;

import java.io.Serializable;

import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;


public class OperatorConstant extends StringConstant implements Serializable {
	
	private String humanStringValue;

	public OperatorConstant() {}
	
	public OperatorConstant(SuggestedValues suggestedValues, String humanStringValue) {
		super(suggestedValues);
		this.humanStringValue = humanStringValue;
	}
	
    public Object parseValue(Object s) throws java.text.ParseException {
    	Object value = super.parseValue(s);
    	setValue(value);
    	parseHumanStringValue(s);
        return value;
    }
    
    protected void parseHumanStringValue(Object s) {
    	humanStringValue = null;
    	for (SuggestedValuesOption option : getSuggestedValuesList()) {
    		if (option.getValue().equals(s)) {
        		humanStringValue = getSuggestedValuesList().get(getSuggestedValuesList().indexOf(option)).getOption().toString();
    		}
    	}
    	
    	if (humanStringValue == null){
    		humanStringValue = getValue().toString();
    	}
    }
	
    public String getHumanStringValue() {
    	return humanStringValue;
    }
    
    public String acceptWhereClause(QueryVisitor visitor) {
        return visitor.visitWhereClauseConstant(this);
    }
}
