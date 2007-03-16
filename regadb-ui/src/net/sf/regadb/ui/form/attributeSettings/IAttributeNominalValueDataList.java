package net.sf.regadb.ui.form.attributeSettings;

import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.IEditableTable;
import net.sf.witty.wt.widgets.WWidget;

public class IAttributeNominalValueDataList implements IEditableTable<AttributeNominalValue>
{
    private FormWidget form_;
    private static final String [] headers_ = {"editableTable.attributeNominvalValue.colName.name"};
    
    public IAttributeNominalValueDataList(FormWidget form)
    {
        form_ = form;
    }
    
    public void addData(WWidget[] widgets) 
    {
        
    }

    public void changeData(AttributeNominalValue type, WWidget[] widgets) 
    {
        
    }

    public void deleteData(AttributeNominalValue type) 
    {
        
    }

    public InteractionState getInteractionState() 
    {
        return form_.getInteractionState();
    }

    public String[] getTableHeaders() 
    {
        return headers_;
    }

    public WWidget[] getWidgets(AttributeNominalValue type) 
    {
        TextField tf = new TextField(form_.getInteractionState(), form_);
                
        WWidget[] widgets = new WWidget[1];
        widgets[0] = tf;
        
        if(type!=null)
        {
        tf.setText(type.getValue());
        }
        return widgets;
    }
}
