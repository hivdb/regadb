package be.kuleuven.rega.research.tce;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;

/**
 * Therapy Change Event
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
}
