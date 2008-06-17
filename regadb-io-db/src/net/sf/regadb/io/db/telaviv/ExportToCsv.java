package net.sf.regadb.io.db.telaviv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashSet;

import net.sf.regadb.io.db.util.db2csv.AccessConnectionProvider;
import net.sf.regadb.io.db.util.db2csv.DBToCsv;
import net.sf.regadb.io.db.util.db2csv.IConnectionProvider;

public class ExportToCsv extends DBToCsv {
	private HashSet<String> ignoreTables_ = new HashSet<String>();
    
    public static void main(String[] args) {
        String input,output;
        
        if(args.length > 2){
            File inFile = new File(args[0]);
            ExportToCsv a2c = new ExportToCsv(new AccessConnectionProvider(inFile));
            File ignoreFile = new File(args[1]);
            
            a2c.loadIgnoreTables(ignoreFile);
            File outFile = new File(args[2]);
            a2c.createCsv(outFile);
        }
        else{
            System.out.println("Usage: ExportToCsv <database_input_file> <table_ignore_file> <csv_output_path>");
        }
    }
    
    public ExportToCsv(IConnectionProvider connectionProvider) {
		super(connectionProvider);
	}
    
    protected boolean loadIgnoreTables(File ignore){
        if(ignore.exists()){
            try{
                BufferedReader fr = new BufferedReader(new FileReader(ignore));
                ignoreTables_.clear();
                String line;
                while((line = fr.readLine()) != null){
                    ignoreTables_.add(line);
                }
                fr.close();
                return true;
            }
            catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }
        else{
            return false;
        }
    }

    protected void exportTable(File out, Connection con, String table){
        if(!ignoreTables_.contains(table)){
            super.exportTable(out,con,table);
        }
        else{
            System.out.println("Ignoring table: "+ table);
        }
    }
}
