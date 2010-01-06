package net.sf.regadb.ui.form.singlePatient;

import java.awt.Color;

import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.regadb.util.settings.ViralIsolateFormConfig;
import net.sf.regadb.util.settings.ViralIsolateFormConfig.ScoreInfo;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;

public class ViralIsolateResistanceLegend extends WContainerWidget {
	private WTable table;
	public ViralIsolateResistanceLegend(WContainerWidget parent) {
		super(parent);
		
		this.setMargin(20, Side.Left, Side.Top);
		
		WText t = new WText(tr("form.viralIsolate.editView.resistance.legend"), this);
		t.setFloatSide(Side.Left);
		table = new WTable(this);
		
		table.setStyleClass("resistance-legend");

		renderLegend();
	}
	
	private void renderLegend() {
		table.clear();
        
		ViralIsolateFormConfig config = 
        	RegaDBSettings.getInstance().getInstituteConfig().getViralIsolateFormConfig();
		
		int row = 0;
		WText na = new WText("");
		na.setText("NA");
		table.getElementAt(row, 0).getDecorationStyle().setForegroundColor(ViralIsolateFormUtils.convert(Color.white));
		table.getElementAt(row, 0).getDecorationStyle().setBackgroundColor(ViralIsolateFormUtils.convert(Color.black));
		table.getElementAt(row, 0).addWidget(na);
		table.getElementAt(row, 0).setStyleClass("item");
		table.getElementAt(row, 1).addWidget(new WText("No result available"));
		
		row++;
		
		for (ScoreInfo si : config.getScoreInfos()) {
			WText t = new WText(table.getElementAt(row, 0));
			table.getElementAt(row, 0).setStyleClass("item");
	        t.setText(si.getStringRepresentation());
	        t.getParent().getDecorationStyle().setForegroundColor(ViralIsolateFormUtils.convert(si.getColor()));
	        t.getParent().getDecorationStyle().setBackgroundColor(ViralIsolateFormUtils.convert(si.getBackgroundColor()));
	        
	        t = new WText(table.getElementAt(row, 1));
	        
	        t.setText(si.getDescription());
	        
	        row++;
		}
		
		table.getElementAt(row, 0).addWidget(new WText("*"));
		table.getElementAt(row, 0).setStyleClass("item");
		table.getElementAt(row, 1).addWidget(new WText("Assumptions"));
	}
}
