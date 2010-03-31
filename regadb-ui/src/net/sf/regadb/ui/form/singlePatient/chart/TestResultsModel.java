package net.sf.regadb.ui.form.singlePatient.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.util.date.DateUtils;
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
	
	public void loadResults(Patient p){
		int cols = 1 + series.size();
		insertColumns(0, cols);
		
		int col = 1;
		int row = 0;

		for(TestResultSeries s : series){
			s.setModelColumn(col);
			setHeaderData(col, s.getName());

			Map<Date, TestResult> results = s.loadResults(p);
			for(TestResult tr : results.values()){
				insertRow(row);
				setData(row, getXSeriesColumn(), new WDate(tr.getTestDate()));
				setData(row, col, s.getValue(tr));
//				System.out.println(row +","+ col +","+ DateUtils.format(tr.getTestDate()) +","+ s.getValue(tr));
				++row;
			}
			++col;
		}
	}
}
