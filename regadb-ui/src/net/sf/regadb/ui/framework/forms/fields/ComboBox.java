package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.WComboBox;
import net.sf.witty.wt.widgets.WFormWidget;

public class ComboBox extends FormField
{
    private WComboBox _fieldEdit;
    
    public ComboBox(boolean edit, IForm form)
    {
        super();
        if(edit)
        {
            _fieldEdit = new WComboBox();
            addWidget(_fieldEdit);
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
        if(_fieldEdit!=null)
        {
            _fieldEdit.addItem(item);
        }
    }
    
    public WMessage currentText()
    {
        return _fieldEdit.currentText();
    }
    
    public void selectItem(WMessage itemToSelect)
    {
        _fieldEdit.setCurrentItem(itemToSelect);
    }

    public WFormWidget getFormWidget()
    {
        return _fieldEdit;
    }
    
    public void flagErroneous()
    {
        _fieldEdit.setStyleClass("form-field-combobox-edit-invalid");
    }

    public void flagValid()
    {
        _fieldEdit.setStyleClass("form-field-combobox-edit-valid");
    }

    public String getFormText() 
    {
        return _fieldEdit.currentText().keyOrValue();
    }
}
