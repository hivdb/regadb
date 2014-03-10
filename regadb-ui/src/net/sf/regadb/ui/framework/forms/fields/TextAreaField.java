package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import eu.webtoolkit.jwt.WFormWidget;
import eu.webtoolkit.jwt.WTextArea;

public class TextAreaField extends FormField
{
	private WTextArea textArea;
	
	public TextAreaField(InteractionState state, IForm form, FieldType type)
	{
		super(form);
        if(state == InteractionState.Adding || state == InteractionState.Editing)
        {
        	textArea = new WTextArea();
        	textArea.addStyleClass("text-area");
            
            addWidget(textArea);
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

		setText(null);
	}
    
    public TextAreaField(InteractionState state, IForm form)
    {
        this(state, form, FieldType.ALFANUMERIC);
    }
	
    public WTextArea getTextArea() {
    	return textArea;
    }

	@Override
	public void setText(String text) {
		super.setText(text);
	}

	public WFormWidget getFormWidget()
	{
		return textArea;
	}
	
	public void flagErroneous()
	{
		if (textArea != null)
			textArea.addStyleClass("Wt-invalid");
	}

	public void flagValid()
	{
		if (textArea != null)
			textArea.removeStyleClass("Wt-invalid");
	}

    public String getFormText() 
    {
        return textArea.getText();
    }
    
    public void setFormText(String text) 
    {
    	textArea.setText(text);
    }
    
    public void setEnabled(boolean enabled)
    {
        if(textArea!=null)
        {
        	textArea.setEnabled(enabled);
        }
    }
    
    public void setHidden(boolean hide)
    {
        if(textArea!=null)
        {
        	textArea.setHidden(hide);
        }
        else
        {
            getViewWidget().setHidden(hide);
        }
    }
}
