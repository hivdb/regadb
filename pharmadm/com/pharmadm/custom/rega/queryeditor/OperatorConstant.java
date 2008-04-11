package com.pharmadm.custom.rega.queryeditor;


public class OperatorConstant extends StringConstant {
	
	private String humanStringValue;

	public OperatorConstant() {}
	
	public OperatorConstant(SuggestedValues suggestedValues, String humanStringValue) {
		super(suggestedValues);
		this.humanStringValue = humanStringValue;
	}
	
    public Object parseValue(String s) throws java.text.ParseException {
    	Object value = super.parseValue(s);
    	
    	humanStringValue = null;
    	for (SuggestedValuesOption option : getSuggestedValuesList()) {
    		if (option.getValue().equals(s)) {
        		humanStringValue = getSuggestedValuesList().get(getSuggestedValuesList().indexOf(option)).getOption().toString();
    		}
    	}
    	
    	if (humanStringValue == null){
    		humanStringValue = value.toString();
    	}
        return value;
    }
	
    public String getHumanStringValue() {
    	return humanStringValue;
    }
	
    public String acceptWhereClause(QueryVisitor visitor) {
        return visitor.visitWhereClauseConstant(this);
    }
}
