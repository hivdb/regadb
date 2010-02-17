package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import eu.webtoolkit.jwt.JSlot;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WCssDecorationStyle;
import eu.webtoolkit.jwt.WFormWidget;
import eu.webtoolkit.jwt.WTextArea;

public class TextArea extends FormField
{
    private WTextArea _fieldEdit;
    
    public TextArea(InteractionState state, IForm form)
    {
        super(form);
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
    
    public void setSize(int cols, int rows) {
    	if (_fieldEdit != null) {
    		_fieldEdit.setColumns(cols);
    		_fieldEdit.setRows(rows);
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
        return _fieldEdit.getText();
    }
    
    public void setFormText(String text) 
    {
        _fieldEdit.setText(text);
    }
    
    public WCssDecorationStyle decorationStyle()
    {
        if(_fieldEdit!=null)
        {
            return _fieldEdit.getDecorationStyle();
        }
        else
        {
            return getViewWidget().getDecorationStyle();
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
