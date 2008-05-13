package net.sf.regadb.ui.form.query.querytool;

import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.SelectionStatusList;
import com.pharmadm.custom.rega.queryeditor.SimpleSelection;

import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.i8n.WMessage;

public class SimpleSelectionContainer extends WContainerWidget {
	private SimpleSelection selection;
	private WCheckBox checkBox;
	

	public SimpleSelectionContainer(SimpleSelection selection) {
		super();
		this.selection = selection;
		init();
	}
	
	private void init() {
		this.setStyleClass("selectionitem simpleselectionitem");
		
		OutputVariable ovar = (OutputVariable) selection.getObject();
		checkBox = new WCheckBox(new WMessage(ovar.getUniqueName(), true), this);
		checkBox.setChecked(selection.isSelected());
		checkBox.changed.addListener(new SignalListener<WEmptyEvent>() {
			public void notify(WEmptyEvent a) {
				toggleFieldChecked();
			}
		});		
	}
	
	private void toggleFieldChecked() {
        ((SelectionStatusList)selection.getController()).setSelected((OutputVariable)selection.getObject(), checkBox.isChecked());
	}
}
