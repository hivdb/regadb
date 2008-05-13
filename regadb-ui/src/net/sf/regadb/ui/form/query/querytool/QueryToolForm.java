package net.sf.regadb.ui.form.query.querytool;



import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.QueryEditorComponent;
import com.pharmadm.custom.rega.queryeditor.WhereClause;


import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.i8n.WMessage;

public class QueryToolForm extends FormWidget implements QueryContext{

	private QueryEditorGroupBox queryGroup_;

	

	
	public QueryToolForm() {
		super(tr("menu.query.querytool"), InteractionState.Viewing);
		setStyleClass("querytoolform");
		init();
	}
	
	public void init() {
		queryGroup_ = new QueryEditorGroupBox(new com.pharmadm.custom.rega.queryeditor.Query(), tr("form.query.querytool.group.query"), this);
		new SelectionGroupBox(queryGroup_.getQueryEditor(), tr("form.query.querytool.group.fields"), this);
		new RunGroupBox(queryGroup_.getQueryEditor(), this);
	}
	
	


	public void cancel() {
		// TODO Auto-generated method stub

	}

	public WMessage deleteObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public void redirectAfterDelete() {
		// TODO Auto-generated method stub

	}

	public void saveData() {
		// TODO Auto-generated method stub

	}

	public WhereClause getContextClause() {
		return queryGroup_.getQueryTree().getContextClause();
	}

	public QueryEditorComponent getEditorModel() {
		return queryGroup_;
	}
}
