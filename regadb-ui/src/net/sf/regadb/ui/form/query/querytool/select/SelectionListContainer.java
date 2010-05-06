package net.sf.regadb.ui.form.query.querytool.select;

import java.util.List;

import net.sf.regadb.ui.form.query.querytool.GSSExporter;
import net.sf.regadb.ui.form.query.querytool.QueryToolApp;
import net.sf.regadb.ui.form.query.querytool.QueryToolForm;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.InputVariable;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.Selection;
import com.pharmadm.custom.rega.queryeditor.SelectionListChangeListener;
import com.pharmadm.custom.rega.queryeditor.SimpleSelection;
import com.pharmadm.custom.rega.queryeditor.TableSelection;
import com.pharmadm.custom.rega.queryeditor.ExporterSelection;

import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WText;

public class SelectionListContainer extends WContainerWidget{
	private QueryToolApp mainForm;
	
	private WContainerWidget rootSelectorPanel;
	
	public SelectionListContainer(QueryToolForm mainForm) {
		super();
		this.mainForm = mainForm;
		init();
	}
	
	private void init() {
		this.setStyleClass("selectionfield");
		rootSelectorPanel = new WContainerWidget(this);
		rootSelectorPanel.setStyleClass("content");
		mainForm.getEditorModel().getQueryEditor().addSelectionListChangeListener(new SelectionListChangeListener() {
			public void listChanged() {
				updateSelection();
			}
		});
		
	}
	
	private String getInputVariableName(OutputVariable ovar){
		for(ConfigurableWord word : ovar.getExpression().getWords()){
			if(word instanceof InputVariable){
				InputVariable ivar = (InputVariable)word;
				if(ivar.getOutputVariable() != null)
					return ivar.getOutputVariable().getUniqueName();
			}
		}
		return null;
	}
	
	private void updateSelection() {
		while(rootSelectorPanel.getChildren().size() > 0) {
			rootSelectorPanel.removeWidget(rootSelectorPanel.getChildren().get(0));
		}
		
		List<Selection> selections = mainForm.getEditorModel().getQueryEditor().getQuery().getSelectList().getSelections();
		for (Selection selection : selections) {
			if (selection instanceof TableSelection) {
				rootSelectorPanel.addWidget(new TableSelectionContainer(mainForm.getSavable(), (TableSelection) selection));
			}
			else {
//				((QueryToolForm)mainForm).
				if(selection instanceof ExporterSelection
					&& (((ExporterSelection)selection).getDbObject().getDescription().startsWith("Genotypic Susceptibility Score")
					|| ((ExporterSelection)selection).getDbObject().getDescription().startsWith("Transmitted Drug Resistance"))) {
					ExporterSelection xsel = (ExporterSelection)selection;
					
					GSSExporter gss = (GSSExporter)xsel.getExporter();
					if(xsel.getExporter() == null){
						String variableName = getInputVariableName((OutputVariable)selection.getObjectSpec());
						gss = new GSSExporter(variableName);
						xsel.setExporter(gss);
					}
					((QueryToolForm)mainForm).exporters.put(gss.getVariableName(), gss);
					rootSelectorPanel.addWidget(new ExporterSelectionContainer(
							mainForm.getSavable(), xsel, gss));
				}
				else
					rootSelectorPanel.addWidget(new SimpleSelectionContainer(mainForm.getSavable(), (SimpleSelection) selection));
			}
		}
		
		if (selections.isEmpty()) {
			WText warning;
			if (mainForm.getSavable().isLoaded()) {
				warning = new WText(tr("form.query.querytool.message.nofields"));
				warning.setStyleClass("warning");
			}
			else {
				warning = new WText(tr("form.query.querytool.message.selectionunverifiable"));
				warning.setStyleClass("warning");
			}
			rootSelectorPanel.addWidget(warning);
		}
	}	
}
