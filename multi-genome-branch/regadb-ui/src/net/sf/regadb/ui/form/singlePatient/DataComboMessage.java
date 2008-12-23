package net.sf.regadb.ui.form.singlePatient;

import eu.webtoolkit.jwt.WString;


public class DataComboMessage <DataType> extends WString
{
    private DataType value_;
    
    public DataComboMessage(DataType value, String text)
    {
        super(text);
        value_ = value;
    }

    public DataType getValue() 
    {
        return value_;
    }
}
