package net.sf.regadb.ui.form.query.querytool.select;

import com.pharmadm.custom.rega.queryeditor.Field;
import com.pharmadm.custom.rega.queryeditor.FieldSelection;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.SelectionStatusList;
import com.pharmadm.custom.rega.queryeditor.TableSelection;

import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.i8n.WMessage;

public class FieldSelectionContainer extends WContainerWidget {
	private FieldSelection selection;
	private TableSelection tableSelection;
	private WCheckBox checkBox;
	
	public FieldSelectionContainer(TableSelection tableSelection, FieldSelection selection) {
		super();
		this.selection = selection;
		this.tableSelection = tableSelection;
		init();
	}
	
	private void init() {
//        Field field = (Field) selection.getObject();
		checkBox = new WCheckBox(new WMessage(selection.getDbObject().getDescription(), true), this);
		checkBox.setChecked(selection.isSelected());
		checkBox.changed.addListener(new SignalListener<WEmptyEvent>() {
			public void notify(WEmptyEvent a) {
				toggleFieldChecked();
			}
		});		
	}
	
	
	private void toggleFieldChecked() {
        ((SelectionStatusList)tableSelection.getController()).setSelected((OutputVariable)tableSelection.getObject(), (Field) selection.getObject(), checkBox.isChecked());
        
        
        
	}
}
