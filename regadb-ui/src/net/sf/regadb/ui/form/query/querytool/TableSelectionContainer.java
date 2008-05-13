package net.sf.regadb.ui.form.query.querytool;

import com.pharmadm.custom.rega.queryeditor.FieldSelection;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.Selection;
import com.pharmadm.custom.rega.queryeditor.SelectionStatusList;
import com.pharmadm.custom.rega.queryeditor.TableSelection;

import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.i8n.WMessage;

public class TableSelectionContainer extends WContainerWidget {
	private TableSelection selection;
	private WContainerWidget fieldsPanel;
	private WCheckBox tableCheckBox;
	

	public TableSelectionContainer(TableSelection selection) {
		super();
		this.selection = selection;
		init();
	}
	
	private void init() {
		this.setStyleClass("selectionitem tableselectionitem");
		
		tableCheckBox = new WCheckBox(new WMessage(selection.getVariableName(), true), this);
		tableCheckBox.setChecked(selection.isSelected());
		tableCheckBox.changed.addListener(new SignalListener<WEmptyEvent>() {
			public void notify(WEmptyEvent a) {
				toggleTableChecked();
			}
		});
		
		fieldsPanel = new WContainerWidget(this);
		toggleTableChecked();
		
		for (Selection subSelection : selection.getSubSelections()) {
			fieldsPanel.addWidget(new FieldSelectionContainer(selection, (FieldSelection) subSelection));
		}
	}
	
	private void toggleTableChecked() {
        ((SelectionStatusList) selection.getController()).setSelected((OutputVariable)selection.getObject(), tableCheckBox.isChecked());
		if (tableCheckBox.isChecked()) {
			fieldsPanel.show();
		}
		else {
			fieldsPanel.hide();
		}
	}
}
