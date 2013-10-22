package net.sf.regadb.io.export.hicdep;

public class StringMapper implements Mapper {
	private String from;
	private String to;
	public StringMapper(String from, String to) {
		this.from = from;
		this.to = to;
	}
	
	@Override
	public String map(String value) {
		if (from.equals(value)) 
			return to;
		else 
			return value;
	}
}
