package net.sf.regadb.io.db.util.db2csv;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;

import net.sf.regadb.io.db.util.export.CsvExporter;
import net.sf.regadb.io.db.util.export.SqlQueryExporter;

public class DBToCsv {
	private SqlQueryExporter sqlQueryExporter_;
	
	private Connection conn_ = null;
	
	private IConnectionProvider connectionProvider_;

	/*public static void main(String[] args) {
		String input,output;
		DBToCsv a2c = new DBToCsv();
		
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
	}*/
			 
	public DBToCsv(IConnectionProvider connectionProvider){
		connectionProvider_ = connectionProvider;
		sqlQueryExporter_ = new SqlQueryExporter();
		sqlQueryExporter_.setExporter(new CsvExporter());
	}

	public void createCsv(File out){
			
		String pfx = connectionProvider_.getCsvPrefix();
		
		try{
			Connection con = connectionProvider_.getConnection();
			sqlQueryExporter_.setConnection(con);
			
			ResultSet rs;
			DatabaseMetaData md;
			String table;
			
			md = con.getMetaData();
		    rs = md.getTables(null, null, "%", new String [] {"TABLE"});
		    while (rs.next()) {
		    	table = rs.getString(3);
		    	exportTable(new File(out.getAbsolutePath() + File.separator + table +".csv"),con, table);
		    }
		}
		catch(Exception e){
			System.out.println("Error: "+ e);
		}
	}
	
	public void createCsv(File out, HashMap<String,String> tableSelections)
	{
		String pfx = connectionProvider_.getCsvPrefix();
		
		try{
			Connection con = connectionProvider_.getConnection();
			sqlQueryExporter_.setConnection(con);
			
			ResultSet rs;
			DatabaseMetaData md;
			String table;
			
			md = con.getMetaData();
		    rs = md.getTables(null, null, "%", new String [] {"TABLE"});
		    while (rs.next()) {
		    	table = rs.getString(3);
		    	
		    	if(tableSelections.containsKey(table))
		    	{
		    		exportTable(new File(out.getAbsolutePath() + File.separator + table +".csv"),con, table, (String)tableSelections.get(table));
		    	}
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
	
	protected void exportTable(File out, Connection con, String table){
		System.out.println("Exporting: "+ table);
		
		try{
			FileOutputStream os = new FileOutputStream(out);
			sqlQueryExporter_.exportQuery("SELECT * FROM `"+table+"`",os);
			os.close();		    	
		}
		catch(Exception e){
			System.out.println("Error exporting("+table+"):"+ e);
		}
	}
	
	protected void exportTable(File out, Connection con, String table, String query){
		System.out.println("Exporting "+ table +" with query \n"+query);
		
		try{
			FileOutputStream os = new FileOutputStream(out);
			sqlQueryExporter_.exportQuery(query ,os);
			os.close();		    	
		}
		catch(Exception e){
			System.out.println("Error exporting("+table+"):"+ e);
		}
	}
}
