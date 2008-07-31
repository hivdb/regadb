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
		showMessage(tr("message.query.ok"), "info");
		showProgressBar(CatalogLoader.getInstance());
	}
	
	protected void loadingComplete() {
		super.loadingComplete();
		mainForm.updateControls();		
	}
	

	public void update() {
			catalogOk = true;
			if (CatalogLoader.getStatus() == Status.FAILED) {
				showMessage(tr("message.query.querytool.catalogfailed"), "error");
				catalogOk = false;
			}
			else if(!mainForm.getSavable().isLoaded()) {
				showMessage(tr("message.query.querytool.loadfailed"), "error");
			}
			else {
				boolean hasWarning = true;
				Query query = mainForm.getEditorModel().getQueryEditor().getQuery();
		        if (!query.isValid()) {
		        	showMessage(tr("message.query.querytool.unassigned"), "error");
		        }
		        else if (!query.hasFromVariables()) {
		        	showMessage(tr("message.query.querytool.noselection"), "warning");
		        }
		        else if (!query.getSelectList().isAnythingSelected()) {
		        	showMessage(tr("message.query.querytool.emptyselection"), "error");
		        }
		        else if (!query.isConnected()) {
		        	showMessage(tr("message.query.querytool.cartesianproduct"), "error");
		        }
		        else {
		        	showMessage(tr("message.query.ok"), "info");
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
