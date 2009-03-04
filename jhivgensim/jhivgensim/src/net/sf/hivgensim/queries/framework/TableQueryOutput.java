package net.sf.hivgensim.queries.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

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

	protected void closeOutput() {
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
	
	int rowIndex = 0;
	int colIndex = 0;
	public void addColumn(String value, boolean endRow) {
		if(value == null)
			value = "";
		
		getOut().setValue(colIndex, rowIndex, value);
		if(endRow) {
			colIndex = 0;
			rowIndex++;
		} else {
			colIndex++;
		}
	}
	
	public void addColumn(String value) {
		this.addColumn(value, false);
	}	

	protected abstract void generateOutput(List<DataType> query);
}
