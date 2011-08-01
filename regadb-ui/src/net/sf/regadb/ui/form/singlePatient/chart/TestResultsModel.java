package net.sf.regadb.ui.form.singlePatient.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import eu.webtoolkit.jwt.WDate;
import eu.webtoolkit.jwt.WStandardItemModel;

public class TestResultsModel extends WStandardItemModel{
	private List<TestResultSeries> series = new ArrayList<TestResultSeries>();

	public TestResultsModel(){
		
	}
	
	public int getXSeriesColumn(){
		return 0;
	}
	
	public List<TestResultSeries> getSeries(){
		return series;
	}
	
	public void loadResults(Patient p, Date minDate, Date maxDate){
		int col = 1;
		int row = 0;

		insertColumn(0);
		
		for(TestResultSeries s : series){
			s.loadResults(p, minDate, maxDate);

			if(s instanceof LimitedValueSeries){
				LimitedValueSeries lvs = (LimitedValueSeries)s;
				row = fill(lvs.getCutOffSeries(), row, col++);
			}
			
			row = fill(s, row, col++);
		}
	}
	
	private int fill(TestResultSeries series, int row, int col){
		insertColumn(col);
		series.setModelColumn(col);
		setHeaderData(col, series.getName());
		
		for(TestResult tr : series.getResults().values()){
			insertRow(row);
			setData(row, getXSeriesColumn(), new WDate(tr.getTestDate()));
			setData(row, col, series.getValue(tr));
//			System.out.println(row +","+ col +","+ DateUtils.format(tr.getTestDate()) +","+ s.getValue(tr));
			++row;
		}
		return row;
	}
}
