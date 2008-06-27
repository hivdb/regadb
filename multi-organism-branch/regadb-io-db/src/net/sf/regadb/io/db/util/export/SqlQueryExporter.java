package net.sf.regadb.io.db.util.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;



public class SqlQueryExporter {
	private String dbDriver_;
	private String dbUrl_;
	private String dbUsername_;
	private String dbPassword_;

	private Connection conn_ = null;
	
	private IExporter exporter_ = null;

	public static void main(String[] args) {
		
		if(args.length >= 6){
			SqlQueryExporter esq = new SqlQueryExporter();
			esq.setDriver(args[0]);
			esq.setUrl(args[1]);
			esq.setUsername(args[2]);
			esq.setPassword(args[3]);
			
			try{
				FileOutputStream os = new FileOutputStream(new File(args[4]));
				esq.setExporter(new CsvExporter());
				esq.exportQuery(args[5], os);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else{
			System.err.println("Provide parameters: <DB Driver> <DB Url> <DB Username> <DB Password> <Output file> <SQL Query>");
		}
	}
	
	public SqlQueryExporter(){
		
	}
	
	public void setDriver(String driver){
		dbDriver_ = driver;
	}
	
	public void setUrl(String url){
		dbUrl_ = url;
	}
	
	public void setUsername(String username){
		dbUsername_ = username;
	}
	
	public void setPassword(String password){
		dbPassword_ = password;
	}
	
	public void setConnection(Connection con){
		conn_ = con;
	}
	
	public void setExporter(IExporter exporter){
		exporter_ = exporter;
	}
	
	public Connection getConnection(){
		if(conn_ == null){
			try{
				Class.forName(dbDriver_);
				
				conn_ = DriverManager.getConnection(dbUrl_,dbUsername_,dbPassword_);
			}
			catch(Exception e){
				e.printStackTrace();
			}			
		}
		return conn_;
	}

	
	public void exportQuery(String query, OutputStream os){
		try{
			Connection con = getConnection();
			ResultSet rs;
			ResultSetMetaData md;
			
			Statement s = con.createStatement();

			s.execute(query);
			rs = s.getResultSet();
			
			if(exporter_ != null){
				exporter_.export(rs, os);
			}
		}
		catch(Exception e){
			System.out.println("Error: "+ e);
		}
	}
}