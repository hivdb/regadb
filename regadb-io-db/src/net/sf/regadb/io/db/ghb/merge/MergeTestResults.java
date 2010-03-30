package net.sf.regadb.io.db.ghb.merge;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class MergeTestResults extends MergeObjects{

	private Map<String,Integer> testIiMap;
	private Map<String,Integer> tnvIiMap;

	@Override
	protected void createMaps() throws SQLException {
		testIiMap = createTestIiMap();
		tnvIiMap = createTestNominalValueIiMap();
	}	

	private Map<String,Integer> createTestIiMap() throws SQLException{
		Map<String,Integer> map = new HashMap<String,Integer>();

		Statement s = db2.createStatement();
		String query = "select t.test_ii, t.description, tt.description, g.organism_name from regadbschema.test t" +
				" join regadbschema.test_type tt using(test_ii) left outer join regadbschema.genome g using(genome_ii)";
		s.execute(query);
		
		ResultSet rs = s.getResultSet();
		while(rs.next()){
			String key = rs.getString("t.description") + rs.getString("tt.description") + rs.getString("g.organism_name");
			map.put(key,rs.getInt("t.test_ii"));
		}
		s.close();
		
		return map;
	}
	
	private Map<String,Integer> createTestNominalValueIiMap() throws SQLException{
		Map<String,Integer> map = new HashMap<String,Integer>();

		Statement s = db2.createStatement();
		String query = "select n.nominal_value_ii, n.value, tt.description, g.organism_name from regadbschema.test_nominal_value n" +
				" join regadbschema.test_type tt using(test_ii) left outer join regadbschema.genome g using(genome_ii)";
		s.execute(query);
		
		ResultSet rs = s.getResultSet();
		while(rs.next()){
			String key = rs.getString("n.value") + rs.getString("tt.description") + rs.getString("g.organism_name");
			map.put(key,rs.getInt("n.nominal_value_ii"));
		}
		s.close();
		
		return map;
	}

	@Override
	protected void add(String patientId, String[] fields) throws Exception {
		String test = fields[1];
		String testtype = fields[2];
		String genome = nullify(fields[3]);
		String testdate = fields[4];
		String sampleid = nullify(fields[5]);
		String value = nullify(fields[6]);
		String nominal = nullify(fields,7);

		Integer pii = getPatientIi(db2, patientId);
		Integer tii = testIiMap.get(test+testtype+genome);
		Integer nii = tnvIiMap.get(nominal+testtype+genome);
		
		PreparedStatement s = db2.prepareStatement("insert into regadbschema.test_result" +
				" (version, patient_ii, test_ii, test_date, sample_id, value, nominal_value_ii)" +
				" values (0, ?, ?, ?, ?, ?, ?)");
		int i = 1;
		s.setInt(i++, pii);
		s.setInt(i++, tii);
		s.setString(i++, testdate);
		setString(s, i++, sampleid);
		setString(s, i++, value);
		setInteger(s, i++, nii);
		
		s.execute();
		s.close();
	}

	@Override
	protected void remove(String patientId, String[] fields) throws Exception {
		String test = fields[1];
		String testtype = fields[2];
		String genome = nullify(fields[3]);
		String testdate = fields[4];
		String sampleid = nullify(fields[5]);
		String value = nullify(fields[6]);
		String nominal = nullify(fields,7);

		Integer pii = getPatientIi(db2, patientId);
		Integer tii = testIiMap.get(test+testtype+genome);
		Integer nii = tnvIiMap.get(nominal+testtype+genome);
		
		Statement s = db2.createStatement();
		s.executeUpdate("delete from regadbschema.test_result where" +
				" patient_ii = "+ pii +
				" and test_ii = "+ tii +
				" and test_date = '"+ testdate +"'"+
				" and sample_id "+ nullifyCondition(sampleid) + 
				" and value "+  nullifyCondition(value) +
				" and nominal_value_ii "+ nullifyCondition(nii));
		s.close();
	}

	public static void main(String args[]) throws Exception{
		MergeTestResults mt = new MergeTestResults();
		run(mt,args);
	}
}
