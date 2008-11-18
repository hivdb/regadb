package net.sf.regadb.util.settings;

public class Filter {
	private String filter;
	public Filter(String filter) {
		this.filter = filter;
	}
	
	public boolean compareRegexp(String s) {
		return false;
	}
	
	public String getHqlString() {
		return filter.replace('*', '%');
	}
}
