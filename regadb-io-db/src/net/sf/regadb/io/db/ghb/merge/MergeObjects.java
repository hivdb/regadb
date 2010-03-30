package net.sf.regadb.io.db.ghb.merge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

public abstract class MergeObjects {
	@SuppressWarnings("serial")
	public static class NoPatientException extends Exception{
		public NoPatientException(String patientId){
			super("Patient ID does not exist: '"+ patientId +"'");
		}
	}
	
	protected Connection db1,db2;
	
	public void init(String db1, String u1, String p1,
			String db2, String u2, String p2) throws ClassNotFoundException, SQLException{
		
		Class.forName(RegaDBSettings.getInstance().getHibernateConfig().getDriverClass());
        this.db1 = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+ db1, u1, p1);
        this.db2 = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+ db2, u2, p2);
        createMaps();
	}
	
	protected abstract void createMaps() throws SQLException; 
	
	public void run(File diffFile) throws IOException, SQLException{
		BufferedReader diff = new BufferedReader(new FileReader(diffFile));
		String line;
		while((line = diff.readLine()) != null){
			if(line.startsWith("<")){
				//remove
				String a[] = line.substring(2).split(",",-1);
				String patientId = fixPatientId(a[0]); 
				try {
					remove(patientId, a);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		diff.close();
		
		diff = new BufferedReader(new FileReader(diffFile));
		while((line = diff.readLine()) != null){
			if(line.startsWith(">")){
				//add
				String a[] = line.substring(2).split(",",-1);
				String patientId = a[0].trim();
				try {
					add(patientId,a);
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
		diff.close();
	}
	
	protected abstract void remove(String patientId, String fields[]) throws Exception;
	protected abstract void add(String patientId, String fields[]) throws Exception;
	
	protected String fixPatientId(String patientId){
		patientId = patientId.trim();
		patientId = patientId.replaceAll("[^0-9]", "");
		return patientId;
	}
	
	protected String getSqlString(Object o){
		if(o == null)
			return "NULL";
		return "'"+ o.toString().replace("'", "''") +"'";
	}
	
	protected String nullify(String s){
		if(s == null)
			return s;
		s = s.trim();
		return s.length() == 0 ? null : s;
	}
	
	protected String nullify(String s[], int i){
		if(s == null)
			return null;
		return i < s.length ? nullify(s[i]) : null;
	}
	
	protected String nullifyCondition(String s){
		return s == null ? " is null" : "="+ getSqlString(s);
	}
	protected String nullifyCondition(Integer i){
		return i == null ? " is null" : "="+ i;
	}
	
	protected void setString(PreparedStatement ps, int i, String s) throws SQLException{
		if(s == null)
			ps.setNull(i, Types.VARCHAR);
		else
			ps.setString(i, s);
	}
	
	protected void setInteger(PreparedStatement ps, int i, Integer ii) throws SQLException{
		if(ii == null)
			ps.setNull(i, Types.INTEGER);
		else
			ps.setInt(i, ii);
	}
	
	protected int getIi(Connection db, String table, String iiColumn, String searchColumn, String searchValue) throws SQLException{
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
	
	protected int getPatientIi(Connection db, String patientId) throws SQLException{
		return getIi(db, "patient", "patient_ii", "patient_id", patientId);
	}
	
	protected Map<Integer,Integer> createIiIiMap(Connection db1, Connection db2, String table, String iiColumn, String valueColumn) throws SQLException{
		Map<String,Integer> iivalue1 = createValueIiMap(db1,table,iiColumn,valueColumn);
		Map<String,Integer> iivalue2 = createValueIiMap(db2,table,iiColumn,valueColumn);
		Map<Integer,Integer> iiMap = new HashMap<Integer,Integer>();
		for(Map.Entry<String, Integer> me : iivalue1.entrySet())
			iiMap.put(me.getValue(), iivalue2.get(me.getKey()));
		return iiMap;
	}
	
	protected Map<String,Integer> createValueIiMap(Connection db, String table, String iiColumn, String valueColumn) throws SQLException{
		Map<String,Integer> map = new HashMap<String,Integer>();
		
		Statement s = db.createStatement();
		ResultSet rs = s.executeQuery("select "+ iiColumn +","+ valueColumn +" from regadbschema."+ table);
		while(rs.next())
			map.put(rs.getString(valueColumn),rs.getInt(iiColumn));
		
		s.close();
		return map;
	}
	
	public static void run(MergeObjects mo, String args[]) throws Exception{
		Arguments as = new Arguments();
		ValueArgument confDir = as.addValueArgument("c", "conf-dir", false);
		PositionalArgument diff = as.addPositionalArgument("diff-file", true);

		PositionalArgument db1 = as.addPositionalArgument("db1", true);
		PositionalArgument u1 = as.addPositionalArgument("user1", true);
		PositionalArgument p1 = as.addPositionalArgument("pass1", true);

		PositionalArgument db2 = as.addPositionalArgument("db2", true);
		PositionalArgument u2 = as.addPositionalArgument("user2", true);
		PositionalArgument p2 = as.addPositionalArgument("pass2", true);
		
		if(!as.handle(args))
			return;
		
		if(confDir.isSet())
			RegaDBSettings.createInstance(confDir.getValue());
		else
			RegaDBSettings.createInstance();
		
		mo.init(db1.getValue(), u1.getValue(), p1.getValue(),
				db2.getValue(), u2.getValue(), p2.getValue());
		mo.run(new File(diff.getValue()));
	}
}
