package net.sf.regadb.io.db.jerusalem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashSet;

import net.sf.regadb.io.db.util.msaccess.AccessToCsv;

public class ExportToCsv extends AccessToCsv {
    private HashSet<String> ignoreTables_ = new HashSet<String>();
    
    public static void main(String[] args) {
        String input,output;
        ExportToCsv a2c = new ExportToCsv();
        
        if(args.length > 1){
            File inFile = new File(args[0]);
            File ignoreFile = new File(args[1]);
            
            if(args.length > 1){
                File outFile = new File(args[2]);
                
                a2c.createCsv(inFile,ignoreFile,outFile);
            }
            else{
                a2c.createCsv(inFile,ignoreFile);
            }
        }
        else{
            System.out.println("Usage: ExportToCsv <database_input_file> <table_ignore_file> [<csv_output_path>]");
        }
    }
    
    public void createCsv(File in, File ignore){
        if(loadIgnoreTables(ignore)){
            super.createCsv(in);
        }
    }
    
    public void createCsv(File in, File ignore, File out){
        if(loadIgnoreTables(ignore)){
            super.createCsv(in, out);
        }
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

    protected void exportTable(Connection con, String table, OutputStream os){
        if(ignoreTables_.contains(table)){
            super.exportTable(con,table,os);
        }
        else{
            System.out.println("Ignoring table: "+ table);
        }
    }
}
