package net.sf.regadb.ui.form.query.querytool.select;

import java.util.List;

import net.sf.regadb.ui.form.query.querytool.QueryToolApp;
import net.sf.regadb.ui.form.query.querytool.QueryToolForm;

import com.pharmadm.custom.rega.queryeditor.ComposedSelection;
import com.pharmadm.custom.rega.queryeditor.Selection;
import com.pharmadm.custom.rega.queryeditor.SelectionListChangeListener;
import com.pharmadm.custom.rega.queryeditor.SimpleSelection;
import com.pharmadm.custom.rega.queryeditor.TableSelection;

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
	
	private void updateSelection() {
		while(rootSelectorPanel.children().size() > 0) {
			rootSelectorPanel.removeWidget(rootSelectorPanel.children().get(0));
		}
		
		List<Selection> selections = mainForm.getEditorModel().getQueryEditor().getQuery().getSelectList().getSelections();
		for (Selection selection : selections) {
			if (selection instanceof ComposedSelection) {
				rootSelectorPanel.addWidget(new TableSelectionContainer(mainForm.getSavable(), (TableSelection) selection));
			}
			else {
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
