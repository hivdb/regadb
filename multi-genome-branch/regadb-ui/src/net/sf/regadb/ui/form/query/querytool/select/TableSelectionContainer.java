package net.sf.regadb.ui.form.query.querytool.select;

import com.pharmadm.custom.rega.queryeditor.FieldSelection;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.Selection;
import com.pharmadm.custom.rega.queryeditor.SelectionStatusList;
import com.pharmadm.custom.rega.queryeditor.TableSelection;
import com.pharmadm.custom.rega.savable.Savable;

import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.i8n.WMessage;

public class TableSelectionContainer extends WContainerWidget {
	private TableSelection selection;
	private WContainerWidget fieldsPanel;
	private WCheckBox tableCheckBox;

	public TableSelectionContainer(Savable savable, TableSelection selection) {
		super();
		this.selection = selection;
		init(savable);
	}
	
	private void init(Savable savable) {
		this.setStyleClass("selectionitem tableselectionitem");
		
		tableCheckBox = new WCheckBox(new WMessage(selection.getVariableName(), true), this);
		tableCheckBox.setChecked(selection.isSelected());
		tableCheckBox.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				toggleTableChecked();
			}
		});
		
		fieldsPanel = new WContainerWidget(this);
		toggleTableChecked();
		
		for (Selection subSelection : selection.getSubSelections()) {
			fieldsPanel.addWidget(new FieldSelectionContainer(savable, selection, (FieldSelection) subSelection));
		}
		
		tableCheckBox.setEnabled(savable.isLoaded());
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