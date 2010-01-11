package net.sf.regadb.ui.form.query.querytool.tree;



import net.sf.regadb.ui.form.query.querytool.QueryToolApp;
import net.sf.regadb.ui.form.query.querytool.QueryToolForm;
import net.sf.regadb.ui.form.query.querytool.buttons.RunButtonPanel;
import net.sf.regadb.ui.form.query.querytool.widgets.WButtonPanel;
import net.sf.regadb.ui.form.query.querytool.widgets.WStatusBar;

import com.pharmadm.custom.rega.queryeditor.Query;

public class QueryStatusBar extends WStatusBar {
	private QueryToolApp mainForm;
	private WButtonPanel runButtonPanel;
	
	// are there warnings about the query's validity
	private boolean hasWarning;

	public QueryStatusBar(QueryToolForm mainForm) {
		super();
		init(mainForm);
	}
	
	private void init(final QueryToolForm mainForm) {
		this.mainForm = mainForm;
		runButtonPanel = new RunButtonPanel(mainForm);
		setButtonPanel(runButtonPanel);
		showMessage(tr("form.query.querytool.message.ok"), "warning");
	}
	
	protected void loadingComplete() {
		super.loadingComplete();
		mainForm.updateControls();		
	}
	

	public void update() {
		if (!mainForm.getSavable().isLoaded()) {
			showMessage(tr("form.query.querytool.message.loadfailed"), "error");
		} else {
			boolean hasWarning = true;
			Query query = mainForm.getEditorModel().getQueryEditor().getQuery();
			if (!query.isValid()) {
				showMessage(tr("form.query.querytool.message.unassigned"),
						"error");
			} else if (!query.hasFromVariables()) {
				showMessage(tr("form.query.querytool.message.noselection"),
						"warning");
			} else if (!query.getSelectList().isAnythingSelected()) {
				showMessage(tr("form.query.querytool.message.emptyselection"),
						"error");
			} else if (!query.isConnected()) {
				showMessage(
						tr("form.query.querytool.message.cartesianproduct"),
						"error");
			} else {
				showMessage(tr("form.query.querytool.message.ok"), "info");
				hasWarning = false;
			}
			this.hasWarning = hasWarning;
		}
			
			// debug query display
//			try {
//				showMessage(lt(mainForm.getEditorModel().getQueryEditor().getQuery().getQueryString()), "warning");
//			} catch (SQLException e) {}
			
		updateRunButton();
	}	
	
	private void updateRunButton() {
        if (!hasWarning && mainForm.getSavable().isLoaded()) {
        	runButtonPanel.setEnabled(true);
        }
        else {
        	runButtonPanel.setEnabled(false);
        }		
	}
}
