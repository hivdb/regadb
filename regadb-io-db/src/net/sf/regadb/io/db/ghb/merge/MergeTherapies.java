package net.sf.regadb.io.db.ghb.merge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.util.args.Argument;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

public class MergeTherapies {
	@SuppressWarnings("serial")
	public static class NoPatientException extends Exception{
		public NoPatientException(String patientId){
			super("Patient ID does not exist: '"+ patientId +"'");
		}
	}
	
	@SuppressWarnings("serial")
	public static class DuplicateTherapyException extends Exception{
		public DuplicateTherapyException(String patientId, int therapyIi, String startDate, String stopDate){
			super("Duplicate therapy for patient '"+ patientId +"': "+ therapyIi +" "+ startDate +" - "+ (stopDate == null ? "...":stopDate));
		}
	}
	
	private Connection db1,db2;
	
	private Map<Integer,Integer> motivationIiMap;
	private Map<Integer,Integer> genericIiMap;
	private Map<Integer,Integer> commercialIiMap;
	
	private Set<String> handledPatients = new HashSet<String>();

	public static void main(String args[]) throws Exception{
		Arguments as = new Arguments();
		ValueArgument confDir = as.addValueArgument("c", "conf-dir", false);
		PositionalArgument diff = as.addPositionalArgument("diff-file", true);

		PositionalArgument db1 = as.addPositionalArgument("db1", true);
		PositionalArgument u1 = as.addPositionalArgument("user1", true);
		PositionalArgument p1 = as.addPositionalArgument("pass1", true);

		PositionalArgument db2 = as.addPositionalArgument("db2", true);
		PositionalArgument u2 = as.addPositionalArgument("user2", true);
		PositionalArgument p2 = as.addPositionalArgument("pass2", true);
		
		Argument all = as.addArgument("copy-all", false);
		
		if(!as.handle(args))
			return;
		
		if(confDir.isSet())
			RegaDBSettings.createInstance(confDir.getValue());
		else
			RegaDBSettings.createInstance();
		
		MergeTherapies mt = new MergeTherapies(db1.getValue(), u1.getValue(), p1.getValue(),
				db2.getValue(), u2.getValue(), p2.getValue());
		mt.run(new File(diff.getValue()), all.isSet());
	}
	
