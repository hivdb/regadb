package net.sf.regadb.ui.form.attributeSettings;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.IEditableTable;
import net.sf.witty.wt.WWidget;

public class IAttributeNominalValueDataList implements IEditableTable<AttributeNominalValue>
{
    private FormWidget form_;
    private static final String [] headers_ = {"editableTable.attributeNominvalValue.colName.name"};
    private static final int[] colWidths = {100};
    
    private Attribute attribute_;
    private Transaction transaction_;
    
    public IAttributeNominalValueDataList(FormWidget form)
    {
        form_ = form;
    }
    
    public void addData(WWidget[] widgets) 
    {
        AttributeNominalValue anv = new AttributeNominalValue(attribute_, ((TextField)widgets[0]).text());
        attribute_.getAttributeNominalValues().add(anv);
    }

    public void changeData(AttributeNominalValue type, WWidget[] widgets) 
    {
        for(AttributeNominalValue anv : attribute_.getAttributeNominalValues())
        {
            if(type.getNominalValueIi().equals(anv.getNominalValueIi()))
            {
                anv.setValue(((TextField)widgets[0]).text());
                break;
            }
        }
    }

    public void deleteData(AttributeNominalValue type) 
    {
        attribute_.getAttributeNominalValues().remove(type);
        transaction_.delete(type);
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

    public Attribute getAttribute() 
    {
        return attribute_;
    }

    public void setAttribute(Attribute attribute) 
    {
        this.attribute_ = attribute;
    }

    public void setTransaction(Transaction transaction) 
    {
        this.transaction_ = transaction;
    }

    public WWidget[] addRow()
    {
        WWidget[] widgets = new WWidget[1];
        widgets[0] = new TextField(form_.getInteractionState(), form_);
        
        return widgets;
    }

    public WWidget[] fixAddRow(WWidget[] widgets)
    {
        WWidget[] widgetsToReturn = new WWidget[1];
        TextField tf = new TextField(form_.getInteractionState(), form_);
        tf.setText(((TextField)widgets[0]).text());
        widgetsToReturn[0] = tf; 
        return widgetsToReturn;
    }
    
    public void flush() 
    {
        transaction_.flush();
    }

	public int[] getColumnWidths() {
		return colWidths;
	}
}
