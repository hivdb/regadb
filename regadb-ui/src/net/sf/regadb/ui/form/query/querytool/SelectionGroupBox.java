package net.sf.regadb.ui.form.query.querytool;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.ComposedSelection;
import com.pharmadm.custom.rega.queryeditor.QueryEditor;
import com.pharmadm.custom.rega.queryeditor.Selection;
import com.pharmadm.custom.rega.queryeditor.SelectionListChangeListener;
import com.pharmadm.custom.rega.queryeditor.SimpleSelection;
import com.pharmadm.custom.rega.queryeditor.TableSelection;

import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.i8n.WMessage;

public class SelectionGroupBox extends WGroupBox{
	private QueryEditor editor;
	
	private WContainerWidget rootSelectorPanel;
	
	public SelectionGroupBox(QueryEditor editor, WMessage title,  WContainerWidget parent) {
		super(title, parent);
		this.editor = editor;
		init();
		updateSelection();
	}
	
	private void init() {
		this.setStyleClass("selectionfield");
		rootSelectorPanel = new WContainerWidget(this);
		rootSelectorPanel.setStyleClass("content");
		editor.addSelectionListChangeListener(new SelectionListChangeListener() {
			public void listChanged() {
				updateSelection();
			}
		});
		
	}
	
	private void updateSelection() {
		rootSelectorPanel.clear();
		
		List<Selection> selections = editor.getQuery().getSelectList().getSelections();
		for (Selection selection : selections) {
			if (selection instanceof ComposedSelection) {
				rootSelectorPanel.addWidget(new TableSelectionContainer((TableSelection) selection));
			}
			else {
				rootSelectorPanel.addWidget(new SimpleSelectionContainer((SimpleSelection) selection));
			}
		}
		
		if (selections.isEmpty()) {
			WText warning = new WText(new WMessage("form.query.querytool.message.nofields"));
			warning.setStyleClass("warning");
			rootSelectorPanel.addWidget(warning);
		}
	}	
}