	public MergeTherapies(String db1, String u1, String p1,
			String db2, String u2, String p2) throws ClassNotFoundException, SQLException{
		
		Class.forName(RegaDBSettings.getInstance().getHibernateConfig().getDriverClass());
        this.db1 = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+ db1, u1, p1);
        this.db2 = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+ db2, u2, p2);
        
        createIiIiMaps();
	}
	
	public void run(File diffFile, boolean copyAll) throws IOException, SQLException{
		BufferedReader diff = new BufferedReader(new FileReader(diffFile));
		String line;
		while((line = diff.readLine()) != null){
			if(line.startsWith("<")){
				//remove
				String a[] = line.substring(2).split(",");
				String patientId = fixPatientId(a[0]); 
				remove(patientId, a[1].trim(), a.length > 2 && a[2].trim().length() > 0 ? a[2].trim():null);
			}
		}
		diff.close();
		
		diff = new BufferedReader(new FileReader(diffFile));
		while((line = diff.readLine()) != null){
			if(line.startsWith(">")){
				//add
				String a[] = line.substring(2).split(",");
				try {
					String patientId = a[0].trim();
					if(!copyAll)
						copy(patientId, a[1].trim(), a.length > 2 && a[2].trim().length() > 0 ? a[2].trim():null);
					else if(handledPatients.add(patientId)){
						removeAll(patientId);
						copyAll(patientId);
					}
				} catch (NoPatientException e) {
					System.err.println(e.getMessage());
				} catch (DuplicateTherapyException e) {
					System.err.println(e.getMessage());
				}
			}
		}
		diff.close();
	}
	
	private String fixPatientId(String patientId){
		patientId = patientId.trim();
		patientId = patientId.replaceAll("[^0-9]", "");
		return patientId;
	}
	
	private void remove(String patientId, String startDate, String stopDate) throws SQLException{
		Statement s = db2.createStatement();
		ResultSet rs = s.executeQuery("select therapy_ii from regadbschema.therapy join regadbschema.patient using(patient_ii)" +
				" where patient_id = '"+ patientId +"'"+
				" and start_date = '"+startDate+"'"+
				" and stop_date "+ ( stopDate == null?"is null":"= '"+stopDate+"'"));
		while(rs.next()){
			int therapyIi = rs.getInt("therapy_ii");
			Statement s2 = db2.createStatement();
			s2.executeUpdate("delete from regadbschema.therapy_commercial where therapy_ii = "+ therapyIi);
			s2.executeUpdate("delete from regadbschema.therapy_generic where therapy_ii = "+ therapyIi);
			s2.executeUpdate("delete from regadbschema.therapy where therapy_ii = "+ therapyIi);
			s2.close();
		}
		s.close();
	}
	
	private void removeAll(String patientId) throws SQLException{
		int patientIi = getPatientIi(db2, patientId);
		if(patientIi != -1){
			Statement s = db2.createStatement();
			String subquery = "(select therapy_ii from regadbschema.therapy where patient_ii = "+ patientIi +")";
			s.executeUpdate("delete from regadbschema.therapy_commercial where therapy_ii in "+ subquery);
			s.executeUpdate("delete from regadbschema.therapy_generic where therapy_ii in "+ subquery);
			s.executeUpdate("delete from regadbschema.therapy where therapy_ii in "+ subquery);
			s.close();
		}
	}
	
	private void copyAll(String patientId) throws SQLException{
		int patientIi1 = getPatientIi(db1, patientId);
		int patientIi2 = getPatientIi(db2, patientId);
		
		if(patientIi1 != -1 && patientIi2 != -1){
			Statement s = db1.createStatement();
			ResultSet rs = s.executeQuery("select therapy_ii from regadbschema.therapy where patient_ii = "+ patientIi1);
			while(rs.next()){
				int therapyIi1 = rs.getInt("therapy_ii");
				copyTherapy(therapyIi1, patientIi2);
			}
			s.close();
		}
	}
	
	private void copy(String patientId, String startDate, String stopDate) throws SQLException, NoPatientException, DuplicateTherapyException{
		int therapyIi1 = getTherapyIi(db1, patientId, startDate, stopDate);
		if(therapyIi1 > 0){
			int patientIi = getPatientIi(db2, fixPatientId(patientId));
			
			if(patientIi == -1)
				throw new NoPatientException(patientId);

			int therapyIi2 = getTherapyIi(db2, patientId, startDate, stopDate);
			if(therapyIi2 != -1)
				throw new DuplicateTherapyException(patientId, therapyIi2, startDate, stopDate);

			copyTherapy(therapyIi1, patientIi);
		}
	}
	
	private void copyTherapy(int therapyIi1, int patientIi2) throws SQLException{
		Statement s = db1.createStatement();
		ResultSet rs = s.executeQuery("select * from regadbschema.therapy where therapy_ii = "+ therapyIi1);
		if(rs.next()){
			int therapyIi2 = createTherapy(db2,
					motivationIiMap.get(rs.getInt("therapy_motivation_ii")),
					patientIi2,
					rs.getString("start_date"),
					rs.getString("stop_date"),
					rs.getString("comment"));
			
			rs = s.executeQuery("select * from regadbschema.therapy_commercial where therapy_ii = "+ therapyIi1);
			while(rs.next()){
				createTherapyCommercial(db2,
						therapyIi2,
						commercialIiMap.get(rs.getInt("commercial_ii")),
						rs.getDouble("day_dosage_units"),
						rs.getBoolean("placebo"),
						rs.getBoolean("blind"),
						rs.getLong("frequency"));
			}
			
			rs = s.executeQuery("select * from regadbschema.therapy_generic where therapy_ii = "+ therapyIi1);
			while(rs.next()){
				createTherapyGeneric(db2,
						therapyIi2,
						genericIiMap.get(rs.getInt("generic_ii")),
						rs.getDouble("day_dosage_mg"),
						rs.getBoolean("placebo"),
						rs.getBoolean("blind"),
						rs.getLong("frequency"));
			}
			
			s.close();
		}
	}
	
	private int createTherapy(Connection db, Integer motivationIi, Integer patientIi, String startDate, String stopDate, String comment) throws SQLException{
		Statement s = db.createStatement();
		s.executeUpdate("insert into regadbschema.therapy (version,therapy_motivation_ii,patient_ii,start_date,stop_date,comment) values ("+
				"0"+
				","+ motivationIi +
				","+ patientIi +
				",'"+ startDate +"'"+
				","+ getSqlString(stopDate)+
				","+ getSqlString(comment)+
				")");
		int ii = -1;
		ResultSet rs = s.executeQuery("select last_value from regadbschema.therapy_therapy_ii_seq");
		if(rs.next())
			ii = rs.getInt(1);
		s.close();
		return ii;
	}
	
	private void createTherapyCommercial(Connection db, Integer therapyIi, Integer commercialIi, Double units, boolean placebo, boolean blind, Long freq) throws SQLException{
		Statement s = db.createStatement();
		s.executeUpdate("insert into regadbschema.therapy_commercial (therapy_ii,commercial_ii,version,day_dosage_units,placebo,blind,frequency) values("+
				therapyIi +
				","+ commercialIi +
				",0"+
				","+ units +
				","+ placebo +
				","+ blind +
				","+ freq +
				")");
		s.close();
	}
	
	private void createTherapyGeneric(Connection db, Integer therapyIi, Integer genericIi, Double mg, boolean placebo, boolean blind, Long freq) throws SQLException{
		Statement s = db.createStatement();
		s.executeUpdate("insert into regadbschema.therapy_generic (therapy_ii,generic_ii,version,day_dosage_mg,placebo,blind,frequency) values("+
				therapyIi +
				","+ genericIi +
				",0"+
				","+ mg +
				","+ placebo +
				","+ blind +
				","+ freq +
				")");
		s.close();
	}

	
	private String getSqlString(Object o){
		if(o == null)
			return "NULL";
		return "'"+ o.toString().replace("'", "''") +"'";
	}
	
	private int getIi(Connection db, String table, String iiColumn, String searchColumn, String searchValue) throws SQLException{
		Statement s = db.createStatement();
		ResultSet rs = s.executeQuery("select "+ iiColumn +" from regadbschema."+ table +
				" where "+ searchColumn + (searchValue == null ? "is null":"= '"+ searchValue +"'")+
				" limit 1");
		int ii = -1;
		if(rs.next())
			ii = rs.getInt(iiColumn);
		s.close();
		return ii;
	}
	
	private int getTherapyIi(Connection db, String patientId, String startDate, String stopDate) throws SQLException{
		Statement s = db.createStatement();
		ResultSet rs = s.executeQuery("select therapy_ii from regadbschema.therapy join regadbschema.patient using(patient_ii)" +
				" where patient_id = '"+ patientId +"'"+
				" and start_date = '"+startDate+"'"+
				" and stop_date "+ ( stopDate == null?"is null":"= '"+stopDate+"'"));
		int therapyIi = -1;
		if(rs.next())
			therapyIi = rs.getInt("therapy_ii");
		s.close();
		return therapyIi;
	}
	
	private int getPatientIi(Connection db, String patientId) throws SQLException{
		return getIi(db, "patient", "patient_ii", "patient_id", patientId);
	}
	
	private void createIiIiMaps() throws SQLException{
		motivationIiMap = createIiIiMap(db1,db2,"therapy_motivation","therapy_motivation_ii","value");
		commercialIiMap = createIiIiMap(db1,db2,"drug_commercial","commercial_ii","name");
		genericIiMap = createIiIiMap(db1,db2,"drug_generic","generic_ii","generic_id");
	}
	
	private Map<Integer,Integer> createIiIiMap(Connection db1, Connection db2, String table, String iiColumn, String valueColumn) throws SQLException{
		Map<String,Integer> iivalue1 = createValueIiMap(db1,table,iiColumn,valueColumn);
		Map<String,Integer> iivalue2 = createValueIiMap(db2,table,iiColumn,valueColumn);
		Map<Integer,Integer> iiMap = new HashMap<Integer,Integer>();
		for(Map.Entry<String, Integer> me : iivalue1.entrySet())
			iiMap.put(me.getValue(), iivalue2.get(me.getKey()));
		return iiMap;
	}
	
	private Map<String,Integer> createValueIiMap(Connection db, String table, String iiColumn, String valueColumn) throws SQLException{
		Map<String,Integer> map = new HashMap<String,Integer>();
		
		Statement s = db.createStatement();
		ResultSet rs = s.executeQuery("select "+ iiColumn +","+ valueColumn +" from regadbschema."+ table);
		while(rs.next())
			map.put(rs.getString(valueColumn),rs.getInt(iiColumn));
		
		s.close();
		return map;
	}
}
