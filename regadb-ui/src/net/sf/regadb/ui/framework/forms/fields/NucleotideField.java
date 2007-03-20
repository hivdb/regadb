package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.core.utils.WVerticalAlignment;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.SignalListener;
import net.sf.witty.wt.widgets.WCheckBox;
import net.sf.witty.wt.widgets.WFont;
import net.sf.witty.wt.widgets.WFontGenericFamily;
import net.sf.witty.wt.widgets.WFormWidget;
import net.sf.witty.wt.widgets.WPushButton;
import net.sf.witty.wt.widgets.WTable;
import net.sf.witty.wt.widgets.WTextArea;
import net.sf.witty.wt.widgets.event.WEmptyEvent;

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
            _fieldEdit.setColumns(70);
            _fieldEdit.setRows(15);
            WTable ntFileTable = new WTable(this);
            ntFileTable.putElementAt(0, 0, _fieldEdit);
            WTable buttonTable = new WTable(ntFileTable.elementAt(0, 1));
            autoFix_ = new WCheckBox(tr("formfield.ntfield.checkbox.autofixSequence"), buttonTable.elementAt(0, 0));
            uploadFasta_ = new WPushButton(tr("formfield.ntfield.button.uploadFastaFile"), buttonTable.elementAt(1, 0));
            ntFileTable.elementAt(0, 1).setVerticalAlignment(WVerticalAlignment.AlignBottom);
            
            /*addWidget(_fieldEdit);*/
            flagValid();
            _fieldEdit.setValidator(new WNucleotideValidator());
        }
        else
        {
            initViewWidget();
            getViewWidget().decorationStyle().setFont(new WFont(WFontGenericFamily.Monospace , "Courier"));
        }
        
        form.addFormField(this);
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

    public void setFormText(String text) 
    {
       _fieldEdit.setText(createLinesFromText("\n", text));
    }
    
    public String getFormText()
    {
        return replaceAllPatterns(_fieldEdit.text(), "\n", "");
    }
    
    @Override
    protected void setViewMessage(WMessage message)
    {
        super.setViewMessage(lt(createLinesFromText("<br>", message.value())));
    }
    
    @Override
    protected WMessage getViewMessage()
    {
        return lt(replaceAllPatterns(super.getViewMessage().value(), "<br>", ""));
    }
    
    public static String replaceAllPatterns(String str, String pattern, String replace) 
    {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();
    
        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e+pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }
    
    private String createLinesFromText(String endOfLine, String text)
    {
        final int charsInLine = 69;
        
        StringBuffer newsb = new StringBuffer(text.length()+(int)(text.length()/charsInLine));
        int index = 0;
        int untill;
        while(index < text.length())
        {
            untill = index + charsInLine;
            try
            {
                newsb.append(text.substring(index, untill));
                newsb.append(endOfLine);
                index = untill;
            }
            catch(StringIndexOutOfBoundsException sioobe)
            {
                if(index>=text.length())
                {
                    break;
                }
                else
                {
                    newsb.append(text.substring(index, text.length()));
                    newsb.append(endOfLine);
                    break;
                }
            }
        }
        
        return newsb.toString();
    }
    
    public void addChangeListener(SignalListener<WEmptyEvent> listener)
    {
        if(_fieldEdit!=null)
        {
            _fieldEdit.changed.addListener(listener);
        }
    }
}
