package net.sf.regadb.ui.form.singlePatient;

import eu.webtoolkit.jwt.WString;

/*
 * TODO remove 
 */

public class DataComboMessage <DataType> extends WString
{
    private DataType value_;
    
    public DataComboMessage(DataType value, String text)
    {
        super(text);
        value_ = value;
    }

    public DataType getDataValue() 
    {
        return value_;
    }
}
