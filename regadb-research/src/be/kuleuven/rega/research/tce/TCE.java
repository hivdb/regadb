package be.kuleuven.rega.research.tce;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.hivgensim.queries.framework.utils.DateUtils;
import net.sf.hivgensim.queries.framework.utils.TestUtils;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;

/**
 * Treatment Change Episode
 * 
 * @author plibin0
 */
public class TCE {
	
	private Date startDate;
	private List<Therapy> therapiesBefore = new ArrayList<Therapy>();
	private List<DrugGeneric> drugs = new ArrayList<DrugGeneric>();
	private Patient patient;
	
	public TCE() {
		
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public List<Therapy> getTherapiesBefore() {
		return therapiesBefore;
	}

	public void setTherapiesBefore(List<Therapy> therapiesBefore) {
		this.therapiesBefore = therapiesBefore;
	}

	public List<DrugGeneric> getDrugs() {
		return drugs;
	}

	public void setDrugs(List<DrugGeneric> drugs) {
		this.drugs = drugs;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public TestResult getTestResultBetweenInterval(Date d, int daysBefore, int daysAfter, TestType testType) {
		List<TestResult> trs = TestUtils.filterTestResults(getPatient().getTestResults(), testType);
		List<TestResult> trs_interval = new ArrayList<TestResult>();
		for(TestResult tr_i : trs) {
			if(DateUtils.betweenInterval(tr_i.getTestDate(), DateUtils.addDaysToDate(d, daysBefore), DateUtils.addDaysToDate(d, daysAfter))) {
				trs_interval.add(tr_i);
			}
		}

		return TestUtils.closestToDate(getStartDate(), trs_interval);
	}
	
	public TestResult getTestResultBetweenInterval(int daysBefore, int daysAfter, TestType testType) {
		return getTestResultBetweenInterval(getStartDate(), daysBefore, daysAfter, testType);
	}
}
