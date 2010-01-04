package net.sf.regadb.ui.form.singlePatient;

import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WTableCell;
import eu.webtoolkit.jwt.WText;

public class ViralIsolateResistanceTableLegend extends WTable{

	public ViralIsolateResistanceTableLegend(WContainerWidget parent) {
        super(parent);
        this.setStyleClass("datatable datatable-resistance datatable-resistance-legend");
        init();
    }
	
	protected void init(){
		addLegend("R","resistance-R","r");
		addLegend("I","resistance-I","i");
		addLegend("S","resistance-S","s");
		addLegend("N/A","resistance-NA","na");
		addLegend("Cannot interprete","resistance-X","x");
		addLegend("S / I / R","resistance-S resistance-remarks","remarks");
	}
	
	protected void addLegend(String sign, String styleClass, String description){
		int row = getRowCount();
		WTableCell cell = getElementAt(row,0);
		cell.addWidget(new WText(sign));
		cell.setStyleClass(styleClass);
		cell = getElementAt(row, 1);
		cell.addWidget(new WText(tr("form.viralIsolate.resistanceTable.legend."+ description)));
		cell.setStyleClass("explanation");
	}
}
