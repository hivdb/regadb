package net.sf.regadb.io.db.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DelimitedReader{
	private String separator;
	private String delimiter;
	private BufferedReader reader;
	private Map<String,Integer> headers = new HashMap<String,Integer>();
	private String[] row;

	public DelimitedReader(File file, String serparator, String delimiter) throws IOException {
		setSeparator(serparator);
		setDelimiter(delimiter);
		
		setReader(new BufferedReader(new FileReader(file)));
		
		setHeaders(readLine());
	}
	
	protected void setHeaders(String[] fields){
		for(int i=0; i<fields.length; ++i)
			headers.put(fields[i], i);
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getSeparator() {
		return separator;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getDelimiter() {
		return delimiter;
	}

	private void setReader(BufferedReader reader) {
		this.reader = reader;
	}

	private BufferedReader getReader() {
		return reader;
	}
	
	public String[] readLine() throws IOException{
		String line = getReader().readLine();
		if(line == null)
			return null;
		if(line.trim().length() == 0)
			return readLine();
		
		String[] fields = split(line);
		setRow(fields);
		return fields;
	}
	
	protected String[] split(String line){
		String[] r = line.split(getSeparator());
		for(int i=0; i<r.length; ++i){
			if(r[i].startsWith(getDelimiter())){
				r[i] = r[i].substring(getDelimiter().length(), r[i].length() - getDelimiter().length());
			}
		}
		return r;
	}

	protected void setRow(String[] row) {
		this.row = row;
	}

	protected String[] getRow() {
		return row;
	}
	
	public String get(int i){
		return i >= getRow().length ? null : getRow()[i];
	}
	
	public String get(String field){
		Integer i = headers.get(field);
		return i == null ? null : get(i);
	}
	
	public void close() throws IOException{
		reader.close();
	}
}
