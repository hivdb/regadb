package net.sf.regadb.io.export.hicdep;

public class IntervalMapper implements Mapper {
	private Interval interval;
	private String to;
	public IntervalMapper(Interval interval, String to) {
		this.interval = interval;
		this.to = to;
	}
	
	@Override
	public String map(String value) {
		if (interval.in(Double.parseDouble(value)))
			return to;
		else
			return value;
	}
}
