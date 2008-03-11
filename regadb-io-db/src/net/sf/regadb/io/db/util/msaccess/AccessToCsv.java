package net.sf.regadb.io.db.util.msaccess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.regadb.io.db.util.export.CsvExporter;
import net.sf.regadb.io.db.util.export.SqlQueryExporter;

public class AccessToCsv {
	private String dbDriver_ = "sun.jdbc.odbc.JdbcOdbcDriver";
	private String dbPrefix_ = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
	private String dbSuffix_ = ";DriverID=22;READONLY=true}";
	private String dbUsername_ = "";
	private String dbPassword_ = "";
	
	private SqlQueryExporter sqlQueryExporter_;
	
	private Connection conn_ = null;

	public static void main(String[] args) {
		String input,output;
		AccessToCsv a2c = new AccessToCsv();
		
		if(args.length > 0){
			input = args[0];
			if(args.length > 1){
				output = args[1];
				
				a2c.createCsv(new File(input),new File(output));
			}
			else{
				a2c.createCsv(new File(input));
			}
		}
		else{
			System.out.println("Usage: AccessToCsv <database_input_file> [<csv_output_path>]");
		}
	}
			 
	public AccessToCsv(){
		sqlQueryExporter_ = new SqlQueryExporter();
		sqlQueryExporter_.setExporter(new CsvExporter());
	}
	
	public Connection getConnection(File dbFile){
		if(conn_ == null){
			try{
				Class.forName(dbDriver_);
				
				String dbUrl = dbPrefix_ + dbFile.getAbsolutePath() + dbSuffix_;
				conn_ = DriverManager.getConnection(dbUrl,dbUsername_,dbPassword_);
			}
			catch(Exception e){
				e.printStackTrace();
			}			
		}
		return conn_;
	}
	
	public void createCsv(File in){
		createCsv(in,deriveOutputDirFrom(in));
	}
	
	public File deriveOutputDirFrom(File f){
		return f.getParentFile();
	}

	public void createCsv(File in, File out){
			
		String pfx = getCsvPrefix(in);
		
		try{
			Connection con = getConnection(in);
			sqlQueryExporter_.setConnection(con);
			
			ResultSet rs;
			DatabaseMetaData md;
			String table;
			FileOutputStream os;
			
			md = con.getMetaData();
		    rs = md.getTables(null, null, "%", new String [] {"TABLE"});
		    while (rs.next()) {
		    	table = rs.getString(3);
		    	os = new FileOutputStream(new File(out.getAbsolutePath() + File.separator + table +".csv"));
		    	exportTable(con, table, os);
		    	os.close();		    	
		    }
		}
		catch(Exception e){
			System.out.println("Error: "+ e);
		}
	}
	
	public String getCsvPrefix(File f){
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if(i == -1)
			return s;
		else
			return s.substring(0,i);
	}
	
	protected void exportTable(Connection con, String table, OutputStream os){
		System.out.println("Exporting: "+ table);
		
		try{
			sqlQueryExporter_.exportQuery("SELECT * FROM `"+table+"`",os);
		}
		catch(Exception e){
			System.out.println("Error exporting("+table+"):"+ e);
		}
	}
}
