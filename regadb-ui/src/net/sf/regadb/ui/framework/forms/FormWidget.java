package net.sf.regadb.ui.framework.forms;

import java.util.ArrayList;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.validation.WFormValidation;
import net.sf.witty.wt.core.utils.WHorizontalAlignment;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.SignalListener;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WGroupBox;
import net.sf.witty.wt.widgets.WPushButton;
import net.sf.witty.wt.widgets.WTable;
import net.sf.witty.wt.widgets.event.WMouseEvent;

public abstract class FormWidget extends WGroupBox implements IForm
{
    private ArrayList<IFormField> formFields_ = new ArrayList<IFormField>();
    
    private WFormValidation formValidation_ = new WFormValidation();
    
    private InteractionState interactionState_;
    
    //control buttons
    private WPushButton _okButton = new WPushButton(tr("form.general.button.ok"));
    private WPushButton _cancelButton = new WPushButton(tr("form.general.button.cancel"));
    private WPushButton _helpButton = new WPushButton(tr("form.general.button.help"));
    
    public FormWidget(WMessage formName, InteractionState interactionState)
	{
        super(formName);
        interactionState_ = interactionState;
        formValidation_.init(this);
	}
    
    public boolean canStore(String toStore)
    {
        return toStore!=null && !toStore.equals("");
    }
	
	public WContainerWidget getWContainer()
	{
		return this;
	}

	public void addFormField(IFormField field)
	{
        formFields_.add(field);
	}
	
    public void addLineToTable(WTable table, Label label, IFormField field)
    {
        int numRows = table.numRows();
        table.putElementAt(numRows, 0, label);
        table.putElementAt(numRows, 1, field.getWidget());
        label.setBuddy(field);
    }

	public InteractionState getInteractionState()
	{
		return interactionState_;
	}
	
	public boolean isEditable()
	{
		return interactionState_== InteractionState.Adding || interactionState_== InteractionState.Editing; 
	}
	
    public void addControlButtons()
    {
        WContainerWidget buttonContainer = new WContainerWidget(this);
        buttonContainer.addWidget(_okButton);
        _okButton.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        if(formValidation_.validate(formFields_))
                        {
                            formValidation_.setHidden(true);
                            saveData();
                        }
                        else
                        {
                            formValidation_.setHidden(false);
                        }
                    }
                });
        buttonContainer.addWidget(_cancelButton);
        buttonContainer.addWidget(_helpButton);
        buttonContainer.setContentAlignment(WHorizontalAlignment.AlignRight);
        if(!isEditable())
        {
            _okButton.setEnabled(false);
            _cancelButton.setEnabled(false);
        }
    }
    
    public abstract void saveData();
}
