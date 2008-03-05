package net.sf.regadb.io.db.util.export;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import net.sf.regadb.csv.Table;

import java.util.ArrayList;
import java.io.OutputStream;

public class CsvExporter implements IExporter {

	public void export(ResultSet rs, OutputStream os){
		try{
			Table table = new Table();
			
			ResultSetMetaData md = rs.getMetaData();

			String value;
			int cc = md.getColumnCount();
			ArrayList<String> row = new ArrayList<String>();

			
			for(int i = 1; i <= cc; ++i){
				value = formatHeader(md.getColumnType(i), md.getColumnName(i));
				row.add(value);
			}
			table.addRow(row);

			//data			
			while(rs.next()){
				row = new ArrayList<String>();
				for(int i = 1; i <= cc; ++i){
					try{
						row.add(format(md.getColumnType(i), md.getColumnName(i), rs.getObject(i)));
					}
					catch(Exception e){
						row.add("");
						System.err.println("Newline char encountered.");						
					}
				}
				table.addRow(row);
			}
			
			table.exportAsCsv(os);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String format(int type, String column, Object obj) throws Exception{
		if(obj != null){
			String str = obj.toString().replaceAll("/n","");
			//if(str.indexOf('\n') != -1) throw new Exception();
			return str;
		}
		else
			return "";
	}
	
	public String formatHeader(int type, String column) throws Exception{
		return column; 
	}
}
