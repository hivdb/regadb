package net.sf.regadb.io.db.util.msaccess;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class AccessConnectionProvider implements IConnectionProvider {
	private String dbDriver_ = "sun.jdbc.odbc.JdbcOdbcDriver";
	private String dbPrefix_ = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
	private String dbSuffix_ = ";DriverID=22;READONLY=true}";
	private String dbUsername_ = "";
	private String dbPassword_ = "";
	
	private File dbFile_;
	private Connection conn_;
	
	public AccessConnectionProvider(File in) {
		dbFile_ = in;
	}
	
	public Connection getConnection(){
		if(conn_ == null){
			try{
				Class.forName(dbDriver_);
				Properties prop = new Properties();            
				prop.put("charSet", "UTF-8");
				prop.put("user", dbUsername_);
				prop.put("password", dbPassword_);
				String dbUrl = dbPrefix_ + dbFile_.getAbsolutePath() + dbSuffix_;
				conn_ = DriverManager.getConnection(dbUrl,prop);
			}
			catch(Exception e){
				e.printStackTrace();
			}			
		}
		return conn_;
	}

	public String getCsvPrefix(){
		String s = dbFile_.getName();
		int i = s.lastIndexOf('.');

		if(i == -1)
			return s;
		else
			return s.substring(0,i);
	}
}
