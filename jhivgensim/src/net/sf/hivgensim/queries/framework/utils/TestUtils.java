package net.sf.hivgensim.queries.framework.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.meta.Equals;

public class TestUtils {
	
	public static final Comparator<TestResult> testResultSortComparator = new Comparator<TestResult>(){
		@Override
		public int compare(TestResult arg0, TestResult arg1) {
			return arg0.getTestDate().compareTo(arg1.getTestDate());
		}
	};

	public static List<TestResult> filterTestResults(Collection<TestResult> trs, TestType testType) {
		List<TestResult> filteredTestResults = new ArrayList<TestResult>();

		for(TestResult tr : trs) {
			if(Equals.isSameTestType(tr.getTest().getTestType(), testType) && tr.getTestDate()!=null) {
				filteredTestResults.add(tr);
			}
		}

		return filteredTestResults;
	}
	
	public static TestResult closestToDate(Date d, List<TestResult> testResults) {
		long min = Long.MAX_VALUE;
		TestResult closest = null;

		if(testResults.size()==0)
			return null;

		long diff;
		for(TestResult tr : testResults) {
			diff = Math.abs(tr.getTestDate().getTime()-d.getTime());
			if(diff<min) {
				min = diff;
				closest = tr;
			}
		}

		return closest;
	}

}
