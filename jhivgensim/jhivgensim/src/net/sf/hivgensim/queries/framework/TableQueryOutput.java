package net.sf.hivgensim.queries.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import net.sf.regadb.csv.Table;

public abstract class TableQueryOutput<DataType> extends QueryOutput<DataType, Table> {
	public enum TableOutputType {
		CSV,
		Excel,
		SPSS;
	}
	
	private File file;
	private TableOutputType type;
	
	public TableQueryOutput(Table out, File file, TableOutputType type) {
		super(out);
		this.file = file;
		this.type = type;
	}

	public void close() {
		System.err.println("close");
		if(type==TableOutputType.CSV) {
			try {
				getOut().exportAsCsv(new PrintStream(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else if(type==TableOutputType.Excel) {
			//TODO
		} else if(type==TableOutputType.SPSS) {
			try {
				getOut().exportAsSpss(new PrintStream(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	ArrayList<String> row = null;
	public void addColumn(String value, boolean endRow) {
		if(row == null) {
			row = new ArrayList<String>();
		}
		
		if(value == null)
			value = "";
		
		row.add(value);
		
		if(endRow) {
			getOut().addRow(row);
			row = null;
		}
	}
	
	public void addColumn(String value) {
		this.addColumn(value, false);
	}
	
	
	
}
