package net.sf.regadb.ui.form.query.querytool.fasta;

import net.sf.regadb.ui.form.query.querytool.QueryToolForm;

import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.SelectionListChangeListener;

import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WText;

public class FastaExportContainer extends WContainerWidget {
	private QueryToolForm mainForm;
	
	private WText warning;
	private FastaExportOptions options;
	
	public FastaExportContainer(QueryToolForm mainForm) {
		super();
		this.mainForm = mainForm;
		this.setStyleClass("content");
		
		warning = new WText(tr("form.query.querytool.fastaExport.noViralIsolate"));
		warning.setStyleClass("warning");
		addWidget(warning);
		
		mainForm.getEditorModel().getQueryEditor().addSelectionListChangeListener(new SelectionListChangeListener() {
			public void listChanged() {
				setWidgets();
				update();
			}
		});
	}
	
	private void setWidgets() {
		boolean hasViralIsolate = false;
		for (OutputVariable ov : mainForm.getEditorModel().getQueryEditor().getRootClause().getExportedOutputVariables())
			if (ov.getObject().getTableName().equals("ViralIsolate")) {
				hasViralIsolate = true;
				break;
			}
		
		if (!hasViralIsolate) {
			warning.setHidden(false);
			if (options != null)
				options.setHidden(true);
		} else {
			warning.setHidden(true);
			if (options == null) {
				options = new FastaExportOptions(mainForm, this,  (QTFastaExporter)mainForm.getEditorModel().getQueryEditor().getQuery().getFastaExport());
				addWidget(options);
			}
			options.setHidden(false);
		}
	}
	
	private void update() {
		if (options != null)
			options.updateOutputVars();
	}
	
	public QTFastaExporter getFastaExporter() {
		if (options != null)
			return options.getFastaExporter();
		else 
			return null;
	}
}
