package net.sf.regadb.io.db.portugal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.db.tools.RunSqlCommand;

public class ChangeSchema {
	public static void main(String[] args) throws WrongUidException,
			WrongPasswordException, DisabledUserException, SQLException {
		RunSqlCommand runsqlcommmand = new RunSqlCommand();
		Connection c = runsqlcommmand.getConnection();
		Statement s = c.createStatement();
		ResultSet rs = s
				.executeQuery("select patient_ii, birth_date from regadbschema.patient;");
		int patient_ii;
		Date birth_date;

		Integer attribute_ii;
		try {
			attribute_ii = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			attribute_ii = null;
		}

		if(attribute_ii!=null)
		while (rs.next()) {
			patient_ii = rs.getInt(1);
			birth_date = rs.getDate(2);

			if (birth_date != null) {
				String insertStm = "INSERT INTO regadbschema.patient_attribute_value "
						+ "(version, attribute_ii, patient_ii, nominal_value_ii, value) "
						+ "VALUES (0, "
						+ attribute_ii
						+ ","
						+ patient_ii
						+ ", null, '" + birth_date.getTime() + "')";
				Statement ss = c.createStatement();
				System.err.println(insertStm);
				ss.execute(insertStm);
			}
		}
		s.close();
		
		changeTestAllPatients(args[1], args[2]);
	}

	public static void changeTestAllPatients(String testName, String eventName) throws WrongUidException, WrongPasswordException, DisabledUserException {
		Login login = Login.authenticate("admin", "resist");
		Transaction trans = login.createTransaction();
		List<Patient> patients = trans.getPatients();
		
		int counter = 0;
		for(Patient p : patients) {
			counter++;
			if(counter==500) {
				System.out.print(".");
				counter = 0;
			}
				
			testToEvent(p, trans.getTest(testName), trans.getEvent(eventName), trans);
		}
		trans.commit();
	}
	
	public static void testToEvent(Patient p, Test t, Event e, Transaction trans) {
		for (TestResult tr : p.getTestResults()) {
			if (Equals.isSameTestType(t.getTestType(), tr.getTest().getTestType())) {
				PatientEventValue pev = p.createPatientEventValue(e);
				pev.setStartDate(tr.getTestDate());
				pev.setEventNominalValue(getENV(e, tr.getTestNominalValue().getValue()));
				p.addPatientEventValue(pev);
				trans.save(pev);
			}
		}
	}
	
	public static EventNominalValue getENV(Event e, String tnv) {
		for(EventNominalValue env : e.getEventNominalValues()) {
			if(env.getValue().equals(tnv)) {
				return env;
			}
		}
		return null;
	}
}
