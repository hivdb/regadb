package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.widgets.WCheckBox;
import net.sf.witty.wt.widgets.WFormWidget;
import net.sf.witty.wt.widgets.WPushButton;
import net.sf.witty.wt.widgets.WTextArea;

public class NucleotideField extends FormField
{
    private WTextArea _fieldEdit;
    private WCheckBox autoFix_;
    private WPushButton uploadFasta_;
    
    public NucleotideField(InteractionState state, IForm form)
    {
        super();
        if(state == InteractionState.Adding || state == InteractionState.Editing)
        {
            _fieldEdit = new WTextArea();
            autoFix_ = new WCheckBox(tr("formfield.ntfield.checkbox.autofixSequence"));
            uploadFasta_ = new WPushButton(tr("formfield.ntfield.button.uploadFastaFile"));
            
            addWidget(_fieldEdit);
            flagValid();
        }
        else
        {
            initViewWidget();
        }
        
        form.addFormField(this);
        
        if(_fieldEdit!=null)
        {
            //TODO set validator
        }
    }
    
    public WFormWidget getFormWidget()
    {
        return _fieldEdit;
    }
    
    public void flagErroneous()
    {
        _fieldEdit.setStyleClass("form-field-ntfield-edit-invalid");
    }

    public void flagValid()
    {
        _fieldEdit.setStyleClass("form-field-ntfield-edit-valid");
    }

    public String getFormText() 
    {
        return _fieldEdit.text();
    }
    
    public void setFormText(String text) 
    {
        _fieldEdit.setText(text);
    }
}
