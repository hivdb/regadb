package com.pharmadm.custom.rega.queryeditor.constant;

import java.io.Serializable;

import com.pharmadm.custom.rega.queryeditor.VariableType.ValueType;


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
	public String getValueTypeString() {
		return ValueType.Boolean.toString();
	}

	@Override
	public Object getdefaultValue() {
		return trueOption;
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
