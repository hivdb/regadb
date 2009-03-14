package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WCssDecorationStyle;
import eu.webtoolkit.jwt.WFormWidget;
import eu.webtoolkit.jwt.WTextArea;

public class TextArea extends FormField
{
    private WTextArea _fieldEdit;
    
    public TextArea(InteractionState state, IForm form)
    {
        super();
        if(state == InteractionState.Adding || state == InteractionState.Editing)
        {
            _fieldEdit = new WTextArea();
            
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
    	_fieldEdit.setStyleClass("Wt-invalid");
    }

    public void flagValid()
    {
    	_fieldEdit.setStyleClass("");
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
    
    public void addChangeListener(Signal.Listener listener)
    {
        if(_fieldEdit!=null)
        {
            _fieldEdit.changed().addListener(this, listener);
        }
    }
}
