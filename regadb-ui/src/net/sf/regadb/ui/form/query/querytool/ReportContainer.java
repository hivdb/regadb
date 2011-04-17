package net.sf.regadb.ui.form.query.querytool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import net.sf.regadb.csv.Table;
import eu.webtoolkit.jwt.Orientation;
import eu.webtoolkit.jwt.Side;
import eu.webtoolkit.jwt.WAbstractItemModel;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WStandardItemModel;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WTreeView;
import eu.webtoolkit.jwt.chart.LabelOption;
import eu.webtoolkit.jwt.chart.WPieChart;

public class ReportContainer extends WContainerWidget {
	public ReportContainer(File summaryCsvFile) {
		super();
		try {
			Table csv = Table.readTable(summaryCsvFile.getAbsolutePath());
			if (csv.numColumns() == 2) {
				initPieChart(csv);
			} 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void initPieChart(Table csv) {
        WAbstractItemModel model = readFromCsv(csv);

        WTreeView table = new WTreeView(this);
        table.setRootIsDecorated(false);
        table.setMargin(10, Side.Top, Side.Bottom);
        table.setMargin(WLength.Auto, Side.Left, Side.Right);
        table.resize(300, 100);
        table.setModel(model);

        WPieChart chart = new WPieChart(this);
        chart.setDisplayLabels(LabelOption.NoLabels);
        chart.setModel(model); 
        chart.setLabelsColumn(0); 
        chart.setDataColumn(1);

        chart.setPerspectiveEnabled(true, 0.2);

        chart.resize(400, 300);

        chart.setMargin(10, Side.Top, Side.Bottom); 
        chart.setMargin(WLength.Auto, Side.Left, Side.Right);
        
        WTable legend = new WTable(this);
        legend.setStyleClass("querytoolform-report-legend");
        
        for (int i = 0; i < model.getRowCount(); i++) {
        	legend.getElementAt(i, 0).addWidget(chart.createLegendItemWidget(i, LabelOption.TextLabel, LabelOption.TextPercentage));
        }
	}
	
    public WAbstractItemModel readFromCsv(Table csv) {
    	WAbstractItemModel model = new WStandardItemModel();
		for (int r = 0; r < csv.numRows(); r++) {
			if (r != 0)
				model.insertRow(r - 1);

			for (int c = 0; c < csv.numColumns(); c++) {
				String value = csv.valueAt(c, r);
				if (r == 0) {
					model.insertColumn(c);
					model.setHeaderData(c, Orientation.Horizontal, value);
				} else {
					try {
						Double d = Double.valueOf(value);
						model.setData(r - 1, c, d);
					} catch (NumberFormatException e) {
						model.setData(r - 1, c, value);
					}
				}
			}
		}
        return model;
    }
}
