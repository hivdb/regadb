package net.sf.regadb.io.db.ghb.merge;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;

public class UpdateDatabase {
	private Login login;

	public static void main(String args[]){
		try {
			UpdateDatabase udb = new UpdateDatabase("admin","admin");
			udb.run();
		} catch (WrongUidException e) {
			e.printStackTrace();
		} catch (WrongPasswordException e) {
			e.printStackTrace();
		} catch (DisabledUserException e) {
			e.printStackTrace();
		}
	}
	
	public UpdateDatabase(String user, String password) throws WrongUidException, WrongPasswordException, DisabledUserException {
    	this(Login.authenticate(user, password));
	}
    	
	public UpdateDatabase(Login login) throws WrongUidException, WrongPasswordException, DisabledUserException {
    	setLogin(login);
    }

	private void setLogin(Login login) {
		this.login = login;
	}

	public Login getLogin() {
		return login;
	}
	
	public void run(){
		Transaction t = login.createTransaction();
		HibernateFilterConstraint hfc = new HibernateFilterConstraint();
		hfc.clause_ = "patient.id = :patientId";
		
		Patient from = t.getPatient(40);
		Patient to 	 = t.getPatient(91);
		mergePatients(from, to);
		
		t.commit();
	}
	
	private void mergePatients(Patient from, Patient to){
		moveAttributeValues(from, to);
		moveEventValues(from, to);
		from.getTestResults().clear();
		moveViralIsolates(from, to);
		moveTherapies(from, to);
	}
	
	private void moveAttributeValues(Patient from, Patient to){
		Set<Integer> indices = new TreeSet<Integer>();		
		for(PatientAttributeValue pav : to.getPatientAttributeValues())
			indices.add(pav.getAttribute().getAttributeIi());
		
		Iterator<PatientAttributeValue> i = from.getPatientAttributeValues().iterator();
		while(i.hasNext()){
			PatientAttributeValue pav = i.next();
			if(!indices.contains(pav.getAttribute().getAttributeIi())){
				i.remove();
				to.addPatientAttributeValue(pav);
			}
		}
	}
	private void moveEventValues(Patient from, Patient to){
		Set<Integer> indices = new TreeSet<Integer>();		
		for(PatientEventValue pev : to.getPatientEventValues())
			indices.add(pev.getEvent().getEventIi());
		
		Iterator<PatientEventValue> i = from.getPatientEventValues().iterator();
		while(i.hasNext()){
			PatientEventValue pev = i.next();
			if(!indices.contains(pev.getEvent().getEventIi())){
				i.remove();
				to.addPatientEventValue(pev);
			}
		}
	}
	private void moveTestResults(Patient from, Patient to){
		Iterator<TestResult> i = from.getTestResults().iterator();
		while(i.hasNext()){
			TestResult tr = i.next();
			i.remove();
			to.addTestResult(tr);
		}
	}
	private void moveViralIsolates(Patient from, Patient to){
		Set<String> sampleIds = new TreeSet<String>();
		for(ViralIsolate vi : to.getViralIsolates())
			sampleIds.add(vi.getSampleId());
		
		Iterator<ViralIsolate> i = from.getViralIsolates().iterator();
		while(i.hasNext()){
			ViralIsolate vi = i.next();
			if(!sampleIds.contains(vi.getSampleId())){
				i.remove();
				to.addViralIsolate(vi);
			}
		}
	}
	private void moveTherapies(Patient from, Patient to){
		Iterator<Therapy> i = from.getTherapies().iterator();
		while(i.hasNext()){
			Therapy t = i.next();
			i.remove();
			to.addTherapy(t);
		}
	}
}
