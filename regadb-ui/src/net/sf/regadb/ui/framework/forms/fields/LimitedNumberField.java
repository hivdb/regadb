package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.MyComboBox;
import eu.webtoolkit.jwt.WDoubleValidator;
import eu.webtoolkit.jwt.WFormWidget;
import eu.webtoolkit.jwt.WIntValidator;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WValidator;

public class LimitedNumberField extends FormField
{
    private WLineEdit fieldEdit_;
    private MyComboBox limiterField_;
    
    public LimitedNumberField(InteractionState state, IForm form, FieldType type)
    {
        super(form);
        if(state == InteractionState.Adding || state == InteractionState.Editing)
        {
            fieldEdit_ = new WLineEdit();
            ConfirmUtils.addConfirmAction(form, fieldEdit_);

            limiterField_ = new MyComboBox();
            limiterField_.addItem("<");
            limiterField_.addItem("=");
            limiterField_.addItem(">");
            WTable table = new WTable(this);
            table.getElementAt(0, 0).addWidget(limiterField_);
            table.getElementAt(0, 0).resize(new WLength(3, WLength.Unit.FontEm), new WLength());
            table.getElementAt(0, 1).addWidget(fieldEdit_);
            
            flagValid();
        }
        else
        {
            initViewWidget();
        }
        
        form.addFormField(this);
        
        if(state == InteractionState.Adding || state == InteractionState.Editing)
        {
            switch(type)
            {
                case DOUBLE:
                    fieldEdit_.setValidator(new WDoubleValidator());
                    break;
                case INTEGER:
                    fieldEdit_.setValidator(new WIntValidator());
                    break;
            }
        }
		setText(null);
    }
    
    public LimitedNumberField(InteractionState state, IForm form)
    {
        this(state, form, FieldType.DOUBLE);
    }
    
    public void setEchomode(WLineEdit.EchoMode mode)
    {
        fieldEdit_.setEchoMode(mode);
    }

    public WFormWidget getFormWidget()
    {
        return fieldEdit_;
    }
    
    public void flagErroneous()
    {
    	fieldEdit_.setStyleClass("Wt-invalid");
        limiterField_.setStyleClass("Wt-invalid");
    }

    public void flagValid()
    {
    	fieldEdit_.setStyleClass("");
        limiterField_.setStyleClass("");
    }

    public String getFormText() 
    {
        if(fieldEdit_.getText().equals(""))
            return "";
        else
            return limiterField_.getCurrentText().getValue()+fieldEdit_.getText();
    }
    
    public boolean validate()
    {
        if(getFormWidget().getValidator()!=null)
        {
            return getFormWidget().getValidator().validate(fieldEdit_.getText()) == WValidator.State.Valid;
        }
        return true;
    }
    
    public void setFormText(String text) 
    {
        String tmpText = text.trim();
        try
        {
	        switch(text.charAt(0))
	        {
	            case '<':
	                limiterField_.setCurrentIndex(0);
                    break;
	            case '=':
	                limiterField_.setCurrentIndex(1);
                    break;
	            case '>':
	                limiterField_.setCurrentIndex(2);
                    break;
	            default :
	            	limiterField_.setCurrentIndex(0);
	        }
	        fieldEdit_.setText(tmpText.substring(1).trim());
        }
        catch(StringIndexOutOfBoundsException sioobe)
        {
        	//handle an empty string
        	limiterField_.setCurrentIndex(0);
        	fieldEdit_.setText("");
        }
    }
}
