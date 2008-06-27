package net.sf.regadb.db;

public enum ValueTypes 
{
    NUMBER,
    LIMITED_NUMBER,
    STRING,
    NOMINAL_VALUE,
    DATE;
    
    public static ValueTypes getValueType(ValueType vt)
    {
    	ValueTypes ret = null;
    	
        if ( vt.getDescription().equals("number") ) ret = NUMBER;
        else if ( vt.getDescription().equals("limited number (<,=,>)") ) ret = LIMITED_NUMBER;
        else if ( vt.getDescription().equals("string") ) ret = STRING;
        else if ( vt.getDescription().equals("nominal value") ) ret = NOMINAL_VALUE;
        else if ( vt.getDescription().equals("date") ) ret = DATE;
        
        return ret;
    }
}
