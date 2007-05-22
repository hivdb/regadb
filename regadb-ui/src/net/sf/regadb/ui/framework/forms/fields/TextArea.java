package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WCssDecorationStyle;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WFormWidget;
import net.sf.witty.wt.WTextArea;

public class TextArea extends FormField
{
    private WTextArea _fieldEdit;
    
    public TextArea(InteractionState state, IForm form)
    {
        super();
        if(state == InteractionState.Adding || state == InteractionState.Editing)
        {
            _fieldEdit = new WTextArea();
            ConfirmUtils.addConfirmAction(form, _fieldEdit);
            
            addWidget(_fieldEdit);
            flagValid();
        }
        else
        {
            initViewWidget();
        }
        
        if(form!=null)
        {
            form.addFormField(this);
        }
    }
    
    public WFormWidget getFormWidget()
    {
        return _fieldEdit;
    }
    
    public void flagErroneous()
    {
        _fieldEdit.setStyleClass("form-field-textarea-edit-invalid");
    }

    public void flagValid()
    {
        _fieldEdit.setStyleClass("form-field-textarea-edit-valid");
    }

    public String getFormText() 
    {
        return _fieldEdit.text();
    }
    
    public void setFormText(String text) 
    {
        _fieldEdit.setText(text);
    }
    
    public WCssDecorationStyle decorationStyle()
    {
        if(_fieldEdit!=null)
        {
            return _fieldEdit.decorationStyle();
        }
        else
        {
            return getViewWidget().decorationStyle();
        }
    }
    
    public void addChangeListener(SignalListener<WEmptyEvent> listener)
    {
        if(_fieldEdit!=null)
        {
            _fieldEdit.changed.addListener(listener);
        }
    }
}
