package net.sf.regadb.ui.form.query.querytool.select;

import com.pharmadm.custom.rega.queryeditor.Field;
import com.pharmadm.custom.rega.queryeditor.FieldSelection;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.SelectionStatusList;
import com.pharmadm.custom.rega.queryeditor.TableSelection;
import com.pharmadm.custom.rega.savable.Savable;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WMouseEvent;

public class FieldSelectionContainer extends WContainerWidget {
	private FieldSelection selection;
	private TableSelection tableSelection;
	private WCheckBox checkBox;
	
	public FieldSelectionContainer(Savable savable, TableSelection tableSelection, FieldSelection selection) {
		super();
		this.selection = selection;
		this.tableSelection = tableSelection;
		init();
		checkBox.setEnabled(savable.isLoaded());
	}
	
	private void init() {
		checkBox = new WCheckBox(selection.getDbObject().getDescription(), this);
		checkBox.setChecked(selection.isSelected());
		checkBox.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				toggleFieldChecked();
			}
		});		
	}
	
	
	private void toggleFieldChecked() {
        ((SelectionStatusList)tableSelection.getController()).setSelected((OutputVariable)tableSelection.getObject(), (Field) selection.getObject(), checkBox.isChecked());
        
        
        
	}
}
