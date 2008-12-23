package net.sf.regadb.ui.form.query.querytool.select;

import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.SelectionStatusList;
import com.pharmadm.custom.rega.queryeditor.SimpleSelection;
import com.pharmadm.custom.rega.savable.Savable;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WMouseEvent;

public class SimpleSelectionContainer extends WContainerWidget {
	private SimpleSelection selection;
	private WCheckBox checkBox;
	

	public SimpleSelectionContainer(Savable savable, SimpleSelection selection) {
		super();
		this.selection = selection;
		init();
		checkBox.setEnabled(savable.isLoaded());
	}
	
	private void init() {
		this.setStyleClass("selectionitem simpleselectionitem");
		
		OutputVariable ovar = (OutputVariable) selection.getObject();
		checkBox = new WCheckBox(lt(ovar.getUniqueName()), this);
		checkBox.setChecked(selection.isSelected());
		checkBox.clicked.addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				toggleFieldChecked();
			}
		});		
	}
	
	private void toggleFieldChecked() {
        ((SelectionStatusList)selection.getController()).setSelected((OutputVariable)selection.getObject(), checkBox.isChecked());
	}
}
