package net.sf.regadb.io.db.ghb.merge;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class MergePatientAttributes extends MergeObjects {
	
	private static final int PATIENT_ID = 0;
	private static final int NAME = 1;
	private static final int VALUE = 2;
	private static final int NOMINAL = 3;
	
	private Map<String, Integer> attrIiMap;
	private Map<String, Integer> anvIiMap;

	@Override
	protected void createMaps() throws SQLException {
		attrIiMap = createAttrIiMap();
		anvIiMap = createAttributeNominalValueIiMap();
	}

	private Map<String,Integer> createAttrIiMap() throws SQLException{
		Map<String,Integer> map = new HashMap<String,Integer>();

		Statement s = db2.createStatement();
		String query = "select a.attribute_ii, a.name from regadbschema.attribute a";
		s.execute(query);
		
		ResultSet rs = s.getResultSet();
		while(rs.next()){
			String key = rs.getString("name");
			map.put(key,rs.getInt("attribute_ii"));
		}
		s.close();
		
		return map;
	}
	
	private Map<String,Integer> createAttributeNominalValueIiMap() throws SQLException{
		Map<String,Integer> map = new HashMap<String,Integer>();

		Statement s = db2.createStatement();
		String query = "select nominal_value_ii, n.value as nominal, a.name from regadbschema.attribute_nominal_value n" +
				" join regadbschema.attribute a using(attribute_ii)";
		s.execute(query);
		
		ResultSet rs = s.getResultSet();
		while(rs.next()){
			String key = rs.getString("nominal") + rs.getString("name");
			map.put(key,rs.getInt("nominal_value_ii"));
		}
		s.close();
		
		return map;
	}

	
	@Override
	protected void add(String patientId, String[] fields) throws Exception {
		String name = fields[NAME];
		String value = nullify(fields[VALUE]);
		String nominal = nullify(fields[NOMINAL]);
		
		Integer pii = getPatientIi(db2, patientId);
		Integer aii = attrIiMap.get(name);
		Integer nii = anvIiMap.get(nominal+name);
		
		PreparedStatement s = db2.prepareStatement("insert into regadbschema.patient_attribute_value"
				+" (version, patient_ii, attribute_ii, value, nominal_value_ii)"
				+" values (0, ?, ?, ?, ?)");
		
		int i = 0;
		s.setInt(++i, pii);
		s.setInt(++i, aii);
		s.setString(++i, value);
		setInteger(s, ++i, nii);
		
		s.execute();
		s.close();
	}

	@Override
	protected void remove(String patientId, String[] fields) throws Exception {
		String name = fields[NAME];
		String value = nullify(fields[VALUE]);
		String nominal = nullify(fields[NOMINAL]);
		
		Integer pii = getPatientIi(db2, patientId);
		Integer aii = attrIiMap.get(name);
		Integer nii = anvIiMap.get(nominal+name);

		PreparedStatement s = db2.prepareStatement("delete from regadbschema.patient_attribute_value where"
				+" patient_ii = "+ pii
				+" and attribute_ii = "+ aii
				+" and nominal_value_ii "+ nullifyCondition(nii)
				+" and value "+ nullifyCondition(value));
		
		s.execute();
		s.close();
	}
	
	public static void main(String args[]) throws Exception{
		MergePatientAttributes mpa = new MergePatientAttributes();
		run(mpa,args);
	}
}
