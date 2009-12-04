package net.sf.regadb.io.db.saturn;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.tools.RunSqlCommand;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ChangeSchema {
	public static void main(String[] args) throws WrongUidException,
			WrongPasswordException, DisabledUserException, SQLException {
		RegaDBSettings.createInstance();
		handleBirthDeathDate();
		
		handleDuplicateAttributes();
	}
	
	private static void handleDuplicateAttributes() throws SQLException {
		RunSqlCommand runsqlcommmand = new RunSqlCommand();
		Connection c = runsqlcommmand.getConnection();
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("select attribute_group_ii from regadbschema.attribute_group where group_name = 'RegaDB'");
		rs.next();
		int group_ii = rs.getInt(1);
		rs = s.executeQuery("select name, attribute_ii from regadbschema.attribute where attribute_group_ii = " + group_ii);
		Map<String, Integer> attributesInRegaDBGroup = new HashMap<String, Integer>();
		while (rs.next()) {
			String name = rs.getString(1);
			int attribute_ii = rs.getInt(2);
			attributesInRegaDBGroup.put(name, attribute_ii);
		}
		for (Map.Entry<String, Integer> e : attributesInRegaDBGroup.entrySet()) {
			rs = s.executeQuery("select attribute_ii from regadbschema.attribute where name = '" + e.getKey() + "' and attribute_group_ii != " + group_ii);
			if (rs.next()) {
				int new_attribute_ii = rs.getInt(1);
				handleDuplicateAttribute(e.getValue(), new_attribute_ii);
			}
		}
	}
	
	private static void handleDuplicateAttribute(int original_attribute_ii, int new_attribute_ii) throws SQLException {
		Map<Integer, Integer> anvMappings = new HashMap<Integer, Integer>();
		
		RunSqlCommand runsqlcommmand = new RunSqlCommand();
		Connection c = runsqlcommmand.getConnection();
		Connection c2 = runsqlcommmand.getConnection();
		Statement s = c.createStatement();
		Statement s2 = c2.createStatement();
		ResultSet rs = s.executeQuery("select nominal_value_ii, value from regadbschema.attribute_nominal_value where attribute_ii = " + original_attribute_ii);		
		while (rs.next()) {
			int anv_ii = rs.getInt(1);
			String value = rs.getString(2);
			String sql = "select nominal_value_ii from regadbschema.attribute_nominal_value where attribute_ii = " + new_attribute_ii + " and value = \'" + value.replace("'", "''") + "\'";
			ResultSet rs2 = s2.executeQuery(sql);
			rs2.next();
			anvMappings.put(anv_ii, rs2.getInt(1));
		}
		
		rs = s.executeQuery("select patient_attribute_value_ii, nominal_value_ii from regadbschema.patient_attribute_value where attribute_ii = " + original_attribute_ii);
		while (rs.next()) {
			int pav_ii = rs.getInt(1);
			int nv_ii = rs.getInt(2);
			s2.executeUpdate("update regadbschema.patient_attribute_value set nominal_value_ii = " + anvMappings.get(nv_ii) + " where patient_attribute_value_ii = " + pav_ii);
			s2.executeUpdate("update regadbschema.patient_attribute_value set attribute_ii = " + new_attribute_ii + " where patient_attribute_value_ii = " + pav_ii);
		}
		s.execute("delete from regadbschema.attribute_nominal_value where attribute_ii = " + original_attribute_ii);
		s.execute("delete from regadbschema.attribute where attribute_ii = " + original_attribute_ii);
	}
	
	private static void handleBirthDeathDate() throws SQLException {
		RunSqlCommand runsqlcommmand = new RunSqlCommand();
		Connection c = runsqlcommmand.getConnection();
		Statement s = c.createStatement();
		int birth_date_attribute_ii;
		ResultSet rs = s.executeQuery("select attribute_ii from regadbschema.attribute where name='Birth date'");
		rs.next();
		birth_date_attribute_ii = rs.getInt(1);
		int death_date_attribute_ii;
		rs = s.executeQuery("select attribute_ii from regadbschema.attribute where name='Death date'");
		rs.next();
		death_date_attribute_ii = rs.getInt(1);
		
		rs = s.executeQuery("select patient_ii, birth_date, death_date from regadbschema.patient;");
		int patient_ii;
		Date birth_date;
		Date death_date;

		while (rs.next()) {
			patient_ii = rs.getInt(1);
			birth_date = rs.getDate(2);
			death_date = rs.getDate(3);

			if (birth_date != null) {
				storeDate(birth_date_attribute_ii, patient_ii, birth_date, c);
			}
			if (death_date != null) {
				storeDate(death_date_attribute_ii, patient_ii, death_date, c);
			}
		}
		s.close();
	}
	
	private static void storeDate(int attribute_ii, int patient_ii, Date date, Connection c) throws SQLException {
		String insertStm = "INSERT INTO regadbschema.patient_attribute_value "
			+ "(version, attribute_ii, patient_ii, nominal_value_ii, value) "
			+ "VALUES (0, "
			+ attribute_ii
			+ ","
			+ patient_ii
			+ ", null, '" + date.getTime() + "')";
		Statement ss = c.createStatement();
		System.err.println(insertStm);
		ss.execute(insertStm);
	}
}
