package com.pharmadm.custom.rega.queryeditor.constant;

import java.io.Serializable;
import java.text.Format;


public class BooleanConstant extends Constant implements Serializable {

	public BooleanConstant(SuggestedValues suggestedValues) {
		super(suggestedValues);
    	addSuggestedValue(new SuggestedValuesOption("true"));
    	addSuggestedValue(new SuggestedValuesOption("false"));
    	setSuggestedValuesMandatory(true);
	}
	
	
	public BooleanConstant() {
    	addSuggestedValue(new SuggestedValuesOption("true"));
    	addSuggestedValue(new SuggestedValuesOption("false"));
    	setSuggestedValuesMandatory(true);
	}
	
    private static final Format BOOLEAN_FORMAT = new BooleanFormat();
    
	@Override
	public Format getFormat() {
		return BOOLEAN_FORMAT;
	}

	public Class getValueType() {
		return Boolean.class;
	}

	   /**
     * A Format that accepts anything.  Any String gets parsed into a String.
     */
    private static class BooleanFormat extends Format {
        
        public StringBuffer format(Object obj, StringBuffer toAppendTo, java.text.FieldPosition pos) {
            if (obj != null && obj instanceof Boolean) {
            	Boolean b = (Boolean) obj;
                toAppendTo.append(b.toString());
            }
            else {
                toAppendTo.append("[unspecified]");
            }
            return toAppendTo;
        }
        
        public Object parseObject(String source, java.text.ParsePosition pos) {
            Boolean result = null;
            if (source != null) {
                if (source.substring(pos.getIndex(), pos.getIndex()+4).equalsIgnoreCase("true")) {
                	result = new Boolean(true);
                	pos.setIndex(pos.getIndex()+4);
                }
                else if (source.substring(pos.getIndex(), pos.getIndex()+5).equalsIgnoreCase("false")) {
                	result = new Boolean(false);
                	pos.setIndex(pos.getIndex()+5);
                }
            }
            return result;
        }
        
    }

	@Override
	public String getValueTypeString() {
		return "Boolean";
	}
	
	
}
