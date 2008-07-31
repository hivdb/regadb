package net.sf.regadb.ui.form.query.querytool;

import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextArea;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WKeyEvent;

public class InfoContainer extends WContainerWidget {
    private Label nameL;
    private TextField nameTF;
    private Label descriptionL;
    private TextArea descriptionTA;
    private Label creatorL;
    private TextField creatorTF;
	
	public InfoContainer(QueryToolApp mainForm, FormWidget form) {
		super();
		init(mainForm, form);
	}
	
	private void init(final QueryToolApp mainForm, FormWidget form) {
		setStyleClass("infofield");

		
		FormTable infoTable = new FormTable(this);
		
    	nameL = new Label(tr("general.name"));
    	nameTF = new TextField(form.getInteractionState(), form);
        nameTF.setMandatory(true);
        infoTable.addLineToTable(nameL, nameTF);
        nameTF.keyPressed.addListener(new SignalListener<WKeyEvent>() {
			public void notify(WKeyEvent a) {
				mainForm.getEditorModel().getQueryEditor().setDirty(true);
			}
        });
        
        descriptionL = new Label(tr("general.description"));
        descriptionTA = new TextArea(form.getInteractionState(), form);
        descriptionTA.setMandatory(true);
        infoTable.addLineToTable(descriptionL, descriptionTA);
        descriptionTA.keyPressed.addListener(new SignalListener<WKeyEvent>() {
			public void notify(WKeyEvent a) {
				mainForm.getEditorModel().getQueryEditor().setDirty(true);
			}
        });
		
        if(form.getInteractionState() == InteractionState.Viewing)
        {
        	creatorL = new Label(tr("query.definition.creator"));
            creatorTF = new TextField(form.getInteractionState(), form);
            infoTable.addLineToTable(creatorL, creatorTF);
        }	
	}
	
	public void setName(String name) {
		nameTF.setText(name);
	}
	
	public void setDescription(String description) {
		descriptionTA.setText(description);
	}
	
	public void setUser(String uid) {
		if (creatorTF != null) {
            creatorTF.setText(uid);
		}
	}
	
	public String getName() {
		return nameTF.getFormText();
	}
	
	public String getDescription() {
		return descriptionTA.getFormText();
	}

	public boolean isValid() {
		return nameTF.validate() && descriptionTA.validate();
	}
	
}
