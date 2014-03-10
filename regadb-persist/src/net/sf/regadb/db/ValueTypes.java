package net.sf.regadb.db;

import java.util.regex.Pattern;

public enum ValueTypes 
{
    NUMBER,
    LIMITED_NUMBER,
    STRING,
    TEXT,
    NOMINAL_VALUE,
    DATE;
    
    public static ValueTypes getValueType(ValueType vt)
    {
    	ValueTypes ret = null;
    	
        if ( vt.getDescription().equals("number") ) ret = NUMBER;
        else if ( vt.getDescription().equals("limited number (<,=,>)") ) ret = LIMITED_NUMBER;
        else if ( vt.getDescription().equals("string") ) ret = STRING;
        else if ( vt.getDescription().equals("text") ) ret = TEXT;
        else if ( vt.getDescription().equals("nominal value") ) ret = NOMINAL_VALUE;
        else if ( vt.getDescription().equals("date") ) ret = DATE;
        
        return ret;
    }
    
    public static boolean isNominal(ValueType vt){
        return getValueType(vt) == NOMINAL_VALUE;
    }
    
    public static boolean isDate(ValueType vt){
        return getValueType(vt) == DATE;
    }
    
    public static boolean isValidValue(ValueType valueType, String value){
        ValueTypes vt = ValueTypes.getValueType(valueType);
        
        if(vt == ValueTypes.NUMBER)
            return isValidNumber(value);
        else if(vt == ValueTypes.LIMITED_NUMBER)
            return isValidLimitedNumber(value);
        
        return true;
    }
    
    public static boolean isValidNumber(String value){
        if(value == null || value.length() == 0)
            return false;
        
        try{
            Double.parseDouble(value);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    
    public static boolean isValidLimitedNumber(String value){
        if(value == null || value.length() == 0)
            return false;
        
        char c = value.charAt(0);
        if(c == '<' || c == '>' || c == '='){
            value = value.substring(1);
            return isValidNumber(value);
        }
        return false;
    }

    public static boolean isValidString(String value, String validationString){
		if(validationString != null && validationString.length() > 0){
			Pattern p = Pattern.compile(validationString);
			return p.matcher(value).matches();
		}
		return true;
    }
}
