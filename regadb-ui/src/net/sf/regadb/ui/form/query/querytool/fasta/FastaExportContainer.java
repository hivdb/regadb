package net.sf.regadb.ui.form.query.querytool.fasta;

import net.sf.regadb.ui.form.query.querytool.QueryToolApp;
import net.sf.regadb.ui.form.query.querytool.QueryToolForm;

import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.SelectionListChangeListener;

import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WText;

public class FastaExportContainer extends WContainerWidget {
	private QueryToolApp mainForm;
	
	private WText warning;
	
	public FastaExportContainer(QueryToolForm mainForm) {
		super();
		this.mainForm = mainForm;
		this.setStyleClass("content");
		
		warning = new WText(tr("form.query.querytool.message.noViralIsolate"));
		warning.setStyleClass("warning");
		
		mainForm.getEditorModel().getQueryEditor().addSelectionListChangeListener(new SelectionListChangeListener() {
			public void listChanged() {
				setWidgets();
			}
		});
	}
	
	private void setWidgets() {
		this.clear();
		
		boolean hasViralIsolate = false;
		for (OutputVariable ov : mainForm.getEditorModel().getQueryEditor().getRootClause().getExportedOutputVariables())
			if (ov.getObject().getTableName().equals("ViralIsolate")) {
				hasViralIsolate = true;
				break;
			}
		
		if (!hasViralIsolate) {
			this.addWidget(warning);
		} else {
			
		}
	}
}
