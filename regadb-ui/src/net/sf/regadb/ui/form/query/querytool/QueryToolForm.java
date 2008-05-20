package net.sf.regadb.ui.form.query.querytool;



import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.QueryEditorComponent;
import com.pharmadm.custom.rega.queryeditor.WhereClause;
import com.thoughtworks.xstream.XStream;

import net.sf.regadb.db.QueryDefinition;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.form.query.querytool.select.SelectionGroupBox;
import net.sf.regadb.ui.form.query.querytool.tree.QueryEditorGroupBox;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextArea;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WKeyEvent;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

public class QueryToolForm extends FormWidget implements QueryContext{

	private QueryEditorGroupBox queryGroup_;
	private RunGroupBox runGroup_;
	
    private Label nameL;
    private TextField nameTF;
    private Label descriptionL;
    private TextArea descriptionTA;
    private Label creatorL;
    private TextField creatorTF;
    
    private QueryDefinition definition;
	
	public QueryToolForm(WMessage title, InteractionState istate) {
		super(title, istate);
		QueryDefinition query = new QueryDefinition(StandardObjects.getQueryToolQueryType());
		init(query);
	}
    
    
	public QueryToolForm(WMessage title, InteractionState istate, QueryDefinition query) {
		super(title, istate);
		init(query);
	}
	
    public WMessage leaveForm() {
        if(isEditable() && queryGroup_.getQueryEditor().isDirty()) {
            return tr("form.warning.stillEditing");
        } else {
            return null;
        }
    }	
    
    public RunGroupBox getExecuter() {
    	return runGroup_;
    }
    
	public void init(QueryDefinition query) {
		setStyleClass("querytoolform");
		definition = query;
		queryGroup_ = new QueryEditorGroupBox(tr("form.query.querytool.group.query"), this, query);
		new SelectionGroupBox(queryGroup_.getQueryEditor(), tr("form.query.querytool.group.fields"), this);
		runGroup_ = new RunGroupBox(queryGroup_.getQueryEditor(), this);
		
		WGroupBox infogroup_ = new WGroupBox(tr("form.query.querytool.group.info"), this);
		WContainerWidget contentPanel = new WContainerWidget(infogroup_);
		contentPanel.setStyleClass("content");
		infogroup_.setStyleClass("infofield");
		
		WTable infoTable = new WTable(contentPanel);
		
    	nameL = new Label(tr("form.query.definition.label.name"));
    	nameTF = new TextField(getInteractionState(), this);
    	nameTF.setText(definition.getName());
        nameTF.setMandatory(true);
        addLineToTable(infoTable, nameL, nameTF);
        infoTable.elementAt(0, 0).setStyleClass("labels");
        infoTable.elementAt(0, 1).setStyleClass("inputs");
        nameTF.keyPressed.addListener(new SignalListener<WKeyEvent>() {
			public void notify(WKeyEvent a) {
				queryGroup_.getQueryEditor().setDirty(true);
			}
        });
        
        descriptionL = new Label(tr("form.query.definition.label.description"));
        descriptionTA = new TextArea(getInteractionState(), this);
        descriptionTA.setMandatory(true);
        descriptionTA.setText(definition.getDescription());
        addLineToTable(infoTable, descriptionL, descriptionTA);
        descriptionTA.keyPressed.addListener(new SignalListener<WKeyEvent>() {
			public void notify(WKeyEvent a) {
				queryGroup_.getQueryEditor().setDirty(true);
			}
        });
		
        if(getInteractionState() == InteractionState.Viewing)
        {
        	creatorL = new Label(tr("form.query.definition.label.creator"));
            creatorTF = new TextField(getInteractionState(), this);
            addLineToTable(infoTable, creatorL, creatorTF);
            creatorTF.setText(definition.getSettingsUser().getUid());
        }	
        
        
        
		addControlButtons();
		if (!isEditable()) {
			queryGroup_.setEditable(false);
		}
	}
	
	


	public void cancel() {
		if(getInteractionState() == InteractionState.Adding)
		{
			redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryMain, RegaDBMain.getApp().getTree().getTreeContent().queryToolSelect);
		}
		else
		{
			redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryToolSelected, RegaDBMain.getApp().getTree().getTreeContent().queryToolSelectedView);
		}
		
	}

	public WMessage deleteObject() {
		Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
        t.delete(definition);
        t.commit();
        
        return null;
	}

	public void redirectAfterDelete() {
		RegaDBMain.getApp().getTree().getTreeContent().queryToolSelect.selectNode();
        RegaDBMain.getApp().getTree().getTreeContent().queryToolSelected.setSelectedItem(null);
	}

	public void saveData() {
		Transaction t = RegaDBMain.getApp().getLogin().createTransaction();
    	
    	definition.setName(nameTF.getFormText());
    	definition.setDescription(descriptionTA.getFormText());
    	definition.setQuery(new XStream().toXML(this.getEditorModel().getQueryEditor().getQuery()));
    	definition.setSettingsUser(t.getSettingsUser(RegaDBMain.getApp().getLogin().getUid()));
    	
    	update(definition, t);
    	
    	t.commit();
    	
    	RegaDBMain.getApp().getTree().getTreeContent().queryToolSelected.setSelectedItem(definition);
		redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryToolSelected, RegaDBMain.getApp().getTree().getTreeContent().queryToolSelectedView);
	}

	public WhereClause getContextClause() {
		return queryGroup_.getContextClause();
	}

	public QueryEditorComponent getEditorModel() {
		return queryGroup_;
	}
}
