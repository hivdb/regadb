package net.sf.regadb.io.db.portugal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;

import org.hibernate.Query;

public class FixMonths {
	//Some error was introduced in the import of the EgazMoniz db
	//This scripts fixes the error on the live db
	public static void main(String [] args) {
		try {
			Login login = Login.authenticate("admin", "admin");
			
			SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
			
			Table t = new Table(new FileInputStream(args[0]), false);
			int counter = 0;
			for(int i = 9; i<t.numRows(); i++) {
				int sampleId = Integer.parseInt(t.valueAt(1, i));
				String patientId = t.valueAt(2, i);
				String year = t.valueAt(4, i);
				String month = t.valueAt(5, i);
				String fixMonth = null;
				if(month.equals("0") && sampleId!=0) {
					if(sampleId<139512) {
						for(int j = i; true; j--) {
							if(!t.valueAt(5, j).equals("0")) {
								fixMonth = t.valueAt(5, j);
								break;
							}
						}
					} else {
						fixMonth = "1";
					}
					if(fixMonth==null) {
						System.err.println("WRONG");
						System.exit(0);
					} else {
						Transaction transaction = login.createTransaction();
						Query q = transaction.createQuery("select new net.sf.regadb.db.Patient(patient, 3) from PatientImpl as patient where patient.patientId = :param");
						q.setParameter("param", patientId);
						List l = q.list();
						if(l.size()>1 || l.size()==0) {
							System.err.println("WRONG");
							System.exit(0);
						}
						Patient p = (Patient)l.get(0);
						for(TestResult tr : p.getTestResults()) {
							if(tr.getSampleId().equals(sampleId+"")) {
								try {
									tr.setTestDate(DATE_FORMAT.parse("01-"+fixMonth+"-"+year));
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}
						}
						
						for(ViralIsolate vi : p.getViralIsolates()) {
							if(vi.getSampleId().equals(sampleId+"")) {
								try {
									vi.setSampleDate(DATE_FORMAT.parse("01-"+fixMonth+"-"+year));
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						
						transaction.save(p);
						transaction.commit();
					}
				}
				
			}
			
			System.err.println(counter);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (WrongUidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongPasswordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DisabledUserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
