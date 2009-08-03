package net.sf.regadb.ui.framework.forms.fields;

import java.util.ArrayList;

import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.MyComboBox;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WFormWidget;

public class ComboBox<ComboDataType> extends FormField
{
    private MyComboBox fieldEdit_;
    private ArrayList<DataComboMessage<ComboDataType>> list_ = null;
    private int selectedIndex = -1;
    private boolean mandatory_ = false;
    private final static String noSelectionItem = "form.combobox.noSelectionItem";
    
    public ComboBox(InteractionState state, IForm form)
    {
        super(form);
        if(state == InteractionState.Adding || state == InteractionState.Editing)
        {
            fieldEdit_ = new MyComboBox();
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
        setText(null);
    }
    
    public void addComboChangeListener(Signal.Listener listener)
    {
    	if(fieldEdit_!=null)
    	{
    	fieldEdit_.changed().addListener(this, listener);
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
            if(fieldEdit_.getCurrentIndex()!=-1)
                fieldEdit_.removeItem(fieldEdit_.getCurrentIndex());
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
            return currentItem().getDataValue();
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
            if(fieldEdit_.getCurrentText().getValue().equals(""))
                return null;
            else if(isNoSelectionItem((DataComboMessage)fieldEdit_.getCurrentText()))
                return null;
            else
                return (DataComboMessage<ComboDataType>)fieldEdit_.getCurrentText();
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
            for(int i = 0; i < fieldEdit_.getCount(); i++) {
                if(fieldEdit_.getItemText(i).getValue().equals(messageValueRepresentation)) {
                    selectIndex(i);
                    return;
                }
            }
        }
        else
        {
            for(int i = 0; i < list_.size(); i++) {
                if(list_.get(i).getValue().equals(messageValueRepresentation)) {
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
            if(fieldEdit_.getCount()>0)
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
        	fieldEdit_.setStyleClass("Wt-invalid");
    }

    public void flagValid()
    {
        if(fieldEdit_!=null)
        	fieldEdit_.setStyleClass("");
    }

    public String getFormText() 
    {
        return UIUtils.keyOrValue(fieldEdit_.getCurrentText());
    }

    public void setFormText(String text) 
    {
        fieldEdit_.setCurrentItem(text);
    }

    public void addNoSelectionItem() 
    {
        if(fieldEdit_!=null)
        {
            fieldEdit_.insertItem(0, new DataComboMessage<ComboDataType>((ComboDataType)null, tr(noSelectionItem).getValue()));
        }
        else
        {
            list_.add(0,new DataComboMessage<ComboDataType>((ComboDataType)null, tr(noSelectionItem).getValue()));
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
            if(fieldEdit_.getCurrentText()==null)
                return false;
            
            return !(UIUtils.keyOrValue(fieldEdit_.getCurrentText()).equals(tr(noSelectionItem).getValue()));
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
    
    public int size(){
        return fieldEdit_.getCount();
    }
    public int count(){
        return size();
    }
}
