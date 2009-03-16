package net.sf.regadb.util.settings;

import java.util.regex.Pattern;

public class Filter {
	private String filter;
	private Pattern filterPattern;
	public Filter(String filter) {
		this.filter = filter;
		filterPattern = Pattern.compile("^"+filter.replace("*", ".*"));
	}
	
	public boolean compareRegexp(String s) {
		return filterPattern.matcher(s).matches();
	}
	
	public String getConfigString(){
		return filter;
	}
	public String getHqlString() {
		return filter.replace('*', '%');
	}
}
