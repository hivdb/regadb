package net.sf.regadb.ui.form.singlePatient;

import net.sf.regadb.db.AttributeNominalValue;
import net.sf.witty.wt.i8n.WMessage;

class AttributeComboMessage extends WMessage
{
    private AttributeNominalValue value_;
    
    public AttributeComboMessage(AttributeNominalValue value) 
    {
        super(value.getValue(), true);
        value_ = value;
    }

    public AttributeNominalValue getValue() 
    {
        return value_;
    }
}
