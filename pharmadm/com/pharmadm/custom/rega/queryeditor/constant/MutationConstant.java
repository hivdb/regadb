package com.pharmadm.custom.rega.queryeditor.constant;

import java.text.Format;

import com.pharmadm.custom.rega.queryeditor.FromVariable;
import com.pharmadm.custom.rega.queryeditor.InputVariable;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;
import com.pharmadm.custom.rega.reporteditor.ValueSpecifier;


public class MutationConstant extends Constant {

    private MutationFormat MUTATION_FORMAT;
    private String humanStringValue;
	
	public MutationConstant(InputVariable ivar) {
		MUTATION_FORMAT = new MutationFormat(ivar);
	}
	
    public Object parseValue(Object s) throws java.text.ParseException {
    	System.err.println("setvalue:" + s.toString());
    	Object value = MUTATION_FORMAT.parseObject(s.toString());
    	setValue(value);
    	humanStringValue = s.toString();
    	System.err.println("hsv:" + humanStringValue);
    	System.err.println("value:" + value.toString());
        return value;
    }	
	
    public String getHumanStringValue() {
    	return humanStringValue;
    }
    
    public Object getHumanValue() {
    	return humanStringValue;
    }
    
	public Format getFormat() {
		return new StringConstant.StringFormat();
	}

	public Class getValueType() {
		return String.class;
	}

	public String getValueTypeString() {
		return "String";
	}
	
    public ValueSpecifier cloneInContext(java.util.Map originalToCloneMap) throws CloneNotSupportedException {
    	MutationConstant clone = (MutationConstant) super.cloneInContext(originalToCloneMap);
    	clone.MUTATION_FORMAT = MUTATION_FORMAT.cloneInContext(originalToCloneMap);
    	clone.humanStringValue = humanStringValue;
        return clone;
    }	
	
    private class MutationFormat extends Format {
        private InputVariable ivar;
    	
        public MutationFormat cloneInContext(java.util.Map originalToCloneMap) throws CloneNotSupportedException {
        	MutationFormat clone = (MutationFormat) this.clone();
        	clone.ivar = (InputVariable) originalToCloneMap.get(ivar);
        	return clone;
        }
        
    	public MutationFormat(InputVariable ivar) {
    		this.ivar = ivar;
    	}
    	
        public StringBuffer format(Object obj, StringBuffer toAppendTo, java.text.FieldPosition pos) {
            if (obj != null) {
                if (! (obj instanceof String)) {
                    System.err.println("Expected String. Got instead: " + obj.getClass());
                    throw new IllegalArgumentException();
                }
                String objString = (String)obj;
                toAppendTo.append(objString);
            } else {
                toAppendTo.append("[unspecified]");
            }
            return toAppendTo;
        }
        
        public Object parseObject(String source, java.text.ParsePosition pos) {
            String result = null;
            if (source != null) {
                int index = pos.getIndex();
                if (index == 0) {
                    result = parse(source);
                } else {
                    result = parse(source.substring(index));
                }
                pos.setIndex(source.length());
                
            }
            return result;
        }
        
        private String parse(String source) {
        	QueryVisitor builder = DatabaseManager.getInstance().getQueryBuilder();       	
        	System.err.println("parse:" + source);
        	String result = "";
        	String[] options = source.split(",");
        	
        	for (String option : options) {
        		FromVariable fromVariable = new FromVariable("net.sf.regadb.db.AaMutation");
        		option = option.trim();
        		if (option.startsWith("!")) {
        			result += "NOT ";
        			option = option.substring(1).trim();
        		}
        		
        		String number = "";
        		String value = "";
        		for (int i = 0 ; i < option.length() ; i++) {
        			if (option.charAt(i) >= '0' && option.charAt(i) <= '9') {
        				number = number += option.charAt(i);
        			}
        			else {
        				break;
        			}
        		}
        		value = option.substring(number.length()).trim();        		
        		Constant valueConstant = new StringConstant();
        		valueConstant.setValue(value);
        		Constant numberConstant = new DoubleConstant();
        		numberConstant.setValue(number);

        		result += ivar.acceptWhereClause(builder);
        		result += " in (\n\t\tSELECT\n\t\t\t";
        		result += fromVariable.acceptWhereClause(builder);
        		result += ".id.aaSequence";
        		result += "\n\t\tFROM\n\t\t\t";
        		result += fromVariable.getFromClauseStringValue(builder);
        		result += "\n\t\tWHERE\n\t\t\t";
        		result += fromVariable.acceptWhereClause(builder);
        		result += ".id.mutationPosition = ";
        		result += numberConstant.acceptWhereClause(builder);
        		
        		if (value.length() > 0) {
	        		result += " AND\n\t\t\tUPPER (";
	        		result += fromVariable.acceptWhereClause(builder);
	        		result += ".aaMutation) LIKE UPPER (";
	        		result += valueConstant.acceptWhereClause(builder);
	        		result += ")";
        		}
        		
        		result += ")\n\t AND ";
        	}
        	return result.substring(0, result.length()-6);
        }
    }	

}
