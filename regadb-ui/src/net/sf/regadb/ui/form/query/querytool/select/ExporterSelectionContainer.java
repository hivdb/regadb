package net.sf.regadb.ui.form.query.querytool.select;


import com.pharmadm.custom.rega.queryeditor.FieldExporter;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.ExporterSelection;
import com.pharmadm.custom.rega.savable.Savable;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WBreak;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WMouseEvent;

public class ExporterSelectionContainer extends WContainerWidget{
	private WContainerWidget fieldsPanel;
	private WCheckBox checkbox;
	private WCheckBox[] checkboxes;
	private FieldExporter exporter;
	private ExporterSelection selection;

	public ExporterSelectionContainer(Savable savable, ExporterSelection selection, FieldExporter exporter) {
		this.exporter = exporter;
		this.selection = selection;
		init(savable);
	}

	private void init(Savable savable){
		this.setStyleClass("selectionitem tableselectionitem");
		
		checkbox = new WCheckBox(((OutputVariable)selection.getObject()).getUniqueName(), this);
		checkbox.setChecked(selection.isSelected());
		checkbox.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				toggleTableChecked();
			}
		});

		
		fieldsPanel = new WContainerWidget(this);
		checkboxes = new WCheckBox[exporter.getColumns().length];
		for(int i=0; i<exporter.getColumns().length; ++i){
			checkboxes[i] = addField(exporter.getColumn(i), i);
		}
	}
	
	private WCheckBox addField(String name, final int i){
		WCheckBox c = new WCheckBox(name);
		c.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				exporter.setSelected(i, checkboxes[i].isChecked());
			}
		});
		
		fieldsPanel.addWidget(c);
		fieldsPanel.addWidget(new WBreak());
		return c;
	}
	
	protected void toggleTableChecked(){
		selection.setSelected(checkbox.isChecked());
		if(checkbox.isChecked())
			fieldsPanel.show();
		else
			fieldsPanel.hide();
	}
}
