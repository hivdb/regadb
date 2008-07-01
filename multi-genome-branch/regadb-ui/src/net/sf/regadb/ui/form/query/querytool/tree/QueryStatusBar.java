package net.sf.regadb.ui.form.query.querytool.tree;


import com.pharmadm.custom.rega.queryeditor.Query;
import com.pharmadm.custom.rega.queryeditor.catalog.AWCPrototypeCatalog.Status;

import net.sf.regadb.ui.form.query.querytool.CatalogLoader;
import net.sf.regadb.ui.form.query.querytool.QueryToolApp;
import net.sf.regadb.ui.form.query.querytool.QueryToolForm;
import net.sf.regadb.ui.form.query.querytool.buttons.RunButtonPanel;
import net.sf.regadb.ui.form.query.querytool.widgets.WButtonPanel;
import net.sf.regadb.ui.form.query.querytool.widgets.WStatusBar;

public class QueryStatusBar extends WStatusBar {
	private QueryToolApp mainForm;
	private WButtonPanel runButtonPanel;
	
	// are there warnings about the query's validity
	private boolean hasWarning;

	// is catalog loaded
	private boolean catalogOk;
	
	public QueryStatusBar(QueryToolForm mainForm) {
		super();
		init(mainForm);
	}
	
	private void init(final QueryToolForm mainForm) {
		this.mainForm = mainForm;
		runButtonPanel = new RunButtonPanel(mainForm);
		setButtonPanel(runButtonPanel);
		showProgressBar(CatalogLoader.getInstance());
	}
	
	protected void loadingComplete() {
		super.loadingComplete();
		mainForm.updateControls();		
	}
	

	public void update() {
			catalogOk = true;
			if (CatalogLoader.getStatus() == Status.FAILED) {
				showMessage("form.query.querytool.catalog.failed", "error");
				catalogOk = false;
			}
			else if(!mainForm.getSavable().isLoaded()) {
				showMessage("form.query.querytool.message.loadfailed", "error");
			}
			else {
				boolean hasWarning = true;
				Query query =mainForm.getEditorModel().getQueryEditor().getQuery();
		        if (!query.isValid()) {
		        	showMessage("form.query.querytool.message.unassigned", "error");
		        }
		        else if (!query.hasFromVariables()) {
		        	showMessage("form.query.querytool.message.noselection", "warning");
		        }
		        else if (!query.getSelectList().isAnythingSelected()) {
		        	showMessage("form.query.querytool.message.emptyselection", "error");
		        }
		        else {
		        	showMessage("form.query.querytool.message.ok", "info");
		        	hasWarning = false;
		        }
				this.hasWarning = hasWarning;
			}
		updateRunButton();
	}	
	
	private void updateRunButton() {
        if (!hasWarning && catalogOk && mainForm.getSavable().isLoaded()) {
        	runButtonPanel.setEnabled(true);
        }
        else {
        	runButtonPanel.setEnabled(false);
        }		
	}

	public boolean isCatalogLoaded() {
		return CatalogLoader.getStatus() == Status.DONE;
	}	
}
