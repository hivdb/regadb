package net.sf.regadb.io.db.portugal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
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

	public static void changeTestAllPatients(String testName, String eventName) throws WrongUidException, WrongPasswordException, DisabledUserException, SQLException {
		Login login = Login.authenticate("admin", "resist");
		Transaction trans = login.createTransaction();
		Test t = trans.getTest(testName);
		Event e = trans.getEvent(eventName);
		if(t==null || e==null) {
			System.err.println("Error");
			System.exit(0);
		}
		
		Map<Integer, Integer> nvMap = new HashMap<Integer, Integer>(); 
		
		for(TestNominalValue tnv : t.getTestType().getTestNominalValues()) {
			for(EventNominalValue env : e.getEventNominalValues()) {
				if(tnv.getValue().equals(env.getValue())) {
					nvMap.put(tnv.getNominalValueIi(), env.getNominalValueIi());
				}
			}
		}
		
		String fetchTestResultsQ = "select patient_ii, nominal_value_ii, test_date from regadbschema.test_result where test_ii = "+t.getTestIi()+";";
		RunSqlCommand runsqlcommmand = new RunSqlCommand();
		Connection c = runsqlcommmand.getConnection();
		Statement s = c.createStatement();
		ResultSet rs = s
				.executeQuery(fetchTestResultsQ);
		
		int patient_ii;
		int nominal_value_ii;
		Date test_date;
		while (rs.next()) {
			patient_ii = rs.getInt(1);
			nominal_value_ii = rs.getInt(2);
			test_date = rs.getDate(3);
			String insertStm = "insert into regadbschema.patient_event_value (version, patient_ii, nominal_value_ii, event_ii, value, start_date, end_date) " +
					" values (0, "+patient_ii+", "+nvMap.get(nominal_value_ii)+", "+e.getEventIi()+", null, ? , null)";
			
			if(test_date != null) {
				PreparedStatement st = c.prepareStatement(insertStm);
				st.setDate(1, new java.sql.Date(test_date.getTime()));
				st.execute();
			}
		}

	}
}
