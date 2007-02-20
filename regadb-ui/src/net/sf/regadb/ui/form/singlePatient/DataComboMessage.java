package net.sf.regadb.ui.form.singlePatient;

import net.sf.witty.wt.i8n.WMessage;

public class DataComboMessage <DataType> extends WMessage
{
    private DataType value_;
    
    public DataComboMessage(DataType value, String text)
    {
        super(text, true);
        value_ = value;
    }

    public DataType getValue() 
    {
        return value_;
    }
}
