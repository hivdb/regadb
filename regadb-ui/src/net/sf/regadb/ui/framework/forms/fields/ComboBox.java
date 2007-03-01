package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.WComboBox;
import net.sf.witty.wt.widgets.WFormWidget;

public class ComboBox extends FormField
{
    private WComboBox fieldEdit_;
    private boolean mandatory_ = false;
    private final static String noSelectionItem = "form.combobox.noSelectionItem";
    
    public ComboBox(InteractionState state, IForm form)
    {
        super();
        if(state == InteractionState.Adding || state == InteractionState.Editing)
        {
            fieldEdit_ = new WComboBox();
            addWidget(fieldEdit_);
            flagValid();
        }
        else
        {
            initViewWidget();
        }
        
        form.addFormField(this);
    }
    
    public void addItem(WMessage item)
    {
        if(fieldEdit_!=null)
        {
            fieldEdit_.addItem(item);
        }
    }
    
    public WMessage currentText()
    {
    	if(fieldEdit_!=null)
    	{
    		return fieldEdit_.currentText();
    	}
    	else
    	{
    		return getViewMessage();
    	}
    }
    
    public void selectItem(WMessage itemToSelect)
    {
        if(fieldEdit_!=null)
        {
            fieldEdit_.setCurrentItem(itemToSelect);
        }
        else
        {
            setViewMessage(itemToSelect);
        }
    }

    public WFormWidget getFormWidget()
    {
        return fieldEdit_;
    }
    
    public void flagErroneous()
    {
        fieldEdit_.setStyleClass("form-field-combobox-edit-invalid");
    }

    public void flagValid()
    {
        fieldEdit_.setStyleClass("form-field-combobox-edit-valid");
    }

    public String getFormText() 
    {
        return fieldEdit_.currentText().keyOrValue();
    }

    public void setFormText(String text) 
    {
        fieldEdit_.setCurrentItem(lt(text));
    }

    public void addNoSelectionItem() 
    {
        addItem(tr(noSelectionItem));
    }
    
    public void setMandatory(boolean mandatory)
    {
        mandatory_ = mandatory;
    }
    
    public boolean isMandatory()
    {
        if(getFormWidget()==null)
        {
            return false;
        }
        else
        {
            return mandatory_;
        }
    }
    
    public boolean validate()
    {
        if(isMandatory())
        {
            return !(fieldEdit_.currentText().keyOrValue().equals(noSelectionItem));
        }
        else
        {
            return true;
        }
    }
    
    public void clearItems()
    {
    	if(fieldEdit_!=null)
    	{
    		fieldEdit_.clear();
    	}
    }
}
