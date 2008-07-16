package com.pharmadm.custom.rega.queryeditor.constant;

import com.pharmadm.custom.rega.queryeditor.FromVariable;
import com.pharmadm.custom.rega.queryeditor.InputVariable;
import com.pharmadm.custom.rega.queryeditor.catalog.DbObject;
import com.pharmadm.custom.rega.queryeditor.catalog.DbObject.ValueType;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;
import com.pharmadm.custom.rega.reporteditor.ValueSpecifier;


public class MutationConstant extends Constant {

	private InputVariable ivar;
	private String referencePath;
	private String mutationPath;
	
	public MutationConstant(InputVariable ivar, String referencePath, String mutationPath) {
		this.ivar = ivar;
		this.referencePath = referencePath;
		this.mutationPath = mutationPath;
	}
	

	
    
	public DbObject getDbObject() {
		return DatabaseManager.getInstance().getAWCCatalog().getObject(ValueType.String.toString());
	}
	
    public ValueSpecifier cloneInContext(java.util.Map originalToCloneMap) throws CloneNotSupportedException {
    	MutationConstant clone = (MutationConstant) super.cloneInContext(originalToCloneMap);
    	clone.ivar = (InputVariable) originalToCloneMap.get(ivar);
        return clone;
    }	
	
	@Override
	public Object getdefaultValue() {
		return "";
	}

	@Override
	protected String parseObject(Object o) {
		String source = o.toString();
		if (validateMutationString(source) != null) {
			return source;
		}
		return null;
	}	
	
    public String acceptWhereClause(QueryVisitor visitor) {
        return validateMutationString(getValue().toString());
    }  	
	
	private String validateMutationString(String str) {
		QueryVisitor builder = DatabaseManager.getInstance().getQueryBuilder();       	
		String query = "";
		DbObject aaMut = DatabaseManager.getInstance().getAWCCatalog().getObject("AaMutation");


		// remove pointless spacing
		str = str.replace('\t', ' ');
		str = str.replace('\n', ' ');
		str = str.replace('\r', ' ');
		str = str.trim();
		
		if (str.length() == 0) {
			return query;
		}
		else {
	    	String[] options = str.split(" ");
        	for (String option : options) {
        		// empty options are invalid
        		if (option.length() == 0) {
        			return null;
        		}
        		
        		// check for negation 
        		if (option.charAt(0) == '!') {
        			option = option.substring(1);
        			query += "NOT ";
        		}

        		// reference aa string
        		String mutationStr = getMutationString(option);
        		option = option.substring(mutationStr.length());
        		
        		// mutation position
        		String number = getNumber(option);
        		option = option.substring(number.length());
        		
        		// mutation string
        		String mutationStr2 = getMutationString(option);
        		option = option.substring(mutationStr2.length());

        		// remaining text is invalid
        		if (option.length() > 0) {
        			return null;
        		}
        		
        		// all options omitted is invalid
        		if (number.length() == 0 && mutationStr.length() == 0 && mutationStr2.length() == 0) {
        			return null;
        		}
        		
        		boolean previousCondition = false;
        		FromVariable fromVariable = new FromVariable(aaMut);
        		query += ivar.acceptWhereClause(builder);
        		query += " in (\n\t\tSELECT\n\t\t\t";
        		query += fromVariable.acceptWhereClause(builder);
        		query += ".id.aaSequence";
        		query += "\n\t\tFROM\n\t\t\t";
        		query += fromVariable.getFromClauseStringValue(builder);
        		query += "\n\t\tWHERE\n\t\t\t";
        		
        		if (mutationStr.length() > 0 && !mutationStr.equals("*")) {
            		Constant mutationStrConstant = new StringConstant();
            		mutationStrConstant.setValue(mutationStr);
            		
            		if (previousCondition) {
            			query += " AND";
            		}
	        		
	        		query += "\n\t\t\tUPPER (";
	        		query += fromVariable.acceptWhereClause(builder);
	        		query += "." + referencePath + ") LIKE UPPER (";
	        		query += mutationStrConstant.acceptWhereClause(builder);
	        		query += ")";
	        		previousCondition = true;
        		}        		
        		
        		if (number.length() > 0){
	        		Constant numberConstant = new DoubleConstant();
	        		numberConstant.setValue(number);
	        		
            		if (previousCondition) {
            			query += " AND";
            		}
            		query += "\n\t\t\t";
            		query += fromVariable.acceptWhereClause(builder);
	        		query += ".id.mutationPosition = ";
	        		query += numberConstant.acceptWhereClause(builder);
	        		previousCondition = true;
        		}
        		
        		if (mutationStr2.length() > 0 && !mutationStr2.equals("*")) {
            		Constant mutationStrConstant = new StringConstant();
            		mutationStrConstant.setValue(mutationStr2);
            		
            		if (previousCondition) {
            			query += " AND";
            		}
	        		
	        		query += "\n\t\t\tUPPER (";
	        		query += fromVariable.acceptWhereClause(builder);
	        		query += "." + mutationPath + ") LIKE UPPER (";
	        		query += mutationStrConstant.acceptWhereClause(builder);
	        		query += ")";
	        		previousCondition = true;
        		}
    		
        		query += ")\n\t AND ";
        	}
        	query = query.substring(0, query.length()-6);
		}
		return query;
	}
	
	private String getNumber(String str) {
		String number = "";
		for (int i = 0 ; i < str.length() ; i++) {
			if (str.charAt(i) >= '0' && str.charAt(i) <= '9') {
				number = number += str.charAt(i);
			}
			else {
				break;
			}
		}
		return number;
	}
	
	private String getMutationString(String str) {
		String mutationStr = "";  
		for (int i = 0 ; i < str.length() ; i++) {
			if (str.charAt(i) >= 'a' && str.charAt(i) <= 'z' || str.charAt(i) >= 'A' && str.charAt(i) <= 'Z' || str.charAt(i) =='*' || str.charAt(i) == '?') {
				mutationStr = mutationStr += str.charAt(i);
			}
			else {
				break;
			}
		}
		return mutationStr;
		
	}

}
