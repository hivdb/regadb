package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.witty.wt.validation.WDoubleValidator;
import net.sf.witty.wt.validation.WIntValidator;
import net.sf.witty.wt.validation.WValidatorState;
import net.sf.witty.wt.widgets.WComboBox;
import net.sf.witty.wt.widgets.WFormWidget;
import net.sf.witty.wt.widgets.WLineEdit;
import net.sf.witty.wt.widgets.WLineEditEchoMode;

public class LimitedNumberField extends FormField
{
    private WLineEdit fieldEdit_;
    private WComboBox limiterField_;
    
    public LimitedNumberField(boolean edit, IForm form, FieldType type)
    {
        super();
        if(edit)
        {
            fieldEdit_ = new WLineEdit();

            limiterField_ = new WComboBox();
            limiterField_.addItem(lt("<"));
            limiterField_.addItem(lt("="));
            limiterField_.addItem(lt(">"));

            addWidget(limiterField_);
            addWidget(fieldEdit_);
            
            flagValid();
        }
        else
        {
            initViewWidget();
        }
        
        form.addFormField(this);
        
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
    
    public LimitedNumberField(boolean edit, IForm form)
    {
        this(edit, form, FieldType.DOUBLE);
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
        fieldEdit_.setStyleClass("form-field-textfield-edit-invalid");
        limiterField_.setStyleClass("form-field-combobox-edit-invalid");
    }

    public void flagValid()
    {
        fieldEdit_.setStyleClass("form-field-textfield-edit-valid");
        limiterField_.setStyleClass("form-field-combobox-edit-valid");
    }

    public String getFormText() 
    {
        if(limiterField_.currentText().value().equals(""))
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
        switch(text.charAt(0))
        {
            case '<':
                limiterField_.setCurrentIndex(0);
            case '=':
                limiterField_.setCurrentIndex(1);
            case '>':
                limiterField_.setCurrentIndex(2);
        }
        fieldEdit_.setText(tmpText.substring(1).trim());
    }
}
