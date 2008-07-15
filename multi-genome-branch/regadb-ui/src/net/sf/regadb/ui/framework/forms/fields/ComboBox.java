package net.sf.regadb.ui.framework.forms.fields;

import java.util.ArrayList;

import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WFormWidget;

public class ComboBox<ComboDataType> extends FormField
{
    private WComboBox fieldEdit_;
    private ArrayList<DataComboMessage<ComboDataType>> list_ = null;
    private int selectedIndex = -1;
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
            list_ = new ArrayList<DataComboMessage<ComboDataType>>();
            initViewWidget();
        }
        
        if(form!=null)
        {
        	form.addFormField(this);
        }
    }
    
    public void addComboChangeListener(SignalListener<WEmptyEvent> listener)
    {
    	if(fieldEdit_!=null)
    	{
    	fieldEdit_.changed.addListener(listener);
    	}
    }
    
    public void addItem(DataComboMessage<ComboDataType> item)
    {
        if(fieldEdit_!=null)
        {
            fieldEdit_.addItem(item);
        }
        else
        {
            list_.add(item);
        }
    }
    
    public void sort() {
        if(fieldEdit_!=null) {
            fieldEdit_.sort();
        }
    }
    
    public void removeCurrentItem()
    {
        if(fieldEdit_!=null)
        {
            if(fieldEdit_.currentIndex()!=-1)
                fieldEdit_.removeItem(fieldEdit_.currentIndex());
        }
        else
        {
            if(selectedIndex!=-1)
                list_.remove(selectedIndex);
        }
    }
    
    public ComboDataType currentValue()
    {
        if(currentItem()!=null)
            return currentItem().getValue();
        else
            return null;
    }
    
    public boolean isNoSelectionItem(DataComboMessage msg)
    {
        return msg.getValue()==null;
    }
    
    public DataComboMessage<ComboDataType> currentItem()
    {
    	if(fieldEdit_!=null)
    	{
            if(fieldEdit_.currentText()==null)
                return null;
            else if(isNoSelectionItem((DataComboMessage)fieldEdit_.currentText()))
                return null;
            else
                return (DataComboMessage<ComboDataType>)fieldEdit_.currentText();
    	}
    	else
    	{
            if(selectedIndex!=-1)
                return list_.get(selectedIndex);
            else
                return null;
    	}
    }
    
    /*public void selectItem(DataComboMessage<ComboDataType> itemToSelect)
    {
        if(fieldEdit_!=null)
        {
            fieldEdit_.setCurrentItem(itemToSelect);
        }
        else
        {
            selectedIndex = list_.indexOf(itemToSelect);
            if(selectedIndex!=-1)
                setViewMessage(itemToSelect);
        }
    }*/
    
    public void selectItem(String messageValueRepresentation)
    {
        if(fieldEdit_!=null)
        {
            for(int i = 0; i < fieldEdit_.count(); i++) {
                if(fieldEdit_.itemText(i).value().equals(messageValueRepresentation)) {
                    selectIndex(i);
                    return;
                }
            }
        }
        else
        {
            for(int i = 0; i < list_.size(); i++) {
                if(list_.get(i).value().equals(messageValueRepresentation)) {
                    selectIndex(i);
                    return;
                }
            }
        }
    }
    
    public void selectIndex(int index)
    {
        if(fieldEdit_!=null)
        {
            if(fieldEdit_.count()>0)
                fieldEdit_.setCurrentIndex(index);
        }
        else
        {
            if(list_.size()>0)
            {
                selectedIndex = index;
                setViewMessage(list_.get(selectedIndex));
            }
        }
    }

    public WFormWidget getFormWidget()
    {
        return fieldEdit_;
    }
    
    public void flagErroneous()
    {
        if(fieldEdit_!=null)
            fieldEdit_.setStyleClass("form-field-combo-edit-invalid");
    }

    public void flagValid()
    {
        if(fieldEdit_!=null)
            fieldEdit_.setStyleClass("form-field-combo-edit-valid");
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
        if(fieldEdit_!=null)
        {
            fieldEdit_.insertItem(0, new DataComboMessage<ComboDataType>((ComboDataType)null, tr(noSelectionItem).value()));
        }
        else
        {
            list_.add(0,new DataComboMessage<ComboDataType>((ComboDataType)null, tr(noSelectionItem).value()));
        }
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
            if(fieldEdit_.currentText()==null)
                return false;
            
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
        else
        {
            list_.clear();
        }
    }
    
    public void setEnabled(boolean enabled)
    {
        if(fieldEdit_!=null)
        {
            fieldEdit_.setEnabled(enabled);
        }
    }
    
    public void setHidden(boolean hide)
    {
        if(fieldEdit_!=null)
        {
            fieldEdit_.setHidden(hide);
        }
        else
        {
            getViewWidget().setHidden(hide);
        }
    }
    
    public boolean isHidden()
    {
        if(fieldEdit_!=null)
        {
            return fieldEdit_.isHidden();
        }
        else
        {
            return getViewWidget().isHidden();
        }
    }
}