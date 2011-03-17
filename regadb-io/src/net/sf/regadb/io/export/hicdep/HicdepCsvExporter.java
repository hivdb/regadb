package net.sf.regadb.io.export.hicdep;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.session.Login;

public class HicdepCsvExporter extends HicdepExporter {
	
	private File outputDir;
	
	private Map<String,PrintStream> streams = new HashMap<String,PrintStream>();

	public HicdepCsvExporter(Login login, File outputDir) {
		super(login);
		
		this.outputDir = outputDir;
	}
	
	@Override
	public void printInsert(String table, String[] columns, String[] values){
		PrintStream out = getPrintStream(table, columns);
		printLine(out, values);
	}
	
	private PrintStream getPrintStream(String table, String[] columns){
		PrintStream out = streams.get(table);
		
		if(out == null){
			try {
				out = new PrintStream(new FileOutputStream(outputDir.getAbsolutePath() + File.separator + table +".csv"));
				streams.put(table, out);
				
				printLine(out, columns);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return out;
	}
	
	private void printLine(PrintStream out, String[] columns){
		boolean first = true;
		
		for(String c : columns){
			if(first)
				first = false;
			else
				out.print(';');
			out.print(c);
		}
		
		out.println();
	}

	public void close(){
		for(PrintStream ps : streams.values()){
			ps.close();
		}
	}
}
