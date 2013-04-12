package net.sf.regadb.io.export.hicdep;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.file.FileUtils;

public class HicdepCsvExporter extends HicdepExporter {
	
	private File zipFile;
	
	private Map<String,PrintStream> streams = new HashMap<String,PrintStream>();
	private Map<String,File> files = new HashMap<String,File>();

	public HicdepCsvExporter(Login login, File zipFile) {
		super(login);
		
		this.zipFile = zipFile;
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
				File f = File.createTempFile(table, "csv");
				out = new PrintStream(new FileOutputStream(f));
				streams.put(table, out);
				files.put(table +".csv", f);
				
				printLine(out, columns);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
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

	public void close() throws IOException{
		for(PrintStream ps : streams.values()){
			ps.close();
		}
		
		FileUtils.createZipFile(zipFile, files);
		
		for(File f : files.values())
			f.delete();
	}
}
