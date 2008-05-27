package net.sf.regadb.ui.form.query.querytool;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextArea;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WKeyEvent;
import net.sf.witty.wt.WTable;

public class InfoContainer extends WContainerWidget {
    private Label nameL;
    private TextField nameTF;
    private Label descriptionL;
    private TextArea descriptionTA;
    private Label creatorL;
    private TextField creatorTF;
	
	public InfoContainer(QueryDefinition definition, QueryToolForm form) {
		super();
		init(definition, form);
	}
	
	private void init(QueryDefinition definition, final QueryToolForm form) {
		setStyleClass("infofield");
		
		WTable infoTable = new WTable(this);
		
    	nameL = new Label(tr("form.query.definition.label.name"));
    	nameTF = new TextField(form.getInteractionState(), form);
    	nameTF.setText(definition.getName());
        nameTF.setMandatory(true);
        infoTable.putElementAt(0, 0, nameL);
        infoTable.putElementAt(0, 1, nameTF);
        infoTable.elementAt(0, 0).setStyleClass("labels");
        infoTable.elementAt(0, 1).setStyleClass("inputs");
        nameTF.keyPressed.addListener(new SignalListener<WKeyEvent>() {
			public void notify(WKeyEvent a) {
				form.getEditorModel().getQueryEditor().setDirty(true);
			}
        });
        
        descriptionL = new Label(tr("form.query.definition.label.description"));
        descriptionTA = new TextArea(form.getInteractionState(), form);
        descriptionTA.setMandatory(true);
        descriptionTA.setText(definition.getDescription());
        infoTable.putElementAt(1, 0, descriptionL);
        infoTable.putElementAt(1, 1, descriptionTA);
        descriptionTA.keyPressed.addListener(new SignalListener<WKeyEvent>() {
			public void notify(WKeyEvent a) {
				form.getEditorModel().getQueryEditor().setDirty(true);
			}
        });
		
        if(form.getInteractionState() == InteractionState.Viewing)
        {
        	creatorL = new Label(tr("form.query.definition.label.creator"));
            creatorTF = new TextField(form.getInteractionState(), form);
            infoTable.putElementAt(1, 0, creatorL);
            infoTable.putElementAt(1, 1, creatorTF);
            creatorTF.setText(definition.getSettingsUser().getUid());
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
