package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.WFormWidget;
import net.sf.witty.wt.WLineEdit;
import net.sf.witty.wt.WLineEditEchoMode;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.core.utils.WLength;
import net.sf.witty.wt.core.utils.WLengthUnit;
import net.sf.witty.wt.validation.WDoubleValidator;
import net.sf.witty.wt.validation.WIntValidator;
import net.sf.witty.wt.validation.WValidatorState;

public class LimitedNumberField extends FormField
{
    private WLineEdit fieldEdit_;
    private WComboBox limiterField_;
    
    public LimitedNumberField(InteractionState state, IForm form, FieldType type)
    {
        super();
        if(state == InteractionState.Adding || state == InteractionState.Editing)
        {
            fieldEdit_ = new WLineEdit();
            ConfirmUtils.addConfirmAction(form, fieldEdit_);

            limiterField_ = new WComboBox();
            limiterField_.addItem(lt("<"));
            limiterField_.addItem(lt("="));
            limiterField_.addItem(lt(">"));
            WTable table = new WTable(this);
            table.putElementAt(0, 0, limiterField_);
            table.elementAt(0, 0).resize(new WLength(3, WLengthUnit.FontEm), new WLength());
            table.putElementAt(0, 1, fieldEdit_);
            
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
    
    public void setEchomode(WLineEditEchoMode mode)
    {
        fieldEdit_.setEchoMode(mode);
    }

    public WFormWidget getFormWidget()
    {
        return fieldEdit_;
    }
    
    public void flagErroneous()
    {
        fieldEdit_.setStyleClass("form-field textfield edit-invalid");
        limiterField_.setStyleClass("form-field combobox edit-invalid");
    }

    public void flagValid()
    {
        fieldEdit_.setStyleClass("form-field textfield edit-valid");
        limiterField_.setStyleClass("form-field combobox edit-valid");
    }

    public String getFormText() 
    {
        if(fieldEdit_.text().equals(""))
            return "";
        else
            return limiterField_.currentText().value()+fieldEdit_.text();
    }
    
    public boolean validate()
    {
        if(getFormWidget().validator()!=null)
        {
            return getFormWidget().validator().validate(fieldEdit_.text(), null) == WValidatorState.Valid;
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
