package net.sf.regadb.ui.form.query.querytool;



import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.QueryEditorComponent;
import com.pharmadm.custom.rega.queryeditor.WhereClause;

import net.sf.regadb.db.QueryDefinition;
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
	
	
	/**
	 * add a new Query
	 */
	public QueryToolForm(WMessage title, InteractionState istate) {
		super(title, istate);
		init(null);
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
        }	
        
        
        
		addControlButtons();
		if (!isEditable()) {
			queryGroup_.setEditable(false);
		}
	}
	
	


	public void cancel() {
		if(getInteractionState() == InteractionState.Adding)
		{
			redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryMain, RegaDBMain.getApp().getTree().getTreeContent().queryWiv);
		}
	}

	public WMessage deleteObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public void redirectAfterDelete() {
		// TODO Auto-generated method stub

	}

	public void saveData() {
		if(getInteractionState() == InteractionState.Adding)
		{
			redirectToView(RegaDBMain.getApp().getTree().getTreeContent().queryMain, RegaDBMain.getApp().getTree().getTreeContent().queryWiv);
		}
	}

	public WhereClause getContextClause() {
		return queryGroup_.getContextClause();
	}

	public QueryEditorComponent getEditorModel() {
		return queryGroup_;
	}
}
