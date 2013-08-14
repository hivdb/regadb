package net.sf.regadb.io.export.hicdep;

import java.io.PrintStream;
import java.util.LinkedHashMap;

import net.sf.regadb.db.session.Login;

public class HicdepSqlExporter extends HicdepExporter {
	public HicdepSqlExporter(Login login) {
		super(login);
	}

	@Override
	public void printRow(String table, LinkedHashMap<String, String> row) {
		printRow(table, row.keySet().toArray(new String[row.keySet().size()]), row.values().toArray(new String[row.values().size()]));
	}
	
	@Override
	protected void printRow(String table, String[] columns, String[] values){
		if(columns.length != values.length)
			System.err.println("columns.length != values.length");
		
		PrintStream out = System.out;
		
		out.print("INSERT INTO ");
		out.print(table);
		out.print(" (");
		
		boolean first = true;
		for(String s : columns){
			if(first)
				first = false;
			else
				out.print(',');
			out.print(s);
		}
		
		out.print(") values (");
		
		first = true;
		for(String s : values){
			if(first)
				first = false;
			else
				out.print(',');
			
			if(s == null){
				out.print("NULL");
			}else{
				out.print('\'');
				out.print(s);
				out.print('\'');
			}
		}
		
		out.println(");");
	}
}
