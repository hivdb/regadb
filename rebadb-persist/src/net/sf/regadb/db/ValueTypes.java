package net.sf.regadb.db;

public enum ValueTypes 
{
    NUMBER,
    LIMITED_NUMBER,
    STRING,
    NOMINAL_VALUE;
    
    public static ValueTypes getValueType(ValueType vt)
    {
        switch(vt.getValueTypeIi())
        {
        case 1:
            return NUMBER;
        case 2:
            return LIMITED_NUMBER;
        case 3:
            return STRING;
        case 4:
            return NOMINAL_VALUE;
        }
        
        return null;
    }
}
